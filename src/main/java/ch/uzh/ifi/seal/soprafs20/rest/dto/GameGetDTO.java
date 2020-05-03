package ch.uzh.ifi.seal.soprafs20.rest.dto;

import ch.uzh.ifi.seal.soprafs20.GameLogic.gameStates.GameState;
import ch.uzh.ifi.seal.soprafs20.entity.Player;
import ch.uzh.ifi.seal.soprafs20.rest.mapper.DTOMapper;

import java.util.*;

public class GameGetDTO {

    private String lobbyName;
    private List<PlayerGetDTO> players = new ArrayList<>();
    private PlayerGetDTO currentGuesser;
    private long lobbyId;
    private int roundsPlayed;
    private String currentWord;
    private GameState gameState;
    private List<String> cluesAsString = new ArrayList<>();
    private List<String> invalidClues = new ArrayList<>();
    private int overallScore;
    private boolean specialGame;
    private boolean isGuessCorrect;


    public List<PlayerGetDTO> getPlayers() {
        return players;
    }

    public void setPlayers(Set<Player> listOfPlayers) {
        for(Player p : listOfPlayers){
            PlayerGetDTO player = DTOMapper.INSTANCE.convertEntityToPlayerGetDTO(p);
            this.players.add(player);
        }
        Collections.sort(this.players, Comparator.comparingLong(PlayerGetDTO::getID));
    }

    public PlayerGetDTO getCurrentGuesser() { return currentGuesser; }

    public void setCurrentGuesser(Player player) {
        this.currentGuesser = DTOMapper.INSTANCE.convertEntityToPlayerGetDTO(player);
    }

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

    public String getCurrentWord() { return currentWord; }

    public void setCurrentWord(String currentWord) { this.currentWord = currentWord; }

    public GameState getGameState() {
        return gameState;
    }

    public void setGameState(GameState gameState) {
        this.gameState = gameState;
    }

    public int getOverallScore() {
        return overallScore;
    }

    public void setOverallScore(int overallScore) {
        this.overallScore = overallScore;
    }

    public String getLobbyName() {
        return lobbyName;
    }

    public void setLobbyName(String lobbyName) {
        this.lobbyName = lobbyName;
    }

    public List<String> getCluesAsString() {
        return cluesAsString;
    }

    public void setCluesAsString(List<String> cluesAsString) {
        this.cluesAsString = cluesAsString;
    }

    public List<String> getInvalidClues() { return invalidClues; }

    public void setInvalidClues(List<String> invalidClues) { this.invalidClues = invalidClues; }

    public void addInvalidClue(String clue) { invalidClues.add(clue); }

    public boolean isSpecialGame() {
        return specialGame;
    }

    public void setSpecialGame(boolean specialGame) {
        this.specialGame = specialGame;
    }

    public boolean isGuessCorrect() {
        return isGuessCorrect;
    }

    public void setGuessCorrect(boolean guessCorrect) {
        isGuessCorrect = guessCorrect;
    }
}
