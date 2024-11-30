import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.nio.file.Files;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class FileBackedTaskManagerTest {

    static FileBackedTaskManager taskManager;
    static File tempFile;

    @BeforeAll
    static void setUp() {
        try {
            tempFile = File.createTempFile("tasks", ".csv");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        taskManager = new FileBackedTaskManager(tempFile);
    }

    @AfterAll
    static void tearDown() {
        tempFile.delete();
    }

    @Test
    void save() {
        String test1 = "id,type,name,status,description,startTime,endTime,duration,epic" + System.lineSeparator();
        Task testTask = new Task("name", "description", Status.NEW, 1);
        testTask.setStartTime(LocalDateTime.now());
        testTask.setDuration(55);
        Epic testEpic = new Epic("epic", "description", Status.NEW, 2);
        Subtask testSubtask = new Subtask("subtask", "description", Status.NEW, 3, 2);
        String test2 = test1 + testTask + System.lineSeparator();
        String test3 = test2 + testEpic + System.lineSeparator() + testSubtask + System.lineSeparator();
        try {
            taskManager.createTask(testTask);
            String textInFile2 = Files.readString(tempFile.toPath());
            assertEquals(test2, textInFile2);

            taskManager.createEpic(testEpic);
            taskManager.createSubtask(testSubtask);
            String textInFile3 = Files.readString(tempFile.toPath());
            assertEquals(test3, textInFile3);


            taskManager.deleteAllTasks();
            taskManager.deleteAllEpics();
            String textInFile4 = Files.readString(tempFile.toPath());
            assertEquals(test1, textInFile4);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void loadFromFile() {
        String sep = System.lineSeparator();
        StringBuilder resultString = new StringBuilder("id,type,name,status,description,startTime,endTime,duration,epic" + sep);
        Task testTask = new Task("name", "description", Status.NEW, 4);
        Epic testEpic = new Epic("epic", "description", Status.NEW, 5);
        Subtask testSubtask = new Subtask("subtask", "description", Status.NEW, 6, 5);
        resultString.append(testTask).append(sep).append(testEpic).append(sep).append(testSubtask).append(sep);
        try (Writer writer = new FileWriter(tempFile)) {
            writer.write(resultString.toString());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        FileBackedTaskManager taskManager = FileBackedTaskManager.loadFromFile(tempFile);
        try {
            String textInFile1 = Files.readString(tempFile.toPath());
            System.out.println(textInFile1);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        System.out.println(taskManager.getTasks());
        assertEquals(List.of(testTask), taskManager.getTasks());
        assertEquals(List.of(testSubtask), taskManager.getSubtasks());
        assertEquals(List.of(testEpic), taskManager.getEpics());
    }
}