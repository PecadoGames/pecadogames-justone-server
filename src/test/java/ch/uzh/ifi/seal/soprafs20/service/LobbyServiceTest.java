package ch.uzh.ifi.seal.soprafs20.service;

import ch.uzh.ifi.seal.soprafs20.entity.Lobby;
import ch.uzh.ifi.seal.soprafs20.entity.Player;
import ch.uzh.ifi.seal.soprafs20.exceptions.ConflictException;
import ch.uzh.ifi.seal.soprafs20.exceptions.UnauthorizedException;
import ch.uzh.ifi.seal.soprafs20.repository.LobbyRepository;
import ch.uzh.ifi.seal.soprafs20.repository.PlayerRepository;
import ch.uzh.ifi.seal.soprafs20.rest.dto.LobbyPutDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.*;


public class LobbyServiceTest {

    @Mock
    private LobbyRepository lobbyRepository;
    @Mock
    private PlayerRepository playerRepository;

    @InjectMocks
    private LobbyService lobbyService;

    private Lobby testLobby;
    private Player host;

    @BeforeEach
    public void setup(){
        MockitoAnnotations.initMocks(this);
        //given
        testLobby = new Lobby();
        testLobby.setLobbyName("BadBunny");
        testLobby.setHostToken("1");
        testLobby.setMaxPlayersAndBots(5);
        testLobby.setVoiceChat(false);
        testLobby.setHostId(1L);
        testLobby.setCurrentNumPlayers(1);
        testLobby.setLobbyId(1L);

        host = new Player();
        host.setToken("1");
        host.setId(1L);
        host.setUsername("Flacko");

        // when -> any object is being save in the userRepository -> return the dummy testUser
        Mockito.when(lobbyRepository.save(Mockito.any())).thenReturn(testLobby);
        Mockito.when(lobbyRepository.findById(Mockito.any())).thenReturn(java.util.Optional.ofNullable(testLobby));

    }

    @Test
    public void createLobby_validInput_publicLobby(){
        testLobby.setPrivate(false);
        Lobby lobby = lobbyService.createLobby(testLobby, host);

        Mockito.verify(lobbyRepository,Mockito.times(1)).save(Mockito.any());

        assertEquals(testLobby.getLobbyId(),lobby.getLobbyId());
        assertEquals(testLobby.getLobbyName(),lobby.getLobbyName());
        assertEquals(testLobby.getMaxPlayersAndBots(),lobby.getMaxPlayersAndBots());
        assertEquals(testLobby.getCurrentNumPlayers(),lobby.getCurrentNumPlayers());
        assertNull(lobby.getPrivateKey());
    }

    @Test
    public void createLobby_validInput_privateLobby(){
        testLobby.setPrivate(true);
        Lobby lobby = lobbyService.createLobby(testLobby, host);

        Mockito.verify(lobbyRepository,Mockito.times(1)).save(Mockito.any());

        assertEquals(testLobby.getLobbyId(),lobby.getLobbyId());
        assertEquals(testLobby.getLobbyName(),lobby.getLobbyName());
        assertEquals(testLobby.getMaxPlayersAndBots(),lobby.getMaxPlayersAndBots());
        assertEquals(testLobby.getCurrentNumPlayers(),lobby.getCurrentNumPlayers());
        assertNotNull(lobby.getPrivateKey());
    }

    @Test
    public void updateExistingLobby_authorizedUser(){
        testLobby.setPrivate(false);
        LobbyPutDTO lobbyPutDTO = new LobbyPutDTO();
        lobbyPutDTO.setMaxNumberOfPlayersAndBots(3);
        lobbyPutDTO.setHostToken("1");

        Lobby lobby = lobbyService.updateLobby(testLobby,lobbyPutDTO);

        assertEquals(3,lobby.getMaxPlayersAndBots());
    }

    @Test
    public void updateExistingLobby_unauthorizedUser(){
        testLobby.setPrivate(false);
        LobbyPutDTO lobbyPutDTO = new LobbyPutDTO();
        lobbyPutDTO.setMaxNumberOfPlayersAndBots(3);
        lobbyPutDTO.setHostToken("2");

        assertThrows(UnauthorizedException.class,()->{lobbyService.updateLobby(testLobby,lobbyPutDTO);});
    }

    @Test
    public void updateExistingLobby_tooManyPlayers(){
        testLobby.setPrivate(false);
        LobbyPutDTO lobbyPutDTO = new LobbyPutDTO();
        lobbyPutDTO.setMaxNumberOfPlayersAndBots(9);
        lobbyPutDTO.setHostToken("1");

        lobbyService.updateLobby(testLobby,lobbyPutDTO);

        assertEquals(5,testLobby.getMaxPlayersAndBots());


    }

