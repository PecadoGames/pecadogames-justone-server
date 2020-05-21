package ch.uzh.ifi.seal.soprafs20.rest.dto;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalTime;

public class MessageGetDTO {

    private Long messageId;

    private Long authorId;

    private String authorUsername;

    private String text;

    @JsonFormat(pattern="hh:mm:ss")
    private LocalTime creationDate;

    public Long getMessageId() {
        return messageId;
    }

    public void setMessageId(Long messageId) {
        this.messageId = messageId;
    }

    public Long getAuthorId() {
        return authorId;
    }

    public void setAuthorId(Long authorId) {
        this.authorId = authorId;
    }

    public String getAuthorUsername() {
        return authorUsername;
    }

    public void setAuthorUsername(String authorUsername) {
        this.authorUsername = authorUsername;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public LocalTime getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(LocalTime creationDate) {
        this.creationDate = creationDate;
    }
}
