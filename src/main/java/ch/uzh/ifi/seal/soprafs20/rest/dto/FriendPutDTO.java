package ch.uzh.ifi.seal.soprafs20.rest.dto;

public class FriendPutDTO {

    //accepting user token
    private String accepterToken;
    private Long requesterID;
    private boolean accepted;

    public String getAccepterToken() {
        return accepterToken;
    }

    public void setAccepterToken(String accepterToken) {
        this.accepterToken = accepterToken;
    }

    public Long getRequesterID() {
        return requesterID;
    }

    public void setRequesterID(Long id) {
        requesterID = id;
    }

    public boolean getAccepted() {return accepted; }

    public void setAccepted(boolean val) { accepted = val; }
}
