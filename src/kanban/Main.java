package kanban;

import kanban.manager.TaskManager;
import kanban.task.Epic;
import kanban.task.Subtask;
import kanban.task.Task;

public class Main {

    public static void main(String[] args) {
        System.out.println("Поехали!");
        TaskManager manager = new TaskManager();

        //Отправка задач в менеджер
        Task task1 = new Task("Привет", "Сказать привет", "NEW");
        Task task2 = new Task("Пока", "Сказать пока", "NEW");
        manager.createTask(task1);
        manager.createTask(task2);

        Epic epic1 = new Epic("Пойти на работу", "Что сделать чтобы пойти на работу", "NEW");
        manager.createEpic(epic1);
        Subtask subtask11 = new Subtask("Шаг № 1", "Встать", "NEW", epic1.getId());
        Subtask subtask12 = new Subtask("Шаг № 2", "Пойти на работу", "NEW", epic1.getId());
        manager.createSubtask(subtask11);
        manager.createSubtask(subtask12);

        Epic epic2 = new Epic("Оплата", "Перечень того что нужно оплатить", "NEW");
        manager.createEpic(epic2);
        Subtask subtask21 = new Subtask("Интернет", "Оплатить интернет", "NEW", epic2.getId());
        manager.createSubtask(subtask21);

        //Печать списков
        System.out.println("Печать списка всех простых задач");
        System.out.println(manager.getTasks());
        System.out.println("Печать списка всех Эпиков");
        System.out.println(manager.getEpics());
        System.out.println("Печать списка всех подзадач");
        System.out.println(manager.getSubtasks());

        //меняю статусы
        task1.setStatus("DONE");
        manager.updateTask(task1);
        task2.setStatus("IN_PROGRESS");
        manager.updateTask(task2);

        epic1.setStatus("DONE");
        subtask11.setStatus("NEW");
        subtask12.setStatus("DONE");
        manager.updateSubtask(subtask11);
        manager.updateSubtask(subtask12);
        manager.updateEpic(epic1);

        subtask21.setStatus("DONE");
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
        System.out.println(manager.getSubtasks());
    }
}
