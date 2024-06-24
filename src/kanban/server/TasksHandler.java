package kanban.server;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import kanban.manager.Manager;
import kanban.manager.TaskManager;
import kanban.task.Task;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.regex.Pattern;

public class TasksHandler extends BaseHttpHandler {
    private final TaskManager manager;
    private final Gson gson;

    public TasksHandler(TaskManager manager) {
        this.manager = manager;
        gson = Manager.getGson();
    }

    @Override
    protected void get(HttpExchange httpExchange) throws IOException {
        String path = httpExchange.getRequestURI().getPath();
        String response;

        if (Pattern.matches("^/tasks$", path)) {

            List<Task> tasks = manager.getTasks();
            response = gson.toJson(tasks);
            sendText(httpExchange, response);

        } else if (Pattern.matches("^/tasks/\\d+$", path)) {
            String pathId = path.replaceFirst("/tasks/", "");
            int id = parsePathId(pathId);
            if (id > 0) {
                Task task = manager.getTask(id);
                if (task != null) {
                    response = gson.toJson(task);
                    sendText(httpExchange, response);
                }
                sendNotFound(httpExchange);
            } else {
                sendNotFound(httpExchange);
            }
        }
    }

    @Override
    protected void post(HttpExchange httpExchange) throws IOException {
        String path = httpExchange.getRequestURI().getPath();

        if (Pattern.matches("^/tasks$", path)) {
            try {
                String body = new String(httpExchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
                Task taskRequest = gson.fromJson(body, Task.class);
                int taskCreate = manager.createTask(taskRequest);
                if (taskCreate != -1) {
                    System.out.println("Задача создана");
                    httpExchange.sendResponseHeaders(201, 0);
                    httpExchange.close();

                } else {
                    sendHasInteractions(httpExchange);
                }
            } catch (Exception e) {
                System.out.println(e);
            }
        } else if (Pattern.matches("^/tasks/\\d+$", path)) {

            String pathId = path.replaceFirst("/tasks/", "");
            int id = parsePathId(pathId);
            if (id > 0) {

                String body = new String(httpExchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
                Task taskRequest = gson.fromJson(body, Task.class);
                int taskUpdate = manager.updateTask(taskRequest);
                if (taskUpdate != -1) {

                    System.out.println("Задача обновлена");
                    httpExchange.sendResponseHeaders(201, 0);
                    httpExchange.close();

                } else {
                    sendHasInteractions(httpExchange);
                }

            } else {
                sendNotFound(httpExchange);
            }
        }
    }

    @Override
    protected void delete(HttpExchange httpExchange) throws IOException {
        String path = httpExchange.getRequestURI().getPath();

        if (Pattern.matches("^/tasks$", path)) {

            manager.deleteAllTask();
            System.out.println("Задачи удалены");
            httpExchange.sendResponseHeaders(201, 0);
            httpExchange.close();

        } else if (Pattern.matches("^/tasks/\\d+$", path)) {

            String pathId = path.replaceFirst("/tasks/", "");
            int id = parsePathId(pathId);
            if (id > 0) {
                manager.removeTask(id);
                System.out.println("Задача удалена");
                httpExchange.sendResponseHeaders(201, 0);
                httpExchange.close();

            } else {
                sendNotFound(httpExchange);
            }
        }
    }
}
