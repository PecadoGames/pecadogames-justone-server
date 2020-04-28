package ch.uzh.ifi.seal.soprafs20.entity;

import org.hibernate.annotations.LazyToOne;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import javax.persistence.*;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

@Entity
@Table(name = "InternalTimer")
public class InternalTimer extends Timer  {

    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Long lobbyId;

    @Column
    private String timeInSeconds;

    @OneToOne(cascade = {CascadeType.ALL})
    private Game game;

    private long time;

    private boolean isRunning;

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }


    public Long getLobbyId() {
        return lobbyId;
    }

    public void setLobbyId(Long lobbyId) {
        this.lobbyId = lobbyId;
    }

    public String getTimeInSeconds() {
        return timeInSeconds;
    }

    public void setTimeInSeconds(String timeInSeconds) {
        this.timeInSeconds = timeInSeconds;
    }

    public Game getGame() {
        return game;
    }

    public void setGame(Game game) {
        this.game = game;
    }

    public boolean isRunning() {
        return isRunning;
    }

    public void setRunning(boolean running) {
        isRunning = running;
    }
}
