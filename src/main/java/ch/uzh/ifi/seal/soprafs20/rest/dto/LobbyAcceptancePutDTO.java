package ch.uzh.ifi.seal.soprafs20.rest.dto;

public class LobbyAcceptancePutDTO {
    private long lobbyId;
    private long accepterId;
    private String accepterToken;
    private boolean accepted;

    public long getLobbyId() {
        return lobbyId;
    }

    public void setLobbyId(long lobbyId) {
        this.lobbyId = lobbyId;
    }

    public long getAccepterId() {
        return accepterId;
    }

    public void setAccepterId(long accepterId) {
        this.accepterId = accepterId;
    }

    public String getAccepterToken() {
        return accepterToken;
    }

    public void setAccepterToken(String accepterToken) {
        this.accepterToken = accepterToken;
    }

    public boolean isAccepted() {
        return accepted;
    }

    public void setAccepted(boolean accepted) {
        this.accepted = accepted;
    }

}
