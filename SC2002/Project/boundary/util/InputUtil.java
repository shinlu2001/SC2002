// SC2002/Project/boundary/util/InputUtil.java
package SC2002.Project.boundary.util;

import java.util.Scanner;

/** Centralised, reusable input validation helpers. */
public final class InputUtil {
    private static final Scanner SC = new Scanner(System.in);

    private InputUtil() {}

    public static String prompt(String msg) {
        System.out.print(msg);
        return SC.nextLine().trim();
    }

    // TODO: add typed prompts (int, double, date) with validation
}
