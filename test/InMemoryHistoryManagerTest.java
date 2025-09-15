import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryHistoryManagerTest {

    private HistoryManager historyManager;


    @BeforeEach
    void newTaskManager() {
        historyManager = Managers.getDefaultHistory();
    }

    // убедитесь, что задачи, добавляемые в HistoryManager, сохраняют предыдущую версию задачи и её данных.

    @Test
    void addAndSaveLastVersion() {
        Task task = new Task(0, "Task 1", TaskStatus.NEW, "Description 1");
        historyManager.addTaskToHistory(task);
        final List<Task> history = historyManager.getHistory();
        assertNotNull(history, "После добавления задачи, история не должна быть пустой.");
        assertEquals(1, history.size(), "После добавления задачи, история не должна быть пустой.");
        Task TaskGet = history.get(0);
        assertEquals("Task 1", TaskGet.getName());
        assertEquals(TaskStatus.NEW, TaskGet.getStatus());
        assertEquals("Description 1", TaskGet.getDescription());

    }


}