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


    public void createInternalTimer(Game game, int durationSeconds, long startTime, GameState nextState){
        InternalTimer internalTimer = game.getTimer();
        internalTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                internalTimer.setRunning(true);
                internalTimer.setTime(TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis()) - startTime);
                internalTimerRepository.saveAndFlush(internalTimer);
                if(internalTimer.getTime() >= durationSeconds) {
                    game.setGameState(nextState);
                    internalTimer.setRunning(false);
                    internalTimer.cancel();
                }
            }
        },0,1000);
    }

    public long getTime(InternalTimer internalTimer){
        return internalTimer.getTime();
    }

}
