package ch.uzh.ifi.seal.soprafs20.entity;


import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Table(name="LOBBYSCORE")

public class LobbyScore implements Serializable {

    private static long serialVersionUID = 1L;

    @Id
    @GeneratedValue
    private Long scoreId;

    @Column
    private String lobbyName;

    @Column
    private int score;

    @Column
    private Date date;

    @ElementCollection
    private Set<Long> playersIdInLobby = new HashSet<>();


    public String getLobbyName() {
        return lobbyName;
    }

    public void setLobbyName(String lobbyName) {
        this.lobbyName = lobbyName;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public void setPlayersIdInLobby(List<Player> players){
        for(Player p : players){
            this.playersIdInLobby.add(p.getId());
        }
    }

    public Set<Long> getPlayersIdInLobby(){
        return this.playersIdInLobby;
    }
}
