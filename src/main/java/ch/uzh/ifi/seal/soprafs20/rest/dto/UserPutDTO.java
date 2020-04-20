package ch.uzh.ifi.seal.soprafs20.rest.dto;

import ch.uzh.ifi.seal.soprafs20.constant.AvatarColor;

import java.util.Date;

//import ch.uzh.ifi.seal.soprafs20.exceptions.GlobalExceptionAdvice;

public class UserPutDTO {

    private String username;
    //@JsonFormat(pattern="dd.MM.yyyy")
    private Date birthday;
    private AvatarColor avatarColor;
    private String token;

    public String getUsername(){return this.username;}

    public void setUsername(String username) {
        this.username = username;
    }

    //@JsonFormat(pattern="dd.MM.yyyy")
    public void setBirthday(Date birthday) {
        this.birthday = birthday;
    }

    public Date getBirthday() {
        return birthday;
    }

    public void setAvatarColor(AvatarColor color) { this.avatarColor = color; }

    public AvatarColor getAvatarColor() { return avatarColor; }

    public String getToken(){return token;}

    public void setToken(String token){this.token = token;}
}
