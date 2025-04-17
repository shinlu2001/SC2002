package SC2002.Project.boundary;

import java.util.Scanner;

import SC2002.Project.BTOSystem;
import SC2002.Project.boundary.util.Input;
import SC2002.Project.boundary.util.MenuPrinter;

public class ManagerUI {
    public static void start(Scanner sc) {
        new ManagerUI().menuLoop(sc);
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
                case 15 -> quit = true;
                default -> System.out.println("Invalid choice.");
            }
        }
        BTOSystem.MainMenu();
    }

    private void createProjectFlow(Scanner sc) { /* TODO */ }
    private void editProjectFlow(Scanner sc) { /* TODO */ }
    private void deleteProjectFlow(Scanner sc) { /* TODO */ }
    private void viewAllProjects() { /* TODO */ }
    private void viewMyProjects() { /* TODO */ }
    private void viewOfficerRegistrations() { /* TODO */ }
    private void approveOfficerRegistration(Scanner sc) { /* TODO */ }
    private void approveOfficerWithdrawal(Scanner sc) { /* TODO */ }
    private void approveApplications(Scanner sc) { /* TODO */ }
    private void approveApplicationWithdrawal(Scanner sc) { /* TODO */ }
    private void generateApplicantReportFlow(Scanner sc) { /* TODO */ }
    private void viewAllEnquiries() { /* TODO */ }
    private void handleProjectEnquiriesFlow(Scanner sc) { /* TODO */ }
    private void viewAccountDetails() { /* TODO */ }
}
