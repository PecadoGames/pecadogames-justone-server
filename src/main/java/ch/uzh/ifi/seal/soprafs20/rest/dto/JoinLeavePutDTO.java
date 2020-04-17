package ch.uzh.ifi.seal.soprafs20.rest.dto;

public class JoinLeavePutDTO {

    private long userId;
    private String userToken;


    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public String getUserToken() {
        return userToken;
    }

    public void setUserToken(String userToken) {
        this.userToken = userToken;
    }
}
