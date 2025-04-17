// SC2002/Project/util/ValidationUtil.java
package SC2002.Project.util;

import java.util.regex.Pattern;

public final class ValidationUtil {
    private static final Pattern NRIC = Pattern.compile("[ST]\\d{7}[A-Z]");

    private ValidationUtil() {}

    public static boolean isValidNric(String nric) {
        return NRIC.matcher(nric).matches();
    }
}
