package ch.uzh.ifi.seal.soprafs20.service;

import ch.uzh.ifi.seal.soprafs20.entity.Lobby;
import ch.uzh.ifi.seal.soprafs20.entity.User;
import ch.uzh.ifi.seal.soprafs20.exceptions.UnauthorizedException;
import ch.uzh.ifi.seal.soprafs20.repository.LobbyRepository;
import ch.uzh.ifi.seal.soprafs20.rest.dto.LobbyPutDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;



public class LobbyServiceTest {

    @Mock
    private LobbyRepository lobbyRepository;

    @InjectMocks
    private LobbyService lobbyService;

    private Lobby testLobby;
    private User host;

    @BeforeEach
    public void setup(){
        MockitoAnnotations.initMocks(this);

        //given
        testLobby = new Lobby();
        testLobby.setLobbyName("BadBunny");
        testLobby.setToken("1");
        testLobby.setMaxPlayersAndBots(5);
        testLobby.setVoiceChat(false);
        testLobby.setUserId(1L);
        testLobby.setCurrentNumPlayersAndBots(1);
        testLobby.setLobbyId(1L);

        host = new User();
        host.setToken("1");
        host.setId(1L);
        host.setUsername("Flacko");
        host.setPassword("1");




        // when -> any object is being save in the userRepository -> return the dummy testUser
        Mockito.when(lobbyRepository.save(Mockito.any())).thenReturn(testLobby);
        Mockito.when(lobbyRepository.findById(Mockito.any())).thenReturn(java.util.Optional.ofNullable(testLobby));

    }


    @Test
    public void createLobby_validInput_publicLobby(){
        testLobby.setPrivate(false);
        Lobby lobby = lobbyService.createLobby(testLobby,host);

        Mockito.verify(lobbyRepository,Mockito.times(1)).save(Mockito.any());

        assertEquals(testLobby.getLobbyId(),lobby.getLobbyId());
        assertEquals(testLobby.getLobbyName(),lobby.getLobbyName());
        assertEquals(testLobby.getMaxPlayersAndBots(),lobby.getMaxPlayersAndBots());
        assertEquals(testLobby.getCurrentNumPlayersAndBots(),lobby.getCurrentNumPlayersAndBots());
        assertNull(lobby.getPrivateKey());
    }

    @Test
    public void createLobby_validInput_privateLobby(){
        testLobby.setPrivate(true);
        Lobby lobby = lobbyService.createLobby(testLobby,host);

        Mockito.verify(lobbyRepository,Mockito.times(1)).save(Mockito.any());

        assertEquals(testLobby.getLobbyId(),lobby.getLobbyId());
        assertEquals(testLobby.getLobbyName(),lobby.getLobbyName());
        assertEquals(testLobby.getMaxPlayersAndBots(),lobby.getMaxPlayersAndBots());
        assertEquals(testLobby.getCurrentNumPlayersAndBots(),lobby.getCurrentNumPlayersAndBots());
        assertNotNull(lobby.getPrivateKey());
    }

    @Test
    public void updateExistingLobby_authorizedUser(){
        testLobby.setPrivate(false);
        LobbyPutDTO lobbyPutDTO = new LobbyPutDTO();
        lobbyPutDTO.setMaxNumberOfPlayersAndBots(3);
        lobbyPutDTO.setToken("1");

        Lobby lobby = lobbyService.updateLobby(testLobby,lobbyPutDTO);

        assertEquals(3,lobby.getMaxPlayersAndBots());
    }

    @Test
    public void updateExistingLobby_unauthorizedUser(){
        testLobby.setPrivate(false);
        LobbyPutDTO lobbyPutDTO = new LobbyPutDTO();
        lobbyPutDTO.setMaxNumberOfPlayersAndBots(3);
        lobbyPutDTO.setToken("2");

        assertThrows(UnauthorizedException.class,()->{lobbyService.updateLobby(testLobby,lobbyPutDTO);});
    }

    @Test
    public void updateExistingLobby_tooManyPlayers(){
        testLobby.setPrivate(false);
        LobbyPutDTO lobbyPutDTO = new LobbyPutDTO();
        lobbyPutDTO.setMaxNumberOfPlayersAndBots(9);
        lobbyPutDTO.setToken("1");

        lobbyService.updateLobby(testLobby,lobbyPutDTO);

        assertEquals(5,testLobby.getMaxPlayersAndBots());


    }

