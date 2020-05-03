package ch.uzh.ifi.seal.soprafs20.entity;

import ch.uzh.ifi.seal.soprafs20.constant.AvatarColor;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import java.util.Objects;

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

    @Column
    private volatile boolean voted;

    @Column
    private String clue;



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

    public boolean isVoted() {
        return voted;
    }

    public void setVoted(boolean voted) {
        this.voted = voted;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) { return true; }
        if (!(o instanceof Player)) { return false; }
        Player other = (Player) o;
        return id != null && id.equals(other.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId());
    }

    public String getClue() {
        return clue;
    }

    public void setClue(String clue) {
        this.clue = clue;
    }
}
