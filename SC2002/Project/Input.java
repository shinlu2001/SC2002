package SC2002.Project;
import java.util.Scanner;

public interface Input {
    static String getStringInput(Scanner sc) {
        return sc.nextLine().trim();
    }

   static int getIntInput(Scanner sc) {
        while (true) {
            try {
                return Integer.parseInt(sc.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a valid number.");
            }
        }
    }
}
