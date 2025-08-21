import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryTaskManagerTest {
    private TaskManager manager;

    @BeforeEach
    void newTaskManager() {
        manager = Managers.getDefault();
        Task task1 = manager.createTask(new Task(0, "Task 1", TaskStatus.NEW, "Description 1"));
        Task task2 = manager.createTask(new Task(0, "Task 2", TaskStatus.NEW, "Description 2"));

        Epic epic1 = manager.createEpic(new Epic(0, "Epic 1", "Description Epic 1"));
        Epic epic2 = manager.createEpic(new Epic(0, "Epic 2", "Description Epic 2"));

        Subtask subtask1 = manager.createSubtask(
                new Subtask(0, "Subtask 1", TaskStatus.NEW, "Description Subtask 1", epic1.getId()));
        Subtask subtask2 = manager.createSubtask(
                new Subtask(0, "Subtask 2", TaskStatus.DONE, "Description Subtask 2", epic1.getId()));
        Subtask subtask3 = manager.createSubtask(
                new Subtask(0, "Subtask 3", TaskStatus.IN_PROGRESS, "Description Subtask 3", epic2.getId()));

    }

    @Test
    void isGoodAddAndDeleteSubtask() {
        assertNotNull(manager.getSubtaskById(7), "отсутствует");
        manager.deleteSubtask(7);
        assertNull(manager.getSubtaskById(7), "не удален");
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
        Task task = manager.createTask(new Task(1, "Task", TaskStatus.NEW, "Description"));
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
}