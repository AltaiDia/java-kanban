package kanban.manager;

import kanban.task.Epic;
import kanban.task.Subtask;
import kanban.task.Task;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class FileBackedTaskManagerTest extends TaskManagerTest<FileBackedTaskManager> {
    static File tempFile;

    @BeforeAll
    public static void createFile() {
        try {
            tempFile = File.createTempFile("temp", ".csv");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    FileBackedTaskManager createManager() throws IOException {
        return FileBackedTaskManager.loadFromFile(tempFile);
    }

    @Test
    public void savingAndLoadingAnEmptyFile() {
        manager.clearAll();
        System.out.println("Проверка корректности сохранения и загрузки пустого файла");
        manager.save();
        assertTrue(manager.getTasks().isEmpty());
        assertTrue(manager.getEpics().isEmpty());
        assertTrue(manager.getSubtasks().isEmpty());
        System.out.println("Проверка прошла успешно поля хранения задач пустые");
    }

    @Test
    public void savingAndLoadingTask() {
        try {
            FileBackedTaskManager newBackManager = FileBackedTaskManager.loadFromFile(tempFile);
            Task newTask = newBackManager.getTask(task.getId());
            Epic newEpic = newBackManager.getEpic(epic.getId());
            Subtask newSubtask = newBackManager.getSubtask(subtask1.getId());

            System.out.println("Проверка сохранения и загрузки задач");
            Assertions.assertEquals(task, newTask);
            Assertions.assertEquals(epic, newEpic);
            Assertions.assertEquals(subtask1, newSubtask);


            List<Task> history1 = manager.getHistory();
            List<Task> histori2 = newBackManager.getHistory();

            assertArrayEquals(new List[]{history1}, new List[]{histori2},
                    "Сохраненные и загруженные списки просмотра задач не совпадают");
            System.out.println("Проверка прошла успешно");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}


