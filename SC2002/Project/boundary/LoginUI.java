package SC2002.Project.boundary;

import SC2002.Project.boundary.util.Input;
import SC2002.Project.control.AuthController;
import SC2002.Project.control.persistence.DataStore;
import SC2002.Project.entity.Applicant;
import SC2002.Project.entity.HDB_Manager;
import SC2002.Project.entity.HDB_Officer;
import SC2002.Project.entity.User;
import SC2002.Project.entity.enums.LoginResult;
import java.util.Scanner;

public class LoginUI {
    private static final AuthController auth = new AuthController();

    /** Called by MainMenu after user picks "1. Log in". */
    public static void start(Scanner sc) {
        // NRIC
        String nric;
        while (true) {
            System.out.print("Please enter your NRIC: ");
            nric = Input.getStringInput(sc);
            if (nric.matches("^[A-Za-z]\\d{7}[A-Za-z]$")) break;
            System.out.println("Invalid NRIC format.");
        }

        // Password
        System.out.print("Please enter your password: ");
        String pwd = Input.getStringInput(sc);

        // Authenticate
        LoginResult res = auth.login(nric, pwd);
        DataStore ds = DataStore.getInstance();
        switch (res) {
            case SUCCESS_APPLICANT -> {
                User u = ds.findUserByNric(nric).get();
                Applicant app = (Applicant) u;
                System.out.println("Login successful as Applicant: " + app.getFirstName());
                ApplicantUI.start(sc, app);
            }
            case SUCCESS_OFFICER -> {
                User u = ds.findUserByNric(nric).get();
                HDB_Officer off = (HDB_Officer) u;
                System.out.println("Login successful as Officer: " + off.getFirstName());
                OfficerUI.start(sc, off);
            }
            case SUCCESS_MANAGER -> {
                User u = ds.findUserByNric(nric).get();
                HDB_Manager mgr = (HDB_Manager) u;
                System.out.println("Login successful as Manager: " + mgr.getFirstName());
                ManagerUI.start(mgr, sc);
            }
            case INVALID_PASSWORD -> {
                System.out.println("Incorrect password. Please try again.");
            }
            case USER_NOT_FOUND -> {
                System.out.println("No such account. Please register first.");
            }
        }
    }
}
