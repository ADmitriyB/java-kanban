package tasks;

import com.google.gson.annotations.SerializedName;
import enums.TaskStatus;
import enums.TaskType;
import managers.InMemoryTaskManager;

import java.time.Duration;
import java.time.LocalDateTime;

public class Subtask extends Task {

    @SerializedName("subtaskStatus")
    private TaskStatus status;
    private final int epicId;
    private final InMemoryTaskManager taskManager = new InMemoryTaskManager();

    public Subtask(int id, TaskType type, String name, TaskStatus status,
                   String description, Duration duration, LocalDateTime startTime, int epicId) {
        super(id, TaskType.SUBTASK, name, status, description, duration, startTime);

        this.status = status;
        this.epicId = epicId;

    }



    public int getEpicId() {
        return epicId;

    }

    @Override
    public void setStatus(TaskStatus status) {
        this.status = status;
        taskManager.updateEpicStatus(getEpicId());

    }

    @Override
    public TaskStatus getStatus() {
        return status;
    }


}
