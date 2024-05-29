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
            //System.out.println(exception.getMessage());
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

        try {
            FileBackedTaskManager newBackManager = FileBackedTaskManager.loadFromFile(tempFile);
            Task task2 = newBackManager.getTask(task.getId());
           Epic epic2 = newBackManager.getEpic(epic.getId());
            Subtask subtask2 = newBackManager.getSubtask(subtask.getId());
            Assertions.assertEquals(task, task2);
            Assertions.assertEquals(epic, epic2);
            Assertions.assertEquals(subtask, subtask2);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}


