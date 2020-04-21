package ch.uzh.ifi.seal.soprafs20.rest.mapper;

import ch.uzh.ifi.seal.soprafs20.entity.Lobby;
import ch.uzh.ifi.seal.soprafs20.entity.Message;
import ch.uzh.ifi.seal.soprafs20.entity.User;
import ch.uzh.ifi.seal.soprafs20.entity.gameLogic.Game;
import ch.uzh.ifi.seal.soprafs20.rest.dto.*;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
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
    @Mapping(source = "userId", target = "userId")
    @Mapping(source = "lobbyId", target = "lobbyId")
    @Mapping(source = "currentNumPlayersAndBots", target = "currentNumPlayersAndBots")
    @Mapping(source = "maxPlayersAndBots",target = "maxPlayersAndBots")
    LobbyGetDTO convertEntityToLobbyGetDTO(Lobby lobby);

    @Mapping(source = "lobbyName", target = "lobbyName")
    @Mapping(source = "maxPlayersAndBots", target = "maxPlayersAndBots")
    @Mapping(source = "voiceChat", target = "voiceChat")
    @Mapping(source = "userId", target = "userId")
    @Mapping(source = "token", target = "token")
    @Mapping(source = "private", target = "private")
    Lobby convertLobbyPostDTOtoEntity(LobbyPostDTO lobbyPostDTO);

    @Mapping(source = "lobbyName", target = "lobbyName")
    @Mapping(source = "voiceChat", target = "voiceChat")
    @Mapping(source = "privateKey",target = "privateKey")
    @Mapping(source = "lobbyId", target = "lobbyId")
    InviteGetDTO convertEntityToInviteGetDTO(Lobby lobby);

    @Mapping(source = "userId", target = "authorId")
    @Mapping(source = "message", target = "text")
    Message convertChatPutDTOtoEntity(ChatPutDTO chatPutDTO);

    @Mapping(source = "lobbyId", target = "lobbyId")
    @Mapping(source = "userToken", target = "userToken")
    Game convertGamePostDTOtoEntity(GamePostDTO gamePostDTO);


}
