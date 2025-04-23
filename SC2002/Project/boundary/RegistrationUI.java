package SC2002.Project.boundary;

import SC2002.Project.boundary.util.Input;
import SC2002.Project.boundary.util.Input.InputExitException;
import SC2002.Project.control.AuthController;
import SC2002.Project.entity.enums.*;
import java.util.Scanner;

/**
 * User interface for the registration process.
 * <p>
 * This class handles user registration for all roles (Applicant, Officer,
 * Manager).
 * It guides users through the entire registration process, collecting required
 * information and validating inputs before creating a new user account.
 * </p>
 * <p>
 * Fulfills the registration requirements by:
 * <ul>
 * <li>Collecting and validating NRIC format</li>
 * <li>Collecting required user details (name, age, marital status)</li>
 * <li>Supporting role selection (Applicant/Officer/Manager)</li>
 * <li>Providing clear feedback on registration success/failure</li>
 * </ul>
 * </p>
 */
public class RegistrationUI {
    private static final AuthController auth = new AuthController();

    /**
     * Starts the registration process by collecting user information and creating
     * a new account in the system.
     * 
     * @param sc Scanner for reading user input
     */
    public static void start(Scanner sc) {
        String nric;
        try {
            // Collect and validate NRIC
            while (true) {
                try {
                    System.out.print("NRIC: ");
                    nric = Input.getStringInput(sc);

                    // regex: start→ letter, 7 digits, letter →end
                    if (nric.matches("^[A-Za-z]\\d{7}[A-Za-z]$")) {
                        break; // valid NRIC, exit loop
                    } else {
                        System.out
                                .println("Invalid NRIC. Format must be: letter + 7 digits + letter (e.g. S1234567A).");
                    }
                } catch (InputExitException e) {
                    System.out.println("Operation cancelled.");
                    return; // or break out to your caller
                }
            }

            // Collect first name (required)
            String firstName;
            while (true) {
                try {
                    System.out.print("First name (letters and digits only): ");
                    String input = Input.getStringInput(sc).trim();
                    if (input.isEmpty()) {
                        System.out.println("First name cannot be empty.");
                        continue;
                    }
                    if (!input.matches("[A-Za-z0-9]+")) {
                        System.out.println("First name may only contain letters and digits.");
                        continue;
                    }
                    firstName = input;
                    break;
                } catch (InputExitException e) {
                    System.out.println("Operation cancelled. Returning to previous menu.");
                    return;
                }
            }

            // Collect last name (optional)
            String lastName;
            while (true) {
                try {
                    System.out.println("Last name (OPTIONAL - letters and digits only):");
                    System.out.print("Enter last name or just press Enter to skip: ");
                    String input = Input.getStringInput(sc).trim();
                    // Allow empty last name (user can press Enter to skip)
                    if (input.isEmpty()) {
                        lastName = "";
                        System.out.println("No last name provided. Continuing with first name only.");
                        break;
                    }
                    if (!input.matches("[A-Za-z0-9]+")) {
                        System.out.println("Last name may only contain letters and digits.");
                        continue;
                    }
                    lastName = input;
                    break;
                } catch (InputExitException e) {
                    System.out.println("Operation cancelled. Returning to previous menu.");
                    return;
                }
            }

            // Collect age
            int age;
            while (true) {
                try {
                    // Use the Input utility method with a range check
                    age = Input.getIntInput(sc, "Age: ", 0, 150); // Assuming 150 is a reasonable max age
                    // The Input.getIntInput method already handles the >= 0 check implicitly via
                    // the min parameter
                    break; // Exit loop if input is valid
                } catch (InputExitException e) {
                    System.out.println("Operation cancelled. Returning to previous menu.");
                    return;
                } catch (NumberFormatException e) {
                    // This catch might be redundant if Input.getIntInput handles it, but kept for
                    // safety
                    System.out.println("Invalid input. Please enter a whole number.");
                }
            }

            // Collect marital status
            MaritalStatus ms;
            while (true) {
                try {
                    System.out.print("Marital status (Single or Married): ");
                    String raw = Input.getStringInput(sc).trim().toLowerCase();

                    if (raw.equals("s") || raw.equals("single")) {
                        ms = MaritalStatus.SINGLE;
                        break;
                    }
                    if (raw.equals("m") || raw.equals("married")) {
                        ms = MaritalStatus.MARRIED;
                        break;
                    }

                    System.out.println("Invalid input. Please enter 'S', 'Single', 'M', or 'Married'.");
                } catch (InputExitException e) {
                    System.out.println("Operation cancelled. Returning to previous menu.");
                    return;
                }
            }

            // Choose role for registration
            int roleChoice;
            while (true) {
                try {
                    System.out.println("Register as:");
                    System.out.println("1) Applicant");
                    System.out.println("2) Officer");
                    System.out.println("3) Manager");
                    roleChoice = Input.getIntInput(sc);
                    if (roleChoice >= 1 && roleChoice <= 3) {
                        break;
                    } else {
                        System.out.println("Invalid choice. Please enter 1, 2, or 3.");
                    }
                } catch (InputExitException e) {
                    System.out.println("Operation cancelled. Returning to previous menu.");
                    return;
                }
            }

            // Register user based on selected role
            RegistrationResult result;
            switch (roleChoice) {
                case 1 -> {
                    result = auth.registerApplicant(nric, firstName, lastName, ms, age);
                    System.out.println("Processing Applicant registration...");
                }
                case 2 -> {
                    result = auth.registerOfficer(nric, firstName, lastName, ms, age);
                    System.out.println("Processing Officer registration...");
                }
                case 3 -> {
                    result = auth.registerManager(nric, firstName, lastName, ms, age);
                    System.out.println("Processing Manager registration...");
                }
                default -> {
                    System.out.println("Invalid role choice.");
                    return;
                }
            }

            if (result == RegistrationResult.SUCCESS) {
                System.out.println("Registration successful! Default password = \"password\"");
                if (roleChoice == 2 || roleChoice == 3) {
                    System.out.println("Note: Staff accounts require approval by a manager before they can be used.");
                }
            } else {
                System.out.println("NRIC already exists. Registration failed.");
            }

        } catch (InputExitException e) {
            System.out.println("Registration cancelled.");
        } catch (IllegalArgumentException e) {
            System.out.println("Invalid marital status. Aborted.");
        }
    }
}
