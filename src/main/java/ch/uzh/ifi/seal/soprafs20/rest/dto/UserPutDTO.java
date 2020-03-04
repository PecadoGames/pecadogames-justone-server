package ch.uzh.ifi.seal.soprafs20.rest.dto;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class UserPutDTO {

    private String username;
    private Date birthday;
    private String token;

    public String getUsername(){return this.username;}

    public void setUsername(String username) {
        this.username = username;
    }

    public void setBirthday(String birthday) throws ParseException {
        if(birthday == null){
            this.birthday = null;
        }
        else{
        birthday = birthday+" 01:00:00";
        this.birthday = new SimpleDateFormat("dd.MM.yyyy hh:mm:ss").parse(birthday);
        }
    }

    public Date getBirthday() {
        return birthday;
    }

    public String getToken(){return token;}

    public void setToken(String token){this.token = token;}
}
