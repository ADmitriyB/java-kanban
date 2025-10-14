import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;


public class InMemoryTaskManager implements TaskManager {
    private int nextId = 1;
    private final HashMap<Integer, Task> tasks = new HashMap<>();
    private final HashMap<Integer, Epic> epics = new HashMap<>();
    private final HashMap<Integer, Subtask> subtasks = new HashMap<>();
    private final HistoryManager historyManager = Managers.getDefaultHistory();
    public final Set<Task> prioritizedTasks = new TreeSet<>(Comparator.comparing(Task::getStartTime));


    @Override
    public List<Task> getPrioritizedTasks() {
        return prioritizedTasks.stream().toList();
    }


    @Override
    public boolean tasksOverlap(Task task1, Task task2) {
        LocalDateTime startTime1 = task1.getStartTime();
        LocalDateTime endTime1 = task1.getEndTime();
        LocalDateTime startTime2 = task2.getStartTime();
        LocalDateTime endTime2 = task2.getEndTime();

        return !(startTime1.isAfter(endTime2) || startTime2.isAfter(endTime1));
    }

    @Override
    public void checkOverlaps() {
        List<Task> prioritizedTask = getPrioritizedTasks();
        for (int i = 0; i < prioritizedTask.size() - 1; i++) {
            Task currentTask = prioritizedTask.get(i);
            Task nextTask = prioritizedTask.get(i + 1);
            if (tasksOverlap(currentTask, nextTask)) {
                System.out.println("Задачи пересекаются: " + currentTask + " и " + nextTask);
            }
        }
    }

    @Override
    public ArrayList<Task> getAllTasks() {
        return new ArrayList<>(tasks.values());
    }

    @Override
    public void deleteAllTasks() {
        tasks.clear();
    }

    @Override
    public Task getTaskById(int id) {
        Task task = tasks.get(id);
        historyManager.addTaskToHistory(task);
        return task;
    }

    @Override
    public Task createTask(Task task) {
        if (task == null) return null;
        int id = generateNextId();
        Task newTask = new Task(
                id, task.getType(), task.getName(), task.getStatus(), task.getDescription(), task.getDuration(), task.getStartTime());
        tasks.put(id, newTask);
        prioritizedTasks.add(newTask);
        checkOverlaps();
        return newTask;
    }

    @Override
    public void updateTask(Task task) {
        if (task != null && tasks.containsKey(task.getId())) {
            tasks.put(task.getId(), task);
            checkOverlaps();
        }
    }

    @Override
    public void deleteTaskById(int id) {
        tasks.remove(id);
    }

    @Override
    public ArrayList<Epic> getAllEpics() {
        return new ArrayList<>(epics.values());
    }

    @Override
    public void deleteAllEpics() {
        epics.clear();
        subtasks.clear();
    }

    @Override
    public Epic getEpicById(int id) {
        Epic epic = epics.get(id);
        historyManager.addTaskToHistory(epic);
        return epic;
    }

    @Override
    public Epic createEpic(Epic epic) {
        if (epic == null) return null;
        int id = generateNextId();
        Epic newEpic = new Epic(
                id, epic.getType(), epic.getName(), epic.getDescription(), epic.getDuration(), epic.getStartTime());
        epics.put(id, newEpic);
        prioritizedTasks.add(newEpic);
        checkOverlaps();
        return newEpic;
    }

    @Override
    public void updateEpic(Epic epic) {
        if (epic != null && epics.containsKey(epic.getId())) {
            Epic savedEpic = epics.get(epic.getId());
            savedEpic.setName(epic.getName());
            savedEpic.setDescription(epic.getDescription());
            checkOverlaps();
        }
    }

    @Override
    public void deleteEpicById(int id) {
        Epic epic = epics.remove(id);
        if (epic != null) {
            for (int subtaskId : epic.getSubtaskIds()) {
                subtasks.remove(subtaskId);
            }
        }
    }

