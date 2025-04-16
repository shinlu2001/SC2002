package SC2002.Project.control;

import java.util.Scanner;
import SC2002.Project.entity.User;
import SC2002.Project.util.Input;

/**
 * Provides functionality to change a User's password.
 */
public class PasswordChanger {
    /**
     * Immediately attempts to change the given user's password.
     *
     * @param user the User whose password is to be changed.
     */
    public PasswordChanger(User user) {
        changePassword(user);
    }

    private void changePassword(User user) {
        Scanner sc = new Scanner(System.in);
        System.out.print("Enter current password: ");
        String current = Input.getStringInput(sc);
        if (!user.verifyPassword(current)) {
            System.out.println("Incorrect current password. Password change aborted.");
            return;
        }
        System.out.print("Enter new password: ");
        String newPassword = Input.getStringInput(sc);
        System.out.print("Confirm new password: ");
        String confirmPassword = Input.getStringInput(sc);
        if (!newPassword.equals(confirmPassword)) {
            System.out.println("New passwords do not match. Password change aborted.");
            return;
        }
        user.setPassword(newPassword);
        System.out.println("Password changed successfully.");
    }
}
