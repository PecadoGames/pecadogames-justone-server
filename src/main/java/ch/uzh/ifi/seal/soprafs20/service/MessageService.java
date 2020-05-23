package ch.uzh.ifi.seal.soprafs20.service;

import ch.uzh.ifi.seal.soprafs20.entity.Message;
import ch.uzh.ifi.seal.soprafs20.repository.MessageRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class MessageService {

    private final Logger log = LoggerFactory.getLogger(MessageService.class);

    private final MessageRepository messageRepository;

    @Autowired
    public MessageService(MessageRepository messageRepository) {
        this.messageRepository = messageRepository;
    }

    public Message createMessage(Message newMessage) {
        newMessage.setCreationDate();
        newMessage = messageRepository.saveAndFlush(newMessage);

        return newMessage;
    }
}
