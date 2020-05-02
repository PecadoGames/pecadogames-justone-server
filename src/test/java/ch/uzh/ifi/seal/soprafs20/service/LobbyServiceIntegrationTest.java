package ch.uzh.ifi.seal.soprafs20.service;

import ch.uzh.ifi.seal.soprafs20.entity.Lobby;
import ch.uzh.ifi.seal.soprafs20.entity.Player;
import ch.uzh.ifi.seal.soprafs20.exceptions.ConflictException;
import ch.uzh.ifi.seal.soprafs20.exceptions.NotAcceptableException;
import ch.uzh.ifi.seal.soprafs20.repository.LobbyRepository;
import ch.uzh.ifi.seal.soprafs20.repository.PlayerRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.web.WebAppConfiguration;

import static org.junit.jupiter.api.Assertions.*;

@WebAppConfiguration
@SpringBootTest
public class LobbyServiceIntegrationTest {

    @Autowired
    private LobbyRepository lobbyRepository;

    @Autowired
    private PlayerRepository playerRepository;

    @Autowired
    private LobbyService lobbyService;

    @BeforeEach
    public void setup() {
        lobbyRepository.deleteAll();
        playerRepository.deleteAll();
    }

    @Test
    public void createLobby_private_validInput_success() {
        Player host = new Player();
        host.setId(1L);
        host.setToken("hostToken");
        host.setUsername("host");

        playerRepository.save(host);
        playerRepository.flush();

        Lobby lobby = new Lobby();
        lobby.setLobbyId(1L);
        lobby.setHostId(1L);
        lobby.setHostToken("hostToken");
        lobby.setLobbyName("BadBunny");
        lobby.setPrivate(true);
        lobby.setMaxPlayersAndBots(7);
        lobby.setVoiceChat(false);

        Lobby createdLobby = lobbyService.createLobby(lobby, host);

        assertEquals(lobby.getLobbyName(), createdLobby.getLobbyName());
        assertNotNull(createdLobby.getPrivateKey());
        assertEquals(createdLobby.getHostId(), 1L);
        assertEquals(createdLobby.getHostToken(), "hostToken");
        assertEquals(createdLobby.getMaxPlayersAndBots(), lobby.getMaxPlayersAndBots());
        assertFalse(createdLobby.isVoiceChat());
    }

    @Test
    public void createLobby_public_validInput_success() {
        Player host = new Player();
        host.setId(1L);
        host.setToken("hostToken");
        host.setUsername("host");

        playerRepository.save(host);
        playerRepository.flush();

        Lobby lobby = new Lobby();
        lobby.setLobbyId(1L);
        lobby.setHostId(1L);
        lobby.setHostToken("hostToken");
        lobby.setLobbyName("BadBunny");
        lobby.setPrivate(false);
        lobby.setMaxPlayersAndBots(7);
        lobby.setVoiceChat(false);

        Lobby createdLobby = lobbyService.createLobby(lobby, host);

        assertEquals(lobby.getLobbyName(), createdLobby.getLobbyName());
        assertNull(createdLobby.getPrivateKey());
        assertEquals(createdLobby.getHostId(), 1L);
        assertEquals(createdLobby.getHostToken(), "hostToken");
        assertEquals(createdLobby.getMaxPlayersAndBots(), lobby.getMaxPlayersAndBots());
        assertFalse(createdLobby.isVoiceChat());
    }

    @Test
    public void createLobby_invalidLobbyName_throwsException() {
        Player host = new Player();
        host.setId(1L);
        host.setToken("hostToken");
        host.setUsername("host");

        playerRepository.save(host);
        playerRepository.flush();

        Lobby lobby = new Lobby();
        lobby.setLobbyId(1L);
        lobby.setHostId(1L);
        lobby.setHostToken("hostToken");
        lobby.setLobbyName("Bad Bunny");
        lobby.setPrivate(false);
        lobby.setMaxPlayersAndBots(7);
        lobby.setVoiceChat(false);

        assertThrows(NotAcceptableException.class, () -> lobbyService.createLobby(lobby, host));
        assertTrue(lobbyRepository.findByHostId(lobby.getHostId()).isEmpty());
    }

    @Test
    public void createLobby_lobbyAlreadyExists_throwsException() {
        Player host = new Player();
        host.setId(2L);
        host.setToken("hostToken2");
        host.setUsername("host2");

        playerRepository.save(host);
        playerRepository.flush();

        Lobby lobby = new Lobby();
        lobby.setLobbyId(1L);
        lobby.setHostId(host.getId());
        lobby.setHostToken(host.getToken());
        lobby.setLobbyName("BadBunny");
        lobby.setPrivate(false);
        lobby.setMaxPlayersAndBots(7);
        lobby.setVoiceChat(false);

        Lobby createdLobby = lobbyService.createLobby(lobby, host);
        Exception ex = assertThrows(ConflictException.class, () -> lobbyService.createLobby(lobby, host));
        assertTrue(ex.getMessage().contains("already hosting another lobby"));
        }
}
