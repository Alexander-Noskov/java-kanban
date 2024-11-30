import com.google.gson.Gson;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class HistoryHandlerTest {
    TaskManager manager = new InMemoryTaskManager();
    HttpTaskServer taskServer = new HttpTaskServer(manager);
    Gson gson = HttpTaskServer.getGson();


    @BeforeEach
    public void setUp() throws IOException {
        manager.deleteAllTasks();
        manager.deleteAllSubtasks();
        manager.deleteAllEpics();
        taskServer.start();
    }

    @AfterEach
    public void shutDown() {
        taskServer.stop();
    }

    @Test
    public void testGetHistory() throws IOException, InterruptedException {
        // создаём задачу
        Task task1 = new Task("Test 1", "Testing task 1");
        task1.setStatus(Status.NEW);
        task1.setDuration(5);
        task1.setStartTime(LocalDateTime.now());

        Task task2 = new Task("Test 2", "Testing task 2");
        task2.setStatus(Status.NEW);
        task2.setDuration(5);
        task2.setStartTime(LocalDateTime.now().plusMinutes(10));

        manager.createTask(task1);
        manager.createTask(task2);

        manager.getTask(1);
        manager.getTask(2);

        // конвертируем её в JSON
        List<Task> tasksFromManager = manager.getHistory();
        String taskJson = gson.toJson(tasksFromManager);

        // создаём HTTP-клиент и запрос
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/history");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();

        // вызываем рест
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        // проверяем код ответа
        assertEquals(200, response.statusCode());
        assertEquals(taskJson, response.body());
    }
}