package ch.uzh.ifi.seal.soprafs20.entity;


import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.springframework.lang.Nullable;

import javax.persistence.*;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Entity
@Table(name="LOBBY")
public class Lobby implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long lobbyId;

    @Column(nullable = false)
    private String lobbyName;

    @Column
    private boolean gameIsStarted;

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

    @Column(nullable = false)
    private Integer currentNumPlayers;

    @Column(nullable = false)
    private Integer currentNumBots;

    //limit of players + bots in lobby
    @Column(nullable = false)
    private Integer maxPlayersAndBots;

    @Column
    @ManyToMany
    @JsonBackReference
    private Set<User> invitedUsers = new HashSet<>();

    @OneToMany(fetch = FetchType.EAGER)
    @Fetch(value = FetchMode.SUBSELECT)
    private Set<Player> playersInLobby = new HashSet<>();

    @Column(nullable = false)
    private Integer rounds;

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

    public Integer getCurrentNumPlayers() {
        return currentNumPlayers;
    }

    public void setCurrentNumPlayers(Integer currentNumPlayersAndBots) { this.currentNumPlayers = currentNumPlayersAndBots; }

    public Integer getCurrentNumBots() { return currentNumBots; }

    public void setCurrentNumBots(Integer currentNumBots) { this.currentNumBots = currentNumBots; }

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

    public void addInvitedUser(User receiver) {
        this.invitedUsers.add(receiver);
    }

    public Set<User> getInvitedUsers() {
        return invitedUsers;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) { return true; }
        if (!(o instanceof Lobby)) { return false; }
        Lobby other = (Lobby) o;
        return lobbyId != null && lobbyId.equals(other.getLobbyId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getLobbyId());
    }

    public void setRounds(int rounds) {
        if(rounds > 13){
            this.rounds = 13;
        } else {
            this.rounds = rounds;
        }
    }

    public Integer getRounds(){
        return this.rounds;
    }
}
