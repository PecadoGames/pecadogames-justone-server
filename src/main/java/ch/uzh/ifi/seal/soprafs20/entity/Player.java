package ch.uzh.ifi.seal.soprafs20.entity;

import ch.uzh.ifi.seal.soprafs20.constant.AvatarColor;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;

@Entity
@Table(name = "PLAYER")
public class Player {

    @Id
    private Long id;

    @NotBlank
    @NotEmpty
    @Column(nullable = false, unique = true)
    private String username;

    @Column(nullable = true, unique = true)
    private String token;

    @Column
    private AvatarColor avatarColor;

    @Column
    private boolean clueIsSent;

    @Column
    private int score;



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

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public AvatarColor getAvatarColor() {
        return avatarColor;
    }

    public void setAvatarColor(AvatarColor avatarColor) {
        this.avatarColor = avatarColor;
    }

    public boolean isClueIsSent() {
        return clueIsSent;
    }

    public void setClueIsSent(boolean clueIsSent) {
        this.clueIsSent = clueIsSent;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }


}
