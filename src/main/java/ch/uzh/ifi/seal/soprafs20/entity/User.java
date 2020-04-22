package ch.uzh.ifi.seal.soprafs20.entity;

import ch.uzh.ifi.seal.soprafs20.constant.AvatarColor;
import ch.uzh.ifi.seal.soprafs20.constant.UserStatus;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonParseException;
import org.springframework.lang.Nullable;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import java.io.Serializable;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

/**
 * Internal User Representation
 * This class composes the internal representation of the user and defines how the user is stored in the database.
 * Every variable will be mapped into a database field with the @Column annotation
 * - nullable = false -> this cannot be left empty
 * - unique = true -> this value must be unqiue across the database -> composes the primary key
 */
@Entity
@Table(name = "USER")
public class User implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @NotEmpty
	@Column(nullable = false, unique = true) 
	private String username;

	@Column(nullable = true, unique = true)
	private String token;

	@Column(nullable = false)
	private UserStatus status;

	@Column(nullable = false)
    private String password;

	@Column(nullable = false)
    private Date creationDate;

    @JsonFormat(pattern="dd.MM.yyyy")
    private Date birthday;

    @Column
    private AvatarColor avatarColor;

	@Column
    private int score;

   @ManyToMany
    private Set<User> friendRequests = new HashSet<>();

   @ManyToMany
   private Set<User> friendList = new HashSet<>();

   @ManyToMany
   private Set<Lobby> lobbyInvites = new HashSet<>();

    public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	public UserStatus getStatus() {
		return status;
	}

	public void setStatus(UserStatus status) {
		this.status = status;
	}

	public void setAvatarColor(AvatarColor color) { this.avatarColor = color; }

	public AvatarColor getAvatarColor() { return this.avatarColor; }

	public void setPassword(String password) { this.password = password; }

	public String getPassword() { return password; }

	public void setCreationDate() {
        this.creationDate = new Date();
    }

	public Date getCreationDate(){return creationDate;}

    @JsonFormat(pattern="dd.MM.yyyy")
    @JsonInclude(value = JsonInclude.Include.NON_EMPTY)
    public void setBirthday(Date birthday) throws JsonParseException {
        this.birthday = birthday;
    }

    public Date getBirthday() {
	    return birthday;
    }

    public int getScore(){return score;}

    public void setScore(int score){this.score = score;}

    public Set<User> getFriendRequests() {
        return friendRequests;
    }

    public void setFriendRequests(User user) {
        friendRequests.add(user);
    }

    public Set<User> getFriendList() {
        return friendList;
    }

    public void setFriendList(User user) {
        friendList.add(user);
    }

    public Set<Lobby> getLobbyInvites() {
        return lobbyInvites;
    }

    public void setLobbyInvites(Lobby lobbyInvites) {
        this.lobbyInvites.add(lobbyInvites);
    }
}
