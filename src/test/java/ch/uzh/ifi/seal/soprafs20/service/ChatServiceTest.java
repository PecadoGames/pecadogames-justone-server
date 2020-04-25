package ch.uzh.ifi.seal.soprafs20.service;

import ch.uzh.ifi.seal.soprafs20.entity.*;
import ch.uzh.ifi.seal.soprafs20.exceptions.UnauthorizedException;
import ch.uzh.ifi.seal.soprafs20.repository.ChatRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ChatServiceTest {

    @Mock
    private ChatRepository chatRepository;

    @InjectMocks
    private ChatService chatService;

    private Chat testChat;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.initMocks(this);

        testChat = new Chat();
        testChat.setLobbyId(1L);

        Mockito.when(chatRepository.save(Mockito.any())).thenReturn(testChat);
        Mockito.when(chatRepository.findById(Mockito.any())).thenReturn(java.util.Optional.ofNullable(testChat));
    }

    @Test
    public void createChat_success() {
        chatService.createChat(1L);

        Mockito.verify(chatRepository,Mockito.times(1)).save(Mockito.any());
    }

    @Test
    public void addMessage_validInput_success() {
        Message message = new Message();
        message.setText("Hello world");

        Player testPlayer = new Player();
        testPlayer.setToken("testToken");
        testPlayer.setUsername("testUsername");

        Lobby lobby = new Lobby();
        lobby.setToken("testToken");
        lobby.addPlayerToLobby(testPlayer);

        Mockito.when(chatRepository.findById(Mockito.any())).thenReturn(java.util.Optional.ofNullable(testChat));

        chatService.addChatMessage(lobby, lobby.getToken(), message);
        assertTrue(testChat.getMessages().contains(message));
    }

    @Test
    public void addMessage_invalidToken_throwsException() {
        Message message = new Message();
        message.setText("Hello world");

        Player testPlayer = new Player();
        testPlayer.setToken("testToken");
        testPlayer.setUsername("testUsername");

        Lobby lobby = new Lobby();
        lobby.setToken("wrongToken");
        lobby.addPlayerToLobby(testPlayer);

        Mockito.when(chatRepository.findById(Mockito.any())).thenReturn(java.util.Optional.ofNullable(testChat));

        assertThrows(UnauthorizedException.class, () -> chatService.addChatMessage(lobby, lobby.getToken(), message));
    }
}
