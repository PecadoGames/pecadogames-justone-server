package ch.uzh.ifi.seal.soprafs20.rest.dto;

import ch.uzh.ifi.seal.soprafs20.entity.User;

import java.util.Set;

public class RequestGetDTO {

    private Long id;
    private Set<User> friendRequests;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Set<User> getFriendRequests() {
        return friendRequests;
    }

    public void setFriendRequests(User user) {
        friendRequests.add(user);
    }
}
