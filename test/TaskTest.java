import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class TaskTest {
    // "проверьте, что экземпляры класса Task равны друг другу, если равен их id"
    @Test
    void tasksWithSameIdShouldBeEqual() {
        Task task1 = new Task(1, "Task 1", TaskStatus.NEW, "Description 1");
        Task task2 = new Task(1, "Task 2", TaskStatus.NEW, "Description 2");

        assertEquals(task1, task2, "Задачи с одинаковым id должны быть равны.");
    }
}