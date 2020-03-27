package ch.uzh.ifi.seal.soprafs20.service;


import ch.uzh.ifi.seal.soprafs20.entity.Lobby;
import ch.uzh.ifi.seal.soprafs20.exceptions.ConflictException;
import ch.uzh.ifi.seal.soprafs20.exceptions.NotAcceptableException;
import ch.uzh.ifi.seal.soprafs20.exceptions.NotFoundException;
import ch.uzh.ifi.seal.soprafs20.exceptions.UnauthorizedException;
import ch.uzh.ifi.seal.soprafs20.repository.LobbyRepository;
import ch.uzh.ifi.seal.soprafs20.rest.dto.LobbyPutDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;


@Service
@Transactional
public class LobbyService {
    private final Logger log = LoggerFactory.getLogger(UserService.class);

    private final LobbyRepository lobbyRepository;

    @Autowired
    public LobbyService(LobbyRepository lobbyRepository) {
        this.lobbyRepository = lobbyRepository;
    }

    public Lobby createLobby(Lobby newLobby){
        checkLobbyName(newLobby.getLobbyName());
        checkIfLobbyExists(newLobby);

        newLobby = lobbyRepository.save(newLobby);
        lobbyRepository.flush();
        return newLobby;

    }

    public Lobby updateLobby(Lobby lobby, LobbyPutDTO receivedValues){
        if(!lobby.getToken().equals(receivedValues.getToken())){
            throw new UnauthorizedException("You are not allowed to change the settings of this lobby!");
        }
        if(receivedValues.isVoiceChat() != lobby.isVoiceChat()){lobby.setVoiceChat(receivedValues.isVoiceChat());}
        if(receivedValues.getNumberOfBots() != null){lobby.setNumberOfBots(receivedValues.getNumberOfBots());}
//        if(!receivedValues.getUsersToKick().isEmpty()){
//        }
        if(receivedValues.getNumberOfPlayers() != null && receivedValues.getNumberOfPlayers() >= 3){
            lobby.setNumberOfPlayers(receivedValues.getNumberOfPlayers());
        }
        return lobby;
    }

    private boolean checkIfLobbyExists(Lobby newLobby) {
        Optional<Lobby> lobbyById = lobbyRepository.findByLobbyId(newLobby.getLobbyId());

        String baseErrorMessage = "The %s provided %s not unique. Therefore, the lobby could not be created!";
        if (!lobbyById.isPresent()) {
            throw new ConflictException(String.format(baseErrorMessage, "lobbyId", "is"));
        }
        return true;
    }

    public void checkLobbyName(String username) {
        if (username.contains(" ") || username.isEmpty() || username.length() > 20 || username.trim().isEmpty()) {
            throw new NotAcceptableException("This is an invalid username. Please max. 20 digits and no spaces.");
        }
    }

    public Lobby getLobby(Long lobbyId){
        Lobby lobby;
        Optional<Lobby> optionalLobby = lobbyRepository.findByLobbyId(lobbyId);
        if(optionalLobby.isPresent()){
            lobby = optionalLobby.get();
            return lobby;
        } else {
            throw new NotFoundException("Could not find lobby!");
        }
    }


}
