public class Subtask extends Task {
    protected int epicId;

    public Subtask(String title, String description, String status, int epicId) {
        super(title, description, status);
        this.epicId = epicId;
    }
}
