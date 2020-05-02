package ch.uzh.ifi.seal.soprafs20.entity;


import com.fasterxml.jackson.annotation.JsonIgnore;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import javax.persistence.*;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name="LOBBY")
public class Lobby implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    private Long lobbyId;

    @Column(nullable = false)
    private String lobbyName;

    @Column
    private boolean gameIsStarted;

    @Column(nullable = false)
    private boolean voiceChat;

    //user id of lobby creator
    @Column(nullable = false)
    private Long hostId;

    @Column(nullable = false)
    @JsonIgnore
    private String hostToken;

    @Column
    private Long lobbyScore;

    @Column(nullable = false)
    private boolean isPrivate;

    @Column
    @JsonIgnore
    private String privateKey;

    //current number of player(and bots) in lobby
    @Column(nullable = false)
    private Integer currentNumPlayersAndBots;

    //limit of players + bots in lobby
    @Column(nullable = false)
    private Integer maxPlayersAndBots;

    @OneToMany(fetch = FetchType.EAGER)
    @Fetch(value = FetchMode.SUBSELECT)
    private Set<Player> playersInLobby = new HashSet<>();

    public Long getLobbyId() {
        return lobbyId;
    }

    public void setLobbyId(Long id) { this.lobbyId = id; }

    public String getLobbyName() {
        return lobbyName;
    }

    public void setLobbyName(String lobbyName) {
        this.lobbyName = lobbyName;
    }

    /*public Integer getNumberOfPlayers() {
        return numberOfPlayers;
    }

    public void setNumberOfPlayers(Integer numberOfPlayers) {
        this.numberOfPlayers = numberOfPlayers;
    }*/

    public boolean isVoiceChat() {
        return voiceChat;
    }

    public void setVoiceChat(boolean voiceChat) {
        this.voiceChat = voiceChat;
    }

    public Long getHostId() {
        return hostId;
    }

    public void setHostId(long userId) {
        this.hostId = userId;
    }

    public String getHostToken() {
        return hostToken;
    }

    public void setHostToken(String token) {
        this.hostToken = token;
    }

    /*public Integer getNumberOfBots() {
        return numberOfBots;
    }

    public void setNumberOfBots(Integer numberOfBots) {
        this.numberOfBots = numberOfBots;
    }*/

    public Long getLobbyScore() {
        return lobbyScore;
    }

    public void setLobbyScore(Long lobbyScore) {
        this.lobbyScore = lobbyScore;
    }

    public boolean isPrivate() {
        return isPrivate;
    }

    public void setPrivate(boolean isPrivate) {
        this.isPrivate = isPrivate;
    }

    public String getPrivateKey() {
        return privateKey;
    }

    public void setPrivateKey(String privateKey) {
        this.privateKey = privateKey;
    }

    public Set<Player> getPlayersInLobby() { return playersInLobby; }

    public void addPlayerToLobby(Player newPlayer) { playersInLobby.add(newPlayer); }

    public void replacePlayersInLobby(Set<Player> players){playersInLobby = players;}

    public Integer getCurrentNumPlayersAndBots() {
        return currentNumPlayersAndBots;
    }

    public void setCurrentNumPlayersAndBots(Integer currentNumPlayersAndBots) {
        this.currentNumPlayersAndBots = currentNumPlayersAndBots;
    }

    public Integer getMaxPlayersAndBots() {
        return maxPlayersAndBots;
    }

    public void setMaxPlayersAndBots(Integer maxPlayersAndBots) {
        this.maxPlayersAndBots = maxPlayersAndBots;
    }

    public boolean isGameStarted() {
        return gameIsStarted;
    }

    public void setGameIsStarted(boolean gameIsStarted) {
        this.gameIsStarted = gameIsStarted;
    }
}
