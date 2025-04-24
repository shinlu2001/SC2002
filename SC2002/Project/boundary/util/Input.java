package SC2002.Project.boundary.util;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Scanner;

/**
 * Utility interface providing standardized input handling across the BTO Management System.
 * <p>
 * This interface centralizes all user input operations, providing consistent validation,
 * error messaging, and exit mechanisms across the system's boundary classes. Key features include:
 * <ul>
 *   <li>Type-specific input methods with validation (string, integer, double, date)</li>
 *   <li>Consistent exit/back functionality in all input operations</li>
 *   <li>Range validation for numeric inputs</li>
 *   <li>Date range validation</li>
 *   <li>Recursive prompting on invalid input</li>
 *   <li>Text truncation utilities for display formatting</li>
 * </ul>
 * </p>
 * 
 * @author Group 1
 * @version 1.0
 * @since 2025-04-24
 */
public interface Input {

    /**
     * Custom exception thrown when a user signals their intent to exit the current operation.
     * <p>
     * This exception is used to provide a consistent mechanism for handling "exit" or "back"
     * commands across all input operations in the system.
     * </p>
     */
    public static class InputExitException extends RuntimeException {
        /**
         * Constructs a new InputExitException with the specified detail message.
         * 
         * @param message The detail message
         */
        public InputExitException(String message) {
            super(message);
        }
    }

    /**
     * Gets a non-empty string input from the user.
     * <p>
     * This method ensures the input is not empty and allows users to exit by typing
     * "exit" or "back". If empty input is provided, it recursively prompts until valid input is received.
     * </p>
     * 
     * @param sc The Scanner object used to read input
     * @return The user's input as a String
     * @throws InputExitException If the user types "exit" or "back"
     */
    static String getStringInput(Scanner sc) {
        String input = sc.nextLine().trim();
        if (input.equalsIgnoreCase("exit") || input.equalsIgnoreCase("back")) {
            throw new InputExitException("User requested exit/back");
        }
        if (input.isEmpty()) {
            System.out.println("Error: Input cannot be empty. Please try again.");
            return getStringInput(sc);
        }
        return input;
    }

    /**
     * Gets a non-empty string input from the user, with a custom prompt.
     * <p>
     * Displays the provided prompt before accepting input and ensures the input is not empty.
     * </p>
     * 
     * @param sc The Scanner object used to read input
     * @param prompt The text to display before accepting input
     * @return The user's input as a String
     * @throws InputExitException If the user types "exit" or "back"
     */
    public static String getStringInput(Scanner sc, String prompt) throws InputExitException {
        while (true) {
            if (prompt != null) {
                System.out.print(prompt);
            }
            String input = sc.nextLine().trim();
            if (input.equalsIgnoreCase("exit") || input.equalsIgnoreCase("back")) {
                throw new InputExitException("User requested exit/back");
            }
            if (input.isEmpty()) {
                System.out.println("Error: Input cannot be empty. Please try again.");
                continue;
            }
            return input;
        }
    }

    /**
     * Gets an integer input from the user.
     * <p>
     * This method ensures the input can be parsed as an integer and continually
     * prompts the user until valid input is received.
     * </p>
     * 
     * @param sc The Scanner object used to read input
     * @return The user's input as an integer
     * @throws InputExitException If the user types "exit" or "back"
     */
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

    /**
     * Gets an integer input from the user, with a custom prompt.
     * <p>
     * Displays the provided prompt before accepting input and ensures the input can be parsed as an integer.
     * </p>
     * 
     * @param sc The Scanner object used to read input
     * @param prompt The text to display before accepting input
     * @return The user's input as an integer
     * @throws InputExitException If the user types "exit" or "back"
     */
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

    /**
     * Gets an integer input from the user within a specified range.
     * <p>
     * Ensures the input is within the specified minimum and maximum values (inclusive).
     * </p>
     * 
     * @param sc The Scanner object used to read input
     * @param prompt The text to display before accepting input
     * @param min The minimum acceptable value (inclusive)
     * @param max The maximum acceptable value (inclusive)
     * @return The user's input as an integer within the specified range
     * @throws InputExitException If the user types "exit" or "back"
     */
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

    /**
     * Gets a double input from the user, with a custom prompt.
     * <p>
     * Displays the provided prompt before accepting input and ensures the input can be parsed as a double.
     * </p>
     * 
     * @param sc The Scanner object used to read input
     * @param prompt The text to display before accepting input
     * @return The user's input as a double
     * @throws InputExitException If the user types "exit" or "back"
     */
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

    /**
     * Gets a double input from the user with a specified minimum value.
     * <p>
     * Ensures the input is greater than or equal to the specified minimum value.
     * </p>
     * 
     * @param sc The Scanner object used to read input
     * @param prompt The text to display before accepting input
     * @param min The minimum acceptable value (inclusive)
     * @return The user's input as a double greater than or equal to the minimum
     * @throws InputExitException If the user types "exit" or "back"
     */
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

    /**
     * Gets a date input from the user in the format yyyy-MM-dd.
     * <p>
     * Ensures the input can be parsed as a valid date.
     * </p>
     * 
     * @param sc The Scanner object used to read input
     * @param prompt The text to display before accepting input
     * @return The user's input as a LocalDate object
     * @throws InputExitException If the user types "exit" or "back"
     */
    public static LocalDate getDateInput(Scanner sc, String prompt) throws InputExitException {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
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
                System.out.println("Error: Invalid date format. Please use format yyyy-MM-dd (e.g., 2025-04-30).");
                System.out.println("Tip: Make sure the date exists. For example, 2025-02-30 is invalid because February doesn't have 30 days.");
            }
        }
    }
    
    /**
     * Gets a date input from the user within a specified range.
     * <p>
     * Ensures the input is a valid date and falls within the specified range.
     * </p>
     * 
     * @param sc The Scanner object used to read input
     * @param prompt The text to display before accepting input
     * @param minDate The earliest acceptable date (inclusive), or null for no minimum
     * @param maxDate The latest acceptable date (inclusive), or null for no maximum
     * @return The user's input as a LocalDate within the specified range
     * @throws InputExitException If the user types "exit" or "back"
     */
    public static LocalDate getDateInput(Scanner sc, String prompt, LocalDate minDate, LocalDate maxDate) throws InputExitException {
        while (true) {
            LocalDate date = getDateInput(sc, prompt);
            boolean beforeMin = minDate != null && date.isBefore(minDate);
            boolean afterMax = maxDate != null && date.isAfter(maxDate);
            
            if (beforeMin && afterMax) {
                System.out.println("Error: Date must be between " + minDate + " and " + maxDate + ". Please try again.");
            } else if (beforeMin) {
                System.out.println("Error: Date cannot be before " + minDate + ". Please try again.");
            } else if (afterMax) {
                System.out.println("Error: Date cannot be after " + maxDate + ". Please try again.");
            } else {
                return date;
            }
        }
    }
    
    /**
     * Truncates text to a specified maximum length, adding ellipsis if necessary.
     * <p>
     * This utility is used throughout the UI to format display output in fixed-width columns.
     * </p>
     * 
     * @param text The text to truncate
     * @param maxLength The maximum allowed length
     * @return The truncated text, with "..." appended if truncation occurred, or "N/A" if the input was null
     */
    public static String truncateText(String text, int maxLength) {
        if (text == null) return "N/A";
        if (text.length() <= maxLength) {
            return text;
        }
        return text.substring(0, maxLength - 3) + "...";
    }
}
