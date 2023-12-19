public class Main {

    public static void main(String[] args) {
        System.out.println("Поехали!");
        TaskManager manager = new TaskManager();

        //Отправка задач в менеджер
        Task task1 = new Task("Привет", "Сказать привет", "NEW");
        Task task2 = new Task("Пока", "Сказать пока", "NEW");
        manager.create(task1);
        manager.create(task2);

        Epic epic1 = new Epic("Пойти на работу", "Что сделать чтобы пойти на работу", "NEW");
        manager.create(epic1);
        Subtask subtask11 = new Subtask("Шаг № 1", "Встать", "NEW", epic1.id);
        Subtask subtask12 = new Subtask("Шаг № 2", "Пойти на работу", "NEW", epic1.id);
        manager.create(subtask11);
        manager.create(subtask12);

        Epic epic2 = new Epic("Оплата", "Перечень того что нужно оплатить", "NEW");
        manager.create(epic2);
        Subtask subtask21 = new Subtask("Интернет", "Оплатить интернет", "NEW", epic2.id);
        manager.create(subtask21);

        //Печать списков
        System.out.println("Печать списка всех простых задач");
        System.out.println(manager.getListOfTasks());
        System.out.println("Печать списка всех Эпиков");
        System.out.println(manager.getListOfEpics());
        System.out.println("Печать списка всех подзадач");
        System.out.println(manager.getListOfSubtasks());

        //меняю статусы
        task1.status = "DONE";
        manager.update(task1);
        task2.status = "IN_PROGRESS";
        manager.update(task2);

        epic1.status = "DONE";
        subtask11.status = "NEW";
        subtask12.status = "DONE";
        manager.update(subtask11);
        manager.update(subtask12);
        manager.update(epic1);

        subtask21.status = "DONE";
        manager.update(subtask21);
        manager.update(epic2);

        System.out.println("Печать списка всех простых задач");
        System.out.println(manager.getListOfTasks());
        System.out.println("Печать списка всех Эпиков");
        for (Epic epic : manager.getListOfEpics()) {
            System.out.println(epic);
            System.out.println("Печать списка подзадач");
            System.out.println(manager.getListOfSubtasksEpic(epic));
        }

        //Удаляю одну задачу и один из эпиков
        manager.removeTask(task1.id);
        manager.removeEpic(epic1.id);

        //Удаляю все
        manager.deleteAll();
        System.out.println(manager.getListOfSubtasks());
    }
}
