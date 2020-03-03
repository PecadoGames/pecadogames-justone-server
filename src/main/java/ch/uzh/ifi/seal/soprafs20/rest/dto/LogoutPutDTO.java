package ch.uzh.ifi.seal.soprafs20.rest.dto;

public class LogoutPutDTO {
    private long id;
    private String token;


    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getToken() {
        return token;
    }
}
