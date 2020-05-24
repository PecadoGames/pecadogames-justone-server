package ch.uzh.ifi.seal.soprafs20.rest.dto;

import ch.uzh.ifi.seal.soprafs20.entity.Player;
import ch.uzh.ifi.seal.soprafs20.rest.mapper.DTOMapper;

import java.util.ArrayList;
import java.util.List;

public class LobbyGetDTO {
    private String lobbyName;

    private Long lobbyId;

    private Long hostId;

    private long lobbyScore;

    private boolean isPrivate;

    private String privateKey;

    private Integer currentNumPlayers;

    private Integer currentNumBots;

    private Integer maxPlayersAndBots;

    private boolean gameStarted;

    private Integer currentNumPlayersAndBots;

    List<PlayerGetDTO> playersInLobby = new ArrayList<>();

    private Integer rounds;


    public String getLobbyName() {
        return lobbyName;
    }

    public void setLobbyName(String lobbyName) {
        this.lobbyName = lobbyName;
    }


    public Long getHostId() {
        return hostId;
    }

    public void setHostId(long hostId) {
        this.hostId = hostId;
    }

    public boolean isPrivate() {
        return isPrivate;
    }

    public void setPrivate(boolean aPrivate) {
        isPrivate = aPrivate;
    }

    public String getPrivateKey() {
        return privateKey;
    }

    public void setPrivateKey(String privateKey) {
        this.privateKey = privateKey;
    }

    public void setHostId(Long hostId) {
        this.hostId = hostId;
    }

    public long getLobbyScore() {
        return lobbyScore;
    }

    public void setLobbyScore(long lobbyScore) {
        this.lobbyScore = lobbyScore;
    }

    public Long getLobbyId() {
        return lobbyId;
    }

    public void setLobbyId(Long lobbyId) {
        this.lobbyId = lobbyId;
    }

    public Integer getCurrentNumPlayers() { return currentNumPlayers; }

    public void setCurrentNumPlayers(Integer currentNumPlayers) {
        this.currentNumPlayers = currentNumPlayers;
    }

    public Integer getCurrentNumBots() { return currentNumBots; }

    public void setCurrentNumBots(Integer currentNumBots) { this.currentNumBots = currentNumBots; }

    public Integer getMaxPlayersAndBots() {
        return maxPlayersAndBots;
    }

    public void setMaxPlayersAndBots(Integer maxPlayersAndBots) {
        this.maxPlayersAndBots = maxPlayersAndBots;
    }

    public boolean isGameStarted() { return gameStarted; }

    public void setGameStarted(boolean gameStarted) { this.gameStarted = gameStarted; }

    public Integer getCurrentNumPlayersAndBots() {
        return currentNumPlayersAndBots;
    }

    public void setCurrentNumPlayersAndBots(Integer currentNumPlayersAndBots) {
        this.currentNumPlayersAndBots = this.currentNumPlayers + this.currentNumBots;
    }

    public List<PlayerGetDTO> getPlayersInLobby() {
        return playersInLobby;
    }

    public void setPlayersInLobby(List<Player> players) {
        this.playersInLobby.clear();
        for(Player player : players) {
            PlayerGetDTO playerGetDTO = DTOMapper.INSTANCE.convertEntityToPlayerGetDTO(player);
            this.playersInLobby.add(playerGetDTO);
        }
    }

    public void addPlayersInLobby(Player player) {
        PlayerGetDTO playerGetDTO = DTOMapper.INSTANCE.convertEntityToPlayerGetDTO(player);
        this.playersInLobby.add(playerGetDTO);
    }

    public Integer getRounds() {
        return rounds;
    }

    public void setRounds(Integer rounds) {
        this.rounds = rounds;
    }
}
