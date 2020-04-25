package ch.uzh.ifi.seal.soprafs20.service;

import ch.uzh.ifi.seal.soprafs20.constant.UserStatus;
import ch.uzh.ifi.seal.soprafs20.entity.Lobby;
import ch.uzh.ifi.seal.soprafs20.entity.Player;
import ch.uzh.ifi.seal.soprafs20.entity.User;
import ch.uzh.ifi.seal.soprafs20.exceptions.*;
import ch.uzh.ifi.seal.soprafs20.repository.UserRepository;
import ch.uzh.ifi.seal.soprafs20.rest.dto.FriendPutDTO;
import ch.uzh.ifi.seal.soprafs20.rest.dto.LobbyAcceptancePutDTO;
import ch.uzh.ifi.seal.soprafs20.rest.dto.RequestPutDTO;
import ch.uzh.ifi.seal.soprafs20.rest.dto.UserPutDTO;
import com.fasterxml.jackson.core.JsonParseException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

public class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;
    @InjectMocks
    private PlayerService playerService;

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
        assertThrows(ConflictException.class, () -> userService.createUser(testUser));
    }

    @Test
    public void getUser_wrongId_throwsException() {
        // given -> a first user has already been created
        userService.createUser(testUser);

        // when -> setup additional mocks for UserRepository
        Mockito.when(userRepository.findById(Mockito.any())).thenReturn(Optional.empty());

        // then -> attempt to create second user with same user -> check that an error is thrown
        assertThrows(NotFoundException.class, () -> userService.getUser(1L));
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
        testUser.setStatus(UserStatus.OFFLINE);

        Mockito.when(userRepository.findByUsername(Mockito.any())).thenReturn(testUser);
        userService.loginUser(testUser);

        assertEquals(UserStatus.ONLINE, testUser.getStatus());
    }

    @Test
    public void loginUser_invalidInput_throwsException() {
        Mockito.when(userRepository.findByUsername(Mockito.any())).thenReturn(null);

        assertThrows(NotFoundException.class, () -> userService.loginUser(testUser));
    }

    @Test
    public void loginUser_invalidCredentials_throwsException() {
        User createdUser = userService.createUser(testUser);
        createdUser.setStatus(UserStatus.OFFLINE);
        User falseUser = new User();
        falseUser.setUsername("testUsername");
        falseUser.setPassword("password");

        Mockito.when(userRepository.findByUsername(Mockito.any())).thenReturn(falseUser);
        assertThrows(NotFoundException.class, () -> userService.loginUser(testUser));
        assertEquals(testUser.getStatus(), UserStatus.OFFLINE);
    }

    @Test
    public void loginUser_whenUserAlreadyLoggedIn_throwsException() {
        testUser.setStatus(UserStatus.ONLINE);
        Mockito.when(userRepository.findByUsername(Mockito.any())).thenReturn(testUser);

        assertThrows(NoContentException.class, () -> userService.loginUser(testUser));
    }

    @Test
    public void logoutUser_validInput_success() {
        testUser.setStatus(UserStatus.ONLINE);

        userService.logoutUser(testUser);
        assertEquals(testUser.getStatus(), UserStatus.OFFLINE);
        assertNull(testUser.getToken());
    }

    @Test
    public void logoutUser_userIsOffline_throwsException() {
        testUser.setStatus(UserStatus.OFFLINE);

        assertThrows(UnauthorizedException.class, () -> userService.logoutUser(testUser));
    }

    @Test
    public void logoutUser_invalidToken_throwsException() {
        // when -> any object is being save in the userRepository -> return the dummy testUser
        User testUser2 = new User();
        testUser2.setId(1L);
        testUser2.setToken("falseToken");
        testUser2.setStatus(UserStatus.ONLINE);

        Mockito.when(userRepository.findById(Mockito.any())).thenReturn(java.util.Optional.ofNullable(testUser));

        assertThrows(UnauthorizedException.class, () -> userService.logoutUser(testUser2));
        assertEquals(testUser2.getStatus(), UserStatus.ONLINE);
    }

    @Test
    public void updateUser_validInput_success() throws Exception {
        UserPutDTO userPutDTO = new UserPutDTO();
        userPutDTO.setUsername("changedUsername");
        userPutDTO.setBirthday(new SimpleDateFormat( "dd.MM.yyyy" ).parse( "20.05.2010" ));
        userPutDTO.setToken("testToken");

        Mockito.when(userRepository.findByUsername(Mockito.any())).thenReturn(null);
        userService.updateUser(testUser, userPutDTO);

        assertEquals(testUser.getUsername(), userPutDTO.getUsername());
        assertEquals(testUser.getBirthday(), userPutDTO.getBirthday());
    }

    @Test
    public void updateUser_validNewUsername_success() throws JsonParseException {
        UserPutDTO userPutDTO = new UserPutDTO();
        userPutDTO.setUsername("changedUsername");
        userPutDTO.setToken("testToken");

        Mockito.when(userRepository.findByUsername(Mockito.any())).thenReturn(null);
        userService.updateUser(testUser, userPutDTO);

        assertEquals(testUser.getUsername(), userPutDTO.getUsername());
    }

    @Test
    public void updateUser_validBirthday_success() throws JsonParseException, ParseException {
        UserPutDTO userPutDTO = new UserPutDTO();
        userPutDTO.setBirthday(new SimpleDateFormat( "dd.MM.yyyy" ).parse( "20.05.2010" ));
        userPutDTO.setToken("testToken");

        Mockito.when(userRepository.findByUsername(Mockito.any())).thenReturn(null);
        userService.updateUser(testUser, userPutDTO);

        assertEquals(testUser.getBirthday(), userPutDTO.getBirthday());
    }

    @Test
    public void updateUser_invalidToken_throwsException() {
        UserPutDTO userPutDTO = new UserPutDTO();
        userPutDTO.setUsername("changedUsername");
        userPutDTO.setToken("wrongToken");

        assertThrows(UnauthorizedException.class, () -> userService.updateUser(testUser, userPutDTO));
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

        assertThrows(ConflictException.class, () -> userService.updateUser(testUser, userPutDTO));
        assertEquals(testUser.getUsername(), "testUsername");
    }

    @Test
    public void updateUser_newUsernameTooLong_throwsException() {
        UserPutDTO userPutDTO = new UserPutDTO();
        userPutDTO.setUsername("thisUsernameIsTooLong");
        userPutDTO.setToken("testToken");

        assertThrows(NotAcceptableException.class, () -> userService.updateUser(testUser, userPutDTO));
        assertEquals(testUser.getUsername(), "testUsername");
    }

    @Test
    public void updateUser_newUsernameInvalid_throwsException() {
        UserPutDTO userPutDTO = new UserPutDTO();
        userPutDTO.setUsername("invalidU$ername");
        userPutDTO.setToken("testToken");

        assertThrows(NotAcceptableException.class, () -> userService.updateUser(testUser, userPutDTO));
        assertEquals(testUser.getUsername(), "testUsername");
    }

    @Test
    public void addFriendRequest_validInput_success() {
        User testUser2 = new User();
        testUser2.setId(2L);
        testUser2.setToken("testToken2");

        RequestPutDTO requestPutDTO = new RequestPutDTO();
        requestPutDTO.setSenderID(2L);
        requestPutDTO.setToken("testToken2");

        Mockito.when(userRepository.findByUsername(Mockito.any())).thenReturn(testUser);
        Mockito.when(userRepository.findById(Mockito.any())).thenReturn(java.util.Optional.ofNullable(testUser2));
        userService.addFriendRequest(testUser, requestPutDTO);

        assertTrue(testUser.getFriendRequests().contains(testUser2));
        assertFalse(testUser2.getFriendRequests().contains(testUser));
    }

    @Test
    public void addFriendRequest_alreadyAdded_throwsException() {
        User testUser2 = new User();
        testUser2.setId(2L);
        testUser2.setToken("testToken2");

        RequestPutDTO requestPutDTO = new RequestPutDTO();
        requestPutDTO.setSenderID(2L);
        requestPutDTO.setToken("testToken2");

        testUser.setFriendRequests(testUser2);

        Mockito.when(userRepository.findByUsername(Mockito.any())).thenReturn(testUser);
        Mockito.when(userRepository.findById(Mockito.any())).thenReturn(java.util.Optional.ofNullable(testUser2));

        assertThrows(NoContentException.class, () -> userService.addFriendRequest(testUser, requestPutDTO));
        assertTrue(testUser.getFriendRequests().contains(testUser2));
    }

    @Test
    public void addFriendRequest_invalidToken_throwsException() {
        User testUser2 = new User();
        testUser2.setId(2L);
        testUser2.setToken("testToken2");

        RequestPutDTO requestPutDTO = new RequestPutDTO();
        requestPutDTO.setSenderID(2L);
        requestPutDTO.setToken("wrongToken");

        Mockito.when(userRepository.findByUsername(Mockito.any())).thenReturn(testUser);
        Mockito.when(userRepository.findById(Mockito.any())).thenReturn(java.util.Optional.ofNullable(testUser2));

        assertThrows(UnauthorizedException.class, () -> userService.addFriendRequest(testUser, requestPutDTO));
        assertFalse(testUser.getFriendRequests().contains(testUser2));
    }

   @Test
   public void addFriendRequest_userNotFound_throwsException() {
       RequestPutDTO requestPutDTO = new RequestPutDTO();
       requestPutDTO.setSenderID(2L);
       requestPutDTO.setToken("testToken2");

       Mockito.when(userRepository.findByUsername(Mockito.any())).thenReturn(null);

       assertThrows(NotFoundException.class, () -> userService.addFriendRequest(testUser, requestPutDTO));
   }

    @Test
    public void acceptFriendRequest_validInput_success() {
        User testUser2 = new User();
        testUser2.setId(2L);
        testUser2.setToken("testToken2");

        testUser.setFriendRequests(testUser2);

        FriendPutDTO friendPutDTO = new FriendPutDTO();
        friendPutDTO.setAccepted(true);
        friendPutDTO.setAccepterToken(testUser.getToken());
        friendPutDTO.setRequesterID(testUser2.getId());

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
        friendPutDTO.setAccepterToken(testUser.getToken());
        friendPutDTO.setRequesterID(testUser2.getId());

        Mockito.when(userRepository.findByUsername(Mockito.any())).thenReturn(testUser);
        Mockito.when(userRepository.findById(Mockito.any())).thenReturn(java.util.Optional.ofNullable(testUser2));

        userService.acceptOrDeclineFriendRequest(testUser, friendPutDTO);

        assertFalse(testUser.getFriendList().contains(testUser2));
        assertFalse(testUser2.getFriendList().contains(testUser));
        assertFalse(testUser.getFriendRequests().contains(testUser2));
    }

    @Test
    public void declineFriendRequest_validInput_unauthorizedUser() {
        User testUser2 = new User();
        testUser2.setId(2L);
        testUser2.setToken("testToken2");

        testUser.setFriendRequests(testUser2);

        FriendPutDTO friendPutDTO = new FriendPutDTO();
        friendPutDTO.setAccepted(false);
        friendPutDTO.setAccepterToken("WrongToken");
        friendPutDTO.setRequesterID(testUser2.getId());

        Mockito.when(userRepository.findByUsername(Mockito.any())).thenReturn(testUser);
        Mockito.when(userRepository.findById(Mockito.any())).thenReturn(java.util.Optional.ofNullable(testUser2));



        assertThrows(UnauthorizedException.class,() ->{userService.acceptOrDeclineFriendRequest(testUser, friendPutDTO);});
        assertFalse(testUser.getFriendList().contains(testUser2));
        assertFalse(testUser2.getFriendList().contains(testUser));
        assertFalse(testUser.getFriendRequests().isEmpty());
    }

    @Test
    public void handleFriendRequest_invalidInput_throwsException() {
        FriendPutDTO friendPutDTO = new FriendPutDTO();
        friendPutDTO.setAccepted(false);
        friendPutDTO.setAccepterToken("testToken");
        friendPutDTO.setRequesterID(5L);

        User testUser2 = new User();
        testUser2.setId(2L);
        testUser2.setToken("anyToken");

        Mockito.when(userRepository.findByUsername(Mockito.any())).thenReturn(testUser);
        Mockito.when(userRepository.findById(Mockito.any())).thenReturn(java.util.Optional.ofNullable(testUser2));

        assertThrows(NotFoundException.class, () -> userService.acceptOrDeclineFriendRequest(testUser, friendPutDTO));
    }

    @Test
    public void handleLobbyInvite_accepted_success() {
        Lobby lobby = new Lobby();
        lobby.setCurrentNumPlayersAndBots(4);
        lobby.setMaxPlayersAndBots(5);

        User receiver = new User();
        receiver.setToken("testToken");
        receiver.setLobbyInvites(lobby);

        Player player = new Player();
        player.setToken("testToken");

        LobbyAcceptancePutDTO lobbyAcceptancePutDTO = new LobbyAcceptancePutDTO();
        lobbyAcceptancePutDTO.setAccepterToken("testToken");
        lobbyAcceptancePutDTO.setAccepted(true);

        Mockito.when(userRepository.findById(Mockito.any())).thenReturn(java.util.Optional.ofNullable(receiver));

        assertTrue(userService.acceptOrDeclineLobbyInvite(lobby, lobbyAcceptancePutDTO));
        assertFalse(receiver.getLobbyInvites().contains(lobby));
    }

    @Test
    public void handleLobbyInvite_notAccepted_throwsException() {
        Lobby lobby = new Lobby();
        lobby.setCurrentNumPlayersAndBots(4);
        lobby.setMaxPlayersAndBots(5);

        User receiver = new User();
        receiver.setToken("testToken");
        receiver.setLobbyInvites(lobby);

        LobbyAcceptancePutDTO lobbyAcceptancePutDTO = new LobbyAcceptancePutDTO();
        lobbyAcceptancePutDTO.setAccepterToken("testToken");
        lobbyAcceptancePutDTO.setAccepted(false);

        Mockito.when(userRepository.findById(Mockito.any())).thenReturn(java.util.Optional.ofNullable(receiver));

        assertThrows(NoContentException.class, () -> userService.acceptOrDeclineLobbyInvite(lobby, lobbyAcceptancePutDTO));
        assertEquals(4, lobby.getCurrentNumPlayersAndBots());
    }

    @Test
    public void handleLobbyInvite_invalidToken_throwsException() {
        Lobby lobby = new Lobby();
        lobby.setCurrentNumPlayersAndBots(4);
        lobby.setMaxPlayersAndBots(5);

        User receiver = new User();
        receiver.setToken("testToken");
        receiver.setLobbyInvites(lobby);

        LobbyAcceptancePutDTO lobbyAcceptancePutDTO = new LobbyAcceptancePutDTO();
        lobbyAcceptancePutDTO.setAccepterToken("wrongToken");
        lobbyAcceptancePutDTO.setAccepted(true);

        Mockito.when(userRepository.findById(Mockito.any())).thenReturn(java.util.Optional.ofNullable(receiver));

        assertThrows(UnauthorizedException.class, () -> userService.acceptOrDeclineLobbyInvite(lobby, lobbyAcceptancePutDTO));
    }

    @Test
    public void handleLobbyInvite_invalidRequest_throwsException() {
        Lobby lobby = new Lobby();
        lobby.setCurrentNumPlayersAndBots(4);
        lobby.setMaxPlayersAndBots(5);

        User receiver = new User();
        receiver.setToken("testToken");

        LobbyAcceptancePutDTO lobbyAcceptancePutDTO = new LobbyAcceptancePutDTO();
        lobbyAcceptancePutDTO.setAccepterToken("testToken");
        lobbyAcceptancePutDTO.setAccepted(true);

        Mockito.when(userRepository.findById(Mockito.any())).thenReturn(java.util.Optional.ofNullable(receiver));

        assertThrows(UnauthorizedException.class, () -> userService.acceptOrDeclineLobbyInvite(lobby, lobbyAcceptancePutDTO));
    }

    @Test
    public void lobbyInviteSent_success(){
        User userToInvite = new User();

        Lobby lobby = new Lobby();
        lobby.setCurrentNumPlayersAndBots(1);
        lobby.setMaxPlayersAndBots(5);
        lobby.setToken("testToken");

        userToInvite = userService.addLobbyInvite(userToInvite,lobby,testUser);

        assertTrue(userToInvite.getLobbyInvites().contains(lobby));
    }

    @Test
    public void lobbyInviteSent_unauthorized(){
        User userToInvite = new User();

        Lobby lobby = new Lobby();
        lobby.setCurrentNumPlayersAndBots(1);
        lobby.setMaxPlayersAndBots(5);
        lobby.setToken("anotherToken");

        assertThrows(UnauthorizedException.class,()->
        {userService.addLobbyInvite(userToInvite,lobby,testUser);});
    }

    @Test
    public void lobbyInviteSent_autoInvite(){
        User userToInvite = new User();

        Lobby lobby = new Lobby();
        lobby.setCurrentNumPlayersAndBots(1);
        lobby.setMaxPlayersAndBots(5);
        lobby.setToken("testToken");

        assertThrows(ConflictException.class,()->
        {userService.addLobbyInvite(testUser,lobby,testUser);});
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