    @Override
    public ArrayList<Subtask> getEpicSubtasks(int epicId) {
        Epic epic = epics.get(epicId);
        if (epic == null) {
            return new ArrayList<>();
        }
        return (ArrayList<Subtask>) epic.getSubtaskIds().stream()
                .map(subtasks::get)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    @Override
    public ArrayList<Subtask> getAllSubtasks() {
        return new ArrayList<>(subtasks.values());
    }

    @Override
    public void deleteAllSubtasks() {
        subtasks.clear();
        epics.clear();

    }

    @Override
    public Subtask getSubtaskById(int id) {
        Subtask subtask = subtasks.get(id);
        historyManager.addTaskToHistory(subtask);
        return subtask;
    }

    @Override
    public Subtask createSubtask(Subtask subtask) {
        if (subtask == null) return null;
        int epicId = subtask.getEpicId();
        if (!epics.containsKey(epicId)) return null;
        int id = generateNextId();
        Subtask newSubtask = new Subtask(id, subtask.getType(), subtask.getName(),
                subtask.getStatus(), subtask.getDescription(), subtask.getDuration(), subtask.getStartTime(), epicId);
        subtasks.put(id, newSubtask);
        Epic epic = epics.get(epicId);
        epic.addSubtaskId(id);
        updateEpicTime(epicId);
        prioritizedTasks.add(newSubtask);
        checkOverlaps();
        updateEpicStatus(epicId);
        return newSubtask;
    }

    @Override
    public void updateSubtask(Subtask subtask) {
        if (subtask != null && subtasks.containsKey(subtask.getId())) {
            subtasks.put(subtask.getId(), subtask);
            updateEpicTime(subtask.getEpicId());
            checkOverlaps();
        }
    }

    @Override
    public void deleteSubtaskById(int id) {
        Subtask subtask = subtasks.remove(id);
        if (subtask != null) {
            Epic epic = epics.get(subtask.getEpicId());
            if (epic != null) {
                epic.removeSubtaskId(id);
                updateEpicStatus(epic.getId());
                updateEpicTime(epic.getId());
            }
        }
    }

    @Override
    public List<Task> getHistory() {
        return historyManager.getHistory();
    }

    @Override
    public void updateEpicStatus(int epicId) {
        Epic epic = epics.get(epicId);
        if (epic == null) return;
        ArrayList<Integer> subtaskIds = epic.getSubtaskIds();
        if (subtaskIds.isEmpty()) {
            epic.setStatus(TaskStatus.NEW);
            return;
        }
        boolean allNew = true;
        boolean allDone = true;
        for (int subtaskId : subtaskIds) {
            Subtask subtask = subtasks.get(subtaskId);
            if (subtask == null) continue;
            TaskStatus status = subtask.getStatus();
            if (status != TaskStatus.NEW) allNew = false;
            if (status != TaskStatus.DONE) allDone = false;
        }
        if (allNew) {
            epic.setStatus(TaskStatus.NEW);
        } else if (allDone) {
            epic.setStatus(TaskStatus.DONE);
        } else {
            epic.setStatus(TaskStatus.IN_PROGRESS);
        }
    }

    public void updateEpicTime(int epicId) {
        Epic epic = epics.get(epicId);
        epic.setStartTime(getStartTimeEpic(epicId));
        epic.setDuration(getDurationEpic(epicId));
    }

    public LocalDateTime getEndTimeEpic(int epicId) {
        LocalDateTime endTime = null;
        Epic epic = epics.get(epicId);
        ArrayList<Integer> subtaskIds = epic.getSubtaskIds();
        for (int subtaskId : subtaskIds) {
            Task subtask = getSubtaskById(subtaskId);
            if (endTime == null || subtask.getEndTime().isAfter(endTime)) {
                endTime = subtask.getEndTime();
            }
        }
        return endTime;
    }

    public LocalDateTime getStartTimeEpic(int epicId) {
        LocalDateTime startTime = null;
        Epic epic = epics.get(epicId);
        ArrayList<Integer> subtaskIds = epic.getSubtaskIds();
        for (int subtaskId : subtaskIds) {
            Task subtask = getSubtaskById(subtaskId);
            if (startTime == null || subtask.getStartTime().isBefore(startTime)) {
                startTime = subtask.getStartTime();
            }
        }
        return startTime;
    }

    public Duration getDurationEpic(int epicId) {
        LocalDateTime startTime = getStartTimeEpic(epicId);
        LocalDateTime endTime = getEndTimeEpic(epicId);
        return Duration.between(startTime, endTime);

    }


    private int generateNextId() {
        return nextId++;
    }
}
