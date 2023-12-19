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
    public int create(Task task) {
        task.id = nexId++;
        tasks.put(task.id, task);
        return task.id;
    }

    public int create(Epic epic) {
        epic.id = nexId++;
        epics.put(epic.id, epic);
        return epic.id;
    }

    public Integer create(Subtask subtask) {
        if (epics.containsKey(subtask.epicId)) {
            subtask.id = nexId++;
            Epic newEpic = epics.get(subtask.epicId);
            ArrayList<Integer> id = newEpic.getSubtaskId();

            this.subtasks.put(subtask.id, subtask);
            id.add(subtask.id);

            newEpic.setSubtaskId(id);
            return subtask.id;
        } else {
            System.out.println("Ошибка! Создание подзадчи без Большой задачи");
            return null;
        }

    }

    // Обновление статуса задач - получаем объект с уже присвоенным Id и перезаписываем
    public void update(Task task) {
        this.tasks.put(task.id, task);
    }

    public void update(Subtask subtask) {
        this.subtasks.put(subtask.id, subtask);
    }

    public void update(Epic epic) {
        ArrayList<Integer> listIdSubtask = epic.getSubtaskId();
        int newSubtask = 0;
        int doneSubtask = 0;

        if (listIdSubtask.isEmpty()) {
            epic.status = "NEW";
            this.epics.put(epic.id, epic);
        } else {
            for (Integer id : listIdSubtask) {
                if(subtasks.get(id).status.equals("NEW")){
                    newSubtask++;
                }
                if(subtasks.get(id).status.equals("DONE")){
                    doneSubtask++;
                }
            }
            if(newSubtask == listIdSubtask.size()){
                epic.status = "NEW";
                this.epics.put(epic.id, epic);
            } else if (doneSubtask == listIdSubtask.size()){
                epic.status = "DONE";
                this.epics.put(epic.id, epic);
            } else {
                epic.status = "IN_PROGRESS";
                this.epics.put(epic.id, epic);
            }
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
        tasks.remove(id);
    }

    public void removeEpic(int id) {
        Epic epic = epics.get(id);
        ArrayList<Integer> listIdSubtask = epic.getSubtaskId();
        for (Integer idSub:listIdSubtask){
            subtasks.remove(idSub);
        }
        epics.remove(id);
    }

    public void removeSubtask(int id) {
        Subtask subtask = subtasks.get(id);
        Epic epic = epics.get(subtask.epicId);

        ArrayList<Integer> listId = epic.getSubtaskId();
        listId.remove(id);
        epic.setSubtaskId(listId);

        epics.remove(id);
    }

    // Удаление всех задач
    public void deleteAll() {
        tasks.clear();
        epics.clear();
        subtasks.clear();
    }

    //Получение списков: Задач/Эпиков/Подзадач
    public ArrayList<Task> getListOfTasks() {
        ArrayList<Task> taskList = new ArrayList<>();
        for (Task task : tasks.values()) {
            taskList.add(task);
        }
        return taskList;
    }

    public ArrayList<Epic> getListOfEpics() {
        ArrayList<Epic> epicList = new ArrayList<>();
        for (Epic epic : epics.values()) {
            epicList.add(epic);
        }
        return epicList;
    }

    public ArrayList<Subtask> getListOfSubtasks() {
        ArrayList<Subtask> subtaskList = new ArrayList<>();
        for (Subtask subtask : subtasks.values()) {
            subtaskList.add(subtask);
        }
        return subtaskList;
    }

    //Дополнительный метод на получение списка определенного эпика
    public ArrayList<Subtask> getListOfSubtasksEpic(Epic newEpic) {
        ArrayList<Subtask> listSubtaskEpic = new ArrayList<>();
        for (Integer id : newEpic.getSubtaskId()) {
            listSubtaskEpic.add(subtasks.get(id));
        }
        return listSubtaskEpic;
    }

}
