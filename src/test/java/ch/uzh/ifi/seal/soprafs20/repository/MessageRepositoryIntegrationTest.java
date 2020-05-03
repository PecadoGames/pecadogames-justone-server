package ch.uzh.ifi.seal.soprafs20.repository;

import ch.uzh.ifi.seal.soprafs20.entity.Message;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
public class MessageRepositoryIntegrationTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private MessageRepository messageRepository;

    @Test
    public void findByMessageId_success() {
        Message message = new Message();
        message.setAuthorId(1L);
        message.setAuthorUsername("Shakespeare");
        message.setText("To be or not to be");
        message.setCreationDate();

        entityManager.persist(message);
        entityManager.flush();

        Optional<Message> foundMessage = messageRepository.findByMessageId(message.getMessageId());
        assertTrue(foundMessage.isPresent());

        Message actualMessage = foundMessage.get();
        assertEquals(message.getMessageId(), actualMessage.getMessageId());
        assertEquals(message.getAuthorId(), actualMessage.getAuthorId());
        assertEquals(message.getText(), actualMessage.getText());
        assertEquals(message.getCreationDate(), actualMessage.getCreationDate());
    }

    @Test
    public void findByMessageId_unsuccessful() {
        Message message = new Message();
        message.setAuthorId(1L);
        message.setAuthorUsername("Shakespeare");
        message.setText("To be or not to be");
        message.setCreationDate();

        entityManager.persist(message);
        entityManager.flush();

        Optional<Message> foundMessage = messageRepository.findByMessageId(100L);
        assertTrue(foundMessage.isEmpty());
    }
}

