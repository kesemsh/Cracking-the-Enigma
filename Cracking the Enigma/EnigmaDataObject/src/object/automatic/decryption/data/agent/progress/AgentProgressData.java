package object.automatic.decryption.data.agent.progress;

public class AgentProgressData {
    private final String agentName;
    private final int totalPulledTasks;
    private final int tasksCompleted;
    private final int foundCandidates;

    public AgentProgressData(String agentName, int totalPulledTasks, int tasksCompleted, int foundCandidates) {
        this.agentName = agentName;
        this.totalPulledTasks = totalPulledTasks;
        this.tasksCompleted = tasksCompleted;
        this.foundCandidates = foundCandidates;
    }

    public String getAgentName() {
        return agentName;
    }

    public int getTotalPulledTasks() {
        return totalPulledTasks;
    }

    public int getTasksCompleted() {
        return tasksCompleted;
    }

    public int getFoundCandidates() {
        return foundCandidates;
    }
}
