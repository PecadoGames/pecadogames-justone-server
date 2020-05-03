package ch.uzh.ifi.seal.soprafs20.rest.dto;

public class JoinLeavePutDTO {

    private long playerId;
    private String playerToken;


    public long getPlayerId() {
        return playerId;
    }

    public void setPlayerId(long playerId) {
        this.playerId = playerId;
    }

    public String getPlayerToken() {
        return playerToken;
    }

    public void setPlayerToken(String playerToken) {
        this.playerToken = playerToken;
    }
}
