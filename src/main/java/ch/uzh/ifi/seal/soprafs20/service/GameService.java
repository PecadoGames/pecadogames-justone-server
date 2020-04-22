package ch.uzh.ifi.seal.soprafs20.service;

import ch.uzh.ifi.seal.soprafs20.GameLogic.WordReader;
import ch.uzh.ifi.seal.soprafs20.entity.Lobby;
import ch.uzh.ifi.seal.soprafs20.entity.User;
import ch.uzh.ifi.seal.soprafs20.entity.gameLogic.Game;
import ch.uzh.ifi.seal.soprafs20.exceptions.NotFoundException;
import ch.uzh.ifi.seal.soprafs20.exceptions.UnauthorizedException;
import ch.uzh.ifi.seal.soprafs20.repository.GameRepository;
import ch.uzh.ifi.seal.soprafs20.rest.dto.GamePostDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;


/**
 * Game Service
 * This class is the "worker" and responsible for all functionality related to the Game
 * The result will be passed back to the caller.
 */
@Service
@Transactional
public class GameService {
    private final GameRepository gameRepository;

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

    public Game createGame(Lobby lobby, GamePostDTO gamePostDTO) {
        if(!lobby.getToken().equals(gamePostDTO.getUserToken())) {
            throw new UnauthorizedException("You are not allowed to start the game.");
        }
        Game newGame = new Game();
        newGame.setLobbyId(lobby.getLobbyId());
        for(User user : lobby.getUsersInLobby()) {
            newGame.addPlayer(user);
        }
        newGame.setRoundsPlayed(0);

        //select 13 random words from the words.txt
        WordReader reader = new WordReader();
        newGame.setWords(reader.getRandomWords(13));
        newGame = gameRepository.save(newGame);
        gameRepository.flush();
        return newGame;
    }

}
