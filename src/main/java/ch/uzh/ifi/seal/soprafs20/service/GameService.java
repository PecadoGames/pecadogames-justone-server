package ch.uzh.ifi.seal.soprafs20.service;

import ch.uzh.ifi.seal.soprafs20.GameLogic.NLP;
import ch.uzh.ifi.seal.soprafs20.GameLogic.WordReader;
import ch.uzh.ifi.seal.soprafs20.GameLogic.gameStates.GameState;
import ch.uzh.ifi.seal.soprafs20.entity.*;
import ch.uzh.ifi.seal.soprafs20.exceptions.ConflictException;
import ch.uzh.ifi.seal.soprafs20.exceptions.NotFoundException;
import ch.uzh.ifi.seal.soprafs20.exceptions.UnauthorizedException;
import ch.uzh.ifi.seal.soprafs20.repository.GameRepository;
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
public class GameService extends Thread{
    private final GameRepository gameRepository;
    private final Logger log = LoggerFactory.getLogger(GameService.class);
    private static final int ROUNDS = 4;
    private static final int ROUNDTIME = 50;

    @Autowired
    public GameService(GameRepository gameRepository) {
        this.gameRepository = gameRepository;
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
    public boolean sendClue(Game game, Player player, Clue clue){
        if(!game.getGameState().equals(GameState.ENTERCLUESSTATE))
            throw new UnauthorizedException("Clues are not accepted in current state!");

        if(!game.getPlayers().contains(player) || player.isClueIsSent() || game.getCurrentGuesser().equals(player)){
            throw new UnauthorizedException("This player is not allowed to send clue!");
        }
        if (!game.isSpecialGame()) {
            game.addClue(clue);
            game.addClueAsString(clue.getActualClue());
            player.setClueIsSent(true);
            gameRepository.saveAndFlush(game);
        }
        else {
            sendClueSpecial(game, player, clue);//handle double clue input from player
        }
        int counter = 0;
        for (Player playerInGame : game.getPlayers()){
            if (playerInGame.isClueIsSent()){
                counter++;
            }
        }
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
    private void sendClueSpecial(Game game, Player player, Clue clue) {
        Clue temporaryClue = new Clue();
        temporaryClue.setPlayerId(player.getId());
        temporaryClue.setActualClue(player.getToken());
        if (!game.getEnteredClues().isEmpty()) {
            if(game.getEnteredClues().removeIf(clue1 -> clue1.getActualClue().equals(player.getToken()))) {
                game.addClue(clue);
                game.addClueAsString(clue.getActualClue());
                player.setClueIsSent(true);
                return;
            }
        }
        game.addClue(clue);
        game.addClueAsString(clue.getActualClue());
        setTimeNeeded(game, clue);
        game.addClue(temporaryClue);
    }


    public void submitGuess(Game game, MessagePutDTO messagePutDTO) {
        if (!game.getCurrentGuesser().getToken().equals(messagePutDTO.getPlayerToken())) {
            throw new UnauthorizedException("User is not allowed to submit a guess!");
        }
        if(!game.getGameState().equals(GameState.ENTERGUESSSTATE)) {
            throw new UnauthorizedException("Can't submit guess in current state!");
        }

        game.setGuessCorrect(messagePutDTO.getMessage().toLowerCase().equals(game.getCurrentWord().toLowerCase()));
        if(game.isGuessCorrect()){
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
        }

        game.setGameState(GameState.PICKWORDSTATE);

        //ToDo: Update scores of player and overall score
    }

    public void startNewRound(Game game) {
        game.setRoundsPlayed(game.getRoundsPlayed() + 1);

        int index = game.getPlayers().indexOf(game.getCurrentGuesser());
        Player currentGuesser = game.getPlayers().get((index + 1) % game.getPlayers().size());
        game.setCurrentGuesser(currentGuesser);

        for(Player p : game.getPlayers()){
            p.setClueIsSent(false);
        }

        game.setCluesAsString(null);
        gameRepository.saveAndFlush(game);
        //ToDo: Update scores of player and overall score
    }

    public void checkClues(Game game) {
        NLP nlp = new NLP();
        Iterator<Clue> iterator = game.getEnteredClues().iterator();
        while(iterator.hasNext()) {
            if(!nlp.checkClue(iterator.next().getActualClue(), game.getCurrentWord())) {
                iterator.remove();
            }
        }
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


    /**
     * Central timer logic for each game. Sets timer for each state,
     * If state is complete before the timer ends, the game transitions
     * into the next state with a new timer.
     * Timer also takes care of all the logic set up for the next state if no user input was entered
     * TODO: Implement the game logic changes for each state if no input is received and the timer ends
     *
     * @param game - takes a game instance as input
     * @param gameState - state to which the game transitions if timer is finished
     */
    public void timer(Game game, GameState gameState,long startTime) {
        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
                while (!getUpdatedGame(game).getTimer().isCancel()) {
                    game.setTime(TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis()) - startTime);
                    if (game.getTime() >= ROUNDTIME && getUpdatedGame(game).getRoundsPlayed() <= ROUNDS) {
                        game.getTimer().cancel();
                        game.getTimer().purge();

                        if(getUpdatedGame(game).getGameState().equals(GameState.TRANSITIONSTATE)){
                            startNewRound(game);
                        }
                        if(getUpdatedGame(game).getGameState().equals(GameState.PICKWORDSTATE)){
                            pickWord(game);
                        }
                        if(getUpdatedGame(game).getGameState().equals(GameState.ENTERCLUESSTATE)){
                            sendClue(game);
                        }
                        if(getUpdatedGame(game).getGameState().equals(GameState.ENTERGUESSSTATE)){
                            game.setGuessCorrect(false);
                        }
                        game.setGameState(getNextState(game));
                        gameRepository.saveAndFlush(game);
                        break;
                    }
                    if (game.getRoundsPlayed() > ROUNDS) {
                        game.getTimer().cancel();
                        game.getTimer().purge();
                        game.getTimer().setCancel(true);
                        break;
                    }
                }
                //timer ran out, transition to next state
                if (game.getRoundsPlayed() <= ROUNDS && !getCancel(game)) {
                    Game updatedGame = getUpdatedGame(game);
                    System.out.println("Timer ran out, next state: " + updatedGame.getGameState());
                    updatedGame.getTimer().cancel();
                    updatedGame.getTimer().purge();
                    game.getTimer().cancel();
                    game.getTimer().purge();
                    updatedGame.setStartTimeSeconds(TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis()));
                    updatedGame.setTimer(new InternalTimer());
                    gameRepository.saveAndFlush(updatedGame);
                    timer(updatedGame, updatedGame.getGameState(), updatedGame.getStartTimeSeconds());

                }
                //user input before timer ran out, update timer and transition to next state
                else if(game.getRoundsPlayed() <= ROUNDS && getCancel(game)){
                    Game updatedGame = getUpdatedGame(game);
                    System.out.println("Timer updated because of player, Word is: " + updatedGame.getCurrentWord() + ", new State: " + updatedGame.getGameState());
                    updatedGame.getTimer().cancel();
                    updatedGame.getTimer().purge();
                    game.getTimer().cancel();
                    game.getTimer().purge();
                    updatedGame.getTimer().setCancel(false);
                    updatedGame.setStartTimeSeconds(TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis()));
//                    updatedGame.setGameState(getNextState(game));
                    updatedGame.setTimer(new InternalTimer());
                    updatedGame.getTimer().setCancel(false);//
                    gameRepository.saveAndFlush(updatedGame);
                    timer(updatedGame, updatedGame.getGameState(), updatedGame.getStartTimeSeconds());
                }
                //game reached final state in final round = Game is over
                else {
                    game.setGameState(GameState.ENDGAMESTATE);
                    game.getTimer().setCancel(true);
                    game.getTimer().setRunning(false);
                    game.getTimer().cancel();
                    game.getTimer().purge();
                    game.setTimer(new InternalTimer());
                    gameRepository.saveAndFlush(game);
                }
            }
        };
        if (game.getRoundsPlayed() <= ROUNDS) {
            game.getTimer().cancel();
            game.getTimer().purge();
            game.setTimer(new InternalTimer());
            game.getTimer().setCancel(false);
            game.getTimer().schedule(timerTask, 0, 1000);
        }
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


    public boolean vote(Game game, Player player, List<String> badWords) {
        for(String bw: badWords){
            game.addBadWord(bw);
            player.setVoted(true);
        }

        int remove = 0;
        for(String bw: game.getBadWords()){
            for(String w: game.getBadWords()){
                if(bw.equals(w)){
                    remove++;
                }
            }
            remove = remove - 1;//counted itself
            if(remove >= ((game.getPlayers().size() - 1 )/2)){
                game.getCluesAsString().remove(bw);
            }
        }
        int counter = 0;
        for (Player p : game.getPlayers()){
            if(p.isVoted())
                counter++;
        }

        gameRepository.saveAndFlush(game);
        return allSent(game, counter);
    }
}


