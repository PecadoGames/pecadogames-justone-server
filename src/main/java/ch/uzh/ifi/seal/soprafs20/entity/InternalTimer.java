package ch.uzh.ifi.seal.soprafs20.entity;

import javax.persistence.*;
import java.util.Objects;
import java.util.Timer;

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

    public  void setCancel(boolean cancel) {
        isCancel = cancel;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) { return true; }
        if (!(o instanceof InternalTimer)) { return false; }
        InternalTimer other = (InternalTimer) o;
        return lobbyId != null && lobbyId.equals(other.getLobbyId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getLobbyId());
    }

}
