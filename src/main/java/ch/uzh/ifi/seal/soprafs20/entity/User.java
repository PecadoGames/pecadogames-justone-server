package ch.uzh.ifi.seal.soprafs20.entity;

import ch.uzh.ifi.seal.soprafs20.constant.UserStatus;
import ch.uzh.ifi.seal.soprafs20.exceptions.NotFoundException;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

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
	@GeneratedValue
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

	@Column(nullable = true)
    private Date birthday;

	@Column
    private int score;

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

	public void setPassword(String password){this.password = password;}

	public String getPassword(){return password;}

	public void setCreationDate(){this.creationDate = new java.util.Date();}

	public Date getCreationDate(){return creationDate;}

    public void setBirthday(String birthday) throws ParseException {
        SimpleDateFormat formatter = new SimpleDateFormat("dd.MM.yyyy");
        try {
            if (birthday.matches("^(1[0-2]|0[1-9]).(3[01]|[12][0-9]|0[1-9]).[0-9]{4}$")) {
//                birthday = birthday + " 01:00:00";
                this.birthday = formatter.parse(birthday);
            } else {
                throw new NotFoundException("Invalid date!");
            }
        } catch (ParseException pa){
            throw new NotFoundException("Invalid date!");
        }
//        throw new NotFoundException("Invalid date!");
    }


    public Date getBirthday() {
        return birthday;
    }

    public int getScore(){return score;}

    public void setScore(int score){this.score = score;}
}
