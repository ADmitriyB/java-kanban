import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;

public class Epic extends Task {
    public ArrayList<Integer> subtaskIds;


    public Epic(int id, TaskType type, String name, String description, Duration duration, LocalDateTime startTime) {
        super(id, TaskType.EPIC, name, TaskStatus.NEW, description, duration, startTime);
        subtaskIds = new ArrayList<>();
    }

    public LocalDateTime getEndTime(InMemoryTaskManager taskManager) {
        LocalDateTime endTime = null;
        for (int subtaskId : subtaskIds) {
            Task subtask = taskManager.getSubtaskById(subtaskId);
            if (endTime == null || subtask.getEndTime().isAfter(endTime)) {
                endTime = subtask.getEndTime();
            }
        }
        return endTime;
    }

    public ArrayList<Integer> getSubtaskIds() {
        return subtaskIds;
    }


    public void addSubtaskId(int subtaskId) {
        subtaskIds.add(subtaskId);
    }

    public void removeSubtaskId(int subtaskId) {
        subtaskIds.remove((Integer) subtaskId);
    }

    public void clearSubtasks() {
        subtaskIds.clear();
    }
}