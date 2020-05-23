package ch.uzh.ifi.seal.soprafs20.service;

import ch.uzh.ifi.seal.soprafs20.entity.Message;
import ch.uzh.ifi.seal.soprafs20.repository.MessageRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.*;

public class MessageServiceTest {

    @Mock
    private MessageRepository messageRepository;

    @InjectMocks
    private MessageService messageService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void createMessage_success() {
        Message message = new Message();
        message.setText("Hello World");
        message.setAuthorUsername("username");
        message.setAuthorId(1L);
        message.setCreationDate();

        Mockito.when(messageRepository.saveAndFlush(Mockito.any())).thenReturn(message);

        message = messageService.createMessage(message);

        assertEquals("Hello World", message.getText());
        assertEquals("username", message.getAuthorUsername());
        assertEquals(1L, message.getAuthorId());
    }
}
