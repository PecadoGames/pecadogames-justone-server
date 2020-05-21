package ch.uzh.ifi.seal.soprafs20.rest.dto;

import ch.uzh.ifi.seal.soprafs20.entity.Message;
import ch.uzh.ifi.seal.soprafs20.rest.mapper.DTOMapper;

import java.util.ArrayList;
import java.util.List;

public class ChatGetDTO {

    private Long lobbyId;

    List<MessageGetDTO> messages = new ArrayList<>();

    public Long getLobbyId() {
        return lobbyId;
    }

    public void setLobbyId(Long lobbyId) {
        this.lobbyId = lobbyId;
    }

    public List<MessageGetDTO> getMessages() {
        return messages;
    }

    public void setMessages(List<Message> messages) {
        for(Message m : messages) {
            MessageGetDTO messageGetDTO = DTOMapper.INSTANCE.convertEntityToMessageGetDTO(m);
            this.messages.add(messageGetDTO);
        }
    }
}
