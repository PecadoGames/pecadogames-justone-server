package ch.uzh.ifi.seal.soprafs20.entity;

import com.fasterxml.jackson.annotation.JsonFormat;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import java.io.Serializable;
import java.util.Date;

@Entity
@Table(name = "MESSAGE")
public class Message implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue
    private Long messageId;

    @Column(nullable = false)
    private Long authorId;

    @NotBlank
    @NotEmpty
    @Column(nullable = false)
    private String text;

    @Column(nullable = false)
    @JsonFormat(pattern="dd.MM.yyyy hh:mm:ss")
    private Date creationDate;

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

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public Date getCreationDate() {
        return creationDate;
    }

    @JsonFormat(pattern="dd.MM.yyyy")
    public void setCreationDate(Date creationDate) {
        this.creationDate = creationDate;
    }
}
