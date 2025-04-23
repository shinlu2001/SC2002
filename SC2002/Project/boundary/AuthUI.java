package SC2002.Project.boundary;

import SC2002.Project.control.AuthController;
import SC2002.Project.entity.User;
import SC2002.Project.boundary.util.Input;

import java.util.Scanner;

/**
 * Boundary class for handling authentication related tasks like password change.
 */
public class AuthUI {
    private static final AuthController authController = new AuthController();

    public static boolean changePassword(Scanner sc, User user) {
        System.out.println("\nChange Password");
        System.out.println("---------------");
        try {
            String currentPassword;
            while (true) {
                System.out.print("Enter current password: ");
                currentPassword = Input.getStringInput(sc);
                if (user.verifyPassword(currentPassword)) {
                    break;
                }
                System.out.println("Incorrect password. Please try again.");
            }

            System.out.print("Enter new password: ");
            String newPassword1 = Input.getStringInput(sc);
            System.out.print("Confirm new password: ");
            String newPassword2 = Input.getStringInput(sc);

            if (!newPassword1.equals(newPassword2)) {
                System.out.println("New passwords do not match. Password change failed.");
                return false;
            }

            if (authController.changePassword(user, currentPassword, newPassword1)) {
                System.out.println("Password changed successfully.");
                return true;
            } else {
                System.out.println("Password change failed. Please contact support.");
                return false;
            }

        } catch (Input.InputExitException e) {
            System.out.println("Password change cancelled.");
            return false;
        }
    }
    
    // Private constructor to prevent instantiation
    private AuthUI() {}
} 