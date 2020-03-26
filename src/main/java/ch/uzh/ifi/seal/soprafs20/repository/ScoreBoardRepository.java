package ch.uzh.ifi.seal.soprafs20.repository;

import ch.uzh.ifi.seal.soprafs20.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository("scoreBoardRepository")
public interface ScoreBoardRepository extends JpaRepository<User, Long> {
    Optional<User> findByTeamName(String teamName);
    User findByUsername(String username);
    Optional<User> findByScoreGreaterThanEqual(int score);
}
