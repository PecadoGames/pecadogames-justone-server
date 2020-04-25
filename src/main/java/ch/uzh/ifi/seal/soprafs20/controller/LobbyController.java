package ch.uzh.ifi.seal.soprafs20.controller;

import ch.uzh.ifi.seal.soprafs20.entity.*;
import ch.uzh.ifi.seal.soprafs20.exceptions.BadRequestException;
import ch.uzh.ifi.seal.soprafs20.exceptions.UnauthorizedException;
import ch.uzh.ifi.seal.soprafs20.rest.dto.*;
import ch.uzh.ifi.seal.soprafs20.rest.mapper.DTOMapper;
import ch.uzh.ifi.seal.soprafs20.service.*;
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
    private final MessageService messageService;
    private final GameService gameService;

    LobbyController(LobbyService lobbyService, UserService userService, ChatService chatService, MessageService messageService, GameService gameService){
        this.lobbyService = lobbyService;
        this.userService = userService;
        this.chatService = chatService;
        this.messageService = messageService;
        this.gameService = gameService;
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

    @PostMapping(path = "lobbies/{lobbyId}", consumes = "application/json")
    @ResponseStatus(HttpStatus.CREATED)
    @ResponseBody
    public ResponseEntity<Object> createGame(@PathVariable long lobbyId, @RequestBody GamePostDTO gamePostDTO) {
        Lobby lobby = lobbyService.getLobby(lobbyId);
        Game createdGame = gameService.createGame(lobby, gamePostDTO);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest().path("/game")
                .build().toUri();
        ResponseEntity<Object> responseEntity = ResponseEntity.created(location).build();
        return ResponseEntity.created(location).build();
    }

    @PutMapping(path = "/lobbies/{lobbyId}", consumes = "application/json")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @ResponseBody
    public void updateLobby(@PathVariable long lobbyId, @RequestBody LobbyPutDTO lobbyPutDTO){
        Lobby lobby = lobbyService.getLobby(lobbyId);
        lobbyService.updateLobby(lobby,lobbyPutDTO);
    }

    @GetMapping(path = "/lobbies/{lobbyId}", produces = "application/json")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public String getLobby(@PathVariable long lobbyId) {
        Lobby lobby = lobbyService.getLobby(lobbyId);
        String json = asJsonString(lobby);
        return asJsonString(lobby);
    }


    @PutMapping(path = "/lobbies/{lobbyId}/invitations", consumes = "application/json")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @ResponseBody
    public void invitePlayerToLobby(@PathVariable long lobbyId, @RequestBody InvitePutDTO invitePutDTO){
        Lobby lobby = lobbyService.getLobby(lobbyId);
        User host = userService.getUser(invitePutDTO.getUserId());
        User userToInvite = userService.getUser(invitePutDTO.getUserToInviteId());
        userToInvite = userService.addLobbyInvite(userToInvite,lobby,host);
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
    public String getChatMessages(@PathVariable long lobbyId,@RequestParam String token) {
        Lobby lobby = lobbyService.getLobby(lobbyId);
        for (User user: lobby.getUsersInLobby()){
            if(user.getToken().equals(token)){
                break;
            } else {
                throw new UnauthorizedException("User not allowed to get messages");
            }
        }
        Chat chat = chatService.getChat(lobbyId);
        return asJsonString(chat);
    }

    @PutMapping(path = "lobbies/{lobbyId}/chat", consumes = "application/json")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @ResponseBody
    public void addChatMessage(@PathVariable long lobbyId, @RequestBody MessagePutDTO messagePutDTO) {
        Message message = DTOMapper.INSTANCE.convertMessagePutDTOtoEntity(messagePutDTO);
        message = messageService.createMessage(message);
        User author  = userService.getUser(messagePutDTO.getUserId());
        Lobby lobby = lobbyService.getLobby(lobbyId);
        chatService.addChatMessage(lobby, author.getToken(), message);
    }

    @PutMapping(path = "/lobbies/{lobbyId}/joins", consumes = "application/json")
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
        lobbyService.removeUserFromLobby(user,lobby);
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
