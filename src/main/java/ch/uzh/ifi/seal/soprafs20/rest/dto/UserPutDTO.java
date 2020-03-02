package ch.uzh.ifi.seal.soprafs20.rest.dto;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class UserPutDTO {

    private String username;
    private String password;
    private Date birthday;
    private String token;

    public String getUsername(){return this.username;}

    public void setUsername(String username) {
        this.username = username;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPassword() {
        return password;
    }

    public void setBirthday(String birthday) throws ParseException {
        birthday = birthday+" 01:00:00";
        this.birthday = new SimpleDateFormat("dd.MM.yyyy hh:mm:ss").parse(birthday);
    }

    public Date getBirthday() {
        return birthday;
    }

    public String getToken(){return token;}

    public void setToken(String token){this.token = token;}
}
