package ch.uzh.ifi.seal.soprafs20.rest.dto;

import ch.uzh.ifi.seal.soprafs20.constant.AvatarColor;
import ch.uzh.ifi.seal.soprafs20.constant.UserStatus;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Date;

public class UserGetDTO {

    private Long id;
    private String username;
    private UserStatus status;
    private AvatarColor avatarColor;
    private Date creationDate;
    @JsonFormat(pattern="dd.MM.yyyy")
    private Date birthday;
    private String token;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    @JsonProperty("logged_in")
    public boolean getStatus() {
        return status == UserStatus.ONLINE;
    }

    public void setStatus(UserStatus status) {
        this.status = status;
    }

    public AvatarColor getAvatarColor() { return avatarColor; }

    public void setAvatarColor(AvatarColor color) { this.avatarColor = color; }

    public void setBirthday(Date birthday){
        this.birthday = birthday;
    }

    public Date getBirthday() {
        return birthday;
    }

    public void setCreationDate(Date creationDate) {
        this.creationDate = creationDate;
    }

    @JsonProperty("creation_date")
    public Date getCreationDate() {
        return creationDate;
    }

    public void setToken(String token){this.token = token;}

    public String getToken(){return token;}


}

