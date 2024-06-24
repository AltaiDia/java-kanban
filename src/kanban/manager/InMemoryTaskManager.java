package kanban.manager;

import kanban.sorters.StartTimeComparator;
import kanban.task.*;

import java.time.LocalDateTime;
import java.util.*;

public class InMemoryTaskManager implements TaskManager {
    protected final HistoryManager historyManager = Manager.getDefaultHistory();
    private final Map<Integer, Task> tasks = new LinkedHashMap<>();
    private final Map<Integer, Epic> epics = new LinkedHashMap<>();
    private final Map<Integer, Subtask> subtasks = new LinkedHashMap<>();
    private final TreeSet<Task> tasksByPriority = new TreeSet<>(new StartTimeComparator());

    private int nexId;

    public InMemoryTaskManager() {
        nexId = 1;
    }

    @Override
    public List<Task> getPrioritizedTasks() {
        return new ArrayList<>(tasksByPriority);
    }

    public boolean isTaskCrossing(Task task) {
        if (task.getStartTime() == null) {
            return false;
        }
        return getPrioritizedTasks().stream()
                .filter(t -> t.getStartTime() != null)
                .filter(t -> t.getStartTime() != task.getStartTime())
                .anyMatch(t -> ((t.getStartTime().isBefore(task.getEndTime()) &&
                        (t.getStartTime().isAfter(task.getStartTime())))) //проверка пересечения с начала
                        || ((t.getEndTime().isAfter(task.getStartTime())) &&
                        (t.getEndTime().isBefore(task.getEndTime()))) // проверка пересечения с конца
                        || ((t.getStartTime().isBefore(task.getStartTime())) &&
                        (t.getEndTime().isAfter(task.getEndTime()))) // временной промежуток task вложен в t
                        || ((t.getStartTime().isAfter(task.getStartTime()) &&
                        (t.getEndTime().isBefore(task.getEndTime())))) // временной промежуток t вложен в task
                );
    }

    /*
    Загрузка задач полученных из вне с присвоенными id
     */
    public void loadingTasks(List<Task> tasksToDistribute) {
        clearAll();
        List<Subtask> subtaskBuffer = new ArrayList<>();

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
            if (task.getTaskType() != TaskType.EPIC) {
                if (!isTaskCrossing(task)) {
                    tasksByPriority.add(task);
                }
            }

            if (task.getId() > nexId) {
                nexId = task.getId() + 1;
            }
        }

