package ch.uzh.ifi.seal.soprafs20.rest.mapper;

import ch.uzh.ifi.seal.soprafs20.entity.*;
import ch.uzh.ifi.seal.soprafs20.rest.dto.*;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.factory.Mappers;

/**
 * DTOMapper
 * This class is responsible for generating classes that will automatically transform/map the internal representation
 * of an entity (e.g., the User) to the external/API representation (e.g., UserGetDTO for getting, UserPostDTO for creating)
 * and vice versa.
 * Additional mappers can be defined for new entities.
 * Always created one mapper for getting information (GET) and one mapper for creating information (POST).
 */
@Mapper
public interface DTOMapper {

    DTOMapper INSTANCE = Mappers.getMapper(DTOMapper.class);

    @Mapping(source = "username", target = "username")
    @Mapping(source = "password", target = "password")
    User convertUserPostDTOtoEntity(UserPostDTO userPostDTO);

    @Mapping(source = "id", target = "id")
    @Mapping(source = "username", target = "username")
    @Mapping(source = "status", target = "status")
    @Mapping(source = "avatarColor", target = "avatarColor")
    @Mapping(source = "creationDate", target = "creationDate")
    @Mapping(source = "birthday", target = "birthday")
    UserGetDTO convertEntityToUserGetDTO(User user);

    @Mapping(source = "username", target = "username")
    @Mapping(source = "password", target = "password")
    User convertLoginPutDTOtoEntity(LoginPutDTO loginPutDTO);

    @Mapping(source = "id", target = "id")
    @Mapping(source = "token", target = "token")
    LoginPutDTO convertEntityToLoginPutDTO(User user);

    @Mapping(source = "token", target = "token")
    @Mapping(source = "id", target = "id")
    User convertLogoutPutDTOtoEntity(LogoutPutDTO logoutPutDTO);

    @Mapping(source = "username", target = "username")
    @Mapping(source = "id", target = "id")
    RequestGetDTO convertEntityToRequestGetDTO(User user);

    @Mapping(source = "token", target = "token")
    @Mapping(source = "senderID", target = "id")
    User convertRequestPutDTOtoEntity(RequestPutDTO requestPutDTO);

    @Mapping(source = "lobbyName", target = "lobbyName")
    @Mapping(source = "voiceChat", target = "voiceChat")
    @Mapping(source = "hostId", target = "hostId")
    @Mapping(source = "lobbyId", target = "lobbyId")
    @Mapping(source = "maxPlayersAndBots", target = "maxPlayersAndBots")
    @Mapping(source = "private", target = "private")
    @Mapping(source = "currentNumPlayers", target = "currentNumPlayers")
    @Mapping(source = "currentNumBots", target = "currentNumBots")
    @Mapping(target = "currentNumPlayersAndBots", ignore = true)
    LobbyGetDTO convertEntityToLobbyGetDTO(Lobby lobby);

    @AfterMapping
    default void setCurrentNumPlayerAndBots(Lobby lobby, @MappingTarget LobbyGetDTO lobbyGetDTO) {
        if(lobbyGetDTO.getCurrentNumPlayers()!= null && lobbyGetDTO.getCurrentNumBots() != null) {
            lobbyGetDTO.setCurrentNumPlayersAndBots(lobbyGetDTO.getCurrentNumPlayers() + lobbyGetDTO.getCurrentNumBots());
        }
    }

    @Mapping(source = "lobbyName", target = "lobbyName")
    @Mapping(source = "maxPlayersAndBots", target = "maxPlayersAndBots")
    @Mapping(source = "voiceChat", target = "voiceChat")
    @Mapping(source = "hostId", target = "hostId")
    @Mapping(source = "hostToken", target = "hostToken")
    @Mapping(source = "private", target = "private")
    Lobby convertLobbyPostDTOtoEntity(LobbyPostDTO lobbyPostDTO);

    @Mapping(source = "lobbyName", target = "lobbyName")
    @Mapping(source = "voiceChat", target = "voiceChat")
    @Mapping(source = "privateKey",target = "privateKey")
    @Mapping(source = "lobbyId", target = "lobbyId")
    InviteGetDTO convertEntityToInviteGetDTO(Lobby lobby);

    @Mapping(source = "playerId", target = "authorId")
    @Mapping(source = "message", target = "text")
    Message convertMessagePutDTOtoEntity(MessagePutDTO messagePutDTO);

    @Mapping(source = "id", target = "id")
    @Mapping(source = "username", target = "username")
    @Mapping(source = "score", target = "score")
    @Mapping(source = "clueIsSent", target = "clueIsSent")
    PlayerGetDTO convertEntityToPlayerGetDTO(Player player);

    @Mapping(source = "actualClue", target = "actualClue")
    @Mapping(source = "playerId", target = "playerId")
    ClueGetDTO convertEntityToClueGetDTO(Clue clue);

    @Mapping(source = "lobbyId",target = "lobbyId")
    @Mapping(source = "roundsPlayed",target = "roundsPlayed")
    @Mapping(source = "gameState",target = "gameState")
    @Mapping(source = "overallScore",target = "overallScore")
    @Mapping(source = "currentWord", target = "currentWord")
    @Mapping(source = "lobbyName", target = "lobbyName")
    @Mapping(source = "specialGame", target = "specialGame")
    @Mapping(source = "currentGuess", target = "currentGuess")
    GameGetDTO convertEntityToGameGetDTO(Game game);

    @Mapping(source = "messageId", target = "messageId")
    @Mapping(source = "authorId", target = "authorId")
    @Mapping(source = "authorUsername", target = "authorUsername")
    @Mapping(source = "text", target = "text")
    @Mapping(source = "creationDate", target = "creationDate")
    MessageGetDTO convertEntityToMessageGetDTO(Message message);

    @Mapping(source = "lobbyId", target = "lobbyId")
    ChatGetDTO convertEntityToChatGetDTO(Chat chat);

    LobbyScoreGetDTO convertEntityToLobbyScoreGetDTO(LobbyScore lb);
}
