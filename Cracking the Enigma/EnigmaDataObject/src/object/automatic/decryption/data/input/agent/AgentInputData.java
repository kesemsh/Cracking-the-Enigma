package object.automatic.decryption.data.input.agent;

public class AgentInputData {
    private final String allyName;
    private final int threadsAmount;
    private final int pulledTasksAmount;

    public AgentInputData(String allyName, int threadsAmount, int pulledTasksAmount) {
        this.allyName = allyName;
        this.threadsAmount = threadsAmount;
        this.pulledTasksAmount = pulledTasksAmount;
    }

    public String getAllyName() {
        return allyName;
    }

    public int getThreadsAmount() {
        return threadsAmount;
    }

    public int getPulledTasksAmount() {
        return pulledTasksAmount;
    }
}