        for (Subtask subtask : subtaskBuffer) {
            subtasks.put(subtask.getId(), subtask);

            Epic newEpic = epics.get(subtask.getEpicId());

            newEpic.setSubtaskId(subtask.getId());
            updateEpicStatus(newEpic.getId());
            updateEpicDuration(newEpic.getId());

        }
    }

    /*
    Загрузка истории просмотров из вне
     */
    public void loadingHistory(List<String> idHistory) {
        historyManager.clearHistory();
        for (String id : idHistory) {
            if (tasks.containsKey(Integer.valueOf(id))) {
                getTask(Integer.parseInt(id));
            } else if (epics.containsKey(Integer.valueOf(id))) {
                getEpic(Integer.parseInt(id));
            } else if (subtasks.containsKey(Integer.valueOf(id))) {
                getSubtask(Integer.parseInt(id));
            } else {
                System.out.println("Невозможно найти задачу по id для загрузки в историю просмотров");
            }
        }
    }

    /*
     * Получение из вне, присваивание Id и запись в HashMap
     */
    @Override
    public int createTask(Task task) {
        if (isTaskCrossing(task)) {
            System.out.println("Попытка создать задачу в занятое время" + task.getTitle());
            return -1;
        }
        int taskId = nexId++;
        task.setId(taskId);
        task.setTaskType(TaskType.TASK);
        tasks.put(taskId, task);
        tasksByPriority.add(task);
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
            if (isTaskCrossing(subtask)) {
                System.out.println("Попытка создать подзадачу в занятое время" + subtask.getTitle());
                return -1;
            }
            int subtaskId = nexId++;
            subtask.setId(subtaskId);
            subtask.setTaskType(TaskType.SUBTASK);
            subtasks.put(subtaskId, subtask);
            tasksByPriority.add(subtask);

            Epic newEpic = epics.get(subtask.getEpicId());

            newEpic.setSubtaskId(subtaskId);
            epics.put(newEpic.getId(), newEpic);
            updateEpicStatus(newEpic.getId());
            updateEpicDuration(newEpic.getId());
            return subtaskId;
        } else {
            System.out.println("Ошибка! Создание подзадачи без Большой задачи");
            return -1;
        }
    }

    /*
     * Обновление статуса задач - получаем объект с уже присвоенным Id и перезаписываем
     */
    @Override
    public int updateTask(Task task) {
        final int id = task.getId();
        final Task savedTask = tasks.get(id);
        if (savedTask == null) {
            System.out.println("Попытка обновления не существующей задачи");
            return -1;
        }
        if (isTaskCrossing(task)) {
            return -1;
        }
        tasks.put(id, task);
        tasksByPriority.remove(tasks.get(id));
        tasksByPriority.add(task);
        return id;
    }

    @Override
    public int updateSubtask(Subtask subtask) {
        if (isTaskCrossing(subtask)) {
            return -1;
        }
        final int subId = subtask.getId();
        final int epicId = subtask.getEpicId();
        final Subtask saveSubtask = subtasks.get(subId);
        final Epic saveEpic = epics.get(epicId);

        if (saveSubtask == null) {
            return -1;
        }
        if (saveEpic == null) {
            return -1;
        }
        subtasks.put(subId, subtask);
        tasksByPriority.remove(subtasks.get(subId));
        tasksByPriority.add(subtask);
        updateEpicStatus(saveEpic.getId());
        updateEpicDuration(saveEpic.getId());
        return subId;
    }

    /*
     * Обновление эпика
     */
    @Override
    public void updateEpic(Epic epic) {
        final int id = epic.getId();
        final Epic saveEpic = epics.get(id);
        if (saveEpic == null) {
            return;
        }
        epics.put(id, saveEpic);
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
            Subtask subtask = subtasks.get(id);

            if (subtask.getStatus().equals(Status.NEW)) {
                newSubtask++;
            }
            if (subtask.getStatus().equals(Status.DONE)) {
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
        epics.put(epic.getId(), epic);
    }

    private void updateEpicDuration(int epicId) {
        Epic epic = epics.get(epicId);
        List<Integer> subtaskId = epic.getSubtaskId();
        if (subtaskId.isEmpty()) {
            epic.setExecutionDuration(0);
            return;
        }
        LocalDateTime start = LocalDateTime.MAX;
        LocalDateTime end = LocalDateTime.MIN;
        long duration = 0L;
        for (int id : subtaskId) {
            final Subtask subtask = subtasks.get(id);
            if (subtask.getStartTime() == null) {
                continue;
            }
            final LocalDateTime startTime = subtask.getStartTime();
            final LocalDateTime endTime = subtask.getEndTime();
            if (startTime.isBefore(start)) {
                start = startTime;
            }
            if (endTime.isAfter(end)) {
                end = endTime;
            }
            duration += subtask.getExecutionDuration().toMinutes();
        }
        epic.setExecutionDuration(duration);
        epic.setStartTime(start);
        epic.setEndTime(end);
        epics.put(epicId, epic);
    }

    /*
     Получение по Id
     */
    @Override
    public Task getTask(int id) {
        final Task task = tasks.get(id);
        if (task != null) {
            historyManager.add(task);
        }
        return task;
    }

    @Override
    public Epic getEpic(int id) {
        final Epic epic = epics.get(id);
        if (epic != null) {
            historyManager.add(epic);
        }
        return epic;
    }

    @Override
    public Subtask getSubtask(int id) {
        final Subtask subtask = subtasks.get(id);
        if (subtask != null) {
            historyManager.add(subtask);
        }
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
        tasksByPriority.remove(savedTask);
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
        updateEpicDuration(epic.getId());

    }

    /*
     Удаление всех задач
     */
    @Override
    public void deleteAllTask() {
        for (Task o : tasks.values()) {
            historyManager.remove(o.getId());
            tasksByPriority.remove(o);
        }
        tasks.clear();
    }

    public void deleteAllEpic() {
        for (Task o : epics.values()) {
            historyManager.remove(o.getId());
            tasksByPriority.remove(o);
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
            tasksByPriority.remove(o);
        }
        subtasks.clear();
        for (Epic epic : epics.values()) {
            epic.getSubtaskId().clear();
            updateEpicStatus(epic.getId());
            updateEpicDuration(epic.getId());
        }
    }

    @Override
    public void clearAll() {
        tasks.clear();
        epics.clear();
        subtasks.clear();
        historyManager.clearHistory();
        tasksByPriority.clear();
        nexId = 1;
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