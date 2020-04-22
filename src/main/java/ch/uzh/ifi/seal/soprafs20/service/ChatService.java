package ch.uzh.ifi.seal.soprafs20.service;

import ch.uzh.ifi.seal.soprafs20.entity.Chat;
import ch.uzh.ifi.seal.soprafs20.entity.Lobby;
import ch.uzh.ifi.seal.soprafs20.entity.Message;
import ch.uzh.ifi.seal.soprafs20.entity.User;
import ch.uzh.ifi.seal.soprafs20.exceptions.NotFoundException;
import ch.uzh.ifi.seal.soprafs20.exceptions.UnauthorizedException;
import ch.uzh.ifi.seal.soprafs20.repository.ChatRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Transactional
public class ChatService {
    private final Logger log = LoggerFactory.getLogger(UserService.class);

    private final ChatRepository chatRepository;

    @Autowired
    public ChatService(ChatRepository chatRepository) {
        this.chatRepository = chatRepository;
    }

    public Chat getChat(Long lobbyId) {
        Optional<Chat> optionalChat = chatRepository.findById(lobbyId);
        if(optionalChat.isPresent()){
            return optionalChat.get();
        } else {
            throw new NotFoundException("Could not find chat!");
        }
    }

    public void createChat(Long lobbyId) {
        Chat chat = new Chat();
        chat.setLobbyId(lobbyId);
        chat = chatRepository.save(chat);
        chatRepository.flush();
    }

    public void addChatMessage(Lobby lobby, String token, Message message) {
        // Long id = chatPutDTO.getUserId();
        for (User user : lobby.getUsersInLobby()) {
            if (user.getToken().equals(token)) {
                message.setAuthorUsername(user.getUsername());
                Chat chat = getChat(lobby.getLobbyId());
                chat.setMessages(message);
                return;
            }
        }
        throw new UnauthorizedException("You are not allowed to send this message.");
    }
}
