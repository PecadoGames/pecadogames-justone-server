package ch.uzh.ifi.seal.soprafs20.rest.dto;

import java.util.ArrayList;

public class LobbyPutDTO {

    private Integer maxNumberOfPlayersAndBots;
    private ArrayList<Long> playersToKick;
    private String hostToken;

    public Integer getMaxNumberOfPlayersAndBots() {
        return maxNumberOfPlayersAndBots;
    }

    public void setMaxNumberOfPlayersAndBots(Integer maxNumberOfPlayersAndBots) {
        this.maxNumberOfPlayersAndBots = maxNumberOfPlayersAndBots;
    }

    public ArrayList<Long> getPlayersToKick() {
        return playersToKick;
    }

    public void setPlayersToKick(ArrayList<Long> playersToKick) {
        this.playersToKick = playersToKick;
    }

    public String getHostToken() {
        return hostToken;
    }

    public void setHostToken(String hostToken) {
        this.hostToken = hostToken;
    }


}
