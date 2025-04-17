package SC2002.Project.boundary;

import SC2002.Project.boundary.util.Input;
import SC2002.Project.boundary.util.Input.InputExitException;
import SC2002.Project.boundary.util.MenuPrinter;
import SC2002.Project.control.AuthController;
import SC2002.Project.entity.Applicant;
import java.util.Scanner;

public class ApplicantUI {
    private final Applicant user;
    private final AuthController auth = new AuthController();

    private ApplicantUI(Applicant user) {
        this.user = user;
    }

    /** Entry point from LoginUI. */
    public static void start(Applicant user, Scanner sc) {
        new ApplicantUI(user).menuLoop(sc);
    }

    private void menuLoop(Scanner sc) {
        boolean quit = false;
        while (!quit) {
            MenuPrinter.printApplicantMenu();
            int choice = Input.getIntInput(sc);
            switch (choice) {
                case 1  -> applyProject(sc);
                case 2  -> viewActiveApplication();
                case 3  -> viewEligibleListings();
                case 4  -> viewAllListings();
                case 5  -> withdrawApplication(sc);
                case 6  -> manageEnquiry(sc);
                case 7  -> viewAccountDetails();
                case 8  -> changePassword(sc);
                case 9  -> {
                    System.out.println("Logging out Applicant...");
                    quit = true;
                }
                default -> System.out.println("Invalid choice.");
            }
        }
        // returns to MainMenu
    }

    private void changePassword(Scanner sc) {
        try {
            // 1) Verify current password
            String oldPwd;
            while (true) {
                System.out.print("Enter current password: ");
                oldPwd = Input.getStringInput(sc);
                if (user.verifyPassword(oldPwd)) {
                    break;
                }
                System.out.println("Incorrect – please try again.");
            }
    
            // 2) Prompt for new password
            System.out.print("Enter new password: ");
            String newPwd = Input.getStringInput(sc);
    
            // 3) Commit change
            boolean ok = auth.changePassword(user.getNric(), oldPwd, newPwd);
            System.out.println(ok
                ? "Password changed successfully."
                : "Failed to change password.");
        } catch (InputExitException e) {
            System.out.println("Password change cancelled. Returning to menu.");
            // simply return to your menuLoop; no further action needed
        }
    }

    /* ===== stubs for the other flows ===== */
    private void applyProject(Scanner sc)       { /* TODO */ }
    private void viewActiveApplication()        { /* TODO */ }
    private void viewEligibleListings()         { /* TODO */ }
    private void viewAllListings()              { /* TODO */ }
    private void withdrawApplication(Scanner sc){ /* TODO */ }
    private void manageEnquiry(Scanner sc)      { /* TODO */ }
    private void viewAccountDetails()           { /* TODO */ }
}
