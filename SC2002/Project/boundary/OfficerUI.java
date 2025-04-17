package SC2002.Project.boundary;

import SC2002.Project.boundary.util.Input;
import SC2002.Project.boundary.util.Input.InputExitException;
import SC2002.Project.boundary.util.MenuPrinter;
import SC2002.Project.control.AuthController;
import SC2002.Project.entity.HDB_Officer;
import java.util.Scanner;

public class OfficerUI {
    private final HDB_Officer user;
    private final AuthController auth = new AuthController();

    private OfficerUI(HDB_Officer user) {
        this.user = user;
    }

    /** Entry point from LoginUI. */
    public static void start(HDB_Officer user, Scanner sc) {
        new OfficerUI(user).menuLoop(sc);
    }

    private void menuLoop(Scanner sc) {
        boolean quit = false;
        while (!quit) {
            MenuPrinter.printOfficerMenu();
            int choice = Input.getIntInput(sc);
            switch (choice) {
                case 1  -> applyForProjectFlow(sc);
                case 2  -> viewActiveApplication();
                case 3  -> viewEligibleListings();
                case 4  -> viewAllListings();
                case 5  -> withdrawApplicationFlow(sc);
                case 6  -> handleUserEnquiriesFlow(sc);
                case 7  -> handleOwnEnquiriesFlow(sc);
                case 8  -> viewAccountDetails();
                case 9  -> changePassword(sc);
                case 10 -> registerForProjectFlow(sc);
                case 11 -> viewRegistrationStatus();
                case 12 -> viewProjectDetails();
                case 13 -> processFlatBookingFlow(sc);
                case 14 -> viewAssignedApplications();
                case 15 -> {
                    System.out.println("Logging out Officer...");
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
    
    /* ===== stubs ===== */
    private void applyForProjectFlow(Scanner sc) { /* TODO */ }
    private void viewActiveApplication()        { /* TODO */ }
    private void viewEligibleListings()         { /* TODO */ }
    private void viewAllListings()              { /* TODO */ }
    private void withdrawApplicationFlow(Scanner sc){ /* TODO */ }
    private void handleUserEnquiriesFlow(Scanner sc) { /* TODO */ }
    private void handleOwnEnquiriesFlow(Scanner sc)   { /* TODO */ }
    private void viewAccountDetails()           { /* TODO */ }
    private void registerForProjectFlow(Scanner sc){ /* TODO */ }
    private void viewRegistrationStatus()       { /* TODO */ }
    private void viewProjectDetails()           { /* TODO */ }
    private void processFlatBookingFlow(Scanner sc){ /* TODO */ }
    private void viewAssignedApplications()     { /* TODO */ }
}
