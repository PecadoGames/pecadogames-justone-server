package ch.uzh.ifi.seal.soprafs20.controller;


import ch.uzh.ifi.seal.soprafs20.GameLogic.CardReader;
import ch.uzh.ifi.seal.soprafs20.entity.Lobby;
import ch.uzh.ifi.seal.soprafs20.exceptions.BadRequestException;
import ch.uzh.ifi.seal.soprafs20.exceptions.NotFoundException;
import ch.uzh.ifi.seal.soprafs20.exceptions.UnauthorizedException;
import ch.uzh.ifi.seal.soprafs20.rest.dto.LobbyPostDTO;
import ch.uzh.ifi.seal.soprafs20.rest.dto.LobbyPutDTO;
import ch.uzh.ifi.seal.soprafs20.service.LobbyService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.*;
import static org.mockito.Mockito.*;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.hamcrest.Matchers.*;


@WebMvcTest(LobbyController.class)
public class LobbyControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private LobbyService lobbyService;

    @Test
    public void givenLobbies_whenGetLobbies_thenReturnJsonArray() throws Exception {
        Lobby lobby = new Lobby();
        lobby.setId(1L);
        lobby.setLobbyName("Badbunny");
        lobby.setNumberOfPlayers(5);
        lobby.setVoiceChat(false);
        lobby.setUserId(1234);

        List<Lobby> allLobbies = Collections.singletonList(lobby);

        given(lobbyService.getLobbies()).willReturn(allLobbies);

        MockHttpServletRequestBuilder getRequest = get("/lobbies").contentType(MediaType.APPLICATION_JSON);

        mockMvc.perform(getRequest).andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].lobbyId", is(lobby.getLobbyId().intValue())))
                .andExpect(jsonPath("$[0].lobbyName", is(lobby.getLobbyName())))
                .andExpect(jsonPath("$[0].numberOfPlayers", is(lobby.getNumberOfPlayers())))
                .andExpect(jsonPath("$[0].voiceChat", is(lobby.isVoiceChat())))
                .andExpect(jsonPath(("$[0].userId"), is(lobby.getUserId().intValue())));
    }

    @Test
    public void createLobby_validInput() throws Exception {
       // given
        Lobby lobby = new Lobby();
        lobby.setId(1L);
        lobby.setLobbyName("Badbunny");
        lobby.setNumberOfPlayers(5);
        lobby.setVoiceChat(false);
        lobby.setUserId(1234);

        LobbyPostDTO lobbyPostDTO = new LobbyPostDTO();
        lobbyPostDTO.setLobbyName("Badbunny");
        lobbyPostDTO.setNumberOfPlayers(5);
        lobbyPostDTO.setVoiceChat(false);
        lobbyPostDTO.setUserId(1234);
        lobbyPostDTO.setToken("1");


        given(lobbyService.createLobby(Mockito.any())).willReturn(lobby);

        MockHttpServletRequestBuilder postRequest = post("/lobbies")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(lobbyPostDTO));

        mockMvc.perform(postRequest)
                .andExpect(status().isCreated())
                .andExpect(header().exists("Location"));
    }



    @Test
    public void updateExistingLobby_existingLobby() throws Exception {
        Lobby lobby = new Lobby();
        lobby.setId(1L);
        lobby.setLobbyName("Badbunny");
        lobby.setNumberOfPlayers(6);
        lobby.setNumberOfBots(1);
        lobby.setVoiceChat(true);
        lobby.setUserId(1234);
        lobby.setToken("2020");

        LobbyPutDTO lobbyPutDTO = new LobbyPutDTO();
        lobbyPutDTO.setLobbyName("Badbunny");
        lobbyPutDTO.setNumberOfPlayers(6);
        lobbyPutDTO.setVoiceChat(true);
        lobbyPutDTO.setNumberOfBots(1);
        lobbyPutDTO.setToken("2020");


        when(lobbyService.updateLobby(Mockito.any(), Mockito.any())).thenReturn(lobby);

        MockHttpServletRequestBuilder putRequest = put("/lobbies/{lobbyId}",lobby.getLobbyId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(lobbyPutDTO));

        mockMvc.perform(putRequest)
                .andExpect(status().isNoContent());
    }

    @Test
    public void updateExistingLobby_NotFound() throws Exception {
        //given
        Lobby lobby = new Lobby();
        lobby.setId(1L);
        lobby.setLobbyName("Badbunny");
        lobby.setNumberOfPlayers(6);
        lobby.setNumberOfBots(1);
        lobby.setVoiceChat(true);
        lobby.setUserId(1234);

        LobbyPutDTO lobbyPutDTO = new LobbyPutDTO();
        lobbyPutDTO.setLobbyName("Badbunny");
        lobbyPutDTO.setNumberOfPlayers(6);
        lobbyPutDTO.setVoiceChat(true);
        lobbyPutDTO.setNumberOfBots(1);

        given(lobbyService.getLobby(Mockito.anyLong())).willThrow(new NotFoundException("Could not find lobby!"));

        MockHttpServletRequestBuilder putRequest = put("/lobbies/{lobbyId}","1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(lobbyPutDTO));

        mockMvc.perform(putRequest)
                .andExpect(status().isNotFound());
    }

    @Test
    public void updateExistingLobby_wrongUser() throws Exception {
        //given => nothing as lobby does not exist

        LobbyPutDTO lobbyPutDTO = new LobbyPutDTO();
        lobbyPutDTO.setLobbyName("Badbunny");
        lobbyPutDTO.setNumberOfPlayers(6);
        lobbyPutDTO.setVoiceChat(true);
        lobbyPutDTO.setNumberOfBots(1);
        lobbyPutDTO.setToken("0000");

        given(lobbyService.updateLobby(Mockito.any(),Mockito.any())).willThrow(new UnauthorizedException("You are not allowed to change the settings of this lobby!"));

        MockHttpServletRequestBuilder putRequest = put("/lobbies/{lobbyId}","1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(lobbyPutDTO));

        mockMvc.perform(putRequest)
                .andExpect(status().isUnauthorized());
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
