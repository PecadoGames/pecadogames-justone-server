package ch.uzh.ifi.seal.soprafs20.rest.dto;

public class InviteGetDTO {
    private Long lobbyId;

    private String lobbyName;

    private String privateKey;

    private String hostName;

    public Long getLobbyId() { return lobbyId; }

    public void setLobbyId(long lobbyId) { this.lobbyId = lobbyId; }

    public String getLobbyName() {
        return lobbyName;
    }

    public void setLobbyName(String lobbyName) {
        this.lobbyName = lobbyName;
    }

    public String getPrivateKey() {
        return privateKey;
    }

    public void setPrivateKey(String privateKey) {
        this.privateKey = privateKey;
    }

    public String getHostName() {
        return hostName;
    }

    public void setHostName(String hostName) {
        this.hostName = hostName;
    }
}
