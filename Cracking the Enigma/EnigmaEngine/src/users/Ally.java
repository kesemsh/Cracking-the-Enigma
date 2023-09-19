package users;

import machine.Machine;
import machine.automatic.decryption.manager.DecryptionManager;
import object.automatic.decryption.data.agent.progress.AgentProgressData;
import object.automatic.decryption.data.dm.progress.DMProgressData;
import object.automatic.decryption.data.input.DecryptionInputData;
import object.automatic.decryption.data.task.details.DecryptionTaskDetails;
import object.user.type.UserType;

import java.util.ArrayList;
import java.util.List;

public class Ally extends User {
    private final List<Agent> agentList;
    private int taskSize;
    private DecryptionManager decryptionManager;
    private int totalTasksCount = 0;
    private int createdTasksCount = 0;
    private int tasksCompletedCount = 0;
    private boolean reset = false;

    public Ally(String alliesName) {
        super(alliesName, UserType.ALLY);
        this.agentList = new ArrayList<>();
    }

    public void startDecryptionManager(DecryptionInputData decryptionInputData, Machine machine) {
        decryptionManager = new DecryptionManager(decryptionInputData, machine, this::onTaskCreated);
        totalTasksCount = decryptionManager.getAmountOfTotalTasks();
        decryptionManager.startAutomaticDecryption();
    }

    public List<DecryptionTaskDetails> getTasks(int tasksAmount) {
        return decryptionManager.getTasks(tasksAmount);
    }

    public void setTaskSize(int taskSize) {
        this.taskSize = taskSize;
    }

    public int getTaskSize() {
        return taskSize;
    }

    public List<Agent> getAgentList() {
        return agentList;
    }

    public int getAgentsCount() {
        return agentList.size();
    }

    public void addAgent(Agent agentToAdd) {
        agentList.add(agentToAdd);
        agentToAdd.setJoinedGameTitle(getJoinedGameTitle());
    }

    public void addAgentsToBattlefield() {
        agentList.forEach(x -> x.setJoinedGameTitle(getJoinedGameTitle()));
    }

    public void removeAgentFromBattlefield(Agent agentToRemove) {
        agentList.remove(agentToRemove);
    }

    public List<AgentProgressData> getAgentsProgressData() {
        List<AgentProgressData> agentProgressDataList = new ArrayList<>();

        agentList.forEach(x -> agentProgressDataList.add(x.getAgentProgressData()));

        return agentProgressDataList;
    }

    public synchronized void onTaskCompleted() {
        tasksCompletedCount++;
    }

    public synchronized void onTaskCreated() {
        createdTasksCount++;
    }

    public synchronized DMProgressData getDMProgressData() {
        return new DMProgressData(totalTasksCount, createdTasksCount, tasksCompletedCount);
    }

    @Override
    public void setJoinedGameTitle(String joinedGameTitle) {
        super.setJoinedGameTitle(joinedGameTitle);
        reset = false;
    }

    public boolean isReset() {
        return reset;
    }

    public void onReset() {
        reset = true;
    }

    public synchronized void leaveBattlefield() {
        setJoinedGameTitle(null);
        setReady(false);
        taskSize = 0;
        totalTasksCount = 0;
        createdTasksCount = 0;
        tasksCompletedCount = 0;
        agentList.forEach(Agent::leaveBattlefield);
    }
}
