package ch.uzh.ifi.seal.soprafs20.entity;


import javax.persistence.*;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "LOBBY")
public class Lobby implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue
    private Long lobbyId;

    @Column(nullable = false)
    private String lobbyName;

    @Column(nullable = false)
    private Integer numberOfPlayers;

    @Column(nullable = false)
    private boolean voiceChat;

    @Column(nullable = false)
    private Long userId; //user id of lobby creator

    @Column(nullable = false)
    private String userToken;

    @Column
    private Integer numberOfBots;

    @Column
    private Long lobbyScore;

    @Column(nullable = false)
    private boolean isPrivate;

    @Column
    private String privateKey;

    @Column(nullable = false)
    private int totalNumPlayers;

    @OneToMany
    private Set<User> usersInLobby = new HashSet<>();

    public Long getLobbyId() {
        return lobbyId;
    }

    public void setId(Long id) {
        this.lobbyId = id;
    }

    public String getLobbyName() {
        return lobbyName;
    }

    public void setLobbyName(String lobbyName) {
        this.lobbyName = lobbyName;
    }

    public Integer getNumberOfPlayers() {
        return numberOfPlayers;
    }

    public void setNumberOfPlayers(Integer numberOfPlayers) {
        this.numberOfPlayers = numberOfPlayers;
    }

    public boolean isVoiceChat() {
        return voiceChat;
    }

    public void setVoiceChat(boolean voiceChat) {
        this.voiceChat = voiceChat;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public String getToken() {
        return userToken;
    }

    public void setToken(String token) {
        this.userToken = token;
    }

    public String getUserToken() {
        return userToken;
    }

    public void setUserToken(String userToken) {
        this.userToken = userToken;
    }

    public Integer getNumberOfBots() {
        return numberOfBots;
    }

    public void setNumberOfBots(Integer numberOfBots) {
        this.numberOfBots = numberOfBots;
    }

    public Long getLobbyScore() {
        return lobbyScore;
    }

    public void setLobbyScore(Long lobbyScore) {
        this.lobbyScore = lobbyScore;
    }

    public void setLobbyId(Long lobbyId) {
        this.lobbyId = lobbyId;
    }

    public boolean isPrivate() {
        return isPrivate;
    }

    public void setPrivate(boolean aPrivate) {
        isPrivate = aPrivate;
    }

    public String getPrivateKey() {
        return privateKey;
    }

    public void setPrivateKey(String privateKey) {
        this.privateKey = privateKey;
    }

    public Set<User> getUsersInLobby() { return usersInLobby; }

    public void setUsersInLobby(User newUser) { usersInLobby.add(newUser); }

    public void replaceUsersInLobby(Set<User> users){usersInLobby = users;}

    public int getTotalNumPlayersAndBots() {
        return totalNumPlayers;
    }

    public void setTotalNumPlayersAndBots(int totalNumPlayers) {
        this.totalNumPlayers = totalNumPlayers;
    }
}
