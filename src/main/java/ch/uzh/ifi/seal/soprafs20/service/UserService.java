package ch.uzh.ifi.seal.soprafs20.service;

import ch.uzh.ifi.seal.soprafs20.constant.UserStatus;
import ch.uzh.ifi.seal.soprafs20.entity.User;
import ch.uzh.ifi.seal.soprafs20.exceptions.SopraServiceException;
import ch.uzh.ifi.seal.soprafs20.repository.UserRepository;
import ch.uzh.ifi.seal.soprafs20.rest.dto.UserPutDTO;
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
        else{
            String baseErrorMessage = "User with ID %d not found.";
            throw new SopraServiceException(String.format(baseErrorMessage, id));
        }
    }

    public User createUser(User newUser) {
        newUser.setToken(UUID.randomUUID().toString());
        newUser.setStatus(UserStatus.ONLINE);
        newUser.setCreationDate();

        checkIfUserExists(newUser);

        // saves the given entity but data is only persisted in the database once flush() is called
        newUser = userRepository.save(newUser);
        userRepository.flush();

        log.debug("Created Information for User: {}", newUser);
        return newUser;
    }

    /**
     * This is a helper method that will check the uniqueness criteria of the username and the name
     * defined in the User entity. The method will do nothing if the input is unique and throw an error otherwise.
     *
     * @param userToBeCreated
     * @throws SopraServiceException
     * @see ch.uzh.ifi.seal.soprafs20.entity.User
     */
    private void checkIfUserExists(User userToBeCreated) {
        User userByUsername = userRepository.findByUsername(userToBeCreated.getUsername());

        String baseErrorMessage = "The %s provided %s not unique. Therefore, the user could not be created!";
        if (userByUsername != null) {
            throw new SopraServiceException(String.format(baseErrorMessage, "username", "is"));
        }
    }

    public void loginUser(User user) {

        User foundUser = userRepository.findByUsername(user.getUsername());

        foundUser.setToken(UUID.randomUUID().toString());
        foundUser.setStatus(UserStatus.ONLINE);

        if(foundUser.getId() == null){
            throw new SopraServiceException("Can't find matching username and password.");
        }

        String enteredPassword = foundUser.getPassword();
        String storedPassword = userRepository.findByUsername(user.getUsername()).getPassword();

        if(!enteredPassword.equals(storedPassword)){
            throw new SopraServiceException("Can't find matching username and password.");
        }

        // saves the given entity but data is only persisted in the database once flush() is called

        log.debug("User {} has logged in.", user);
    }

    public boolean isAlreadyLoggedIn(String username){
        User user = userRepository.findByUsername(username);
        return user.getStatus() == UserStatus.ONLINE;
    }

    public void logoutUser(User findUser){
        User user;
        Long id = findUser.getId();
        Optional<User> optional = userRepository.findById(id);
        if(optional.isPresent()){
            user = optional.get();
            if(user.getToken().equals(findUser.getToken())){
                user.setStatus(UserStatus.OFFLINE);
                user.setToken(null);
            }
            else{
                throw new SopraServiceException("Wrong token.");
            }
        }
        else{
            throw new SopraServiceException("Can't find matching user.");
        }
    }

    public void updateUser(User user, UserPutDTO receivedValues){
        if(userRepository.findByUsername(receivedValues.getUsername()) != null){
            throw new SopraServiceException("This username already exists.");
        }
        if(!user.getToken().equals(receivedValues.getToken())){
            throw new SopraServiceException("Wrong token.");
        }
        if(receivedValues.getUsername() != null){user.setUsername(receivedValues.getUsername());}
        if(receivedValues.getPassword() != null){user.setPassword(receivedValues.getPassword());}
        if(receivedValues.getBirthday() != null){user.setBirthday(receivedValues.getBirthday());}
    }
}
