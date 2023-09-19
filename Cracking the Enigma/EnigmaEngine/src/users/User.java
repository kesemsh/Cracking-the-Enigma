package users;

import object.user.type.UserType;

import java.util.Objects;

public class User {
    private final String username;
    private final UserType userType;
    private String joinedGameTitle;
    private boolean ready;

    public User(String username, UserType userType) {
        this.username = username;
        this.userType = userType;
        joinedGameTitle = null;
    }

    public String getUsername() {
        return username;
    }

    public UserType getUserType() {
        return userType;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return Objects.equals(username, user.username);
    }

    @Override
    public int hashCode() {
        return Objects.hash(username);
    }

    public void setJoinedGameTitle(String joinedGameTitle) {
        this.joinedGameTitle = joinedGameTitle;
    }

    public String getJoinedGameTitle() {
        return joinedGameTitle;
    }

    public boolean isReady() {
        return ready;
    }

    public void setReady(boolean ready) {
        this.ready = ready;
    }

    public boolean isJoinedToBattlefield() {
        return joinedGameTitle != null;
    }
}
