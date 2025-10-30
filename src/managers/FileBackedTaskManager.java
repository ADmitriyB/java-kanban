package managers;

import enums.TaskStatus;
import enums.TaskType;
import exceptions.ManagerSaveException;
import tasks.Epic;
import tasks.Subtask;
import tasks.Task;

import java.io.*;
import java.time.Duration;
import java.time.LocalDateTime;

public class FileBackedTaskManager extends InMemoryTaskManager {


    private final File saveFile;

    public FileBackedTaskManager(File saveFile) {
        this.saveFile = saveFile;
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
                if (task instanceof Epic) {
                    manager.createEpic((Epic) task);
                } else if (task instanceof Subtask) {
                    manager.createSubtask((Subtask) task);
                } else {
                    manager.createTask(task);
                }



            }
            return manager;

        } catch (IOException e) {
            throw new ManagerSaveException("Ошибка загрузки из файла: " + file.getName(), e);
        }


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
        Duration duration = subtask.getDuration();
        LocalDateTime startTime = subtask.getStartTime();
        int epicId = subtask.getEpicId();
        long minutes = duration.toMinutes();

        return String.format("%d, %s, %s, %s, %s, %d, %s,  %d", id, type, name, status, description, minutes, startTime, epicId);
    }

    private String toStringTaskEpic(Task task) {
        int id = task.getId();
        TaskType type = task.getType();
        String name = task.getName();
        TaskStatus status = task.getStatus();
        String description = task.getDescription();
        Duration duration = task.getDuration();
        LocalDateTime startTime = task.getStartTime();
        long minutes = duration.toMinutes();


        return String.format("%d, %s, %s, %s, %s, %d, %s", id, type, name, status, description, minutes, startTime);
    }

    private void save() {
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



    private static Task parseTaskFromString(String line) {
        if (line.startsWith("id, type")) { // Проверка на строку с заголовками
            return null; // Пропускаем строку с заголовками
        }
        String[] parts = line.split(",");
        if (parts.length < 6) {
            throw new IllegalArgumentException("Некорректная строка данных: " + line);
        }

        int id = Integer.parseInt(parts[0].trim());
        TaskType type = TaskType.valueOf(parts[1].trim());
        String name = parts[2].trim();
        TaskStatus status = TaskStatus.valueOf(parts[3].trim());
        String description = parts[4].trim();
        Duration duration = Duration.ofMinutes(Long.parseLong(parts[5].trim()));
        LocalDateTime startTime = LocalDateTime.parse(parts[6].trim());


        switch (type) {
            case TASK:
                return new Task(id, type, name, status, description, duration, startTime);
            case EPIC:
                return new Epic(id, type, name, description, duration, startTime);
            case SUBTASK:
                int epicId = Integer.parseInt(parts[7].trim());
                return new Subtask(id, type, name, status, description, duration, startTime, epicId);
            default:
                throw new IllegalArgumentException("Неизвестный тип задачи: " + type);
        }


    }

}