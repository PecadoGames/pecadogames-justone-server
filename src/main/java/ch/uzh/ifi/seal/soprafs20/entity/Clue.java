package ch.uzh.ifi.seal.soprafs20.entity;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import java.io.Serializable;
import java.util.Objects;

@Entity
@Table(name = "CLUE")
public class Clue implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue
    private Long clueId;

    private Long playerId;

    @NotBlank
    @NotEmpty
    @Column(nullable = false)
    private String actualClue;

    @Column
    private Long timeNeeded;

    public Long getClueId() { return clueId; }

    public void setClueId(Long clueId) { this.clueId = clueId; }

    public Long getPlayerId() {
        return playerId;
    }

    public void setPlayerId(Long playerId) {
        this.playerId = playerId;
    }

    public String getActualClue() {
        return actualClue;
    }

    public void setActualClue(String actualClue) {
        this.actualClue = actualClue.toLowerCase();
    }

    public Long getTimeNeeded() { return timeNeeded; }

    public void setTimeNeeded(Long timeNeeded) { this.timeNeeded = timeNeeded; }

    @Override
    public boolean equals(Object o) {
        if (this == o) { return true; }
        if (!(o instanceof Clue)) { return false; }
        Clue other = (Clue) o;
        return actualClue.equalsIgnoreCase(other.getActualClue());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getPlayerId());
    }
}
