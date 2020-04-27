package ch.uzh.ifi.seal.soprafs20.rest.dto;

public class PlayerGetDTO {
    private String username;
//    private UserStatus status;
    private boolean clueIsSent;
    private Long id;
    private int score;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

//    public UserStatus getStatus() {
//        return status;
//    }
//
//    public void setStatus(UserStatus status) {
//        this.status = status;
//    }

    public boolean isClueIsSent() {
        return clueIsSent;
    }

    public void setClueIsSent(boolean clueIsSent) {
        this.clueIsSent = clueIsSent;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public Long getID() {
        return id;
    }
    public void setId(Long playerId) {
        this.id = playerId;
    }
}
