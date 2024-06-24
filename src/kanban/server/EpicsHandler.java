package kanban.server;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import kanban.manager.Manager;
import kanban.manager.TaskManager;
import kanban.task.Epic;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.regex.Pattern;

public class EpicsHandler extends BaseHttpHandler {
    private final TaskManager manager;
    private final Gson gson;

    public EpicsHandler(TaskManager manager) {
        this.manager = manager;
        gson = Manager.getGson();
    }

    @Override
    protected void get(HttpExchange httpExchange) throws IOException {
        String path = httpExchange.getRequestURI().getPath();
        String response;

        if (Pattern.matches("^/epics$", path)) {

            List<Epic> epics = manager.getEpics();
            response = gson.toJson(epics);
            sendText(httpExchange, response);

        } else if (Pattern.matches("^/epics/\\d+$", path)) {
            String pathId = path.replaceFirst("/epics/", "");
            int id = parsePathId(pathId);
            if (id > 0) {
                Epic epic = manager.getEpic(id);
                if (epic != null) {
                    response = gson.toJson(epic);
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
        if (Pattern.matches("^/epics$", path)) {

            String body = new String(httpExchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
            Epic epicRequest = gson.fromJson(body, Epic.class);
            int epicCreate = manager.createEpic(epicRequest);
            if (epicCreate != -1) {
                System.out.println("Эпик создан");
                httpExchange.sendResponseHeaders(201, 0);
                httpExchange.close();

            } else {
                sendHasInteractions(httpExchange);
            }
        } else if (Pattern.matches("^/epics/\\d+$", path)) {
            String pathId = path.replaceFirst("/epics/", "");
            int id = parsePathId(pathId);
            if (id > 0) {
                String body = new String(httpExchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
                Epic epicRequest = gson.fromJson(body, Epic.class);
                manager.updateEpic(epicRequest);
                System.out.println("Эпик обновлен");
                httpExchange.sendResponseHeaders(201, 0);
                httpExchange.close();

            } else {
                sendHasInteractions(httpExchange);
            }
        }
    }

    @Override
    protected void delete(HttpExchange httpExchange) throws IOException {
        String path = httpExchange.getRequestURI().getPath();

        if (Pattern.matches("^/epics$", path)) {

            manager.deleteAllEpic();
            System.out.println("Все эпики удалены");
            httpExchange.sendResponseHeaders(201, 0);
            httpExchange.close();

        } else if (Pattern.matches("^/epics/\\d+$", path)) { //Если есть id возвращаем задачу по id
            String pathId = path.replaceFirst("/epics/", "");// должен вернуть id
            int id = parsePathId(pathId);
            if (id > 0) {
                manager.removeEpic(id);
                System.out.println("Эпик удален");
                httpExchange.sendResponseHeaders(201, 0);
                httpExchange.close();

            } else {
                sendNotFound(httpExchange);
            }
        }
    }
}
