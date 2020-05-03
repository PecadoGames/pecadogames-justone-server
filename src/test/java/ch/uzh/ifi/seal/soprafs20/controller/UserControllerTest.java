package ch.uzh.ifi.seal.soprafs20.controller;

import ch.uzh.ifi.seal.soprafs20.constant.UserStatus;
import ch.uzh.ifi.seal.soprafs20.entity.User;
import ch.uzh.ifi.seal.soprafs20.exceptions.*;
import ch.uzh.ifi.seal.soprafs20.repository.UserRepository;
import ch.uzh.ifi.seal.soprafs20.rest.dto.*;
import ch.uzh.ifi.seal.soprafs20.service.UserService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;

import org.junit.jupiter.api.Test;
import org.mockito.Mock;
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
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * UserControllerTest
 * This is a WebMvcTest which allows to test the UserController i.e. GET/POST request without actually sending them over the network.
 * This tests if the UserController works.
 */
@WebMvcTest(UserController.class)
public class UserControllerTest {

    @Mock
    private UserRepository userRepository;

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @Test
    public void givenUsers_whenGetUsers_thenReturnJsonArray() throws Exception {
        Calendar calndr = Calendar.getInstance();
        String tmZ = calndr.getTimeZone().getDisplayName();

        // given
        User user = new User();
        SimpleDateFormat sF = new SimpleDateFormat( "dd.MM.yyyy");
        sF.setTimeZone(TimeZone.getTimeZone(tmZ));

        Date birthday = sF.parse( "20.05.2010 ");
        user.setUsername("firstname@lastname");
        user.setStatus(UserStatus.OFFLINE);
        user.setBirthday(birthday);
        user.setCreationDate();
        user.setToken("1");

        List<User> allUsers = Collections.singletonList(user);

        // this mocks the UserService -> we define above what the userService should return when getUsers() is called
        given(userService.getUsers()).willReturn(allUsers);

        // when
        MockHttpServletRequestBuilder getRequest = get("/users").contentType(MediaType.APPLICATION_JSON);
        SimpleDateFormat format = new SimpleDateFormat("dd.MM.yyyy");

        format.setTimeZone(TimeZone.getTimeZone(tmZ));

        // then
        mockMvc.perform(getRequest).andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].username", is(user.getUsername())))
                .andExpect(jsonPath("$[0].logged_in", is(user.getStatus() == UserStatus.ONLINE)))
                .andExpect(jsonPath("$[0].creation_date", is(
                        user.getCreationDate().toInstant().toString().replace("Z", "+0000"))))
                .andExpect(jsonPath(("$[0].birthday"), is(
                        format.format(user.getBirthday()))))
                .andExpect(jsonPath("$[0].token", is(user.getToken())));
    }

    @Test
    public void givenUser_whenGetUser_thenReturnJson() throws Exception {
        User user = new User();
        user.setId(1L);
        user.setUsername("testUsername");
        user.setPassword("test");
        user.setToken("1");
        user.setStatus(UserStatus.ONLINE);
        user.setCreationDate();

        given(userService.getUser(user.getId())).willReturn(user);

        MockHttpServletRequestBuilder getRequest = get("/users/" + user.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(user));

        mockMvc.perform(getRequest).andExpect(status().isOk())
                .andExpect(jsonPath("$.username", is(user.getUsername())))
                .andExpect(jsonPath("$.logged_in",is(true)))
                .andExpect(jsonPath("$.id", is(user.getId().intValue())));
    }

    @Test
    public void createUser_validInput_userCreated() throws Exception {
        // given
        User user = new User();

        UserPostDTO userPostDTO = new UserPostDTO();
        userPostDTO.setUsername("testUsername");
        userPostDTO.setPassword("test");

        given(userService.createUser(Mockito.any())).willReturn(user);

        // when/then -> do the request + validate the result
        MockHttpServletRequestBuilder postRequest = post("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(userPostDTO));

        // then
        mockMvc.perform(postRequest)
                .andExpect(status().isCreated())
                .andExpect(header().exists("Location"));
    }

    @Test
    public void createUser_invalidInput_userNotCreated() throws Exception {
        UserPostDTO userPostDTO = new UserPostDTO();
        userPostDTO.setUsername("testUsername");
        userPostDTO.setPassword("1");

        given(userService.createUser(Mockito.any())).willThrow(new ConflictException("exception"));

        MockHttpServletRequestBuilder postRequest = post("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(userPostDTO));

        mockMvc.perform(postRequest)
                .andExpect(status().isConflict());
    }

    @Test
    public void loginUser_whenUserIsOffline() throws Exception {
        User user = new User();

        LoginPutDTO loginPutDTO = new LoginPutDTO();
        loginPutDTO.setUsername("testUsername");
        loginPutDTO.setPassword("1");

        given(userService.loginUser(Mockito.any())).willReturn(user);

        MockHttpServletRequestBuilder putRequest = put("/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(loginPutDTO));

        mockMvc.perform(putRequest)
                .andExpect(status().isOk());
    }

    @Test
    public void loginUser_whenUserAlreadyLoggedIn() throws Exception {
        LoginPutDTO loginPutDTO = new LoginPutDTO();
        loginPutDTO.setUsername("testUsername");
        loginPutDTO.setPassword("1");

        Mockito.doThrow(new NoContentException("message")).when(userService).loginUser(Mockito.any());

        MockHttpServletRequestBuilder putRequest = put("/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(loginPutDTO));

        mockMvc.perform(putRequest)
                .andExpect(status().isNoContent());
    }

    @Test
    public void loginUser_invalidInput() throws Exception {
        LoginPutDTO loginPutDTO = new LoginPutDTO();
        loginPutDTO.setUsername("testUsername");
        loginPutDTO.setPassword("1");

        Mockito.doThrow(new NotFoundException("message")).when(userService).loginUser(Mockito.any());

        MockHttpServletRequestBuilder putRequest = put("/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(loginPutDTO));

        mockMvc.perform(putRequest)
                .andExpect(status().isNotFound());
    }

    @Test
    public void logoutUser_validInput() throws Exception {
        User user = new User();

        LogoutPutDTO logoutPutDTO = new LogoutPutDTO();
        logoutPutDTO.setId(1L);
        logoutPutDTO.setToken("1");

        given(userRepository.findById(Mockito.any())).willReturn(java.util.Optional.of(user));

        MockHttpServletRequestBuilder putRequest = put("/logout")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(logoutPutDTO));

        mockMvc.perform(putRequest)
                .andExpect(status().isNoContent());
    }

    @Test
    public void logoutUser_unauthorized() throws Exception {
        LogoutPutDTO logoutPutDTO = new LogoutPutDTO();
        logoutPutDTO.setId(1L);
        logoutPutDTO.setToken("1");

        Mockito.doThrow(new UnauthorizedException("message")).when(userService).logoutUser(Mockito.any());

        MockHttpServletRequestBuilder putRequest = put("/logout")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(logoutPutDTO));

        mockMvc.perform(putRequest)
                .andExpect(status().isUnauthorized());
    }

    @Test
    public void logoutUser_UserNotFound() throws Exception {
        LogoutPutDTO logoutPutDTO = new LogoutPutDTO();
        logoutPutDTO.setId(1L);
        logoutPutDTO.setToken("1");

        Mockito.doThrow(new NotFoundException("message")).when(userService).logoutUser(Mockito.any());

        MockHttpServletRequestBuilder putRequest = put("/logout")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(logoutPutDTO));

        mockMvc.perform(putRequest)
                .andExpect(status().isNotFound());
    }


    @Test
    public void updateUser_invalidBirthday() throws InvalidFormatException {
        ObjectMapper objectMapper = new ObjectMapper();
        String json = "{\"token\" : \"1\", \"birthday\" : \"123\"}";

        assertThrows(InvalidFormatException.class,() ->{UserPutDTO userPutDTO = objectMapper.readValue(json,UserPutDTO.class);});
    }


    @Test
    public void updateUser_validInput() throws Exception {
        User user = new User();
        user.setId(1L);
        user.setUsername("testUsername");
        user.setToken("testToken");

        UserPutDTO userPutDTO = new UserPutDTO();
        userPutDTO.setUsername("updatedUsername");
        userPutDTO.setToken("testToken");

        MockHttpServletRequestBuilder putRequest = put("/users/" + user.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(userPutDTO));

        mockMvc.perform(putRequest)
                .andExpect(status().isNoContent());
    }

    @Test
    public void givenFriendRequests_whenGetFriendRequests_thenReturnJsonArray() throws Exception {
        User user1 = new User();
        user1.setId(1L);
        user1.setToken("testToken");

        User user2 = new User();
        user2.setId(2L);

        user1.setFriendRequests(user2);

        given(userService.getUser(Mockito.any())).willReturn(user1);

        // when
        MockHttpServletRequestBuilder getRequest = get("/users/" + user1.getId() + "/friendRequests")
                .contentType(MediaType.APPLICATION_JSON)
                .param("token", user1.getToken());
        //then
        mockMvc.perform(getRequest)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(user2.getId().intValue())));
    }

    @Test
    public void sendFriendRequest_validInput_success() throws Exception {
        User user1 = new User();
        user1.setId(1L);

        User user2 = new User();
        user2.setId(2L);
        user2.setToken("testToken");

        RequestPutDTO requestPutDTO = new RequestPutDTO();
        requestPutDTO.setSenderID(user2.getId());
        requestPutDTO.setToken(user2.getToken());

        MockHttpServletRequestBuilder putRequest = put("/users/" + user1.getId() + "/friendRequests")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(requestPutDTO));

        mockMvc.perform(putRequest)
                .andExpect(status().isNoContent());
    }

    /**
     * Helper Method to convert userPostDTO into a JSON string such that the input can be processed
     * Input will look like this: {"name": "Test User", username": "testUsername"}
     * @param object
     * @return string
     */
    private String asJsonString(final Object object) {
        try {
            return new ObjectMapper().writeValueAsString(object);
        }
        catch (JsonProcessingException e) {
            throw new BadRequestException(String.format("The request body could not be created.%s", e.toString()));
        }
    }
}