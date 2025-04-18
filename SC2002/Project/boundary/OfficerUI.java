package SC2002.Project.boundary;

import SC2002.Project.boundary.util.Input;
import SC2002.Project.boundary.util.MenuPrinter;
import SC2002.Project.control.*;
import SC2002.Project.entity.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;
import java.util.stream.Collectors;

public class OfficerUI {
    private final HDB_Officer user;
    private final AuthController auth = new AuthController();

    private OfficerUI(HDB_Officer user) {
        this.user = user;
    }

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
                    // Applicant Role Features (Cases 1-5, 7-9) - Delegate to ApplicantUI methods
                    case 1 -> ApplicantUI.applyForProject(sc, officer, applicantController, projectController);
                    case 2 -> ApplicantUI.viewActiveApplication(officer);
                    case 3 -> ApplicantUI.viewEligibleListings(officer, applicantController);
                    case 4 -> ApplicantUI.viewAllListings(projectController, applicantController, officer);
                    case 5 -> ApplicantUI.withdrawApplication(sc, officer, applicantController);
                    
                    // Enquiry Management (Cases 6-7)
                    case 6 -> manageUserEnquiries(sc, officer, enquiryController, officerController);
                    case 7 -> EnquiryUI.start(sc, officer); // Manage own enquiries via Applicant role
                    
                    case 8 -> viewAccountDetails(officer);
                    case 9 -> AuthUI.changePassword(sc, officer); // Delegate
                    
                    // Officer Specific Features (Cases 10-14)
                    case 10 -> registerForProject(sc, officer, registrationController, projectController);
                    case 11 -> checkRegistrationStatus(officer, registrationController);
                    case 12 -> viewProjectDetails(sc, projectController);
                    case 13 -> processFlatBooking(sc, officerController);
                    case 14 -> viewAssignedApplications(officerController, applicationController);
                    
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
    
    private static void manageUserEnquiries(Scanner sc, HDB_Officer officer, EnquiryController enquiryCtrl, OfficerController officerCtrl) {
        List<Project> assignedProjects = officerCtrl.getAssignedProjects();
        List<Enquiry> relevantEnquiries = new ArrayList<>();

        // Get enquiries for assigned projects
        for (Project p : assignedProjects) {
            relevantEnquiries.addAll(enquiryCtrl.getProjectEnquiries(p));
        }
        // Get general enquiries (not project-specific)
        relevantEnquiries.addAll(enquiryCtrl.getGeneralEnquiries());

        // Filter out enquiries created by the officer themselves and already answered ones
        List<Enquiry> actionableEnquiries = relevantEnquiries.stream()
                .filter(e -> !e.getCreator().equals(officer))
                .filter(e -> !e.isAnswered())
                .distinct() // Avoid duplicates if an enquiry somehow appears twice
                .collect(Collectors.toList());

        if (actionableEnquiries.isEmpty()) {
            System.out.println("No pending enquiries found for your assigned projects or general topics.");
            return;
        }

        System.out.println("\nPending Enquiries:");
        System.out.println("------------------");
        EnquiryUI.viewEnquiries(sc, officer, actionableEnquiries, false); // Use false to prevent immediate selection prompt

        try {
            System.out.print("Enter Enquiry ID to respond (or -1 to cancel): ");
            int enquiryId = Input.getIntInput(sc);
            if (enquiryId == -1) {
                System.out.println("Cancelled.");
                return;
            }

            Optional<Enquiry> selectedEnquiryOpt = actionableEnquiries.stream()
                    .filter(e -> e.getId() == enquiryId)
                    .findFirst();

            if (selectedEnquiryOpt.isEmpty()) {
                System.out.println("Invalid Enquiry ID or enquiry not actionable by you.");
                return;
            }

            Enquiry selectedEnquiry = selectedEnquiryOpt.get();
            System.out.println("\nSelected Enquiry:");
            EnquiryUI.viewSingleEnquiry(selectedEnquiry); // Display full details

            System.out.print("Enter your response: ");
            String response = Input.getStringInput(sc);

            if (enquiryCtrl.respondToEnquiry(officer, enquiryId, response)) {
                System.out.println("Response submitted successfully!");
            } else {
                System.out.println("Failed to submit response. Please try again.");
            }

        } catch (Input.InputExitException e) {
            System.out.println("Response process cancelled.");
        } catch (Exception e) {
            System.err.println("Error managing enquiries: " + e.getMessage());
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

    private static void checkRegistrationStatus(HDB_Officer officer, RegistrationController regCtrl) {
        List<Registration> officerRegistrations = regCtrl.listForOfficer(officer.getOfficerId());

        System.out.println("\nYour Project Registration Statuses:");
        System.out.println("-----------------------------------");
        if (officerRegistrations.isEmpty()) {
            System.out.println("You have not registered for any projects.");
        } else {
            System.out.printf("%-10s %-20s %-15s%n", "Reg ID", "Project Name", "Status");
            System.out.println("-".repeat(50));
            for (Registration reg : officerRegistrations) {
                System.out.printf("%-10d %-20s %-15s%n",
                                  reg.getId(),
                                  reg.getProject().getName(),
                                  reg.getStatus());
            }
            System.out.println("-".repeat(50));
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
            if (selectedProject == null || !selectedProject.isVisible()) {
                System.out.println("Invalid Project ID or project not visible.");
                return;
            }

            System.out.println("\nProject Details:");
            System.out.println("----------------");
            System.out.println(selectedProject); // Use Project's toString()

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

        System.out.println("\nApplications for Your Assigned Projects:");
        System.out.println("========================================");

        boolean foundAny = false;
        for (Project project : assignedProjects) {
            List<BTOApplication> projectApps = appCtrl.listApplicationsForProject(project.getId());
            if (!projectApps.isEmpty()) {
                foundAny = true;
                System.out.println("\n--- Project: " + project.getName() + " (ID: " + project.getId() + ") ---");
                printApplicationListSimple(projectApps); // Reuse existing helper
            }
        }

        if (!foundAny) {
            System.out.println("No applications found for the projects you are assigned to.");
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
