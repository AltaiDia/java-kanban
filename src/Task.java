
public class Task {
    protected String title;
    protected String description;
    protected int id = -1;
    protected String status;

    public Task(String title, String description, String status) {
        this.title = title;
        this.description = description;
        this.status = status;
    }

    @Override
    public String toString() {
        return "Task id = " + id +
                " Название задачи='" + title + '\'' +
                ", Описание задачи='" + description + '\'' +
                ", Статус='" + status + '\'';
    }


}
