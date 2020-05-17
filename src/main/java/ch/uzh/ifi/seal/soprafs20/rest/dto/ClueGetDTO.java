package ch.uzh.ifi.seal.soprafs20.rest.dto;

public class ClueGetDTO {
    String actualClue;

    Long playerId;

    public String getActualClue() { return actualClue; }

    public void setActualClue(String actualClue) {
        this.actualClue = actualClue;
    }

    public Long getPlayerId() { return playerId; }

    public void setPlayerId(Long id) { playerId = id; }
}
