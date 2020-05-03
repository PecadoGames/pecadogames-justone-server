package ch.uzh.ifi.seal.soprafs20.entity;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "CHAT")
public class Chat {

    @Id
    private Long lobbyId;

    @OneToMany
    List<Message> messages = new ArrayList<>();

    public Long getLobbyId() {
        return lobbyId;
    }

    public void setLobbyId(Long lobbyId) {
        this.lobbyId = lobbyId;
    }

    public List<Message> getMessages() {
        return messages;
    }

    public void setMessages(Message message) {
        messages.add(message);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) { return true; }
        if (!(o instanceof Chat)) { return false; }
        Chat other = (Chat) o;
        return lobbyId != null && lobbyId.equals(other.getLobbyId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getLobbyId());
    }
}
