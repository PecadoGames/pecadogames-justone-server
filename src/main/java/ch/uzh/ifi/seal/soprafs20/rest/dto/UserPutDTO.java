package ch.uzh.ifi.seal.soprafs20.rest.dto;

import ch.uzh.ifi.seal.soprafs20.exceptions.ConflictException;
//import ch.uzh.ifi.seal.soprafs20.exceptions.GlobalExceptionAdvice;
import ch.uzh.ifi.seal.soprafs20.exceptions.NotFoundException;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class UserPutDTO {

    private String username;

    @JsonFormat(pattern="dd.MM.yyyy")
    private Date birthday;
    private String token;

    public String getUsername(){return this.username;}

    public void setUsername(String username) {
        this.username = username;
    }

    @JsonFormat(pattern="dd.MM.yyyy")
    public void setBirthday(Date birthday) {
        this.birthday = birthday;
    }

    public Date getBirthday() {
        return birthday;
    }

    public String getToken(){return token;}

    public void setToken(String token){this.token = token;}
}
