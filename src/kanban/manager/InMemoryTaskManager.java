package kanban.manager;

import kanban.task.*;

import java.util.*;

public class InMemoryTaskManager implements TaskManager {
    HistoryManager historyManager = Manager.getDefaultHistory();
    private final Map<Integer, Task> tasks = new LinkedHashMap<>();
    private final Map<Integer, Epic> epics = new LinkedHashMap<>();
    private final Map<Integer, Subtask> subtasks = new LinkedHashMap<>();
    private int nexId;

    public InMemoryTaskManager() {
        nexId = 1;
    }

    /*
    Загрузка задач полученных из вне с присвоенными id
     */
    public void loadingTasks(List<Task> tasksToDistribute)
            throws ClassCastException {
        clearAll();
        List<Subtask> subtaskBuffer = new ArrayList<>();
        try {
            for (Task task : tasksToDistribute) {
                switch (task.getTaskType()) {
                    case TASK:
                        tasks.put(task.getId(), task);
                        break;
                    case EPIC:
                        Epic e = (Epic) task;
                        epics.put(task.getId(), e);
                        break;
                    case SUBTASK:
                        subtaskBuffer.add((Subtask) task);
                        break;
                }
            }
        } catch (ClassCastException e){
            System.out.println(e.getMessage());
        }
        for (Subtask subtask : subtaskBuffer){
            subtasks.put(subtask.getId(),subtask);

            Epic newEpic = epics.get(subtask.getEpicId());

            newEpic.setSubtaskId(subtask.getId());
            updateEpicStatus(newEpic.getId());
        }
    }

    /*
     * Получение из вне, присваивание Id и запись в HashMap
     */
    @Override
    public int createTask(Task task) {
        int taskId = nexId++;
        task.setId(taskId);
        task.setTaskType(TaskType.TASK);
        tasks.put(taskId, task);
        return taskId;
    }

    @Override
    public int createEpic(Epic epic) {
        int epicId = nexId++;
        epic.setId(epicId);
        epic.setTaskType(TaskType.EPIC);
        epics.put(epicId, epic);
        return epicId;
    }

    @Override
    public Integer createSubtask(Subtask subtask) {
        if (epics.containsKey(subtask.getEpicId())) {
            int subtaskId = nexId++;
            subtask.setId(subtaskId);
            subtask.setTaskType(TaskType.SUBTASK);
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

    /*
     * Обновление статуса задач - получаем объект с уже присвоенным Id и перезаписываем
     */
    @Override
    public void updateTask(Task task) {
        final int id = task.getId();
        final Task savedTask = tasks.get(id);
        if (savedTask == null) {
            return;
        }
        tasks.put(id, task);
    }

    @Override
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

    /*
     * Обновление эпика
     */
    @Override
    public void updateEpic(Epic epic) {
        final Epic saveEpic = epics.get(epic.getId());
        if (saveEpic == null) {
            return;
        }
        saveEpic.setTitle(epic.getTitle());
        saveEpic.setDescription(epic.getDescription());
        epics.put(saveEpic.getId(), saveEpic);
    }

    /*
     * Обновление статуса эпика
     */
    private void updateEpicStatus(int epicId) {
        Epic epic = epics.get(epicId);
        List<Integer> subtaskId = epic.getSubtaskId();
        if (subtaskId.isEmpty()) {
            epic.setStatus(Status.NEW);
            return;
        }
        int newSubtask = 0;
        int doneSubtask = 0;

        for (Integer id : subtaskId) {
            if (subtasks.get(id).getStatus().equals(Status.NEW)) {
                newSubtask++;
            }
            if (subtasks.get(id).getStatus().equals(Status.DONE)) {
                doneSubtask++;
            }
        }

        if (newSubtask == subtaskId.size()) {
            epic.setStatus(Status.NEW);
        } else if (doneSubtask == subtaskId.size()) {
            epic.setStatus(Status.DONE);
        } else {
            epic.setStatus(Status.IN_PROGRESS);
        }
    }


    /*
     Получение по Id
     */
    @Override
    public Task getTask(int id) {
        final Task task = tasks.get(id);
        historyManager.add(task);
        return task;
    }

    @Override
    public Epic getEpic(int id) {
        final Epic epic = epics.get(id);
        historyManager.add(epic);
        return epic;
    }

    @Override
    public Subtask getSubtask(int id) {
        final Subtask subtask = subtasks.get(id);
        historyManager.add(subtask);
        return subtask;
    }

    /*
    Получение списка просмотренных задач
     */
    @Override
    public List<Task> getHistory() {
        return historyManager.getHistory();
    }

    /*
    Удаление по Id
     */
    @Override
    public void removeTask(int id) {
        final Task savedTask = tasks.get(id);
        if (savedTask == null) {
            return;
        }
        tasks.remove(id);
        historyManager.remove(id);
    }

    @Override
    public void removeEpic(int id) {
        Epic epic = epics.get(id);
        if (epic == null) {
            return;
        }
        List<Integer> listIdSubtask = epic.getSubtaskId();
        for (Integer idSub : listIdSubtask) {
            subtasks.remove(idSub);
            historyManager.remove(idSub);
        }
        epics.remove(id);
        historyManager.remove(id);
    }

    @Override
    public void removeSubtask(int id) {
        Subtask subtask = subtasks.get(id);
        if (subtask == null) {
            return;
        }
        Epic epic = epics.get(subtask.getEpicId());

        epic.removeSubtaskId(id);
        subtasks.remove(id);
        historyManager.remove(id);

        epics.put(epic.getId(), epic);
        updateEpicStatus(epic.getId());
    }

    /*
     Удаление всех задач
     */
    @Override
    public void deleteAllTask() {
        for (Task o : tasks.values()) {
            historyManager.remove(o.getId());
        }
        tasks.clear();
    }

    public void deleteAllEpic() {
        for (Task o : epics.values()) {
            historyManager.remove(o.getId());
        }
        epics.clear();
        for (Task o : subtasks.values()) {
            historyManager.remove(o.getId());
        }
        subtasks.clear();
    }

    @Override
    public void deleteAllSubtask() {
        for (Task o : subtasks.values()) {
            historyManager.remove(o.getId());
        }
        subtasks.clear();
        for (Epic epic : epics.values()) {
            epic.getSubtaskId().clear();
        }
    }

    @Override
    public void clearAll() {
        tasks.clear();
        epics.clear();
        subtasks.clear();
        historyManager.clearHistory();
    }

    /*
    Получение списков: Задач/Эпиков/Подзадач
     */
    @Override
    public List<Task> getTasks() {
        return new ArrayList<>(tasks.values());
    }

    @Override
    public List<Epic> getEpics() {
        return new ArrayList<>(epics.values());
    }

    @Override
    public List<Subtask> getSubtasks() {
        return new ArrayList<>(subtasks.values());
    }

    /*
    Дополнительный метод на получение списка определенного эпика
     */
    @Override
    public List<Subtask> getSubtasksEpic(Epic newEpic) {
        List<Subtask> listSubtaskEpic = new ArrayList<>();
        for (Integer id : newEpic.getSubtaskId()) {
            listSubtaskEpic.add(subtasks.get(id));
        }
        return listSubtaskEpic;
    }
}
