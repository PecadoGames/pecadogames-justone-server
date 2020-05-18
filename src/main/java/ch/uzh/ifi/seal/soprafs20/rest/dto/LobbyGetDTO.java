package ch.uzh.ifi.seal.soprafs20.rest.dto;

public class LobbyGetDTO {
    private String lobbyName;

    private Long lobbyId;

    private Long hostId;

    private boolean voiceChat;

    private long lobbyScore;

    private boolean isPrivate;

    private Integer currentNumPlayers;

    private Integer currentNumBots;

    private Integer maxPlayersAndBots;

    private boolean gameIsStarted;

    private Integer currentNumPlayersAndBots;


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

    public Long getHostId() {
        return hostId;
    }

    public void setHostId(long hostId) {
        this.hostId = hostId;
    }

    public boolean isPrivate() {
        return isPrivate;
    }

    public void setPrivate(boolean aPrivate) {
        isPrivate = aPrivate;
    }

    public Long getLobbyId() {
        return lobbyId;
    }

    public void setLobbyId(Long lobbyId) {
        this.lobbyId = lobbyId;
    }

    public Integer getCurrentNumPlayers() { return currentNumPlayers; }

    public void setCurrentNumPlayers(Integer currentNumPlayers) { this.currentNumPlayers = currentNumPlayers; }

    public Integer getCurrentNumBots() { return currentNumBots; }

    public void setCurrentNumBots(Integer currentNumBots) { this.currentNumBots = currentNumBots; }

    public Integer getMaxPlayersAndBots() {
        return maxPlayersAndBots;
    }

    public void setMaxPlayersAndBots(Integer maxPlayersAndBots) {
        this.maxPlayersAndBots = maxPlayersAndBots;
    }

    public boolean isGameIsStarted() { return gameIsStarted; }

    public void setGameIsStarted(boolean gameIsStarted) { this.gameIsStarted = gameIsStarted; }

    public Integer getCurrentNumPlayersAndBots() {
        return currentNumPlayersAndBots;
    }

    public void setCurrentNumPlayersAndBots(Integer currentNumPlayersAndBots) {
        this.currentNumPlayersAndBots = this.currentNumPlayers + this.currentNumBots;
    }
}
