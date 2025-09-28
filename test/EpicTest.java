import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class EpicTest {
   // проверьте, что наследники класса Task равны друг другу, если равен их id;
   @Test
   void epicsWithSameIdShouldBeEqual() {
       Epic epic1 = new Epic(1, TaskType.EPIC,"Task 1", "Description 1");
       Epic epic2 = new Epic(1, TaskType.EPIC,"Task 2", "Description 2");

       assertEquals(epic1, epic2, "Задачи с одинаковым id должны быть равны.");
   }
}