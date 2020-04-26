package ch.uzh.ifi.seal.soprafs20.rest.dto;

public class GamePostDTO {

    //ID of lobby host
    private long hostId;

    private String hostToken;

    public long getHostId() {
        return hostId;
    }

    public void setHostId(long hostId) {
        this.hostId = hostId;
    }

    public String getHostToken() {
        return hostToken;
    }

    public void setHostToken(String hostToken) {
        this.hostToken = hostToken;
    }
}
