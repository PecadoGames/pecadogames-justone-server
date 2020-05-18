package ch.uzh.ifi.seal.soprafs20.rest.dto;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

public class LobbyScoreGetDTO {
    private String lobbyName;
    private int score;
    private Date date;
    private Set<Long> playersIdInLobby = new HashSet<>();

    public String getLobbyName() {
        return lobbyName;
    }

    public void setLobbyName(String lobbyName) {
        this.lobbyName = lobbyName;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public Set<Long> getPlayersIdInLobby() {
        return playersIdInLobby;
    }

    public void setPlayersIdInLobby(Set<Long> playersIdInLobby) {
        this.playersIdInLobby = playersIdInLobby;
    }
}
