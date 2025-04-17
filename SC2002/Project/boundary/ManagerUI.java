package SC2002.Project.boundary;

import SC2002.Project.boundary.util.Input;
import SC2002.Project.boundary.util.Input.InputExitException;
import SC2002.Project.boundary.util.MenuPrinter;
import SC2002.Project.control.AuthController;
import SC2002.Project.entity.HDB_Manager;
import java.util.Scanner;

public class ManagerUI {
    private final HDB_Manager user;
    private final AuthController auth = new AuthController();

    private ManagerUI(HDB_Manager user) {
        this.user = user;
    }

    /** Entry point from LoginUI. */
    public static void start(HDB_Manager user, Scanner sc) {
        new ManagerUI(user).menuLoop(sc);
    }

    private void menuLoop(Scanner sc) {
        boolean quit = false;
        while (!quit) {
            MenuPrinter.printManagerMenu();
            int choice = Input.getIntInput(sc);
            switch (choice) {
                case 1  -> createProjectFlow(sc);
                case 2  -> editProjectFlow(sc);
                case 3  -> deleteProjectFlow(sc);
                case 4  -> viewAllProjects();
                case 5  -> viewMyProjects();
                case 6  -> viewOfficerRegistrations();
                case 7  -> approveOfficerRegistration(sc);
                case 8  -> approveOfficerWithdrawal(sc);
                case 9  -> approveApplications(sc);
                case 10 -> approveApplicationWithdrawal(sc);
                case 11 -> generateApplicantReportFlow(sc);
                case 12 -> viewAllEnquiries();
                case 13 -> handleProjectEnquiriesFlow(sc);
                case 14 -> viewAccountDetails();
                case 15 -> changePassword(sc);
                case 16 -> {
                    System.out.println("Logging out Manager...");
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
    private void createProjectFlow(Scanner sc)      { /* TODO */ }
    private void editProjectFlow(Scanner sc)        { MenuPrinter.printEditProjectMenu(); }
    private void deleteProjectFlow(Scanner sc)      { /* TODO */ }
    private void viewAllProjects()                  { /* TODO */ }
    private void viewMyProjects()                   { /* TODO */ }
    private void viewOfficerRegistrations()         { /* TODO */ }
    private void approveOfficerRegistration(Scanner sc)     { /* TODO */ }
    private void approveOfficerWithdrawal(Scanner sc)       { /* TODO */ }
    private void approveApplications(Scanner sc)             { /* TODO */ }
    private void approveApplicationWithdrawal(Scanner sc)   { /* TODO */ }
    private void generateApplicantReportFlow(Scanner sc)    { /* TODO */ }
    private void viewAllEnquiries()                         { /* TODO */ }
    private void handleProjectEnquiriesFlow(Scanner sc)     { /* TODO */ }
    private void viewAccountDetails()                       { /* TODO */ }
}
