package ch.uzh.ifi.seal.soprafs20.entity.gameLogic;

import ch.uzh.ifi.seal.soprafs20.entity.User;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Table(name="GAME")
public class Game {

    @Id
    private long lobbyId;

    @Column
    private int roundsPlayed;

    @OneToMany
    private Set<User> players = new HashSet<>();

    @Column
    private String currentWord;

    @ElementCollection
    private List<String> enteredClues = new ArrayList<>();

    @ElementCollection
    private List<String> words = new ArrayList<>();


    public long getLobbyId() {
        return lobbyId;
    }

    public void setLobbyId(long lobbyId) {
        this.lobbyId = lobbyId;
    }

    public int getRoundsPlayed() {
        return roundsPlayed;
    }

    public void setRoundsPlayed(int roundsPlayed) {
        this.roundsPlayed = roundsPlayed;
    }

    public Set<User> getPlayers() {
        return players;
    }

    public void setPlayers(Set<User> players) {
        this.players = players;
    }

    public String getCurrentWord() {
        return currentWord;
    }

    public void setCurrentWord(String currentWord) {
        this.currentWord = currentWord;
    }

    public List<String> getEnteredClues() {
        return enteredClues;
    }

    public void setEnteredClues(List<String> enteredClues) {
        this.enteredClues = enteredClues;
    }

    public List<String> getWords() {
        return words;
    }

    public void setWords(List<String> words) {
        this.words = words;
    }

}
