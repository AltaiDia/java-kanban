package kanban.manager;

import kanban.exception.ManagerSaveException;
import kanban.task.*;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class FileBackedTaskManager extends InMemoryTaskManager {
    private final String fileToSave;

    public FileBackedTaskManager(String fileToSave) {
        this.fileToSave = fileToSave;
    }

    /*
    Метод возвращает задачу в виде подготовленной строки
     */
    String taskInSpecialString(Task task)
            throws ManagerSaveException {
        String taskString;

        switch (task.getTaskType()) {
            case TASK:
            case EPIC:
                taskString = Integer.toString(task.getId())
                        + task.getTaskType()
                        + task.getTitle()
                        + task.getStatus()
                        + task.getDescription();
                return taskString;
            case SUBTASK:
                taskString = Integer.toString(task.getId())
                        + task.getTaskType()
                        + task.getTitle()
                        + task.getStatus()
                        + task.getDescription()
                        + ((Subtask) task).getEpicId();
                return taskString;
            default:
                throw new ManagerSaveException("Для форматирования в строку была передана задача" +
                        " с неопределенным типом");
        }

    }

    /*
    Метод сохраняет задачи в файл
     */
    void save()
            throws ManagerSaveException {
        List<Task> allTask = new ArrayList<>();
        allTask.addAll(getTasks());
        allTask.addAll(getEpics());
        allTask.addAll(getSubtasks());

        try (
                FileWriter fileWriter = new FileWriter(fileToSave);
                BufferedWriter bufferWriter = new BufferedWriter(fileWriter)
        ) {
            bufferWriter.write("id,type,name,status,description,duration,epic\n");
            for (Task task : allTask) {
                try {
                    bufferWriter.write(taskInSpecialString(task) + "\n");
                } catch (ManagerSaveException e) {
                    System.out.println(e.getMessage());
                }
            }
        } catch (IOException e) {
            throw new ManagerSaveException("Ошибка при записи задач в файл");
        }
    }

    /*
    Метод преобразует строку в объект класса Task,Epic или Subtask
     */
    Task fromString(String value)
            throws ManagerSaveException {
        String[] taskElements = value.split(",");
        Task task;
        switch (TaskType.valueOf(taskElements[1])) {
            case TASK:
                task = new Task(
                        taskElements[2],
                        taskElements[4],
                        Status.valueOf(taskElements[3]));
                break;
            case EPIC:
                task = new Epic(
                        taskElements[2],
                        taskElements[4],
                        Status.valueOf(taskElements[3]));
                break;
            case SUBTASK:
                task = new Subtask(taskElements[2],
                        taskElements[4],
                        Status.valueOf(taskElements[3]),
                        Integer.parseInt(taskElements[5]));
                break;
            default:
                throw new ManagerSaveException("Ошибка при преобразовании строки из файла,в задачу:" +
                        " в поток попала задача с неопределенным типом");
        }
        task.setTaskType(TaskType.valueOf(taskElements[1]));
        task.setId(Integer.parseInt(taskElements[0]));
        return task;
    }


    /*
    Метод реализует загрузку данных из файла и возвращает объект FileBackedTaskManager с задачами
     */
    static FileBackedTaskManager loadFromFile(File file)
            throws IOException {

        Path path = file.toPath();
        FileBackedTaskManager fileBackedTaskManager = new FileBackedTaskManager(path.toString());
        try {
            String taskString = Files.readString(path, StandardCharsets.UTF_8);

            if (taskString.isBlank()) {
                System.out.println("Загружен пустой файл");
                return fileBackedTaskManager;
            }

            String[] taskLine = taskString.split("\n");
            for (int i = 1; i < taskLine.length; i++) {
                Task task = fileBackedTaskManager.fromString(taskLine[i]);
                switch (task.getTaskType()) {
                    case TASK:

                }
            }
        } catch (IOException e) {
            System.out.println(e.getMessage());
            return null;
        }
        return fileBackedTaskManager;
    }


