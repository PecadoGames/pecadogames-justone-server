package ch.uzh.ifi.seal.soprafs20.rest.dto;

import java.util.ArrayList;

public class LobbyPutDTO {

    private Integer maxNumberOfPlayersAndBots;
    private Long playerToKickId;
    private String hostToken;

    public Integer getMaxNumberOfPlayersAndBots() {
        return maxNumberOfPlayersAndBots;
    }

    public void setMaxNumberOfPlayersAndBots(Integer maxNumberOfPlayersAndBots) {
        this.maxNumberOfPlayersAndBots = maxNumberOfPlayersAndBots;
    }

    public Long getPlayerToKickId() {
        return playerToKickId;
    }

    public void setPlayerToKickId(Long playersToKickId) {
        this.playerToKickId = playersToKickId;
    }

    public String getHostToken() {
        return hostToken;
    }

    public void setHostToken(String hostToken) {
        this.hostToken = hostToken;
    }


}
