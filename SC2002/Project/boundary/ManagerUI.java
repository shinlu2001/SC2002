package SC2002.Project.boundary;

import SC2002.Project.boundary.util.Input;
import SC2002.Project.boundary.util.MenuPrinter;
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
        boolean exit = false;

        while (!exit) {
            System.out.println("\nWelcome, Manager " + user.getFirstName() + "!");
            MenuPrinter.printRoleMenuHeader(2);
            MenuPrinter.printManagerMenu();
            try {
                int choice = Input.getIntInput(sc);
                switch (choice) {
                    // Project Management (Cases 1-5)
                    case 1 -> createProject(sc, managerController);
                    case 2 -> editProject(sc, managerController);
                    case 3 -> deleteProject(sc, managerController);
                    case 4 -> viewAllProjects(managerController);
                    case 5 -> viewOwnProjects(managerController);
                    
                    // Officer Registration Management (Cases 6-8) - TODO: Implement later
                    case 6 -> System.out.println("View Officer Registrations - Not yet implemented."); // viewOfficerRegistrations(managerController);
                    case 7 -> System.out.println("Handle Officer Registration - Not yet implemented."); // handleOfficerRegistration(sc, managerController);
                    case 8 -> System.out.println("Handle Officer Withdrawal - Not yet implemented."); // handleOfficerWithdrawal(sc, managerController);
                    
                    // BTO Application Management (Cases 9-10)
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
                String flatType = Input.getStringInput(sc);
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

            int officerSlots = Input.getIntInput(sc, "Enter Available HDB Officer Slots (1-" + Project.MAX_OFFICER_SLOTS + "): ", 1, Project.MAX_OFFICER_SLOTS);

            System.out.print("Make project visible initially? (yes/no): ");
            boolean visible = Input.getStringInput(sc).toLowerCase().equals("yes");

            Project newProject = controller.createProject(projectName, neighbourhood, flatTypes, totalUnits, prices, openDate, closeDate, visible, officerSlots);

            if (newProject != null) {
                System.out.println("Project '" + newProject.getName() + "' created successfully with ID: " + newProject.getId());
            } else {
                System.out.println("Failed to create project. Please check constraints (e.g., overlapping management periods).");
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
        List<Project> managedProjects = controller.listMyProjects();
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
                            } else System.out.println("Failed to update project name.");
                        }
                        case 2 -> {
                            System.out.print("Enter new Neighbourhood: ");
                            String newHood = Input.getStringInput(sc);
                            if (controller.changeNeighbourhood(projectId, newHood)) {
                                System.out.println("Neighbourhood updated.");
                                projectToEdit.setNeighbourhood(newHood);
                            } else System.out.println("Failed to update neighbourhood.");
                        }
                        case 3 -> editFlatUnits(sc, controller, projectToEdit);
                        case 4 -> addFlatType(sc, controller, projectToEdit);
                        case 5 -> removeFlatType(sc, controller, projectToEdit);
                        case 6 -> editFlatPrice(sc, controller, projectToEdit);
                        case 7 -> {
                            LocalDate newOpenDate = Input.getDateInput(sc, "Enter new Application Opening Date (yyyy-MM-dd): ");
                            if (controller.setOpenDate(projectId, newOpenDate)) {
                                System.out.println("Opening date updated.");
                                projectToEdit.setOpenDate(newOpenDate);
                            } else System.out.println("Failed to update opening date.");
                        }
                        case 8 -> {
                            LocalDate newCloseDate = Input.getDateInput(sc, "Enter new Application Closing Date (yyyy-MM-dd): ");
                            if (newCloseDate.isBefore(projectToEdit.getOpenDate())) {
                                System.out.println("Error: Closing date must be on or after the opening date (" + projectToEdit.getOpenDate() + ").");
                                break;
                            }
                            if (controller.setCloseDate(projectId, newCloseDate)) {
                                System.out.println("Closing date updated.");
                                projectToEdit.setCloseDate(newCloseDate);
                            } else System.out.println("Failed to update closing date.");
                        }
                        case 9 -> {
                            Visibility currentVisibility = projectToEdit.getVisibility();
                            Visibility newVisibility = (currentVisibility == Visibility.ON) ? Visibility.OFF : Visibility.ON;
                            if (controller.setVisibility(projectId, newVisibility)) {
                                System.out.println("Visibility toggled to " + newVisibility + ".");
                                projectToEdit.setVisibility(newVisibility);
                            } else System.out.println("Failed to toggle visibility.");
                        }
                        case 10 -> {
                            int newSlots = Input.getIntInput(sc, "Enter new available HDB Officer Slots (1-" + Project.MAX_OFFICER_SLOTS + "): ", 1, Project.MAX_OFFICER_SLOTS);
                            if (controller.setOfficerSlotLimit(projectId, newSlots)) {
                                System.out.println("Officer slots updated.");
                                projectToEdit.setOfficerSlotLimit(newSlots);
                            } else System.out.println("Failed to update officer slots (check constraints).");
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

    private static void editFlatUnits(Scanner sc, ManagerController controller, Project project) throws Input.InputExitException {
        System.out.println("--- Edit Flat Units ---");
        MenuPrinter.printFlatTypesMenu(project);
        System.out.print("Select flat type to edit units (by name): ");
        String flatType = Input.getStringInput(sc);

        if (!project.getFlatTypes().contains(flatType)) {
            System.out.println("Error: Flat type '" + flatType + "' not found in this project.");
            return;
        }

        int newUnits = Input.getIntInput(sc, "Enter new total unit count for " + flatType + ": ", 0, Integer.MAX_VALUE);
        if (controller.updateFlatTypeUnits(project.getId(), flatType, newUnits)) {
            System.out.println("Unit count for " + flatType + " updated.");
        } else {
            System.out.println("Failed to update unit count (check constraints, e.g., cannot be less than booked units).");
        }
    }

    private static void addFlatType(Scanner sc, ManagerController controller, Project project) throws Input.InputExitException {
        System.out.println("--- Add New Flat Type ---");
        System.out.print("Enter new Flat Type name: ");
        String flatType = Input.getStringInput(sc);

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

    private static void removeFlatType(Scanner sc, ManagerController controller, Project project) throws Input.InputExitException {
        System.out.println("--- Remove Flat Type ---");
        if (project.getFlatTypes().size() <= 1) {
            System.out.println("Error: Cannot remove the last flat type from a project.");
            return;
        }
        MenuPrinter.printFlatTypesMenu(project);
        System.out.print("Select flat type to remove (by name): ");
        String flatType = Input.getStringInput(sc);

        if (!project.getFlatTypes().contains(flatType)) {
            System.out.println("Error: Flat type '" + flatType + "' not found.");
            return;
        }

        System.out.print("Are you sure you want to remove flat type '" + flatType + "'? (yes/no): ");
        if (Input.getStringInput(sc).equalsIgnoreCase("yes")) {
            if (controller.removeFlatType(project.getId(), flatType)) {
                System.out.println("Flat type '" + flatType + "' removed.");
            } else {
                System.out.println("Failed to remove flat type (check constraints, e.g., existing applications/bookings).");
            }
        } else {
            System.out.println("Removal cancelled.");
        }
    }

    private static void editFlatPrice(Scanner sc, ManagerController controller, Project project) throws Input.InputExitException {
        System.out.println("--- Edit Flat Price ---");
        MenuPrinter.printFlatTypesMenu(project);
        System.out.print("Select flat type to edit price (by name): ");
        String flatType = Input.getStringInput(sc);

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
        List<Project> managedProjects = controller.listMyProjects();
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

            System.out.print("Are you sure you want to delete project '" + projectToDelete.getName() + "' (ID: " + projectId + ")? This cannot be undone. (yes/no): ");
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

    private static void viewAllProjects(ManagerController controller) {
        System.out.println("\n=== All Projects ===");
        List<Project> allProjects = controller.listAllProjects();
        if (allProjects.isEmpty()) {
            System.out.println("No projects available in the system.");
            return;
        }
        MenuPrinter.printProjectTableDetailed(allProjects);
    }

    private static void viewOwnProjects(ManagerController controller) {
        System.out.println("\n=== Your Managed Projects ===");
        List<Project> managedProjects = controller.listMyProjects();
        if (managedProjects.isEmpty()) {
            System.out.println("You are not managing any projects.");
            return;
        }
        MenuPrinter.printProjectTableDetailed(managedProjects);
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
        System.out.println(manager);
        System.out.println("Managed Projects:");
        if (manager.getManagedProjects().isEmpty()) {
            System.out.println("  (None)");
        } else {
            manager.getManagedProjects().forEach(p -> System.out.println("  - " + p.getName() + " (ID: " + p.getId() + ")"));
        }
    }
    
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
}
