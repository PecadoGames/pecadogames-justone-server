package ch.uzh.ifi.seal.soprafs20.rest.dto;

import ch.uzh.ifi.seal.soprafs20.exceptions.GlobalExceptionAdvice;
import ch.uzh.ifi.seal.soprafs20.exceptions.NotFoundException;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import org.springframework.http.converter.HttpMessageNotReadableException;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class UserPutDTO {

    private String username;
    private String birthday;
    private String token;

    public String getUsername(){return this.username;}

    public void setUsername(String username) {
        this.username = username;
    }

    public void setBirthday(String birthday) throws ParseException {
        this.birthday = birthday;
    }

    public String getBirthday() {
        return birthday;
    }

    public String getToken(){return token;}

    public void setToken(String token){this.token = token;}
}
