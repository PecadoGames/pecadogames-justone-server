package ch.uzh.ifi.seal.soprafs20.repository;

import ch.uzh.ifi.seal.soprafs20.entity.Message;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository("messageRepository")
public interface MessageRepository extends JpaRepository<Message, Long> {
    Optional<Message> findByMessageId(long messageId);
}
