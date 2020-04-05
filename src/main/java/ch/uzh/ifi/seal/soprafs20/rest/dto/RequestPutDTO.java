package ch.uzh.ifi.seal.soprafs20.rest.dto;

public class RequestPutDTO {

    private String token;
    private Long senderID;

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
}
