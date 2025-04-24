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

public class ApplicantUI {

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

    public static void viewActiveApplication(Scanner sc, Applicant applicant) {
        Optional<BTOApplication> oa = applicant.getCurrentApplication();
        if (oa.isEmpty()) {
            if (applicant.getApplicationHistory().isEmpty()) {
            System.out.println("You have no active application.");

            } else {
                System.out.println("Your most recent application was withdrawn/rejected. You may create a new application.");
                System.out.println(applicant.getApplicationHistory().getLast());
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

    public static void viewAccountDetails(Applicant applicant) {
        System.out.println("\nAccount Details:");
        System.out.println(applicant);
    }
}
