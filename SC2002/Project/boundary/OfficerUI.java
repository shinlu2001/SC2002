package SC2002.Project.boundary;

import java.util.Scanner;

import SC2002.Project.BTOSystem;
import SC2002.Project.boundary.util.Input;
import SC2002.Project.boundary.util.MenuPrinter;

public class OfficerUI {
    public static void start(Scanner sc) {
        new OfficerUI().menuLoop(sc);
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
                case 9  -> changePasswordFlow(sc);
                case 10 -> registerForProjectFlow(sc);
                case 11 -> viewRegistrationStatus();
                case 12 -> viewProjectDetails();
                case 13 -> processFlatBookingFlow(sc);
                case 14 -> viewAssignedApplications();
                case 15 -> quit = true;
                default -> System.out.println("Invalid choice.");
            }
        }
        BTOSystem.MainMenu();
    }

    private void applyForProjectFlow(Scanner sc) { /* TODO */ }
    private void viewActiveApplication() { /* TODO */ }
    private void viewEligibleListings() { /* TODO */ }
    private void viewAllListings() { /* TODO */ }
    private void withdrawApplicationFlow(Scanner sc) { /* TODO */ }
    private void handleUserEnquiriesFlow(Scanner sc) { /* TODO */ }
    private void handleOwnEnquiriesFlow(Scanner sc) { /* TODO */ }
    private void viewAccountDetails() { /* TODO */ }
    private void changePasswordFlow(Scanner sc) { /* TODO */ }
    private void registerForProjectFlow(Scanner sc) { /* TODO */ }
    private void viewRegistrationStatus() { /* TODO */ }
    private void viewProjectDetails() { /* TODO */ }
    private void processFlatBookingFlow(Scanner sc) { /* TODO */ }
    private void viewAssignedApplications() { /* TODO */ }
}