    @Test
    public void updateExistingLobby_tooLittlePlayers(){
        testLobby.setPrivate(false);
        LobbyPutDTO lobbyPutDTO = new LobbyPutDTO();
        lobbyPutDTO.setMaxNumberOfPlayersAndBots(2);
        lobbyPutDTO.setHostToken("1");

        testLobby = lobbyService.updateLobby(testLobby,lobbyPutDTO);

        assertEquals(5,testLobby.getMaxPlayersAndBots());
    }

    @Test
    public void updateExistingLobby(){
        testLobby.setPrivate(false);
        Player player1 = new Player();
        Player player2 = new Player();
        Player player3 = new Player();

        testLobby.addPlayerToLobby(host);
        testLobby.addPlayerToLobby(player1);
        testLobby.addPlayerToLobby(player2);
        testLobby.addPlayerToLobby(player3);
        testLobby.setCurrentNumPlayers(4);

        LobbyPutDTO lobbyPutDTO = new LobbyPutDTO();
        lobbyPutDTO.setMaxNumberOfPlayersAndBots(3);
        lobbyPutDTO.setHostToken("1");

        testLobby = lobbyService.updateLobby(testLobby,lobbyPutDTO);

        assertEquals(5,testLobby.getMaxPlayersAndBots());
    }

    @Test
    public void getLobby(){
        testLobby.setPrivate(false);
        Lobby lobby = lobbyService.createLobby(testLobby, host);
        Lobby foundLobby = lobbyService.getLobby(lobby.getLobbyId());

        Mockito.verify(lobbyRepository, Mockito.times(1)).findById(Mockito.any());

        assertEquals(foundLobby.getLobbyId(),lobby.getLobbyId());
        assertEquals(foundLobby.getLobbyName(),lobby.getLobbyName());
        assertEquals(foundLobby.getHostId(),lobby.getHostId());
    }

    @Test
    public void getLobby_wrongId_throwsException(){


        testLobby.setPrivate(false);
        lobbyService.createLobby(testLobby, host);

        Mockito.when(lobbyRepository.findById(Mockito.any())).thenReturn(null);
        assertThrows(NullPointerException.class,() -> lobbyService.getLobby(1L));

    }

    @Test
    public void kickPlayerFromLobby_success(){
        //second user
        Player player2 = new Player();
        player2.setToken("123");
        player2.setId(3L);
        player2.setUsername("Bunny");

        //lobby setup
        testLobby.setPrivate(false);
        testLobby.addPlayerToLobby(host);
        testLobby.addPlayerToLobby(player2);
        testLobby.setCurrentNumPlayers(2);

        lobbyService.kickPlayers(testLobby, player2);

        assertEquals(1,testLobby.getPlayersInLobby().size());
        assertFalse(testLobby.getPlayersInLobby().contains(player2));
        assertEquals(1, testLobby.getCurrentNumPlayers());
    }

    @Test
    public void removeLobbyLeaderFromLobby_fail(){

        //second user
        Player player2 = new Player();
        player2.setToken("123");
        player2.setId(3L);
        player2.setUsername("Bunny");

        //lobby setup
        testLobby.setPrivate(false);
        testLobby.addPlayerToLobby(host);
        testLobby.addPlayerToLobby(player2);

        //kick list setup
        LobbyPutDTO lobbyPutDTO = new LobbyPutDTO();
        lobbyPutDTO.setHostToken("1");
        lobbyPutDTO.setPlayerToKickId(1L);

        lobbyService.updateLobby(testLobby,lobbyPutDTO);

        assertEquals(2,testLobby.getPlayersInLobby().size());
        assertTrue(testLobby.getPlayersInLobby().contains(host));

    }

    @Test
    public void joinLobby_success(){

        //second user
        Player player2 = new Player();
        player2.setToken("123");
        player2.setId(3L);


        //lobby setup
        testLobby.setPrivate(false);
        testLobby.addPlayerToLobby(host);

        lobbyService.addPlayerToLobby(player2.getToken(), player2, testLobby);

        assertEquals(2, testLobby.getPlayersInLobby().size());
    }

    @Test
    public void joinLobby_fail_userAlreadyInLobby(){

        //second user
        Player player2 = new Player();
        player2.setToken("123");
        player2.setId(3L);


        //lobby setup
        testLobby.setPrivate(false);
        testLobby.addPlayerToLobby(host);
        testLobby.addPlayerToLobby(player2);

        Throwable ex = assertThrows(ConflictException.class,() ->{lobbyService.addPlayerToLobby(player2.getToken(), player2, testLobby);;});
        //assertEquals("User already in lobby", ex.getMessage());
    }

