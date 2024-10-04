import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SubtaskTest {
    private static Subtask subtask1;
    private static Subtask subtask2;

    @BeforeAll
    public static void createTask() {
        subtask1 = new Subtask("test1", "testDisc1", 1);
        subtask2 = new Subtask("test2", "testDisc2", 1);
    }

    @Test
    public void equalsById() {
        assertEquals(subtask1, subtask2);
        subtask2.setId(2);
        assertNotEquals(subtask1, subtask2);
    }

}