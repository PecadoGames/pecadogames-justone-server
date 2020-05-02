package ch.uzh.ifi.seal.soprafs20.repository;

import ch.uzh.ifi.seal.soprafs20.entity.Chat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository("chatRepository")
public interface ChatRepository extends JpaRepository<Chat, Long> {
    Optional<Chat> findByLobbyId(long lobbyId);
}
