package ch.uzh.ifi.seal.soprafs20.entity;

import ch.uzh.ifi.seal.soprafs20.constant.AvatarColor;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import java.util.ArrayList;
import java.util.List;
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

    @Column(unique = true)
    private String token;

    @Column
    private AvatarColor avatarColor;

    @Column
    private boolean clueIsSent;

    @Column
    private boolean guessIsSent;

    @Column
    private int score;

    @Column
    private volatile boolean voted;

    @OneToMany(fetch = FetchType.EAGER)
    @Fetch(value = FetchMode.SUBSELECT)
    @Cascade(org.hibernate.annotations.CascadeType.ALL)
    private List<Clue> clues = new ArrayList<>();


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

    public boolean isGuessIsSent() { return guessIsSent; }

    public void setGuessIsSent(boolean guessIsSent) { this.guessIsSent = guessIsSent; }

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

    public List<Clue> getClues() {
        return clues;
    }

    public Clue getClue(int i) { return clues.get(i); }

    public void setClues(List<Clue> clues) {
        this.clues.clear();
        for (Clue clue : clues) {
            clue.setActualClue(clue.getActualClue().toLowerCase());
            this.clues.add(clue);
        }
    }

    public void addClue(Clue clue) {
        clue.setActualClue(clue.getActualClue().toLowerCase());
        this.clues.add(clue); }

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
}
