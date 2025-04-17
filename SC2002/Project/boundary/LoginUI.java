package SC2002.Project.boundary;

import java.util.Scanner;

import SC2002.Project.BTOSystem;
import SC2002.Project.boundary.util.Input;
import SC2002.Project.boundary.util.Input.InputExitException;
import SC2002.Project.control.AuthController;
import SC2002.Project.entity.*;
import SC2002.Project.entity.enums.*;
public class LoginUI {
    private static final AuthController auth = new AuthController();

    public static void start(Scanner sc) {
        String nric;
        while (true) {
            try {
                System.out.print("NRIC: ");
                nric = Input.getStringInput(sc);
                if (nric.matches("^[A-Za-z]\\d{7}[A-Za-z]$")) {
                    break;  // valid NRIC
                }
                System.out.println("Invalid NRIC. Format must be: letter + 7 digits + letter (e.g. S1234567A).");
            } catch (InputExitException e) {
                System.out.println("Login cancelled.");
                return;
            }
        }

        String pwd;
        while (true) {
            try {
                System.out.print("Enter password: ");
                pwd = Input.getStringInput(sc);
                break;
            } catch (InputExitException e) {
                System.out.println("Login cancelled.");
                return;
            }
        }
            LoginResult res = auth.login(nric, pwd);
            switch (res) {
                case SUCCESS_APPLICANT -> {
                    Applicant.start_menu(sc);
                }
                case SUCCESS_OFFICER -> {
                    HDB_Officer.start_menu(sc);
                }
                case SUCCESS_MANAGER -> {
                    HDB_Manager.start_menu(sc);
                }

                case INVALID_PASSWORD -> {
                    System.out.println("Incorrect password. Please try again\n");
                    BTOSystem.MainMenu();
                }
                case USER_NOT_FOUND -> {
                    System.out.println("Account does not exist. Please try again\n");
                    BTOSystem.MainMenu();
                }
            }
    }
}