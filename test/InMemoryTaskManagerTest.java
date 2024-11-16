import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryTaskManagerTest {

    private InMemoryTaskManager taskManager;
    private Task task1;
    private Task task2;
    private Subtask subtask1;
    private Subtask subtask2;
    private Epic epic1;
    private Epic epic2;
    private List<Task> testList;

    @BeforeEach
    void setUp() {
        taskManager = new InMemoryTaskManager();
        testList = new ArrayList<>();
        task1 = new Task("task1", "task1Disc", Status.IN_PROGRESS, 0);
        task2 = new Task("task2", "task2Disc", Status.IN_PROGRESS, 0);
        taskManager.createTask(task1);
        epic1 = new Epic("epic1", "epic1Disc", 1);
        epic2 = new Epic("epic2", "epic2Disc", 1);
        taskManager.createEpic(epic1);
        subtask1 = new Subtask("subtask1", "subtask1Disc", Status.IN_PROGRESS, 2, epic1);
        subtask2 = new Subtask("subtask2", "subtask2Disc", Status.IN_PROGRESS, 2, epic1);
        taskManager.createSubtask(subtask1);

    }

    @Test
    void isValidTime() {
        assertFalse(taskManager.isValidTime(task1, task2));
        task1.setStartTime(LocalDateTime.now());
        task2.setStartTime(LocalDateTime.now().plusMinutes(10));
        task1.setDuration(5);
        task2.setDuration(20);
        assertTrue(taskManager.isValidTime(task1, task2));
        task1.setDuration(15);
        assertFalse(taskManager.isValidTime(task1, task2));
    }

    @Test
    void addTaskToPrioritizedTask() {
        task1.setStartTime(LocalDateTime.now());
        task2.setStartTime(LocalDateTime.now());
        task1.setDuration(10);
        task2.setDuration(10);

        taskManager.createTask(task1);

        assertThrows(IllegalArgumentException.class, () -> taskManager.createTask(task2));
        task2.setStartTime(LocalDateTime.now().plusMinutes(9));
        assertThrows(IllegalArgumentException.class, () -> taskManager.createTask(task2));
        task2.setStartTime(LocalDateTime.now().plusMinutes(10));
        assertDoesNotThrow(() -> taskManager.createTask(task2));
    }

    @Test
    void getTasks() {
        testList.add(task1);
        assertEquals(testList, taskManager.getTasks());
    }

    @Test
    void getSubtasks() {
        testList.add(subtask1);
        assertEquals(testList, taskManager.getSubtasks());
    }

    @Test
    void getEpics() {
        testList.add(epic1);
        assertEquals(testList, taskManager.getEpics());
    }

    @Test
    void deleteAllTasks() {
        taskManager.deleteAllTasks();
        assertEquals(testList, taskManager.getTasks());
    }

    @Test
    void deleteAllSubtasks() {
        taskManager.deleteAllSubtasks();
        assertEquals(testList, taskManager.getSubtasks());
    }

    @Test
    void deleteAllEpics() {
        taskManager.deleteAllEpics();
        assertEquals(testList, taskManager.getEpics());
        assertEquals(testList, taskManager.getSubtasks());
    }

    @Test
    void getTask() {
        assertEquals(task1, taskManager.getTask(0));
        assertTrue(taskManager.getHistory().contains(task1));
    }

    @Test
    void getSubtask() {
        assertEquals(subtask1, taskManager.getSubtask(2));
        assertTrue(taskManager.getHistory().contains(subtask2));
    }

    @Test
    void getEpic() {
        assertEquals(epic1, taskManager.getEpic(1));
        assertTrue(taskManager.getHistory().contains(epic1));
    }

    @Test
    void createTask() {
        assertEquals(Status.NEW, taskManager.getTask(0).getStatus());
    }

    @Test
    void createSubtask() {
        assertEquals(Status.NEW, taskManager.getSubtask(2).getStatus());
    }

    @Test
    void createEpic() {
        assertEquals(Status.NEW, taskManager.getEpic(1).getStatus());
    }

    @Test
    void updateTask() {
        taskManager.updateTask(task2);
        assertEquals(Status.IN_PROGRESS, taskManager.getTask(0).getStatus());
    }

    @Test
    void updateSubtask() {
        taskManager.updateSubtask(subtask2);
        assertEquals(Status.IN_PROGRESS, taskManager.getSubtask(2).getStatus());
        assertEquals(Status.IN_PROGRESS, taskManager.getEpic(1).getStatus());
    }

    @Test
    void updateEpic() {
        subtask2.setStatus(Status.DONE);
        taskManager.updateSubtask(subtask2);
        assertEquals(Status.DONE, taskManager.getSubtask(2).getStatus());
        assertEquals(Status.DONE, taskManager.getEpic(1).getStatus());
    }

    @Test
    void deleteTask() {
        taskManager.deleteTask(0);
        assertEquals(testList, taskManager.getTasks());
        assertFalse(taskManager.getHistory().contains(task1));
    }

    @Test
    void deleteSubtask() {
        taskManager.deleteSubtask(2);
        assertEquals(testList, taskManager.getSubtasks());
        assertEquals(Status.NEW, taskManager.getEpic(1).getStatus());
        assertFalse(taskManager.getHistory().contains(subtask1));
    }

    @Test
    void deleteEpic() {
        taskManager.deleteEpic(1);
        assertEquals(testList, taskManager.getSubtasks());
        assertEquals(testList, taskManager.getEpics());
        assertFalse(taskManager.getHistory().contains(epic1));
        assertFalse(taskManager.getHistory().contains(subtask1));
        assertFalse(taskManager.getHistory().contains(subtask2));
    }
}