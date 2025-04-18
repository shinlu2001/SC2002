// SC2002/Project/entity/Report.java
package SC2002.Project.entity;

import SC2002.Project.util.IdGenerator;
import java.time.LocalDateTime;

/**
 * Entity class representing a generated report.
 */
public class Report {
    private final int id;
    private final LocalDateTime generatedDate;
    private final String content;

    public Report(String content) {
        this.id = IdGenerator.nextReportId();
        this.generatedDate = LocalDateTime.now();
        this.content = content;
    }

    public int getId() {
        return id;
    }

    public LocalDateTime getGeneratedDate() {
        return generatedDate;
    }

    public String getContent() {
        return content;
    }

    @Override
    public String toString() {
        return String.format("Report ID: %d\nGenerated on: %s\n\n%s",
            id, generatedDate.toString(), content);
    }
}
