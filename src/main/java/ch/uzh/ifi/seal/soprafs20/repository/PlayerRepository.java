package ch.uzh.ifi.seal.soprafs20.repository;

import ch.uzh.ifi.seal.soprafs20.entity.Player;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository("playerRepository")
public interface PlayerRepository extends JpaRepository<Player, Long> {
    Optional<Player> findById(Long id);
}
