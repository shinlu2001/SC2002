package SC2002.Project.boundary.util;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
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

    public static int getIntInput(Scanner sc, String prompt) throws InputExitException {
        while (true) {
            if (prompt != null) {
                System.out.print(prompt);
            }
            String line = sc.nextLine().trim();
            if (line.equalsIgnoreCase("exit") || line.equalsIgnoreCase("back")) {
                throw new InputExitException("User requested exit/back");
            }
            try {
                return Integer.parseInt(line);
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a whole number.");
            }
        }
    }

    public static int getIntInput(Scanner sc, String prompt, int min, int max) throws InputExitException {
        while (true) {
            int value = getIntInput(sc, prompt);
            if (value >= min && value <= max) {
                return value;
            } else {
                System.out.println("Input out of range. Please enter a number between " + min + " and " + max + ".");
            }
        }
    }

    public static double getDoubleInput(Scanner sc, String prompt) throws InputExitException {
        while (true) {
            if (prompt != null) {
                System.out.print(prompt);
            }
            String line = sc.nextLine().trim();
            if (line.equalsIgnoreCase("exit") || line.equalsIgnoreCase("back")) {
                throw new InputExitException("User requested exit/back");
            }
            try {
                return Double.parseDouble(line);
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a number (e.g., 123.45).");
            }
        }
    }

    public static double getDoubleInput(Scanner sc, String prompt, double min) throws InputExitException {
        while (true) {
            double value = getDoubleInput(sc, prompt);
            if (value >= min) {
                return value;
            } else {
                System.out.println("Input below minimum. Please enter a number greater than or equal to " + min + ".");
            }
        }
    }

    public static LocalDate getDateInput(Scanner sc, String prompt) throws InputExitException {
        DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE;
        while (true) {
            if (prompt != null) {
                System.out.print(prompt);
            }
            String line = sc.nextLine().trim();
            if (line.equalsIgnoreCase("exit") || line.equalsIgnoreCase("back")) {
                throw new InputExitException("User requested exit/back");
            }
            try {
                return LocalDate.parse(line, formatter);
            } catch (DateTimeParseException e) {
                System.out.println("Invalid date format. Please use yyyy-MM-dd.");
            }
        }
    }
    
    public static String truncateText(String text, int maxLength) {
        if (text == null) return "N/A";
        if (text.length() <= maxLength) {
            return text;
        }
        return text.substring(0, maxLength - 3) + "...";
    }
}
