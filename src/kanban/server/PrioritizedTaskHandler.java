package kanban.server;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import kanban.manager.Manager;
import kanban.manager.TaskManager;
import kanban.task.Task;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.regex.Pattern;

public class PrioritizedTaskHandler extends BaseHttpHandler implements HttpHandler {
    private final TaskManager manager;
    private final Gson gson;

    public PrioritizedTaskHandler(TaskManager manager) {
        this.manager = manager;
        gson = Manager.getGson();
    }

    @Override
    public void handle(HttpExchange httpExchange) throws IOException {
        String method = httpExchange.getRequestMethod();
        if (method.equals("GET")) {
            String path = httpExchange.getRequestURI().getPath();
            if (Pattern.matches("^/prioritized$", path)) {
                List<Task> prioritized = manager.getPrioritizedTasks();
                String responseJson = gson.toJson(prioritized);
                byte[] responseByte = responseJson.getBytes(StandardCharsets.UTF_8);
                httpExchange.getResponseHeaders().add("Content-Type", "application/json;charset=utf-8");
                httpExchange.sendResponseHeaders(200, responseByte.length);
                httpExchange.getResponseBody().write(responseByte);
            } else {
                sendNotFound(httpExchange);
            }

        } else {
            sendBadRequest(httpExchange);
        }

    }

    @Override
    protected void sendNotFound(HttpExchange httpExchange) throws IOException {
        httpExchange.sendResponseHeaders(404, 0);
        String response = "По этому пути нет запрашиваемого списка.";
        try (OutputStream os = httpExchange.getResponseBody()) {
            os.write(response.getBytes(StandardCharsets.UTF_8));
        }
        httpExchange.close();
    }
}
