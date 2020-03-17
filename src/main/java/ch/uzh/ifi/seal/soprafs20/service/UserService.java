package ch.uzh.ifi.seal.soprafs20.service;

import ch.uzh.ifi.seal.soprafs20.constant.UserStatus;
import ch.uzh.ifi.seal.soprafs20.entity.User;
import ch.uzh.ifi.seal.soprafs20.exceptions.*;
import ch.uzh.ifi.seal.soprafs20.repository.UserRepository;
import ch.uzh.ifi.seal.soprafs20.rest.dto.UserPutDTO;
import ch.uzh.ifi.seal.soprafs20.rest.mapper.DTOMapper;
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
        newUser.setStatus(UserStatus.ONLINE);
        newUser.setCreationDate();
        checkUsername(newUser.getUsername());
        checkIfUserExists(newUser);

        // saves the given entity but data is only persisted in the database once flush() is called
        newUser = userRepository.save(newUser);
        userRepository.flush();

        log.debug("Created Information for User: {}", newUser);
        return newUser;
    }

    private boolean checkIfUserExists(User userToBeCreated) {
        User userByUsername = userRepository.findByUsername(userToBeCreated.getUsername());
        String baseErrorMessage = "The %s provided %s not unique. Therefore, the user could not be created!";
        if (userByUsername != null) {
            throw new ConflictException(String.format(baseErrorMessage, "username", "is"));
        }
        return true;
    }

    public User loginUser(User user) {
        User foundUser = userRepository.findByUsername(user.getUsername());
        if(foundUser == null){
            throw new NotFoundException("Can't find matching username and password.");
        }

        String enteredPassword = user.getPassword();
        String storedPassword = userRepository.findByUsername(user.getUsername()).getPassword();

        if(!enteredPassword.equals(storedPassword)){
            throw new NotFoundException("Can't find matching username and password.");
        }
        isAlreadyLoggedIn(foundUser);

        foundUser.setToken(UUID.randomUUID().toString());
        foundUser.setStatus(UserStatus.ONLINE);
        log.debug("User {} has logged in.", user);

        return foundUser;
    }

    public boolean isAlreadyLoggedIn(User user){
        if (user.getStatus() == UserStatus.ONLINE){
            throw new NoContentException("User already logged in!");
        }
        return true;
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
            }
        }
    }


    public void updateUser(User user, UserPutDTO receivedValues){

        if(userRepository.findByUsername(receivedValues.getUsername()) != null){
            throw new ConflictException("This username already exists.");
        }
        if(!user.getToken().equals(receivedValues.getToken())){
            throw new UnauthorizedException("You are not allowed to change this user!.");
        }
        if(receivedValues.getUsername()!=null){
        checkUsername(receivedValues.getUsername());}
        if(receivedValues.getUsername() != null){user.setUsername(receivedValues.getUsername());}
        if(receivedValues.getBirthday() != null){user.setBirthday(receivedValues.getBirthday());}
    }

    public void checkUsername(String username){
        if (username.contains(" ") || username.isEmpty() || username.length() > 20 || username.trim().isEmpty() ) {
            throw new NotAcceptableException("This is an invalid username. Please max. 20 digits and no spaces.");
        }

    }
}
