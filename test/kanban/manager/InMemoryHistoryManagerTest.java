package kanban.manager;

import kanban.task.Status;
import kanban.task.Task;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryHistoryManagerTest {
    final private TaskManager manager = Manager.getDefault();
    final private HistoryManager historyManager = Manager.getDefaultHistory();
    final private Map<Integer, Integer> idTasks = new HashMap<>();

    @BeforeEach
    void BeforeEach() {
        for (int i = 0; i <= 10; i++) {
            Task task = new Task("Задача № " + i, "Описание № " + i, Status.NEW);
            manager.createTask(task);
            idTasks.put(i, task.getId());
        }
    }

    @AfterEach
    void AfterEach() {
        manager.clearAll();
        idTasks.clear();
    }

    @Test
    public void theTaskHistoryIsSavedInTheRequiredOrder() {
        for (Task task : manager.getTasks()) {
            manager.getTask(task.getId());
        }
        List<String> expectedTaskList = new ArrayList<>();
        List<String> actualTaskList = new ArrayList<>();
        for (int i = 0; i <= 10; i++) {
            Task task = new Task("Задача № " + i, "Описание № " + i, Status.NEW);
            expectedTaskList.add(task.getTitle());
        }
        for (Task task : historyManager.getHistory()) {
            actualTaskList.add(task.getTitle());
        }
        assertArrayEquals(new List[]{expectedTaskList}, new List[]{actualTaskList}, "Порядок нарушен");
    }

    @Test
    public void whenViewingSeveralIdenticalTasksOneIsSavedInTheHistoryList() {
        Task expectedTaskHead = manager.getTask(idTasks.get(0));
        for (int i = 0; i < 100; i++) {
            manager.getTask(idTasks.get(0));
        }
        assertEquals(1, manager.getHistory().size(), "Старая задача не удаляется при дублировании");
        for (Task actualTask : manager.getHistory()) {
            assertEquals(expectedTaskHead, actualTask, "Неполадка при дублировании задачи в начале списка");
        }

        Task expectedTaskTail = manager.getTask(idTasks.get(10));
        for (int i = 0; i < 100; i++) {
            manager.getTask(idTasks.get(10));
        }
        assertEquals(2, manager.getHistory().size(), "Старая задача не удаляется при дублировании");
        Task actualTask = null;
        for (Task o : manager.getHistory()) {
            actualTask = o;
        }
        assertEquals(expectedTaskTail, actualTask, "Неполадка при дублировании задачи в конце списка");
    }

    @Test
    public void correctRemovalOfTaskFromTheHistoryList() {
        List<Task> expectedTaskList = new ArrayList<>();
        List<Task> actualTaskList;

        for (int i = 0; i <= 3; i++) {
            Task task = manager.getTask(idTasks.get(i));
            expectedTaskList.add(task);
        }

        historyManager.remove(expectedTaskList.get(1).getId());
        expectedTaskList.remove(1);
        actualTaskList = manager.getHistory();
        assertArrayEquals(new List[]{expectedTaskList}, new List[]{actualTaskList},
                "Удаление задачи по середине списка не корректно");

        historyManager.remove(expectedTaskList.get(2).getId());
        expectedTaskList.remove(2);
        actualTaskList = manager.getHistory();
        assertArrayEquals(new List[]{expectedTaskList}, new List[]{actualTaskList},
                "Удаление последней задачи, не корректно");

        historyManager.remove(expectedTaskList.get(0).getId());
        expectedTaskList.remove(0);
        actualTaskList = manager.getHistory();
        assertArrayEquals(new List[]{expectedTaskList}, new List[]{actualTaskList},
                "Удаление первой задачи, не корректно");

    }
}