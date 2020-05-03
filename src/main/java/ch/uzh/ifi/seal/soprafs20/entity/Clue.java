package ch.uzh.ifi.seal.soprafs20.entity;

import java.util.Objects;

public class Clue {

    private Long playerId;

    private String actualClue;

    private Long timeNeeded;

    public Long getPlayerId() {
        return playerId;
    }

    public void setPlayerId(Long playerId) {
        this.playerId = playerId;
    }

    public String getActualClue() {
        return actualClue;
    }

    public void setActualClue(String actualClue) {
        this.actualClue = actualClue.toLowerCase();
    }

    public Long getTimeNeeded() { return timeNeeded; }

    public void setTimeNeeded(Long timeNeeded) { this.timeNeeded = timeNeeded; }

    @Override
    public boolean equals(Object o) {
        if (this == o) { return true; }
        if (!(o instanceof Clue)) { return false; }
        Clue other = (Clue) o;
        return playerId != null && playerId.equals(other.getPlayerId()) && actualClue.equals(other.getActualClue());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getPlayerId());
    }
}
