package object.automatic.decryption.data.dm.progress;

public class DMProgressData {
    private final int totalTasksCount;
    private final int createdTasksCount;
    private final int tasksCompletedCount;

    public DMProgressData(int totalTasksCount, int createdTasksCount, int tasksCompletedCount) {
        this.totalTasksCount = totalTasksCount;
        this.createdTasksCount = createdTasksCount;
        this.tasksCompletedCount = tasksCompletedCount;
    }

    public int getTotalTasksCount() {
        return totalTasksCount;
    }

    public int getCreatedTasksCount() {
        return createdTasksCount;
    }

    public int getTasksCompletedCount() {
        return tasksCompletedCount;
    }
}
