package ch.uzh.ifi.seal.soprafs20.service;


import ch.uzh.ifi.seal.soprafs20.entity.Lobby;
import ch.uzh.ifi.seal.soprafs20.entity.Player;
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

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;


@Service
@Transactional
public class LobbyService {
    private final Logger log = LoggerFactory.getLogger(LobbyService.class);

    private final LobbyRepository lobbyRepository;

    @Autowired
    public LobbyService(LobbyRepository lobbyRepository) {
        this.lobbyRepository = lobbyRepository;
    }

    public List<Lobby> getLobbies() {
        return this.lobbyRepository.findAll();
    }

    public Lobby getLobby(Long lobbyId){
        Lobby lobby;
        Optional<Lobby> optionalLobby = lobbyRepository.findById(lobbyId);
        if(optionalLobby.isPresent()){
            lobby = optionalLobby.get();
            return lobby;
        } else {
            throw new NotFoundException("Could not find lobby!");
        }
    }

    public Lobby createLobby(Lobby newLobby, Player host){
        checkLobbyName(newLobby.getLobbyName());
        checkIfLobbyExists(newLobby);
        if(newLobby.isPrivate()){
            newLobby.setPrivateKey((UUID.randomUUID().toString()));
        }
        newLobby.addPlayerToLobby(host);
        newLobby.setCurrentNumPlayersAndBots(newLobby.getPlayersInLobby().size());
        if(newLobby.getMaxPlayersAndBots() > 7 || newLobby.getMaxPlayersAndBots() < 3){
            newLobby.setMaxPlayersAndBots(7);
        }
        newLobby.setCurrentNumPlayersAndBots(newLobby.getUsersInLobby().size());
        newLobby = lobbyRepository.save(newLobby);
        lobbyRepository.flush();
        return newLobby;
    }

    /**
     * @param lobby
     * @param receivedValues
     * @return
     */
    public Lobby updateLobby(Lobby lobby, LobbyPutDTO receivedValues){
        if(!lobby.getToken().equals(receivedValues.getToken())){
            throw new UnauthorizedException("You are not allowed to change the settings of this lobby!");
        }

        //remove kicked players from lobby
        if(receivedValues.getPlayersToKick() != null){
            lobby.replacePlayersInLobby(kickPlayers(receivedValues.getPlayersToKick(),lobby));
            //update number of player in lobby
            lobby.setCurrentNumPlayersAndBots(lobby.getCurrentNumPlayersAndBots() - receivedValues.getPlayersToKick().size());
        }
        //change size of lobby
        if(receivedValues.getMaxNumberOfPlayersAndBots() != null){
            int newLobbySize = receivedValues.getMaxNumberOfPlayersAndBots();
            if(newLobbySize >= 3 && newLobbySize <= 7 && newLobbySize >= lobby.getCurrentNumPlayersAndBots()){
                lobby.setMaxPlayersAndBots(newLobbySize);
            }
        }
        return lobby;
    }

    private void checkIfLobbyExists(Lobby newLobby) {
        Optional<Lobby> lobbyByUserId = lobbyRepository.findByHostId(newLobby.getHostId());

        String baseErrorMessage = "The current %s provided %s already hosting another lobby. Therefore, the lobby could not be created!";
        if (lobbyByUserId.isPresent()) {
            throw new ConflictException(String.format(baseErrorMessage, "userId", "is"));
        }

    }

    public void checkLobbyName(String username) {
        if (username.contains(" ") || username.isEmpty() || username.length() > 20 || username.trim().isEmpty()) {
            throw new NotAcceptableException("This is an invalid username. Please max. 20 digits and no spaces.");
        }
    }

    public void addPlayerToLobby(Player playerToAdd, Lobby lobby){
        if(lobby.isGameStarted()){
            throw new ConflictException("Cant join the lobby, the game is already under way!");
        }
        //player that is not host wants to join but they are already in lobby
        if(!lobby.getHostId().equals(playerToAdd.getId()) && lobby.getPlayersInLobby().contains(playerToAdd)){
            throw new ConflictException("User is already in lobby!");
        }
        if(lobby.getHostId().equals(playerToAdd.getId())){
            throw new ConflictException("Host cannot join their own lobby!");
        }
        checkIfLobbyIsFull(lobby);
        lobby.addPlayerToLobby(playerToAdd);
        lobby.setCurrentNumPlayersAndBots(lobby.getPlayersInLobby().size());
        lobbyRepository.save(lobby);
    }

    public void removePlayerFromLobby(Player playerToRemove, Lobby lobby){
        if(lobby.isGameStarted()){
            throw new ConflictException("Cannot leave lobby, game already started");
        }
        if(playerToRemove.getId().equals(lobby.getHostId())){
            //host leaves lobby and is alone
            if(lobby.getCurrentNumPlayersAndBots().equals(1)){
                lobbyRepository.delete(lobby);
            }
            //host leaves lobby, so new host is chosen
            else{
                Player newHost = lobby.getPlayersInLobby().iterator().next();
                lobby.setHostId(newHost.getId());
                lobby.setToken(newHost.getToken());
                lobby.getPlayersInLobby().remove(playerToRemove);
                lobby.setCurrentNumPlayersAndBots(lobby.getPlayersInLobby().size());
                lobbyRepository.save(lobby);
            }
        } else if(lobby.getPlayersInLobby().contains(playerToRemove)){
            lobby.getPlayersInLobby().remove(playerToRemove);
            lobby.setCurrentNumPlayersAndBots(lobby.getPlayersInLobby().size());
        }
    }

    private void checkIfLobbyIsFull(Lobby lobby) {
        if(lobby.getCurrentNumPlayersAndBots() + 1 > lobby.getMaxPlayersAndBots()) {
            throw new ConflictException("Failed to join lobby: Sorry, the lobby is already full");
        }
    }

    public Set<Player> kickPlayers(List<Long> kickList, Lobby lobby){
        //remove user from lobby but dont remove lobby leader
        lobby.getPlayersInLobby().removeIf(player -> kickList.contains(player.getId()) && !player.getId().equals(lobby.getHostId()));
        return lobby.getPlayersInLobby();
    }

}
