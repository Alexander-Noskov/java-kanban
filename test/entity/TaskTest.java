package entity;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
class TaskTest {
    private static Task task1;
    private static Task task2;

    @BeforeAll
    public static void createTask() {
        task1 = new Task("test1", "testDisc1", Status.NEW, 1);
        task2 = new Task("test2", "testDisc2", Status.IN_PROGRESS, 1);
    }

    @Test
    public void equalsById() {
        assertEquals(task1, task2);
        task2.setId(2);
        assertNotEquals(task1, task2);
    }
  
}