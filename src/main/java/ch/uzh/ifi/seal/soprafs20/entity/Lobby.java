package ch.uzh.ifi.seal.soprafs20.entity;


import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import java.io.Serializable;

@Entity
@Table(name = "LOBBY")
public class Lobby implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue
    private Long lobbyId;

    @NotBlank
    @NotEmpty
    @Column(nullable = false)
    String lobbyName;

    @NotBlank
    @NotEmpty
    @Column(nullable = false)
    Integer numberOfPlayers;


    @Column(nullable = false)
    boolean voiceChat;

    @Column(nullable = false)
    long userId; //user id of lobby creator


    @NotBlank
    @NotEmpty
    @Column(nullable = false)
    String userToken;

    @NotBlank
    @NotEmpty
    @Column(nullable = false)
    Integer numberOfBots;

    @Column
    Long lobbyScore;

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

    public long getUserId() {
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
}
