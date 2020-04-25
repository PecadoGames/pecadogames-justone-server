package ch.uzh.ifi.seal.soprafs20.rest.dto;

import java.util.ArrayList;

public class LobbyPutDTO {

    private Integer maxNumberOfPlayersAndBots;
    private ArrayList<Long> playersToKick;
    private String token;

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

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }


}
