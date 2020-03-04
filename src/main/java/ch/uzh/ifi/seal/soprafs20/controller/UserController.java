package ch.uzh.ifi.seal.soprafs20.controller;

import ch.uzh.ifi.seal.soprafs20.entity.User;
import ch.uzh.ifi.seal.soprafs20.exceptions.SopraServiceException;
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

    @GetMapping("/users")
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

    @GetMapping("/users/{id}")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public UserGetDTO getUser(@PathVariable long id) {
        User user = userService.getUser(id);
        return DTOMapper.INSTANCE.convertEntityToUserGetDTO(user);
    }

    @PutMapping("/users/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @ResponseBody
    public void updateUser(@PathVariable long id, @RequestBody UserPutDTO userPutDTO) {
        User user;
        try{
            user = userService.getUser(id);
        }catch (SopraServiceException error){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Couldn't find user.");
        }
        try{
            userService.updateUser(user, userPutDTO);
        }catch (SopraServiceException error){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Error during the update of the user");
        }
    }

    @CrossOrigin(exposedHeaders = "Location")
    @PostMapping("/users")
    @ResponseStatus(HttpStatus.CREATED)
    @ResponseBody
    public ResponseEntity<Object> createUser(@RequestBody UserPostDTO userPostDTO) {
        User createdUser;
        // convert API user to internal representation
        User userInput = DTOMapper.INSTANCE.convertUserPostDTOtoEntity(userPostDTO);

        // create user
        try{
            createdUser = userService.createUser(userInput);
        }
        catch(SopraServiceException error){
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Username already exists");
        }

        // convert internal representation of user back to API
        URI location = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}")
                .buildAndExpand(createdUser.getId()).toUri();
        return ResponseEntity.created(location).build();
    }

    @CrossOrigin(exposedHeaders = "Location")
    @PutMapping("/login")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @ResponseBody
    public ResponseEntity<Object> login(@RequestBody LoginPutDTO loginPutDTO) {
        User user;

        // convert API user to internal representation
        User userInput = DTOMapper.INSTANCE.convertLoginPutDTOtoEntity(loginPutDTO);
        try {
            if (userService.isAlreadyLoggedIn(userInput.getUsername())) {
                throw new ResponseStatusException(HttpStatus.NO_CONTENT, "User is already logged in.");
            }
        }
        catch (NullPointerException error){
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Credentials are incorrect", error);
        }

        try{
            // check password
            user = userService.loginUser(userInput);
        }
        catch (SopraServiceException error){
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Credentials are incorrect", error);
        }
        URI location = ServletUriComponentsBuilder.fromCurrentContextPath().path("/users/{id}")
                .buildAndExpand(user.getId()).toUri();
        return ResponseEntity.created(location).build();
    }

    @PutMapping("/logout")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @ResponseBody
    public void logoutUser(@RequestBody LogoutPutDTO logoutPutDTO){
        // convert API user to internal representation
        User userInput = DTOMapper.INSTANCE.convertLogoutPutDTOtoEntity(logoutPutDTO);

        //logout user
        userService.logoutUser(userInput);
    }

}
