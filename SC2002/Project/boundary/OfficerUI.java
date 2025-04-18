package SC2002.Project.boundary;

import SC2002.Project.boundary.util.Input;
import SC2002.Project.boundary.util.MenuPrinter;
import SC2002.Project.control.AuthController;
import SC2002.Project.control.OfficerController;
import SC2002.Project.entity.*;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;

public class OfficerUI {
    private final HDB_Officer user;
    private final AuthController auth = new AuthController();

    private OfficerUI(HDB_Officer user) {
        this.user = user;
    }

    /** Entry point from LoginUI. */
    public static void start(Scanner sc, HDB_Officer officer) {
        OfficerController officerController = new OfficerController(officer);
        boolean exit = false;

        while (!exit) {
            System.out.println("\nWelcome, Officer " + officer.getFirstName() + "!");
            MenuPrinter.printRoleMenuHeader(1);
            MenuPrinter.printOfficerMenu();
            try {
                int choice = Input.getIntInput(sc);
                switch (choice) {
                    // Applicant Role Features (Cases 1-5, 7-9) - Handled by ApplicantUI logic if needed, or reimplement here
                    case 1 -> System.out.println("Apply for Project (as Applicant) - Not yet implemented here."); // Delegate or reuse ApplicantUI.applyForProject
                    case 2 -> System.out.println("View Active Application - Not yet implemented here."); // Delegate or reuse ApplicantUI.manageActiveApplication
                    case 3 -> System.out.println("View Eligible Listings - Not yet implemented here."); // Delegate or reuse ApplicantUI.viewEligibleListings
                    case 4 -> System.out.println("View All Listings - Not yet implemented here."); // Delegate or reuse ApplicantUI.viewAllListings
                    case 5 -> System.out.println("Withdraw Application - Not yet implemented here."); // Delegate or reuse ApplicantUI.withdrawApplication
                    
                    // Enquiry Management (Cases 6-7) - TODO: Implement later
                    case 6 -> System.out.println("Manage User Enquiries - Not yet implemented."); // manageUserEnquiries(sc, officerController);
                    case 7 -> EnquiryUI.start(sc, officer); // Manage own enquiries via Applicant role
                    
                    case 8 -> viewAccountDetails(officer);
                    case 9 -> AuthUI.changePassword(sc, officer); // Delegate
                    
                    // Officer Specific Features (Cases 10-14)
                    case 10 -> System.out.println("Register for Project - Not yet implemented."); // registerForProject(sc, officerController);
                    case 11 -> System.out.println("Check Registration Status - Not yet implemented."); // checkRegistrationStatus(officerController);
                    case 12 -> System.out.println("View Project Details - Not yet implemented."); // viewProjectDetails(sc, officerController);
                    case 13 -> processFlatBooking(sc, officerController);
                    case 14 -> System.out.println("View Applications for Assigned Project - Not yet implemented."); // viewAssignedApplications(officerController);
                    
                    case 15 -> exit = true;
                    default -> System.out.println("Invalid choice. Please try again.");
                }
            } catch (Input.InputExitException e) {
                System.out.println("Returning to previous menu.");
                exit = true;
            } catch (Exception e) {
                System.err.println("An unexpected error occurred in Officer menu: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    private static void processFlatBooking(Scanner sc, OfficerController controller) {
        List<BTOApplication> bookableApps = controller.getSuccessfulApplicationsForManagedProjects();
        if (bookableApps.isEmpty()) {
            System.out.println("No successful applications found for your managed projects that require booking.");
            return;
        }

        System.out.println("\nApplications Ready for Booking:");
        System.out.println("------------------------------");
        printApplicationListSimple(bookableApps); // Use a simpler list for selection

        try {
            System.out.print("Enter Application ID to process booking: ");
            int appId = Input.getIntInput(sc);

            Optional<BTOApplication> selectedAppOpt = controller.findBookableApplicationById(appId);
            if (selectedAppOpt.isEmpty()) {
                System.out.println("Invalid Application ID or application not eligible for booking.");
                return;
            }
            
            BTOApplication selectedApp = selectedAppOpt.get();
            System.out.println("\nProcessing booking for:");
            System.out.println(selectedApp);
            System.out.println("Required Flat Type: " + selectedApp.getRoomType() + " in Project: " + selectedApp.getProject().getName());

            // Attempt booking
            Receipt receipt = controller.processFlatBooking(selectedApp);

            if (receipt != null) {
                System.out.println("\nBooking Successful! Generating Receipt...");
                System.out.println(receipt.getReceiptDetails());
            } else {
                System.out.println("Booking failed. Please check availability or application status.");
            }

        } catch (Input.InputExitException e) {
            System.out.println("Booking process cancelled.");
        } catch (Exception e) {
            System.err.println("Error processing flat booking: " + e.getMessage());
        }
    }
    
    private static void viewAccountDetails(HDB_Officer officer) {
        System.out.println("\nOfficer Account Details:");
        System.out.println("------------------------");
        System.out.println(officer); // Uses User's toString()
        System.out.println("Officer ID: " + officer.getOfficerId());
        System.out.println("Registrations:");
         if (officer.getRegistrations().isEmpty()) {
            System.out.println("  (None)");
        } else {
            officer.getRegistrations().forEach(reg -> System.out.println("  - " + reg)); // Use Registration toString
        }
        // Also show applicant details if needed
         Optional<BTOApplication> appOpt = officer.getCurrentApplication();
         if(appOpt.isPresent()){
              System.out.println("\nCurrent Applicant Application:");
              System.out.println("  " + appOpt.get());
         }
    }
    
     /**
     * Helper to print a simpler list of applications for selection purposes.
     */
    private static void printApplicationListSimple(List<BTOApplication> applications) {
        if (applications == null || applications.isEmpty()) {
            return; // Already handled usually, but safe check
        }
        System.out.printf("%-10s %-20s %-15s %-15s %-10s%n", 
                          "App ID", "Applicant Name", "Project", "Flat Type", "Status");
        System.out.println("-".repeat(75));
        for (BTOApplication app : applications) {
            Applicant applicant = app.getApplicant();
            System.out.printf("%-10d %-20s %-15s %-15s %-10s%n",
                              app.getId(),
                              applicant.getFirstName() + " " + applicant.getLastName(),
                              app.getProject().getName(),
                              app.getRoomType(),
                              app.getStatus());
        }
         System.out.println("-".repeat(75));
    }

    // TODO: Implement UI methods for registration, viewing details, assigned apps, etc.
}
