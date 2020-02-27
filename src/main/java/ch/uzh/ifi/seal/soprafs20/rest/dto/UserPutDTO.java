package ch.uzh.ifi.seal.soprafs20.rest.dto;

public class UserPutDTO {

    private Long id;
    private String name;
    private String username;

    public Long getId() {
        return id;
    }

    public String getName(){return this.name;}

    public String getUsername(){return this.username;}

    public void setName(String name) {
        this.name = name;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
