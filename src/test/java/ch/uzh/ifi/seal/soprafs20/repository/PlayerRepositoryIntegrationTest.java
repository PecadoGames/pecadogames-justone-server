package ch.uzh.ifi.seal.soprafs20.repository;

import ch.uzh.ifi.seal.soprafs20.constant.AvatarColor;
import ch.uzh.ifi.seal.soprafs20.entity.Player;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class PlayerRepositoryIntegrationTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private PlayerRepository playerRepository;

    @Test
    void findById_success() {
        Player playa = new Player();
        playa.setId(1L);
        playa.setUsername("Bad Bunny");
        playa.setToken("playaToken");
        playa.setAvatarColor(AvatarColor.BLUE);

        entityManager.persist(playa);
        entityManager.flush();

        Optional<Player> foundPlayer = playerRepository.findById(playa.getId());
        assertTrue(foundPlayer.isPresent());

        Player actualPlayer = foundPlayer.get();
        assertEquals(playa.getId(), actualPlayer.getId());
        assertEquals(playa.getUsername(), actualPlayer.getUsername());
        assertEquals(playa.getToken(), actualPlayer.getToken());
        assertEquals(playa.getAvatarColor(), actualPlayer.getAvatarColor());
    }

    @Test
    void findById_unsuccessful() {
        Player playa = new Player();
        playa.setId(1L);
        playa.setUsername("Bad Bunny");
        playa.setToken("playaToken");
        playa.setAvatarColor(AvatarColor.BLUE);

        entityManager.persist(playa);
        entityManager.flush();

        Optional<Player> foundPlayer = playerRepository.findById(100L);
        assertTrue(foundPlayer.isEmpty());
    }
}
