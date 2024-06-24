package kanban.manager;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import kanban.adapter.LocalDateTimeAdapter;

import java.time.LocalDateTime;

public class Manager {
    private static TaskManager taskManager;
    private static HistoryManager historyManager;

    public static TaskManager getDefault() {
        if (taskManager == null) {
            taskManager = new InMemoryTaskManager();
        }
        return taskManager;
    }

    public static HistoryManager getDefaultHistory() {
        if (historyManager == null) {
            historyManager = new InMemoryHistoryManager();
        }
        return historyManager;
    }
    public static Gson getGson() {
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter());
        return gsonBuilder.create();
    }
}
