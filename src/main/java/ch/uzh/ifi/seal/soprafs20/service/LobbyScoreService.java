package ch.uzh.ifi.seal.soprafs20.service;

import ch.uzh.ifi.seal.soprafs20.entity.LobbyScore;
import ch.uzh.ifi.seal.soprafs20.repository.LobbyScoreRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class LobbyScoreService {
    private LobbyScoreRepository lobbyScoreRepository;

    @Autowired
    public LobbyScoreService(LobbyScoreRepository lobbyScoreRepository) {
        this.lobbyScoreRepository = lobbyScoreRepository;
    }

    public List<LobbyScore> getLobbyScoresByScore() {
        return this.lobbyScoreRepository.findAllByOrderByScoreDesc();
    }

    public List<LobbyScore> getLobbyScoresByDate() {
        return this.lobbyScoreRepository.findAllByOrderByDate();
    }

}
