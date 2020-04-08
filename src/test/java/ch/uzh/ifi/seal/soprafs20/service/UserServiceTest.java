package ch.uzh.ifi.seal.soprafs20.service;

import ch.uzh.ifi.seal.soprafs20.constant.UserStatus;
import ch.uzh.ifi.seal.soprafs20.entity.User;
import ch.uzh.ifi.seal.soprafs20.exceptions.*;
import ch.uzh.ifi.seal.soprafs20.repository.UserRepository;
import ch.uzh.ifi.seal.soprafs20.rest.dto.FriendPutDTO;
import ch.uzh.ifi.seal.soprafs20.rest.dto.UserPutDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.text.SimpleDateFormat;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

public class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    private User testUser;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.initMocks(this);

        // given
        testUser = new User();
        testUser.setId(1L);
        testUser.setUsername("testUsername");
        testUser.setPassword("test");
        testUser.setToken("testToken");

        // when -> any object is being save in the userRepository -> return the dummy testUser
        Mockito.when(userRepository.save(Mockito.any())).thenReturn(testUser);
        Mockito.when(userRepository.findById(Mockito.any())).thenReturn(java.util.Optional.ofNullable(testUser));
    }

    @Test
    public void createUser_validInputs_success() {
        // when -> any object is being save in the userRepository -> return the dummy testUser
        User createdUser = userService.createUser(testUser);

        // then
        Mockito.verify(userRepository, Mockito.times(1)).save(Mockito.any());

        assertEquals(testUser.getId(), createdUser.getId());
        assertEquals(testUser.getUsername(), createdUser.getUsername());
        assertNotNull(createdUser.getToken());
        assertEquals(UserStatus.OFFLINE, createdUser.getStatus());
    }

    @Test
    public void createUser_duplicateName_throwsException() {
        // given -> a first user has already been created
        userService.createUser(testUser);

        // when -> setup additional mocks for UserRepository
        Mockito.when(userRepository.findByUsername(Mockito.any())).thenReturn(testUser);

        // then -> attempt to create second user with same user -> check that an error is thrown
        String exceptionMessage = "The username provided is not unique. Therefore, the user could not be created!";
        ConflictException exception = assertThrows(ConflictException.class, () -> userService.createUser(testUser), exceptionMessage);
        assertEquals(exceptionMessage, exception.getMessage());
    }

    @Test
    public void getUser_wrongId_throwsException() {
        // given -> a first user has already been created
        userService.createUser(testUser);

        // when -> setup additional mocks for UserRepository
        Mockito.when(userRepository.findById(Mockito.any())).thenReturn(null);

        // then -> attempt to create second user with same user -> check that an error is thrown
        String exceptionMessage = "null";
        NullPointerException exception = assertThrows(NullPointerException.class, () -> userService.getUser(1L));

    }

    @Test
    public void getUser_validInputs_success() {
        // when -> any object is being save in the userRepository -> return the dummy testUser
        User createdUser = userService.createUser(testUser);
        User foundUser = userService.getUser(testUser.getId());

        // then
        Mockito.verify(userRepository, Mockito.times(1)).findById(Mockito.any());

        assertEquals(foundUser.getId(), createdUser.getId());
        assertEquals(foundUser.getUsername(), createdUser.getUsername());
        assertNotNull(createdUser.getToken());
        assertEquals(UserStatus.OFFLINE, createdUser.getStatus());
    }

    @Test
    public void loginUser_validInputs_success() {
        // when -> any object is being save in the userRepository -> return the dummy testUser
        Mockito.when(userRepository.findByUsername(Mockito.any())).thenReturn(testUser);
        testUser.setStatus(UserStatus.OFFLINE);

        userService.loginUser(testUser);
        assertEquals(UserStatus.ONLINE, testUser.getStatus());
    }

    @Test
    public void loginUser_invalidInput_throwsException() {
        // when -> any object is being save in the userRepository -> return the dummy testUser

        Mockito.when(userRepository.findByUsername(Mockito.any())).thenReturn(null);

        String exceptionMessage = "user credentials are incorrect!";
        NotFoundException exception = assertThrows(NotFoundException.class, () -> userService.loginUser(testUser), exceptionMessage);
        assertEquals(exceptionMessage, exception.getMessage());
    }

    @Test
    public void loginUser_invalidCredentials_throwsException() {
        User createdUser = userService.createUser(testUser);
        createdUser.setStatus(UserStatus.OFFLINE);
        User falseUser = new User();
        falseUser.setUsername("testUsername");
        falseUser.setPassword("password");

        Mockito.when(userRepository.findByUsername(Mockito.any())).thenReturn(falseUser);
        String exceptionMessage = "user credentials are incorrect!";
        NotFoundException exception = assertThrows(NotFoundException.class, () -> userService.loginUser(testUser), exceptionMessage);
        assertEquals(exceptionMessage, exception.getMessage());
        assertEquals(testUser.getStatus(), UserStatus.OFFLINE);
    }

    @Test
    public void logoutUser_invalidInput_throwsException() {

        Mockito.when(userRepository.findById(Mockito.any())).thenReturn(Optional.empty());

        String exceptionMessage = "not found";
        String exceptionMessage2 = "user with ID";
        NotFoundException exception = assertThrows(NotFoundException.class, () -> userService.logoutUser(testUser), exceptionMessage);
        assertTrue(exception.getMessage().contains(exceptionMessage) && exception.getMessage().contains(exceptionMessage2));
    }

    @Test
    public void logoutUser_invalidToken_throwsException() {
        // when -> any object is being save in the userRepository -> return the dummy testUser
        User testUser2 = new User();
        testUser2.setId(1L);
        testUser2.setToken("falseToken");
        testUser2.setStatus(UserStatus.ONLINE);

        Mockito.when(userRepository.findById(Mockito.any())).thenReturn(java.util.Optional.ofNullable(testUser));

        String exceptionMessage = "Logout is not allowed!";
        UnauthorizedException exception = assertThrows(UnauthorizedException.class, () -> userService.logoutUser(testUser2), exceptionMessage);
        assertEquals(exceptionMessage, exception.getMessage());
        assertEquals(testUser2.getStatus(), UserStatus.ONLINE);
    }

    @Test
    public void updateUser_validInput_success() throws Exception {

        UserPutDTO userPutDTO = new UserPutDTO();
        userPutDTO.setUsername("changedUsername");
        userPutDTO.setBirthday(new SimpleDateFormat( "dd.MM.yyyy" ).parse( "20.05.2010" ));
        userPutDTO.setToken("testToken");

        userService.updateUser(testUser, userPutDTO);

        assertEquals(testUser.getUsername(), userPutDTO.getUsername());
        assertEquals(testUser.getBirthday(), userPutDTO.getBirthday());
    }

    @Test
    public void updateUser_invalidToken_throwsException() {
        UserPutDTO userPutDTO = new UserPutDTO();
        userPutDTO.setUsername("changedUsername");
        userPutDTO.setToken("wrongToken");

        String exceptionMessage = "You are not allowed to change this user's information";
        UnauthorizedException exception = assertThrows(UnauthorizedException.class, () -> userService.updateUser(testUser, userPutDTO), exceptionMessage);
        assertTrue(exception.getMessage().contains(exceptionMessage));

        assertEquals(testUser.getUsername(), "testUsername");
    }

    @Test
    public void updateUser_userNameTaken_throwsException() {
        User user = new User();
        user.setUsername("anyUsername");
        UserPutDTO userPutDTO = new UserPutDTO();
        userPutDTO.setUsername("anyUsername");
        userPutDTO.setToken("testToken");

        Mockito.when(userRepository.findByUsername(Mockito.any())).thenReturn(user);

        String exceptionMessage = "This username already exists";
        ConflictException exception = assertThrows(ConflictException.class, () -> userService.updateUser(testUser, userPutDTO), exceptionMessage);
        assertTrue(exception.getMessage().contains(exceptionMessage));

        assertEquals(testUser.getUsername(), "testUsername");
    }

    @Test
    public void updateUser_newUsernameTooLong_throwsException() {
        UserPutDTO userPutDTO = new UserPutDTO();
        userPutDTO.setUsername("thisUsernameIsTooLong");
        userPutDTO.setToken("testToken");

        String exceptionMessage = "This is an invalid username";
        NotAcceptableException exception = assertThrows(NotAcceptableException.class, () -> userService.updateUser(testUser, userPutDTO), exceptionMessage);
        assertTrue(exception.getMessage().contains(exceptionMessage));

        assertEquals(testUser.getUsername(), "testUsername");
    }

    @Test
    public void updateUser_newUsernameInvalid_throwsException() {
        UserPutDTO userPutDTO = new UserPutDTO();
        userPutDTO.setUsername("invalidU$ername");
        userPutDTO.setToken("testToken");

        String exceptionMessage = "This is an invalid username";
        NotAcceptableException exception = assertThrows(NotAcceptableException.class, () -> userService.updateUser(testUser, userPutDTO), exceptionMessage);
        assertTrue(exception.getMessage().contains(exceptionMessage));

        assertEquals(testUser.getUsername(), "testUsername");
    }

    @Test
    public void addFriendRequest_validInput_success() {
        User testUser2 = new User();
        testUser2.setId(2L);
        testUser2.setToken("testToken2");

        Mockito.when(userRepository.findByUsername(Mockito.any())).thenReturn(testUser);
        Mockito.when(userRepository.findById(Mockito.any())).thenReturn(java.util.Optional.ofNullable(testUser2));
        userService.addFriendRequest(testUser, testUser2);

        assertTrue(testUser.getFriendRequests().contains(testUser2));
        assertFalse(testUser2.getFriendRequests().contains(testUser));
    }

    @Test
    public void addFriendRequest_invalidInput() {
        User testUser2 = new User();
        testUser2.setId(2L);
        testUser2.setToken("testToken2");

        User testUser_wrongToken = new User();
        testUser_wrongToken.setId(2L);
        testUser_wrongToken.setToken("wrongToken");

        Mockito.when(userRepository.findByUsername(Mockito.any())).thenReturn(testUser);
        Mockito.when(userRepository.findById(Mockito.any())).thenReturn(java.util.Optional.ofNullable(testUser2));

        String exceptionMessage = "not allowed to send a friend request";
        UnauthorizedException exception = assertThrows(UnauthorizedException.class, () -> userService.addFriendRequest(testUser, testUser_wrongToken), exceptionMessage);
        assertTrue(exception.getMessage().contains(exceptionMessage));
        assertFalse(testUser.getFriendRequests().contains(testUser2));
    }

    @Test
    public void acceptFriendRequest_validInput_success() {
        User testUser2 = new User();
        testUser2.setId(2L);
        testUser2.setToken("testToken2");

        testUser.setFriendRequests(testUser2);

        FriendPutDTO friendPutDTO = new FriendPutDTO();
        friendPutDTO.setAccepted(true);
        friendPutDTO.setToken(testUser2.getToken());
        friendPutDTO.setSenderID(testUser2.getId());

        Mockito.when(userRepository.findByUsername(Mockito.any())).thenReturn(testUser);
        Mockito.when(userRepository.findById(Mockito.any())).thenReturn(java.util.Optional.ofNullable(testUser2));

        userService.acceptOrDeclineFriendRequest(testUser, friendPutDTO);

        assertTrue(testUser.getFriendList().contains(testUser2));
        assertTrue(testUser2.getFriendList().contains(testUser));
        assertFalse(testUser.getFriendRequests().contains(testUser2));
    }

    @Test
    public void declineFriendRequest_validInput_success() {
        User testUser2 = new User();
        testUser2.setId(2L);
        testUser2.setToken("testToken2");

        testUser.setFriendRequests(testUser2);

        FriendPutDTO friendPutDTO = new FriendPutDTO();
        friendPutDTO.setAccepted(false);
        friendPutDTO.setToken(testUser2.getToken());
        friendPutDTO.setSenderID(testUser2.getId());

        Mockito.when(userRepository.findByUsername(Mockito.any())).thenReturn(testUser);
        Mockito.when(userRepository.findById(Mockito.any())).thenReturn(java.util.Optional.ofNullable(testUser2));

        userService.acceptOrDeclineFriendRequest(testUser, friendPutDTO);

        assertFalse(testUser.getFriendList().contains(testUser2));
        assertFalse(testUser2.getFriendList().contains(testUser));
        assertFalse(testUser.getFriendRequests().contains(testUser2));
    }

    @Test
    public void handleFriendRequest_invalidInput_throwsException() {
        FriendPutDTO friendPutDTO = new FriendPutDTO();
        friendPutDTO.setAccepted(false);
        friendPutDTO.setToken("anyToken");
        friendPutDTO.setSenderID(5L);

        User testUser2 = new User();
        testUser2.setId(2L);
        testUser2.setToken("testToken2");

        Mockito.when(userRepository.findByUsername(Mockito.any())).thenReturn(testUser);
        Mockito.when(userRepository.findById(Mockito.any())).thenReturn(java.util.Optional.ofNullable(testUser2));

        String exceptionMessage = "friend request from user";
        NotFoundException exception = assertThrows(NotFoundException.class, () -> userService.acceptOrDeclineFriendRequest(testUser, friendPutDTO), exceptionMessage);
        assertTrue(exception.getMessage().contains(exceptionMessage));

    }

    @Test
    public void testAlreadyLoggedIn() {
        // when -> any object is being save in the userRepository -> return the dummy testUser
        User createdUser = userService.createUser(testUser);
        createdUser.setStatus(UserStatus.ONLINE);
        Mockito.when(userRepository.findByUsername(Mockito.any())).thenReturn(testUser);

        assertThrows(NoContentException.class, () -> userService.isAlreadyLoggedIn(createdUser));

    }

}
