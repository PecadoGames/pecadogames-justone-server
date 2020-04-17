package ch.uzh.ifi.seal.soprafs20.rest.mapper;

import ch.uzh.ifi.seal.soprafs20.constant.UserStatus;
import ch.uzh.ifi.seal.soprafs20.entity.User;
import ch.uzh.ifi.seal.soprafs20.rest.dto.LoginPutDTO;
import ch.uzh.ifi.seal.soprafs20.rest.dto.LogoutPutDTO;
import ch.uzh.ifi.seal.soprafs20.rest.dto.UserGetDTO;
import ch.uzh.ifi.seal.soprafs20.rest.dto.UserPostDTO;
import com.fasterxml.jackson.core.JsonParseException;
import org.junit.jupiter.api.Test;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * DTOMapperTest
 * Tests if the mapping between the internal and the external/API representation works.
 */
public class DTOMapperTest {
    @Test
    public void testCreateUser_fromUserPostDTO_toUser_success() {
        // create UserPostDTO
        UserPostDTO userPostDTO = new UserPostDTO();
        userPostDTO.setUsername("username");
        userPostDTO.setPassword("password");

        // MAP -> Create user
        User user = DTOMapper.INSTANCE.convertUserPostDTOtoEntity(userPostDTO);

        // check content
        assertEquals(userPostDTO.getUsername(), user.getUsername());
        assertEquals(userPostDTO.getPassword(), user.getPassword());
    }

    @Test
    public void testGetUser_fromUser_toUserGetDTO_success() throws ParseException, JsonParseException {

        // create User
        User user = new User();
        user.setUsername("firstname@lastname");
        user.setStatus(UserStatus.OFFLINE);
        user.setCreationDate();
        user.setBirthday(new SimpleDateFormat( "dd.MM.yyyy" ).parse( "20.05.2010" ));
        user.setToken("1");

        // MAP -> Create UserGetDTO
        UserGetDTO userGetDTO = DTOMapper.INSTANCE.convertEntityToUserGetDTO(user);

        // check content
        assertEquals(user.getId(), userGetDTO.getId());
        assertEquals(user.getUsername(), userGetDTO.getUsername());
        assertEquals(user.getCreationDate(), userGetDTO.getCreationDate());
        assertEquals(user.getBirthday(), userGetDTO.getBirthday());
        assertEquals(user.getStatus() == UserStatus.ONLINE, userGetDTO.getStatus());
    }

    @Test
    public void testGetUser_fromLoginPutDTO_toUser_success(){
        // create LoginPutDTO
        LoginPutDTO loginPutDTO = new LoginPutDTO();
        loginPutDTO.setUsername("username");
        loginPutDTO.setPassword("password");

        // MAP -> Create user
        User user = DTOMapper.INSTANCE.convertLoginPutDTOtoEntity(loginPutDTO);

        // check content
        assertEquals(loginPutDTO.getUsername(), user.getUsername());
        assertEquals(loginPutDTO.getPassword(), user.getPassword());
    }

    @Test
    public void testGetUser_fromLogoutPutDTO_toUser_success(){
        // create LogoutPutDTO
        LogoutPutDTO logoutPutDTO = new LogoutPutDTO();
        logoutPutDTO.setId(1);
        logoutPutDTO.setToken("token");

        // MAP -> Create user
        User user = DTOMapper.INSTANCE.convertLogoutPutDTOtoEntity(logoutPutDTO);

        // check content
        assertEquals(logoutPutDTO.getId(), user.getId());
        assertEquals(logoutPutDTO.getToken(), user.getToken());
    }
}
