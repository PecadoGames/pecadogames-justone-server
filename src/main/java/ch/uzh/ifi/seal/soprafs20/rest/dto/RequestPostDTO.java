package ch.uzh.ifi.seal.soprafs20.rest.dto;

public class RequestPostDTO {
    private long requestedUserId;
    private String token;





    public long getRequestedUserId() {
        return requestedUserId;
    }

    public void setRequestedUserId(long requestedUserId) {
        this.requestedUserId = requestedUserId;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
