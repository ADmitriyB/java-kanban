import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryTaskManagerTest {
    private TaskManager manager;
    LocalDateTime now = LocalDateTime.now();

    @BeforeEach
    void newTaskManager() {
        manager = Managers.getDefault();
        manager.createTask(new Task(0, TaskType.TASK, "Task 1", TaskStatus.NEW, "Description 1", Duration.ofMinutes(10), now));
        manager.createTask(new Task(0, TaskType.TASK, "Task 2", TaskStatus.NEW, "Description 2", Duration.ofMinutes(10), now.plusMinutes(15)));

        Epic epic1 = manager.createEpic(new Epic(0, TaskType.EPIC, "Epic 1", "Description Epic 1", Duration.ofMinutes(10), now.plusMinutes(30)));
        Epic epic2 = manager.createEpic(new Epic(0, TaskType.EPIC, "Epic 2", "Description Epic 2", Duration.ofMinutes(10), now.plusMinutes(45)));

        Subtask subtask1 = manager.createSubtask(new Subtask(0, TaskType.SUBTASK, "Subtask 1", TaskStatus.NEW, "Description Subtask 1", Duration.ofMinutes(10), now.plusMinutes(60), epic1.getId()));
        Subtask subtask2 = manager.createSubtask(new Subtask(0, TaskType.SUBTASK, "Subtask 2", TaskStatus.DONE, "Description Subtask 2", Duration.ofMinutes(10), now.plusMinutes(75), epic1.getId()));
        Subtask subtask3 = manager.createSubtask(new Subtask(0, TaskType.SUBTASK, "Subtask 3", TaskStatus.IN_PROGRESS, "Description Subtask 3", Duration.ofMinutes(10), now.plusMinutes(90), epic2.getId()));

    }


    @Test
    void isGoodAddAndDeleteEpicAndHisSubtask() {
        assertNotNull(manager.getEpicById(3), "отсутствует");
        manager.deleteEpicById(3);
        assertNull(manager.getSubtaskById(5), "не удален");
        assertNull(manager.getEpicById(3), "не удален");
    }

    @Test
    void isGoodGetAllTasksAndDeleteAllTasks() {
        assertNotNull(manager.getTaskById(2), "отсутствует");
        manager.deleteAllTasks();
        assertNull(manager.getTaskById(2), "не удален");
    }

    // проверьте, что задачи с заданным id и сгенерированным id не конфликтуют внутри менеджера;
    @Test
    void doesNotConflictId() {
        assertNull(manager.getTaskById(8), "отсутствует");
        Task task = manager.createTask(new Task(1, TaskType.TASK, "Task", TaskStatus.NEW, "Description", Duration.ofMinutes(15), LocalDateTime.now()));
        assertNotNull(manager.getTaskById(8), "не удален");
    }

    // создайте тест, в котором проверяется неизменность задачи (по всем полям) при добавлении задачи в менеджер
    @Test
    void isNamelessnessAfterCreate() {
        Task task = manager.getSubtaskById(5);
        assertEquals("Subtask 1", task.getName());
        assertEquals(TaskStatus.NEW, task.getStatus());
        assertEquals("Description Subtask 1", task.getDescription());
    }

    @Test
    void isCorrectGetPrioritizedTasks() {
        List<Task> prioritizedTasks = manager.getPrioritizedTasks();

        // Определяем ожидаемый порядок задач
        // Предположим, что задачи должны быть отсортированы по времени начала
        List<Task> expectedTasks = List.of(manager.getTaskById(1),  // Задача с наименьшим временем начала
                manager.getTaskById(2), manager.getEpicById(3), manager.getEpicById(4), manager.getSubtaskById(6) // Следующая задача по времени
        );

        // Проверяем, что полученный список совпадает с ожидаемым
        assertEquals(expectedTasks, prioritizedTasks);

        for (int i = 0; i < prioritizedTasks.size() - 1; i++) {
            Task currentTask = prioritizedTasks.get(i);
            Task nextTask = prioritizedTasks.get(i + 1);
            if (manager.tasksOverlap(currentTask, nextTask)) {
                System.out.println("Задачи пересекаются: " + currentTask + " и " + nextTask);
            }
        }
    }


}