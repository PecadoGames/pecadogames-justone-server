package ch.uzh.ifi.seal.soprafs20.entity;
import ch.uzh.ifi.seal.soprafs20.GameLogic.gameStates.GameState;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name="GAME")
public class Game {

    @Id
    //@GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long lobbyId;

    @Column
    private int roundsPlayed;

    @Column
    private String lobbyName;

    @OneToMany
    private List<Player> players = new ArrayList<>();

    @OneToOne
    private Player currentGuesser;

    @Column
    private volatile String currentWord;

    @Column
    private int overallScore;

    @Column(nullable = false)
    private boolean specialGame;

    @Transient
    private List<Clue> enteredClues = new ArrayList<>();

    @ElementCollection
    private List<String> cluesAsString = new ArrayList<>();

    @ElementCollection
    private List<String> words = new ArrayList<>();

    @Column
    private GameState gameState;

    @Column
    private boolean isGuessCorrect;

    @Column
    private Long startTimeSeconds;

    @Column
    private String currentGuess;

    @Column
    private long time;

    @Column
    private volatile boolean cancelled;

    @OneToOne(cascade = {CascadeType.ALL})
    private InternalTimer timer;

    public Long getLobbyId() {
        return lobbyId;
    }

    public void setLobbyId(Long lobbyId) {
        this.lobbyId = lobbyId;
    }

    public int getRoundsPlayed() {
        return roundsPlayed;
    }

    public void setRoundsPlayed(int roundsPlayed) {
        this.roundsPlayed = roundsPlayed;
    }

    public List<Player> getPlayers() {
        return players;
    }

    public void addPlayer(Player player) {
        this.players.add(player);
    }

    public synchronized String getCurrentWord() { return currentWord; }

    public synchronized void setCurrentWord(String currentWord) {
        this.currentWord = currentWord;
    }

    public List<Clue> getEnteredClues() {
        return enteredClues;
    }

    public void setEnteredClues(List<Clue> enteredClues) {
        this.enteredClues = enteredClues;
    }

    public void addClue(Clue clue){
        this.enteredClues.add(clue);
    }

    public List<String> getCluesAsString() {
        return cluesAsString;
    }

    public void setCluesAsString(List<String> cluesAsString) {
        this.cluesAsString = cluesAsString;
    }

    public void addClueAsString(String clueAsString) {
        cluesAsString.add(clueAsString);
    }

    public List<String> getWords() {
        return words;
    }

    public void setWords(List<String> words) {
        this.words = words;
    }

    public GameState getGameState () { return gameState; }

    public void setGameState(GameState state) { this.gameState = state; }

    public Player getCurrentGuesser() {
        return currentGuesser;
    }

    public void setCurrentGuesser(Player currentGuesser) {
        this.currentGuesser = currentGuesser;
    }

    public int getOverallScore() {
        return overallScore;
    }

    public void setOverallScore(int overallScore) {
        this.overallScore = overallScore;
    }

    public boolean isGuessCorrect() {
        return isGuessCorrect;
    }

    public void setGuessCorrect(boolean guessCorrect) {
        isGuessCorrect = guessCorrect;
    }

    public Long getStartTimeSeconds() {
        return startTimeSeconds;
    }

    public void setStartTimeSeconds(long startTime) {
        this.startTimeSeconds = startTime;
    }

    public String getCurrentGuess() { return currentGuess; }

    public void setCurrentGuess(String currentGuess) { this.currentGuess = currentGuess; }

    public boolean isSpecialGame() {
        return specialGame;
    }

    public void setSpecialGame(boolean specialGame) {
        this.specialGame = specialGame;
    }

    public InternalTimer getTimer() {
        return timer;
    }

    public void setTimer(InternalTimer timer) {
        this.timer = timer;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public String getLobbyName() {
        return lobbyName;
    }

    public void setLobbyName(String lobbyName) {
        this.lobbyName = lobbyName;
    }
}
