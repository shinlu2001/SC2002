package SC2002.Project.boundary;

import SC2002.Project.boundary.util.Input;
import SC2002.Project.boundary.util.MenuPrinter;
import SC2002.Project.control.ApplicantController;
import SC2002.Project.control.ProjectController;
import SC2002.Project.entity.Applicant;
import SC2002.Project.entity.BTOApplication;
import SC2002.Project.entity.Project;
import SC2002.Project.entity.enums.ApplicationStatus;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;

/**
 * Boundary class handling all user interface operations for Applicants.
 * <p>
 * This class manages the interaction between Applicant users and the BTO Management System,
 * providing a command-line interface for applicants to:
 * <ul>
 *   <li>Apply for BTO projects based on eligibility</li>
 *   <li>View and manage their active applications</li>
 *   <li>Browse and filter available BTO listings</li>
 *   <li>Request withdrawal from applications</li>
 *   <li>Submit and manage enquiries</li>
 *   <li>View and update account details</li>
 * </ul>
 * </p>
 * 
 * @author Group 1
 * @version 1.0
 * @since 2025-04-24
 */
public class ApplicantUI {

    /**
     * Entry point for applicant functionality, displaying the main menu and handling user choices.
     * 
     * @param sc        The Scanner object for user input
     * @param applicant The authenticated Applicant user
     */
    public static void start(Scanner sc, Applicant applicant) {
        ApplicantController applicantController = new ApplicantController(applicant);
        ProjectController projectController = new ProjectController();
        boolean exit = false;

        while (!exit) {
            System.out.println("\nWelcome, " + applicant.getFirstName() + "!");
            MenuPrinter.printRoleMenuHeader(0);
            MenuPrinter.printApplicantMenu();
            try {
                int choice = Input.getIntInput(sc);
                switch (choice) {
                    // BTO Application
                    case 1 -> applyForProject(sc, applicant, applicantController, projectController);
                    case 2 -> viewActiveApplication(sc, applicant);
                    case 3 -> viewEligibleListings(sc, applicant, applicantController);
                    case 4 -> viewAllListings(sc, projectController, applicantController, applicant);
                    case 5 -> withdrawApplication(sc, applicant, applicantController);

                    // Enquiries
                    case 6 -> EnquiryUI.start(sc, applicant);

                    // Account
                    case 7 -> viewAccountDetails(applicant);
                    case 8 -> {
                        if (AuthUI.changePassword(sc, applicant)) {
                            exit = true;
                        }
                    }
                    case 0 -> exit = true;
                    default -> System.out.println("Invalid choice. Please try again.");
                }
            } catch (Input.InputExitException e) {
                System.out.println("Returning to previous menu.");
                exit = true;
            }
        }
    }

    /**
     * Handles the BTO application process for an applicant.
     * <p>
     * This method guides the applicant through selecting an eligible project
     * and flat type, then creates a new application if all criteria are met.
     * It prevents applicants from applying for multiple projects simultaneously.
     * </p>
     * 
     * @param sc        The Scanner object for user input
     * @param applicant The Applicant user submitting the application
     * @param ctrl      The ApplicantController handling business logic
     * @param projCtrl  The ProjectController providing project data
     */
    public static void applyForProject(Scanner sc,
            Applicant applicant,
            ApplicantController ctrl,
            ProjectController projCtrl) {
        List<Project> eligible = ctrl.listEligibleProjects();
        if (eligible.isEmpty()) {
            System.out.println("You are not eligible for any current projects.");
            return;
        }

        if (ctrl.hasActiveApplication()) {
            return;
        }

        System.out.println("\nEligible Projects for Application:");
        MenuPrinter.printProjectTableEligible(eligible, applicant, ctrl);

        try {
            System.out.print("Project ID: ");
            int pid = Input.getIntInput(sc);
            Project p = projCtrl.findById(pid);
            if (p == null || !eligible.contains(p)) {
                System.out.println("Invalid Project ID or not eligible.");
                return;
            }

            List<String> types = p.getFlatTypes().stream()
                    .filter(ctrl::isEligibleForRoomType)
                    .toList();
            System.out.println("\nChoose flat type:");
            types.forEach(t -> System.out.println(" - " + t));

            String ft = Input.getStringInput(sc).toUpperCase();
            if (!types.contains(ft)) {
                System.out.println("Invalid flat type.");
                return;
            }
            ctrl.createApplication(p, ft);
        } catch (Input.InputExitException e) {
            System.out.println("Cancelled.");
        }
    }

