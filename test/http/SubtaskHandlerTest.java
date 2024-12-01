package http;

import com.google.gson.Gson;
import entity.Epic;
import entity.Status;
import entity.Subtask;
import exception.NotFoundException;
import manager.InMemoryTaskManager;
import manager.TaskManager;
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

class SubtaskHandlerTest {
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
    public void testCreateSubtask() throws IOException, InterruptedException {
        // создаём задачу
        Epic epic = new Epic("entity.Epic 1", "Description 1");
        manager.createEpic(epic);
        Subtask task = new Subtask("Test 1", "Testing task 1",1);
        task.setStatus(Status.NEW);
        task.setDuration(5);
        task.setStartTime(LocalDateTime.now());
        // конвертируем её в JSON
        String taskJson = gson.toJson(task);

        // создаём HTTP-клиент и запрос
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/subtasks");
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(taskJson)).build();

        // вызываем рест, отвечающий за создание задач
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        // проверяем код ответа
        assertEquals(201, response.statusCode());

        // проверяем, что создалась одна задача с корректным именем
        List<Subtask> tasksFromManager = manager.getSubtasks();

        assertNotNull(tasksFromManager, "Задачи не возвращаются");
        assertEquals(1, tasksFromManager.size(), "Некорректное количество задач");
        assertEquals("Test 1", tasksFromManager.getFirst().getName(), "Некорректное имя задачи");
    }

    @Test
    public void testUpdateSubtask() throws IOException, InterruptedException {
        // создаём задачу
        Epic epic = new Epic("entity.Epic 1", "Description 1");
        manager.createEpic(epic);
        Subtask task1 = new Subtask("Test 1", "Testing task 1", 1);
        task1.setStatus(Status.NEW);
        task1.setDuration(5);
        task1.setStartTime(LocalDateTime.now());
        manager.createSubtask(task1);

        Subtask task2 = new Subtask("Test 2", "Testing task 2", 1);
        task2.setId(2);
        task2.setStatus(Status.NEW);
        task2.setDuration(5);
        task2.setStartTime(LocalDateTime.now());
        // конвертируем её в JSON
        String taskJson2 = gson.toJson(task2);

        // создаём HTTP-клиент и запрос
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/subtasks");
        HttpRequest request2 = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(taskJson2)).build();

        // вызываем рест, отвечающий за создание задач
        HttpResponse<String> response2 = client.send(request2, HttpResponse.BodyHandlers.ofString());
        // проверяем код ответа
        assertEquals(201, response2.statusCode());

        // проверяем, что создалась одна задача с корректным именем
        List<Subtask> tasksFromManager = manager.getSubtasks();

        assertNotNull(tasksFromManager, "Задачи не возвращаются");
        assertEquals(1, tasksFromManager.size(), "Некорректное количество задач");
        assertEquals("Test 2", tasksFromManager.getFirst().getName(), "Некорректное имя задачи");
    }

    @Test
    public void testInteractions() throws IOException, InterruptedException {
        // создаём задачу
        Epic epic = new Epic("entity.Epic 1", "Description 1");
        manager.createEpic(epic);
        Subtask task1 = new Subtask("Test 1", "Testing task 1", 1);
        task1.setStatus(Status.NEW);
        task1.setDuration(5);
        task1.setStartTime(LocalDateTime.now());
        manager.createSubtask(task1);

        Subtask task2 = new Subtask("Test 2", "Testing task 2", 1);
        task2.setStatus(Status.NEW);
        task2.setDuration(5);
        task2.setStartTime(LocalDateTime.now());
        // конвертируем её в JSON
        String taskJson2 = gson.toJson(task2);

        // создаём HTTP-клиент и запрос
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/subtasks");
        HttpRequest request2 = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(taskJson2)).build();

        // вызываем рест, отвечающий за создание задач
        HttpResponse<String> response2 = client.send(request2, HttpResponse.BodyHandlers.ofString());
        // проверяем код ответа
        assertEquals(406, response2.statusCode());
    }

    @Test
    public void testGetSubtasks()  throws IOException, InterruptedException {
        // создаём задачу
        Epic epic = new Epic("entity.Epic 1", "Description 1");
        manager.createEpic(epic);
        Subtask task = new Subtask("Test 1", "Testing task 1", 1);
        task.setStatus(Status.NEW);
        task.setDuration(5);
        task.setStartTime(LocalDateTime.now());
        manager.createSubtask(task);
        Subtask task2 = new Subtask("Test 2", "Testing task 2", 1);
        task2.setStatus(Status.NEW);
        task2.setDuration(5);
        task2.setStartTime(LocalDateTime.now().plusMinutes(10));
        manager.createSubtask(task2);

        // конвертируем её в JSON
        String taskJson = gson.toJson(manager.getSubtasks());

        // создаём HTTP-клиент и запрос
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/subtasks");
        HttpRequest request2 = HttpRequest.newBuilder().uri(url).GET().build();

        // вызываем рест, отвечающий за создание задач
        HttpResponse<String> response = client.send(request2, HttpResponse.BodyHandlers.ofString());

        // проверяем код ответа
        assertEquals(200, response.statusCode());
        assertEquals(taskJson, response.body());
    }

    @Test
    public void testGetSubtaskById() throws IOException, InterruptedException {
        // создаём задачу
        Epic epic = new Epic("entity.Epic 1", "Description 1");
        manager.createEpic(epic);
        Subtask task = new Subtask("Test 1", "Testing task 1", 1);
        task.setId(2);
        task.setStatus(Status.NEW);
        task.setDuration(5);
        task.setStartTime(LocalDateTime.now());
        manager.createSubtask(task);

        // конвертируем её в JSON
        String taskJson = gson.toJson(task);

        // создаём HTTP-клиент и запрос
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/subtasks/2");
        HttpRequest request2 = HttpRequest.newBuilder().uri(url).GET().build();

        // вызываем рест, отвечающий за создание задач
        HttpResponse<String> response = client.send(request2, HttpResponse.BodyHandlers.ofString());

        // проверяем код ответа
        assertEquals(200, response.statusCode());
        assertEquals(taskJson, response.body());
    }

    @Test
    public void testNotFound() throws IOException, InterruptedException {
        // создаём HTTP-клиент и запрос
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/subtasks/1");
        HttpRequest request2 = HttpRequest.newBuilder().uri(url).GET().build();

        // вызываем рест, отвечающий за создание задач
        HttpResponse<String> response = client.send(request2, HttpResponse.BodyHandlers.ofString());

        // проверяем код ответа
        assertEquals(404, response.statusCode());
        assertThrows(NotFoundException.class, () -> manager.getSubtask(1));
    }

    @Test
    public void testDeleteSubtask() throws IOException, InterruptedException {
        // создаём задачу
        Epic epic = new Epic("entity.Epic 1", "Description 1");
        manager.createEpic(epic);
        Subtask task = new Subtask("Test 1", "Testing task 1", 1);
        task.setId(2);
        task.setStatus(Status.NEW);
        task.setDuration(5);
        task.setStartTime(LocalDateTime.now());
        manager.createSubtask(task);

        // создаём HTTP-клиент и запрос
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/subtasks/2");
        HttpRequest request2 = HttpRequest.newBuilder().uri(url).DELETE().build();

        // вызываем рест, отвечающий за создание задач
        HttpResponse<String> response = client.send(request2, HttpResponse.BodyHandlers.ofString());

        // проверяем код ответа
        assertEquals(200, response.statusCode());
        assertThrows(NotFoundException.class, () -> manager.getSubtask(2));
    }
}