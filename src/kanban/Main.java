package kanban;

import kanban.manager.Manager;
import kanban.manager.TaskManager;
import kanban.task.Epic;
import kanban.task.Status;
import kanban.task.Subtask;
import kanban.task.Task;

public class Main {

    public static void main(String[] args) {
        System.out.println("Поехали!");
        TaskManager manager = Manager.getDefault();

        //Отправка задач в менеджер
        Task task1 = new Task("Привет", "Сказать привет", Status.NEW);
        Task task2 = new Task("Пока", "Сказать пока", Status.NEW);
        manager.createTask(task1);
        manager.createTask(task2);

        Epic epic1 = new Epic("Пойти на работу", "Что сделать чтобы пойти на работу", Status.NEW);
        manager.createEpic(epic1);
        Subtask subtask11 = new Subtask("Шаг № 1", "Встать", Status.NEW, epic1.getId());
        Subtask subtask12 = new Subtask("Шаг № 2", "Пойти на работу", Status.NEW, epic1.getId());
        manager.createSubtask(subtask11);
        manager.createSubtask(subtask12);

        Epic epic2 = new Epic("Оплата", "Перечень того что нужно оплатить", Status.NEW);
        manager.createEpic(epic2);
        Subtask subtask21 = new Subtask("Интернет", "Оплатить интернет", Status.NEW, epic2.getId());
        manager.createSubtask(subtask21);

        manager.getTask(task1.getId());
        printAllTasks(manager);


        //меняю статусы
        task1.setStatus(Status.DONE);
        manager.updateTask(task1);
        task2.setStatus(Status.IN_PROGRESS);
        manager.updateTask(task2);

        epic1.setStatus(Status.DONE);
        subtask11.setStatus(Status.NEW);
        subtask12.setStatus(Status.DONE);
        manager.updateSubtask(subtask11);
        manager.updateSubtask(subtask12);
        manager.updateEpic(epic1);

        subtask21.setStatus(Status.DONE);
        manager.updateSubtask(subtask21);
        epic2.setTitle("Важно не забыть");
        epic2.setDescription("Оправить работу");
        manager.updateEpic(epic2);

        printAllTasks(manager);

        //Удаляю одну задачу и один из эпиков
        manager.removeTask(task1.getId());
        manager.removeEpic(epic1.getId());
        printAllTasks(manager);
        //Удаляю все
        manager.deleteAllEpic();
        System.out.println(manager.getSubtasks());
        printAllTasks(manager);

    }

    private static void printAllTasks(TaskManager manager) {
        System.out.println("Задачи:");
        for (Task task : manager.getTasks()) {
            System.out.println(task);
        }
        System.out.println("Эпики:");
        for (Epic epic : manager.getEpics()) {
            System.out.println(epic);

            for (Task task : manager.getSubtasksEpic(epic)) {
                System.out.println("--> " + task);
            }
        }
        System.out.println("Подзадачи:");
        for (Task subtask : manager.getSubtasks()) {
            System.out.println(subtask);
        }

        System.out.println("История:");
        if (manager.getHistory().isEmpty()) {
            System.out.println("Список просмотров пуст");
        } else {
            for (Task task : manager.getHistory()) {
                System.out.println(task);
            }
        }
    }
}
