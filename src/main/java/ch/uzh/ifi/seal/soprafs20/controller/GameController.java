package ch.uzh.ifi.seal.soprafs20.controller;

import ch.uzh.ifi.seal.soprafs20.GameLogic.gameStates.EnterClueState;
import ch.uzh.ifi.seal.soprafs20.GameLogic.gameStates.PickWordState;
import ch.uzh.ifi.seal.soprafs20.entity.Game;
import ch.uzh.ifi.seal.soprafs20.entity.Player;
import ch.uzh.ifi.seal.soprafs20.exceptions.BadRequestException;
import ch.uzh.ifi.seal.soprafs20.exceptions.ForbiddenException;
import ch.uzh.ifi.seal.soprafs20.rest.dto.GameGetDTO;
import ch.uzh.ifi.seal.soprafs20.rest.dto.MessagePutDTO;
import ch.uzh.ifi.seal.soprafs20.rest.mapper.DTOMapper;
import ch.uzh.ifi.seal.soprafs20.service.*;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
public class GameController {
    private final LobbyService lobbyService;
    private final UserService userService;
    private final PlayerService playerService;
    private final ChatService chatService;
    private final MessageService messageService;
    private final GameService gameService;

    GameController(LobbyService lobbyService, UserService userService, PlayerService playerService, ChatService chatService, MessageService messageService, GameService gameService){
        this.lobbyService = lobbyService;
        this.userService = userService;
        this.playerService = playerService;
        this.chatService = chatService;
        this.messageService = messageService;
        this.gameService = gameService;
    }

    @GetMapping(path = "lobbies/{lobbyId}/game", produces = "application/json")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public GameGetDTO getGame(@PathVariable long lobbyId) {
        Game game = gameService.getGame(lobbyId);
        return DTOMapper.INSTANCE.convertEntityToGameGetDTO(game);
    }
    @PutMapping(path = "lobbies/{lobbyId}/game/clue",consumes = "application/json")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @ResponseBody
    public void sendClue(@PathVariable long lobbyId, @RequestBody MessagePutDTO messagePutDTO){
        Game currentGame = gameService.getGame(lobbyId);
        if(!(currentGame.getGameState() instanceof EnterClueState)){
            throw new ForbiddenException("Clues not accepted in current state");
        }
        Player player = playerService.getPlayer(messagePutDTO.getPlayerId());
        String clue = messagePutDTO.getMessage();
        currentGame = gameService.sendClue(currentGame, player, clue);
    }
    @PutMapping(path = "lobbies/{lobbyId}/game/word")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public String pickWord(@PathVariable long lobbyId){
        Game game = gameService.getGame(lobbyId);
        if(!(game.getGameState() instanceof PickWordState)){
            throw new ForbiddenException("Cant choose word in current state");
        }
        return gameService.chooseWordAtRandom(game.getWords());

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
