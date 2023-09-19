package users.manager;

import object.user.type.UserType;
import users.Agent;
import users.Ally;
import users.UBoat;
import users.User;

import java.util.*;

public class UserManager {
    private final Set<String> allUsersNames;
    private final Set<UBoat> uBoatUsers;
    private final Set<Ally> allyUsers;
    private final Set<Agent> agentUsers;

    public UserManager() {
        allUsersNames = new HashSet<>();
        uBoatUsers = new HashSet<>();
        allyUsers = new HashSet<>();
        agentUsers = new HashSet<>();
    }

    public synchronized void addUBoat(String username) {
        allUsersNames.add(username);
        uBoatUsers.add(new UBoat(username));
    }

    public synchronized void addAllies(String username) {
        allUsersNames.add(username);
        allyUsers.add(new Ally(username));
    }

    public synchronized void addAgent(String username, Ally ally, Integer threadsAmount, Integer pulledTasksAmount) {
        Agent agentToAdd = new Agent(username, ally, threadsAmount, pulledTasksAmount);

        allUsersNames.add(username);
        agentUsers.add(agentToAdd);
        //ally.addAgent(agentToAdd);
    }

    public synchronized void removeUser(UserType userType, String username) {
        allUsersNames.remove(username);
        switch (userType) {
            case UBOAT:
                uBoatUsers.removeIf(x -> x.getUsername().equals(username));
                break;
            case ALLY:
                allyUsers.removeIf(x -> x.getUsername().equals(username));
                break;
            case AGENT:
                agentUsers.removeIf(x -> x.getUsername().equals(username));
                break;
        }
    }

    public synchronized Set<User> getUsers(UserType userType) {
        switch (userType) {
            case UBOAT:
                return Collections.unmodifiableSet(uBoatUsers);
            case ALLY:
                return Collections.unmodifiableSet(allyUsers);
            case AGENT:
                return Collections.unmodifiableSet(agentUsers);
        }

        return null;
    }

    public synchronized List<String> getAllAlliesNames() {
        List<String> result = new ArrayList<>();

        allyUsers.forEach(x -> result.add(x.getUsername()));

        return result;
    }

    public synchronized User getUser(String username, UserType userType) {
        switch (userType) {
            case UBOAT:
                return uBoatUsers.stream().filter(x -> x.getUsername().equals(username)).findAny().orElse(null);
            case ALLY:
                return allyUsers.stream().filter(x -> x.getUsername().equals(username)).findAny().orElse(null);
            case AGENT:
                return agentUsers.stream().filter(x -> x.getUsername().equals(username)).findAny().orElse(null);
        }

        return null;
    }

    public boolean doesUserExist(String username) {
        return allUsersNames.contains(username);
    }

    public boolean isUserOfType(String username, UserType userType) {
        switch (userType) {
            case UBOAT:
                return uBoatUsers.stream().anyMatch(x -> x.getUsername().equals(username));
            case ALLY:
                return allyUsers.stream().anyMatch(x -> x.getUsername().equals(username));
            case AGENT:
                return agentUsers.stream().anyMatch(x -> x.getUsername().equals(username));
        }

        return false;
    }
}
