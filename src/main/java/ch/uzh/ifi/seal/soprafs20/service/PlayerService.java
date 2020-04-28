package ch.uzh.ifi.seal.soprafs20.service;

import ch.uzh.ifi.seal.soprafs20.entity.Player;
import ch.uzh.ifi.seal.soprafs20.entity.User;
import ch.uzh.ifi.seal.soprafs20.exceptions.ConflictException;
import ch.uzh.ifi.seal.soprafs20.exceptions.NotFoundException;
import ch.uzh.ifi.seal.soprafs20.repository.PlayerRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Transactional
public class PlayerService {

    private final Logger log = LoggerFactory.getLogger(PlayerService.class);

    private final PlayerRepository playerRepository;

    @Autowired
    public PlayerService(PlayerRepository playerRepository) { this.playerRepository = playerRepository; }

    public Player getPlayer(Long id) {
        Player player;
        Optional<Player> optional = playerRepository.findById(id);
        if (optional.isPresent()) {
            player = optional.get();
            return player;
        }
        else {
            throw new NotFoundException("Couldn't find player. Did you convert the user into a player?");
        }
    }

    public Player convertUserToPlayer(User user) {
        if(playerRepository.findById(user.getId()).isPresent()) {
            throw new ConflictException("This player has already been converted!");
        }
        Player player = new Player();
        player.setId(user.getId());
        player.setUsername(user.getUsername());
        player.setToken(user.getToken());
        player.setAvatarColor(user.getAvatarColor());

        player = playerRepository.save(player);
        playerRepository.flush();

        log.debug("Converted user with id {} to player", user.getId());

        return player;
    }

    public void deletePlayer(Player player) {
        playerRepository.delete(player);
    }
}
