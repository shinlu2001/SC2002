package SC2002.Project.boundary;

import SC2002.Project.boundary.util.Input;
import SC2002.Project.boundary.util.Input.InputExitException;
import SC2002.Project.control.AuthController;
import SC2002.Project.entity.enums.*;
import java.util.Scanner;

public class RegistrationUI {
    private static final AuthController auth = new AuthController();

    public static void start(Scanner sc) {
        String nric;
        try {
            while (true) {
                try {
                    System.out.print("NRIC: ");
                    nric = Input.getStringInput(sc);
            
                    // regex:  start→ letter, 7 digits, letter →end
                    if (nric.matches("^[A-Za-z]\\d{7}[A-Za-z]$")) {
                        break;  // valid NRIC, exit loop
                    } else {
                        System.out.println("Invalid NRIC. Format must be: letter + 7 digits + letter (e.g. S1234567A).");
                    }
                } catch (InputExitException e) {
                    System.out.println("Operation cancelled.");
                    return;  // or break out to your caller
                }
            }
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
            
            String lastName;
            while (true) {
                try {
                    System.out.print("Last name (letters and digits only.): ");
                    String input = Input.getStringInput(sc).trim();
                    // allow empty last name
                    if (input.isEmpty()) {
                        lastName = "";
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
            
            int age;
            while (true) {
                try {
                    System.out.print("Age: ");
                    age = Input.getIntInput(sc);
                    if (age < 0) {
                        System.out.println("Invalid age. Please enter 0 or a positive number.");
                        continue;
                    }
                    break;
                } catch (InputExitException e) {
                    System.out.println("Operation cancelled. Returning to previous menu.");
                    return;
                }
            }
            
            //System.out.printf("Name: %s %s, Age: %d%n", firstName, lastName, age);
            

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

            int roleChoice;
            while (true) {
                try {
                    System.out.println("Register as:\n1) Applicant\n2) Officer\n3) Manager");
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

            RegistrationResult result;
            switch (roleChoice) {
                case 1 -> result = auth.registerApplicant(nric, firstName, lastName, ms, age);
                case 2 -> result = auth.registerOfficer   (nric, firstName, lastName, ms, age);
                case 3 -> result = auth.registerManager   (nric, firstName, lastName, ms, age);
                default -> {
                    System.out.println("Invalid role choice.");
                    return;
                }
            }

            if (result == RegistrationResult.SUCCESS) {
                System.out.println("Registration successful! Default password = \"password\"");
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
