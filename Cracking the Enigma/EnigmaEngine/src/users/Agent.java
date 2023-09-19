package users;

import object.automatic.decryption.data.agent.progress.AgentProgressData;
import object.automatic.decryption.results.DecryptionTaskResults;
import object.user.type.UserType;

public class Agent extends User {
    private final Ally ally;
    private final int threadsAmount;
    private final int pulledTasksAmount;
    private int totalPulledTasks = 0;
    private int tasksCompleted = 0;
    private int foundCandidates = 0;

    public Agent(String username, Ally ally, int threadsAmount, int pulledTasksAmount) {
        super(username, UserType.AGENT);
        this.ally = ally;
        this.threadsAmount = threadsAmount;
        this.pulledTasksAmount = pulledTasksAmount;
    }

    public Ally getAlly() {
        return ally;
    }

    public int getThreadsAmount() {
        return threadsAmount;
    }

    public int getPulledTasksAmount() {
        return pulledTasksAmount;
    }

    public void updateTasksFinished(DecryptionTaskResults decryptionTaskResults) {
        tasksCompleted++;
        foundCandidates += decryptionTaskResults.getDecryptedMessageCandidatesResultList().size();
    }

    public void updatePulledTasks(int pulledTasksCount) {
        totalPulledTasks += pulledTasksCount;
    }

    public AgentProgressData getAgentProgressData() {
        return new AgentProgressData(getUsername(), totalPulledTasks, tasksCompleted, foundCandidates);
    }

    public synchronized void leaveBattlefield() {
        setJoinedGameTitle(null);
        totalPulledTasks = 0;
        tasksCompleted = 0;
        foundCandidates = 0;
    }
}
