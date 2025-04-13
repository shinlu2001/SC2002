package SC2002.Project;

import java.util.Scanner;

public interface Input {

    // Custom unchecked exception to signal an exit request from an input loop.
    public static class InputExitException extends RuntimeException {
        public InputExitException(String message) {
            super(message);
        }
    }

    static String getStringInput(Scanner sc) {
        String input = sc.nextLine().trim();
        if (input.equalsIgnoreCase("exit") || input.equalsIgnoreCase("back")) {
            throw new InputExitException("User requested exit/back");
        }
        return input;
    }

    static int getIntInput(Scanner sc) {
        while (true) {
            String input = sc.nextLine().trim();
            if (input.equalsIgnoreCase("exit") || input.equalsIgnoreCase("back")) {
                throw new InputExitException("User requested exit/back");
            }
            try {
                return Integer.parseInt(input);
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a valid number.");
            }
        }
    }
}
