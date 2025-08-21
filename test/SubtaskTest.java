import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SubtaskTest {
    // проверьте, что наследники класса Task равны друг другу, если равен их id;
    @Test
    void subtasksWithSameIdShouldBeEqual() {
        Subtask subtask1 = new Subtask(1, "Task 1", TaskStatus.NEW, "Description 1", 2);
        Subtask subtask2 = new Subtask(1, "Task 2", TaskStatus.NEW, "Description 2", 2);

        assertEquals(subtask1, subtask2, "Задачи с одинаковым id должны быть равны.");
    }
}