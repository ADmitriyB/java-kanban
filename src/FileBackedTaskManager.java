import exceptions.ManagerSaveException;

import java.io.*;


public class FileBackedTaskManager extends InMemoryTaskManager {


    private File saveFile;

    public FileBackedTaskManager(File saveFile) {
        this.saveFile = saveFile;
    }

    @Override
    public Task createTask(Task task) {
        super.createTask(task);
        save();
        return task;
    }

    @Override
    public Epic createEpic(Epic epic) {
        super.createEpic(epic);
        save();
        return epic;
    }

    @Override
    public Subtask createSubtask(Subtask subtask) {
        super.createSubtask(subtask);
        save();
        return subtask;
    }

    @Override
    public void deleteTaskById(int id) {
        super.deleteTaskById(id);
        save();
    }

    @Override
    public void deleteEpicById(int id) {
        super.deleteEpicById(id);
        save();
    }

    @Override
    public void deleteSubtaskById(int id) {
        super.deleteSubtaskById(id);
        save();
    }

    @Override
    public void deleteAllTasks() {
        super.deleteAllTasks();
        save();
    }

    @Override
    public void deleteAllEpics() {
        super.deleteAllEpics();
        save();
    }

    @Override
    public void deleteAllSubtasks() {
        super.deleteAllSubtasks();
        save();
    }

    private String toStringSubtask(Subtask subtask) {
        int id = subtask.getId();
        TaskType type = subtask.getType();
        String name = subtask.getName();
        TaskStatus status = subtask.getStatus();
        String description = subtask.getDescription();
        int epicId = subtask.getEpicId();

        return String.format("%d, %s, %s, %s, %s, %d", id, type, name, status, description, epicId);
    }

    private String toStringTaskEpic(Task task) {
        int id = task.getId();
        TaskType type = task.getType();
        String name = task.getName();
        TaskStatus status = task.getStatus();
        String description = task.getDescription();


        return String.format("%d, %s, %s, %s, %s", id, type, name, status, description);
    }

    public void save() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(saveFile))) {
            writer.write("id, type, name, status, description, epicId");
            writer.newLine();

            for (Task task : getAllTasks()) {
                writer.write(toStringTaskEpic(task));
                writer.newLine();
            }
            for (Epic epic : getAllEpics()) {
                writer.write(toStringTaskEpic(epic));
                writer.newLine();
            }
            for (Subtask subtask : getAllSubtasks()) {
                writer.write(toStringSubtask(subtask));
                writer.newLine();
            }

        } catch (IOException e) {
            throw new ManagerSaveException("Ошибка при сохранении данных", e);
        }
    }

    public static FileBackedTaskManager loadFromFile(File file) {
        if (!file.exists()) {
            throw new ManagerSaveException("Файл не найден: " + file.getPath());
        }
        try (BufferedReader bufferedReader = new BufferedReader(new FileReader(file))) {
            FileBackedTaskManager manager = new FileBackedTaskManager(file);
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                if (line.isEmpty()) {
                    continue;
                }
                Task task = parseTaskFromString(line);
                manager.createTask(task);


            }
            return manager;

        } catch (IOException e) {
            throw new ManagerSaveException("Ошибка загрузки из файла: " + file.getName(), e);
        }


    }

    private static Task parseTaskFromString(String line) {
        String[] parts = line.split(",");
        if (parts.length < 5) {
            throw new IllegalArgumentException("Некорректная строка данных: " + line);
        }

        int id = Integer.parseInt(parts[0].trim());
        TaskType type = TaskType.valueOf(parts[1].trim());
        String name = parts[2].trim();
        TaskStatus status = TaskStatus.valueOf(parts[3].trim());
        String description = parts[4].trim();
        int epicId = Integer.parseInt(parts[5].trim());

        switch (type) {
            case TASK:
                return new Task(id, type, name, status, description);
            case EPIC:
                return new Epic(id, type, name, description);
            case SUBTASK:
                return new Subtask(id, type, name, status, description, epicId);
            default:
                throw new IllegalArgumentException("Неизвестный тип задачи: " + type);
        }


    }

}