package ch.uzh.ifi.seal.soprafs20.entity.gameLogic;

import ch.uzh.ifi.seal.soprafs20.entity.User;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name="GAME")
public class Game {

    @Id
    private long lobbyId;

    @Column
    private int roundsPlayed;

    @OneToMany
    private List<User> players = new ArrayList<>();

    @OneToOne
    private User currentGuesser;

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

    public List<User> getPlayers() {
        return players;
    }

    public void addPlayer(User player) {
        this.players.add(player);
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
