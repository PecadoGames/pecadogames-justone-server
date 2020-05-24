package ch.uzh.ifi.seal.soprafs20.rest.dto;

import ch.uzh.ifi.seal.soprafs20.constant.AvatarColor;

public class PlayerGetDTO {
    private String username;
    private boolean clueIsSent;
    private boolean isVoted;
    private Long id;
    private int score;
    private AvatarColor avatarColor;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

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

    public boolean isVoted() {
        return isVoted;
    }

    public void setVoted(boolean voted) {
        isVoted = voted;
    }

    public AvatarColor getAvatarColor() {
        return avatarColor;
    }

    public void setAvatarColor(AvatarColor avatarColor) {
        this.avatarColor = avatarColor;
    }
}
