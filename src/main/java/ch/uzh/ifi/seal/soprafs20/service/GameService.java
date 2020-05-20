package ch.uzh.ifi.seal.soprafs20.service;

import ch.uzh.ifi.seal.soprafs20.GameLogic.NLP;
import ch.uzh.ifi.seal.soprafs20.GameLogic.WordReader;
import ch.uzh.ifi.seal.soprafs20.GameLogic.gameStates.GameState;
import ch.uzh.ifi.seal.soprafs20.entity.*;
import ch.uzh.ifi.seal.soprafs20.exceptions.ConflictException;
import ch.uzh.ifi.seal.soprafs20.exceptions.NotFoundException;
import ch.uzh.ifi.seal.soprafs20.exceptions.UnauthorizedException;
import ch.uzh.ifi.seal.soprafs20.repository.*;
import ch.uzh.ifi.seal.soprafs20.rest.dto.CluePutDTO;
import ch.uzh.ifi.seal.soprafs20.rest.dto.GamePostDTO;
import ch.uzh.ifi.seal.soprafs20.rest.dto.MessagePutDTO;
import ch.uzh.ifi.seal.soprafs20.rest.dto.RequestPutDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.concurrent.TimeUnit;


/**
 * Game Service
 * This class is the "worker" and responsible for all functionality related to the Game
 * The result will be passed back to the caller.
 */
@Service
@Transactional
public class GameService{
    private final GameRepository gameRepository;
    private final LobbyRepository lobbyRepository;
    private final UserRepository userRepository;
    private final ClueRepository clueRepository;
    private final LobbyScoreRepository lobbyScoreRepository;
    private final PlayerRepository playerRepository;
    private final Logger log = LoggerFactory.getLogger(GameService.class);
    private static final int ROUNDS = 3;
    private static final int pickWordTime = 10;
    private static final int enterCluesTime = 30;
    private static final int voteTime = 15;
    private static final int guessTime = 30;
    private static final int transitionTime = 5;
    private static final int endTime = 10;
    private final Random rand = new Random();

    @Autowired
    public GameService(GameRepository gameRepository, LobbyRepository lobbyRepository, UserRepository userRepository, LobbyScoreRepository lobbyScoreRepository, ClueRepository clueRepository, PlayerRepository playerRepository) {

        this.gameRepository = gameRepository;
        this.lobbyRepository = lobbyRepository;
        this.userRepository = userRepository;
        this.clueRepository = clueRepository;
        this.lobbyScoreRepository = lobbyScoreRepository;
        this.playerRepository = playerRepository;
    }

    public Game getGame(Long id) {
        Game game;
        Optional<Game> optionalGame = gameRepository.findById(id);
        if (optionalGame.isPresent()) {
            game = optionalGame.get();
            return game;
        }
        else {
            throw new NotFoundException("Could not find game!");
        }
    }

    public int getMaxTime(Game game){
        if(game.getGameState().equals(GameState.END_GAME_STATE))
            return endTime;
        else if(game.getGameState().equals(GameState.PICK_WORD_STATE))
            return pickWordTime;
        else if(game.getGameState().equals(GameState.TRANSITION_STATE))
            return transitionTime;
        else if(game.getGameState().equals(GameState.ENTER_CLUES_STATE))
            return enterCluesTime;
        else if(game.getGameState().equals(GameState.VOTE_ON_CLUES_STATE))
            return voteTime;
        else
            return guessTime;
    }

    /**
     * creates new Game instance, sets current guesser and chooses first word
     *
     * @param lobby
     * @param gamePostDTO
     * @return
     */
    public Game createGame(Lobby lobby, GamePostDTO gamePostDTO) {
        if (!lobby.getHostToken().equals(gamePostDTO.getHostToken())) {
            throw new UnauthorizedException("You are not allowed to start the game.");
        }
        if (lobby.isGameStarted()) {
            throw new ConflictException("Game has already started!");
        }
        //set lobby status to started
        lobby.setGameIsStarted(true);

        //init new game
        Game newGame = new Game();
        newGame.setLobbyId(lobby.getLobbyId());
        newGame.setGameState(GameState.PICK_WORD_STATE);
        newGame.setLobbyName(lobby.getLobbyName());


        for (Player player : lobby.getPlayersInLobby()) {
            newGame.addPlayer(player);
        }

        //if there are only 3 players, the special rule set has to be applied
        newGame.setSpecialGame(newGame.getPlayers().size() == 3);

        //assign first guesser
        Player currentGuesser = newGame.getPlayers().get(rand.nextInt(newGame.getPlayers().size()));
        newGame.setCurrentGuesser(currentGuesser);

        //set round count to 1
        newGame.setRoundsPlayed(1);
        setStartTime(TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis()), newGame);

