package ch.uzh.ifi.seal.soprafs20.service;

import ch.uzh.ifi.seal.soprafs20.entity.Lobby;
import ch.uzh.ifi.seal.soprafs20.entity.User;
import ch.uzh.ifi.seal.soprafs20.entity.Game;
import ch.uzh.ifi.seal.soprafs20.repository.GameRepository;
import ch.uzh.ifi.seal.soprafs20.rest.dto.GamePostDTO;
import ch.uzh.ifi.seal.soprafs20.rest.mapper.DTOMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.*;

public class GameServiceTest {

    @Mock
    private GameRepository gameRepository;

    @InjectMocks
    private GameService gameService;

    private Game testGame;
    private Lobby testLobby;
    private User testHost;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.initMocks(this);

        testGame = new Game();
        testGame.setLobbyId(1L);
        testGame.setRoundsPlayed(0);

        Mockito.when(gameRepository.save(Mockito.any())).thenReturn(testGame);

        testHost = new User();
        testHost.setId(2L);
        testHost.setToken("hostToken");

        testLobby = new Lobby();
        testLobby.setId(1L);
        testLobby.setToken("hostToken");
        testLobby.addUserToLobby(testHost);
        testLobby.setPrivate(false);


    }

    @Test
    public void getGame_validInput_success() {

    }

    @Test
    public void create_Game_validInput_success() {
        GamePostDTO gamePostDTO = new GamePostDTO();
        gamePostDTO.setUserId(testHost.getId());
        gamePostDTO.setUserToken(testHost.getToken());

        Game testGame = new Game();
        Game game = gameService.createGame(testLobby, gamePostDTO);
        Mockito.verify(gameRepository,Mockito.times(1)).save(Mockito.any());

        assertEquals(testLobby.getLobbyId(), game.getLobbyId());
//        assertTrue(game.getPlayers().contains(testHost));
//        assertEquals(0, game.getRoundsPlayed());
//        assertEquals(13, game.getWords().size());
    }


}