    @Test
    public void updateExistingLobby_tooLittlePlayers(){
        testLobby.setPrivate(false);
        LobbyPutDTO lobbyPutDTO = new LobbyPutDTO();
        lobbyPutDTO.setMaxNumberOfPlayersAndBots(2);
        lobbyPutDTO.setToken("1");

        testLobby = lobbyService.updateLobby(testLobby,lobbyPutDTO);

        assertEquals(5,testLobby.getMaxPlayersAndBots());
    }

    @Test
    public void updateExistingLobby(){
        testLobby.setPrivate(false);
        User user1 = new User();
        User user2 = new User();
        User user3 = new User();

        testLobby.addUserToLobby(host);
        testLobby.addUserToLobby(user1);
        testLobby.addUserToLobby(user2);
        testLobby.addUserToLobby(user3);
        testLobby.setCurrentNumPlayersAndBots(4);

        LobbyPutDTO lobbyPutDTO = new LobbyPutDTO();
        lobbyPutDTO.setMaxNumberOfPlayersAndBots(3);
        lobbyPutDTO.setToken("1");

        testLobby = lobbyService.updateLobby(testLobby,lobbyPutDTO);

        assertEquals(5,testLobby.getMaxPlayersAndBots());
    }

    @Test
    public void getLobby(){
        testLobby.setPrivate(false);
        Lobby lobby = lobbyService.createLobby(testLobby,host);
        Lobby foundLobby = lobbyService.getLobby(lobby.getLobbyId());

        Mockito.verify(lobbyRepository, Mockito.times(1)).findById(Mockito.any());

        assertEquals(foundLobby.getLobbyId(),lobby.getLobbyId());
        assertEquals(foundLobby.getLobbyName(),lobby.getLobbyName());
        assertEquals(foundLobby.getUserId(),lobby.getUserId());
    }

    @Test
    public void getLobby_wrongId_throwsException(){


        testLobby.setPrivate(false);
        lobbyService.createLobby(testLobby,host);

        Mockito.when(lobbyRepository.findById(Mockito.any())).thenReturn(null);
        assertThrows(NullPointerException.class,() -> lobbyService.getLobby(1L));

    }

    @Test
    public void removePlayerFromLobby_success(){

        //second user
        User user2 = new User();
        user2.setToken("123");
        user2.setId(3L);
        user2.setUsername("Bunny");
        user2.setPassword("1");

        //lobby setup
        testLobby.setPrivate(false);
        testLobby.addUserToLobby(host);
        testLobby.addUserToLobby(user2);
        testLobby.setCurrentNumPlayersAndBots(2);

        //kick list setup
        LobbyPutDTO lobbyPutDTO = new LobbyPutDTO();
        lobbyPutDTO.setToken("1");
        ArrayList<Long> kick = new ArrayList<>();
        kick.add(3L);
        lobbyPutDTO.setUsersToKick(kick);

        assertEquals(testLobby.getUsersInLobby().size(),2);
        lobbyService.updateLobby(testLobby,lobbyPutDTO);

        assertEquals(1,testLobby.getUsersInLobby().size());
        assertFalse(testLobby.getUsersInLobby().contains(user2));
        assertEquals(1, testLobby.getCurrentNumPlayersAndBots());
    }

    @Test
    public void removeLobbyLeaderFromLobby_fail(){

        //second user
        User user2 = new User();
        user2.setToken("123");
        user2.setId(3L);
        user2.setUsername("Bunny");
        user2.setPassword("1");

        //lobby setup
        testLobby.setPrivate(false);
        testLobby.addUserToLobby(host);
        testLobby.addUserToLobby(user2);

        //kick list setup
        LobbyPutDTO lobbyPutDTO = new LobbyPutDTO();
        lobbyPutDTO.setToken("1");
        ArrayList<Long> kick = new ArrayList<>();
        kick.add(1L);
        lobbyPutDTO.setUsersToKick(kick);

        assertEquals(testLobby.getUsersInLobby().size(),2);

        lobbyService.updateLobby(testLobby,lobbyPutDTO);

        assertEquals(2,testLobby.getUsersInLobby().size());
        assertTrue(testLobby.getUsersInLobby().contains(host));

    }
}
