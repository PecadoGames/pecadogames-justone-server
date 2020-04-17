package ch.uzh.ifi.seal.soprafs20.controller;

import ch.uzh.ifi.seal.soprafs20.entity.Chat;
import ch.uzh.ifi.seal.soprafs20.entity.Lobby;
import ch.uzh.ifi.seal.soprafs20.entity.User;
import ch.uzh.ifi.seal.soprafs20.exceptions.BadRequestException;
import ch.uzh.ifi.seal.soprafs20.rest.dto.*;
import ch.uzh.ifi.seal.soprafs20.rest.mapper.DTOMapper;
import ch.uzh.ifi.seal.soprafs20.service.ChatService;
import ch.uzh.ifi.seal.soprafs20.service.LobbyService;
import ch.uzh.ifi.seal.soprafs20.service.UserService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
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
    private final UserService userService;
    private final ChatService chatService;

    LobbyController(LobbyService lobbyService, UserService userService, ChatService chatService){
        this.lobbyService = lobbyService;
        this.userService = userService;
        this.chatService = chatService;
    }


    /**
     *
     * @param lobbyPostDTO
     * @return - header with location of lobby if the lobby is set to public
     *         - if the lobby is private, returns header with lobby location and privateKey in body
     */
    @CrossOrigin(exposedHeaders = "Location")
    @PostMapping(path = "/lobbies", consumes = "application/json")
    @ResponseStatus(HttpStatus.CREATED)
    @ResponseBody
    public ResponseEntity<Object> createLobby(@RequestBody LobbyPostDTO lobbyPostDTO) {
        Lobby createdLobby;
        // convert API user to internal representation
        Lobby userLobby = DTOMapper.INSTANCE.convertLobbyPostDTOtoEntity(lobbyPostDTO);
        User host = userService.getUser(lobbyPostDTO.getUserId());
        // create lobby
        createdLobby = lobbyService.createLobby(userLobby,host);

        //create chat for lobby
        chatService.createChat(createdLobby.getLobbyId());
        // convert internal representation of user back to API
        URI location = ServletUriComponentsBuilder.fromCurrentRequest().path("/{lobbyId}")
                .buildAndExpand(createdLobby.getLobbyId()).toUri();
        if(createdLobby.isPrivate()) {
            return ResponseEntity.created(location)
                    .body(createdLobby.getPrivateKey());
        }
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


    @PutMapping(path = "/lobbies/{lobbyId}/invitations", consumes = "application/json")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @ResponseBody
    public void invitePlayerToLobby(@PathVariable long lobbyId, @RequestBody InvitePutDTO invitePutDTO){
        Lobby lobby = lobbyService.getLobby(lobbyId);
        User host = userService.getUser(invitePutDTO.getUserId());
        User userToInvite = userService.getUser(invitePutDTO.getUserToInviteId());
        userService.addLobbyInvite(userToInvite,lobby,host);
    }

    @PutMapping(path = "/lobbies/{lobbyId}/acceptances", consumes = "application/json")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public LobbyGetDTO handleLobbyInvite(@PathVariable long lobbyId, @RequestBody LobbyAcceptancePutDTO lobbyAcceptancePutDTO) {
        Lobby lobby = lobbyService.getLobby(lobbyId);
        userService.acceptOrDeclineLobbyInvite(lobby, lobbyAcceptancePutDTO);

        return DTOMapper.INSTANCE.convertEntityToLobbyGetDTO(lobby);
    }

    @GetMapping(path = "/lobbies/{lobbyId}/chat", produces = "application/json")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public String getChatMessages(@PathVariable long lobbyId) {
        Chat chat = chatService.getChat(lobbyId);
        return asJsonString(chat);
    }


    @PutMapping(path = "lobbies/{lobbyId}/joins", consumes = "application/json")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public void joinLobby(@PathVariable long lobbyId, @RequestBody JoinLeavePutDTO joinLeavePutDTO){
        Lobby lobby = lobbyService.getLobby(lobbyId);
        User user = userService.getUser(joinLeavePutDTO.getUserId());
        lobbyService.addUserToLobby(user,lobby);
    }

    @PutMapping(path = "lobbies/{lobbyId}/rageQuits", consumes = "application/json")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public void leaveLobby(@PathVariable long lobbyId, @RequestBody JoinLeavePutDTO joinLeavePutDTO){
        Lobby lobby = lobbyService.getLobby(lobbyId);
        User user = userService.getUser(joinLeavePutDTO.getUserId());
        lobbyService.addUserToLobby(user,lobby);
    }

    private String asJsonString(final Object object) {
        try {
            return new ObjectMapper().writeValueAsString(object);
        }
        catch (JsonProcessingException e) {
            throw new BadRequestException(String.format("The request body could not be created.%s", e.toString()));
        }
    }


}
