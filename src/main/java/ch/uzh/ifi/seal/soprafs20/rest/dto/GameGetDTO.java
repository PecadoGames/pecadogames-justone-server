package ch.uzh.ifi.seal.soprafs20.rest.dto;

import ch.uzh.ifi.seal.soprafs20.GameLogic.gameStates.GameState;
import ch.uzh.ifi.seal.soprafs20.entity.Clue;
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
    private List<ClueGetDTO> enteredClues = new ArrayList<>();
    private List<ClueGetDTO> invalidClues = new ArrayList<>();
    private int overallScore;
    private boolean specialGame;
    private boolean isGuessCorrect;
    private String currentGuess;


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

    public void addPlayer(Player player) {
        PlayerGetDTO playerGetDTO = DTOMapper.INSTANCE.convertEntityToPlayerGetDTO(player);
        this.players.add(playerGetDTO);
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

    public List<ClueGetDTO> getEnteredClues() { return enteredClues; }

    public void setEnteredClues(List<Clue> clues) {
        for (Clue clue : clues) {
            ClueGetDTO clueGetDTO = DTOMapper.INSTANCE.convertEntityToClueGetDTO(clue);
            this.enteredClues.add(clueGetDTO);
        }
    }

    public List<ClueGetDTO> getInvalidClues() { return invalidClues; }

    public void setInvalidClues(List<Clue> invalidClues) {
        for (Clue clue : invalidClues) {
            ClueGetDTO clueGetDTO = DTOMapper.INSTANCE.convertEntityToClueGetDTO(clue);
            this.invalidClues.add(clueGetDTO);
        }
    }

    public void addInvalidClue(Clue clue) {
        ClueGetDTO clueGetDTO = DTOMapper.INSTANCE.convertEntityToClueGetDTO(clue);
        invalidClues.add(clueGetDTO); }

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

    public String getCurrentGuess() { return currentGuess; }

    public void setCurrentGuess(String guess) { currentGuess = guess; }
}
