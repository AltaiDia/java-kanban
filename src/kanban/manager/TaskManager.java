package kanban.manager;

import kanban.task.Epic;
import kanban.task.Subtask;
import kanban.task.Task;

import java.util.ArrayList;
import java.util.List;

public interface TaskManager {
    // Получение из вне, присваивание Id и запись в мапу
    int createTask(Task task);

    int createEpic(Epic epic);

    Integer createSubtask(Subtask subtask);

    // Обновление статуса задач - получаем объект с уже присвоенным Id и перезаписываем
    int updateTask(Task task);

    int updateSubtask(Subtask subtask);

    // обновление эпика
    void updateEpic(Epic epic);

    // Получение по Id
    Task getTask(int id);

    Epic getEpic(int id);

    Subtask getSubtask(int id);

    //Удаление по Id
    void removeTask(int id);

    void removeEpic(int id);

    void removeSubtask(int id);

    // Удаление всех задач
    void deleteAllTask();

    void deleteAllEpic();

    void deleteAllSubtask();

    void clearAll();

    //Получение списков: Задач/Эпиков/Подзадач
    List<Task> getTasks();

    List<Epic> getEpics();

    List<Subtask> getSubtasks();

    //Дополнительный метод на получение списка определенного эпика
    List<Subtask> getSubtasksEpic(Epic newEpic);

    //Получение списка просмотренных задач
    List<Task> getHistory();

    List<Task> getPrioritizedTasks();
}
