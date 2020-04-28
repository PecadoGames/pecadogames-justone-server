package ch.uzh.ifi.seal.soprafs20.repository;

import ch.uzh.ifi.seal.soprafs20.entity.InternalTimer;
import ch.uzh.ifi.seal.soprafs20.entity.Lobby;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InternalTimerRepository extends JpaRepository<InternalTimer, Long> {

}
