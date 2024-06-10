package kanban.task;

import java.time.LocalDateTime;
import java.util.Objects;

public class Subtask extends Task {
    protected int epicId;

    /*
    Конструктор для подзадач с заданным временем
     */
    public Subtask(String title, String description, Status status,
                   int executionDuration, LocalDateTime startTime, int epicId) {
        super(title, description, status, executionDuration, startTime);
        this.epicId = epicId;
    }

    /*
    Конструктор для подзадач не с заданным временем
    */
    public Subtask(String title, String description, Status status, int epicId) {
        super(title, description, status);
        this.epicId = epicId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        Subtask subtask = (Subtask) o;
        return epicId == subtask.epicId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), epicId);
    }

    @Override
    public String toString() {
        return "Subtask{" +
                "epicId=" + epicId +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", id=" + id +
                ", status=" + status +
                ", taskType='" + taskType + '\'' +
                '}';
    }

    public int getEpicId() {
        return epicId;
    }
}
