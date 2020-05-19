package ch.uzh.ifi.seal.soprafs20.rest.dto;

import ch.uzh.ifi.seal.soprafs20.constant.AvatarColor;
import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDate;

//import ch.uzh.ifi.seal.soprafs20.exceptions.GlobalExceptionAdvice;

public class UserPutDTO {

    private String username;
    @JsonFormat(pattern="dd.MM.yyyy")
    private LocalDate birthday;
    private AvatarColor avatarColor;
    private String token;

    public String getUsername(){return this.username;}

    public void setUsername(String username) {
        this.username = username;
    }

    //@JsonFormat(pattern="dd.MM.yyyy")
    public void setBirthday(LocalDate birthday) {
        this.birthday = birthday;
    }

    public LocalDate getBirthday() {
        return birthday;
    }

    public void setAvatarColor(AvatarColor color) { this.avatarColor = color; }

    public AvatarColor getAvatarColor() { return avatarColor; }

    public String getToken(){return token;}

    public void setToken(String token){this.token = token;}
}
