package ch.uzh.ifi.seal.soprafs20.repository;

import ch.uzh.ifi.seal.soprafs20.entity.Lobby;
import ch.uzh.ifi.seal.soprafs20.entity.Player;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
public class LobbyRepositoryIntegrationTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private LobbyRepository lobbyRepository;

    @Test
    public void findByLobbyId_success() {
        Player player = new Player();
        player.setId(1L);
        player.setToken("hostToken");
        player.setUsername("Bad Bunny");

        entityManager.persist(player);
        entityManager.flush();

        Lobby lobby = new Lobby();
        lobby.setLobbyName("Ballerz");
        lobby.setHostToken("hostToken");
        lobby.setHostId(1L);
        lobby.addPlayerToLobby(player);
        lobby.setVoiceChat(false);
        lobby.setPrivate(true);
        lobby.setCurrentNumPlayersAndBots(1);
        lobby.setMaxPlayersAndBots(7);

        entityManager.persist(lobby);
        entityManager.flush();

        Optional<Lobby> foundLobby = lobbyRepository.findByLobbyId(lobby.getLobbyId());
        assertTrue(foundLobby.isPresent());
        Lobby actualLobby = foundLobby.get();


    }

}
