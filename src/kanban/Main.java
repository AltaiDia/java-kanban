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


        System.out.println(manager.getTask(task1.getId())+ "\n");

        System.out.println(manager.getTask(task2.getId())+ "\n");
        System.out.println(manager.getEpic(epic1.getId()));
        System.out.println(manager.getEpic(epic2.getId()));


        System.out.println(manager.getSubtask(subtask11.getId()));
        System.out.println(manager.getSubtask(subtask12.getId()));
        System.out.println(manager.getSubtask(subtask21.getId()));

        System.out.println(manager.getEpic(epic1.getId()));
        System.out.println(manager.getEpic(epic2.getId()));
        System.out.println(manager.getTask(task2.getId()));

        System.out.println(manager.getEpic(epic1.getId())+ "\n");

        System.out.println(manager.getHistory());


        /*//Печать списков
        System.out.println("Печать списка всех простых задач");
        System.out.println(manager.getTasks());
        System.out.println("Печать списка всех Эпиков");
        System.out.println(manager.getEpics());
        System.out.println("Печать списка всех подзадач");
        System.out.println(manager.getSubtasks());

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

        System.out.println("Печать списка всех простых задач");
        System.out.println(manager.getTasks());
        System.out.println("Печать списка всех Эпиков");
        for (Epic epic : manager.getEpics()) {
            System.out.println(epic);
            System.out.println("Печать списка подзадач");
            System.out.println(manager.getSubtasksEpic(epic));
        }

        //Удаляю одну задачу и один из эпиков
        manager.removeTask(task1.getId());
        manager.removeEpic(epic1.getId());

        //Удаляю все
        manager.deleteAllEpic();
        System.out.println(manager.getSubtasks());*/
    }
}
