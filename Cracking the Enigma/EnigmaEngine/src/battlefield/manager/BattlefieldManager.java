package battlefield.manager;

import battlefield.Battlefield;

import java.util.HashMap;
import java.util.Map;

public class BattlefieldManager {
    private final Map<String, Battlefield> gameTitleToBattlefield;

    public BattlefieldManager() {
        gameTitleToBattlefield = new HashMap<>();
    }

    public synchronized boolean doesGameTitleAlreadyExist(String gameTitle) {
        return gameTitleToBattlefield.keySet().stream().anyMatch(x -> x.equals(gameTitle));
    }

    public synchronized void addBattlefield(Battlefield battlefield) {
        gameTitleToBattlefield.put(battlefield.getGameTitle(), battlefield);
    }

    public Map<String, Battlefield> getGameTitleToBattlefield() {
        return gameTitleToBattlefield;
    }

    public Battlefield getBattlefield(String gameTitle) {
        return gameTitleToBattlefield.get(gameTitle);
    }

    public void removeBattlefield(String gameTitle) {
        gameTitleToBattlefield.remove(gameTitle);
    }
}