    @Test
    public void joinLobby_fail_hostIsAlreadyInLobby(){

        //second user
        Player player2 = new Player();
        player2.setToken("123");
        player2.setId(3L);


        //lobby setup
        testLobby.setPrivate(false);
        testLobby.addPlayerToLobby(host);
        testLobby.addPlayerToLobby(player2);

        Throwable ex = assertThrows(ConflictException.class,() ->{lobbyService.addPlayerToLobby(host.getToken(), host, testLobby);;});
        //assertEquals("Host cannot join their own lobby",ex.getMessage());
    }

    @Test
    public void joinLobby_fail_LobbyFull(){

        //second user
        Player player2 = new Player();
        player2.setToken("123");
        player2.setId(3L);

        //third user
        Player player3 = new Player();
        player3.setToken("000");
        player3.setId(4L);

        //fourth user
        Player player4 = new Player();
        player4.setToken("555");
        player4.setId(5L);


        //lobby setup
        testLobby.setPrivate(false);
        testLobby.setMaxPlayersAndBots(3);
        testLobby.setCurrentNumPlayers(3);
        testLobby.addPlayerToLobby(host);
        testLobby.addPlayerToLobby(player2);
        testLobby.addPlayerToLobby(player3);

        Throwable ex = assertThrows(ConflictException.class,() ->{lobbyService.addPlayerToLobby(player4.getToken(), player4,testLobby);;});
        //assertEquals("Lobby is full",ex.getMessage());
    }

    @Test
    public void joinLobby_fail_gameIsStarted(){

        //second user
        Player player2 = new Player();
        player2.setToken("123");
        player2.setId(3L);

        //third user
        Player player3 = new Player();
        player3.setToken("000");
        player3.setId(4L);

        //fourth user
        Player player4 = new Player();
        player4.setToken("555");
        player4.setId(5L);


        //lobby setup
        testLobby.setPrivate(false);
        testLobby.setMaxPlayersAndBots(5);
        testLobby.setCurrentNumPlayers(3);
        testLobby.setGameIsStarted(true);
        testLobby.addPlayerToLobby(host);
        testLobby.addPlayerToLobby(player2);
        testLobby.addPlayerToLobby(player3);

        Throwable ex = assertThrows(ConflictException.class,() ->{lobbyService.addPlayerToLobby(player4.getToken(), player4, testLobby);});
        //assertEquals("Cant join the lobby, the game is already under way!",ex.getMessage());
    }

    @Test
    public void leaveLobby_success_hostIsAloneAndLeaves(){

        //lobby setup
        testLobby.setPrivate(false);
        testLobby.setMaxPlayersAndBots(5);
        testLobby.setCurrentNumPlayers(1);
        testLobby.setGameIsStarted(false);
        testLobby.addPlayerToLobby(host);

        lobbyService.removePlayerFromLobby(host,testLobby);
        Mockito.doReturn(null).when(lobbyRepository).findByLobbyId(1L);
        assertNull(lobbyRepository.findByLobbyId(1L));
    }

    @Test
    public void leaveLobby_success_newHostIsChosen(){
        //second user
        Player player2 = new Player();
        player2.setToken("123");
        player2.setId(3L);

        //third user
        Player player3 = new Player();
        player3.setToken("000");
        player3.setId(4L);

        //lobby setup
        testLobby.setPrivate(false);
        testLobby.addPlayerToLobby(host);
        testLobby.addPlayerToLobby(player2);
        testLobby.addPlayerToLobby(player3);
        testLobby.setHostId(player2.getId());
        testLobby.setHostToken(player2.getToken());
        testLobby.setCurrentNumPlayers(2);

        Mockito.doReturn(testLobby).when(lobbyRepository).save(testLobby);
        Mockito.doNothing().when(playerRepository).delete(Mockito.any());

        lobbyService.removePlayerFromLobby(player2, testLobby);

        assertNotEquals(player2.getId(), testLobby.getHostId());
        assertEquals(2, testLobby.getCurrentNumPlayers());
    }

    @Test
    public void leaveLobby_fail_gameAlreadyStarted(){

        //second user
        Player player2 = new Player();
        player2.setToken("123");
        player2.setId(3L);

        //lobby setup
        testLobby.setPrivate(false);
        testLobby.setGameIsStarted(true);
        testLobby.addPlayerToLobby(host);
        testLobby.addPlayerToLobby(player2);


        Throwable ex = assertThrows(ConflictException.class,()->{lobbyService.removePlayerFromLobby(player2, testLobby);});
        assertTrue(ex.getMessage().contains("game already started"));
    }

}
