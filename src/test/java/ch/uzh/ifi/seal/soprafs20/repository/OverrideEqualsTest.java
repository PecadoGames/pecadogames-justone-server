package ch.uzh.ifi.seal.soprafs20.repository;

import ch.uzh.ifi.seal.soprafs20.entity.Chat;
import ch.uzh.ifi.seal.soprafs20.entity.Message;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class OverrideEqualsTest {

    @Test
    public void chatEquals_success() {
        Message message1 = new Message();
        message1.setText("Hello world");
        message1.setCreationDate();

        Message message2 = new Message();
        message2.setText("Goodbye world");
        message2.setCreationDate();

        Chat chat = new Chat();
        chat.setLobbyId(1L);
        chat.setMessages(message1);

        Chat chat2 = new Chat();
        chat2.setLobbyId(1L);
        chat2.setMessages(message2);

        assertEquals(chat, chat2);
        assertEquals(chat, chat2);
    }

    @Test
    void chatNotEquals_success() {
        Message message1 = new Message();
        message1.setText("Hello world");
        message1.setCreationDate();

        Message message2 = new Message();
        message2.setText("Goodbye world");
        message2.setCreationDate();

        Chat chat = new Chat();
        chat.setLobbyId(1L);
        chat.setMessages(message1);

        Chat chat2 = new Chat();
        chat2.setLobbyId(2L);
        chat2.setMessages(message1);

        assertNotEquals(chat, chat2);
    }
}
