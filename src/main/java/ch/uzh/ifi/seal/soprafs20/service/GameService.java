package ch.uzh.ifi.seal.soprafs20.service;

import ch.uzh.ifi.seal.soprafs20.GameLogic.WordReader;
import ch.uzh.ifi.seal.soprafs20.entity.Lobby;
import ch.uzh.ifi.seal.soprafs20.entity.User;
import ch.uzh.ifi.seal.soprafs20.entity.gameLogic.Game;
import ch.uzh.ifi.seal.soprafs20.exceptions.UnauthorizedException;
import ch.uzh.ifi.seal.soprafs20.rest.dto.GamePostDTO;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.net.URISyntaxException;


/**
 * Game Service
 * This class is the "worker" and responsible for all functionality related to the Game
 * The result will be passed back to the caller.
 */
@Service
@Transactional
public class GameService {

    public Game createGame(Lobby lobby, GamePostDTO gamePostDTO) throws IOException, URISyntaxException {
        if(!lobby.getToken().equals(gamePostDTO.getUserToken())) {
            throw new UnauthorizedException("You are not allowed to start the game.");
        }
        Game newGame = new Game();
        newGame.setLobbyId(lobby.getLobbyId());
        for(User user : lobby.getUsersInLobby()) {
            newGame.addPlayer(user);
        }
        newGame.setRoundsPlayed(0);
        //select 13 random words from the words.txt file using WordReader.class
        WordReader reader = new WordReader();
        reader.getRandomWords(13);

        return null;
    }

}
