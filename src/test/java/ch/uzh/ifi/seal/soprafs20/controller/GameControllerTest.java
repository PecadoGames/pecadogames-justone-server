package ch.uzh.ifi.seal.soprafs20.controller;

import ch.uzh.ifi.seal.soprafs20.GameLogic.gameStates.GameState;
import ch.uzh.ifi.seal.soprafs20.entity.Clue;
import ch.uzh.ifi.seal.soprafs20.entity.Game;
import ch.uzh.ifi.seal.soprafs20.entity.Player;
import ch.uzh.ifi.seal.soprafs20.exceptions.BadRequestException;
import ch.uzh.ifi.seal.soprafs20.exceptions.UnauthorizedException;
import ch.uzh.ifi.seal.soprafs20.rest.dto.MessagePutDTO;
import ch.uzh.ifi.seal.soprafs20.service.GameService;
import ch.uzh.ifi.seal.soprafs20.service.InternalTimerService;
import ch.uzh.ifi.seal.soprafs20.service.PlayerService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import java.util.concurrent.TimeUnit;

import static org.hamcrest.Matchers.*;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(GameController.class)
public class GameControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private PlayerService playerService;
    @MockBean
    private GameService gameService;
    @MockBean
    private InternalTimerService internalTimerService;


    @Test
    public void givenGame_whenGetGame_returnJson() throws Exception {
        Player player1 = new Player();
        player1.setId(1L);
        player1.setToken("token1");

        Player player2 = new Player();
        player2.setId(2L);
        player2.setToken("token2");

        Game game = new Game();
        game.setLobbyId(1L);
        game.setRoundsPlayed(1);
        game.addPlayer(player1);
        game.addPlayer(player2);
        game.setCurrentGuesser(player1);
        game.setCurrentWord("Erdbeermarmeladebrot");

        given(gameService.getGame(Mockito.anyLong())).willReturn(game);

        MockHttpServletRequestBuilder getRequest = get("/lobbies/{lobbyId}/game", game.getLobbyId())
                .contentType(MediaType.APPLICATION_JSON)
                .param("token", player2.getToken());

        mockMvc.perform(getRequest)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.lobbyId", is(game.getLobbyId().intValue())))
                .andExpect(jsonPath("$.roundsPlayed", is(game.getRoundsPlayed())))
                .andExpect(jsonPath("$.players", hasSize(2)))
                .andExpect(jsonPath("$.currentWord", is(game.getCurrentWord())));
    }

    @Test
    public void getGame_guesserToken_returnJson() throws Exception {
        Player player1 = new Player();
        player1.setId(1L);
        player1.setToken("token1");

        Player player2 = new Player();
        player2.setId(2L);
        player2.setToken("token2");

        Game game = new Game();
        game.setLobbyId(1L);
        game.setRoundsPlayed(1);
        game.addPlayer(player1);
        game.addPlayer(player2);
        game.setCurrentGuesser(player1);
        game.setCurrentWord("Erdbeermarmeladebrot");
        game.setGameState(GameState.PICKWORDSTATE);

        given(gameService.getGame(Mockito.anyLong())).willReturn(game);

        MockHttpServletRequestBuilder getRequest = get("/lobbies/{lobbyId}/game", game.getLobbyId())
                .contentType(MediaType.APPLICATION_JSON)
                .param("token", player1.getToken());

        mockMvc.perform(getRequest)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.lobbyId", is(game.getLobbyId().intValue())))
                .andExpect(jsonPath("$.roundsPlayed", is(game.getRoundsPlayed())))
                .andExpect(jsonPath("$.players", hasSize(2)))
                .andExpect(jsonPath("$.currentWord", is(nullValue())));
    }

    @Test
    public void getGame_invalidToken_throwsException() throws Exception{
        Player player1 = new Player();
        player1.setId(1L);
        player1.setToken("token1");

        Game game = new Game();
        game.setLobbyId(1L);
        game.setRoundsPlayed(1);
        game.addPlayer(player1);
        game.setCurrentGuesser(player1);
        game.setCurrentWord("Erdbeermarmeladebrot");

        given(gameService.getGame(Mockito.anyLong())).willReturn(game);

        MockHttpServletRequestBuilder getRequest = get("/lobbies/{lobbyId}/game", game.getLobbyId())
                .contentType(MediaType.APPLICATION_JSON)
                .param("token", "wrongToken");

        mockMvc.perform(getRequest)
                .andExpect(status().isUnauthorized());
    }

    @Test
    public void sendClue_validInput() throws Exception{
        Player player1 = new Player();
        player1.setId(1L);
        player1.setToken("token1");

        Player player2 = new Player();
        player2.setId(2L);
        player2.setToken("token2");

        Clue clue = new Clue();
        clue.setActualClue("Zopf");

        Game game = new Game();
        game.setLobbyId(1L);
        game.setRoundsPlayed(1);
        game.addPlayer(player1);
        game.setCurrentGuesser(player1);
        game.setCurrentWord("Erdbeermarmeladebrot");
        game.getEnteredClues().add(clue);
        game.setGameState(GameState.ENTERCLUESSTATE);

        MessagePutDTO messagePutDTO = new MessagePutDTO();
        messagePutDTO.setMessage("Zopf");
        messagePutDTO.setPlayerId(2L);
        messagePutDTO.setPlayerToken("token2");

        given(gameService.getGame(Mockito.anyLong())).willReturn(game);

        MockHttpServletRequestBuilder putRequest = put("/lobbies/{lobbyId}/game/clue", game.getLobbyId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(messagePutDTO));

        mockMvc.perform(putRequest)
                .andExpect(status().isNoContent());
    }

    @Test
    public void sendClue_validInput_wrongState() throws Exception {
        Player player1 = new Player();
        player1.setId(1L);
        player1.setToken("token1");

        Player player2 = new Player();
        player2.setId(2L);
        player2.setToken("token2");

        Game game = new Game();
        game.setLobbyId(1L);
        game.setRoundsPlayed(1);
        game.addPlayer(player1);
        game.setGameState(GameState.PICKWORDSTATE);

        MessagePutDTO messagePutDTO = new MessagePutDTO();
        messagePutDTO.setMessage("Zopf");
        messagePutDTO.setPlayerId(2L);
        messagePutDTO.setPlayerToken("token2");

        //given(gameService.getGame(Mockito.anyLong())).willReturn(game);
        given(gameService.sendClue(Mockito.any(), Mockito.any(), Mockito.any())).willThrow(new UnauthorizedException("ex"));

        MockHttpServletRequestBuilder putRequest = put("/lobbies/{lobbyId}/game/clue", game.getLobbyId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(messagePutDTO));

        mockMvc.perform(putRequest)
                .andExpect(status().isUnauthorized());
    }

    @Test
    public void pickWord_validInput_success() throws Exception {
        Player player1 = new Player();
        player1.setId(1L);
        player1.setToken("token1");

        Player player2 = new Player();
        player2.setId(2L);
        player2.setToken("token2");

        Game game = new Game();
        game.setLobbyId(1L);
        game.setRoundsPlayed(1);
        game.addPlayer(player1);
        game.setCurrentGuesser(player1);
        game.setGameState(GameState.PICKWORDSTATE);
        game.setStartTimeSeconds(TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis()));

        given(gameService.getGame(Mockito.anyLong())).willReturn(game);

        MockHttpServletRequestBuilder getRequest = get("/lobbies/{lobbyId}/game/word", game.getLobbyId())
                .contentType(MediaType.APPLICATION_JSON)
                .param("token", player1.getToken());

        mockMvc.perform(getRequest)
                .andExpect(status().isOk());
    }

    @Test
    public void pickWord_validInput_wrongState() throws Exception {
        Player player1 = new Player();
        player1.setId(1L);
        player1.setToken("token1");

        Player player2 = new Player();
        player2.setId(2L);
        player2.setToken("token2");

        Game game = new Game();
        game.setLobbyId(1L);
        game.setRoundsPlayed(1);
        game.addPlayer(player1);
        game.setCurrentGuesser(player1);
        game.setGameState(GameState.NLPSTATE);
        game.setStartTimeSeconds(TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis()));

        given(gameService.getGame(Mockito.anyLong())).willReturn(game);

        MockHttpServletRequestBuilder getRequest = get("/lobbies/{lobbyId}/game/word", game.getLobbyId())
                .contentType(MediaType.APPLICATION_JSON)
                .param("token", player1.getToken());

        mockMvc.perform(getRequest).andExpect(status().is4xxClientError());
    }

    private String asJsonString(final Object object) {
        try {
            return new ObjectMapper().writeValueAsString(object);
        }
        catch (JsonProcessingException e) {
            throw new BadRequestException(String.format("The request body could not be created.%s", e.toString()));
        }
    }
}
