package kanban.server;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import kanban.manager.Manager;
import kanban.manager.TaskManager;
import kanban.task.Subtask;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.regex.Pattern;

public class SubtaskHandler extends BaseHttpHandler implements HttpHandler {
    private final TaskManager manager;
    private final Gson gson;

    public SubtaskHandler(TaskManager manager) {
        this.manager = manager;
        gson = Manager.getGson();
    }

    @Override
    public void handle(HttpExchange httpExchange) throws IOException {
        String method = httpExchange.getRequestMethod();

        switch (method) {
            case "GET":
                subtasksGet(httpExchange);
                break;
            case "POST":
                subtasksPost(httpExchange);
                break;
            case "DELETE":
                subtasksDelete(httpExchange);
                break;
            default:
                sendBadRequest(httpExchange);
        }
    }

    private void subtasksGet(HttpExchange httpExchange) throws IOException {
        String path = httpExchange.getRequestURI().getPath();
        String response;

        if (Pattern.matches("^/subtasks$", path)) {
            List<Subtask> subtasks = manager.getSubtasks();
            response = gson.toJson(subtasks);
            sendText(httpExchange, response);
        } else if (Pattern.matches("^/subtasks/\\d+$", path)) {
            String pathId = path.replaceFirst("/subtasks/", "");
            int id = parsePathId(pathId);
            if (id > 0) {
                Subtask subtask = manager.getSubtask(id);
                if (subtask != null) {
                    response = gson.toJson(subtask);
                    sendText(httpExchange, response);
                }
                sendNotFound(httpExchange);
            } else {
                sendNotFound(httpExchange);
            }
        }
    }

    private void subtasksPost(HttpExchange httpExchange) throws IOException {
        String path = httpExchange.getRequestURI().getPath();
        if (Pattern.matches("^/subtasks$", path)) {

            String body = new String(httpExchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
            Subtask subTaskRequest = gson.fromJson(body, Subtask.class);
            int subTaskCreate = manager.createSubtask(subTaskRequest);
            if (subTaskCreate != -1) {
                System.out.println("Подзадача создана");
                httpExchange.sendResponseHeaders(201, 0);
                httpExchange.close();

            } else {
                sendHasInteractions(httpExchange);
            }

        } else if (Pattern.matches("^/subtasks/\\d+$", path)) {
            String pathId = path.replaceFirst("/subtasks/", "");
            int id = parsePathId(pathId);
            if (id > 0) {
                String body = new String(httpExchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
                Subtask subtaskRequest = gson.fromJson(body, Subtask.class);
                int subtaskCreate = manager.updateSubtask(subtaskRequest);
                if (subtaskCreate != -1) {
                    System.out.println("Подзадача обновлена");
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

    private void subtasksDelete(HttpExchange httpExchange) throws IOException {
        String path = httpExchange.getRequestURI().getPath();

        if (Pattern.matches("^/subtasks$", path)) {
            manager.deleteAllSubtask();
            System.out.println("Все подзадачи удалены");
            httpExchange.sendResponseHeaders(201, 0);
            httpExchange.close();
        } else if (Pattern.matches("^/subtasks/\\d+$", path)) {
            String pathId = path.replaceFirst("/subtasks/", "");
            int id = parsePathId(pathId);
            if (id > 0) {
                manager.removeSubtask(id);
                System.out.println("Подзадача удалена");
                httpExchange.sendResponseHeaders(201, 0);
                httpExchange.close();

            } else {
                sendNotFound(httpExchange);
            }
        }
    }
}
