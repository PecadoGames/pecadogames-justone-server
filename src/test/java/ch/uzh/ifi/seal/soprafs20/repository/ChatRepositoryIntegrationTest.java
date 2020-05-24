package ch.uzh.ifi.seal.soprafs20.repository;

import ch.uzh.ifi.seal.soprafs20.entity.Chat;
import ch.uzh.ifi.seal.soprafs20.entity.Message;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class ChatRepositoryIntegrationTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private ChatRepository chatRepository;

    @Test
    void findById_success() {
        Message message = new Message();
        message.setAuthorId(2L);
        message.setAuthorUsername("Mark Twain");
        message.setText("Hello world");
        message.setCreationDate();

        entityManager.persist(message);
        entityManager.flush();

        Chat chat = new Chat();
        chat.setLobbyId(1L);
        chat.setMessages(message);

        entityManager.persist(chat);
        entityManager.flush();

        Optional<Chat> foundChat = chatRepository.findByLobbyId(chat.getLobbyId());

        assertTrue(foundChat.isPresent());
        Chat actualChat = foundChat.get();
        assertEquals(actualChat.getLobbyId(), chat.getLobbyId());
        assertTrue(actualChat.getMessages().contains(message));
    }

    @Test
    void findById_unsuccessful() {
        Message message = new Message();
        message.setAuthorId(2L);
        message.setAuthorUsername("Mark Twain");
        message.setText("Hello world");
        message.setCreationDate();

        entityManager.persist(message);
        entityManager.flush();

        Chat chat = new Chat();
        chat.setLobbyId(1L);
        chat.setMessages(message);

        entityManager.persist(chat);
        entityManager.flush();

        Optional<Chat> foundChat = chatRepository.findByLobbyId(100L);

        assertTrue(foundChat.isEmpty());
    }

}
