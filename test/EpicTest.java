import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class EpicTest {
    private static Epic epic1;
    private static Epic epic2;

    @BeforeAll
    public static void createEpic() {
        epic1 = new Epic("test1", "testDisc1", 1);
        epic2 = new Epic("test2", "testDisc2", 1);
    }

    @Test
    public void equalsById() {
        assertEquals(epic1, epic2);
        epic2.setId(2);
        assertNotEquals(epic1, epic2);
    }

    @Test
    public void unableToAddEpicToEpic() {
        TaskManager taskManager = Managers.getDefault();
        taskManager.createEpic(epic1);
    }

}