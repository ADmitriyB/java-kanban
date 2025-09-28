import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;


import static org.junit.jupiter.api.Assertions.assertEquals;

class FileBackedTaskManagerTest {


    @Test
    public void testSaveAndLoad() {
        File tempFile = null;

        try {
            tempFile = File.createTempFile("temp", ".csv");


            FileBackedTaskManager manager = new FileBackedTaskManager(tempFile);
            // Создаем задачи и сохраняем их
            manager.createEpic(new Epic(1, TaskType.EPIC, "Epic1", "Description"));
            manager.createSubtask(new Subtask(2, TaskType.SUBTASK, "Subtask1", TaskStatus.NEW, "Description", 1));

            // Загружаем задачи из файла FileBackedTaskManager
            FileBackedTaskManager loadedManager = FileBackedTaskManager.loadFromFile(tempFile);
            // Проверяем, что задачи были успешно загружены
            assertEquals(manager.getAllTasks(), loadedManager.getAllTasks());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}