// SC2002/Project/boundary/LoginUI.java
package SC2002.Project.boundary;

import java.util.Scanner;

import SC2002.Project.control.AuthController;
import SC2002.Project.entity.Applicant;
import SC2002.Project.boundary.util.Input;
import SC2002.Project.boundary.util.Input.InputExitException;

public class LoginUI {
    private final Menu menu = new Menu();
    private final AuthController auth = new AuthController();

    public void start(Scanner sc) {
        while (true) {
            menu.printMenu(menu.getWelcomeMenu());
            int choice;
            try {
                choice = Input.getIntInput(sc);
            } catch (InputExitException e) {
                System.out.println("Operation cancelled. Exiting login.");
                return;
            }

            switch (choice) {
                case 1:
                    handleLogin(sc);
                    break;
                case 2:
                    handleRegistration(sc);
                    break;
                case 3:
                    // TODO: fetch-from-excel flow
                    break;
                case 4:
                    System.out.println("Goodbye!");
                    return;
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        }
    }

    private void handleLogin(Scanner sc) {
        try {
            System.out.print("Enter NRIC: ");
            String nric = Input.getStringInput(sc);

            System.out.print("Enter password: ");
            String pwd = Input.getStringInput(sc);

            String role = auth.login(nric, pwd);
            if (role == null) {
                menu.printMenu(menu.getLoginError());
            } else {
                new MainMenuUI().start(sc);
            }
        } catch (InputExitException e) {
            System.out.println("Operation cancelled. Returning to main menu.");
        }
    }

    private void handleRegistration(Scanner sc) {
        try {
            System.out.print("Full name: ");
            String fullName = Input.getStringInput(sc);
            String[] parts = fullName.split("\\s+", 2);
            String first = parts[0];
            String last  = parts.length > 1 ? parts[1] : "";

            System.out.print("NRIC: ");
            String nric = Input.getStringInput(sc);

            System.out.print("Age: ");
            int age = Input.getIntInput(sc);

            System.out.print("Marital status (SINGLE/MARRIED): ");
            String msRaw = Input.getStringInput(sc).toUpperCase();
            var ms = SC2002.Project.entity.enums.MaritalStatus.valueOf(msRaw);

            Applicant a = auth.registerApplicant(nric, first, last, ms, age);
            if (a == null) {
                System.out.println("NRIC already exists. Registration failed.");
            } else {
                System.out.println("Registration successful! Default password = \"password\"");
            }
        } catch (InputExitException e) {
            System.out.println("Operation cancelled. Returning to main menu.");
        } catch (IllegalArgumentException e) {
            System.out.println("Invalid marital status. Registration aborted.");
        }
    }
}
