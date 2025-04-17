// SC2002/Project/boundary/util/MenuPrinter.java
package SC2002.Project.boundary.util;

/** Pretty console menu & banner rendering. */
public final class MenuPrinter {
    private MenuPrinter() {}

    public static void title(String text) {
        System.out.println("\n==== " + text + " ====");
    }

    // TODO: other helpers (options list, error, success colouring)
}
