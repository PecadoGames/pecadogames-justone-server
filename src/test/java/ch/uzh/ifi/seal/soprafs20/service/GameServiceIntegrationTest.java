package ch.uzh.ifi.seal.soprafs20.service;

import ch.uzh.ifi.seal.soprafs20.GameLogic.gameStates.GameState;
import ch.uzh.ifi.seal.soprafs20.entity.Game;
import ch.uzh.ifi.seal.soprafs20.entity.Lobby;
import ch.uzh.ifi.seal.soprafs20.entity.Player;
import ch.uzh.ifi.seal.soprafs20.exceptions.ConflictException;
import ch.uzh.ifi.seal.soprafs20.repository.GameRepository;
import ch.uzh.ifi.seal.soprafs20.repository.LobbyRepository;
import ch.uzh.ifi.seal.soprafs20.repository.PlayerRepository;
import ch.uzh.ifi.seal.soprafs20.rest.dto.GamePostDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.web.WebAppConfiguration;

import static org.junit.jupiter.api.Assertions.*;

@WebAppConfiguration
@SpringBootTest
public class GameServiceIntegrationTest {

    @Autowired
    private GameRepository gameRepository;

    @Autowired
    private LobbyRepository lobbyRepository;

    @Autowired
    private PlayerRepository playerRepository;

    @Autowired
    private GameService gameService;

    @BeforeEach
    public void setup() {
        gameRepository.deleteAll();
        lobbyRepository.deleteAll();
        playerRepository.deleteAll();
    }

    @Test
    public void createGame_validInput_success() {
        Player host = new Player();
        host.setId(1L);
        host.setToken("hostToken");
        host.setUsername("host");

        playerRepository.save(host);
        playerRepository.flush();

        Lobby lobby = new Lobby();
        lobby.setLobbyId(1L);
        lobby.setHostId(host.getId());
        lobby.setHostToken(host.getToken());
        lobby.setLobbyName("BadBunny");
        lobby.setPrivate(true);
        lobby.setMaxPlayersAndBots(7);
        lobby.setVoiceChat(false);
        lobby.addPlayerToLobby(host);
        lobby.setCurrentNumPlayersAndBots(1);

        lobbyRepository.save(lobby);
        lobbyRepository.flush();

        GamePostDTO gamePostDTO = new GamePostDTO();
        gamePostDTO.setHostToken(host.getToken());
        gamePostDTO.setHostId(host.getId());

        Game createdGame = gameService.createGame(lobby, gamePostDTO);

        assertTrue(lobby.isGameStarted());
        assertEquals(lobby.getLobbyId(), createdGame.getLobbyId());
        assertEquals(GameState.PICKWORDSTATE, createdGame.getGameState());
        assertEquals(lobby.getLobbyName(), createdGame.getLobbyName());
        assertTrue(createdGame.getPlayers().contains(host));
        assertFalse(createdGame.isSpecialGame());
        assertEquals(1, createdGame.getRoundsPlayed());
        assertEquals(13, createdGame.getWords().size());
    }

    @Test
    public void createGame_gameAlreadyStarted_throwsException() {
        Player host = new Player();
        host.setId(1L);
        host.setToken("hostToken");
        host.setUsername("host");

        playerRepository.save(host);
        playerRepository.flush();

        Lobby lobby = new Lobby();
        lobby.setLobbyId(1L);
        lobby.setHostId(host.getId());
        lobby.setHostToken(host.getToken());
        lobby.setLobbyName("BadBunny");
        lobby.setPrivate(true);
        lobby.setMaxPlayersAndBots(7);
        lobby.setVoiceChat(false);
        lobby.addPlayerToLobby(host);
        lobby.setCurrentNumPlayersAndBots(1);
        lobby.setGameIsStarted(true);

        lobbyRepository.save(lobby);
        lobbyRepository.flush();

        GamePostDTO gamePostDTO = new GamePostDTO();
        gamePostDTO.setHostToken(host.getToken());
        gamePostDTO.setHostId(host.getId());

        assertThrows(ConflictException.class, () -> gameService.createGame(lobby, gamePostDTO));
    }
}
