package ch.uzh.ifi.seal.soprafs20.service;

import ch.uzh.ifi.seal.soprafs20.GameLogic.WordReader;
import ch.uzh.ifi.seal.soprafs20.GameLogic.gameStates.PickWordState;
import ch.uzh.ifi.seal.soprafs20.entity.Lobby;
import ch.uzh.ifi.seal.soprafs20.entity.User;
import ch.uzh.ifi.seal.soprafs20.entity.Game;
import ch.uzh.ifi.seal.soprafs20.exceptions.ConflictException;
import ch.uzh.ifi.seal.soprafs20.exceptions.ForbiddenException;
import ch.uzh.ifi.seal.soprafs20.exceptions.NotFoundException;
import ch.uzh.ifi.seal.soprafs20.exceptions.UnauthorizedException;
import ch.uzh.ifi.seal.soprafs20.repository.GameRepository;
import ch.uzh.ifi.seal.soprafs20.rest.dto.GamePostDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.Random;


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
        if(!lobby.getToken().equals(gamePostDTO.getUserToken())) {
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
        newGame.setGameState(new PickWordState());


        for(User user : lobby.getUsersInLobby()) {
            newGame.addPlayer(user);
        }
        //assign first guesser
        Random rand = new Random();
        User currentGuesser = newGame.getPlayers().get(rand.nextInt(newGame.getPlayers().size()));
        newGame.setCurrentGuesser(currentGuesser);

        //set round count to 0
        newGame.setRoundsPlayed(0);

        //select 13 random words from the words.txt
        WordReader reader = new WordReader();
        newGame.setWords(reader.getRandomWords(13));

        newGame = gameRepository.save(newGame);
        gameRepository.flush();
        return newGame;
    }

    /**
     *
     * @param game
     * @param user
     * @param clue
     * @return game with updated clue list
     */
    public Game sendClue(Game game, User user, String clue){
        if(!game.getPlayers().contains(user) || user.isSent() || game.getCurrentGuesser().equals(user)){
            throw new ForbiddenException("User not allowed to send clue");
        }
        game.addClue(clue);
        user.setSent(true);
        int counter = 0;
        for(User player: game.getPlayers()){
            if (player.isSent()){
                counter++;
            }
        }
        if(counter == game.getPlayers().size()-1){
            //game.setGameState(); set next game State
        }
        return game;
    }


}
