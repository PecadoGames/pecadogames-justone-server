package ch.uzh.ifi.seal.soprafs20.rest.dto;

public class LoginPutDTO {

    private String username;

    private String password;

    private Long id;

    private String token;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setPassword(String password){this.password = password;}

    public String getPassword(){return password;}

    public String getToken() {
        return token;
    }
    public void setToken(String token) {
        this.token = token;
    }
    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }
}