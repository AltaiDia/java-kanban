package kanban.task;


import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Epic extends Task {
    private final List<Integer> subtaskId;
    private LocalDateTime endTime;

    public Epic(String title, String description, Status status) {
        super(title, description, status);
        this.subtaskId = new ArrayList<>();
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        Epic epic = (Epic) o;
        return Objects.equals(subtaskId, epic.subtaskId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), subtaskId);
    }

    @Override
    public String toString() {
        return "Epic{" +
                "subtaskId=" + subtaskId +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", id=" + id +
                ", status=" + status +
                ", taskType='" + taskType + '\'' +
                '}';
    }

    public List<Integer> getSubtaskId() {
        return subtaskId;
    }

    public void removeSubtaskId(Integer id) {
        subtaskId.remove(id);
    }

    public void setSubtaskId(Integer subtaskId) {
        this.subtaskId.add(subtaskId);
    }

    @Override
    public LocalDateTime getEndTime() {
        return endTime;
    }

    @Override
    public String getEndTimeToString() {
        return endTime.format(DateTimeFormat.getFormatDateTime());
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }
}
