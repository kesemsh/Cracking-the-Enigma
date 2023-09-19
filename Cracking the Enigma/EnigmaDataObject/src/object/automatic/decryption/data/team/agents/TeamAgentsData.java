package object.automatic.decryption.data.team.agents;

public class TeamAgentsData {
    private final String agentName;
    private final int threadsCount;
    private final int taskSize;

    public TeamAgentsData(String agentName, int threadsCount, int taskSize) {
        this.agentName = agentName;
        this.threadsCount = threadsCount;
        this.taskSize = taskSize;
    }

    public String getAgentName() {
        return agentName;
    }

    public int getThreadsCount() {
        return threadsCount;
    }

    public int getTaskSize() {
        return taskSize;
    }
}
