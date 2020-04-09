package ch.uzh.ifi.seal.soprafs20.rest.dto;

public class InvitePutDTO {
    private long userId;
    private String token;
    private long userToInviteId;



    public String getToken() {return token;}

    public void setToken(String token) {this.token = token;}

    public long getUserToInviteId() {
        return userToInviteId;
    }

    public void setUserToInviteId(long userToInviteId) {
        this.userToInviteId = userToInviteId;
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }
}
