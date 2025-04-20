package SC2002.Project.boundary;

import SC2002.Project.boundary.util.Input;
import SC2002.Project.boundary.util.MenuPrinter;
import SC2002.Project.control.*;
import SC2002.Project.entity.*;
import SC2002.Project.entity.enums.RegistrationStatus;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;
import java.util.stream.Collectors;

public class OfficerUI {

    /** Entry point from LoginUI. */
    public static void start(Scanner sc, HDB_Officer officer) {
        OfficerController officerController = new OfficerController(officer);
        ApplicantController applicantController = new ApplicantController(officer);
        ProjectController projectController = new ProjectController();
        RegistrationController registrationController = new RegistrationController();
        EnquiryController enquiryController = new EnquiryController();
        ApplicationController applicationController = new ApplicationController();
        boolean exit = false;

        while (!exit) {
            System.out.println("\nWelcome, Officer " + officer.getFirstName() + "!");
            MenuPrinter.printRoleMenuHeader(1);
            MenuPrinter.printOfficerMenu();
            try {
                int choice = Input.getIntInput(sc);
                switch (choice) {
                    // BTO Application (Applicant Role)
                    case 1 -> ApplicantUI.applyForProject(sc, officer, applicantController, projectController);
                    case 2 -> ApplicantUI.viewActiveApplication(officer);
                    case 3 -> ApplicantUI.viewEligibleListings(officer, applicantController);
                    case 4 -> ApplicantUI.viewAllListings(projectController, applicantController, officer);
                    case 5 -> ApplicantUI.withdrawApplication(sc, officer, applicantController);
                    
                    // Enquiry Management
                    case 6 -> EnquiryUI.viewEnquiriesStaff(sc, enquiryController, officerController);
                    case 7 -> StaffUI.manageUserEnquiries(sc, officer, enquiryController, officerController);
                    case 8 -> EnquiryUI.start(sc, officer); // Manage own enquiries via Applicant role
                    
                    // Officer Specific Features
                    case 9 -> registerForProject(sc, officer, registrationController, projectController);
                    case 10 -> checkRegistrationStatus(sc, officer, registrationController);
                    case 11 -> viewProjectDetails(sc, projectController);
                    case 12 -> processFlatBooking(sc, officerController);
                    case 13 -> viewAssignedApplications(officerController, applicationController);
                    
                    // Account
                    case 14 -> viewAccountDetails(officer);
                    case 15 -> AuthUI.changePassword(sc, officer);
                    case 0 -> exit = true;
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
            
            // Check flat availability first
            int availableUnits = selectedApp.getProject().getRemainingUnits(selectedApp.getRoomType());
            if (availableUnits <= 0) {
                System.out.println("Error: No available units of type " + selectedApp.getRoomType() + 
                             " in project " + selectedApp.getProject().getName());
                return;
            }
            
            // Ask for confirmation instead of re-entering room type
            System.out.print("Type 'confirm' to proceed with booking or 'back' to cancel: ");
            String confirmation = Input.getStringInput(sc);
            
            if (!confirmation.equalsIgnoreCase("confirm")) {
                System.out.println("Booking cancelled.");
                return;
            }

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

    private static void registerForProject(Scanner sc, HDB_Officer officer, RegistrationController regCtrl, ProjectController projCtrl) {
        List<Project> allProjects = projCtrl.listAll().stream()
                                        .filter(Project::isVisible) // Only show visible projects
                                        .collect(Collectors.toList());

        if (allProjects.isEmpty()) {
            System.out.println("No projects currently available for registration.");
            return;
        }

        System.out.println("\nAvailable Projects for Registration:");
        MenuPrinter.printProjectTableSimple(allProjects); // Use a simple printer

        try {
            System.out.print("Enter Project ID to register for: ");
            int projectId = Input.getIntInput(sc);

            Project selectedProject = projCtrl.findById(projectId);
            if (selectedProject == null || !selectedProject.isVisible()) {
                System.out.println("Invalid Project ID or project not available.");
                return;
            }

            // Attempt registration (controller handles checks like duplicates, overlaps etc.)
            Registration registration = regCtrl.register(officer, projectId);

            if (registration != null) {
                System.out.println("Registration request submitted for Project: " + selectedProject.getName());
                System.out.println("Status: " + registration.getStatus());
            } else {
                System.out.println("Registration failed. Please check eligibility or existing registrations.");
            }

        } catch (Input.InputExitException e) {
            System.out.println("Registration process cancelled.");
        } catch (Exception e) {
            System.err.println("Error during project registration: " + e.getMessage());
        }
    }

    private static void checkRegistrationStatus(Scanner sc, HDB_Officer officer, RegistrationController regCtrl) {
        List<Registration> officerRegistrations = regCtrl.listForOfficer(officer.getOfficerId());

        if (officerRegistrations.isEmpty()) {
            System.out.println("\nYou have not registered for any projects.");
            return;
        }

        System.out.println("\nYour Project Registration Statuses:");
        System.out.println("-----------------------------------");
        System.out.printf("%-10s %-" + Menu.COL_NAME + "s %-20s%n", "Reg ID", "Project Name", "Status");
        System.out.println("-".repeat(72)); // Updated separator length to match column widths
        for (Registration reg : officerRegistrations) {
            String statusDisplay = reg.getStatus().toString();
            // Only show "Withdrawal Requested" for PENDING, not for already WITHDRAWN ones
            if (reg.isWithdrawalRequested() && reg.getStatus() != RegistrationStatus.WITHDRAWN) {
                statusDisplay += " (Withdrawal Requested)";
            }
            
            // Truncate project name if too long
            String projectName = Input.truncateText(reg.getProject().getName(), Menu.COL_NAME - 2);
            
            System.out.printf("%-10d %-" + Menu.COL_NAME + "s %-20s%n",
                              reg.getId(),
                              projectName,
                              statusDisplay);
        }
        System.out.println("-".repeat(72)); // Updated separator length
        
        // Add option to request withdrawal for registrations
        try {
            System.out.print("Enter Registration ID to request withdrawal (or type 'back' to return): ");
            String input = sc.nextLine().trim();
            
            if (input.equalsIgnoreCase("back")) {
                return;
            }
            
            try {
                int regId = Integer.parseInt(input);
                
                Registration selectedReg = officerRegistrations.stream()
                    .filter(reg -> reg.getId() == regId)
                    .findFirst()
                    .orElse(null);
                    
                if (selectedReg == null) {
                    System.out.println("Invalid Registration ID or not your registration.");
                    return;
                }
                
                // Check if registration is already withdrawn
                if (selectedReg.getStatus() == RegistrationStatus.WITHDRAWN) {
                    System.out.println("Registration already withdrawn. No further action needed.");
                    return;
                }
                
                // Check if already processing a withdrawal request
                if (selectedReg.isWithdrawalRequested()) {
                    System.out.println("Withdrawal already requested. Awaiting manager approval.");
                    return;
                }
                
                // Check if registration is APPROVED - cannot withdraw if APPROVED
                if (selectedReg.getStatus() == RegistrationStatus.APPROVED) {
                    System.out.println("Cannot withdraw from an APPROVED project registration.");
                    System.out.println("Once a registration is approved, you cannot withdraw from the project.");
                    return;
                }
                
                // Check if registration is in a state that can't be withdrawn
                if (selectedReg.getStatus() != RegistrationStatus.PENDING) {
                    System.out.println("Cannot request withdrawal for a registration with status: " + selectedReg.getStatus());
                    return;
                }
                
                System.out.println("\nSelected Registration:");
                System.out.println("Project: " + selectedReg.getProject().getName());
                System.out.println("Status: " + selectedReg.getStatus());
                
                System.out.print("Are you sure you want to request withdrawal of this registration? (yes/no): ");
                String confirmation = Input.getStringInput(sc).toLowerCase();
                
                if (confirmation.equals("yes")) {
                    boolean success = regCtrl.requestWithdrawalForApproval(regId, officer);
                    if (success) {
                        System.out.println("Withdrawal request submitted for manager approval.");
                    } else {
                        System.out.println("Failed to request withdrawal. The registration may not be in a valid state.");
                    }
                } else {
                    System.out.println("Withdrawal request cancelled.");
                }
            } catch (NumberFormatException e) {
                System.out.println("Please enter a valid registration ID number.");
            }
            
        } catch (Exception e) {
            System.err.println("Error during registration withdrawal request: " + e.getMessage());
        }
    }

    private static void viewProjectDetails(Scanner sc, ProjectController projCtrl) {
        List<Project> allProjects = projCtrl.listAll().stream()
                                        .filter(Project::isVisible)
                                        .collect(Collectors.toList());

        if (allProjects.isEmpty()) {
            System.out.println("No projects available to view.");
            return;
        }

        System.out.println("\nAll Visible Projects:");
        MenuPrinter.printProjectTableSimple(allProjects);

        try {
            System.out.print("Enter Project ID to view details: ");
            int projectId = Input.getIntInput(sc);
            
            Project selectedProject = projCtrl.findById(projectId);
            List<Project> toPrint = new ArrayList<>();
            if (selectedProject == null || !selectedProject.isVisible()) {
                System.out.println("Invalid Project ID or project not visible.");
                return;
            }
            toPrint.add(selectedProject);
            System.out.println("\nProject Details:");
            System.out.println("----------------");
            MenuPrinter.printProjectTableDetailed(toPrint); // Use detailed view from menu printer (like manager)

        } catch (Input.InputExitException e) {
            System.out.println("Viewing cancelled.");
        } catch (Exception e) {
            System.err.println("Error viewing project details: " + e.getMessage());
        }
    }

    private static void viewAssignedApplications(OfficerController officerCtrl, ApplicationController appCtrl) {
        List<Project> assignedProjects = officerCtrl.getAssignedProjects();

        if (assignedProjects.isEmpty()) {
            System.out.println("You are not currently assigned to manage any projects.");
            return;
        }

        System.out.println("\nYour Assigned Projects and Applications:");
        System.out.println("======================================");

        boolean hasBookableApplications = false;

        for (Project project : assignedProjects) {
            System.out.println("\n--- Project: " + project.getName() + " (ID: " + project.getId() + ") ---");
            
            // Display basic project information
            System.out.println("Neighborhood: " + project.getNeighbourhood());
            System.out.println("Application Period: " + project.getOpenDate() + " to " + project.getCloseDate());
            
            // Display flat types available
            System.out.println("Flat Types:");
            for (String flatType : project.getFlatTypes()) {
                int totalUnits = project.getTotalUnits(flatType);
                int remainingUnits = project.getRemainingUnits(flatType);
                double price = project.getFlatTypePrice(flatType);
                System.out.printf("  %s: %d/%d units available (%.2f SGD)\n", 
                                  flatType, remainingUnits, totalUnits, price);
            }
            
            // Show applications for this project
            List<BTOApplication> projectApps = appCtrl.listApplicationsForProject(project.getId());
            
            // Count bookable applications
            List<BTOApplication> bookableApps = projectApps.stream()
                .filter(app -> officerCtrl.isApplicationBookable(app))
                .collect(Collectors.toList());
                
            int bookableCount = bookableApps.size();
            
            if (!projectApps.isEmpty()) {
                System.out.println("\nApplications: " + projectApps.size() + " total, " + 
                                  bookableCount + " waiting for booking");
                
                if (bookableCount > 0) {
                    hasBookableApplications = true;
                    System.out.println("\nApplications Ready for Booking:");
                    printApplicationListSimple(bookableApps);
                }
            } else {
                System.out.println("\nNo applications for this project.");
            }
            
            System.out.println("-".repeat(75));
        }

        if (hasBookableApplications) {
            try {
                System.out.print("\nEnter Application ID to process booking (or type 'back' to return): ");
                String input = new Scanner(System.in).nextLine().trim();
                
                if (input.equalsIgnoreCase("back")) {
                    return;
                }
                
                try {
                    int appId = Integer.parseInt(input);
                    processFlatBookingById(appId, officerCtrl);
                } catch (NumberFormatException e) {
                    System.out.println("Please enter a valid application ID number.");
                }
            } catch (Input.InputExitException e) {
                System.out.println("Operation cancelled.");
            }
        }
    }
    
    private static void processFlatBookingById(int appId, OfficerController controller) {
        Optional<BTOApplication> selectedAppOpt = controller.findBookableApplicationById(appId);
        if (selectedAppOpt.isEmpty()) {
            System.out.println("Invalid Application ID or application not eligible for booking.");
            return;
        }
        
        BTOApplication selectedApp = selectedAppOpt.get();
        System.out.println("\nProcessing booking for:");
        System.out.println(selectedApp);
        System.out.println("Required Flat Type: " + selectedApp.getRoomType() + " in Project: " + selectedApp.getProject().getName());
        
        // Check flat availability first
        int availableUnits = selectedApp.getProject().getRemainingUnits(selectedApp.getRoomType());
        if (availableUnits <= 0) {
            System.out.println("Error: No available units of type " + selectedApp.getRoomType() + 
                         " in project " + selectedApp.getProject().getName());
            return;
        }
        
        // Ask for confirmation instead of re-entering room type
        System.out.print("Type 'confirm' to proceed with booking or 'back' to cancel: ");
        String confirmation = new Scanner(System.in).nextLine().trim();
        
        if (!confirmation.equalsIgnoreCase("confirm")) {
            System.out.println("Booking cancelled.");
            return;
        }

        // Attempt booking
        Receipt receipt = controller.processFlatBooking(selectedApp);

        if (receipt != null) {
            System.out.println("\nBooking Successful! Generating Receipt...");
            System.out.println(receipt.getReceiptDetails());
        } else {
            System.out.println("Booking failed. Please check availability or application status.");
        }
    }

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
}
