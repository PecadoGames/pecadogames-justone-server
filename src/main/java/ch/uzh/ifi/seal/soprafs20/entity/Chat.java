package ch.uzh.ifi.seal.soprafs20.entity;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "CHAT")
public class Chat {

    @Id
    private Long lobbyId;

    @OneToMany
    Set<Message> messages = new HashSet<>();


}
