package kanban.manager;

import kanban.task.Task;

import java.util.*;

public class InMemoryHistoryManager implements HistoryManager {

    private final DoublyLinkedHistoryList<Task> linkedHistoryList = new DoublyLinkedHistoryList<>();

    @Override
    public void add(Task task) {
        linkedHistoryList.linkLast(task);
    }

    @Override
    public List<Task> getHistory() {
        return linkedHistoryList.getTasks();
    }

    @Override
    public void clearHistory() {
        linkedHistoryList.clearTaskNode();
    }

    @Override
    public void remove(int id) {
        if (linkedHistoryList.isOnList(id)) {
            linkedHistoryList.removeOldTaskNode(linkedHistoryList.nodesKeeper.get(id));
        }
    }

    /*
    Реализация двусвязного списка
     */
    static class DoublyLinkedHistoryList<T extends Task> {

        Node<T> head;
        private Node<T> tail;
        private final Map<Integer, Node<T>> nodesKeeper;

        public DoublyLinkedHistoryList() {
            this.head = null;
            this.tail = null;
            this.nodesKeeper = new HashMap<>();
        }

        /*
        Реализация класса-узла
         */
        static class Node<E> {
            public E data;
            public Node<E> next;
            public Node<E> prev;

            public Node(E data) {
                this.data = data;
                this.next = null;
                this.prev = null;
            }
        }

        /*
        Проверка на - пустой ли список?
         */
        private boolean isEmpty() {
            return head == null;
        }

        /*
        Проверка на наличие задачи в истории просмотра
        */
        public boolean isOnList(int id) {
            return nodesKeeper.containsKey(id);
        }

        /*
        Добавление задачи в список
         */
        private void linkLast(T task) {
            if (isOnList(task.getId())) {
                removeOldTaskNode(nodesKeeper.get(task.getId()));
            }

            Node<T> taskNode = new Node<>(task);

            if (isEmpty()) {
                head = taskNode;
            } else {
                tail.next = taskNode;
                taskNode.prev = tail;
            }

            tail = taskNode;
            nodesKeeper.put(task.getId(), taskNode);
        }

        /*
        Удаление старого узла
         */
        private void removeOldTaskNode(Node<T> taskNode) {
            if (taskNode == head) {
                if (taskNode.next == null) {
                    uncouple(taskNode);
                    head = null;
                } else {
                    head = taskNode.next;
                    taskNode.next.prev = null;
                    uncouple(taskNode);
                }
            } else if (taskNode == tail) {
                taskNode.prev.next = null;
                tail = taskNode.prev;
                uncouple(taskNode);
            } else {
                if (taskNode.next != null) {
                    taskNode.next.prev = taskNode.prev;
                }
                taskNode.prev.next = taskNode.next;
                uncouple(taskNode);
            }
        }

        /*
        Отдельный метод для "разъединения" старого узла
         */
        private void uncouple(Node<T> taskNode) {
            nodesKeeper.remove(taskNode.data.getId());
            taskNode.next = null;
            taskNode.prev = null;
            taskNode.data = null;

        }

        /*
        Полная очистка двусвязного списка
         */
        private void clearTaskNode() {
            nodesKeeper.clear();
            this.head = null;
            this.tail = null;
        }

        /*
        Возвращение заполненной истории задач
         */
        public List<Task> getTasks() {
            List<Task> historyList = new ArrayList<>();
            Node<T> node = head;

            while (node != null) {
                historyList.add(node.data);
                node = node.next;
            }
            return historyList;
        }

    }
}

