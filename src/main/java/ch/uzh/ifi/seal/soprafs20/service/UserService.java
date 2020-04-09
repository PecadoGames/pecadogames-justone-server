package ch.uzh.ifi.seal.soprafs20.service;

import ch.uzh.ifi.seal.soprafs20.constant.UserStatus;
import ch.uzh.ifi.seal.soprafs20.entity.Lobby;
import ch.uzh.ifi.seal.soprafs20.entity.User;
import ch.uzh.ifi.seal.soprafs20.exceptions.*;
import ch.uzh.ifi.seal.soprafs20.repository.UserRepository;
import ch.uzh.ifi.seal.soprafs20.rest.dto.FriendPutDTO;
import ch.uzh.ifi.seal.soprafs20.rest.dto.UserPutDTO;
import com.fasterxml.jackson.core.JsonParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * User Service
 * This class is the "worker" and responsible for all functionality related to the user
 * (e.g., it creates, modifies, deletes, finds). The result will be passed back to the caller.
 */
@Service
@Transactional
public class UserService {

    private final Logger log = LoggerFactory.getLogger(UserService.class);

    private final UserRepository userRepository;

    @Autowired
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public List<User> getUsers() {
        return this.userRepository.findAll();
    }

    public User getUser(Long id){
        User user;
        Optional<User> optional = userRepository.findById(id);
        if(optional.isPresent()){
            user = optional.get();
            return user;
        }
        else{throw new NotFoundException("Couldn't find user.");

        }
    }
    public User createUser(User newUser) {
        newUser.setToken(UUID.randomUUID().toString());
        newUser.setStatus(UserStatus.OFFLINE);
        newUser.setCreationDate();
        checkUsername(newUser.getUsername());
        checkIfUserExists(newUser);

        // saves the given entity but data is only persisted in the database once flush() is called
        newUser = userRepository.save(newUser);
        userRepository.flush();

        log.debug("Created Information for User: {}", newUser);
        return newUser;
    }

    public void loginUser(User user) {
        User foundUser = userRepository.findByUsername(user.getUsername());
        if(foundUser == null){
            throw new NotFoundException("user credentials are incorrect!");
        }

        String enteredPassword = user.getPassword();
        String storedPassword = userRepository.findByUsername(user.getUsername()).getPassword();

        if(!enteredPassword.equals(storedPassword)){
            throw new NotFoundException("user credentials are incorrect!");
        }
        isAlreadyLoggedIn(foundUser);

        foundUser.setToken(UUID.randomUUID().toString());
        foundUser.setStatus(UserStatus.ONLINE);
        log.debug("User {} has logged in.", user);
    }

    public void logoutUser(User findUser){
        User user;
        Long id = findUser.getId();
        Optional<User> optional = userRepository.findById(id);
        if(optional.isPresent()){
            user = optional.get();
            if(user.getStatus() == UserStatus.ONLINE && user.getToken().equals(findUser.getToken())){
                user.setStatus(UserStatus.OFFLINE);
                user.setToken(null);
                log.debug("User {} has logged out.", user);
            }
            else {
                throw new UnauthorizedException("Logout is not allowed!");
            }
        }
        else {
            String message = String.format("user with ID %s not found!", id.toString());
            throw new NotFoundException(message);
        }
    }

    public void updateUser(User user, UserPutDTO receivedValues) throws JsonParseException {

        if(userRepository.findByUsername(receivedValues.getUsername()) != null){
            throw new ConflictException("This username already exists.");
        }
        if(!user.getToken().equals(receivedValues.getToken())){
            throw new UnauthorizedException("You are not allowed to change this user's information!");
        }

        if (receivedValues.getBirthday() != null) {
            user.setBirthday(receivedValues.getBirthday());
        }
        if(receivedValues.getUsername()!=null){
            checkUsername(receivedValues.getUsername());
            user.setUsername(receivedValues.getUsername());
        }
        userRepository.save(user);

    }

    public void addFriendRequest(User receiver, User sender) {
        if(userRepository.findByUsername(receiver.getUsername()) == null) {
            String exceptionMessage = "User with id %s does not exist!";
            throw new NotFoundException(String.format(exceptionMessage, receiver.getId().toString()));
        }
        User user = userRepository.findById(sender.getId()).get();
        if(!sender.getToken().equals(user.getToken())) {
            throw new UnauthorizedException("You are not allowed to send a friend request!");
        }
        receiver.setFriendRequests(sender);
    }

    public void acceptOrDeclineFriendRequest(User receiver, FriendPutDTO friendPutDTO) {
        User sender = getUser(friendPutDTO.getSenderID());
        if(receiver.getFriendRequests().contains(sender)) {
            if(friendPutDTO.getAccepted()){
                receiver.setFriendList(sender);
                sender.setFriendList(receiver);
            }
            receiver.getFriendRequests().remove(sender);
        }
        else{
            throw new NotFoundException(String.format("No friend request from user with id %s was found!", sender.getId().toString()));
        }
    }

    public void addLobbyInvite(User receiver, Lobby lobby, User sender) {
        if(!sender.getToken().equals(lobby.getToken())){
            throw new UnauthorizedException("User is not authorized to send lobby invites");
        }
        if(sender.getId().equals(receiver.getId())){
            throw new ConflictException("Cannot invite yourself to the lobby");
        }
        receiver.setLobbyInvites(lobby);
    }

    private void checkIfUserExists(User userToBeCreated) {
        User userByUsername = userRepository.findByUsername(userToBeCreated.getUsername());
        String baseErrorMessage = "The %s provided %s not unique. Therefore, the user could not be created!";
        if (userByUsername != null) {
            throw new ConflictException(String.format(baseErrorMessage, "username", "is"));
        }
    }

    public void isAlreadyLoggedIn(User user){
        if (user.getStatus() == UserStatus.ONLINE){
            throw new NoContentException("User already logged in!");
        }
    }

    public void checkUsername(String username){
        if (username.contains(" ") || username.isEmpty() || username.isBlank() || username.length() > 20 || username.trim().isEmpty() || !username.matches("[a-zA-Z_0-9]*")) {
            throw new NotAcceptableException("This is an invalid username. Please choose a username with a maximum length of 20 characters consisting of letters, digits and underscores..");
        }
    }
}
