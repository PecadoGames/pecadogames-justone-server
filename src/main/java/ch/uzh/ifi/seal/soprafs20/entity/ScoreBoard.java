package ch.uzh.ifi.seal.soprafs20.entity;


import javax.persistence.*;

import ch.uzh.ifi.seal.soprafs20.repository.LobbyRepository;
import ch.uzh.ifi.seal.soprafs20.repository.UserRepository;

import java.util.ArrayList;

@Entity
@Table(name = "ScoreBoard")
public class ScoreBoard {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue
    private Long lobbyId;

    public void findByPlayerScore(){

    }
}
