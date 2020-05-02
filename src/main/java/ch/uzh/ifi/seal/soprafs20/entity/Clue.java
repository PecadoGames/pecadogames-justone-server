package ch.uzh.ifi.seal.soprafs20.entity;

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
        this.actualClue = actualClue;
    }

    public Long getTimeNeeded() { return timeNeeded; }

    public void setTimeNeeded(Long timeNeeded) { this.timeNeeded = timeNeeded; }
}
