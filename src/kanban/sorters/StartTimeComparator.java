package kanban.sorters;

import kanban.task.Task;

import java.util.Comparator;

public class StartTimeComparator implements Comparator<Task> {
    @Override
    public int compare(Task t1, Task t2) {
        if (t1.getStartTime() == null && t2.getStartTime() == null) {
            return 0;
        }
        if (t1.getStartTime() == null) {
            return -1;
        }
        if (t2.getStartTime() == null) {
            return 1;
        }
        return t1.getStartTime().compareTo(t2.getStartTime());
    }
}

