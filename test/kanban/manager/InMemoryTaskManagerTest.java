package kanban.manager;

import kanban.task.Epic;
import kanban.task.Status;
import kanban.task.Subtask;
import kanban.task.Task;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryTaskManagerTest {
    private TaskManager manager = Manager.getDefault();

    @BeforeEach
    public void beforeAll() {
        manager.deleteAllTask();
    }

    @Test
    public void addNewTask() {
        System.out.println("Проверка добавления задачи...");

        Task task = new Task("Тестовая новая задача", "Описание задачи", Status.NEW);
        manager.createTask(task);

        final int saveTaskId = task.getId();
        final Task savedTask = manager.getTask(saveTaskId);

        assertNotNull(savedTask, "Задача не найдена");
        assertEquals(task, savedTask, "Задачи не совпадают");

        final List<Task> tasks = manager.getTasks();

        assertNotNull(tasks, "Задачи не возвращаются.");
        assertEquals(1, tasks.size(), "Неверное количество задач.");
        assertEquals(task, tasks.get(0), "Задачи не совпадают.");
    }

    @Test
    public void addNewEpic() {
        System.out.println("Проверка добавления большой задачи...");

        Epic epic = new Epic("Тестовая новая большая задача", "Описание большой задачи", Status.NEW);
        manager.createEpic(epic);

        final int saveEpicId = epic.getId();
        final Epic savedEpic = manager.getEpic(saveEpicId);

        assertNotNull(savedEpic, "Большая задача не найдена");
        assertEquals(epic, savedEpic, "Большие задачи не совпадают");

        final List<Epic> epics = manager.getEpics();

        assertNotNull(epics, "Большие задачи не возвращаются.");
        assertEquals(1, epics.size(), "Неверное количество больших задач.");
        assertEquals(epic, epics.get(0), "Большие задачи не совпадают.");
    }

    @Test
    public void addSubTask() {
        System.out.println("Проверка добавления подзадачи...");

        Epic epic = new Epic("Тестовая новая большая задача", "Описание большой задачи", Status.NEW);
        manager.createEpic(epic);

        Subtask subtask = new Subtask("Тестовая новая подзадача", "Описание подзадачи", Status.NEW, epic.getId());
        manager.createSubtask(subtask);

        final int saveSubtaskId = subtask.getId();
        final Subtask savedSubtask = manager.getSubtask(saveSubtaskId);

        assertNotNull(savedSubtask, "Подзадача не найдена");
        assertEquals(subtask, savedSubtask, "Подзадачи не совпадают");

        final List<Subtask> subtasks = manager.getSubtasks();

        assertNotNull(epic.getSubtaskId(), "Подзадача не записалась под большую задачу");
        assertNotNull(subtasks, "Подзадачи не возвращаются.");
        assertEquals(1, subtasks.size(), "Неверное количество Подзадач.");
        assertEquals(subtask, subtasks.get(0), "Подзадачи не совпадают.");


    }
}