        //select 13 random words from words.txt
        WordReader reader = new WordReader();
        newGame.setWords(reader.getRandomWords(13));

        newGame = gameRepository.save(newGame);
        gameRepository.flush();
        return newGame;
    }

    /**
     * @param game
     * @param player
     * @param cluePutDTO
     */
    public boolean sendClue(Game game, Player player, CluePutDTO cluePutDTO){
        if(!game.getGameState().equals(GameState.ENTER_CLUES_STATE))
            throw new UnauthorizedException("Clues are not accepted in current state!");

        if(!game.getPlayers().contains(player) || player.isClueIsSent() || game.getCurrentGuesser().equals(player) ||
                (!player.getToken().equals(cluePutDTO.getPlayerToken()))){
            throw new UnauthorizedException("This player is not allowed to send a clue!");
        }

        if (!game.isSpecialGame()) {
            Clue clue = new Clue();
            clue.setPlayerId(player.getId());
            clue.setActualClue(cluePutDTO.getMessage());
            clue.setTimeNeeded(enterCluesTime - (TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis()) - game.getStartTimeSeconds()));
            player.addClue(clue);
            player.setClueIsSent(true);
            // if the same clue is sent twice, remove it from list of entered clues
            addClue(clue, game);
            clueRepository.saveAndFlush(clue);
            gameRepository.saveAndFlush(game);
        }
        else {
            sendClueSpecial(game, player, cluePutDTO);//handle double clue input from player
        }
        int counter = 0;
        for (Player playerInGame : game.getPlayers()){
            if (playerInGame.isClueIsSent()){
                counter++;
            }
        }
        System.out.println("Counter = " + counter);
        if(allSent(game, counter)) {
            checkClues(game);
            gameRepository.saveAndFlush(game);
            return true;
        }
        return false;
    }


    /**
     * Overloaded sendClue method for the case that the timer runs out and not every player sent a clue
     * @param game
     * @return
     */
    public boolean sendClue(Game game){
        //if a user did not send a clue, fill his clue with empty string
        if (!game.isSpecialGame()) {
            for(Player p : game.getPlayers()){
                p.setClueIsSent(true);
            }
        }
        return true;
    }

    public boolean pickWord(String token, Game game) {
        if (!game.getCurrentGuesser().getToken().equals(token)) {
            throw new UnauthorizedException("This player is not allowed to pick a word!");
        }
        game.setCurrentWord(chooseWordAtRandom(game.getWords()));
        game.setGameState(GameState.ENTER_CLUES_STATE);
        gameRepository.saveAndFlush(game);
        return true;
    }

    /**
     * Overloaded pickword method for the case that the timer runs out and the guesser did not send a guess
     * @param game
     * @return
     */
    public boolean pickWord(Game game) {
        game.setCurrentWord(chooseWordAtRandom(game.getWords()));
        System.out.println("Picked a word!");
        return true;
    }


    /**
     * method adds clue and token of player to clues. When a player enters their second clue, their token is replaced with it
     *
     * @param game
     * @param player
     * @param cluePutDTO
     */
    private void sendClueSpecial(Game game, Player player, CluePutDTO cluePutDTO) {
        Clue firstClue = new Clue();
        firstClue.setPlayerId(player.getId());
        firstClue.setActualClue(cluePutDTO.getMessage());
        firstClue.setTimeNeeded(enterCluesTime - (TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis()) - game.getStartTimeSeconds()));
        player.addClue(firstClue);

        Clue secondClue = new Clue();
        secondClue.setPlayerId(player.getId());
        secondClue.setTimeNeeded(enterCluesTime - (TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis()) - game.getStartTimeSeconds()));
        if(cluePutDTO.getMessage2() != null) {
            secondClue.setActualClue(cluePutDTO.getMessage2());
            player.addClue(secondClue);
        }
        else {
            secondClue.setActualClue("");
        }
        addClue(firstClue, game);
        addClue(secondClue, game);
        player.setClueIsSent(true);

        gameRepository.saveAndFlush(game);
        clueRepository.saveAndFlush(firstClue);
        clueRepository.saveAndFlush(secondClue);
    }


    public void submitGuess(Game game, MessagePutDTO messagePutDTO, long time) {
        if (!game.getCurrentGuesser().getToken().equals(messagePutDTO.getPlayerToken())) {
            throw new UnauthorizedException("User is not allowed to submit a guess!");
        }
        if(!game.getGameState().equals(GameState.ENTER_GUESS_STATE)) {
            throw new UnauthorizedException("Can't submit guess in current state!");
        }
        int pastScore = game.getCurrentGuesser().getScore();
        game.setGuessCorrect(messagePutDTO.getMessage().toLowerCase().equals(game.getCurrentWord().toLowerCase()));
        if(game.isGuessCorrect()){
            if(game.isSpecialGame()){
                game.getCurrentGuesser().setScore(pastScore + (int)((guessTime - time)*10));
            } else {
                game.getCurrentGuesser().setScore(pastScore + (int)((guessTime - time)*5));
            }
            System.out.println("Guess was correct");
        } else {
            if(game.isSpecialGame() && pastScore > 60){
                game.getCurrentGuesser().setScore(pastScore - 60);
            } else {
                game.getCurrentGuesser().setScore(0);
            }
            if(!game.isSpecialGame() && pastScore > 30){
                game.getCurrentGuesser().setScore(pastScore - 30);
            } else {
                game.getCurrentGuesser().setScore(0);

            }
            System.out.println("Guess was not correct");
        }
        Optional<User> optionalUser = userRepository.findById(game.getCurrentGuesser().getId());
        if(optionalUser.isPresent()){
            User user = optionalUser.get();
            user.setScore(user.getScore() + game.getCurrentGuesser().getScore());
        }

        game.setCurrentGuess(messagePutDTO.getMessage());
        game.setOverallScore(game.getOverallScore() + game.getCurrentGuesser().getScore());
        gameRepository.saveAndFlush(game);
    }


    public void startNewRound(Game game, RequestPutDTO requestPutDTO) {
        if (!game.getCurrentGuesser().getToken().equals(requestPutDTO.getToken())) {
            throw new UnauthorizedException("User is not allowed to start a new round!");
        }
        game.setRoundsPlayed(game.getRoundsPlayed() + 1);

        int index = game.getPlayers().indexOf(game.getCurrentGuesser());
        Player currentGuesser = game.getPlayers().get((index + 1) % game.getPlayers().size());
        game.setCurrentGuesser(currentGuesser);

        for(Player p : game.getPlayers()){
            p.setClueIsSent(false);
            p.setVoted(false);
            p.getClues().clear();
        }
        game.getEnteredClues().clear();
        game.getInvalidClues().clear();
        game.setGameState(GameState.PICK_WORD_STATE);

        //ToDo: Update scores of player and overall score
    }

    public void startNewRound(Game game) {
        game.setRoundsPlayed(game.getRoundsPlayed() + 1);


        int index = game.getPlayers().indexOf(game.getCurrentGuesser());
        Player currentGuesser = game.getPlayers().get((index + 1) % game.getPlayers().size());
        game.setCurrentGuesser(currentGuesser);

        for(Player p : game.getPlayers()){
            p.setClueIsSent(false);
            p.setVoted(false);
            p.getClues().clear();
        }

        game.getEnteredClues().clear();
        game.getInvalidClues().clear();
        game.setGuessCorrect(false);
        game.setCurrentGuess("");
        gameRepository.saveAndFlush(game);
        //ToDo: Update scores of player and overall score
    }

    public void checkClues(Game game) {
        NLP nlp = new NLP();
        List<Clue> invalidClues = new ArrayList<>();
        System.out.println("Game clues as string: "+ game.getCluesAsString());
        for (Clue clue : game.getEnteredClues()) {
            if (!nlp.checkClue(clue.getActualClue(), game.getCurrentWord())) {
                clue.setPlayerId(-1L);
                invalidClues.add(clue);
            }
        }
        game.getEnteredClues().removeAll(invalidClues);
        game.setInvalidClues(invalidClues);
        gameRepository.saveAndFlush(game);
    }

    /**
     *
     * @param game
     * @param counter
     * @return true if all clues of each player are received, false if no
     */
    public boolean allSent(Game game, int counter) {
        return counter == game.getPlayers().size() - 1;
    }

    public void setStartTime(long time, Game game) {
        game.setStartTimeSeconds(time);
        gameRepository.saveAndFlush(game);
    }


    /**
     * Helper function that returns a random word from list and deletes it from list
     *
     * @param words
     * @return random word from list
     */
    public String chooseWordAtRandom(List<String> words) {
        String currentWord = words.get(rand.nextInt(words.size()));
        words.remove(currentWord);
        return currentWord;
    }

    public void updateScores(Game game){
        game = getUpdatedGame(game);
        for (Player player : game.getPlayers()) {
            // in case of 3-player-logic, the size of clues is 2, otherwise 1 (or 0, if player did not send any clues)
            int newScore = 0;
            for(int i = 0; i < player.getClues().size(); i++) {
                if(!game.isSpecialGame()) {
                    if (game.getEnteredClues().contains(player.getClue(i)) && game.isGuessCorrect()) {
                        newScore = (int) ((player.getClue(i).getTimeNeeded()) * (((game.getPlayers().size()) - game.getEnteredClues().size())));
                    }
                    else if (game.getEnteredClues().contains(player.getClue(i)) && !game.isGuessCorrect()) {
                        newScore = (int) ((player.getClue(i).getTimeNeeded()) * (((game.getPlayers().size()) - game.getEnteredClues().size())) - 15);
                    }
                } else {
                    if(game.getEnteredClues().contains(player.getClue(i)) && game.isGuessCorrect()){
                        newScore = (int) ((player.getClue(i).getTimeNeeded()) * (((game.getPlayers().size() * 2) - game.getEnteredClues().size())));
                    } else if (game.getEnteredClues().contains(player.getClue(i)) && !game.isGuessCorrect()) {
                        newScore = (int) ((player.getClue(i).getTimeNeeded()) * (((game.getPlayers().size() * 2) - game.getEnteredClues().size())) - 15);
                    }
                }
                player.setScore(player.getScore() + newScore);
                Optional<User> optionalUser = userRepository.findById(player.getId());
                if(optionalUser.isPresent()) {
                    User user = optionalUser.get();
                    user.setScore(user.getScore() + newScore);
                    userRepository.saveAndFlush(user);
                }
                game.setOverallScore(game.getOverallScore() + player.getScore());
            }
        }

    }

    /**
     * Central timer logic for each game. Sets timer for each state,
     * If state is complete before the timer ends, the game transitions into the next state with a new timer.
     * Timer also takes care of all the logic set up for the next state if no user input was entered
     * TODO: Implement the game logic changes for each state if no input is received and the timer ends
     *
     * @param g - takes a game instance as input
     */
    public void timer(Game g) {
        final Game[] game = {g};
        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
                game[0] = getUpdatedGame(game[0]);
                game[0].setTime(TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis()) - game[0].getStartTimeSeconds());

                //pickwordState
                if(game[0].getTime() >= pickWordTime && game[0].getRoundsPlayed() <= ROUNDS && !getCancel(game[0]) && game[0].getGameState().equals(GameState.PICK_WORD_STATE)){
                    pickWord(game[0]);
                    game[0].setGameState(getNextState(game[0]));
                    System.out.println("Timer ran out, next state: " + game[0].getGameState());
                    game[0].setStartTimeSeconds(TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis()));
                    gameRepository.saveAndFlush(game[0]);
                }

                //EnterCluesState
                else if(game[0].getTime() >= enterCluesTime && game[0].getRoundsPlayed() <= ROUNDS && !getCancel(game[0]) && game[0].getGameState().equals(GameState.ENTER_CLUES_STATE)){
                    sendClue(game[0]);
                    game[0].setGameState(getNextState(game[0]));
                    System.out.println("Timer ran out, next state: " + game[0].getGameState());
                    game[0].setStartTimeSeconds(TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis()));
                    gameRepository.saveAndFlush(game[0]);
                }

                //VoteState
                else if(game[0].getTime() >= voteTime && game[0].getRoundsPlayed() <= ROUNDS && !getCancel(game[0]) && game[0].getGameState().equals(GameState.VOTE_ON_CLUES_STATE)){
                    vote(game[0]);
                    game[0].setGameState(getNextState(game[0]));
                    System.out.println("Timer ran out, next state: " + game[0].getGameState());
                    game[0].setStartTimeSeconds(TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis()));
                    gameRepository.saveAndFlush(game[0]);
                }
                //GuessState
                else if(game[0].getTime() >= guessTime && game[0].getRoundsPlayed() <= ROUNDS && !getCancel(game[0]) && game[0].getGameState().equals(GameState.ENTER_GUESS_STATE)){
                    game[0].setGuessCorrect(false);
                    game[0].setGameState(getNextState(game[0]));
                    updateScores(game[0]);

                    System.out.println("Timer ran out, next state: " + game[0].getGameState());
                    game[0].setStartTimeSeconds(TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis()));
                    gameRepository.saveAndFlush(game[0]);
                }
                //TransitionState
                else if(game[0].getTime() >= transitionTime && game[0].getRoundsPlayed() <= ROUNDS && !getCancel(game[0]) && game[0].getGameState().equals(GameState.TRANSITION_STATE)){
                    startNewRound(game[0]);
                    if(game[0].getRoundsPlayed() > ROUNDS){
                        game[0].setGameState(GameState.END_GAME_STATE);
                        game[0].setRoundsPlayed(ROUNDS);
                    } else {
                        game[0].setGameState(getNextState(game[0]));
                    }
                    System.out.println("Timer ran out, next state: " + game[0].getGameState());
                    game[0].setStartTimeSeconds(TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis()));
                    gameRepository.saveAndFlush(game[0]);
                }

                //EndGameState
                else if (game[0].getTime() >= endTime && !getCancel(game[0]) && game[0].getGameState().equals(GameState.END_GAME_STATE)){
                    game[0].getTimer().cancel();
                    game[0].getTimer().purge();
                    g.getTimer().cancel();
                    g.getTimer().purge();

                    Lobby currentLobby = getUpdatedLobby(game[0].getLobbyId());
                    currentLobby.setGameIsStarted(false);
                    lobbyRepository.saveAndFlush(currentLobby);

                    LobbyScore lobbyScore = new LobbyScore();
                    lobbyScore.setLobbyName(game[0].getLobbyName());
                    lobbyScore.setScore(game[0].getOverallScore());
                    lobbyScore.setPlayersIdInLobby(game[0].getPlayers());
                    lobbyScore.setDate(new Date());
                    lobbyScoreRepository.saveAndFlush(lobbyScore);

                    game[0].setPlayers(null);
                    game[0].setCurrentGuesser(null);
                    gameRepository.saveAndFlush(game[0]);
                    gameRepository.delete(game[0]);
                    gameRepository.flush();
                }
                //player input cancels timer
                else if (getCancel(game[0]) && game[0].getRoundsPlayed() <= ROUNDS && !game[0].getGameState().equals(GameState.END_GAME_STATE)) {
                    game[0] = getUpdatedGame(game[0]);
                    System.out.println("Timer updated because of player, Word is: " + game[0].getCurrentWord() + ", new State: " + game[0].getGameState());
                    game[0].setStartTimeSeconds(TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis()));
                    game[0].getTimer().setCancel(false);
                    gameRepository.saveAndFlush(game[0]);
                }
            }
        };
        if(game[0].getRoundsPlayed() <= ROUNDS) {
            game[0].getTimer().schedule(timerTask, 0, 1000);
        }
    }



    public Lobby getUpdatedLobby(Long lobbyId) {
        Optional<Lobby> currentLobby = lobbyRepository.findByLobbyId(lobbyId);
        if(currentLobby.isPresent()){
            return currentLobby.get();
        }
        throw new NotFoundException(String.format("Lobby with ID %d not found", lobbyId));
    }

    /**
     * Helper function to get current cancel boolean of game stored in database
     * @param game
     * @return
     */
    public boolean getCancel(Game game){
        Optional<Game> updated = gameRepository.findByLobbyId(game.getLobbyId());
        return updated.map(value -> value.getTimer().isCancel()).orElse(false);

    }

    public Game getUpdatedGame(Game game){
        Optional<Game> currentGame = gameRepository.findByLobbyId(game.getLobbyId());
        return currentGame.orElse(game);
    }

    /**
     *
     * @param game
     * @return the next state that the game will enter
     */
    public GameState getNextState(Game game){
        GameState nextGameState;
        GameState currentGameState = game.getGameState();
        switch (currentGameState){
            case PICK_WORD_STATE:
                nextGameState = GameState.ENTER_CLUES_STATE;
                break;
            case ENTER_CLUES_STATE:
                nextGameState = GameState.VOTE_ON_CLUES_STATE;
                break;
            case VOTE_ON_CLUES_STATE:
                nextGameState = GameState.ENTER_GUESS_STATE;
                break;
            case ENTER_GUESS_STATE:
                nextGameState = GameState.TRANSITION_STATE;
                break;
            case TRANSITION_STATE:
                nextGameState = GameState.PICK_WORD_STATE;
                break;
            default:
                nextGameState = GameState.END_GAME_STATE;
                break;
        }
        return nextGameState;
    }

    public void setTimer(Game game) {
        game.setTimer(new InternalTimer());
        game.getTimer().setCancel(false);
    }

    public boolean vote(Game game, Player player, List<String> invalidWords) {
        if(!player.isVoted()) {
            for(String s : invalidWords) {
                Clue clue = new Clue();
                clue.setPlayerId(player.getId());
                clue.setActualClue(s);
                game.addInvalidClue(clue);
            }
            player.setVoted(true);
        }
        else {
            throw new UnauthorizedException("This player already sent his votes!");
        }
        int counter = 0;
        for (Player p : game.getPlayers()){
            if(p.isVoted())
                counter++;
        }

        if(counter == game.getPlayers().size() - 1) {
            checkVotes(game, (int)Math.ceil(((float)game.getPlayers().size() - 1 )/2));
            game.getTimer().setCancel(true);
            gameRepository.saveAndFlush(game);
        }
        return allSent(game, counter);
    }

    public void vote(Game game) {
        for (Player p: game.getPlayers()){
            if(!game.getCurrentGuesser().equals(p) && !p.isVoted()){
                p.setVoted(true);
            }
        }
        checkVotes(game, (int)Math.ceil(((float)game.getPlayers().size() - 1 )/2));
        gameRepository.saveAndFlush(game);
    }

    public void checkVotes(Game game, int threshold) {
        List<Clue> actualInvalidClues = new ArrayList<>();
        Iterator<Clue> iterator = game.getEnteredClues().iterator();
        while(iterator.hasNext()) {
            Clue clue = iterator.next();
            int occurrences = Collections.frequency(game.getInvalidClues(), clue);
            if(occurrences >=  threshold) {
                iterator.remove();
                if(!actualInvalidClues.contains(clue)) {
                    actualInvalidClues.add(clue);
                }
            }
            if(clue.getPlayerId().equals(0L) || clue.getPlayerId().equals(-1L)) {
                if(!actualInvalidClues.contains(clue)) {
                    actualInvalidClues.add(clue);
                }
            }
        }
        //Remove duplicates from list of invalid clues to return to client
        game.setInvalidClues(actualInvalidClues);
        gameRepository.saveAndFlush(game);
    }

    public void addClue(Clue clue, Game game) {
        // if the same clue is sent twice, remove it from list of entered clues
        if(game.getEnteredClues().contains(clue)) {
            game.getEnteredClues().remove(clue);
            clue.setPlayerId(0L);
            game.addInvalidClue(clue);
        }
        //only add the clue to list of entered clues if the same clue wasn't sent before
        else if(!game.getInvalidClues().contains(clue)) {
            game.addClue(clue);
        }
    }
}


