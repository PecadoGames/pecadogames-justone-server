package ch.uzh.ifi.seal.soprafs20.service;

import ch.uzh.ifi.seal.soprafs20.GameLogic.NLP;
import ch.uzh.ifi.seal.soprafs20.GameLogic.WordReader;
import ch.uzh.ifi.seal.soprafs20.GameLogic.gameStates.GameState;
import ch.uzh.ifi.seal.soprafs20.entity.*;
import ch.uzh.ifi.seal.soprafs20.exceptions.ConflictException;
import ch.uzh.ifi.seal.soprafs20.exceptions.NotFoundException;
import ch.uzh.ifi.seal.soprafs20.exceptions.UnauthorizedException;
import ch.uzh.ifi.seal.soprafs20.repository.GameRepository;
import ch.uzh.ifi.seal.soprafs20.repository.LobbyRepository;
import ch.uzh.ifi.seal.soprafs20.repository.UserRepository;
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
    private final Logger log = LoggerFactory.getLogger(GameService.class);
    private static final int ROUNDS = 3;
    private static final int ROUNDTIME = 45;

    @Autowired
    public GameService(GameRepository gameRepository, LobbyRepository lobbyRepository,UserRepository userRepository) {
        this.gameRepository = gameRepository;
        this.lobbyRepository = lobbyRepository;
        this.userRepository = userRepository;
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

    public int getMaxTime(){
        return ROUNDTIME;
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
        newGame.setGameState(GameState.PICKWORDSTATE);
        newGame.setLobbyName(lobby.getLobbyName());


        for (Player player : lobby.getPlayersInLobby()) {
            newGame.addPlayer(player);
        }

        //if there are only 3 players, the special rule set has to be applied
        newGame.setSpecialGame(newGame.getPlayers().size() == 3);

        //assign first guesser
        Random rand = new Random();
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
     * @param clue
     * @return game with updated clue list
     */
    public boolean sendClue(Game game, Player player, String clue){
        if(!game.getGameState().equals(GameState.ENTERCLUESSTATE))
            throw new UnauthorizedException("Clues are not accepted in current state!");

        if(!game.getPlayers().contains(player) || player.isClueIsSent() || game.getCurrentGuesser().equals(player)){
            throw new UnauthorizedException("This player is not allowed to send clue!");
        }
        if (!game.isSpecialGame()) {
            player.setClue(clue);
            player.setClueIsSent(true);
            if(game.getCluesAsString().contains(clue)) {
                game.getCluesAsString().remove(clue);
            } else {
                game.getCluesAsString().add(clue);
            }
            gameRepository.saveAndFlush(game);
        }
        else {
//            sendClueSpecial(game, player, clue);//handle double clue input from player
        }
        int counter = 0;
        for (Player playerInGame : game.getPlayers()){
            if (playerInGame.isClueIsSent()){
                counter++;
            }
        }
        System.out.println("Counter = " + counter);
        if(allSent(game, counter)) {
            game.setGameState(GameState.VOTEONCLUESSTATE);
            game.getTimer().setCancel(true);
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
        game.setGameState(GameState.ENTERCLUESSTATE);
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
     * @param clue
     */
    private void sendClueSpecial(Game game, Player player, String clue) {
        String temporaryClue = player.getToken();

        if (!game.getEnteredClues().isEmpty()) {
            if(game.getCluesAsString().removeIf(clue1 -> clue1.equals(player.getToken()))) {
                game.addClueAsString(clue);
                //game.addClueAsString(clue.getActualClue());
                player.setClueIsSent(true);
                return;
            }
        }
        game.addClueAsString(clue);
//        setTimeNeeded(game, clue);
        game.addClueAsString(temporaryClue);
    }


    public void submitGuess(Game game, MessagePutDTO messagePutDTO, long time) {
        if (!game.getCurrentGuesser().getToken().equals(messagePutDTO.getPlayerToken())) {
            throw new UnauthorizedException("User is not allowed to submit a guess!");
        }
        if(!game.getGameState().equals(GameState.ENTERGUESSSTATE)) {
            throw new UnauthorizedException("Can't submit guess in current state!");
        }
        game.setGuessCorrect(messagePutDTO.getMessage().toLowerCase().equals(game.getCurrentWord().toLowerCase()));
        if(game.isGuessCorrect()){
            int pastScore = game.getCurrentGuesser().getScore();
            game.getCurrentGuesser().setScore(pastScore + (int)(2*(ROUNDTIME - time)));
            System.out.println("Guess was correct");
        } else {
            System.out.println("Guess was not correct");
        }
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
        }
        game.setGuessCorrect(false);
        game.setGameState(GameState.PICKWORDSTATE);

        //ToDo: Update scores of player and overall score
    }

    public void startNewRound(Game game) {
        game.setRoundsPlayed(game.getRoundsPlayed() + 1);

        for(Player p: game.getPlayers()){
            Optional<User> optionalUser = userRepository.findById(p.getId());
            if(optionalUser.isPresent()){
                User user = optionalUser.get();
                user.setScore(user.getScore() + p.getScore());
            }

        }

        int index = game.getPlayers().indexOf(game.getCurrentGuesser());
        Player currentGuesser = game.getPlayers().get((index + 1) % game.getPlayers().size());
        game.setCurrentGuesser(currentGuesser);

        for(Player p : game.getPlayers()){
            p.setClueIsSent(false);
            p.setVoted(false);
        }

        game.setCluesAsString(null);
        gameRepository.saveAndFlush(game);
        //ToDo: Update scores of player and overall score
    }

    public void checkClues(Game game) {
        NLP nlp = new NLP();
        System.out.println("Game clues as string: "+ game.getCluesAsString());
        game.getCluesAsString().removeIf(clue -> !nlp.checkClue(clue, game.getCurrentWord()));
//        updateCluesAsString(game);
//        System.out.println(game.getEnteredClues().toString());
        gameRepository.saveAndFlush(game);
    }

    /**
     *
     * @param game
     * @param counter
     * @return true if all clues of each player are received, false if no
     */
    public boolean allSent(Game game, int counter) {
        if(game.isSpecialGame()) {
            return counter == (game.getPlayers().size() - 1) * 2;
        }
        System.out.println("only one guesser");
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
        Random random = new Random();
        String currentWord = words.get(random.nextInt(words.size()));
        words.remove(currentWord);
        return currentWord;
    }

    public void updateScores(Game game){
        game = getUpdatedGame(game);
        Iterator<Player> iterator = game.getPlayers().iterator();
        while(iterator.hasNext()){
            Player player = iterator.next();
            if(game.getCluesAsString().contains(player.getClue())){
                int newScore = 40 * (((game.getPlayers().size()) - game.getCluesAsString().size()));
                player.setScore(player.getScore() + newScore);
                game.setOverallScore(game.getOverallScore() + player.getScore());
            }
        }

    }



    /**
     * Central timer logic for each game. Sets timer for each state,
     * If state is complete before the timer ends, the game transitions
     * into the next state with a new timer.
     * Timer also takes care of all the logic set up for the next state if no user input was entered
     * TODO: Implement the game logic changes for each state if no input is received and the timer ends
     *
     * @param g - takes a game instance as input
     * @param gameState - state to which the game transitions if timer is finished
     */
    public void timer(Game g, GameState gameState,long startTime) {
        final boolean[] cont = {false};
        final Game[] game = {g};
        final long[] time = new long[1];
        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
                game[0] = getUpdatedGame(game[0]);
                game[0].setTime(TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis()) - game[0].getStartTimeSeconds());
//                System.out.println("Timer: " + time[0]);

//                if (!getCancel(game[0]) && time[0] < ROUNDTIME) {
//                    game[0].getTimer().cancel();
//                    game[0].getTimer().purge();
//                    game[0].setTimer(new InternalTimer());
//                    gameRepository.saveAndFlush(game[0]);
//                }
                if (game[0].getTime() >= ROUNDTIME && game[0].getRoundsPlayed() <= ROUNDS && !getCancel(game[0])) {
                    game[0].getTimer().cancel();
                    game[0].getTimer().purge();

                    if ((game[0]).getGameState().equals(GameState.PICKWORDSTATE)) {
                        pickWord(game[0]);
                    }
                    else if ((game[0]).getGameState().equals(GameState.ENTERCLUESSTATE)) {
                        sendClue(game[0]);
                    }
                    else if ((game[0]).getGameState().equals(GameState.VOTEONCLUESSTATE)) {
                        vote(game[0]);
                    }
                    else if ((game[0]).getGameState().equals(GameState.ENTERGUESSSTATE)) {
                        game[0].setGuessCorrect(false);
                    }
                    else if ((game[0]).getGameState().equals(GameState.TRANSITIONSTATE)) {
                        updateScores(game[0]);
                        startNewRound(game[0]);
                    }
                    else if ((game[0]).getGameState().equals(GameState.ENDGAMESTATE)) {
                        game[0].setPlayers(null);
                        gameRepository.delete(game[0]);
                        gameRepository.flush();
                        return;
                    }
                    game[0].setGameState(getNextState(game[0]));
                    System.out.println("Timer ran out, next state: " + game[0].getGameState());
                    game[0].getTimer().cancel();
                    game[0].getTimer().purge();
                    game[0].setStartTimeSeconds(TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis()));
                    gameRepository.saveAndFlush(game[0]);
                    gameRepository.saveAndFlush(game[0]);

                }

                if(game[0].getRoundsPlayed() > ROUNDS) {
                    game[0].getTimer().cancel();
                    game[0].getTimer().purge();
                    game[0].getTimer().setCancel(true);
                    gameRepository.saveAndFlush(game[0]);
                }
                if(game[0].getGameState() == null){
                    System.out.println("Game was terminated");
                }

                if (game[0].getRoundsPlayed() <= ROUNDS && !getCancel(game[0])) {



                }
                //Update timer user input
                else if (getCancel(game[0]) && game[0].getRoundsPlayed() <= ROUNDS && !game[0].getGameState().equals(GameState.ENDGAMESTATE)) {
                    game[0] = getUpdatedGame(game[0]);
                    System.out.println("Timer updated because of player, Word is: " + game[0].getCurrentWord() + ", new State: " + game[0].getGameState());
                    game[0].getTimer().cancel();
                    game[0].getTimer().purge();
                    game[0].setTimer(new InternalTimer());
                    game[0].setStartTimeSeconds(TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis()));
                    game[0].getTimer().setCancel(false);//
                    gameRepository.saveAndFlush(game[0]);

                }

                else if (game[0].getGameState() == null) {
                    System.out.println("Game was terminated!");
                }
                else{
                    game[0].setGameState(GameState.ENDGAMESTATE);
                    game[0].getTimer().setCancel(true);
                    game[0].getTimer().cancel();
                    game[0].getTimer().purge();

                    game[0].setTimer(new InternalTimer());
                    game[0].setStartTimeSeconds(TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis()));
                    game[0].setRoundsPlayed(ROUNDS);
                    Lobby currentLobby = getUpdatedLobby(game[0].getLobbyId());
                    currentLobby.setGameIsStarted(false);
                    lobbyRepository.saveAndFlush(currentLobby);
                    game[0] = gameRepository.saveAndFlush(game[0]);
                    timer(game[0],game[0].getGameState(),game[0].getStartTimeSeconds());
                }

            }
        };
        if(game[0].getRoundsPlayed() <= ROUNDS) {
            game[0].getTimer().cancel();
            game[0].getTimer().purge();
            game[0].setTimer(new InternalTimer());
            game[0].getTimer().setCancel(false);
            game[0].getTimer().schedule(timerTask, 0, 1000);
        }
    }



    private Lobby getUpdatedLobby(Long lobbyId) {
        Optional<Lobby> currentLobby = lobbyRepository.findByLobbyId(lobbyId);
        return currentLobby.orElse(null);
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
            case PICKWORDSTATE:
                nextGameState = GameState.ENTERCLUESSTATE;
                break;
            case ENTERCLUESSTATE:
                nextGameState = GameState.VOTEONCLUESSTATE;
                break;
            case VOTEONCLUESSTATE:
                nextGameState = GameState.ENTERGUESSSTATE;
                break;
            case ENTERGUESSSTATE:
                nextGameState = GameState.TRANSITIONSTATE;
                break;
            case TRANSITIONSTATE:
                nextGameState = GameState.PICKWORDSTATE;
                break;
            case ENDGAMESTATE:
                nextGameState = GameState.ENDGAMESTATE;
            default:
                nextGameState = null;
        }
        return nextGameState;
    }

    public void setTimer(Game game) {
        game.setTimer(new InternalTimer());
        game.getTimer().setCancel(false);
    }

    public void setTimeNeeded(Game game, Clue clue) {
        long timeNeeded = TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis()) - game.getStartTimeSeconds();
        clue.setTimeNeeded(ROUNDTIME - timeNeeded);
    }

    public void updateCluesAsString(Game game) {
        for(Clue clue : game.getEnteredClues()) {
            game.addClueAsString(clue.getActualClue());
        }
        gameRepository.saveAndFlush(game);
    }


    public boolean vote(Game game, Player player, List<String> invalidWords) {
        if(!player.isVoted()) {
            game.setInvalidClues(invalidWords);
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
        Iterator<String> iterator = game.getCluesAsString().iterator();
        while(iterator.hasNext()) {
            String string = iterator.next();
            int occurrences = Collections.frequency(game.getInvalidClues(), string);
            if(occurrences >=  threshold) {
                iterator.remove();
                if(!game.getInvalidClues().contains(string)) {
                    game.addInvalidClue(string);
                }
            }
        }
        //Remove duplicates from list of invalid clues to return to client
        Set<String> set = new LinkedHashSet<>(game.getInvalidClues());
        game.getInvalidClues().clear();
        game.getInvalidClues().addAll(set);
        System.out.println("Clues after voting: " + game.getCluesAsString());
        gameRepository.saveAndFlush(game);
    }
}


