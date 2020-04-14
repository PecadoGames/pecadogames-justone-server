package ch.uzh.ifi.seal.soprafs20.controller;


import ch.uzh.ifi.seal.soprafs20.entity.Chat;
import ch.uzh.ifi.seal.soprafs20.entity.Lobby;
import ch.uzh.ifi.seal.soprafs20.entity.Message;
import ch.uzh.ifi.seal.soprafs20.entity.User;
import ch.uzh.ifi.seal.soprafs20.exceptions.BadRequestException;
import ch.uzh.ifi.seal.soprafs20.exceptions.NotFoundException;
import ch.uzh.ifi.seal.soprafs20.exceptions.UnauthorizedException;
import ch.uzh.ifi.seal.soprafs20.rest.dto.LobbyAcceptancePutDTO;
import ch.uzh.ifi.seal.soprafs20.rest.dto.LobbyPostDTO;
import ch.uzh.ifi.seal.soprafs20.rest.dto.LobbyPutDTO;
import ch.uzh.ifi.seal.soprafs20.service.ChatService;
import ch.uzh.ifi.seal.soprafs20.service.LobbyService;
import ch.uzh.ifi.seal.soprafs20.service.UserService;
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

import java.text.SimpleDateFormat;
import java.util.*;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@WebMvcTest(LobbyController.class)
public class LobbyControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private LobbyService lobbyService;
    @MockBean
    private UserService userService;
    @MockBean
    private ChatService chatService;

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
    public void createLobby_validInput_publicLobby() throws Exception {
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
    public void createLobby_validInput_privateLobby() throws Exception {
        // given
        Lobby lobby = new Lobby();
        lobby.setId(1L);
        lobby.setLobbyName("Badbunny");
        lobby.setNumberOfPlayers(5);
        lobby.setVoiceChat(false);
        lobby.setUserId(1234);
        lobby.setPrivate(true);
        lobby.setPrivateKey("1010");

        LobbyPostDTO lobbyPostDTO = new LobbyPostDTO();
        lobbyPostDTO.setLobbyName("Badbunny");
        lobbyPostDTO.setNumberOfPlayers(5);
        lobbyPostDTO.setVoiceChat(false);
        lobbyPostDTO.setUserId(1234);
        lobbyPostDTO.setPrivate(true);
        lobbyPostDTO.setToken("1");


        given(lobbyService.createLobby(Mockito.any())).willReturn(lobby);

        MockHttpServletRequestBuilder postRequest = post("/lobbies")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(lobbyPostDTO));

        mockMvc.perform(postRequest)
                .andExpect(status().isCreated())
                .andExpect(content().string(lobby.getPrivateKey()))
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

    @Test
    public void handleLobbyInvite_validInput_success() throws Exception {
        Lobby lobby = new Lobby();
        lobby.setId(1L);
        lobby.setLobbyName("Badbunny");
        lobby.setNumberOfPlayers(5);
        lobby.setVoiceChat(false);
        lobby.setUserId(1234);

        User testUser = new User();
        testUser.setId(1L);
        testUser.setToken("testToken");
        testUser.setLobbyInvites(lobby);

        LobbyAcceptancePutDTO lobbyAcceptancePutDTO = new LobbyAcceptancePutDTO();
        lobbyAcceptancePutDTO.setAccepterId(testUser.getId());
        lobbyAcceptancePutDTO.setAccepterToken(testUser.getToken());
        lobbyAcceptancePutDTO.setAccepted(true);
        lobbyAcceptancePutDTO.setLobbyId(lobby.getLobbyId());

        given(lobbyService.getLobby(Mockito.any())).willReturn(lobby);
        given(userService.getUser(Mockito.any())).willReturn(testUser);

        MockHttpServletRequestBuilder putRequest = put("/lobbies/{lobbyId}/acceptances", "1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(lobbyAcceptancePutDTO));

        mockMvc.perform(putRequest)
                .andExpect(status().isOk());
    }

    @Test
    public void getChat_validInput_returnsJson() throws Exception {
        Calendar calndr = Calendar.getInstance();
        String tmZ = calndr.getTimeZone().getDisplayName();
        SimpleDateFormat sF = new SimpleDateFormat( "dd.MM.yyyy hh:mm:ss");
        sF.setTimeZone(TimeZone.getTimeZone(tmZ));

        Date birthday = sF.parse( "20.05.2010 10:52:30");
        Message message1 = new Message();
        message1.setAuthorId(1L);
        message1.setMessageId(2L);
        message1.setCreationDate(birthday);
        message1.setText("Hello world");

        Date birthday2 = sF.parse( "20.05.2010 10:52:33");
        Message message2 = new Message();
        message2.setAuthorId(1L);
        message2.setMessageId(4L);
        message2.setCreationDate(birthday2);
        message2.setText("Hello world");

        Chat chat = new Chat();
        chat.setLobbyId(3L);
        chat.setMessages(message1);
        chat.setMessages(message2);

        given(chatService.getChat(Mockito.any())).willReturn(chat);

        MockHttpServletRequestBuilder getRequest = get("/lobbies/" + chat.getLobbyId() + "/chat").contentType(MediaType.APPLICATION_JSON);


        mockMvc.perform(getRequest)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.lobbyId", is(chat.getLobbyId().intValue())))
                .andExpect(jsonPath("$.messages[0].messageId", is(message1.getMessageId().intValue())))
                .andExpect(jsonPath("$.messages[0].authorId", is(message1.getAuthorId().intValue())))
                .andExpect(jsonPath("$.messages[0].text", is(message1.getText())))
                .andExpect(jsonPath("$.messages[0].creationDate", is(sF.format(message1.getCreationDate()))))
                .andExpect(jsonPath("$.messages[1].messageId", is(message2.getMessageId().intValue())))
                .andExpect(jsonPath("$.messages[1].authorId", is(message2.getAuthorId().intValue())))
                .andExpect(jsonPath("$.messages[1].text", is(message2.getText())))
                .andExpect(jsonPath("$.messages[1].creationDate", is(sF.format(message2.getCreationDate()))));
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
