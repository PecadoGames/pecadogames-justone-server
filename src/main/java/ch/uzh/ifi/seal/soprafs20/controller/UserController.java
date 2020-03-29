package ch.uzh.ifi.seal.soprafs20.controller;

import ch.uzh.ifi.seal.soprafs20.entity.User;
import ch.uzh.ifi.seal.soprafs20.exceptions.BadRequestException;
import ch.uzh.ifi.seal.soprafs20.exceptions.NotFoundException;
import ch.uzh.ifi.seal.soprafs20.rest.dto.*;
import ch.uzh.ifi.seal.soprafs20.rest.mapper.DTOMapper;
import ch.uzh.ifi.seal.soprafs20.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

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
    public void updateUser(@PathVariable long id, @RequestBody UserPutDTO userPutDTO) {
        User user;
        user = userService.getUser(id);
        userService.updateUser(user, userPutDTO);
    }

    @PutMapping(path = "/login", consumes = "application/json")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public void login(@RequestBody LoginPutDTO loginPutDTO) {

        // convert API user to internal representation
        User userInput = DTOMapper.INSTANCE.convertLoginPutDTOtoEntity(loginPutDTO);

        // check password
        if(!userService.loginUser(userInput)) {
            throw new NotFoundException("user credentials are incorrect!");
        }

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

}
