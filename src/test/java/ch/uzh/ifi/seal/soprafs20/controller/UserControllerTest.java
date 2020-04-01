package ch.uzh.ifi.seal.soprafs20.controller;

import ch.uzh.ifi.seal.soprafs20.constant.UserStatus;
import ch.uzh.ifi.seal.soprafs20.entity.User;
import ch.uzh.ifi.seal.soprafs20.exceptions.*;
import ch.uzh.ifi.seal.soprafs20.repository.UserRepository;
import ch.uzh.ifi.seal.soprafs20.rest.dto.LoginPutDTO;
import ch.uzh.ifi.seal.soprafs20.rest.dto.LogoutPutDTO;
import ch.uzh.ifi.seal.soprafs20.rest.dto.UserPostDTO;
import ch.uzh.ifi.seal.soprafs20.rest.dto.UserPutDTO;
import ch.uzh.ifi.seal.soprafs20.service.UserService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.web.server.ResponseStatusException;

import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
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
        // given
        User user = new User();
        Date birthday = new SimpleDateFormat("dd.MM.yyyy hh:mm:ss").parse("01.01.2001 01:00:00");
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

        // then
        mockMvc.perform(getRequest).andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].username", is(user.getUsername())))
                .andExpect(jsonPath("$[0].logged_in", is(user.getStatus() == UserStatus.ONLINE)))
                .andExpect(jsonPath("$[0].creation_date", is(
                        user.getCreationDate().toInstant().toString().replace("Z", "+0000"))))
                .andExpect(jsonPath(("$[0].birthday"), is(
                        user.getBirthday().toInstant().toString().replace("Z", ".000+0000"))))
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
        user.setId(1L);
        user.setUsername("testUsername");
        user.setPassword("test");
        user.setToken("1");
        user.setStatus(UserStatus.ONLINE);

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
        user.setId(1L);
        user.setUsername("testUsername");
        user.setToken("1");
        user.setPassword("1");
        user.setStatus(UserStatus.ONLINE);

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
        User user = new User();
        user.setId(1L);
        user.setUsername("testUsername");
        user.setToken("1");
        user.setPassword("1");
        user.setStatus(UserStatus.OFFLINE);

        LoginPutDTO loginPutDTO = new LoginPutDTO();
        loginPutDTO.setUsername("testUsername");
        loginPutDTO.setPassword("1");

        given(userService.loginUser(Mockito.any())).willThrow(new NoContentException("message"));

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

        given(userService.loginUser(Mockito.any())).willThrow(new NotFoundException("message"));

        MockHttpServletRequestBuilder putRequest = put("/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(loginPutDTO));

        mockMvc.perform(putRequest)
                .andExpect(status().isNotFound());
    }

    @Test
    public void logoutUser_validInput() throws Exception {
        User user = new User();
        user.setId(1L);
        user.setToken("1");
        user.setUsername("username");
        user.setPassword("1");
        user.setStatus(UserStatus.ONLINE);

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
    public void logoutUser_Unauthorized() throws Exception {
        LogoutPutDTO logoutPutDTO = new LogoutPutDTO();
        logoutPutDTO.setId(1L);
        logoutPutDTO.setToken("1");

        given(userService.logoutUser(Mockito.any())).willThrow(new UnauthorizedException("message"));

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

        given(userService.logoutUser(Mockito.any())).willThrow(new NotFoundException("message"));

        MockHttpServletRequestBuilder putRequest = put("/logout")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(logoutPutDTO));

        mockMvc.perform(putRequest)
                .andExpect(status().isNotFound());
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