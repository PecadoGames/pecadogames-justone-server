package ch.uzh.ifi.seal.soprafs20.repository;

import ch.uzh.ifi.seal.soprafs20.entity.Lobby;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import javax.persistence.Tuple;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public interface LobbyRepository extends JpaRepository<Lobby, Long> {
    Optional<Lobby> findById(long lobbyId);
    Optional<Lobby> findByUserId(long userId);

    @Query(value = "SELECT lobbyName, lobbyScore FROM Lobby")
    List<Lobby> getLobbiesByLobbyScore();
}
