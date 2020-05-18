package ch.uzh.ifi.seal.soprafs20.service;

import ch.uzh.ifi.seal.soprafs20.entity.Lobby;
import ch.uzh.ifi.seal.soprafs20.entity.LobbyScore;
import ch.uzh.ifi.seal.soprafs20.repository.LobbyScoreRepository;
import ch.uzh.ifi.seal.soprafs20.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class LobbyScoreService {

    private final Logger log = LoggerFactory.getLogger(UserService.class);
    private LobbyScoreRepository lobbyScoreRepository;

    @Autowired
    public LobbyScoreService(LobbyScoreRepository lobbyScoreRepository) {
        this.lobbyScoreRepository = lobbyScoreRepository;
    }

    public List<LobbyScore> getLobbyScoresByScore() {
        return this.lobbyScoreRepository.findAllByOrderByScore();
    }

    public List<LobbyScore> getLobbyScoresByDate() {
        return this.lobbyScoreRepository.findAllByOrderByDate();
    }

}
