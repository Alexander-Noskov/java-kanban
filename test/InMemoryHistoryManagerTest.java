import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.LinkedList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryHistoryManagerTest {
    HistoryManager historyManager;

    @BeforeEach
    void setUp() {
        historyManager = Managers.getDefaultHistory();
    }

    @Test
    void addAndGetHistory() {
        List<Task> testList = new LinkedList<>();
        Task task = new Task("task", "1");
        testList.add(task);
        historyManager.add(task);
        assertEquals(testList, historyManager.getHistory());
        Epic epic = new Epic("epic", "2");
        testList.add(epic);
        historyManager.add(epic);
        assertEquals(testList, historyManager.getHistory());
        Subtask subtask = new Subtask("subtask", "3", 1);
        testList.add(subtask);
        historyManager.add(subtask);
        assertEquals(testList,historyManager.getHistory());
        historyManager.add(task);
        historyManager.add(task);
        historyManager.add(task);
        historyManager.add(task);
        historyManager.add(task);
        historyManager.add(task);
        assertEquals(9, historyManager.getHistory().size());
        historyManager.add(task);
        historyManager.add(task);
        assertEquals(10,historyManager.getHistory().size());
    }
}