package kanban.manager;

import kanban.task.Epic;
import kanban.task.Subtask;
import kanban.task.Task;

import java.util.ArrayList;
import java.util.HashMap;

public class TaskManager {
    private HashMap<Integer, Task> tasks = new HashMap<>();
    private HashMap<Integer, Epic> epics = new HashMap<>();
    private HashMap<Integer, Subtask> subtasks = new HashMap<>();
    private int nexId;

    public TaskManager() {
        nexId = 1;
    }

    // Получение из вне, присваивание Id и запись в мапу
    public int createTask(Task task) {
        int taskId = nexId++;
        task.setId(taskId);
        tasks.put(taskId, task);
        return taskId;
    }

    public int createEpic(Epic epic) {
        int epicId = nexId++;
        epic.setId(epicId);
        epics.put(epicId, epic);
        return epicId;
    }

    public Integer createSubtask(Subtask subtask) {
        if (epics.containsKey(subtask.getEpicId())) {
            int subtaskId = nexId++;
            subtask.setId(subtaskId);
            subtasks.put(subtaskId, subtask);

            Epic newEpic = epics.get(subtask.getEpicId());

            newEpic.setSubtaskId(subtaskId);
            updateEpicStatus(newEpic.getId());
            return subtaskId;
        } else {
            System.out.println("Ошибка! Создание подзадчи без Большой задачи");
            return null;
        }

    }

    // Обновление статуса задач - получаем объект с уже присвоенным Id и перезаписываем
    public void updateTask(Task task) {
        final int id = task.getId();
        final Task savedTask = tasks.get(id);
        if (savedTask == null) {
            return;
        }
        tasks.put(id, task);
    }

    public void updateSubtask(Subtask subtask) {
        final int subId = subtask.getId();
        final int epicId = subtask.getEpicId();
        final Subtask saveSubtask = subtasks.get(subId);
        final Epic saveEpic = epics.get(epicId);

        if (saveSubtask == null) {
            return;
        }
        if (saveEpic == null) {
            return;
        }
        updateEpicStatus(saveEpic.getId());
        subtasks.put(subId, subtask);
    }

    // обновление эпика
    public void updateEpic(Epic epic) {
        final Epic saveEpic = epics.get(epic.getId());
        if (saveEpic == null) {
            return;
        }
        saveEpic.setTitle(epic.getTitle());
        saveEpic.setDescription(epic.getDescription());
    }

    //обновление статуса эпика
    private void updateEpicStatus(int epicId) {
        Epic epic = epics.get(epicId);
        ArrayList<Integer> subtaskId = epic.getSubtaskId();
        if (subtaskId.isEmpty()) {
            epic.setStatus("NEW");
            return;
        }
        int newSubtask = 0;
        int doneSubtask = 0;

        for (Integer id : subtaskId) {
            if (subtasks.get(id).getStatus().equals("NEW")) {
                newSubtask++;
            }
            if (subtasks.get(id).getStatus().equals("DONE")) {
                doneSubtask++;
            }
        }

        if (newSubtask == subtaskId.size()) {
            epic.setStatus("NEW");
        } else if (doneSubtask == subtaskId.size()) {
            epic.setStatus("DONE");
        } else {
            epic.setStatus("IN_PROGRESS");
        }
    }


    // Получение по Id
    public Task getTask(int id) {
        return tasks.get(id);
    }

    public Epic getEpic(int id) {
        return epics.get(id);
    }

    public Subtask getSubtask(int id) {
        return subtasks.get(id);
    }

    //Удаление по Id
    public void removeTask(int id) {
        final Task savedTask = tasks.get(id);
        if (savedTask == null) {
            return;
        }
        tasks.remove(id);
    }

    public void removeEpic(int id) {
        Epic epic = epics.get(id);
        if (epic == null) {
            return;
        }
        ArrayList<Integer> listIdSubtask = epic.getSubtaskId();
        for (Integer idSub : listIdSubtask) {
            subtasks.remove(idSub);
        }
        epics.remove(id);
    }

    public void removeSubtask(int id) {
        Subtask subtask = subtasks.get(id);
        if (subtask == null) {
            return;
        }
        Epic epic = epics.get(subtask.getEpicId());

        epic.getSubtaskId().remove(id);
        subtasks.remove(id);
        updateEpicStatus(epic.getId());
    }

    // Удаление всех задач
    public void deleteAllTask() {
        tasks.clear();
    }

    public void deleteAllEpic() {
        epics.clear();
        subtasks.clear();
    }

    public void deleteAllSubtask() {
        subtasks.clear();
        for (Epic epic : epics.values()) {
            epic.getSubtaskId().clear();
        }
    }

    //Получение списков: Задач/Эпиков/Подзадач
    public ArrayList<Task> getTasks() {
        return new ArrayList<>(tasks.values());
    }

    public ArrayList<Epic> getEpics() {
        return new ArrayList<>(epics.values());
    }

    public ArrayList<Subtask> getSubtasks() {
        return new ArrayList<>(subtasks.values());
    }

    //Дополнительный метод на получение списка определенного эпика
    public ArrayList<Subtask> getSubtasksEpic(Epic newEpic) {
        ArrayList<Subtask> listSubtaskEpic = new ArrayList<>();
        for (Integer id : newEpic.getSubtaskId()) {
            listSubtaskEpic.add(subtasks.get(id));
        }
        return listSubtaskEpic;
    }

}
