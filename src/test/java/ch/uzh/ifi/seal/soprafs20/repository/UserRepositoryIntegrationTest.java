package ch.uzh.ifi.seal.soprafs20.repository;

import ch.uzh.ifi.seal.soprafs20.constant.UserStatus;
import ch.uzh.ifi.seal.soprafs20.entity.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
public class UserRepositoryIntegrationTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private UserRepository userRepository;

    @Test
    public void findByName_success() {
        // given
        User user = new User();

        user.setUsername("firstname@lastname");
        user.setStatus(UserStatus.OFFLINE);
        user.setPassword("test");
        user.setToken("1");
        user.setCreationDate();

        entityManager.persist(user);
        entityManager.flush();

        // when
        User found = userRepository.findByUsername(user.getUsername());

        // then
        assertNotNull(found.getId());
        assertEquals(found.getPassword(), user.getPassword());
        assertEquals(found.getUsername(), user.getUsername());
        assertEquals(found.getToken(), user.getToken());
        assertEquals(found.getStatus(), user.getStatus());
        assertNotNull(found.getCreationDate());
    }

    @Test
    public void findByUsername_success() {
        // given
        User user = new User();
        user.setUsername("firstname@lastname");
        user.setStatus(UserStatus.OFFLINE);
        user.setPassword("test");
        user.setToken("1");
        user.setCreationDate();

        entityManager.persist(user);
        entityManager.flush();

        // when
        User found = userRepository.findByUsername(user.getUsername());

        // then
        assertNotNull(found.getId());
        assertEquals(found.getPassword(), user.getPassword());
        assertEquals(found.getUsername(), user.getUsername());
        assertEquals(found.getToken(), user.getToken());
        assertEquals(found.getStatus(), user.getStatus());
        assertNotNull(found.getCreationDate());
    }

    @Test
    public void findById_success() {
        // given
        User user = new User();
        user.setUsername("firstname@lastname");
        user.setStatus(UserStatus.OFFLINE);
        user.setPassword("test");
        user.setToken("1");
        user.setCreationDate();

        entityManager.persist(user);
        entityManager.flush();

        // when
        Optional<User> found = userRepository.findById(user.getId());

        // then
        if(found.isPresent()){
            assertNotNull(found.get().getId());
            assertEquals(found.get().getPassword(), user.getPassword());
            assertEquals(found.get().getUsername(), user.getUsername());
            assertEquals(found.get().getToken(), user.getToken());
            assertEquals(found.get().getStatus(), user.getStatus());
            assertNotNull(found.get().getCreationDate());
        }
    }

    @Test
    public void findByToken_success() {
        // given
        User user = new User();
        user.setUsername("firstname@lastname");
        user.setStatus(UserStatus.OFFLINE);
        user.setPassword("test");
        user.setToken("1");
        user.setCreationDate();

        entityManager.persist(user);
        entityManager.flush();

        // when
        User found = userRepository.findByToken(user.getToken());

        // then
        assertNotNull(found.getId());
        assertEquals(found.getPassword(), user.getPassword());
        assertEquals(found.getUsername(), user.getUsername());
        assertEquals(found.getToken(), user.getToken());
        assertEquals(found.getStatus(), user.getStatus());
        assertNotNull(found.getCreationDate());
    }

    @Test
    public void findByToken_unsuccessful() {
        // given
        User user = new User();
        user.setUsername("firstname@lastname");
        user.setStatus(UserStatus.OFFLINE);
        user.setPassword("test");
        user.setToken("1");
        user.setCreationDate();

        entityManager.persist(user);
        entityManager.flush();

        // when
        User found = userRepository.findByToken("unsuccessful");

        // then
        assertNull(found);
    }

    @Test
    public void findByUsername_unsuccessful() {
        // given
        User user = new User();
        user.setUsername("firstname@lastname");
        user.setStatus(UserStatus.OFFLINE);
        user.setPassword("test");
        user.setToken("1");
        user.setCreationDate();

        entityManager.persist(user);
        entityManager.flush();

        // when
        User found = userRepository.findByUsername("unsuccessful");

        // then
        assertNull(found);
    }

    @Test
    public void findByName_unsuccessful() {
        // given
        User user = new User();
        user.setUsername("firstname@lastname");
        user.setStatus(UserStatus.OFFLINE);
        user.setPassword("test");
        user.setToken("1");
        user.setCreationDate();

        entityManager.persist(user);
        entityManager.flush();

        // when
        User found = userRepository.findByUsername("unsuccessful");

        // then
        assertNull(found);
    }

    @Test
    public void findById_unsuccessful() {
        // given
        User user = new User();
        user.setUsername("firstname@lastname");
        user.setStatus(UserStatus.OFFLINE);
        user.setPassword("test");
        user.setToken("1");
        user.setCreationDate();

        entityManager.persist(user);
        entityManager.flush();

        // when
        Optional<User> found = userRepository.findById(100L);

        // then
        assertFalse(found.isPresent());
    }
}
