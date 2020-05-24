package ch.uzh.ifi.seal.soprafs20.service;

import ch.uzh.ifi.seal.soprafs20.constant.AvatarColor;
import ch.uzh.ifi.seal.soprafs20.entity.Player;
import ch.uzh.ifi.seal.soprafs20.entity.User;
import ch.uzh.ifi.seal.soprafs20.exceptions.ConflictException;
import ch.uzh.ifi.seal.soprafs20.exceptions.NotFoundException;
import ch.uzh.ifi.seal.soprafs20.exceptions.UnauthorizedException;
import ch.uzh.ifi.seal.soprafs20.repository.PlayerRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class PlayerServiceTest {

    @Mock
    private PlayerRepository playerRepository;

    @InjectMocks
    private PlayerService playerService;

    private Player testPlayer;

    @BeforeEach
    void setup() {
        MockitoAnnotations.initMocks(this);

        // given
        testPlayer = new Player();
        testPlayer.setId(1L);
        testPlayer.setUsername("testUsername");
        testPlayer.setToken("testToken");
        testPlayer.setAvatarColor(AvatarColor.PURPLE);

        // when -> any object is being save in the userRepository -> return the dummy testUser
        Mockito.when(playerRepository.save(Mockito.any())).thenReturn(testPlayer);
        Mockito.when(playerRepository.findById(Mockito.any())).thenReturn(java.util.Optional.ofNullable(testPlayer));
        Mockito.when(playerRepository.findByToken(Mockito.any())).thenReturn(java.util.Optional.ofNullable(testPlayer));
    }

    @Test
    void getPlayer_validInput_success() {
        Player player = playerService.getPlayer(testPlayer.getId());

        Mockito.verify(playerRepository, Mockito.times(1)).findById(Mockito.any());
        
        assertEquals(1L, player.getId());
        assertEquals(testPlayer.getUsername(), player.getUsername());
        assertEquals(testPlayer.getToken(), player.getToken());
        assertEquals(testPlayer.getAvatarColor(), player.getAvatarColor());
    }

    @Test
    void getPlayer_invalidInput_throwsException() {
        Mockito.when(playerRepository.findById(Mockito.any())).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> playerService.getPlayer(2L));
    }

    @Test
    void getPlayerByToken_validInput_success() {
        Player player = playerService.getPlayerByToken(testPlayer.getToken());

        Mockito.verify(playerRepository, Mockito.times(1)).findByToken(Mockito.any());

        assertEquals(1L, player.getId());
        assertEquals(testPlayer.getUsername(), player.getUsername());
        assertEquals(testPlayer.getToken(), player.getToken());
        assertEquals(testPlayer.getAvatarColor(), player.getAvatarColor());
    }

    @Test
    void getPlayerByToken_invalidInput_throwsException() {
        Mockito.when(playerRepository.findByToken(Mockito.any())).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> playerService.getPlayerByToken("wrongToken"));
    }

    @Test
    void convertUserToPlayer_validInput_success() {
        User user = new User();
        user.setId(1L);
        user.setUsername("testUsername");
        user.setToken("testToken");
        user.setAvatarColor(AvatarColor.PURPLE);

        Mockito.when(playerRepository.findById(Mockito.any())).thenReturn(Optional.empty());

        Player player = playerService.convertUserToPlayer(user);

        assertEquals(user.getId(), player.getId());
        assertEquals(user.getUsername(), player.getUsername());
        assertEquals(user.getToken(), player.getToken());
        assertEquals(user.getAvatarColor(), player.getAvatarColor());
    }

    @Test
    void convertUserToPlayer_alreadyConverted_throwsException() {
        User user = new User();
        user.setId(1L);
        user.setUsername("testUsername");
        user.setToken("testToken");
        user.setAvatarColor(AvatarColor.PURPLE);

        assertThrows(ConflictException.class, () -> playerService.convertUserToPlayer(user));
    }

    @Test
    void checkPlayerToken_invalidInput_throwsException() {
        assertThrows(UnauthorizedException.class, () -> playerService.checkPlayerToken(testPlayer.getToken(), "wrongToken"));
    }
}
