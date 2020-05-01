package ch.uzh.ifi.seal.soprafs20.rest.dto;

import ch.uzh.ifi.seal.soprafs20.GameLogic.gameStates.GameState;
import ch.uzh.ifi.seal.soprafs20.entity.Clue;
import ch.uzh.ifi.seal.soprafs20.entity.Player;
import ch.uzh.ifi.seal.soprafs20.rest.mapper.DTOMapper;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class GameGetDTO {

    private String lobbyName;
    private Set<PlayerGetDTO> players = new HashSet<>();
    private PlayerGetDTO currentGuesser;
    private long lobbyId;
    private int roundsPlayed;
    private String currentWord;
    private GameState gameState;
    private List<ClueGetDTO> enteredClues = new ArrayList<>();
    private int overallScore;


    public Set<PlayerGetDTO> getPlayers() {
        return players;
    }

    public void setPlayers(Set<Player> players) {
        for(Player p : players){
            PlayerGetDTO player = DTOMapper.INSTANCE.convertEntityToPlayerGetDTO(p);
            this.players.add(player);
        }
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

    public List<ClueGetDTO> getEnteredClues() {
        return enteredClues;
    }

    public void setEnteredClues(List<Clue> enteredClues) {
        for (Clue clue : enteredClues) {
            ClueGetDTO clueGetDTO = DTOMapper.INSTANCE.convertEntityToClueGetDTO(clue);
            this.enteredClues.add(clueGetDTO);
        }
    }
}
