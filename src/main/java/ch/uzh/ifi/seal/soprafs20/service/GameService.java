package ch.uzh.ifi.seal.soprafs20.service;

import ch.uzh.ifi.seal.soprafs20.GameLogic.WordReader;
import ch.uzh.ifi.seal.soprafs20.GameLogic.gameStates.GameState;
import ch.uzh.ifi.seal.soprafs20.entity.Game;
import ch.uzh.ifi.seal.soprafs20.entity.InternalTimer;
import ch.uzh.ifi.seal.soprafs20.entity.Lobby;
import ch.uzh.ifi.seal.soprafs20.entity.Player;
import ch.uzh.ifi.seal.soprafs20.exceptions.ConflictException;
import ch.uzh.ifi.seal.soprafs20.exceptions.ForbiddenException;
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

import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.concurrent.TimeUnit;


/**
 * Game Service
 * This class is the "worker" and responsible for all functionality related to the Game
 * The result will be passed back to the caller.
 */
@Service
@Transactional
public class GameService {
    private final GameRepository gameRepository;
    private final Logger log = LoggerFactory.getLogger(GameService.class);

    @Autowired
    public GameService(GameRepository gameRepository) {
        this.gameRepository = gameRepository;
    }

    public Game getGame(Long id) {
        Game game;
        Optional<Game> optionalGame = gameRepository.findById(id);
        if(optionalGame.isPresent()){
            game = optionalGame.get();
            return game;
        } else {
            throw new NotFoundException("Could not find game!");
        }
    }

    /**
     *  creates new Game instance, sets current guesser and chooses first word
     * @param lobby
     * @param gamePostDTO
     * @return
     */
    public Game createGame(Lobby lobby, GamePostDTO gamePostDTO) {
        if(!lobby.getToken().equals(gamePostDTO.getHostToken())) {
            throw new UnauthorizedException("You are not allowed to start the game.");
        }
        if(lobby.isGameStarted()){
            throw new ConflictException("Game has already started!");
        }
        //set lobby status to started
        lobby.setGameIsStarted(true);

        //init new game
        Game newGame = new Game();
        newGame.setLobbyId(lobby.getLobbyId());
        newGame.setGameState(GameState.PICKWORDSTATE);


        for(Player player : lobby.getPlayersInLobby()) {
            newGame.addPlayer(player);
        }

        if(newGame.getPlayers().size() == 3){
            newGame.setSpecialGame(true);
        } else {newGame.setSpecialGame(false);}

        //assign first guesser
        Random rand = new Random();
        Player currentGuesser = newGame.getPlayers().get(rand.nextInt(newGame.getPlayers().size()));
        newGame.setCurrentGuesser(currentGuesser);

        //set round count to 0
        newGame.setRoundsPlayed(0);

        //select 13 random words from words.txt
        WordReader reader = new WordReader();
        newGame.setWords(reader.getRandomWords(13));

        newGame = gameRepository.save(newGame);
        gameRepository.flush();
        return newGame;
    }

    /**
     *
     * @param game
     * @param player
     * @param clue
     * @return game with updated clue list
     */
    public boolean sendClue(Game game, Player player, String clue){
//        if(isTimeOver(game)){
//            game.setGameState(GameState.NLPSTATE);
//            return game;
//        }
        if(!game.getPlayers().contains(player) || player.isClueIsSent() || game.getCurrentGuesser().equals(player)){
            throw new ForbiddenException("User not allowed to send clue");
        }

        if(!game.isSpecialGame()) {
            game.addClue(clue);
            player.setClueIsSent(true);
        } else {
            sendClueSpecial(game,player,clue);//handle double clue input from player
        }
        int counter = 0;
        for(Player playerInGame : game.getPlayers()){
            if (player.isClueIsSent()){
                counter++;
            }
        }
        if(counter == game.getPlayers().size() - 1 && !game.isSpecialGame()){
            game.setGameState(GameState.NLPSTATE);
            return true;
        }
        else if(counter == (game.getPlayers().size() - 1) * 2 && game.isSpecialGame()){
            game.setGameState(GameState.NLPSTATE);
            return true;
        } else
            return false;
    }

    public void pickWord(String token, Game game) {
        if(!game.getCurrentGuesser().getToken().equals(token)){
            throw new UnauthorizedException("This user is not allowed to pick a word!");
        }
        game.setCurrentWord(chooseWordAtRandom(game.getWords()));
        game.setGameState(GameState.ENTERCLUESSTATE);
        setStartTime(TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis()), game);
        if(game.getTimer() == null){
            InternalTimer internalTimer = new InternalTimer();
            setTimer(game,internalTimer);
        }
    }

    /**
     * method adds clue and token of player to clues. When a player enters their second clue, their token is replaced with it
     * @param game
     * @param player
     * @param clue
     * @return
     */
    private Game sendClueSpecial(Game game, Player player, String clue) {
        if (!game.getEnteredClues().isEmpty()) {
            for (String storedClue : game.getEnteredClues()) {
                if (player.getToken().equals(storedClue)) {
                    game.getEnteredClues().removeIf(enteredClue -> player.getToken().equals(enteredClue));
                    game.addClue(clue);
                    player.setClueIsSent(true);
                }
            }
        }
        game.addClue(clue);
        game.addClue(player.getToken());
        return game;
    }

    public void submitGuess(Game game, MessagePutDTO messagePutDTO, long currentTimeSeconds) {
        if(!game.getCurrentGuesser().getToken().equals(messagePutDTO.getPlayerToken())) {
            throw new ForbiddenException("User is not allowed to submit a guess!");
        }
        if(currentTimeSeconds - game.getStartTimeSeconds() > 60){
            throw new ForbiddenException("Time ran out!");
        }
        game.setGuessCorrect(messagePutDTO.getMessage().toLowerCase().equals(game.getCurrentWord().toLowerCase()));
        game.setGameState(GameState.TRANSITIONSTATE);
    }

    public void startNewRound(Game game, RequestPutDTO requestPutDTO) {
        if(!game.getCurrentGuesser().getToken().equals(requestPutDTO.getToken())) {
            throw new ForbiddenException("User is not allowed to start a new round!");
        }
        game.setRoundsPlayed(game.getRoundsPlayed() + 1);

        int index = game.getPlayers().indexOf(game.getCurrentGuesser());
        Player currentGuesser = game.getPlayers().get((index + 1) % game.getPlayers().size());
        game.setCurrentGuesser(currentGuesser);

        game.setGameState(GameState.PICKWORDSTATE);

        //ToDo: Update scores of player and overall score
    }

    public void setStartTime(long time, Game game){
        game.setStartTimeSeconds(time);
    }



    /**
     * Helper function that returns a random word from list and deletes it from list
     * @param words
     * @return
     */
    public String chooseWordAtRandom(List<String> words){
        Random random = new Random();
        String currentWord = words.get(random.nextInt(13));
        words.remove(currentWord);
        return currentWord;
    }

    public String getTime(String time){
        return time;
    }
    public void setState(Game game, GameState gameState){
        game.setGameState(gameState);
    }

    /**
     * Intern timer for server, if timer ends transition to next state
     * @param game
     * @param gameState - state to which the game transitions if timer is finished
     */
    public void timer(Game game,GameState gameState){
        InternalTimer internalTimer = new InternalTimer();
//        internalTimerService.createInternalTimer(internalTimer,60,game.getStartTimeSeconds());
    }

    public void setTimer(Game game, InternalTimer internalTimer) {
        game.setTimer(internalTimer);
    }
}
