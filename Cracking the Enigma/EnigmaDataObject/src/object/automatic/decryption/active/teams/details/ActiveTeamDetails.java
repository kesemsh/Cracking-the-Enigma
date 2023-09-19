package object.automatic.decryption.active.teams.details;

public class ActiveTeamDetails {
    private final String alliesName;
    private final int agentsCount;
    private final int taskSize;

    public ActiveTeamDetails(String alliesName, int agentsCount, int taskSize) {
        this.alliesName = alliesName;
        this.agentsCount = agentsCount;
        this.taskSize = taskSize;
    }

    public String getAlliesName() {
        return alliesName;
    }

    public int getAgentsCount() {
        return agentsCount;
    }

    public int getTaskSize() {
        return taskSize;
    }
}
