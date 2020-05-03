package ch.uzh.ifi.seal.soprafs20.controller;

import ch.uzh.ifi.seal.soprafs20.GameLogic.gameStates.GameState;
import ch.uzh.ifi.seal.soprafs20.entity.Clue;
import ch.uzh.ifi.seal.soprafs20.entity.Game;
import ch.uzh.ifi.seal.soprafs20.entity.Player;
import ch.uzh.ifi.seal.soprafs20.exceptions.BadRequestException;
import ch.uzh.ifi.seal.soprafs20.exceptions.ForbiddenException;
import ch.uzh.ifi.seal.soprafs20.exceptions.UnauthorizedException;
import ch.uzh.ifi.seal.soprafs20.rest.dto.GameGetDTO;
import ch.uzh.ifi.seal.soprafs20.rest.dto.MessagePutDTO;
import ch.uzh.ifi.seal.soprafs20.rest.dto.VotePutDTO;
import ch.uzh.ifi.seal.soprafs20.rest.mapper.DTOMapper;
import ch.uzh.ifi.seal.soprafs20.service.GameService;
import ch.uzh.ifi.seal.soprafs20.service.InternalTimerService;
import ch.uzh.ifi.seal.soprafs20.service.PlayerService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

@RestController
public class GameController {
    private final PlayerService playerService;
    private final GameService gameService;


    GameController(PlayerService playerService, GameService gameService, InternalTimerService internalTimerService){
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
        for(Player player : game.getPlayers()) {
            tokens.add(player.getToken());
        }
        if(!tokens.contains(token)) {
            throw new UnauthorizedException("You are not allowed to access this game instance!");
        }
        //if guesser requests game, eliminate current word from dto
        if(game.getCurrentGuesser().getToken().equals(token) && !game.getGameState().equals(GameState.TRANSITIONSTATE)) {
            gameGetDTO.setCurrentWord(null);
        }
        return gameGetDTO;
    }

    @PutMapping(path = "lobbies/{lobbyId}/game/clue", consumes = "application/json")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @ResponseBody
    public void sendClue(@PathVariable long lobbyId, @RequestBody MessagePutDTO messagePutDTO){
        Game currentGame = gameService.getGame(lobbyId);
        Player player = playerService.getPlayer(messagePutDTO.getPlayerId());
        Clue clue = new Clue();
        clue.setActualClue(messagePutDTO.getMessage());
        clue.setPlayerId(messagePutDTO.getPlayerId());
        System.out.println(clue.getActualClue());
        if(gameService.sendClue(currentGame, player, clue)){
            currentGame.getTimer().setCancel(true);
            currentGame.setGameState(GameState.VOTEONCLUESSTATE);
            gameService.setStartTime(TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis()),currentGame);
        }
    }

    @GetMapping(path = "lobbies/{lobbyId}/game/word")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public void pickWord(@PathVariable long lobbyId,@RequestParam("token") String token){
        Game game = gameService.getGame(lobbyId);
        if(!game.getGameState().equals(GameState.PICKWORDSTATE)){
            throw new UnauthorizedException("Can't choose word in current state");
        }
        if(gameService.pickWord(token, game)){
            game.getTimer().setCancel(true);
            game.setGameState(GameState.ENTERCLUESSTATE);
            gameService.setStartTime(TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis()),game);
        }
    }

    @GetMapping(path = "lobbies/{lobbyId}/game/timer")
    @ResponseStatus(HttpStatus.OK)
    public String getTimer(@PathVariable long lobbyId, @RequestParam String token) {
        long currentTime = TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis());
        boolean found = false;
        Game game = gameService.getGame(lobbyId);
        for(Player p : game.getPlayers()){
            if(p.getToken().equals(token)) {
                found = true;
                break;
            }
        }
        if(!found)
            throw new UnauthorizedException("Not allowed to retrieve timer for this game!");
        if(game.getStartTimeSeconds() == null){
            return "No timer started yet";
        }
        if(game.getGameState().equals(GameState.ENDGAMESTATE)){
            return "Timer is over";
        }
        else {
            long diff = 60 - (currentTime - game.getStartTimeSeconds());
            return Long.toString(diff);
        }
    }

    @PutMapping(path = "lobbies/{lobbyId}/game/guess")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @ResponseBody
    public void sendGuess(@PathVariable long lobbyId, @RequestBody MessagePutDTO messagePutDTO) {
        Game game = gameService.getGame(lobbyId);
        gameService.submitGuess(game, messagePutDTO);
        game.getTimer().setCancel(true);
        game.setGameState(GameState.TRANSITIONSTATE);
        gameService.setStartTime(TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis()),game);
    }
//
//    @PutMapping(path = "lobbies/{lobbyId}/game/transition")
//    @ResponseStatus(HttpStatus.NO_CONTENT)
//    @ResponseBody
//    public void startNewRound(@PathVariable long lobbyId, @RequestBody RequestPutDTO requestPutDTO) {
//        Game game = gameService.getGame(lobbyId);
//        if(!game.getGameState().equals(GameState.TRANSITIONSTATE)) {
//            throw new ForbiddenException("Can't start new round in current state!");
//        }
//        gameService.startNewRound(game, requestPutDTO);
//    }

    @PutMapping(path = "lobbies/{lobbyId}/game/vote")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @ResponseBody
    public void vote(@PathVariable long lobbyId, @RequestBody VotePutDTO votePutDTO) {
        Game game = gameService.getGame(lobbyId);
        if(!game.getGameState().equals(GameState.VOTEONCLUESSTATE)) {
            throw new ForbiddenException("Can't start new round in current state!");
        }
        for (Player p : game.getPlayers()){
            if(p.getToken().equals(votePutDTO.getPlayerToken())){
                Player player = playerService.getPlayer(votePutDTO.getPlayerId());
                List<String> badWords = votePutDTO.getInvalidWords();
                if(gameService.vote(game, player,badWords)){
                    game.getTimer().setCancel(true);
                    game.setGameState(GameState.TRANSITIONSTATE);
                    gameService.setStartTime(TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis()),game);
                }
            }
        }

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
