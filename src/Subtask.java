public class Subtask extends Task {

    private final int epicId;

    public Subtask(int id, TaskType type, String name, TaskStatus status, String description, int epicId) {
        super(id, TaskType.SUBTASK, name, status, description);
        this.epicId = epicId;
    }

    public int getEpicId() {
        return epicId;

    }
}
