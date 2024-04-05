package kanban.manager;

import kanban.task.Status;
import kanban.task.Task;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryHistoryManagerTest {
    private TaskManager manager;
    private HistoryManager historyManager;

    @BeforeEach
    void beforeEach() {
        manager = Manager.getDefault();
        historyManager = Manager.getDefaultHistory();
        for (int i = 0; i <= 10; i++) {
            Task task = new Task("Задача № " + i, "Описание № " + i, Status.NEW);
            manager.createTask(task);
            manager.getTask(task.getId());
        }
    }

    @Test
    public void browsingHistoryAlwaysContainsNoMoreThat_10_Objects() {
        System.out.println("Проверка размера истории");
        assertEquals(10, historyManager.getHistory().size(), "История не очищается");
    }

    @Test
    public void theTaskHistoryIsSavedInTheRequiredOrder() {
        List<String> expectedTaskList = new ArrayList<>();
        List<String> actualTaskList = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            Task task = new Task("Задача № " + (i + 1), "Описание № " + i, Status.NEW);
            expectedTaskList.add(task.getTitle());
        }
        for (Task task : historyManager.getHistory()) {
            actualTaskList.add(task.getTitle());
        }
        assertArrayEquals(new List[]{expectedTaskList}, new List[]{actualTaskList}, "Порядок нарушен");
    }

}