package ch.uzh.ifi.seal.soprafs20.rest.dto;

public class LobbyPutDTO {

    private Integer maxNumberOfPlayersAndBots;
    private Integer numberOfBots;
    private Long playerToKickId;
    private String hostToken;
    private Integer rounds;

    public Integer getMaxNumberOfPlayersAndBots() {
        return maxNumberOfPlayersAndBots;
    }

    public void setMaxNumberOfPlayersAndBots(Integer maxNumberOfPlayersAndBots) {
        this.maxNumberOfPlayersAndBots = maxNumberOfPlayersAndBots;
    }

    public Integer getNumberOfBots() { return numberOfBots; }

    public void setNumberOfBots(Integer numberOfBots) { this.numberOfBots = numberOfBots; }

    public Long getPlayerToKickId() {
        return playerToKickId;
    }

    public void setPlayerToKickId(Long playersToKickId) {
        this.playerToKickId = playersToKickId;
    }

    public String getHostToken() {
        return hostToken;
    }

    public void setHostToken(String hostToken) {
        this.hostToken = hostToken;
    }

    public Integer getRounds() {
        return rounds;
    }

    public void setRounds(Integer rounds) {
        this.rounds = rounds;
    }
}
