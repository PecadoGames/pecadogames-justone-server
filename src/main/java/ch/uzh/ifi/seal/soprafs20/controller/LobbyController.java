package ch.uzh.ifi.seal.soprafs20.controller;

import ch.uzh.ifi.seal.soprafs20.entity.Lobby;
import ch.uzh.ifi.seal.soprafs20.rest.dto.*;
import ch.uzh.ifi.seal.soprafs20.rest.mapper.DTOMapper;
import ch.uzh.ifi.seal.soprafs20.service.LobbyService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;



@RestController
public class LobbyController {
    private final LobbyService lobbyService;

    LobbyController(LobbyService lobbyService){
        this.lobbyService = lobbyService;
    }



    @CrossOrigin(exposedHeaders = "Location")
    @PostMapping(path = "/lobbies", consumes = "application/json")
    @ResponseStatus(HttpStatus.CREATED)
    @ResponseBody
    public ResponseEntity<Object> createLobby(@RequestBody LobbyPostDTO lobbyPostDTO) {
        Lobby createdLobby;
        // convert API user to internal representation
        Lobby userLobby = DTOMapper.INSTANCE.convertLobbyPostDTOtoEntity(lobbyPostDTO);

        // create user
        createdLobby = lobbyService.createLobby(userLobby);

        // convert internal representation of user back to API
        URI location = ServletUriComponentsBuilder.fromCurrentRequest().path("/{lobbyId}")
                .buildAndExpand(createdLobby.getLobbyId()).toUri(); //getLobbyId instead of lobbyname => what happens if two lobbies have the same name, same uri! not good!
        return ResponseEntity.created(location).build();
    }

    @GetMapping(path = "/lobbies", produces = "application/json")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public List<LobbyGetDTO> getAllLobbies() {
        // fetch all lobbies in the internal representation
        List<Lobby> lobbies = lobbyService.getLobbies();
        List<LobbyGetDTO> lobbyGetDTOs = new ArrayList<>();

        // convert each lobby to the API representation
        for (Lobby lobby : lobbies) {
            lobbyGetDTOs.add(DTOMapper.INSTANCE.convertEntityToLobbyGetDTO(lobby));
        }
        return lobbyGetDTOs;
    }


    @PutMapping(path = "/lobbies/{lobbyId}", consumes = "application/json")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @ResponseBody
    public void updateLobby(@PathVariable long lobbyId, @RequestBody LobbyPutDTO lobbyPutDTO){
        Lobby lobby;
        lobby = lobbyService.getLobby(lobbyId);
        lobbyService.updateLobby(lobby,lobbyPutDTO);
    }
}
