import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ManagersTest {
    // убедитесь, что утилитарный класс всегда возвращает проинициализированные и готовые к работе экземпляры менеджеров;
    @Test
    void getDefaultManagersInMemoryTaskManager() {
        TaskManager taskManager = Managers.getDefault();
        assertNotNull(taskManager, "менеджер задач не готов");
    }

    @Test
    void getDefaultManagersInMemoryHistoryManager() {
        HistoryManager historyManager = Managers.getDefaultHistory();
        assertNotNull(historyManager, "менеджер истории не готов");
    }
}