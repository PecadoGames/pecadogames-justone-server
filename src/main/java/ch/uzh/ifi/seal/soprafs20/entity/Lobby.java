package ch.uzh.ifi.seal.soprafs20.entity;


import javax.persistence.*;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name="LOBBY")
public class Lobby implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long lobbyId;

    @Column(nullable = false)
    private String lobbyName;

    //keep track of number of players currently in lobby
    @Column
    private Integer numberOfPlayers;

    @Column(nullable = false)
    private boolean voiceChat;

    //user id of lobby creator
    @Column(nullable = false)
    private Long userId;

    @Column(nullable = false)
    private String userToken;

    //keep track of number of players currently in lobby
    @Column
    private Integer numberOfBots;

    @Column
    private Long lobbyScore;

    @Column(nullable = false)
    private boolean isPrivate;

    @Column
    private String privateKey;

    //current number of player(and bots) in lobby
    @Column(nullable = false)
    private Integer currentNumPlayersAndBots;

    //limit of players + bots in lobby
    @Column(nullable = false)
    private Integer maxPlayersAndBots;

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

    public void addUserToLobby(User newUser) { usersInLobby.add(newUser); }

    public void replaceUsersInLobby(Set<User> users){usersInLobby = users;}

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
}
