import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

public class TaskTest {
    LocalDateTime now = LocalDateTime.now();
    // "проверьте, что экземпляры класса Task равны друг другу, если равен их id"
    @Test
    void tasksWithSameIdShouldBeEqual() {
        Task task1 = new Task(1,TaskType.TASK, "Task 1", TaskStatus.NEW,
                "Description 1", Duration.ofMinutes(15), now.plusMinutes(15));
        Task task2 = new Task(1,TaskType.TASK, "Task 2", TaskStatus.NEW,
                "Description 2", Duration.ofMinutes(15), now.plusMinutes(30));

        assertEquals(task1, task2, "Задачи с одинаковым id должны быть равны.");
    }
}