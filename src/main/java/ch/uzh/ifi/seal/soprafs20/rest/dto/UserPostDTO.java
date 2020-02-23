package ch.uzh.ifi.seal.soprafs20.rest.dto;

public class UserPostDTO {

    private String name;

    private String username;

    private String password;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setPassword(String password){this.password = password;}

    public String getPassword(){return password;}
}
