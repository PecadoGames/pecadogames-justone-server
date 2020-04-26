package ch.uzh.ifi.seal.soprafs20.controller;

import ch.uzh.ifi.seal.soprafs20.entity.Lobby;
import ch.uzh.ifi.seal.soprafs20.entity.User;
import ch.uzh.ifi.seal.soprafs20.exceptions.BadRequestException;
import ch.uzh.ifi.seal.soprafs20.exceptions.UnauthorizedException;
import ch.uzh.ifi.seal.soprafs20.rest.dto.*;
import ch.uzh.ifi.seal.soprafs20.rest.mapper.DTOMapper;
import ch.uzh.ifi.seal.soprafs20.service.UserService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * User Controller
 * This class is responsible for handling all REST request that are related to the user.
 * The controller will receive the request and delegate the execution to the UserService and finally return the result.
 */
@RestController
public class UserController {

    private final UserService userService;

    UserController(UserService userService) {
        this.userService = userService;
    }

    @CrossOrigin(exposedHeaders = "Location")
    @PostMapping(path = "/users", consumes = "application/json")
    @ResponseStatus(HttpStatus.CREATED)
    @ResponseBody
    public ResponseEntity<Object> createUser(@RequestBody UserPostDTO userPostDTO) {
        User createdUser;
        // convert API user to internal representation
        User userInput = DTOMapper.INSTANCE.convertUserPostDTOtoEntity(userPostDTO);

        // create user
        createdUser = userService.createUser(userInput);

        // convert internal representation of user back to API
        URI location = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}")
                .buildAndExpand(createdUser.getId()).toUri();
        return ResponseEntity.created(location).build();
    }

    @GetMapping(path = "/users", produces = "application/json")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public List<UserGetDTO> getAllUsers() {
        // fetch all users in the internal representation
        List<User> users = userService.getUsers();
        List<UserGetDTO> userGetDTOs = new ArrayList<>();

        // convert each user to the API representation
        for (User user : users) {
            userGetDTOs.add(DTOMapper.INSTANCE.convertEntityToUserGetDTO(user));
        }
        return userGetDTOs;
    }

    @GetMapping(path = "/users/{id}", produces = "application/json")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public UserGetDTO getUser(@PathVariable long id) {
        User user = userService.getUser(id);
        return DTOMapper.INSTANCE.convertEntityToUserGetDTO(user);
    }

    @PutMapping(path = "/users/{id}", consumes = "application/json")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @ResponseBody
    public void updateUser(@PathVariable long id, @RequestBody UserPutDTO userPutDTO) throws HttpMessageNotReadableException, IOException {
        User user = userService.getUser(id);
        userService.updateUser(user, userPutDTO);
    }

    @GetMapping(path = "/users/{id}/friendRequests",produces = "application/json")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public List<RequestGetDTO> getFriendRequests(@PathVariable long id, @RequestParam("token") String token) {
        User user = userService.getUser(id);
        if(!user.getToken().equals(token)){
            throw new UnauthorizedException("You are not authorized to get this users friend requests");
        }
        Set<User> requests = user.getFriendRequests();
        List<RequestGetDTO> requestGetDTOs = new ArrayList<>();

        for (User request : requests) {
            requestGetDTOs.add(DTOMapper.INSTANCE.convertEntityToRequestGetDTO(request));
        }
        return requestGetDTOs;
    }

    @PutMapping(path = "/users/{id}/friendRequests", consumes = "application/json")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @ResponseBody
    public void sendFriendRequest(@PathVariable long id, @RequestBody RequestPutDTO requestPutDTO)  {
        User receiver = userService.getUser(id);
        userService.addFriendRequest(receiver, requestPutDTO);
    }

    @GetMapping(path = "users/{id}/friends", produces = "application/json")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public List<RequestGetDTO> getFriends(@PathVariable long id,@RequestParam String token) {
        User user = userService.getUser(id);
        if(!user.getToken().equals(token)){
            throw new UnauthorizedException("You are not authorized to get this users friends");
        }
        Set<User> friends = user.getFriendList();
        List<RequestGetDTO> requestGetDTOs = new ArrayList<>();

        for (User friend : friends) {
            requestGetDTOs.add(DTOMapper.INSTANCE.convertEntityToRequestGetDTO(friend));
        }
        return requestGetDTOs;
    }

    @PutMapping(path = "/users/{id}/friends", consumes = "application/json")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @ResponseBody
    public void handleFriendRequest(@PathVariable long id, @RequestBody FriendPutDTO friendPutDTO) {
        User receiver = userService.getUser(id);
        userService.acceptOrDeclineFriendRequest(receiver, friendPutDTO);
    }

    @CrossOrigin(exposedHeaders = "Location")
    @PutMapping(path = "/login", consumes = "application/json")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public LoginPutDTO login(@RequestBody LoginPutDTO loginPutDTO) {

        // convert API user to internal representation
        User userInput = DTOMapper.INSTANCE.convertLoginPutDTOtoEntity(loginPutDTO);

        // check password
        User user = userService.loginUser(userInput);

        return DTOMapper.INSTANCE.convertEntityToLoginPutDTO(user);

    }

    @PutMapping(path = "/logout", consumes = "application/json")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @ResponseBody
    public void logout(@RequestBody LogoutPutDTO logoutPutDTO){
        // convert API user to internal representation
        User userInput = DTOMapper.INSTANCE.convertLogoutPutDTOtoEntity(logoutPutDTO);

        //logout user
        userService.logoutUser(userInput);
    }


    @GetMapping(path = "/users/{userId}/invitations")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public List<InviteGetDTO> getLobbyInvites(@PathVariable long userId){
        User user = userService.getUser(userId);
        Set<Lobby> invites = user.getLobbyInvites();
        List<InviteGetDTO> lobbies = new ArrayList<>();
        for(Lobby lobby : invites){
            lobbies.add(DTOMapper.INSTANCE.convertEntityToInviteGetDTO(lobby));
        }
        return lobbies;
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
