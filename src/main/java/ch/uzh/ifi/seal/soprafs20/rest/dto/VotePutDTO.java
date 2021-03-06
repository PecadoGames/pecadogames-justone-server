package ch.uzh.ifi.seal.soprafs20.rest.dto;

import java.util.ArrayList;

public class VotePutDTO {


    private Long playerId;
    private String playerToken;
    private ArrayList<String> invalidClues = new ArrayList<>();

    public Long getPlayerId() {
        return playerId;
    }

    public void setPlayerId(Long playerId) {
        this.playerId = playerId;
    }

    public String getPlayerToken() {
        return playerToken;
    }

    public void setPlayerToken(String playerToken) {
        this.playerToken = playerToken;
    }

    public ArrayList<String> getInvalidClues() {
        return invalidClues;
    }

    public void setInvalidClues(ArrayList<String> invalidClues) {
        this.invalidClues = invalidClues;
    }
}
