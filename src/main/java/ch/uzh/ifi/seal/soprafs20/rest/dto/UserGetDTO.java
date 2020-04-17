package ch.uzh.ifi.seal.soprafs20.rest.dto;

import ch.uzh.ifi.seal.soprafs20.constant.UserStatus;
import ch.uzh.ifi.seal.soprafs20.entity.Lobby;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import org.springframework.http.converter.HttpMessageNotReadableException;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Set;

public class UserGetDTO {

    private Long id;
    private String username;
    private UserStatus status;
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