    /**
     * Displays details of the applicant's current active application.
     * <p>
     * Shows application status, project details, and any pending actions.
     * Provides appropriate messages if no active application exists or 
     * if application is in a specific state (e.g., successful, withdrawal requested).
     * </p>
     * 
     * @param sc        The Scanner object for user input
     * @param applicant The Applicant user whose application is being viewed
     */
    public static void viewActiveApplication(Scanner sc, Applicant applicant) {
        Optional<BTOApplication> oa = applicant.getCurrentApplication();
        if (oa.isEmpty()) {
            if (applicant.getApplicationHistory().isEmpty()) {
            System.out.println("You have no active application.");

            } else {
                System.out.println("Your most recent application was withdrawn/rejected. You may create a new application.");
                System.out.println(applicant.getApplicationHistory().get(applicant.getApplicationHistory().size() - 1));
            }
            return;
        }
        BTOApplication app = oa.get();
        System.out.println("\nCurrent Application:");
        System.out.println(app);
        if (app.getStatus() == ApplicationStatus.SUCCESS) {
            System.out.println("Your application succeeded! An officer will be in touch.");
        } else if (app.isWithdrawalRequested()) {
            System.out.println("Withdrawal is pending approval.");
        } 
    }

    /**
     * Displays and filters BTO projects that the applicant is eligible to apply for.
     * <p>
     * Provides filtering options by neighborhood and room type to help applicants
     * find suitable projects. Eligibility is determined based on applicant's marital
     * status and age.
     * </p>
     * 
     * @param sc        The Scanner object for user input
     * @param applicant The Applicant user viewing eligible projects
     * @param ctrl      The ApplicantController determining eligibility
     */
    protected static void viewEligibleListings(Scanner sc, Applicant applicant,
            ApplicantController ctrl) {
        List<Project> eligible = ctrl.listEligibleProjects();
        if (eligible.isEmpty()) {
            System.out.println("No eligible projects.");
            return;
        }
        boolean exit = false;

        while (!exit) {
            System.out.println("\nEligible Projects Menu:");
            System.out.println("1. View All Eligible Projects");
            System.out.println("2. Filter by Neighbourhood");
            System.out.println("3. Filter by Room Type");
            System.out.println("0. Back to Main Menu");
            System.out.print("Enter choice: ");

            try {
                int choice = Input.getIntInput(sc);
                switch (choice) {
                    case 1 -> {
                        System.out.println("\nAll Eligible Projects:");
                        MenuPrinter.printProjectTableEligible(eligible, applicant, ctrl);
                    }
                    case 2 -> {
                        // Get all unique neighbourhoods from eligible projects
                        List<String> neighbourhoods = eligible.stream()
                                .map(Project::getNeighbourhood)
                                .distinct()
                                .sorted()
                                .toList();

                        if (neighbourhoods.isEmpty()) {
                            System.out.println("No neighbourhoods available.");
                            break;
                        }

                        System.out.println("\nAvailable Neighbourhoods:");
                        for (int i = 0; i < neighbourhoods.size(); i++) {
                            System.out.println((i + 1) + ". " + neighbourhoods.get(i));
                        }

                        System.out.print("Select neighbourhood (1-" + neighbourhoods.size() + "): ");
                        int index = Input.getIntInput(sc) - 1;

                        if (index >= 0 && index < neighbourhoods.size()) {
                            String selectedNeighbourhood = neighbourhoods.get(index);
                            List<Project> filteredProjects = eligible.stream()
                                    .filter(p -> p.getNeighbourhood().equals(selectedNeighbourhood))
                                    .toList();

                            System.out.println("\nEligible Projects in " + selectedNeighbourhood + ":");
                            if (filteredProjects.isEmpty()) {
                                System.out.println("No eligible projects in this neighbourhood.");
                            } else {
                                MenuPrinter.printProjectTableEligible(filteredProjects, applicant, ctrl);
                            }
                        } else {
                            System.out.println("Invalid selection.");
                        }
                    }
                    case 3 -> {
                        // Get all unique flat types from eligible projects
                        List<String> allFlatTypes = new java.util.ArrayList<>();
                        for (Project p : eligible) {
                            for (String flatType : p.getFlatTypes()) {
                                if (ctrl.isEligibleForRoomType(flatType) && !allFlatTypes.contains(flatType)) {
                                    allFlatTypes.add(flatType);
                                }
                            }
                        }

                        if (allFlatTypes.isEmpty()) {
                            System.out.println("No eligible room types available.");
                            break;
                        }

                        java.util.Collections.sort(allFlatTypes);

                        System.out.println("\nAvailable Room Types:");
                        for (int i = 0; i < allFlatTypes.size(); i++) {
                            System.out.println((i + 1) + ". " + allFlatTypes.get(i));
                        }

                        System.out.print("Select room type (1-" + allFlatTypes.size() + "): ");
                        int index = Input.getIntInput(sc) - 1;

                        if (index >= 0 && index < allFlatTypes.size()) {
                            String selectedRoomType = allFlatTypes.get(index);
                            List<Project> filteredProjects = eligible.stream()
                                    .filter(p -> p.getFlatTypes().contains(selectedRoomType))
                                    .toList();

                            System.out.println("\nEligible Projects with " + selectedRoomType + ":");
                            if (filteredProjects.isEmpty()) {
                                System.out.println("No eligible projects with this room type.");
                            } else {
                                MenuPrinter.printProjectTableEligible(filteredProjects, applicant, ctrl);
                            }
                        } else {
                            System.out.println("Invalid selection.");
                        }
                    }
                    case 0 -> exit = true;
                    default -> System.out.println("Invalid choice. Please try again.");
                }
            } catch (Input.InputExitException e) {
                System.out.println("Returning to previous menu.");
                exit = true;
            }
        }
    }

