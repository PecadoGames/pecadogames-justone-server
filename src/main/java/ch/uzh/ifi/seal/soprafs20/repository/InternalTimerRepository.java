package ch.uzh.ifi.seal.soprafs20.repository;

import ch.uzh.ifi.seal.soprafs20.entity.InternalTimer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository("internalTimerRepository")
public interface InternalTimerRepository extends JpaRepository<InternalTimer, Long> {

}
