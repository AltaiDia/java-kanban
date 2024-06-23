package kanban.task;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

public class Task {
    protected String title;
    protected String description;
    protected int id = -1;
    protected Status status;
    protected TaskType taskType = null;
    protected Duration executionDuration = null;
    protected LocalDateTime startTime = null;


    /*
    Конструктор для объектов у которых время начала выполнения есть
     */
    public Task(String title, String description, Status status,
                int executionDuration, LocalDateTime startTime) {
        this.title = title;
        this.description = description;
        this.status = status;
        this.executionDuration = Duration.ofMinutes(executionDuration);
        this.startTime = startTime;
    }

    /*
    Конструктор для объектов у которых нет времени начала выполнения
    */
    public Task(String title, String description, Status status) {
        this.title = title;
        this.description = description;
        this.status = status;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Task task = (Task) o;
        return id == task.id
                && Objects.equals(title, task.title)
                && Objects.equals(description, task.description)
                && status == task.status
                && taskType == task.taskType;
    }

    @Override
    public int hashCode() {
        return Objects.hash(title, description, id, status, taskType);
    }

    @Override
    public String toString() {
        return "Task{" +
                "title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", id=" + id +
                ", status=" + status +
                ", taskType='" + taskType + '\'' +
                ", duration ='" + executionDuration + '\'' +
                ", startTime='" + startTime +
                '}';
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public TaskType getTaskType() {
        return taskType;
    }

    public void setTaskType(TaskType taskType) {
        this.taskType = taskType;
    }

    public Duration getExecutionDuration() {
        return executionDuration;
    }

    public void setExecutionDuration(long executionDuration) {
        this.executionDuration = Duration.ofMinutes(executionDuration);

    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public String getStartTimeToString() {
        if (startTime == null) {
            return "null";
        } else {
            return startTime.format(DateTimeFormat.getFormatDateTime());
        }
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public LocalDateTime getEndTime() {
        return startTime.plus(executionDuration);
    }

    public String getEndTimeToString() {
        return startTime.plus(executionDuration).format(DateTimeFormat.getFormatDateTime());
    }
}
