package ch.uzh.ifi.seal.soprafs20.controller;

import ch.uzh.ifi.seal.soprafs20.entity.Game;
import ch.uzh.ifi.seal.soprafs20.entity.Player;
import ch.uzh.ifi.seal.soprafs20.service.GameService;
import ch.uzh.ifi.seal.soprafs20.service.PlayerService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
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

}
