// SC2002/Project/entity/Report.java
package SC2002.Project.entity;

import java.util.List;

/** Simple value object holding filtered report rows before CSV/print. */
public record Report(String title, List<String[]> rows) {}
