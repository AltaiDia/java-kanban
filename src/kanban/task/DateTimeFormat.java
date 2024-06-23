package kanban.task;

import java.time.format.DateTimeFormatter;

public class DateTimeFormat {
    static final DateTimeFormatter formatDateTime = DateTimeFormatter.ofPattern("dd.MM.yyyy, HH:mm");

    static DateTimeFormatter getFormatDateTime() {
        return formatDateTime;
    }
}
