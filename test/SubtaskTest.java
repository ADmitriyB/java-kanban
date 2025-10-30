import enums.TaskStatus;
import enums.TaskType;
import org.junit.jupiter.api.Test;
import tasks.Subtask;

import java.time.Duration;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class SubtaskTest {
    LocalDateTime now = LocalDateTime.now();
    // проверьте, что наследники класса tasks.Task равны друг другу, если равен их id;
    @Test
    void subtasksWithSameIdShouldBeEqual() {
        Subtask subtask1 = new Subtask(1, TaskType.TASK,"tasks.Task 1", TaskStatus.NEW,
                "Description 1", Duration.ofMinutes(15), now, 2);
        Subtask subtask2 = new Subtask(1, TaskType.TASK, "tasks.Task 2", TaskStatus.NEW,
                "Description 2", Duration.ofMinutes(15),now.plusMinutes(15), 2);

        assertEquals(subtask1, subtask2, "Задачи с одинаковым id должны быть равны.");
    }
}