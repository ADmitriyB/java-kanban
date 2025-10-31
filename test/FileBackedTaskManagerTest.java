import enums.TaskStatus;
import enums.TaskType;
import managers.FileBackedTaskManager;
import org.junit.jupiter.api.Test;
import tasks.Epic;
import tasks.Subtask;

import java.io.File;
import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;


import static org.junit.jupiter.api.Assertions.assertEquals;

class FileBackedTaskManagerTest {


    @Test
    public void testSaveAndLoad() {
        File tempFile = null;

        try {
            tempFile = File.createTempFile("temp", ".csv");


            FileBackedTaskManager manager = new FileBackedTaskManager(tempFile);
            // Создаем задачи и сохраняем их
            LocalDateTime now = LocalDateTime.now();
            manager.createEpic(new Epic(1, TaskType.EPIC,
                    "Epic1", "Description", Duration.ofMinutes(15), now));
            manager.createSubtask(new Subtask(2, TaskType.SUBTASK, "Subtask1", TaskStatus.NEW,
                    "Description", Duration.ofMinutes(15), now.plusMinutes(15), 1));

            // Загружаем задачи из файла managers.FileBackedTaskManager
            FileBackedTaskManager loadedManager = FileBackedTaskManager.loadFromFile(tempFile);
            // Проверяем, что задачи были успешно загружены
            assertEquals(manager.getAllTasks(), loadedManager.getAllTasks());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}