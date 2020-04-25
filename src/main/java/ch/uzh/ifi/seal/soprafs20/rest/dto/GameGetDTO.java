package ch.uzh.ifi.seal.soprafs20.rest.dto;

import ch.uzh.ifi.seal.soprafs20.GameLogic.gameStates.GameState;
import ch.uzh.ifi.seal.soprafs20.entity.User;
import ch.uzh.ifi.seal.soprafs20.rest.mapper.DTOMapper;

import java.util.Set;

public class GameGetDTO {

    private Set<PlayerGetDTO> players;
    private long lobbyId;
    private int roundsPlayed;
    private String gameState;
    private int overallScore;


    public Set<PlayerGetDTO> getPlayers() {
        return players;
    }

    public void setPlayers(Set<User> players) {
        for(User p : players){
            PlayerGetDTO player = DTOMapper.INSTANCE.convertEntityToPlayerGetDTO(p);
            this.players.add(player);
        }
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

    public String getGameState() {
        return gameState;
    }

    public void setGameState(GameState gameState) {
        this.gameState = gameState.toString();
    }

    public int getOverallScore() {
        return overallScore;
    }

    public void setOverallScore(int overallScore) {
        this.overallScore = overallScore;
    }
}
