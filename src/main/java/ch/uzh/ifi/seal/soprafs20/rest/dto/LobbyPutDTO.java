package ch.uzh.ifi.seal.soprafs20.rest.dto;

import java.util.ArrayList;

public class LobbyPutDTO {
    private Integer numberOfPlayers;
    private ArrayList<Long> usersToKick;
    private Integer numberOfBots;
    private String token;


    public Integer getNumberOfPlayers() {
        return numberOfPlayers;
    }

    public void setNumberOfPlayers(Integer numberOfPlayers) {
        this.numberOfPlayers = numberOfPlayers;
    }


    public ArrayList<Long> getUsersToKick() {
        return usersToKick;
    }

    public void setUsersToKick(ArrayList<Long> usersToKick) {
        this.usersToKick = usersToKick;
    }

    public Integer getNumberOfBots() {
        return numberOfBots;
    }

    public void setNumberOfBots(Integer numberOfBots) {
        this.numberOfBots = numberOfBots;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }


}
