package SC2002.Project.boundary;

import java.util.Scanner;

import SC2002.Project.BTOSystem;
import SC2002.Project.boundary.util.Input;
import SC2002.Project.boundary.util.MenuPrinter;

public class ApplicantUI {
    public static void start(Scanner sc) {
        new ApplicantUI().menuLoop(sc);
    }

    private void menuLoop(Scanner sc) {
        boolean quit = false;
        while (!quit) {
            MenuPrinter.printManagerMenu();
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
                case 9 -> quit = true;
                default -> System.out.println("Invalid choice.");
            }
        }
        BTOSystem.MainMenu();
    }
    
    private void applyProject(Scanner sc) { /* TODO */ }
    private void viewActiveApplication() { /* TODO */ }
    private void viewEligibleListings() { /* TODO */ }
    private void viewAllListings() { /* TODO */ }
    private void withdrawApplication(Scanner sc) { /* TODO */ }
    private void manageEnquiry(Scanner sc) { /* TODO */ }
    private void viewAccountDetails() { /* TODO */ }
    private void changePassword(Scanner sc) { /* TODO */ }
}
