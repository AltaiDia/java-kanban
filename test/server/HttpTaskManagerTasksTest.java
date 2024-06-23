package server;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import kanban.manager.InMemoryTaskManager;
import kanban.manager.Manager;
import kanban.manager.TaskManager;
import kanban.server.HttpTaskServer;
import kanban.task.Epic;
import kanban.task.Status;
import kanban.task.Subtask;
import kanban.task.Task;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.AfterEach;

import java.io.IOException;
import java.lang.reflect.Type;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class HttpTaskManagerTasksTest {


    TaskManager manager = new InMemoryTaskManager();

    HttpTaskServer taskServer = new HttpTaskServer(manager);
    Gson gson = Manager.getGson();

    public HttpTaskManagerTasksTest() throws IOException {
    }

    @BeforeEach
    public void setUp() {
        manager.clearAll();
        taskServer.start();
    }

    @AfterEach
    public void shutDown() {
        taskServer.stop();
    }

    @Test
    public void testAddTask() throws IOException, InterruptedException {

        Task task = new Task("Тестовая задача", "Описание тестовой задачи",
                Status.NEW, 5, LocalDateTime.now());

        String taskJson = gson.toJson(task);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .version(HttpClient.Version.HTTP_1_1)
                .header("Content-Type", "application/json;charset=utf-8")
                .POST(HttpRequest.BodyPublishers.ofString(taskJson))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(201, response.statusCode());

        List<Task> tasksFromManager = manager.getTasks();

        assertNotNull(tasksFromManager, "Задачи не возвращаются");
        assertEquals(1, tasksFromManager.size(), "Некорректное количество задач");
        assertEquals("Тестовая задача", tasksFromManager.get(0).getTitle(),
                "Некорректное имя задачи");
    }

    @Test
    public void testAddEpicAndSubtask() throws IOException, InterruptedException {

        Epic epic = new Epic("Эпик № 1", "Описание эпика № 1", Status.NEW);

        Subtask subtask1 = new Subtask("Подзадача № 1", "Описание Подзадачи № 1",
                Status.NEW, 65, LocalDateTime.now().plusHours(2), 1);

        Subtask subtask2 = new Subtask("Подзадача № 2", "Описание Подзадачи № 2",
                Status.NEW, 65, LocalDateTime.now().plusHours(4), 1);

        String epicJson = gson.toJson(epic);
        String subtaskJson1 = gson.toJson(subtask1);
        String subtaskJson2 = gson.toJson(subtask2);

        HttpClient client = HttpClient.newHttpClient();

        URI urlEpic = URI.create("http://localhost:8080/epics");
        URI urlSubtask = URI.create("http://localhost:8080/subtasks");

        HttpRequest request1 = HttpRequest.newBuilder()
                .uri(urlEpic)
                .version(HttpClient.Version.HTTP_1_1)
                .header("Content-Type", "application/json;charset=utf-8")
                .POST(HttpRequest.BodyPublishers.ofString(epicJson))
                .build();

        HttpRequest request2 = HttpRequest.newBuilder()
                .uri(urlSubtask)
                .version(HttpClient.Version.HTTP_1_1)
                .header("Content-Type", "application/json;charset=utf-8")
                .POST(HttpRequest.BodyPublishers.ofString(subtaskJson1))
                .build();

        HttpRequest request3 = HttpRequest.newBuilder()
                .uri(urlSubtask)
                .version(HttpClient.Version.HTTP_1_1)
                .header("Content-Type", "application/json;charset=utf-8")
                .POST(HttpRequest.BodyPublishers.ofString(subtaskJson2))
                .build();


        HttpResponse<String> response1 = client.send(request1, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, response1.statusCode());

        HttpResponse<String> response2 = client.send(request2, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, response2.statusCode());

        HttpResponse<String> response3 = client.send(request3, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, response3.statusCode());

        List<Epic> epicsFromManager = manager.getEpics();
        List<Subtask> subtasksFromManager = manager.getSubtasks();

        assertNotNull(epicsFromManager, "Эпики не возвращаются");
        assertNotNull(subtasksFromManager, "Подзадачи не возвращаются");

        assertEquals(1, epicsFromManager.size(), "Некорректное количество Эпиков");
        assertEquals("Эпик № 1", epicsFromManager.get(0).getTitle(),
                "Некорректное имя задачи");

        assertEquals(2, subtasksFromManager.size(), "Некорректное количество Подзадач");

        assertEquals("Подзадача № 1", subtasksFromManager.get(0).getTitle(),
                "Некорректное имя подзадачи № 1");
        assertEquals("Подзадача № 2", subtasksFromManager.get(1).getTitle(),
                "Некорректное имя подзадачи № 2");
    }

    @Test
    public void testGetTasks() {
        Task task = new Task("Задача № 1", "Описание задачи № 1", Status.NEW,
                65, LocalDateTime.now());
        manager.createTask(task);

        Epic epic = new Epic("Эпик № 1", "Описание эпика № 1", Status.NEW);
        manager.createEpic(epic);

        Subtask subtask = new Subtask("Подзадача № 1", "Описание Подзадачи № 1",
                Status.NEW, 65, LocalDateTime.now().plusHours(2), epic.getId());
        manager.createSubtask(subtask);


        HttpClient client = HttpClient.newHttpClient();
        URI urlTask = URI.create("http://localhost:8080/tasks/1");
        URI urlEpic = URI.create("http://localhost:8080/epics/2");
        URI urlSubtask = URI.create("http://localhost:8080/subtasks/3");

        HttpRequest request1 = HttpRequest.newBuilder()
                .uri(urlTask)
                .version(HttpClient.Version.HTTP_1_1)
                .header("Accept", "application/json;charset=utf-8")
                .GET()
                .build();

        HttpRequest request2 = HttpRequest.newBuilder()
                .uri(urlEpic)
                .version(HttpClient.Version.HTTP_1_1)
                .header("Accept", "application/json;charset=utf-8")
                .GET()
                .build();

        HttpRequest request3 = HttpRequest.newBuilder()
                .uri(urlSubtask)
                .version(HttpClient.Version.HTTP_1_1)
                .header("Accept", "application/json;charset=utf-8")
                .GET()
                .build();

        try {
            HttpResponse<String> response1 = client.send(request1, HttpResponse.BodyHandlers.ofString());
            HttpResponse<String> response2 = client.send(request2, HttpResponse.BodyHandlers.ofString());
            HttpResponse<String> response3 = client.send(request3, HttpResponse.BodyHandlers.ofString());

            assertEquals(200, response1.statusCode());
            assertEquals(200, response2.statusCode());
            assertEquals(200, response3.statusCode());

            Task actualTask = gson.fromJson(response1.body(), Task.class);

            assertNotNull(actualTask, "Задача не вернулась");
            assertEquals(task, actualTask, "Запрошенная задача не соответствует вернувшейся");

            Task actualEpic = gson.fromJson(response2.body(), Epic.class);

            assertNotNull(actualEpic, "Эпик не вернулся");
            assertEquals(epic, actualEpic, "Запрошенный эпик не соответствует вернувшемуся");

            Task actualSubtask = gson.fromJson(response3.body(), Subtask.class);

            assertNotNull(actualSubtask, "Подзадача не вернулась");
            assertEquals(subtask, actualSubtask, "Запрошенная подзадача не соответствует вернувшейся");

        } catch (IOException | InterruptedException e) {
            System.out.println(e.getStackTrace());
        }
    }

    @Test
    public void testDeleteTasks() {
        Task task = new Task("Задача № 1", "Описание задачи № 1", Status.NEW,
                65, LocalDateTime.now());
        manager.createTask(task);

        Epic epic = new Epic("Эпик № 1", "Описание эпика № 1", Status.NEW);
        manager.createEpic(epic);

        Subtask subtask = new Subtask("Подзадача № 1", "Описание Подзадачи № 1",
                Status.NEW, 65, LocalDateTime.now().plusHours(2), epic.getId());
        manager.createSubtask(subtask);


        HttpClient client = HttpClient.newHttpClient();
        URI urlTask = URI.create("http://localhost:8080/tasks/1");
        URI urlEpic = URI.create("http://localhost:8080/epics/2");
        URI urlSubtask = URI.create("http://localhost:8080/subtasks/3");

        HttpRequest request1 = HttpRequest.newBuilder()
                .uri(urlTask)
                .DELETE()
                .build();
        HttpRequest request2 = HttpRequest.newBuilder()
                .uri(urlEpic)
                .DELETE()
                .build();
        HttpRequest request3 = HttpRequest.newBuilder()
                .uri(urlSubtask)
                .DELETE()
                .build();
        try {
            HttpResponse<String> response1 = client.send(request1, HttpResponse.BodyHandlers.ofString());
            HttpResponse<String> response2 = client.send(request2, HttpResponse.BodyHandlers.ofString());
            HttpResponse<String> response3 = client.send(request3, HttpResponse.BodyHandlers.ofString());

            assertEquals(201, response1.statusCode());
            assertEquals(201, response2.statusCode());
            assertEquals(201, response3.statusCode());
        } catch (IOException | InterruptedException e) {
            System.out.println(e.getStackTrace());
        }
        assertNull(manager.getTask(1), "Задача не удалилась");
        assertNull(manager.getTask(2), "Эпик не удалился");
        assertNull(manager.getTask(3), "Подзадача не удалилась");
    }

    @Test
    public void testGetHistory() {
        Task task = new Task("Задача № 1", "Описание задачи № 1", Status.NEW,
                65, LocalDateTime.now());
        manager.createTask(task);
        manager.getTask(task.getId());

        Epic epic = new Epic("Эпик № 1", "Описание эпика № 1", Status.NEW);
        manager.createEpic(epic);
        manager.getEpic(epic.getId());

        Subtask subtask = new Subtask("Подзадача № 1", "Описание Подзадачи № 1",
                Status.NEW, 65, LocalDateTime.now().plusHours(2), epic.getId());
        manager.createSubtask(subtask);
        manager.getSubtask(subtask.getId());

        HttpClient client = HttpClient.newHttpClient();

        URI url = URI.create("http://localhost:8080/history");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .GET()
                .build();

        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            assertEquals(200, response.statusCode());
            Type taskType = new TypeToken<ArrayList<Task>>() {
            }.getType();
            List<Task> actual = gson.fromJson(response.body(), taskType);

            Assertions.assertNotNull(actual, "Список пуст");
            Assertions.assertEquals(3, actual.size(), "Неверное количество");
        } catch (IOException | InterruptedException e) {
            System.out.println(e.getStackTrace());
        }
    }

    @Test
    public void testGetPrioritized() {
        Task task = new Task("Задача № 1", "Описание задачи № 1", Status.NEW,
                65, LocalDateTime.now());
        manager.createTask(task);

        Epic epic = new Epic("Эпик № 1", "Описание эпика № 1", Status.NEW);
        manager.createEpic(epic);

        Subtask subtask = new Subtask("Подзадача № 1", "Описание Подзадачи № 1",
                Status.NEW, 65, LocalDateTime.now().plusHours(2), epic.getId());
        manager.createSubtask(subtask);

        HttpClient client = HttpClient.newHttpClient();

        URI url = URI.create("http://localhost:8080/prioritized");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .GET()
                .build();
        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            Assertions.assertEquals(200, response.statusCode());

            Type taskType = new TypeToken<ArrayList<Task>>() {
            }.getType();

            List<Task> actual = gson.fromJson(response.body(), taskType);

            Assertions.assertNotNull(actual, "Список пуст");
            Assertions.assertEquals(2, actual.size(), "Неверное количество");

        } catch (IOException | InterruptedException e) {
            System.out.println(e.getStackTrace());
        }
    }

}