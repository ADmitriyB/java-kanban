import enums.TaskType;
import org.junit.jupiter.api.Test;
import tasks.Epic;

import java.time.Duration;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class EpicTest {
    LocalDateTime now = LocalDateTime.now();

    // проверьте, что наследники класса tasks.Task равны друг другу, если равен их id;
    @Test
    void epicsWithSameIdShouldBeEqual() {
        Epic epic1 = new Epic(
                1, TaskType.EPIC, "tasks.Task 1", "Description 1", Duration.ofMinutes(15), now);
        Epic epic2 = new Epic(
                1, TaskType.EPIC, "tasks.Task 2", "Description 2", Duration.ofMinutes(15), now.plusMinutes(15));

        assertEquals(epic1, epic2, "Задачи с одинаковым id должны быть равны.");
    }
}