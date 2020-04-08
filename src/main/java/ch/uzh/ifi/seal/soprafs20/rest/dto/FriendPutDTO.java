package ch.uzh.ifi.seal.soprafs20.rest.dto;

public class FriendPutDTO {

    private String token;
    private Long senderID;
    private boolean accepted;

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public Long getSenderID() {
        return senderID;
    }

    public void setSenderID(Long id) {
        senderID = id;
    }

    public boolean getAccepted() {return accepted; }

    public void setAccepted(boolean val) { accepted = val; }
}
