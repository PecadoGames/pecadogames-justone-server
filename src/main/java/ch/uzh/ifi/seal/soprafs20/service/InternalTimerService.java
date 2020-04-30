package ch.uzh.ifi.seal.soprafs20.service;

import ch.uzh.ifi.seal.soprafs20.GameLogic.gameStates.GameState;
import ch.uzh.ifi.seal.soprafs20.entity.Game;
import ch.uzh.ifi.seal.soprafs20.entity.InternalTimer;
import ch.uzh.ifi.seal.soprafs20.repository.InternalTimerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

@Service
@Transactional
public class InternalTimerService extends Thread {

    private final InternalTimerRepository internalTimerRepository;

    @Autowired
    public InternalTimerService(InternalTimerRepository internalTimerRepository){
        this.internalTimerRepository = internalTimerRepository;
    }

    public void stopThread(Game game){
        game.getTimer().cancel();
    }


    public void delete(Game game){
        internalTimerRepository.delete(game.getTimer());
    }

    public void store(InternalTimer internalTimer){
        internalTimerRepository.saveAndFlush(internalTimer);
    }


    public long getTime(InternalTimer internalTimer){
        return internalTimer.getTime();
    }

    /**
     * helper function, sets gameState in game object
     * @param game
     * @param gameState
     * @return
     */
    public Game setState(Game game,GameState gameState){
        game.setGameState(gameState);
        return game;
    }

}
