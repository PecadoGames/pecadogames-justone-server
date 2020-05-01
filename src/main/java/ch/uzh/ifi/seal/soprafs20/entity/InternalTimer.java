package ch.uzh.ifi.seal.soprafs20.entity;

import org.hibernate.annotations.LazyToOne;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import javax.persistence.*;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

@Entity
@Table(name = "InternalTimer")
public class InternalTimer extends Timer  {

    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Long lobbyId;

    private long time;

    @Column
    private volatile boolean isRunning;


    @Column
    private volatile boolean isCancel;

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



    public boolean isRunning() {
        return isRunning;
    }

    public void setRunning(boolean running) {
        isRunning = running;
    }

    public boolean isCancel() {
        return isCancel;
    }

    public void setCancel(boolean cancel) {
        isCancel = cancel;
    }

}
