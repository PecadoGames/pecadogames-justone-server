package ch.uzh.ifi.seal.soprafs20.rest.dto;

public class LobbyPostDTO {
    private String lobbyName;

    private Integer maxPlayersAndBots;

    private boolean voiceChat;

    private Long hostId;

    private String hostToken; //user token!

    private boolean isPrivate;



    public String getLobbyName() {
        return lobbyName;
    }

    public void setLobbyName(String lobbyName) {
        this.lobbyName = lobbyName;
    }

    public Integer getMaxPlayersAndBots() {
        return maxPlayersAndBots;
    }

    public void setMaxPlayersAndBots(Integer maxPlayersAndBots) {
        this.maxPlayersAndBots = maxPlayersAndBots;
    }

    public boolean isVoiceChat() {
        return voiceChat;
    }

    public void setVoiceChat(boolean voiceChat) {
        this.voiceChat = voiceChat;
    }

    public Long getHostId() {
        return hostId;
    }

    public void setHostId(long hostId) {
        this.hostId = hostId;
    }

    public String getHostToken() {
        return hostToken;
    }

    public void setHostToken(String hostToken) {
        this.hostToken = hostToken;
    }

    public boolean isPrivate() {
        return isPrivate;
    }

    public void setPrivate(boolean isPrivate) {
        this.isPrivate = isPrivate;
    }
}
