package kanban.manager;

import kanban.exception.ManagerSaveException;
import kanban.task.*;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.time.LocalDateTime;
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
    String taskInSpecialString(Task task) {
        switch (task.getTaskType()) {
            case TASK:
            case EPIC:
                return String.join(",",
                        String.valueOf(task.getId()), // 0
                        String.valueOf(task.getTaskType()), //1
                        task.getTitle(), //2
                        String.valueOf(task.getStatus()), //3
                        task.getDescription(), //4
                        String.valueOf(task.getExecutionDuration()), //5
                        String.valueOf(task.getStartTime())); //6

            case SUBTASK:
                int id = ((Subtask) task).getEpicId();
                return String.join(",",
                        String.valueOf(task.getId()), //0
                        String.valueOf(task.getTaskType()), //1
                        task.getTitle(), //2
                        String.valueOf(task.getStatus()), //3
                        task.getDescription(), //4
                        String.valueOf(task.getExecutionDuration()), //5
                        String.valueOf(task.getStartTime()), //6
                        String.valueOf(id)); //7

            default:
                throw new ManagerSaveException("Для форматирования в строку была передана задача" +
                        " с неопределенным типом");
        }
    }

    /*
    Метод возвращает историю просмотра задач в виде подготовленной строчки
     */
    String historiInSpecialString(List<Task> listHistory) {
        StringBuilder stringHistory = new StringBuilder();
        for (Task task : listHistory) {
            stringHistory.append(task.getId());
            stringHistory.append(",");
        }
        stringHistory.deleteCharAt(stringHistory.length() - 1);
        return stringHistory.toString();
    }

    /*
    Метод сохраняет задачи в файл
     */
    void save() {
        List<Task> allTask = new ArrayList<>();
        allTask.addAll(getTasks());
        allTask.addAll(getEpics());
        allTask.addAll(getSubtasks());

        try (
                FileWriter fileWriter = new FileWriter(fileToSave);
                BufferedWriter bufferWriter = new BufferedWriter(fileWriter)
        ) {
            bufferWriter.write("id,type,name,status,description,duration,startTime,epic");
            bufferWriter.newLine();

            for (Task task : allTask) {
                bufferWriter.write(taskInSpecialString(task));
                bufferWriter.newLine();
            }

            bufferWriter.write("idHistory");
            bufferWriter.newLine();
            if (!historyManager.getHistory().isEmpty()) {
                bufferWriter.write(historiInSpecialString(historyManager.getHistory()));
            }
        } catch (IOException exception) {
            throw new ManagerSaveException("Ошибка при сохранении в файл: " + fileToSave);
        }
    }

    /*
    Метод преобразует строку в объект класса Task,Epic или Subtask
     */
    Task fromString(String value) {
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
                        Integer.parseInt(taskElements[7]));
                break;
            default:
                throw new ManagerSaveException("Ошибка при преобразовании строки из файла,в задачу:" +
                        " в поток попала задача с неопределенным типом");
        }

        task.setTaskType(TaskType.valueOf(taskElements[1]));
        task.setId(Integer.parseInt(taskElements[0]));
        if (!taskElements[6].equals("null")) {
            task.setExecutionDuration(Duration.parse(taskElements[5]).toMinutes());
            task.setStartTime(LocalDateTime.parse(taskElements[6]));
        }
        return task;
    }


    /*
    Метод преобразует строку в массив id просмотренных задач
     */
    String[] idHistoryFromString(String value) {
        return value.split(",");
    }

    /*
    Метод реализует загрузку данных из файла и возвращает объект FileBackedTaskManager
     */
    static FileBackedTaskManager loadFromFile(File file) throws IOException {

        Path path = file.toPath();
        FileBackedTaskManager fileBackedTaskManager = new FileBackedTaskManager(path.toString());

        try {
            List<String> taskString = Files.readAllLines(path, StandardCharsets.UTF_8);

            if (taskString.size() <= 1) {
                System.out.println("Загружен пустой файл");
                return fileBackedTaskManager;
            }

            List<Task> loadedTasks = new ArrayList<>();
            List<String> idHistory = new ArrayList<>();

            if (taskString.get(taskString.size() - 1).equals("idHistory")) {
                for (int i = 1; i < taskString.size() - 1; i++) {
                    Task task = fileBackedTaskManager.fromString(taskString.get(i));
                    loadedTasks.add(task);
                }
                fileBackedTaskManager.loadingTasks(loadedTasks);
            } else {
                for (int i = 1; i < taskString.size(); i++) {
                    if (i < (taskString.size() - 2)) {
                        Task task = fileBackedTaskManager.fromString(taskString.get(i));
                        loadedTasks.add(task);
                    }
                    if (i < (taskString.size())) {
                        idHistory = List.of(fileBackedTaskManager.idHistoryFromString(taskString.get(i)));
                    }
                }
                fileBackedTaskManager.loadingTasks(loadedTasks);
                fileBackedTaskManager.loadingHistory(idHistory);
            }

        } catch (IOException exception) {
            throw new ManagerSaveException("Ошибка при загрузке файла" + file.getName());
        }
        return fileBackedTaskManager;

    }

    @Override
    public int createTask(Task task) {
        int id = super.createTask(task);
        save();
        return id;
    }

    @Override
    public int createEpic(Epic epic) {
        int id = super.createEpic(epic);
        save();
        return id;
    }

    @Override
    public Integer createSubtask(Subtask subtask) {
        int id = super.createSubtask(subtask);
        save();
        return id;
    }

    @Override
    public Task getTask(int id) {
        final Task task = super.getTask(id);
        save();
        return task;
    }

    @Override
    public Epic getEpic(int id) {
        final Epic epic = super.getEpic(id);
        save();
        return epic;
    }

    @Override
    public Subtask getSubtask(int id) {
        final Subtask subtask = super.getSubtask(id);
        save();
        return subtask;
    }

    @Override
    public void updateTask(Task task) {
        super.updateTask(task);
        save();
    }

    @Override
    public void updateSubtask(Subtask subtask) {
        super.updateSubtask(subtask);
        save();
    }

    @Override
    // обновление эпика
    public void updateEpic(Epic epic) {
        super.updateEpic(epic);
        save();
    }

    @Override
    public void removeTask(int id) {
        super.removeTask(id);
        save();
    }

    @Override
    public void removeEpic(int id) {
        super.removeEpic(id);
        save();
    }

    @Override
    public void removeSubtask(int id) {
        super.removeSubtask(id);
        save();
    }

    @Override
    public void deleteAllTask() {
        super.deleteAllTask();
        save();
    }

    @Override
    public void deleteAllEpic() {
        super.deleteAllEpic();
        save();
    }

    @Override
    public void deleteAllSubtask() {
        super.deleteAllSubtask();
        save();
    }

    @Override
    public void clearAll() {
        super.clearAll();
        save();
    }
}