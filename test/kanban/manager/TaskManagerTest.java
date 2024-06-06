package kanban.manager;

import kanban.task.Epic;
import kanban.task.Status;
import kanban.task.Subtask;
import kanban.task.Task;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public abstract class TaskManagerTest<T extends TaskManager> {
    T manager;
    Task task;
    Epic epic;
    Subtask subtask1;
    Subtask subtask2;
    Subtask subtask3;

    abstract T createManager() throws IOException;

    @BeforeEach
    protected void createTasks() throws IOException {
        manager = createManager();
        manager.clearAll();
        task = new Task("Задача № 1", "Описание задачи № 1", Status.NEW,
                65, LocalDateTime.now());
        manager.createTask(task);
        epic = new Epic("Эпик № 1", "Описание эпика № 1", Status.NEW);
        manager.createEpic(epic);

        subtask1 = new Subtask("Подзадача № 1", "Описание Подзадачи № 1",
                Status.NEW, 65, LocalDateTime.now().plusHours(2), epic.getId());
        manager.createSubtask(subtask1);
        subtask2 = new Subtask("Подзадача № 2", "Описание Подзадачи № 2",
                Status.NEW, 65, LocalDateTime.now().plusHours(4), epic.getId());
        manager.createSubtask(subtask2);
        subtask3 = new Subtask("Подзадача № 3", "Описание Подзадачи № 3",
                Status.NEW, 65, LocalDateTime.now().plusHours(6), epic.getId());
        manager.createSubtask(subtask3);
    }

    @Test
    public void addNewTask() {
        System.out.println("Проверка добавления задачи...");
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

        final int saveEpicId = epic.getId();
        final Epic savedEpic = manager.getEpic(saveEpicId);

        assertNotNull(savedEpic, "Эпик не найден");
        assertEquals(epic, savedEpic, "Эпики не совпадают");

        final List<Epic> epics = manager.getEpics();

        assertNotNull(epics, "Эпики не возвращаются.");
        assertEquals(1, epics.size(), "Неверное количество эпиков.");
        assertEquals(epic, epics.get(0), "Эпики не совпадают.");
    }

    @Test
    public void addSubTask() {
        System.out.println("Проверка добавления подзадачи...");

        final int saveSubtaskId = subtask1.getId();
        final Subtask savedSubtask = manager.getSubtask(saveSubtaskId);

        assertNotNull(savedSubtask, "Подзадача не найдена");
        assertEquals(subtask1, savedSubtask, "Подзадачи не совпадают");

        final List<Subtask> subtasks = manager.getSubtasks();

        assertNotNull(epic.getSubtaskId(), "Подзадача не записалась под большую задачу");
        assertNotNull(subtasks, "Подзадачи не возвращаются.");
        assertEquals(3, subtasks.size(), "Неверное количество Подзадач.");
        assertEquals(subtask1, subtasks.get(0), "Подзадачи не совпадают.");
    }

    @Test
    public void removeSubtask() {
        List<Integer> expectedSubTaskList = new ArrayList<>();

        expectedSubTaskList.add(subtask1.getId());
        expectedSubTaskList.add(subtask3.getId());

        manager.removeSubtask(subtask2.getId());

        assertArrayEquals(new List[]{expectedSubTaskList}, new List[]{epic.getSubtaskId()},
                "Внутри Epic остается не актуальный id SubTask");
    }

    @Test
    public void epicStatusUpdate() {
        assertEquals(epic.getStatus(), Status.NEW, "Cтатус эпика не соотвествует ожидаемому - NEW");
        subtask1.setStatus(Status.DONE);
        manager.updateSubtask(subtask1);
        manager.updateEpic(epic);
        assertEquals(epic.getStatus(), Status.IN_PROGRESS, "Cтатус эпика не соотвествует ожидаемому " +
                "- IN_PROGRESS");
        subtask2.setStatus(Status.DONE);
        subtask3.setStatus(Status.DONE);
        manager.updateSubtask(subtask1);
        manager.updateEpic(epic);
        assertEquals(epic.getStatus(), Status.DONE, "Cтатус эпика не соотвествует ожидаемому " +
                "- DONE");
    }

    @Test
    public void testTimeСrossing() {
        Task crossingTask = new Task("Задача", "Описание задачи", Status.NEW,
                65, LocalDateTime.now().minusMinutes(15));
        Integer g1 = manager.createTask(crossingTask);
        assertEquals(g1, -1, "Пересечение не было обнаруженно");
        Subtask crossingSubtask = new Subtask("Подзадача", "Описание Подзадачи",
                Status.NEW, 65, LocalDateTime.now().plusHours(1), epic.getId());
        Integer g2 = manager.createSubtask(crossingSubtask);
        assertEquals(g2, -1, "Пересечение не было обнаруженно");
    }

}
