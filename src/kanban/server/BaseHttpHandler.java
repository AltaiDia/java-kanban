package kanban.server;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

public class BaseHttpHandler implements HttpHandler {
    @Override
    public void handle(HttpExchange httpExchange) throws IOException {
        String method = httpExchange.getRequestMethod();

        switch (method) {
            case "GET":
                get(httpExchange);
                break;
            case "POST":
                post(httpExchange);
                break;
            case "DELETE":
                delete(httpExchange);
                break;
            default:
                sendBadRequest(httpExchange);
        }
    }

    protected void get(HttpExchange httpExchange) throws IOException {

    }

    protected void post(HttpExchange httpExchange) throws IOException {

    }

    protected void delete(HttpExchange httpExchange) throws IOException {

    }

    protected void sendText(HttpExchange httpExchange, String text) throws IOException {
        byte[] resp = text.getBytes(StandardCharsets.UTF_8);
        httpExchange.getResponseHeaders().add("Content-Type", "application/json;charset=utf-8");
        httpExchange.sendResponseHeaders(200, resp.length);
        httpExchange.getResponseBody().write(resp);
        httpExchange.close();
    }

    protected void sendNotFound(HttpExchange httpExchange) throws IOException {
        httpExchange.sendResponseHeaders(404, 0);
        String response = "Задача не найдена.";
        try (OutputStream os = httpExchange.getResponseBody()) {
            os.write(response.getBytes(StandardCharsets.UTF_8));
        }
        httpExchange.close();
    }

    protected void sendHasInteractions(HttpExchange httpExchange) throws IOException {
        httpExchange.sendResponseHeaders(406, 0);
        String response = "Задача пересекается с существующими.";
        try (OutputStream os = httpExchange.getResponseBody()) {
            os.write(response.getBytes(StandardCharsets.UTF_8));
        }
        httpExchange.close();
    }

    protected void sendBadRequest(HttpExchange httpExchange) throws IOException {
        httpExchange.sendResponseHeaders(400, 0);
        String response = "Я не понимаю ваш запрос";
        try (OutputStream os = httpExchange.getResponseBody()) {
            os.write(response.getBytes(StandardCharsets.UTF_8));
        }
        httpExchange.close();
    }

    protected int parsePathId(String pathId) {
        try {
            return Integer.parseInt(pathId);
        } catch (NumberFormatException exception) {
            return -1;
        }
    }
}
