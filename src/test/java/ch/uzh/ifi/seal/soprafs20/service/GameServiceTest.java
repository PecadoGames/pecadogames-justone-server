package ch.uzh.ifi.seal.soprafs20.service;

import ch.uzh.ifi.seal.soprafs20.GameLogic.gameStates.GameState;
import ch.uzh.ifi.seal.soprafs20.entity.Game;
import ch.uzh.ifi.seal.soprafs20.entity.Lobby;
import ch.uzh.ifi.seal.soprafs20.entity.Player;
import ch.uzh.ifi.seal.soprafs20.exceptions.ForbiddenException;
import ch.uzh.ifi.seal.soprafs20.repository.GameRepository;
import ch.uzh.ifi.seal.soprafs20.rest.dto.GamePostDTO;
import ch.uzh.ifi.seal.soprafs20.rest.dto.MessagePutDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

public class GameServiceTest {

    @Mock
    private GameRepository gameRepository;

    @InjectMocks
    private GameService gameService;

    private Game testGame;
    private Lobby testLobby;
    private Player testHost;
    private Player player2;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.initMocks(this);

        testGame = new Game();
        testGame.setLobbyId(1L);
        testGame.setRoundsPlayed(0);

        Mockito.when(gameRepository.save(Mockito.any())).thenReturn(testGame);

        testHost = new Player();
        testHost.setId(1L);
        testHost.setToken("hostToken");

        player2 = new Player();
        player2.setId(2L);
        player2.setToken("token2");

        testLobby = new Lobby();
        testLobby.setLobbyId(1L);
        testLobby.setToken("hostToken");
        testLobby.addPlayerToLobby(testHost);
        testGame.addPlayer(player2);
        testGame.setCurrentGuesser(testHost);
        testLobby.setPrivate(false);



    }

    @Test
    public void getGame_validInput_success() {

    }

    @Test
    public void create_Game_validInput_success() {
        GamePostDTO gamePostDTO = new GamePostDTO();
        gamePostDTO.setHostId(testHost.getId());
        gamePostDTO.setHostToken(testHost.getToken());

        Game testGame = new Game();
        Game game = gameService.createGame(testLobby, gamePostDTO);
        Mockito.verify(gameRepository,Mockito.times(1)).save(Mockito.any());

        assertEquals(testLobby.getLobbyId(), game.getLobbyId());
//        assertTrue(game.getPlayers().contains(testHost));
//        assertEquals(0, game.getRoundsPlayed());
//        assertEquals(13, game.getWords().size());
    }

    @Test
    public void sendClue_normalGame_success(){
        testGame.setGameState(GameState.ENTERCLUESSTATE);
        testGame.setSpecialGame(false);
        testGame.setStartTimeSeconds(TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis()));

        MessagePutDTO messagePutDTO = new MessagePutDTO();
        messagePutDTO.setPlayerToken("token2");
        messagePutDTO.setPlayerId(2L);
        messagePutDTO.setMessage("star");
        long sendTime = TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis());

        gameService.sendClue(testGame,player2,"star");
        assertEquals("star",testGame.getEnteredClues().get(0));
        assertTrue(player2.isClueIsSent());
    }

    @Test
    public void sendClue_normalGame_fail_unauthorizedUser(){
        testGame.setGameState(GameState.ENTERCLUESSTATE);
        testGame.setSpecialGame(false);
        testGame.setStartTimeSeconds(TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis()));

        MessagePutDTO messagePutDTO = new MessagePutDTO();
        messagePutDTO.setPlayerToken("hostToken");
        messagePutDTO.setPlayerId(1L);
        messagePutDTO.setMessage("star");
        long sendTime = TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis());

        Exception ex = assertThrows(ForbiddenException.class,()->{gameService.sendClue(testGame,testHost,"star");});
        assertEquals("User not allowed to send clue",ex.getMessage());
        assertTrue(testGame.getEnteredClues().isEmpty());
        assertFalse(testHost.isClueIsSent());
    }

    @Test
    public void sendClue_normalGame_fail_timeRanOut(){
        testGame.setGameState(GameState.ENTERCLUESSTATE);
        testGame.setSpecialGame(false);
        testGame.setStartTimeSeconds(TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis()));

        MessagePutDTO messagePutDTO = new MessagePutDTO();
        messagePutDTO.setPlayerToken("token2");
        messagePutDTO.setPlayerId(2L);
        messagePutDTO.setMessage("star");
        long sendTime = TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis()) + 1000;

        Exception ex = assertThrows(ForbiddenException.class,()->{gameService.sendClue(testGame,player2,"star");});
        assertEquals("Time ran out!",ex.getMessage());
        assertEquals("",testGame.getEnteredClues().get(0));
        assertTrue(player2.isClueIsSent());
    }

    @Test
    public void sendClue_specialGame_firstClue_success(){
        testGame.setGameState(GameState.ENTERCLUESSTATE);
        testGame.setSpecialGame(true);
        testGame.setStartTimeSeconds(TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis()));

        MessagePutDTO messagePutDTO = new MessagePutDTO();
        messagePutDTO.setPlayerToken("token2");
        messagePutDTO.setPlayerId(2L);
        messagePutDTO.setMessage("star");
        long sendTime = TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis());

        gameService.sendClue(testGame,player2,"star");

        assertEquals("star",testGame.getEnteredClues().get(0));
        assertEquals("token2",testGame.getEnteredClues().get(1));
        assertFalse(player2.isClueIsSent());
    }

    @Test
    public void sendClue_specialGame_secondClue_success(){
        testGame.setGameState(GameState.ENTERCLUESSTATE);
        testGame.setSpecialGame(true);
        testGame.getEnteredClues().add("star");
        testGame.getEnteredClues().add("token2");
        testGame.setStartTimeSeconds(TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis()));

        MessagePutDTO messagePutDTO = new MessagePutDTO();
        messagePutDTO.setPlayerToken("token2");
        messagePutDTO.setPlayerId(2L);
        messagePutDTO.setMessage("star");
        long sendTime = TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis());

        gameService.sendClue(testGame,player2,"wars");

        assertEquals("star",testGame.getEnteredClues().get(0));
        assertEquals("wars",testGame.getEnteredClues().get(1));
        assertTrue(player2.isClueIsSent());
    }

    @Test
    public void sendClue_specialGame_moreThanTwoClues_fail(){
        testGame.setGameState(GameState.ENTERCLUESSTATE);
        testGame.setSpecialGame(true);
        testGame.getEnteredClues().add("star");
        testGame.getEnteredClues().add("wars");
        testGame.setStartTimeSeconds(TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis()));
        player2.setClueIsSent(true);

        MessagePutDTO messagePutDTO = new MessagePutDTO();
        messagePutDTO.setPlayerToken("token2");
        messagePutDTO.setPlayerId(2L);
        messagePutDTO.setMessage("badbunny");
        long sendTime = TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis());

        Exception ex = assertThrows(ForbiddenException.class,()->{gameService.sendClue(testGame,player2,"wars");});

        assertEquals("User not allowed to send clue",ex.getMessage());
        assertEquals(2,testGame.getEnteredClues().size());
        assertTrue(player2.isClueIsSent());
    }


}
