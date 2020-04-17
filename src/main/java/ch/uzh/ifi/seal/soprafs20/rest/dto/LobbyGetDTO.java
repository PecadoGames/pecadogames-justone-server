package ch.uzh.ifi.seal.soprafs20.rest.dto;

public class LobbyGetDTO {
    private String lobbyName;

    private boolean voiceChat;

    private Long userId;

    private Long lobbyId;

    private Integer totalPlayersAndBots;

    private Integer maxPlayersAndBots;


    public String getLobbyName() {
        return lobbyName;
    }

    public void setLobbyName(String lobbyName) {
        this.lobbyName = lobbyName;
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


    public Long getLobbyId() {
        return lobbyId;
    }

    public void setLobbyId(Long lobbyId) {
        this.lobbyId = lobbyId;
    }

    public Integer getTotalPlayersAndBots() {
        return totalPlayersAndBots;
    }

    public void setTotalPlayersAndBots(Integer totalPlayersAndBots) {
        this.totalPlayersAndBots = totalPlayersAndBots;
    }

    public Integer getMaxPlayersAndBots() {
        return maxPlayersAndBots;
    }

    public void setMaxPlayersAndBots(Integer maxPlayersAndBots) {
        this.maxPlayersAndBots = maxPlayersAndBots;
    }
}
