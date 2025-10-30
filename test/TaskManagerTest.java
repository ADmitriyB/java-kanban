import enums.TaskStatus;
import enums.TaskType;
import managers.Managers;
import managers.TaskManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tasks.Epic;
import tasks.Subtask;

import java.time.Duration;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

abstract class TaskManagerTest<T extends TaskManager> {
    private TaskManager manager;
    LocalDateTime now = LocalDateTime.now();

    @BeforeEach
    void newTaskManager() {
        manager = Managers.getDefault();
        Epic epic1 = manager.createEpic(new Epic(0, TaskType.EPIC, "tasks.Epic 1",
                "Description tasks.Epic 1", Duration.ofMinutes(10), now.plusMinutes(30)));

        Subtask subtask1 = manager.createSubtask(new Subtask(0, TaskType.SUBTASK, "tasks.Subtask 1",
                TaskStatus.NEW, "Description tasks.Subtask 1", Duration.ofMinutes(10), now.plusMinutes(60), epic1.getId()));

        Subtask subtask2 = manager.createSubtask(new Subtask(0, TaskType.SUBTASK, "tasks.Subtask 2",
                TaskStatus.NEW, "Description tasks.Subtask 2", Duration.ofMinutes(10), now.plusMinutes(75), epic1.getId()));
    }

    @Test
    public void testSubtaskHasEpic() {
        assertNotNull(manager.getEpicById(1));
    }

    @Test
    public void testEpicStatusNew() {
        assertEquals(TaskStatus.NEW, manager.getEpicById(1).getStatus());
    }

    @Test
    public void testEpicStatusDone() {
        manager.getSubtaskById(2).setStatus(TaskStatus.DONE);
        manager.getSubtaskById(3).setStatus(TaskStatus.DONE);
        assertEquals(TaskStatus.DONE, manager.getEpicById(1).getStatus());
    }

    @Test
    public void testEpicStatusInProgress() {
        manager.getSubtaskById(2).setStatus(TaskStatus.DONE);
        assertEquals(TaskStatus.IN_PROGRESS, manager.getEpicById(1).getStatus());
    }

    @Test
    public void testTasksOverlap() {
        assertFalse(manager.tasksOverlap(manager.getSubtaskById(2), manager.getSubtaskById(3)));
    }

}