    /**
     * Displays and filters all visible BTO projects, regardless of eligibility.
     * <p>
     * Provides comprehensive project browsing with filtering options by neighborhood
     * and room type. Shows all projects with visibility set to ON.
     * </p>
     * 
     * @param sc        The Scanner object for user input
     * @param projCtrl  The ProjectController providing project data
     * @param ctrl      The ApplicantController for eligibility checks
     * @param applicant The Applicant user viewing the projects
     */
    protected static void viewAllListings(Scanner sc, ProjectController projCtrl,
            ApplicantController ctrl,
            Applicant applicant) {
        List<Project> all = projCtrl.listAll()
                .stream()
                .filter(Project::isVisible)
                .toList();
        if (all.isEmpty()) {
            System.out.println("No projects available.");
            return;
        }

        boolean exit = false;

        while (!exit) {
            System.out.println("\nAll Projects Menu:");
            System.out.println("1. View All Projects");
            System.out.println("2. Filter by Neighbourhood");
            System.out.println("3. Filter by Room Type");
            System.out.println("0. Back to Main Menu");
            System.out.print("Enter choice: ");

            try {
                int choice = Input.getIntInput(sc);
                switch (choice) {
                    case 1 -> {
                        System.out.println("\nAll Projects:");
                        MenuPrinter.printProjectTableAll(all, applicant, ctrl);
                    }
                    case 2 -> {
                        // Get all unique neighbourhoods from available projects
                        List<String> neighbourhoods = all.stream()
                                .map(Project::getNeighbourhood)
                                .distinct()
                                .sorted()
                                .toList();

                        if (neighbourhoods.isEmpty()) {
                            System.out.println("No neighbourhoods available.");
                            break;
                        }

                        System.out.println("\nAvailable Neighbourhoods:");
                        for (int i = 0; i < neighbourhoods.size(); i++) {
                            System.out.println((i + 1) + ". " + neighbourhoods.get(i));
                        }

                        System.out.print("Select neighbourhood (1-" + neighbourhoods.size() + "): ");
                        int index = Input.getIntInput(sc) - 1;

                        if (index >= 0 && index < neighbourhoods.size()) {
                            String selectedNeighbourhood = neighbourhoods.get(index);
                            List<Project> filteredProjects = all.stream()
                                    .filter(p -> p.getNeighbourhood().equals(selectedNeighbourhood))
                                    .toList();

                            System.out.println("\nProjects in " + selectedNeighbourhood + ":");
                            if (filteredProjects.isEmpty()) {
                                System.out.println("No projects in this neighbourhood.");
                            } else {
                                MenuPrinter.printProjectTableAll(filteredProjects, applicant, ctrl);
                            }
                        } else {
                            System.out.println("Invalid selection.");
                        }
                    }
                    case 3 -> {
                        // Get all unique flat types from available projects
                        List<String> allFlatTypes = new java.util.ArrayList<>();
                        for (Project p : all) {
                            for (String flatType : p.getFlatTypes()) {
                                if (!allFlatTypes.contains(flatType)) {
                                    allFlatTypes.add(flatType);
                                }
                            }
                        }

                        if (allFlatTypes.isEmpty()) {
                            System.out.println("No room types available.");
                            break;
                        }

                        java.util.Collections.sort(allFlatTypes);

                        System.out.println("\nAvailable Room Types:");
                        for (int i = 0; i < allFlatTypes.size(); i++) {
                            System.out.println((i + 1) + ". " + allFlatTypes.get(i));
                        }

                        System.out.print("Select room type (1-" + allFlatTypes.size() + "): ");
                        int index = Input.getIntInput(sc) - 1;

                        if (index >= 0 && index < allFlatTypes.size()) {
                            String selectedRoomType = allFlatTypes.get(index);
                            List<Project> filteredProjects = all.stream()
                                    .filter(p -> p.getFlatTypes().contains(selectedRoomType))
                                    .toList();

                            System.out.println("\nProjects with " + selectedRoomType + ":");
                            if (filteredProjects.isEmpty()) {
                                System.out.println("No projects with this room type.");
                            } else {
                                MenuPrinter.printProjectTableAll(filteredProjects, applicant, ctrl);
                            }
                        } else {
                            System.out.println("Invalid selection.");
                        }
                    }
                    case 0 -> exit = true;
                    default -> System.out.println("Invalid choice. Please try again.");
                }
            } catch (Input.InputExitException e) {
                System.out.println("Returning to previous menu.");
                exit = true;
            }
        }
    }

