package kanban.server;

import com.sun.net.httpserver.HttpServer;
import kanban.manager.Manager;
import kanban.manager.TaskManager;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.Scanner;

public class HttpTaskServer {
    private final int port = 8080;
    private final HttpServer taskServer;

    public HttpTaskServer(TaskManager taskManager) throws IOException {
        taskServer = HttpServer.create(new InetSocketAddress("localhost", port), 0);

        taskServer.createContext("/tasks", new TasksHandler(taskManager));
        taskServer.createContext("/epics", new EpicsHandler(taskManager));
        taskServer.createContext("/subtasks", new SubtaskHandler(taskManager));
        taskServer.createContext("/history", new TaskHistoryHandler(taskManager));
        taskServer.createContext("/prioritized", new PrioritizedTaskHandler(taskManager));
    }

    public void start() {
        System.out.println("Запуск сервера, порт " + port);
        taskServer.start();
    }

    public void stop() {
        System.out.println("Остановка сервера, порт " + port);
        taskServer.stop(0);
    }

    public static void main(String[] args) throws IOException {
        HttpTaskServer httpTaskServer = new HttpTaskServer(Manager.getDefault());
        Scanner scanner = new Scanner(System.in);
        while (true) {
            System.out.println("Запустить сервер - 1\n" +
                    "Остановка сервера - 2");
            String command = scanner.nextLine();
            switch (command) {
                case "1":
                    httpTaskServer.start();
                    break;
                case "2":
                    httpTaskServer.stop();
                    return;
                default:
                    System.out.println("Такой команды нет");
            }
        }
    }

}
