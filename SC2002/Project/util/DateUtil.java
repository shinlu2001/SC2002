// SC2002/Project/util/DateUtil.java
package SC2002.Project.util;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public final class DateUtil {
    private static final DateTimeFormatter DF = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private DateUtil() {}

    public static LocalDate parse(String s) { return LocalDate.parse(s, DF); }
    public static String format(LocalDate d) { return d.format(DF); }
}
