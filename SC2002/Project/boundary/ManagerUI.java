package SC2002.Project.boundary;

import SC2002.Project.boundary.util.Input;
import SC2002.Project.boundary.util.MenuPrinter;
import SC2002.Project.control.AuthController;
import SC2002.Project.control.ManagerController;
import SC2002.Project.entity.*;
import SC2002.Project.entity.enums.ApplicationStatus;
import java.util.List;
import java.util.Scanner;

public class ManagerUI {
    private final HDB_Manager user;
    private final AuthController auth = new AuthController();

    private ManagerUI(HDB_Manager user) {
        this.user = user;
    }

    /** Entry point from LoginUI. */
    public static void start(HDB_Manager user, Scanner sc) {
        ManagerController managerController = new ManagerController(user);
        boolean exit = false;

        while (!exit) {
            System.out.println("\nWelcome, Manager " + user.getFirstName() + "!");
            MenuPrinter.printRoleMenuHeader(2);
            MenuPrinter.printManagerMenu();
            try {
                int choice = Input.getIntInput(sc);
                switch (choice) {
                    // Project Management (Cases 1-5) - TODO: Implement later
                    case 1 -> System.out.println("Project Creation - Not yet implemented."); // createProject(sc, managerController);
                    case 2 -> System.out.println("Project Edit - Not yet implemented."); // editProject(sc, managerController);
                    case 3 -> System.out.println("Project Delete - Not yet implemented."); // deleteProject(sc, managerController);
                    case 4 -> System.out.println("View All Projects - Not yet implemented."); // viewAllProjects(managerController);
                    case 5 -> System.out.println("View My Projects - Not yet implemented."); // viewOwnProjects(managerController);
                    
                    // Officer Registration Management (Cases 6-8) - TODO: Implement later
                    case 6 -> System.out.println("View Officer Registrations - Not yet implemented."); // viewOfficerRegistrations(managerController);
                    case 7 -> System.out.println("Handle Officer Registration - Not yet implemented."); // handleOfficerRegistration(sc, managerController);
                    case 8 -> System.out.println("Handle Officer Withdrawal - Not yet implemented."); // handleOfficerWithdrawal(sc, managerController);
                    
                    // BTO Application Management (Cases 9-10) - Implementing now
                    case 9 -> handleBTOApplications(sc, managerController);
                    case 10 -> handleBTOWithdrawals(sc, managerController);
                    
                    // Report & Enquiry (Cases 11-13) - TODO: Implement later
                    case 11 -> ReportUI.start(sc, user); // Delegate
                    case 12 -> System.out.println("View All Enquiries - Not yet implemented."); // viewAllEnquiries(sc, managerController); // Needs EnquiryController integration
                    case 13 -> System.out.println("Handle Project Enquiries - Not yet implemented."); // handleProjectEnquiries(sc, managerController); // Needs EnquiryController integration
                    
                    case 14 -> viewAccountDetails(user);
                    case 15 -> AuthUI.changePassword(sc, user);
                    case 16 -> exit = true;
                    default -> System.out.println("Invalid choice. Please try again.");
                }
            } catch (Input.InputExitException e) {
                System.out.println("Returning to previous menu.");
                exit = true;
            } catch (Exception e) {
                System.err.println("An unexpected error occurred in Manager menu: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    private static void handleBTOApplications(Scanner sc, ManagerController controller) {
        List<BTOApplication> pendingApps = controller.getPendingApplications();
        if (pendingApps.isEmpty()) {
            System.out.println("No pending BTO applications to handle.");
            return;
        }

        System.out.println("\nPending BTO Applications:");
        System.out.println("-------------------------");
        printApplicationList(pendingApps);

        try {
            System.out.print("Enter Application ID to approve/reject: ");
            int appId = Input.getIntInput(sc);

            BTOApplication selectedApp = controller.findManagedApplicationById(appId);
            if (selectedApp == null || selectedApp.getStatus() != ApplicationStatus.PENDING) {
                System.out.println("Invalid Application ID or application is not pending.");
                return;
            }
            
            System.out.println("\nSelected Application:");
            System.out.println(selectedApp);
            System.out.print("Approve or Reject? (approve/reject): ");
            String decision = Input.getStringInput(sc).toLowerCase();

            if (decision.equals("approve")) {
                controller.approveApplication(selectedApp);
            } else if (decision.equals("reject")) {
                controller.rejectApplication(selectedApp);
            } else {
                System.out.println("Invalid decision. No action taken.");
            }

        } catch (Input.InputExitException e) {
            System.out.println("Handling cancelled.");
        } catch (Exception e) {
            System.err.println("Error handling BTO application: " + e.getMessage());
        }
    }

    private static void handleBTOWithdrawals(Scanner sc, ManagerController controller) {
        List<BTOApplication> withdrawalRequests = controller.getWithdrawalRequests();
        if (withdrawalRequests.isEmpty()) {
            System.out.println("No pending BTO withdrawal requests.");
            return;
        }

        System.out.println("\nPending BTO Withdrawal Requests:");
        System.out.println("-------------------------------");
        printApplicationList(withdrawalRequests);

        try {
            System.out.print("Enter Application ID to confirm/reject withdrawal: ");
            int appId = Input.getIntInput(sc);
            
            BTOApplication selectedApp = controller.findManagedApplicationById(appId);
             if (selectedApp == null || !selectedApp.isWithdrawalRequested()) {
                System.out.println("Invalid Application ID or no withdrawal requested.");
                return;
            }

            System.out.println("\nSelected Application for Withdrawal:");
            System.out.println(selectedApp);
            System.out.print("Confirm or Reject Withdrawal? (confirm/reject): ");
            String decision = Input.getStringInput(sc).toLowerCase();

            if (decision.equals("confirm")) {
                 if (selectedApp.getStatus() == ApplicationStatus.BOOKED){
                     System.out.println("Error: Cannot confirm withdrawal for a BOOKED application. Officer must cancel booking first.");
                 } else {
                     controller.confirmWithdrawal(selectedApp);
                 }
            } else if (decision.equals("reject")) {
                controller.rejectWithdrawalRequest(selectedApp);
            } else {
                System.out.println("Invalid decision. No action taken.");
            }

        } catch (Input.InputExitException e) {
            System.out.println("Handling cancelled.");
        } catch (Exception e) {
            System.err.println("Error handling withdrawal request: " + e.getMessage());
        }
    }

    private static void viewAccountDetails(HDB_Manager manager) {
        System.out.println("\nManager Account Details:");
        System.out.println("------------------------");
        System.out.println(manager); // Uses User's toString()
        System.out.println("Managed Projects:");
        if (manager.getManagedProjects().isEmpty()) {
            System.out.println("  (None)");
        } else {
            manager.getManagedProjects().forEach(p -> System.out.println("  - " + p.getName() + " (ID: " + p.getId() + ")"));
        }
    }
    
     /**
     * Helper to print a list of BTO applications.
     */
    private static void printApplicationList(List<BTOApplication> applications) {
        if (applications == null || applications.isEmpty()) {
            System.out.println("No applications to display.");
            return;
        }
        System.out.printf("%-10s %-20s %-15s %-15s %-10s %s%n", 
                          "App ID", "Applicant Name", "Project", "Flat Type", "Status", "Details");
        System.out.println("-".repeat(90));
        for (BTOApplication app : applications) {
            Applicant applicant = app.getApplicant();
            System.out.printf("%-10d %-20s %-15s %-15s %-10s %s%n",
                              app.getId(),
                              applicant.getFirstName() + " " + applicant.getLastName(),
                              app.getProject().getName(),
                              app.getRoomType(),
                              app.getStatus(),
                              (app.isWithdrawalRequested() ? "(Withdrawal Req)" : "") +
                              (app.getBookedFlat() != null ? "(Booked Flat: " + app.getBookedFlat().getId() + ")" : ""));
        }
         System.out.println("-".repeat(90));
    }

    // TODO: Implement UI methods for project management, officer registration, etc.
}
