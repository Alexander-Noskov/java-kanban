package manager;

import entity.Task;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class InMemoryHistoryManagerTest {
    static HistoryManager historyManager;

    @BeforeAll
    static void setUp() {
        historyManager = Managers.getDefaultHistory();

    }

    @Test
    void add() {
        assertEquals(0, historyManager.getHistory().size());
        historyManager.add(new Task("task1", "description1"));
        assertEquals(1, historyManager.getHistory().size());
    }

    @Test
    void getHistory() {
        List<Task> testList = new ArrayList<>();
        testList.add(new Task("task1", "description1"));
        assertEquals(testList, historyManager.getHistory());
    }

    @Test
    void remove() {
        List<Task> testList = new ArrayList<>();
        historyManager.remove(-1);
        assertEquals(testList, historyManager.getHistory());
    }
}