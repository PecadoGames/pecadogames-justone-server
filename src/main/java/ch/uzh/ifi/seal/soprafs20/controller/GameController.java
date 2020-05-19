package ch.uzh.ifi.seal.soprafs20.controller;

import ch.uzh.ifi.seal.soprafs20.GameLogic.gameStates.GameState;
import ch.uzh.ifi.seal.soprafs20.entity.Game;
import ch.uzh.ifi.seal.soprafs20.entity.Player;
import ch.uzh.ifi.seal.soprafs20.exceptions.UnauthorizedException;
import ch.uzh.ifi.seal.soprafs20.rest.dto.CluePutDTO;
import ch.uzh.ifi.seal.soprafs20.rest.dto.GameGetDTO;
import ch.uzh.ifi.seal.soprafs20.rest.dto.MessagePutDTO;
import ch.uzh.ifi.seal.soprafs20.rest.dto.VotePutDTO;
import ch.uzh.ifi.seal.soprafs20.rest.mapper.DTOMapper;
import ch.uzh.ifi.seal.soprafs20.service.GameService;
import ch.uzh.ifi.seal.soprafs20.service.PlayerService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

@RestController
public class GameController {
    private final PlayerService playerService;
    private final GameService gameService;


    GameController(PlayerService playerService, GameService gameService) {
        this.playerService = playerService;
        this.gameService = gameService;
    }

    @GetMapping(path = "lobbies/{lobbyId}/game", produces = "application/json")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public GameGetDTO getGame(@PathVariable Long lobbyId, @RequestParam("token") String token) {
        Game game = gameService.getGame(lobbyId);

        GameGetDTO gameGetDTO = DTOMapper.INSTANCE.convertEntityToGameGetDTO(game);

        List<String> tokens = new ArrayList<>();
        for (Player player : game.getPlayers()) {
            tokens.add(player.getToken());
        }
        if (!tokens.contains(token)) {
            throw new UnauthorizedException("You are not allowed to access this game instance!");
        }
        //if guesser requests game, eliminate current word from dto
        if (game.getCurrentGuesser().getToken().equals(token) && !game.getGameState().equals(GameState.TRANSITION_STATE)) {
            gameGetDTO.setCurrentWord(null);
            gameGetDTO.getInvalidClues().clear();
        }
        return gameGetDTO;
    }

    @PutMapping(path = "lobbies/{lobbyId}/game/clue", consumes = "application/json")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @ResponseBody
    public void sendClue(@PathVariable long lobbyId, @RequestBody CluePutDTO cluePutDTO) {
        Game currentGame = gameService.getGame(lobbyId);
        Player player = playerService.getPlayer(cluePutDTO.getPlayerId());
        //If all clues were sent, sendClue returns true and the game moves on to the next state
        if (gameService.sendClue(currentGame, player, cluePutDTO)) {
            currentGame.getTimer().setCancel(true);
            currentGame.setGameState(GameState.VOTE_ON_CLUES_STATE);
            gameService.setStartTime(TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis()), currentGame);
        }
    }

    @GetMapping(path = "lobbies/{lobbyId}/game/word")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public void pickWord(@PathVariable long lobbyId, @RequestParam("token") String token) {
        Game game = gameService.getGame(lobbyId);
        if (!game.getGameState().equals(GameState.PICK_WORD_STATE)) {
            throw new UnauthorizedException("Can't choose word in current state");
        }
        if (gameService.pickWord(token, game)) {
            game.getTimer().setCancel(true);
            game.setGameState(GameState.ENTER_CLUES_STATE);
            gameService.setStartTime(TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis()), game);
        }
    }

    @GetMapping(path = "lobbies/{lobbyId}/game/timer")
    @ResponseStatus(HttpStatus.OK)
    public String getTimer(@PathVariable long lobbyId, @RequestParam String token) {
        long currentTime = TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis());
        boolean found = false;
        Game game = gameService.getGame(lobbyId);
        for (Player p : game.getPlayers()) {
            if (p.getToken().equals(token)) {
                found = true;
                break;
            }
        }
        if (!found)
            throw new UnauthorizedException("Not allowed to retrieve timer for this game!");
        if (game.getStartTimeSeconds() == null) {
            return "No timer started yet";
        }
        if (game.getGameState().equals(GameState.END_GAME_STATE)) {
            return "0";
        }
        else {

            long diff = gameService.getMaxTime(game) - (currentTime - game.getStartTimeSeconds());
            if(diff < 0)
                return "0";
            else
                return Long.toString(diff);
        }
    }

    @PutMapping(path = "lobbies/{lobbyId}/game/guess")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @ResponseBody
    public void sendGuess(@PathVariable long lobbyId, @RequestBody MessagePutDTO messagePutDTO) {
        Game game = gameService.getGame(lobbyId);
        gameService.submitGuess(game, messagePutDTO,TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis()) - game.getStartTimeSeconds());
        gameService.updateScores(game);
        game.getTimer().setCancel(true);
        game.setGameState(GameState.TRANSITION_STATE);
        gameService.setStartTime(TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis()), game);
    }

    @PutMapping(path = "lobbies/{lobbyId}/game/vote")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @ResponseBody
    public void vote(@PathVariable long lobbyId, @RequestBody VotePutDTO votePutDTO) {
        Game game = gameService.getGame(lobbyId);
        if (!game.getGameState().equals(GameState.VOTE_ON_CLUES_STATE)) {
            throw new UnauthorizedException("Can't vote on clues in current state!");
        }
        Player player = playerService.getPlayerByToken(votePutDTO.getPlayerToken());
        if (!game.getPlayers().contains(player) || game.getCurrentGuesser().equals(player)) {
            throw new UnauthorizedException("This player is not allowed to vote on clues!");
        }
        List<String> invalidWords = votePutDTO.getInvalidClues();
        if (gameService.vote(game, player, invalidWords)) {
            game.getTimer().setCancel(true);
            game.setGameState(GameState.ENTER_GUESS_STATE);
            gameService.setStartTime(TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis()), game);
        }
    }
}
