package kanban.manager;

import kanban.task.Epic;
import kanban.task.Status;
import kanban.task.Subtask;
import kanban.task.Task;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class FileBackedTaskManagerTest {

    static File tempFile;
    static FileBackedTaskManager backedTaskManager;

    @BeforeAll
    static void createBackedManager()
            throws IOException {
        try {
            tempFile = File.createTempFile("temp", ".csv");

            backedTaskManager = FileBackedTaskManager.loadFromFile(tempFile);
        } catch (IOException exception) {
            exception.printStackTrace();
        }
    }

    @BeforeEach
    public void BeforeEach() {
        backedTaskManager.clearAll();
    }

    @Test
    public void savingAndLoadingAnEmptyFile() {
        System.out.println("Проверка корректности сохранения и загрузки пустого файла");
        backedTaskManager.save();
        assertTrue(backedTaskManager.getTasks().isEmpty());
        assertTrue(backedTaskManager.getEpics().isEmpty());
        assertTrue(backedTaskManager.getSubtasks().isEmpty());
        System.out.println("Проверка прошла успешно поля хранения задач пустые");
    }

    @Test
    public void savingAndLoadingTask() {
        Task task = new Task("Задача № 1", "Описание задачи № 1", Status.NEW);
        backedTaskManager.createTask(task);
        Epic epic = new Epic("Эпик № 1", "Описание Эпика № 1", Status.NEW);
        backedTaskManager.createEpic(epic);
        Subtask subtask = new Subtask("Подзадача № 1", "Описание подзадачи № 1", Status.NEW, epic.getId());
        backedTaskManager.createSubtask(subtask);

        backedTaskManager.getTask(task.getId());
        backedTaskManager.getEpic(epic.getId());
        backedTaskManager.getSubtask(subtask.getId());


        try {
            FileBackedTaskManager newBackManager = FileBackedTaskManager.loadFromFile(tempFile);
            Task task2 = newBackManager.getTask(task.getId());
            Epic epic2 = newBackManager.getEpic(epic.getId());
            Subtask subtask2 = newBackManager.getSubtask(subtask.getId());

            System.out.println("Проверка сохранения и загрузки задач");
            Assertions.assertEquals(task, task2);
            Assertions.assertEquals(epic, epic2);
            Assertions.assertEquals(subtask, subtask2);


            List<Task> history1 = backedTaskManager.getHistory();
            List<Task> histori2 = newBackManager.getHistory();

            assertArrayEquals(new List[]{history1}, new List[]{histori2},
                    "Сохраненные и загруженные списки просмотра задач не совпадают");

            System.out.println("Проверка прошла успешно");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}


