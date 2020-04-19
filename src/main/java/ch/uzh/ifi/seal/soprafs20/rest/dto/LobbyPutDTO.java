package ch.uzh.ifi.seal.soprafs20.rest.dto;

import java.util.ArrayList;

public class LobbyPutDTO {

    private Integer maxNumberOfPlayersAndBots;
    private ArrayList<Long> usersToKick;
    private String token;

    public Integer getMaxNumberOfPlayersAndBots() {
        return maxNumberOfPlayersAndBots;
    }

    public void setMaxNumberOfPlayersAndBots(Integer maxNumberOfPlayersAndBots) {
        this.maxNumberOfPlayersAndBots = maxNumberOfPlayersAndBots;
    }

    public ArrayList<Long> getUsersToKick() {
        return usersToKick;
    }

    public void setUsersToKick(ArrayList<Long> usersToKick) {
        this.usersToKick = usersToKick;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }


}
