package kanban.manager;

import kanban.task.Task;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class InMemoryHistoryManager implements HistoryManager {
    private List<Task> historyList = new LinkedList<>();

    @Override
    public void add(Task task) {
        if (historyList.size() >= 10) {
            historyList.remove(0);
            historyList.add(task);
        } else {
            historyList.add(task);
        }
    }

    @Override
    public List<Task> getHistory() {
        return historyList;
    }

    @Override
    public void clearHistory(){
        historyList.clear();
    }
}
