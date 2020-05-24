package ch.uzh.ifi.seal.soprafs20.service;

import ch.uzh.ifi.seal.soprafs20.GameLogic.APIResponse;
import ch.uzh.ifi.seal.soprafs20.GameLogic.NLP;
import ch.uzh.ifi.seal.soprafs20.GameLogic.WordReader;
import ch.uzh.ifi.seal.soprafs20.GameLogic.GameState;
import ch.uzh.ifi.seal.soprafs20.entity.*;
import ch.uzh.ifi.seal.soprafs20.exceptions.ConflictException;
import ch.uzh.ifi.seal.soprafs20.exceptions.NotFoundException;
import ch.uzh.ifi.seal.soprafs20.exceptions.UnauthorizedException;
import ch.uzh.ifi.seal.soprafs20.repository.*;
import ch.uzh.ifi.seal.soprafs20.rest.dto.CluePutDTO;
import ch.uzh.ifi.seal.soprafs20.rest.dto.GamePostDTO;
import ch.uzh.ifi.seal.soprafs20.rest.dto.MessagePutDTO;
import ch.uzh.ifi.seal.soprafs20.rest.dto.RequestPutDTO;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

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
    private static final int PICK_WORD_TIME = 10;
    private static final int ENTER_CLUES_TIME = 30;
    private static final int VOTE_TIME = 15;
    private static final int GUESS_TIME = 30;
    private static final int TRANSITION_TIME = 5;
    private static final int END_TIME = 10;
    private final Random rand = new Random();
    NLP nlp = new NLP();

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
            return END_TIME;
        else if(game.getGameState().equals(GameState.PICK_WORD_STATE))
            return PICK_WORD_TIME;
        else if(game.getGameState().equals(GameState.TRANSITION_STATE))
            return TRANSITION_TIME;
        else if(game.getGameState().equals(GameState.ENTER_CLUES_STATE))
            return ENTER_CLUES_TIME;
        else if(game.getGameState().equals(GameState.VOTE_ON_CLUES_STATE))
            return VOTE_TIME;
        else
            return GUESS_TIME;
    }

    /**
     * creates new Game instance, sets current guesser and chooses first word
     *
     * @param lobby Lobby for which the game is created
     * @return the created game
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
        newGame.setRounds(lobby.getRounds());


        for (Player player : lobby.getPlayersInLobby()) {
            newGame.addPlayer(player);
        }

        //if there are only 3 players, the special rule set has to be applied
        newGame.setSpecialGame((lobby.getCurrentNumBots() + lobby.getCurrentNumPlayers()) == 3);

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

    public boolean sendClue(Game game, Player player, CluePutDTO cluePutDTO) {
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
            clue.setTimeNeeded(ENTER_CLUES_TIME - (TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis()) - game.getStartTimeSeconds()));
            player.addClue(clue);
            player.setClueIsSent(true);
            // if the same clue is sent twice, remove it from list of entered clues
            addClue(clue, game);
            clueRepository.saveAndFlush(clue);
            gameRepository.saveAndFlush(game);
        }
        else {
            sendClueSpecial(game, player, cluePutDTO);
        }
        int counter = 0;
        for (Player playerInGame : game.getPlayers()){
            if (playerInGame.isClueIsSent()){
                counter++;
            }
        }
        if(allSent(game, counter)) {
            generateCluesForBots(game);
            checkClues(game);
            gameRepository.saveAndFlush(game);
            return true;
        }
        return false;
    }


    /**
     * Overloaded sendClue method for the case that the timer runs out and not every player sent a clue
     */
    public void sendClue(Game game){
        //if a user did not send a clue, fill his clue with empty string

        for(Player p : game.getPlayers()){
            if(!game.getCurrentGuesser().equals(p)) {
                p.setClueIsSent(true);
            }
        }

        if(game.getEnteredClues().isEmpty()) {
            checkClues(game);
        }
        generateCluesForBots(game);
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
     * Overloaded pickWord method for the case that the timer runs out and the guesser did not send a guess
     */
    public void pickWord(Game game) {
        game.setCurrentWord(chooseWordAtRandom(game.getWords()));
    }


    /**
     * method to send clues in case of a special game (i.e. 3 players)
     */
    private void sendClueSpecial(Game game, Player player, CluePutDTO cluePutDTO) {
        Clue firstClue = new Clue();
        firstClue.setPlayerId(player.getId());
        firstClue.setActualClue(cluePutDTO.getMessage());
        firstClue.setTimeNeeded(ENTER_CLUES_TIME - (TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis()) - game.getStartTimeSeconds()));
        player.addClue(firstClue);

        Clue secondClue = new Clue();
        secondClue.setPlayerId(player.getId());
        secondClue.setTimeNeeded(ENTER_CLUES_TIME - (TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis()) - game.getStartTimeSeconds()));
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
        if(game.getCurrentGuesser().isGuessIsSent()) {
            throw new UnauthorizedException("This player already submitted his guess!");
        }
        if(!game.getGameState().equals(GameState.ENTER_GUESS_STATE)) {
            throw new UnauthorizedException("Can't submit guess in current state!");
        }

        game.getCurrentGuesser().setGuessIsSent(true);
        game.setGuessCorrect(messagePutDTO.getMessage().equalsIgnoreCase(game.getCurrentWord()));
        game.setCurrentGuess(messagePutDTO.getMessage());
        guesserScore(game, time);
        gameRepository.saveAndFlush(game);
    }

    private void guesserScore(Game game, long time){
        int pastScore = game.getCurrentGuesser().getScore();
        int score = 0;
        if(game.isGuessCorrect()){
            if(game.isSpecialGame()){
                score = (int)((GUESS_TIME - time)*10);

            } else {
                score = (int) ((GUESS_TIME - time)*5);
            }
            game.setOverallScore(game.getOverallScore() + score);
            game.getCurrentGuesser().setScore(pastScore + score);
        }
        else {
            if(game.isSpecialGame()){
                score = -60;
            }
            if(!game.isSpecialGame()){
                score = -30;
            }
            game.getCurrentGuesser().setScore(Math.max(pastScore+score,0));
            if(game.getCurrentGuesser().getScore() <= 0){
                game.setOverallScore(Math.max(game.getOverallScore() - pastScore,0));
            } else {
                game.setOverallScore(Math.max(game.getOverallScore() + score, 0));
            }
        }
        Optional<User> optionalUser = userRepository.findById(game.getCurrentGuesser().getId());
        if(optionalUser.isPresent()){
            User user = optionalUser.get();
            user.setScore(Math.max(user.getScore() + score,0));
        }
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
            p.setGuessIsSent(false);
            p.getClues().clear();
        }
        game.getEnteredClues().clear();
        game.getInvalidClues().clear();
        game.setGameState(GameState.PICK_WORD_STATE);
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

        game.getCurrentGuesser().setGuessIsSent(false);
        game.getEnteredClues().clear();
        game.getInvalidClues().clear();
        game.setGuessCorrect(false);
        game.setCurrentGuess("");
        gameRepository.saveAndFlush(game);
    }

    public void checkClues(Game game) {
        List<Clue> invalidClues = new ArrayList<>();
        for (Clue clue : game.getEnteredClues()) {
            if (!nlp.checkClue(clue.getActualClue(), game.getCurrentWord())) {
                clue.setPlayerId(-1L);
                invalidClues.add(clue);
            }
        }
        game.getEnteredClues().removeAll(invalidClues);
        game.addInvalidClues(invalidClues);
        gameRepository.saveAndFlush(game);
    }

    /**
     * Helper function to determine if all clues or votes have been sent

     * @return true if all clues/votes of each player are received, false if no
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
     * @param words The list of words
     * @return random word from list
     */
    public String chooseWordAtRandom(List<String> words) {
        String currentWord = words.get(rand.nextInt(words.size()));
        words.remove(currentWord);
        return currentWord;
    }

    public void updateScores(Game game){
        game = getUpdatedGame(game);
        int counter = 0;
        for(Clue clue: game.getEnteredClues()){
            if(!(clue.getPlayerId()==0L)){
                counter++;
            }
        }
        for (Player player : game.getPlayers()) {
            // in case of 3-player-logic, the size of clues is 2, otherwise 1 (or 0, if player did not send any clues)
            for(int i = 0; i < player.getClues().size(); i++) {
                if(game.getEnteredClues().contains(player.getClue(i))) {
                    int newScore = 0;
                    if (!game.isSpecialGame() && game.isGuessCorrect()){
                        newScore = (int) ((player.getClue(i).getTimeNeeded()) * ((game.getPlayers().size() - counter)));
                    }
                    if (!game.isSpecialGame() && !game.isGuessCorrect()) {
                        newScore = - 15;
                    }

                    if (game.isSpecialGame() && game.isGuessCorrect()) {
                        newScore = (int) ((player.getClue(i).getTimeNeeded()) * ((game.getPlayers().size() * 2 - counter)));
                    }

                    if (game.isSpecialGame() && !game.isGuessCorrect()) {
                        newScore = - 30;
                    }
                    player.setScore(Math.max(player.getScore() + newScore, 0));
                    if (player.getScore() <= 0) {
                        game.setOverallScore(Math.max(game.getOverallScore() - player.getScore(), 0));
                    }
                    else {
                        game.setOverallScore(Math.max(game.getOverallScore() + newScore, 0));
                    }
                    Optional<User> optionalUser = userRepository.findById(player.getId());
                    if (optionalUser.isPresent()) {
                        User user = optionalUser.get();
                        user.setScore(Math.max(user.getScore() + newScore, 0));
                        userRepository.saveAndFlush(user);
                    }
                }

            }
        }

    }

    /**
     * Central timer logic for each game. Sets timer for each state,
     * If state is complete before the timer ends, the game transitions into the next state with a new timer.
     * Timer also takes care of all the logic set up for the next state if no user input was entered
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
                //PickwordState
                if(game[0].getTime() >= PICK_WORD_TIME && game[0].getRoundsPlayed() <= game[0].getRounds() && !getCancel(game[0]) && game[0].getGameState().equals(GameState.PICK_WORD_STATE)){
                    game[0].getTimer().cancel();
                    game[0].getTimer().purge();
                    pickWord(game[0]);

                    game[0].setGameState(getNextState(game[0]));
                    game[0].setStartTimeSeconds(TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis()));

                    gameRepository.saveAndFlush(game[0]);
                }

                //EnterCluesState
                else if(game[0].getTime() >= ENTER_CLUES_TIME && game[0].getRoundsPlayed() <= game[0].getRounds() && !getCancel(game[0]) && game[0].getGameState().equals(GameState.ENTER_CLUES_STATE)){
                    game[0].getTimer().cancel();
                    game[0].getTimer().purge();
                    sendClue(game[0]);
                    game[0].setGameState(getNextState(game[0]));
                    game[0].setStartTimeSeconds(TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis()));
                    gameRepository.saveAndFlush(game[0]);
                }

                //VoteState
                else if(game[0].getTime() >= VOTE_TIME && game[0].getRoundsPlayed() <= game[0].getRounds() && !getCancel(game[0]) && game[0].getGameState().equals(GameState.VOTE_ON_CLUES_STATE)){
                    game[0].getTimer().cancel();
                    game[0].getTimer().purge();
                    vote(game[0]);
                    game[0].setGameState(getNextState(game[0]));
                    game[0].setStartTimeSeconds(TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis()));
                    gameRepository.saveAndFlush(game[0]);
                }
                //GuessState
                else if(game[0].getTime() >= GUESS_TIME && game[0].getRoundsPlayed() <= game[0].getRounds() && !getCancel(game[0]) && game[0].getGameState().equals(GameState.ENTER_GUESS_STATE)){
                    game[0].getTimer().cancel();
                    game[0].getTimer().purge();
                    game[0].setGuessCorrect(false);
                    game[0].setGameState(getNextState(game[0]));
                    updateScores(game[0]);
                    guesserScore(game[0], GUESS_TIME);

                    game[0].setStartTimeSeconds(TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis()));
                    gameRepository.saveAndFlush(game[0]);
                }
                //TransitionState
                else if(game[0].getTime() >= TRANSITION_TIME && game[0].getRoundsPlayed() <= game[0].getRounds() && !getCancel(game[0]) && game[0].getGameState().equals(GameState.TRANSITION_STATE)){
                    game[0].getTimer().cancel();
                    game[0].getTimer().purge();
                    startNewRound(game[0]);
                    if(game[0].getRoundsPlayed() > game[0].getRounds()){
                        game[0].setGameState(GameState.END_GAME_STATE);
                        game[0].setRoundsPlayed(game[0].getRounds());
                    } else {
                        game[0].setGameState(getNextState(game[0]));
                    }
                    game[0].setStartTimeSeconds(TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis()));
                    gameRepository.saveAndFlush(game[0]);
                }

                //EndGameState
                else if (game[0].getTime() >= END_TIME && !getCancel(game[0]) && game[0].getGameState().equals(GameState.END_GAME_STATE)){
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

                    for(Player p: game[0].getPlayers()){
                        p.setScore(0);
                    }
                    playerRepository.saveAll(game[0].getPlayers());

                    game[0].setPlayers(null);
                    game[0].setCurrentGuesser(null);
                    gameRepository.saveAndFlush(game[0]);
                    gameRepository.delete(game[0]);
                    gameRepository.flush();
                }
                //player input cancels timer
                else if (getCancel(game[0]) && game[0].getRoundsPlayed() <= game[0].getRounds() && !game[0].getGameState().equals(GameState.END_GAME_STATE)) {
                    game[0].getTimer().cancel();
                    game[0] = getUpdatedGame(game[0]);
                    game[0].setStartTimeSeconds(TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis()));
                    game[0].getTimer().setCancel(false);
                    gameRepository.saveAndFlush(game[0]);
                }
            }
        };
        if(game[0].getRoundsPlayed() <= game[0].getRounds()) {
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
     * Helper function to get current timer cancel boolean
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
     * Helper method to return the next game state
     *
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

    public void generateCluesForBots(Game game) {
        Lobby lobby;
        Optional<Lobby> foundLobby = lobbyRepository.findByLobbyId(game.getLobbyId());
        if(foundLobby.isPresent()) {
            lobby = foundLobby.get();
        }
        else { return; }
        String uri;
        // The api call is a bit different if the current word consists of two separate words
        String[] split = game.getCurrentWord().split(" ");
        if(split.length == 1) {
            uri = String.format("https://api.datamuse.com/words?ml=%s", split[0]);
        }
        else if(split.length == 2) {
            uri = String.format("https://api.datamuse.com/words?ml=%s+%s", split[0], split[1]);
        }
        else { return; }
        RestTemplate restTemplate = new RestTemplate();
        String result = restTemplate.getForObject(uri, String.class);
        // In the case of a game with 3 players, a bot submits two clues instead of one
        int amountOfClues = (game.isSpecialGame() ? lobby.getCurrentNumBots()*2 : lobby.getCurrentNumBots());
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            List<APIResponse> response = objectMapper.readValue(result, new TypeReference<>() {
            });
            Iterator<APIResponse> iterator = response.iterator();
            for(int i = 0; i < amountOfClues; i++) {
                while(iterator.hasNext()) {
                    APIResponse apiResponse = iterator.next();
                    String potentialClue = apiResponse.getWord();
                    if(nlp.checkClue(potentialClue, game.getCurrentWord())) {
                        Clue clueFromBot = new Clue();
                        clueFromBot.setPlayerId(0L);
                        clueFromBot.setActualClue(potentialClue);
                        if(!game.getEnteredClues().contains(clueFromBot)) {
                            game.getEnteredClues().add(clueFromBot);
                            clueRepository.saveAndFlush(clueFromBot);
                            break;
                        }
                    }
                }
            }
        } catch (JsonProcessingException ex) {
            ex.getMessage();
        }
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
        // If there is only one real player and the rest are bots, voting is not necessary since bots can not vote
        if(game.getPlayers().size() < 2) {
            return;
        }
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
        }
        //Iterate over invalidClues to preserve clues voted out from NLP
        iterator = game.getInvalidClues().iterator();
        while(iterator.hasNext()) {
            Clue invalidClue = iterator.next();
            if(invalidClue.getPlayerId().equals(-1L) || invalidClue.getPlayerId().equals(0L)) {
                if(!actualInvalidClues.contains(invalidClue)) {
                    actualInvalidClues.add(invalidClue);
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
            if(!game.getInvalidClues().contains(clue)) {
                game.addInvalidClue(clue);
            }
        }
        //only add the clue to list of entered clues if the same clue wasn't sent before
        else if(!game.getInvalidClues().contains(clue)) {
            game.addClue(clue);
        }
        gameRepository.saveAndFlush(game);
    }

}


