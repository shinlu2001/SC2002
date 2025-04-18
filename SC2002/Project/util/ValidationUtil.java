// SC2002/Project/util/ValidationUtil.java
package SC2002.Project.util;

import java.util.regex.Pattern;

public final class ValidationUtil {
    // Updated regex to match spec: ^[A-Za-z]\d{7}[A-Za-z]$
    private static final Pattern NRIC = Pattern.compile("^[A-Za-z]\\d{7}[A-Za-z]$");

    private ValidationUtil() {}

    public static boolean isValidNric(String nric) {
        if (nric == null) return false;
        return NRIC.matcher(nric.toUpperCase()).matches(); // Ensure case-insensitivity for letters
    }
}
