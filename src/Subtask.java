public class Subtask extends Task {

    private final int epicId;

    public Subtask(int id, String name, TaskStatus status, String description, int epicId) {
        super(id, name, status, description);
        this.epicId = epicId;
    }

    public int getEpicId() {
        return epicId;

    }
}
