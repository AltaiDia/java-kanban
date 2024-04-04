package kanban.manager;

import kanban.task.Status;
import kanban.task.Task;

import java.util.List;

public interface HistoryManager {
    void add(Task task);

    List<Task> getHistory();

    void clearHistory();
}
