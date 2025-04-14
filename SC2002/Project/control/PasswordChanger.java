package SC2002.Project.control;
import java.util.*;

import SC2002.Project.Input;
import SC2002.Project.User;
public class PasswordChanger {
    private final User user;

    public PasswordChanger(User u) {
        this.user = u;
    }

    public void changePassword(Scanner sc) {
        System.out.println("                                            Change your password");
        System.out.println("====================================================================================================================");
        System.out.print("Enter current password: ");
        String oldpass = Input.getStringInput(sc);
        System.out.print("Enter new password: ");
        String new_pass1 = Input.getStringInput(sc);
        System.out.print("Enter new password again to confirm: ");
        String new_pass2 = Input.getStringInput(sc);
        if (!verify_password(oldpass)) {
            System.out.println("Current password is wrong. Password change unsuccessful.");
        } else if (!new_pass1.equals(new_pass2)) {
            System.out.println("New passwords do not match.");
        } else {
            change_password(new_pass2);
            System.out.println("Password changed successfully.");
        }
    }
    public void change_password(String newpass) {
        user.setPassword(newpass);
    }
    public boolean verify_password(String password) {
        if (user.get_password().equals(password)) {
            return true;
        } else {
            return false;
        }
    }
}