    /**
     * Processes a withdrawal request for an applicant's current application.
     * <p>
     * Requires NRIC confirmation for security before processing the withdrawal.
     * The actual withdrawal processing is delegated to the controller.
     * </p>
     * 
     * @param sc        The Scanner object for user input
     * @param applicant The Applicant requesting withdrawal
     * @param ctrl      The ApplicantController handling the withdrawal process
     */
    public static void withdrawApplication(Scanner sc,
            Applicant applicant,
            ApplicantController ctrl) {
        Optional<BTOApplication> oa = applicant.getCurrentApplication();
        if (oa.isEmpty()) {
            System.out.println("No application to withdraw.");
            return;
        }
        BTOApplication app = oa.get();
        System.out.println(app);

        try {
            System.out.print("Confirm by entering your NRIC: ");
            String n = Input.getStringInput(sc);
            if (n.equalsIgnoreCase(applicant.getNric())) {
                ctrl.requestWithdrawal();
            } else {
                System.out.println("NRIC mismatch.");
            }
        } catch (Input.InputExitException e) {
            System.out.println("Cancelled.");
        }
    }

    /**
     * Displays complete account details for the applicant.
     * <p>
     * Shows personal information and receipt details if a booking has been made.
     * </p>
     * 
     * @param applicant The Applicant whose details are being displayed
     */
    public static void viewAccountDetails(Applicant applicant) {
        System.out.println("\nAccount Details:");
        System.out.println(applicant);
        if (applicant.getReceipt()!=null) {
            System.out.println(applicant.getReceipt().getReceiptDetails());
        }
    }
}
