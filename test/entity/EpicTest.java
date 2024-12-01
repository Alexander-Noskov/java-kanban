package entity;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.Set;

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
    void addSubtaskId() {
        epic1.addSubtaskId(2);
        epic1.addSubtaskId(3);
        assertEquals(2, epic1.getSubtaskIds().size());
    }

    @Test
    void getSubtaskIds() {
        Set<Integer> subtaskIds = new HashSet<>();
        subtaskIds.add(2);
        subtaskIds.add(3);
        epic1.addSubtaskId(2);
        epic1.addSubtaskId(3);
        assertEquals(subtaskIds, epic1.getSubtaskIds());
    }

    @Test
    void equalsById() {
        assertEquals(epic1, epic2);
        epic2.setId(2);
        assertNotEquals(epic1, epic2);
    }
}