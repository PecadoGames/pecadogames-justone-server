package ch.uzh.ifi.seal.soprafs20.service;

import ch.uzh.ifi.seal.soprafs20.entity.Lobby;
import ch.uzh.ifi.seal.soprafs20.entity.Player;
import ch.uzh.ifi.seal.soprafs20.exceptions.ConflictException;
import ch.uzh.ifi.seal.soprafs20.exceptions.NotAcceptableException;
import ch.uzh.ifi.seal.soprafs20.repository.LobbyRepository;
import ch.uzh.ifi.seal.soprafs20.repository.PlayerRepository;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.web.WebAppConfiguration;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@WebAppConfiguration
@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class LobbyServiceIntegrationTest {

    @Autowired
    private LobbyRepository lobbyRepository;

    @Autowired
    private PlayerRepository playerRepository;

    @Autowired
    private LobbyService lobbyService;

    @AfterAll
    public void setup() {
        List<Lobby> allLobbies = lobbyRepository.findAll();
        for(Lobby l : allLobbies) {
            l.replacePlayersInLobby(null);
        }
        lobbyRepository.deleteAll();
        playerRepository.deleteAll();
    }

    @Test
    public void createLobby_private_validInput_success() {
        Player host = new Player();
        host.setId(1L);
        host.setToken("hostToken1");
        host.setUsername("host1");

        playerRepository.save(host);
        playerRepository.flush();

        Lobby lobby = new Lobby();
        lobby.setHostId(1L);
        lobby.setHostToken(host.getToken());
        lobby.setLobbyName("BadBunny");
        lobby.setPrivate(true);
        lobby.setMaxPlayersAndBots(7);
        lobby.setVoiceChat(false);

        Lobby createdLobby = lobbyService.createLobby(lobby, host);

        assertEquals(lobby.getLobbyName(), createdLobby.getLobbyName());
        assertNotNull(createdLobby.getPrivateKey());
        assertEquals(createdLobby.getHostId(), 1L);
        assertEquals(createdLobby.getHostToken(), host.getToken());
        assertTrue(createdLobby.getPlayersInLobby().contains(host));
        assertEquals(createdLobby.getMaxPlayersAndBots(), lobby.getMaxPlayersAndBots());
        assertFalse(createdLobby.isVoiceChat());

    }

    @Test
    public void createLobby_public_validInput_success() {
        Player badbunny = new Player();
        badbunny.setId(2L);
        badbunny.setToken("hostToken2");
        badbunny.setUsername("host2");

        playerRepository.save(badbunny);
        playerRepository.flush();

        Lobby lobby = new Lobby();
        lobby.setHostId(badbunny.getId());
        lobby.setHostToken(badbunny.getToken());
        lobby.setLobbyName("BadBunny");
        lobby.setPrivate(false);
        lobby.setMaxPlayersAndBots(7);
        lobby.setVoiceChat(false);

        Lobby createdLobby = lobbyService.createLobby(lobby, badbunny);

        assertEquals(lobby.getLobbyName(), createdLobby.getLobbyName());
        assertNull(createdLobby.getPrivateKey());
        assertEquals(createdLobby.getHostId(), badbunny.getId());
        assertEquals(createdLobby.getHostToken(), badbunny.getToken());
        assertEquals(createdLobby.getMaxPlayersAndBots(), lobby.getMaxPlayersAndBots());
        assertFalse(createdLobby.isVoiceChat());
    }

    @Test
    public void createLobby_invalidLobbyName_throwsException() {
        Player host = new Player();
        host.setId(3L);
        host.setToken("hostToken3");
        host.setUsername("host3");

        playerRepository.save(host);
        playerRepository.flush();

        Lobby lobby = new Lobby();
        lobby.setHostId(host.getId());
        lobby.setHostToken(host.getToken());
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
        host.setId(4L);
        host.setToken("hostToken4");
        host.setUsername("host4");

        playerRepository.save(host);
        playerRepository.flush();

        Lobby lobby = new Lobby();
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

    @Test
    public void deleteLobby_success() {
        Player host = new Player();
        host.setId(5L);
        host.setToken("hostToken5");
        host.setUsername("host5");

        playerRepository.save(host);
        playerRepository.flush();

        Lobby lobby = new Lobby();
        lobby.setHostId(host.getId());
        lobby.setHostToken(host.getToken());
        lobby.setLobbyName("BadBunny");
        lobby.setPrivate(false);
        lobby.setMaxPlayersAndBots(7);
        lobby.setVoiceChat(false);
        lobby.setCurrentNumPlayersAndBots(1);

        lobbyRepository.save(lobby);
        lobbyRepository.flush();

        Optional<Lobby> foundLobby = lobbyRepository.findByHostId(host.getId());
        assertTrue(foundLobby.isPresent());
        Lobby actualLobby = foundLobby.get();
        actualLobby.replacePlayersInLobby(null);
        lobbyRepository.delete(lobby);
        lobbyRepository.flush();

        foundLobby = lobbyRepository.findByHostId(host.getId());
        assertFalse(foundLobby.isPresent());
    }
}
