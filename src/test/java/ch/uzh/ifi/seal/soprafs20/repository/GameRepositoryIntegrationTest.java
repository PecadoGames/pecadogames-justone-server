package ch.uzh.ifi.seal.soprafs20.repository;

import ch.uzh.ifi.seal.soprafs20.GameLogic.WordReader;
import ch.uzh.ifi.seal.soprafs20.GameLogic.gameStates.GameState;
import ch.uzh.ifi.seal.soprafs20.entity.Game;
import ch.uzh.ifi.seal.soprafs20.entity.Player;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
public class GameRepositoryIntegrationTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private GameRepository gameRepository;

    @Test
    public void findById_success() {
        //given
        Player player = new Player();
        player.setId(1L);
        player.setUsername("testUsername");

        entityManager.persist(player);
        entityManager.flush();

        Game game = new Game();
        game.setLobbyId(1L);
        game.setSpecialGame(true);
        game.setRoundsPlayed(0);
        game.setOverallScore(0);
        game.addPlayer(player);

        entityManager.merge(game);
        entityManager.flush();

        Optional<Game> foundGame = gameRepository.findByLobbyId(game.getLobbyId());

        assertTrue(foundGame.isPresent());
        Game actualGame = foundGame.get();
        assertEquals(actualGame.getLobbyId(), game.getLobbyId());
        assertEquals(actualGame.getRoundsPlayed(), game.getRoundsPlayed());
        assertEquals(actualGame.getOverallScore(), game.getOverallScore());
        assertTrue(actualGame.getPlayers().contains(player));
    }

    @Test
    public void findById_unsuccessful() {
        Game game = new Game();
        game.setLobbyId(1L);
        game.setSpecialGame(true);
        game.setRoundsPlayed(0);
        game.setOverallScore(0);

        entityManager.merge(game);
        entityManager.flush();

        Optional<Game> foundGame = gameRepository.findByLobbyId(100L);
        assertFalse(foundGame.isPresent());
    }

    @Test
    public void createGame_validInput_findById_success() {
        // Use the Word Reader class to read random words into the game's list of words
        WordReader reader = new WordReader();

        Player player = new Player();
        player.setId(1L);
        player.setUsername("testUsername");

        entityManager.persist(player);
        entityManager.flush();

        Game newGame = new Game();
        newGame.setLobbyId(1L);
        newGame.setGameState(GameState.PICKWORDSTATE);
        newGame.addPlayer(player);
        newGame.setCurrentGuesser(player);
        newGame.setRoundsPlayed(0);
        newGame.setWords(reader.getRandomWords(13));

        entityManager.merge(newGame);
        entityManager.flush();

        Optional<Game> foundGame = gameRepository.findByLobbyId(newGame.getLobbyId());

        assertTrue(foundGame.isPresent());
        Game actualGame = foundGame.get();

        assertEquals(actualGame.getLobbyId(), newGame.getLobbyId());
        assertEquals(actualGame.getRoundsPlayed(), newGame.getRoundsPlayed());
        assertEquals(actualGame.getOverallScore(), newGame.getOverallScore());
        assertTrue(actualGame.getPlayers().contains(player));
        assertEquals(13, actualGame.getWords().size());
    }
}
