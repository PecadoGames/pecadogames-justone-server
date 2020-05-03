package ch.uzh.ifi.seal.soprafs20.entity;


import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Objects;

@Entity
@Table(name = "ScoreBoard")
public class ScoreBoard {

    @Id
    private Long lobbyId;

    public Long getLobbyId() {
        return lobbyId;
    }

    public void setLobbyId(Long lobbyId) {
        this.lobbyId = lobbyId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) { return true; }
        if (!(o instanceof ScoreBoard)) { return false; }
        ScoreBoard other = (ScoreBoard) o;
        return lobbyId != null && lobbyId.equals(other.getLobbyId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getLobbyId());
    }
}
