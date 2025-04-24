package SC2002.Project.boundary;

import SC2002.Project.boundary.util.Input;
import SC2002.Project.boundary.util.MenuPrinter;
import SC2002.Project.control.EnquiryController;
import SC2002.Project.control.ManagerController;
import SC2002.Project.entity.*;
import SC2002.Project.entity.enums.ApplicationStatus;
import SC2002.Project.entity.enums.Visibility;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.InputMismatchException;
import java.util.List;
import java.util.Scanner;

public class ManagerUI {
    private final HDB_Manager manager; // Renamed from 'user' since we're accessing it

    private ManagerUI(HDB_Manager manager) {
        this.manager = manager; // Updated name
    }

    /** Entry point from LoginUI. */
    public static void start(HDB_Manager user, Scanner sc) {
        ManagerController managerController = new ManagerController(user);
        EnquiryController enquiryController = new EnquiryController();
        boolean exit = false;

        while (!exit) {
            System.out.println("\nWelcome, Manager " + user.getFirstName() + "!");
            MenuPrinter.printRoleMenuHeader(2);
            MenuPrinter.printManagerMenu();
            try {
                int choice = Input.getIntInput(sc);
                switch (choice) {
                    // Project Management
                    case 1 -> createProject(sc, managerController);
                    case 2 -> editProject(sc, managerController);
                    case 3 -> deleteProject(sc, managerController);
                    case 4 -> viewAllProjectsWithFilters(sc, managerController);
                    case 5 -> viewOwnProjectsWithFilters(sc, managerController);

                    // Officer Management
                    case 6 -> viewOfficerRegistrations(managerController);
                    case 7 -> handleOfficerRegistration(sc, managerController);
                    case 8 -> handleOfficerWithdrawal(sc, managerController);
                    case 9 -> viewAssignedOfficers(managerController);

                    // BTO Application Management
                    case 10 -> handleBTOApplications(sc, managerController);
                    case 11 -> handleBTOWithdrawals(sc, managerController);
                    case 12 -> ReportUI.start(sc, user); // Delegate to ReportUI

                    // Enquiry Management
                    case 13 -> EnquiryUI.viewEnquiriesStaff(sc, enquiryController, managerController);
                    case 14 -> StaffUI.manageUserEnquiries(sc, user, enquiryController, managerController);

                    // Account
                    case 15 -> viewAccountDetails(user);
                    case 16 -> AuthUI.changePassword(sc, user);
                    case 0 -> exit = true;
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

    // --- Project Management Methods ---

    private static void createProject(Scanner sc, ManagerController controller) {
        System.out.println("\n=== Create New Project ===");
        try {
            System.out.print("Enter Project Name: ");
            String projectName = Input.getStringInput(sc);
            System.out.print("Enter Neighbourhood: ");
            String neighbourhood = Input.getStringInput(sc);

            List<String> flatTypes = new ArrayList<>();
            List<Integer> totalUnits = new ArrayList<>();
            List<Double> prices = new ArrayList<>();

            boolean addMoreFlats = true;
            while (addMoreFlats) {
                System.out.print("Enter Flat Type (e.g., 2-ROOM, 3-ROOM): ");
                String flatType = Input.getStringInput(sc).toUpperCase();
                int units = Input.getIntInput(sc, "Enter number of " + flatType + " units: ", 0, Integer.MAX_VALUE);
                double price = Input.getDoubleInput(sc, "Enter price for " + flatType + ": ", 0.0);

                flatTypes.add(flatType);
                totalUnits.add(units);
                prices.add(price);

                System.out.print("Add another flat type? (yes/no): ");
                String addMore = Input.getStringInput(sc);
                if (!addMore.equals("yes")) {
                    addMoreFlats = false;
                }
            }

            LocalDate openDate = Input.getDateInput(sc, "Enter Application Opening Date (yyyy-MM-dd): ");
            LocalDate closeDate = Input.getDateInput(sc, "Enter Application Closing Date (yyyy-MM-dd): ");

            if (closeDate.isBefore(openDate)) {
                System.out.println("Error: Closing date must be after opening date.");
                return;
            }

            int officerSlots = Input.getIntInput(sc,
                    "Enter Available HDB Officer Slots (1-" + Project.MAX_OFFICER_SLOTS + "): ", 1,
                    Project.MAX_OFFICER_SLOTS);

            System.out.print("Make project visible initially? (yes/no): ");
            boolean visible = Input.getStringInput(sc).toLowerCase().equals("yes");

            Project newProject = controller.createProject(projectName, neighbourhood, flatTypes, totalUnits, prices,
                    openDate, closeDate, visible, officerSlots);

            if (newProject != null) {
                System.out.println(
                        "Project '" + newProject.getName() + "' created successfully with ID: " + newProject.getId());
            } else {
                System.out.println(
                        "Failed to create project. Please check constraints (e.g., overlapping management periods).");
            }

        } catch (Input.InputExitException e) {
            System.out.println("Project creation cancelled.");
        } catch (InputMismatchException e) {
            System.out.println("Invalid input type during project creation.");
        } catch (DateTimeParseException e) {
            System.out.println("Invalid date format. Please use yyyy-MM-dd.");
        } catch (Exception e) {
            System.err.println("An error occurred during project creation: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static void editProject(Scanner sc, ManagerController controller) {
        System.out.println("\n=== Edit Project ===");
        List<Project> managedProjects = controller.getAssignedProjects();
        if (managedProjects.isEmpty()) {
            System.out.println("You are not managing any projects.");
            return;
        }

        System.out.println("Your Managed Projects:");
        MenuPrinter.printProjectTableSimple(managedProjects);

        try {
            System.out.print("Enter the ID of the project you wish to edit: ");
            int projectId = Input.getIntInput(sc);

            Project projectToEdit = controller.findManagedProjectById(projectId);
            if (projectToEdit == null) {
                System.out.println("Error: Project not found or you do not manage this project.");
                return;
            }

            boolean editing = true;
            while (editing) {
                System.out.println("\nEditing Project: " + projectToEdit.getName() + " (ID: " + projectId + ")");
                MenuPrinter.printEditProjectMenu();
                int choice = Input.getIntInput(sc);

                try {
                    switch (choice) {
                        case 1 -> {
                            System.out.print("Enter new Project Name: ");
                            String newName = Input.getStringInput(sc);
                            if (controller.renameProject(projectId, newName)) {
                                System.out.println("Project name updated.");
                                projectToEdit.setName(newName);
                            } else
                                System.out.println("Failed to update project name.");
                        }
                        case 2 -> {
                            System.out.print("Enter new Neighbourhood: ");
                            String newHood = Input.getStringInput(sc);
                            if (controller.changeNeighbourhood(projectId, newHood)) {
                                System.out.println("Neighbourhood updated.");
                                projectToEdit.setNeighbourhood(newHood);
                            } else
                                System.out.println("Failed to update neighbourhood.");
                        }
                        case 3 -> editFlatUnits(sc, controller, projectToEdit);
                        case 4 -> addFlatType(sc, controller, projectToEdit);
                        case 5 -> removeFlatType(sc, controller, projectToEdit);
                        case 6 -> editFlatPrice(sc, controller, projectToEdit);
                        case 7 -> {
                            LocalDate newOpenDate = Input.getDateInput(sc,
                                    "Enter new Application Opening Date (yyyy-MM-dd): ");
                            if (controller.setOpenDate(projectId, newOpenDate)) {
                                System.out.println("Opening date updated.");
                                projectToEdit.setOpenDate(newOpenDate);
                            } else
                                System.out.println("Failed to update opening date.");
                        }
                        case 8 -> {
                            LocalDate newCloseDate = Input.getDateInput(sc,
                                    "Enter new Application Closing Date (yyyy-MM-dd): ");
                            if (newCloseDate.isBefore(projectToEdit.getOpenDate())) {
                                System.out.println("Error: Closing date must be on or after the opening date ("
                                        + projectToEdit.getOpenDate() + ").");
                                break;
                            }
                            if (controller.setCloseDate(projectId, newCloseDate)) {
                                System.out.println("Closing date updated.");
                                projectToEdit.setCloseDate(newCloseDate);
                            } else
                                System.out.println("Failed to update closing date.");
                        }
                        case 9 -> {
                            Visibility currentVisibility = projectToEdit.getVisibility();
                            Visibility newVisibility = (currentVisibility == Visibility.ON) ? Visibility.OFF
                                    : Visibility.ON;
                            if (controller.setVisibility(projectId, newVisibility)) {
                                System.out.println("Visibility toggled to " + newVisibility + ".");
                                projectToEdit.setVisibility(newVisibility);
                            } else
                                System.out.println("Failed to toggle visibility.");
                        }
                        case 10 -> {
                            int newSlots = Input.getIntInput(sc,
                                    "Enter new available HDB Officer Slots (1-" + Project.MAX_OFFICER_SLOTS + "): ", 1,
                                    Project.MAX_OFFICER_SLOTS);
                            if (controller.setOfficerSlotLimit(projectId, newSlots)) {
                                System.out.println("Officer slots updated.");
                                projectToEdit.setOfficerSlotLimit(newSlots);
                            } else
                                System.out.println("Failed to update officer slots (check constraints).");
                        }
                        case 11 -> editing = false;
                        default -> System.out.println("Invalid choice.");
                    }
                } catch (Input.InputExitException e) {
                    System.out.println("Edit operation cancelled. Returning to edit menu.");
                } catch (InputMismatchException e) {
                    System.out.println("Invalid input type during edit.");
                } catch (DateTimeParseException e) {
                    System.out.println("Invalid date format. Please use yyyy-MM-dd.");
                }
            }

        } catch (Input.InputExitException e) {
            System.out.println("Project editing cancelled.");
        } catch (InputMismatchException e) {
            System.out.println("Invalid Project ID format.");
        } catch (Exception e) {
            System.err.println("An error occurred during project editing: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static void editFlatUnits(Scanner sc, ManagerController controller, Project project)
            throws Input.InputExitException {
        System.out.println("--- Edit Flat Units ---");
        MenuPrinter.printFlatTypesMenu(project);
        System.out.print("Select flat type to edit units (by name): ");
        String flatType = Input.getStringInput(sc).toUpperCase();

        if (!project.getFlatTypes().contains(flatType)) {
            System.out.println("Error: Flat type '" + flatType + "' not found in this project.");
            return;
        }

        int newUnits = Input.getIntInput(sc, "Enter new total unit count for " + flatType + ": ", 0, Integer.MAX_VALUE);
        if (controller.updateFlatTypeUnits(project.getId(), flatType, newUnits)) {
            System.out.println("Unit count for " + flatType + " updated.");
        } else {
            System.out.println(
                    "Failed to update unit count (check constraints, e.g., cannot be less than booked units).");
        }
    }

    private static void addFlatType(Scanner sc, ManagerController controller, Project project)
            throws Input.InputExitException {
        System.out.println("--- Add New Flat Type ---");
        System.out.print("Enter new Flat Type name: ");
        String flatType = Input.getStringInput(sc).toUpperCase();

        if (project.getFlatTypes().contains(flatType)) {
            System.out.println("Error: Flat type '" + flatType + "' already exists.");
            return;
        }

        int units = Input.getIntInput(sc, "Enter number of " + flatType + " units: ", 0, Integer.MAX_VALUE);
        double price = Input.getDoubleInput(sc, "Enter price for " + flatType + ": ", 0.0);

        if (controller.addFlatType(project.getId(), flatType, units, price)) {
            System.out.println("Flat type '" + flatType + "' added.");
        } else {
            System.out.println("Failed to add flat type.");
        }
    }

    private static void removeFlatType(Scanner sc, ManagerController controller, Project project)
            throws Input.InputExitException {
        System.out.println("--- Remove Flat Type ---");
        if (project.getFlatTypes().size() <= 1) {
            System.out.println("Error: Cannot remove the last flat type from a project.");
            return;
        }
        MenuPrinter.printFlatTypesMenu(project);
        System.out.print("Select flat type to remove (by name): ");
        String flatType = Input.getStringInput(sc).toUpperCase();

        if (!project.getFlatTypes().contains(flatType)) {
            System.out.println("Error: Flat type '" + flatType + "' not found.");
            return;
        }

        System.out.print("Are you sure you want to remove flat type '" + flatType + "'? (yes/no): ");
        if (Input.getStringInput(sc).equalsIgnoreCase("yes")) {
            if (controller.removeFlatType(project.getId(), flatType)) {
                System.out.println("Flat type '" + flatType + "' removed.");
            } else {
                System.out.println(
                        "Failed to remove flat type (check constraints, e.g., existing applications/bookings).");
            }
        } else {
            System.out.println("Removal cancelled.");
        }
    }

    private static void editFlatPrice(Scanner sc, ManagerController controller, Project project)
            throws Input.InputExitException {
        System.out.println("--- Edit Flat Price ---");
        MenuPrinter.printFlatTypesMenu(project);
        System.out.print("Select flat type to edit price (by name): ");
        String flatType = Input.getStringInput(sc).toUpperCase();

        if (!project.getFlatTypes().contains(flatType)) {
            System.out.println("Error: Flat type '" + flatType + "' not found.");
            return;
        }

        double newPrice = Input.getDoubleInput(sc, "Enter new price for " + flatType + ": ", 0.0);
        if (controller.updateFlatPrice(project.getId(), flatType, newPrice)) {
            System.out.println("Price for " + flatType + " updated.");
        } else {
            System.out.println("Failed to update price.");
        }
    }

    private static void deleteProject(Scanner sc, ManagerController controller) {
        System.out.println("\n=== Delete Project ===");
        List<Project> managedProjects = controller.getAssignedProjects();
        if (managedProjects.isEmpty()) {
            System.out.println("You are not managing any projects.");
            return;
        }

        System.out.println("Your Managed Projects:");
        MenuPrinter.printProjectTableSimple(managedProjects);

        try {
            System.out.print("Enter the ID of the project you wish to delete: ");
            int projectId = Input.getIntInput(sc);

            Project projectToDelete = controller.findManagedProjectById(projectId);
            if (projectToDelete == null) {
                System.out.println("Error: Project not found or you do not manage this project.");
                return;
            }

            System.out.print("Are you sure you want to delete project '" + projectToDelete.getName() + "' (ID: "
                    + projectId + ")? This cannot be undone. (yes/no): ");
            String confirmation = Input.getStringInput(sc).toLowerCase();

            if (confirmation.equals("yes")) {
                if (controller.deleteProject(projectId)) {
                    System.out.println("Project deleted successfully.");
                } else {
                    System.out.println("Failed to delete project. It might not exist or you might not manage it.");
                }
            } else {
                System.out.println("Deletion cancelled.");
            }

        } catch (Input.InputExitException e) {
            System.out.println("Project deletion cancelled.");
        } catch (InputMismatchException e) {
            System.out.println("Invalid Project ID format.");
        } catch (Exception e) {
            System.err.println("An error occurred during project deletion: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static void viewAllProjectsWithFilters(Scanner sc, ManagerController managerController) {
        System.out.println("\n=== All Projects with Filters ===");
        boolean exit = false;

        while (!exit) {
            System.out.println("1. View All Projects");
            System.out.println("2. Filter by Neighbourhood");
            System.out.println("3. Filter by Room Type");
            System.out.println("0. Back");
            System.out.print("Enter your choice: ");

            try {
                int choice = Input.getIntInput(sc);
                switch (choice) {
                    case 1 -> viewAllProjects(managerController);
                    case 2 -> filterByNeighbourhood(sc, managerController);
                    case 3 -> filterByRoomType(sc, managerController);
                    case 0 -> exit = true;
                    default -> System.out.println("Invalid choice. Please try again.");
                }
            } catch (Input.InputExitException e) {
                System.out.println("Filtering cancelled.");
                exit = true;
            } catch (Exception e) {
                System.out.println("Invalid input. Please try again.");
            }
        }
    }

    private static void viewOwnProjectsWithFilters(Scanner sc, ManagerController managerController) {
        System.out.println("\n=== Own Projects with Filters ===");
        boolean exit = false;

        while (!exit) {
            System.out.println("1. View Own Projects");
            System.out.println("2. Filter by Neighbourhood");
            System.out.println("3. Filter by Room Type");
            System.out.println("0. Back");
            System.out.print("Enter your choice: ");

            try {
                int choice = Input.getIntInput(sc);
                switch (choice) {
                    case 1 -> viewOwnProjects(managerController);
                    case 2 -> filterByNeighbourhood(sc, managerController);
                    case 3 -> filterByRoomType(sc, managerController);
                    case 0 -> exit = true;
                    default -> System.out.println("Invalid choice. Please try again.");
                }
            } catch (Input.InputExitException e) {
                System.out.println("Filtering cancelled.");
                exit = true;
            } catch (Exception e) {
                System.out.println("Invalid input. Please try again.");
            }
        }
    }

    private static void viewAllProjects(ManagerController controller) {
        System.out.println("\n=== All Projects ===");
        List<Project> allProjects = controller.listAllProjects();
        if (allProjects.isEmpty()) {
            System.out.println("No projects available in the system.");
            return;
        }
        MenuPrinter.printProjectTableDetailed(allProjects);
    }

    private static void viewOwnProjects(ManagerController managerController) {
        List<Project> projects = managerController.getAssignedProjects();
        if (projects.isEmpty()) {
            System.out.println("You have no assigned projects.");
            return;
        }

        System.out.println("Your assigned projects:");
        displayProjects(projects);
    }

    private static void filterByNeighbourhood(Scanner sc, ManagerController managerController) {
        List<Project> allProjects = managerController.listAllProjects();
        if (allProjects.isEmpty()) {
            System.out.println("No projects available.");
            return;
        }

        // Get all unique neighbourhoods from available projects
        List<String> neighbourhoods = allProjects.stream()
                .map(Project::getNeighbourhood)
                .distinct()
                .sorted()
                .toList();

        if (neighbourhoods.isEmpty()) {
            System.out.println("No neighbourhoods available.");
            return;
        }

        System.out.println("\nAvailable Neighbourhoods:");
        for (int i = 0; i < neighbourhoods.size(); i++) {
            System.out.println((i + 1) + ". " + neighbourhoods.get(i));
        }

        try {
            System.out.print("Select neighbourhood (1-" + neighbourhoods.size() + "): ");
            int index = Input.getIntInput(sc) - 1;

            if (index >= 0 && index < neighbourhoods.size()) {
                String selectedNeighbourhood = neighbourhoods.get(index);
                List<Project> filteredProjects = allProjects.stream()
                        .filter(p -> p.getNeighbourhood().equals(selectedNeighbourhood))
                        .toList();

                System.out.println("\nProjects in " + selectedNeighbourhood + ":");
                if (filteredProjects.isEmpty()) {
                    System.out.println("No projects in this neighbourhood.");
                } else {
                    displayProjects(filteredProjects);
                }
            } else {
                System.out.println("Invalid selection.");
            }
        } catch (Input.InputExitException e) {
            System.out.println("Neighbourhood filtering cancelled.");
        }
    }

    private static void filterByRoomType(Scanner sc, ManagerController managerController) {
        List<Project> allProjects = managerController.listAllProjects();
        if (allProjects.isEmpty()) {
            System.out.println("No projects available.");
            return;
        }

        // Get all unique flat types from available projects
        List<String> allFlatTypes = new java.util.ArrayList<>();
        for (Project p : allProjects) {
            for (String flatType : p.getFlatTypes()) {
                if (!allFlatTypes.contains(flatType)) {
                    allFlatTypes.add(flatType);
                }
            }
        }

        if (allFlatTypes.isEmpty()) {
            System.out.println("No room types available.");
            return;
        }

        java.util.Collections.sort(allFlatTypes);

        System.out.println("\nAvailable Room Types:");
        for (int i = 0; i < allFlatTypes.size(); i++) {
            System.out.println((i + 1) + ". " + allFlatTypes.get(i));
        }

        try {
            System.out.print("Select room type (1-" + allFlatTypes.size() + "): ");
            int index = Input.getIntInput(sc) - 1;

            if (index >= 0 && index < allFlatTypes.size()) {
                String selectedRoomType = allFlatTypes.get(index);
                List<Project> filteredProjects = allProjects.stream()
                        .filter(p -> p.getFlatTypes().contains(selectedRoomType))
                        .toList();

                System.out.println("\nProjects with " + selectedRoomType + ":");
                if (filteredProjects.isEmpty()) {
                    System.out.println("No projects with this room type.");
                } else {
                    displayProjects(filteredProjects);
                }
            } else {
                System.out.println("Invalid selection.");
            }
        } catch (Input.InputExitException e) {
            System.out.println("Room type filtering cancelled.");
        }
    }

    private static void displayProjects(List<Project> projects) {
        if (projects.isEmpty()) {
            System.out.println("No projects to display.");
            return;
        }
        MenuPrinter.printProjectTableDetailed(projects);
    }

    private static void handleBTOApplications(Scanner sc, ManagerController controller) {
        List<BTOApplication> pendingApps = controller.getPendingApplications();
        List<BTOApplication> applicationWithdrawalRequests = controller.getWithdrawalRequests();

        if (pendingApps.isEmpty() || !applicationWithdrawalRequests.isEmpty()) {
            System.out.println("No pending BTO applications to handle.");
            return;
        }

        System.out.println("\nPending BTO Applications:");
        System.out.println("-------------------------");
        printApplicationList(pendingApps);

        try {
            System.out.print("Enter Application ID to approve/reject (or type 'back' to return): ");

            String input = Input.getStringInput(sc);

            if (input.equalsIgnoreCase("back")) {
                return;
            }

            try {
                int appId = Integer.parseInt(input);

                BTOApplication selectedApp = controller.findManagedApplicationById(appId);
                if (selectedApp == null || selectedApp.getStatus() != ApplicationStatus.PENDING) {
                    System.out.println("Invalid Application ID or application is not pending.");
                    return;
                }

                System.out.println("\nSelected Application:");
                // view applications in full
                System.out.println(selectedApp.toString());
                System.out.print("Approve or Reject? (approve/reject): ");
                String decision = Input.getStringInput(sc).toLowerCase();

                if (decision.equals("approve")) {
                    controller.approveApplication(selectedApp);
                } else if (decision.equals("reject")) {
                    controller.rejectApplication(selectedApp);
                } else {
                    System.out.println("Invalid decision. No action taken.");
                }
            } catch (NumberFormatException e) {
                System.out.println("Please enter a valid application ID number.");
            }
        } catch (Input.InputExitException e) {
            System.out.println("Handling cancelled.");
        } catch (Exception e) {
            System.err.println("Error handling BTO application: " + e.getMessage());
        }
    }

    private static void handleBTOWithdrawals(Scanner sc, ManagerController controller) {
        List<BTOApplication> applicationWithdrawalRequests = controller.getWithdrawalRequests();
        if (applicationWithdrawalRequests.isEmpty()) {
            System.out.println("No pending BTO withdrawal requests.");
            return;
        }

        System.out.println("\nPending BTO Withdrawal Requests:");
        System.out.println("-------------------------------");
        printApplicationList(applicationWithdrawalRequests);

        try {
            System.out.print("Enter Application ID to confirm/reject withdrawal (or type 'back' to return): ");
            String input = sc.nextLine().trim();

            if (input.equalsIgnoreCase("back")) {
                return;
            }

            try {
                int appId = Integer.parseInt(input);

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
                    // Removed the check for BOOKED status to allow managers to confirm withdrawals
                    // regardless of status
                    controller.confirmWithdrawal(selectedApp);
                } else if (decision.equals("reject")) {
                    controller.rejectWithdrawalRequest(selectedApp);
                } else {
                    System.out.println("Invalid decision. No action taken.");
                }
            } catch (NumberFormatException e) {
                System.out.println("Please enter a valid application ID number.");
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
        System.out.println(manager);
        System.out.println("Managed Projects:");
        if (manager.getManagedProjects().isEmpty()) {
            System.out.println("  (None)");
        } else {
            manager.getManagedProjects()
                    .forEach(p -> System.out.println("  - " + p.getName() + " (ID: " + p.getId() + ")"));
        }
    }

    /**
     * Format table for displaying applications with standardized column widths
     */
    private static void printApplicationList(List<BTOApplication> applications) {
        if (applications == null || applications.isEmpty()) {
            System.out.println("No applications to display.");
            return;
        }
        // Updated column widths using Menu.COL_NAME for project name
        System.out.printf("%-8s %-20s %-" + Menu.COL_NAME + "s %-10s %-15s %-20s%n",
                "App ID", "Applicant Name", "Project", "Flat Type", "Status", "Details");
        System.out.println("-".repeat(115)); // Adjusted width for the separator line

        for (BTOApplication app : applications) {
            Applicant applicant = app.getApplicant();
            // Truncate long text to maintain column width using Menu.COL_NAME
            String projectName = Input.truncateText(app.getProject().getName(), Menu.COL_NAME - 2);
            String statusText = app.getStatus().toString();
            String details = (app.isWithdrawalRequested() ? "Withdrawal Req" : "");

            if (app.getBookedFlat() != null) {
                details += (details.isEmpty() ? "" : ", ") + "Booked: " + app.getBookedFlat().getId();
            }

            System.out.printf("%-8d %-20s %-" + Menu.COL_NAME + "s %-10s %-15s %-20s%n",
                    app.getId(),
                    applicant.getFirstName() + " " + applicant.getLastName(),
                    projectName,
                    app.getRoomType(),
                    statusText,
                    details);
        }
        System.out.println("-".repeat(115)); // Adjusted width for the separator line
    }

    // --- Officer Registration Management Methods ---

    /**
     * View pending officer registration requests for the manager's projects.
     */
    private static void viewOfficerRegistrations(ManagerController controller) {
        System.out.println("\n=== Pending Officer Registration Requests ===");
        List<Registration> pendingRegs = controller.getPendingOfficerRegistrations();
        List<Registration> officerWithdrawalRequests = controller.getOfficerWithdrawalRequests();

        if (pendingRegs.isEmpty()) {
            System.out.println("No pending officer registration requests for your projects.");
            return;
        }

        System.out.println("Pending Officer Registration Requests:");
        // Standardized column widths for better display formatting
        System.out.printf("%-8s %-20s %-" + Menu.COL_NAME + "s %-15s %-20s%n",
                "Reg ID", "Officer Name", "Project", "Status", "Project Dates");
        System.out.println("-".repeat(105));

        for (Registration reg : pendingRegs) {
            HDB_Officer officer = reg.getOfficer();
            Project project = reg.getProject();

            // Truncate project name to maintain column alignment using Menu.COL_NAME
            String projectName = Input.truncateText(project.getName(), Menu.COL_NAME - 2);

            if (officerWithdrawalRequests.isEmpty()) {
                System.out.printf("%-8d %-20s %-" + Menu.COL_NAME + "s %-15s %-20s%n",
                        reg.getId(),
                        officer.getFirstName() + " " + officer.getLastName(),
                        projectName,
                        reg.getStatus(),
                        project.getOpenDate() + " to " + project.getCloseDate());
            } else {
                System.out.printf("%-8d %-20s %-" + Menu.COL_NAME + "s %-20s%n",
                        reg.getId(),
                        officer.getFirstName() + " " + officer.getLastName(),
                        projectName,
                        reg.getStatus() + " (Withdrawal Req)");
            }
        }
        System.out.println("-".repeat(105));
    }

    /**
     * Handle (approve/reject) pending officer registration requests.
     */
    private static void handleOfficerRegistration(Scanner sc, ManagerController controller) {
        List<Registration> pendingRegs = controller.getPendingOfficerRegistrations();
        List<Registration> officerWithdrawalRequests = controller.getOfficerWithdrawalRequests();

        if (pendingRegs.isEmpty() || !officerWithdrawalRequests.isEmpty()) {
            System.out.println("No pending officer registration requests to handle.");
            return;
        }

        System.out.println("\n=== Handle Officer Registration Requests ===");
        // Standardized column widths using Menu.COL_NAME for project name
        System.out.printf("%-8s %-20s %-" + Menu.COL_NAME + "s %-15s %-20s%n",
                "Reg ID", "Officer Name", "Project", "Status", "Project Dates");
        System.out.println("-".repeat(105));

        for (Registration reg : pendingRegs) {
            HDB_Officer officer = reg.getOfficer();
            Project project = reg.getProject();

            // Truncate project name to maintain column alignment using Menu.COL_NAME
            String projectName = Input.truncateText(project.getName(), Menu.COL_NAME - 2);

            System.out.printf("%-8d %-20s %-" + Menu.COL_NAME + "s %-15s %-20s%n",
                    reg.getId(),
                    officer.getFirstName() + " " + officer.getLastName(),
                    projectName,
                    reg.getStatus(),
                    project.getOpenDate() + " to " + project.getCloseDate());
        }
        System.out.println("-".repeat(105));

        try {
            System.out.print("Enter Registration ID to approve/reject (or type 'back' to return): ");
            String input = sc.nextLine().trim();

            if (input.equalsIgnoreCase("back")) {
                return;
            }

            try {
                int regId = Integer.parseInt(input);

                Registration selectedReg = controller.findManagedPendingRegistrationById(regId);
                if (selectedReg == null) {
                    System.out.println("Invalid Registration ID or registration not pending for your project.");
                    return;
                }

                System.out.println("\nSelected Registration:");
                System.out.println("Officer: " + selectedReg.getOfficer().getFirstName() + " "
                        + selectedReg.getOfficer().getLastName());
                System.out.println("Project: " + selectedReg.getProject().getName());
                System.out.println("Status: " + selectedReg.getStatus());

                System.out.print("Approve or Reject? (approve/reject): ");
                String decision = Input.getStringInput(sc).toLowerCase();

                boolean success = false;
                if (decision.equals("approve")) {
                    success = controller.approveOfficerRegistration(selectedReg);
                    if (success) {
                        System.out.println("Registration approved successfully.");
                    }
                } else if (decision.equals("reject")) {
                    success = controller.rejectOfficerRegistration(selectedReg);
                    if (success) {
                        System.out.println("Registration rejected successfully.");
                    }
                } else {
                    System.out.println("Invalid decision. No action taken.");
                }

                if (!success && (decision.equals("approve") || decision.equals("reject"))) {
                    System.out.println("Action failed. Please check constraints (officer slots, overlaps).");
                }

            } catch (NumberFormatException e) {
                System.out.println("Please enter a valid registration ID number.");
            }

        } catch (Input.InputExitException e) {
            System.out.println("Operation cancelled.");
        } catch (Exception e) {
            System.err.println("Error handling officer registration: " + e.getMessage());
        }
    }

    /**
     * Handle officer registration withdrawals (both PENDING and APPROVED).
     * Allows managers to review and approve/reject withdrawal requests.
     */
    private static void handleOfficerWithdrawal(Scanner sc, ManagerController controller) {
        List<Registration> officerWithdrawalRequests = controller.getOfficerWithdrawalRequests();

        if (officerWithdrawalRequests.isEmpty()) {
            System.out.println("No pending officer registration withdrawal requests to handle.");
            return;
        }

        System.out.println("\n=== Handle Officer Registration Withdrawal Requests ===");
        // Standardized column widths using Menu.COL_NAME for project name
        System.out.printf("%-8s %-20s %-" + Menu.COL_NAME + "s %-20s%n",
                "Reg ID", "Officer Name", "Project", "Status");
        System.out.println("-".repeat(90));

        for (Registration reg : officerWithdrawalRequests) {
            HDB_Officer officer = reg.getOfficer();
            Project project = reg.getProject();

            // Truncate project name to maintain column alignment using Menu.COL_NAME
            String projectName = Input.truncateText(project.getName(), Menu.COL_NAME - 2);

            System.out.printf("%-8d %-20s %-" + Menu.COL_NAME + "s %-20s%n",
                    reg.getId(),
                    officer.getFirstName() + " " + officer.getLastName(),
                    projectName,
                    reg.getStatus() + " (Withdrawal Req)");
        }
        System.out.println("-".repeat(90));

        try {
            System.out.print("Enter Registration ID to handle withdrawal request (or type 'back' to return): ");
            String input = sc.nextLine().trim();

            if (input.equalsIgnoreCase("back")) {
                return;
            }

            try {
                int regId = Integer.parseInt(input);

                Registration selectedReg = officerWithdrawalRequests.stream()
                        .filter(reg -> reg.getId() == regId)
                        .findFirst()
                        .orElse(null);

                if (selectedReg == null) {
                    System.out.println("Invalid Registration ID or not a withdrawal request for your project.");
                    return;
                }

                System.out.println("\nSelected Registration Withdrawal Request:");
                System.out.println("Officer: " + selectedReg.getOfficer().getFirstName() + " "
                        + selectedReg.getOfficer().getLastName());
                System.out.println("Project: " + selectedReg.getProject().getName());
                System.out.println("Current Status: " + selectedReg.getStatus());

                System.out.print("Approve or Reject withdrawal request? (approve/reject): ");
                String decision = Input.getStringInput(sc).toLowerCase();

                boolean success = false;
                if (decision.equals("approve")) {
                    success = controller.approveRegistrationWithdrawal(selectedReg);
                    if (success) {
                        System.out.println(
                                "Registration withdrawal approved successfully. Officer removed from project.");
                    }
                } else if (decision.equals("reject")) {
                    success = controller.rejectRegistrationWithdrawal(selectedReg);
                    if (success) {
                        System.out
                                .println("Registration withdrawal rejected. Officer remains assigned to the project.");
                    }
                } else {
                    System.out.println("Invalid decision. No action taken.");
                }

                if (!success && (decision.equals("approve") || decision.equals("reject"))) {
                    System.out.println("Action failed. The registration may not be in a valid state.");
                }

            } catch (NumberFormatException e) {
                System.out.println("Please enter a valid registration ID number.");
            }

        } catch (Input.InputExitException e) {
            System.out.println("Operation cancelled.");
        } catch (Exception e) {
            System.err.println("Error handling officer withdrawal request: " + e.getMessage());
        }
    }

    /**
     * View officers assigned to manager's projects.
     * This shows a list of projects with their assigned officers.
     */
    private static void viewAssignedOfficers(ManagerController controller) {
        System.out.println("\n=== Officers Assigned to Your Projects ===");
        List<Project> managedProjects = controller.getAssignedProjects();

        if (managedProjects.isEmpty()) {
            System.out.println("You are not managing any projects.");
            return;
        }

        // Display projects and their assigned officers
        System.out.printf("%-5s %-" + Menu.COL_NAME + "s %-15s %-10s %-15s%n",
                "ID", "Project Name", "Available Slots", "Used Slots", "Assigned Officers");
        System.out.println("-".repeat(5 + Menu.COL_NAME + 15 + 10 + 15 + 4));

        for (Project project : managedProjects) {
            String projectName = Input.truncateText(project.getName(), Menu.COL_NAME - 2);
            List<HDB_Officer> officers = project.getAssignedOfficers();

            if (officers.isEmpty()) {
                System.out.printf("%-5d %-" + Menu.COL_NAME + "s %-15d %-10d No officers assigned%n",
                        project.getId(),
                        projectName,
                        project.getOfficerSlotLimit(),
                        0);
            } else {
                // Print first row with project info and first officer
                System.out.printf("%-5d %-" + Menu.COL_NAME + "s %-15d %-10d %s%n",
                        project.getId(),
                        projectName,
                        project.getOfficerSlotLimit(),
                        officers.size(),
                        officers.get(0).getFirstName() + " " + officers.get(0).getLastName());

                // Print remaining officers in separate rows
                for (int i = 1; i < officers.size(); i++) {
                    HDB_Officer officer = officers.get(i);
                    System.out.printf("%-5s %-" + Menu.COL_NAME + "s %-15s %-10s %s%n",
                            "", "", "", "",
                            officer.getFirstName() + " " + officer.getLastName());
                }
            }
            System.out.println(); // Extra line between projects
        }
    }
}