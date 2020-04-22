package ch.uzh.ifi.seal.soprafs20.controller;

import ch.uzh.ifi.seal.soprafs20.service.*;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class GameController {
    private final LobbyService lobbyService;
    private final UserService userService;
    private final ChatService chatService;
    private final MessageService messageService;
    private final GameService gameService;

    GameController(LobbyService lobbyService, UserService userService, ChatService chatService, MessageService messageService, GameService gameService){
        this.lobbyService = lobbyService;
        this.userService = userService;
        this.chatService = chatService;
        this.messageService = messageService;
        this.gameService = gameService;
    }


}
