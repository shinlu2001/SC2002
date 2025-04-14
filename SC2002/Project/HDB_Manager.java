package SC2002.Project;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.InputMismatchException;
import java.util.List;
import java.util.Scanner;

public class HDB_Manager extends User implements Input {
    protected static int nextId = -1;
    private final int manager_id;
    private String type = "MANAGER";

    // List to store projects created by the manager
    protected List<Project> managerProjects = new ArrayList<>();

    // Constructor
    public HDB_Manager(String nric, String firstname, String lastname, String marital_status, int age) {
        super(nric, firstname, lastname, marital_status, age);
        this.managerProjects = new ArrayList<>();
        manager_id = ++nextId;
    }

    public void to_string() {
        super.to_string();
        System.out.println("Account type: " + type);
        System.out.println("ManagerID: " + manager_id);
    }

    // Start manager menu loop
    public void start_menu(Scanner sc) {
        System.out.println("Welcome to HDB BTO Management System, " + this.get_firstname() + "!");
        int choice = 0;
        boolean loop = true;  // Control variable for menu loop

        while (loop) {
            try {
                System.out.println("============================================");
                System.out.println("         M A N A G E R   M E N U");
                System.out.println("============================================");
                // Print manager menu from the Menu class
                menu.printManagerMenu();

                // Get menu choice with proper exit handling.
                choice = Input.getIntInput(sc);

                switch (choice) {
                    case 1:     // Create a new project via keyboard input
                        createProject(sc);
                        break;

                    case 2:     // Edit a Project
                        System.out.println("Available projects managed by you:");
                        printProjectTable(managerProjects);
                        System.out.print("Enter the ID of the project you wish to edit: ");
                        Project projectToEdit = findAndValidateProject(sc);
                        if (projectToEdit != null) {
                            editProject(projectToEdit, sc);
                        }
                        break;

                    case 3: // Delete a Project
                        System.out.println("Available projects managed by you:");
                        printProjectTable(managerProjects);
                        System.out.print("Enter the ID of the project you wish to delete: ");
                        Project projectToDelete = findAndValidateProject(sc);
                        if (projectToDelete != null) {
                            deleteProject(projectToDelete);
                        }
                        break;

                    case 4:     // View all projects
                        viewAllProjects();
                        break;

                    case 5:     // View own projects
                        viewOwnProjects();
                        break;

                    case 6:     // View officer registration
                        viewOfficerRegistration();
                        break;

                    case 7:     // Handle Officer Registration
                        System.out.println("Available projects (by ID) that you manage:");
                        printProjectTable(managerProjects);
                        System.out.print("Enter the Project ID to manage officer registrations: ");
                        Project projectForOfficer = findAndValidateProject(sc);
                        if (projectForOfficer == null)
                            break;
                        List<HDB_Officer> pendingOfficers = new ArrayList<>();
                        // Iterate over all officers in the system and check their registrations for this project.
                        for (HDB_Officer o : BTOsystem.officers) {
                            // Using the new list: get the registration entry for the target project.
                            HDB_Officer.OfficerRegistration reg = o.getRegistrationForProject(projectForOfficer);
                            if (reg != null && reg.getStatus().equals("PENDING")) {
                                pendingOfficers.add(o);
                            }
                        }
                        if (pendingOfficers.isEmpty()) {
                            System.out.println("No pending officer registrations for this project.");
                            break;
                        }
                        // Print a table of pending officers for selection
                        printOfficerTable(pendingOfficers);
                        System.out.print("Enter index of officer to review: ");
                        int idx = Input.getIntInput(sc);
                        if (idx < 0 || idx >= pendingOfficers.size()) {
                            System.out.println("Invalid index.");
                            break;
                        }
                        HDB_Officer officerToHandle = pendingOfficers.get(idx);
                        System.out.print("Do you approve this officer's registration? (yes/no): ");
                        String decision = Input.getStringInput(sc);
                        if (decision.equalsIgnoreCase("yes")) {
                            handleOfficerRegistration(projectForOfficer, officerToHandle);
                        } else if (decision.equalsIgnoreCase("no")) {
                            // Mark the registration as rejected if it exists.
                            HDB_Officer.OfficerRegistration reg = officerToHandle.getRegistrationForProject(projectForOfficer);
                            if (reg != null) {
                                reg.setStatus("REJECTED");
                            }
                            System.out.println("Officer " + officerToHandle.get_firstname() + " " + officerToHandle.get_lastname() 
                                    + "'s registration has been rejected.");
                        } else {
                            System.out.println("Invalid decision. Operation cancelled.");
                        }
                        break;                        
                    case 8:     // Handle officer withdrawal requests
                        System.out.println("Available projects managed by you:");
                        printProjectTable(managerProjects);
                        System.out.print("Enter the Project ID to manage: ");
                        Project projectForWithdrawal_o = findAndValidateProject(sc);
                        if (projectForWithdrawal_o == null) break;

                        // Print officers registered for the selected project (by table)
                        List<HDB_Officer> officersForWithdrawal = new ArrayList<>();
                        for (HDB_Officer o : BTOsystem.officers) {
                            if (o.isRegisteredForProject(projectForWithdrawal_o)) {
                                officersForWithdrawal.add(o);
                            }
                        }
                        if (officersForWithdrawal.isEmpty()) {
                            System.out.println("No officers found for the selected project.");
                            break;
                        }
                        printOfficerTable(officersForWithdrawal);
                        System.out.print("Enter Officer's ID (as shown in the table): ");
                        int withdrawalOfficerId = Input.getIntInput(sc);
                        HDB_Officer officer_withdrawal = null;
                        // Find the officer by ID from the printed list.
                        for (HDB_Officer o : officersForWithdrawal) {
                            if (o.getOfficerId() == withdrawalOfficerId) {
                                officer_withdrawal = o;
                                break;
                            }
                        }
                        if (officer_withdrawal != null) {
                            handleWithdrawalRequest_officer(projectForWithdrawal_o, officer_withdrawal, sc);
                        } else {
                            System.out.println("Error: Officer not found.");
                        }
                        break;

                    case 9:     // Handle BTO application (approve/reject)
                        System.out.println("Available projects managed by you:");
                        printProjectTable(managerProjects);
                        System.out.print("Enter the Project ID to manage: ");
                        Project projectForBTO = findAndValidateProject(sc);
                        if (projectForBTO == null) break;
                        // Filter applications for this project and print them in a table format.
                        List<BTOapplication> appsForProject = new ArrayList<>();
                        for (BTOapplication app : BTOsystem.applications) {
                            if (app.getProject().equals(projectForBTO)) {
                                appsForProject.add(app);
                            }
                        }
                        if (appsForProject.isEmpty()) {
                            System.out.println("No applications found for this project.");
                            break;
                        }
                        printApplicationTable(appsForProject);
                        System.out.print("Enter Applicant's ID (as shown in the table): ");
                        int applicationId = Input.getIntInput(sc);
                        String flatType = null;
                        BTOapplication application = null;
                        for (BTOapplication app : appsForProject) {
                            if (app.getId() == applicationId) {
                                application = app;
                                flatType = app.getFlatType();
                                break;
                            }
                        }
                        if (application != null) {
                            System.out.println("Do you want to approve this application? Enter 'yes/no': ");
                            String confirm = Input.getStringInput(sc);
                            if (confirm.toLowerCase().equals("yes")) {
                                handleBTOapplication(projectForBTO, application, flatType);
                            } else if (confirm.toLowerCase().equals("no")) {
                                // Change the application's status to rejected
                                application.setStatus("REJECTED");  
                                System.out.println("Application has been rejected.");
                            }
                        } else {
                            System.out.println("Error: Application not found.");
                        }
                        break;
                    case 10:    // Handle application withdrawal requests
                        System.out.println("Available projects managed by you:");
                        printProjectTable(managerProjects);
                        System.out.print("Enter the Project ID to manage: ");
                        Project projectForWithdrawal_a = findAndValidateProject(sc);
                        if (projectForWithdrawal_a == null) break;
                        // Filter applications by this project and print the table
                        List<BTOapplication> appsForWithdrawal = new ArrayList<>();
                        for (BTOapplication app : BTOsystem.applications) {
                            if (app.getProject().equals(projectForWithdrawal_a)) {
                                appsForWithdrawal.add(app);
                            }
                        }
                        if (appsForWithdrawal.isEmpty()) {
                            System.out.println("No applications found for this project.");
                            break;
                        }
                        printApplicationTable(appsForWithdrawal);
                        System.out.print("Enter application ID (as shown in the table): ");
                        int withdrawalApplicationId = Input.getIntInput(sc);
                        BTOapplication withdrawalApplication = null;
                        for (BTOapplication app : appsForWithdrawal) {
                            if (app.getId() == withdrawalApplicationId) {
                                withdrawalApplication = app;
                                break;
                            }
                        }
                        if (withdrawalApplication != null) {
                            handleWithdrawalRequest_application(projectForWithdrawal_a, withdrawalApplication, sc);
                        } else {
                            System.out.println("Error: Application not found.");
                        }
                        break;

                    case 11:    // Generate applicant report
                        generateReport(sc);
                        break;

                    case 12:    // View all enquiries
                        viewAllEnquiries(sc);
                        break;

                    case 13:    // Handle project enquiries
                        handleProjectEnquiries(sc);
                        break;

                    case 14:    // View manager account details
                        System.out.println("---------------------------------------------------");
                        to_string();
                        System.out.println("---------------------------------------------------");
                        break;

                    case 15:    // Log out
                        System.out.println("---------------------------------------------------");
                        System.out.println("Logged out. Returning to main menu...");
                        System.out.println("---------------------------------------------------");
                        loop = false;
                        break;

                    default:
                        System.out.println("---------------------------------------------------");
                        System.out.println("Error: Invalid choice. Please try again.");
                        System.out.println("---------------------------------------------------");
                        break;
                }
            } catch (Input.InputExitException e) {
                // This catch will capture exit/back commands thrown from any inner input
                System.out.println("Operation cancelled by user. Returning to Manager Menu.");
                // Continue the outer manager menu loop.
            } catch (InputMismatchException e) {
                System.out.println("---------------------------------------------------");
                System.out.println("Error: Invalid input. Please enter a number.");
                System.out.println("---------------------------------------------------");
                sc.nextLine();  // Consume leftover newline
            }
        } // end of while(loop)
    }

    // Find and validate project by ID ensuring the manager is responsible for it.
    private Project findAndValidateProject(Scanner sc) {
        int projectID = Input.getIntInput(sc);
        Project project = null;
        for (Project p : BTOsystem.projects) {
            if (p.getId() == projectID) {
                project = p;
                break;
            }
        }
        if (project == null) {
            System.out.println("---------------------------------------------------");
            System.out.println("Error: Project does not exist.");
            return null;
        }
        if (!managerProjects.contains(project)) {
            System.out.println("---------------------------------------------------");
            System.out.println("Error: You are not the manager of Project " + project.getId() + ".");
            return null;
        }
        return project;
    }

    // Display current flat types for a project.
    public void FlatTypesMenu(Project p) {
        System.out.println("\n============================================");
        System.out.println("            CURRENT FLAT TYPES");
        System.out.println("============================================");
        System.out.printf("%-5s %-12s %-13s %-15s %-10s%n", 
                "No.", "Flat Type", "Prices", "Total Units", "Available");
        System.out.println("-----------------------------------------------------------");
        for (int i = 0; i < p.getFlatTypes().size(); i++) {
            System.out.printf("%-5s %-12s %-13s %-15s %-10s%n",
                    i + 1,
                    p.getFlatTypes().get(i),
                    p.getFlatPrice(p.getFlatTypes().get(i)),
                    p.getTotalUnits().get(i),
                    p.getAvailableUnits().get(i));
        }
    }

    // Create a new project.
    public Project createProject(Scanner sc) {
        System.out.println("\n============================================");
        System.out.println("            CREATING A PROJECT");
        System.out.println("============================================");
        System.out.print("Enter Project Name: ");
        String projectName = Input.getStringInput(sc);
        System.out.print("Enter Neighbourhood: ");
        String neighbourhood = Input.getStringInput(sc);

        List<String> flatTypes = new ArrayList<>();
        List<Integer> totalUnits = new ArrayList<>();
        List<Integer> availableUnits = new ArrayList<>();
        boolean addMoreFlats = true;
        while (addMoreFlats) {
            System.out.print("Enter Flat Type (e.g., 2-Room, 3-Room, 4-Room): ");
            String flatType = Input.getStringInput(sc);
            int units;
            while (true) {
                try {
                    System.out.print("Enter number of " + flatType + " units (none = 0): ");
                    units = Input.getIntInput(sc);
                    if (units >= 0) {
                        break;
                    }
                    System.out.println("Error: Number of units cannot be negative.");
                } catch (InputMismatchException e) {
                    System.out.println("Error: Please enter a valid number.");
                    sc.nextLine();
                }
            }
            double price;
            while (true) {
                try {
                    System.out.print("Enter price of flat: ");
                    price = Input.getIntInput(sc);
                    if (price >= 0) {
                        break;
                    }
                    System.out.println("Error: Price cannot be negative.");
                } catch (InputMismatchException e) {
                    System.out.println("Error: Please enter a valid number.");
                }
            }
            flatTypes.add(flatType);
            totalUnits.add(units);
            availableUnits.add(units); // Initially, available units equal total units.
            while (true) {
                try {
                    System.out.print("Add another flat type? (yes/no): ");
                    String response = Input.getStringInput(sc).toLowerCase();
                    if (response.equals("yes") || response.equals("no")) {
                        if (response.equals("no")) {
                            addMoreFlats = false;
                        }
                        break;
                    } else {
                        System.out.println("Error: Please enter either 'yes' or 'no'.");
                    }
                } catch (Input.InputExitException e) {
                    System.out.println("Operation cancelled. Exiting flat type input loop.");
                    addMoreFlats = false;
                    break;
                }
            }
        }

        // Get the application opening date.
        LocalDate openDate = null;
        while (true) {
            try {
                System.out.print("Enter Application Opening Date for Project (yyyy-MM-dd): ");
                String dateInput = Input.getStringInput(sc);
                openDate = LocalDate.parse(dateInput);
                break;
            } catch (DateTimeParseException e) {
                System.out.println("Error: Invalid format. Use yyyy-MM-dd.");
            } catch (Input.InputExitException e) {
                System.out.println("Operation cancelled. Exiting date input loop.");
                break;
            }
        }
        // Get the application closing date.
        LocalDate closeDate;
        while (true) {
            try {
                System.out.print("Enter Application Closing Date for Project (yyyy-MM-dd): ");
                String dateInput = Input.getStringInput(sc);
                closeDate = LocalDate.parse(dateInput);
                break;
            } catch (DateTimeParseException e) {
                System.out.println("Error: Invalid format. Use yyyy-MM-dd.");
            } catch (Input.InputExitException e) {
                System.out.println("Operation cancelled. Exiting date input loop.");
                return null;
            }
        }
        int totalOfficerSlots;
        while (true) {
            try {
                System.out.print("Enter Available HDB Officer Slots (MAX " + Project.getmaxOfficerSlots() + "): ");
                totalOfficerSlots = Input.getIntInput(sc);
                sc.nextLine(); // Consume newline
                if (totalOfficerSlots > Project.getmaxOfficerSlots() || totalOfficerSlots <= 0) {
                    System.out.println("Error: Available officer slots must be between 1 and " + Project.getmaxOfficerSlots() + ".");
                    continue;
                }
                break;
            } catch (InputMismatchException e) {
                System.out.println("Error: Please enter a valid number.");
                sc.nextLine();
            }
        }
        // Check if the manager already has a project within the same application period.
        for (Project existingProject : managerProjects) {
            LocalDate existingOpen = existingProject.getOpenDate();
            LocalDate existingClose = existingProject.getCloseDate();
            if (!(closeDate.isBefore(existingOpen) || openDate.isAfter(existingClose))) {
                System.out.println("Error: Failed to create " + projectName + ". You can only manage one project within an application period.");
                return null;
            }
        }
        // Create the project.
        Project project = new Project(projectName, neighbourhood, flatTypes, totalUnits,
                openDate, closeDate, false, totalOfficerSlots);
        project.setManager(this);
        managerProjects.add(project);
        BTOsystem.projects.add(project);
        System.out.println("---------------------------------------------------");
        System.out.println(projectName + " successfully created with ID: " + project.getId());
        return project;
    }

    public void viewOwnProjects() {
        System.out.println("\n============================================");
        System.out.println("                 MY PROJECTS");
        System.out.println("============================================");
        if (!managerProjects.isEmpty()) {
            System.out.printf("%-5s %-20s %-15s %-15s %-10s %-15s %-15s %-10s %-15s %-15s%n", 
                    "ID", "Project Name", "Neighbourhood", "Flat Types", "Price", "Open Date", "Close Date", "Visible", "Manager", "Officer Slots");
            System.err.println("------------------------------------------------------------------------------------------------------------------------------------------------");
            for (Project p : managerProjects) {
                if (p.getManager() == this) {
                    System.out.println(p);
                }
            }
        } else {
            System.out.println("No projects available.");
        }
    }

    public static void viewAllProjects() {
        System.out.println("\n============================================");
        System.out.println("               ALL PROJECTS");
        System.out.println("============================================");
        if (!BTOsystem.projects.isEmpty()) {
            System.out.printf("%-5s %-20s %-15s %-15s %-10s %-15s %-15s %-10s %-15s %-15s%n", 
                    "ID", "Project Name", "Neighbourhood", "Flat Types", "Price", "Open Date", "Close Date", "Visible", "Manager", "Officer Slots");
            System.err.println("------------------------------------------------------------------------------------------------------------------------------------------------");
            for (Project p : BTOsystem.projects) {
                System.out.println(p);
            }
        } else {
            System.out.println("No projects available.");
        }
    }

    // Edit project attributes with proper try/catch handling so that exit/back aborts the current operation only.
    public void editProject(Project project, Scanner sc) {
        int option = 0;
        // Find the project in the global list
        for (Project p : BTOsystem.projects) {
            if (p.equals(project)) {
                boolean loop = true;
                while (loop) {
                    try {
                        System.out.println("\n============================================");
                        System.out.println("              EDITING PROJECT");
                        System.out.println("============================================");
                        System.err.println("------------------------------------------------------------------------------------------------------------------------------------------------");
                        System.out.printf("%-5s %-20s %-15s %-15s %-10s %-15s %-15s %-10s %-15s %-15s%n", 
                                "ID", "Project Name", "Neighbourhood", "Flat Types", "Price", "Open Date", "Close Date", "Visible", "Manager", "Officer Slots");
                        System.err.println("------------------------------------------------------------------------------------------------------------------------------------------------");
                        System.out.println(project.toString());
                        System.out.println("Select attribute to edit: ");
                        System.out.println("1. Project Name");
                        System.out.println("2. Neighbourhood");
                        System.out.println("3. Edit Unit Count for Existing Flats");
                        System.out.println("4. Add New Flat Type");
                        System.out.println("5. Remove Flat Type");
                        System.out.println("6. Edit Existing Flat Prices");
                        System.out.println("7. Application Opening Date");
                        System.out.println("8. Application Closing Date");
                        System.out.println("9. Toggle Visibility");
                        System.out.println("10. Available HDB Officer Slots");
                        System.out.println("11. Return to Manager Menu");
                        System.out.print("Enter your choice: ");
                        try {
                            option = Input.getIntInput(sc);
                        } catch (Input.InputExitException e) {
                            System.out.println("Operation cancelled. Returning to editing menu.");
                            continue;
                        }
                        
                        switch (option) {
                            case 1:
                                System.out.print("Enter new Project Name: ");
                                try {
                                    p.setProjectName(Input.getStringInput(sc));
                                } catch (Input.InputExitException e) {
                                    System.out.println("Operation cancelled. Returning to editing menu.");
                                }
                                break;
    
                            case 2:
                                System.out.print("Enter new Neighbourhood: ");
                                try {
                                    p.setneighbourhood(Input.getStringInput(sc));
                                } catch (Input.InputExitException e) {
                                    System.out.println("Operation cancelled. Returning to editing menu.");
                                }
                                break;
    
                            case 3:
                                FlatTypesMenu(p);
                                String currentType = null; // Declare currentType
                                int index = -1;
                                int flatChoice;
                                while (true) {
                                    try {
                                        System.out.print("\nSelect flat type to edit (No. 1-" + p.getFlatTypes().size() + "): ");
                                        flatChoice = Input.getIntInput(sc);
                                        if (flatChoice > 0 && flatChoice <= p.getFlatTypes().size()) {
                                            index = flatChoice - 1;
                                            currentType = p.getFlatTypes().get(index);
                                            break;
                                        }
                                        System.out.println("Error: Please enter a number between 1 and " + p.getFlatTypes().size());
                                    } catch (InputMismatchException e) {
                                        System.out.println("Error: Please enter a valid number.");
                                        sc.nextLine();
                                    } catch (Input.InputExitException e) {
                                        System.out.println("Operation cancelled. Returning to editing menu.");
                                        break;
                                    }
                                }
                                if (currentType == null) {
                                    break;
                                }
                                int newUnits;
                                while (true) {
                                    try {
                                        System.out.print("Enter new unit count for " + currentType + ": ");
                                        newUnits = Input.getIntInput(sc);
                                        if (newUnits >= 0) {
                                            p.updateFlatTypeUnits(currentType, newUnits);
                                            System.out.println(currentType + " updated successfully.");
                                            break;
                                        }
                                        System.out.println("Error: Units cannot be negative.");
                                    } catch (InputMismatchException e) {
                                        System.out.println("Error: Please enter a valid number.");
                                        sc.nextLine();
                                    } catch (Input.InputExitException e) {
                                        System.out.println("Operation cancelled. Returning to editing menu.");
                                        break;
                                    }
                                }
                                break;
    
                            case 4:
                                FlatTypesMenu(p);
                                System.out.print("\nEnter new Flat Type: ");
                                String newType = "";
                                try {
                                    newType = Input.getStringInput(sc);
                                } catch (Input.InputExitException e) {
                                    System.out.println("Operation cancelled. Returning to editing menu.");
                                    break;
                                }
                                if (p.getFlatTypes().contains(newType)) {
                                    System.out.println("Error: This flat type already exists.");
                                    break;
                                }
                                while (true) {
                                    try {
                                        System.out.print("Enter number of " + newType + " units (none = 0): ");
                                        newUnits = Input.getIntInput(sc);
                                        if (newUnits >= 0) {
                                            p.addFlatType(newType, newUnits);
                                            System.out.println(newType + " added successfully.");
                                            break;
                                        }
                                        System.out.println("Error: Units cannot be negative.");
                                    } catch (InputMismatchException e) {
                                        System.out.println("Error: Please enter a valid number.");
                                        sc.nextLine();
                                    } catch (Input.InputExitException e) {
                                        System.out.println("Operation cancelled. Returning to editing menu.");
                                        break;
                                    }
                                }
                                break;
    
                            case 5:
                                FlatTypesMenu(p);
                                String typeToRemove;
                                if (p.getFlatTypes().size() <= 1) {
                                    System.out.println("Error: Unable to remove (a project must have at least one flat type).");
                                    break;
                                }
                                while (true) {
                                    try {
                                        System.out.print("\nSelect flat type to remove (No. 1-" + p.getFlatTypes().size() + "): ");
                                        flatChoice = Input.getIntInput(sc);
                                        if (flatChoice > 0 && flatChoice <= p.getFlatTypes().size()) {
                                            index = flatChoice - 1;
                                            typeToRemove = p.getFlatTypes().get(index);
                                            int currentUnit = p.getTotalUnits().get(index);
                                            p.removeFlatType(typeToRemove, currentUnit);
                                            System.out.println(typeToRemove + " removed successfully.");
                                            break;
                                        } else {
                                            System.out.println("Error: Invalid selection. Please enter a number between 1 and " + p.getFlatTypes().size());
                                        }
                                    } catch (InputMismatchException e) {
                                        System.out.println("Error: Please enter a valid number.");
                                        sc.nextLine();
                                    } catch (Input.InputExitException e) {
                                        System.out.println("Operation cancelled. Returning to editing menu.");
                                        break;
                                    }
                                }
                                break;
    
                            case 6:
                                FlatTypesMenu(p);
                                String currentTypeForPrice = null;  // Declare new variable for price editing
                                while (true) {
                                    try {
                                        System.out.print("\nSelect flat type to edit price (No. 1-" + p.getFlatTypes().size() + "): ");
                                        flatChoice = Input.getIntInput(sc);
                                        if (flatChoice > 0 && flatChoice <= p.getFlatTypes().size()) {
                                            index = flatChoice - 1;
                                            currentTypeForPrice = p.getFlatTypes().get(index);
                                            break;
                                        }
                                        System.out.println("Error: Please enter a number between 1 and " + p.getFlatTypes().size());
                                    } catch (InputMismatchException e) {
                                        System.out.println("Error: Please enter a valid number.");
                                        sc.nextLine();
                                    } catch (Input.InputExitException e) {
                                        System.out.println("Operation cancelled. Returning to editing menu.");
                                        break;
                                    }
                                }
                                // Check if the operation was cancelled.
                                if (currentTypeForPrice == null) {
                                    break;
                                }
                                double newPrice;
                                while (true) {
                                    try {
                                        System.out.print("Enter new price for " + currentTypeForPrice + ": ");
                                        newPrice = Input.getIntInput(sc);
                                        if (newPrice >= 0) {
                                            System.out.println(currentTypeForPrice + " updated successfully.");
                                            break;
                                        }
                                        System.out.println("Error: Price cannot be negative.");
                                    } catch (InputMismatchException e) {
                                        System.out.println("Error: Please enter a valid number.");
                                        sc.nextLine();
                                    } catch (Input.InputExitException e) {
                                        System.out.println("Operation cancelled. Returning to editing menu.");
                                        break;
                                    }
                                }
                                break;
    
                            case 7:
                                LocalDate openDate;
                                while (true) {
                                    try {
                                        System.out.print("Enter new application opening date (yyyy-MM-dd): ");
                                        String dateInput = Input.getStringInput(sc);
                                        openDate = LocalDate.parse(dateInput);
                                        p.setOpenDate(openDate);
                                        break;
                                    } catch (DateTimeParseException e) {
                                        System.out.println("Error: Invalid format. Use yyyy-MM-dd.");
                                    } catch (Input.InputExitException e) {
                                        System.out.println("Operation cancelled. Returning to editing menu.");
                                        break;
                                    }
                                }
                                break;
    
                            case 8:
                                LocalDate closeDate;
                                while (true) {
                                    try {
                                        System.out.print("Enter new application closing date (yyyy-MM-dd): ");
                                        String dateInput = Input.getStringInput(sc);
                                        closeDate = LocalDate.parse(dateInput);
                                        p.setCloseDate(closeDate);
                                        break;
                                    } catch (DateTimeParseException e) {
                                        System.out.println("Error: Invalid format. Use yyyy-MM-dd.");
                                    } catch (Input.InputExitException e) {
                                        System.out.println("Operation cancelled. Returning to editing menu.");
                                        break;
                                    }
                                }
                                break;
    
                            case 9:
                                p.toggle_visibility();
                                break;
    
                            case 10:
                                int totalOfficerSlots;
                                while (true) {
                                    try {
                                        System.out.print("Enter new available number of HDB Officer Slots (MAX " + Project.getmaxOfficerSlots() + "): ");
                                        totalOfficerSlots = Input.getIntInput(sc);
                                        sc.nextLine(); // Consume newline
                                        if (totalOfficerSlots > Project.getmaxOfficerSlots() || totalOfficerSlots <= 0) {
                                            System.out.println("Error: Available officer slots must be between 1 and " + Project.getmaxOfficerSlots() + ".");
                                            continue;
                                        }
                                        if (totalOfficerSlots < p.assignedOfficers.size()) {
                                            System.out.println("Error: Cannot set slots below current assigned officers (" + p.assignedOfficers.size() + ").");
                                            continue;
                                        }
                                        p.setTotalOfficerSlots(totalOfficerSlots);
                                        break;
                                    } catch (InputMismatchException e) {
                                        System.out.println("Error: Please enter a valid number.");
                                        sc.nextLine();
                                    } catch (Input.InputExitException e) {
                                        System.out.println("Operation cancelled. Returning to editing menu.");
                                        break;
                                    }
                                }
                                break;
    
                            case 11:
                                System.out.println("---------------------------------------------------");
                                System.out.println("Exiting editing mode...");
                                loop = false;  // Exit editing mode
                                break;
    
                            default:
                                System.out.println("Error: Invalid choice. Please try again.");
                        }
                    } catch (InputMismatchException e) {
                        System.out.println("Error: Invalid input. Please try again.");
                        sc.nextLine();
                    }
                }
            }
        }
    }

    public void deleteProject(Project project) {
        managerProjects.remove(project);
        BTOsystem.projects.remove(project);

        // Unregister any officers assigned to the project
        List<HDB_Officer> officerList = BTOsystem.officers;
        // Instead of clearing a single field, remove the registration from each officer's registration list.
        for (HDB_Officer officer : officerList) {
            List<HDB_Officer.OfficerRegistration> regsToRemove = new ArrayList<>();
            for (HDB_Officer.OfficerRegistration reg : officer.getOfficerRegistrations()) {
                if (reg.getProject().equals(project)) {
                    regsToRemove.add(reg);
                }
            }
            officer.getOfficerRegistrations().removeAll(regsToRemove);
        }
        System.out.println("---------------------------------------------------");
        System.out.println("Successfully deleted.");
    }

    // Approve or reject an officer's registration.
    public void handleOfficerRegistration(Project project, HDB_Officer officer) {
        // Find the pending registration entry for the given project
        HDB_Officer.OfficerRegistration reg = officer.getRegistrationForProject(project);
        if (reg == null) {
            System.out.println("Error: Officer " + officer.get_firstname() + " " + officer.get_lastname() +
                    " did not register for Project " + project.getId() + ".");
            return;
        }
        if (!officer.isApplicationPeriodOverlapping(project)) {
            reg.setStatus("REJECTED");
            System.out.println("Officer " + officer.get_firstname() + " " + officer.get_lastname() +
                    "'s registration rejected. Officer is already assigned to another project during this application period.");
            return;
        }
        if (officer.hasAppliedAsApplicant() &&
            officer.getApplication().getProject().equals(project)) {
            reg.setStatus("REJECTED");
            System.out.println("Officer " + officer.get_firstname() + " " + officer.get_lastname() +
                    "'s registration rejected. Officer has already applied as an applicant for this project.");
            return;
        }
    
        if (project.addOfficer(officer)) {
            reg.setStatus("APPROVED");
            System.out.println("Officer " + officer.get_firstname() + " " + officer.get_lastname() +
                    "'s registration approved and assigned to Project " + project.getId() + ".");
        } else {
            reg.setStatus("REJECTED");
            System.out.println("Maximum number of officers already assigned to Project " + project.getId() +
                    ". Officer's registration rejected.");
        }
    }

    public void viewOfficerRegistration() {
        System.out.println("\n============================================================");
        System.out.println("                    OFFICER REGISTRATION");
        System.out.println("============================================================");
        
        boolean hasRegistrations = false;
        // First, check if any officer has a registration for a project managed by this manager
        for (HDB_Officer officer : BTOsystem.officers) {
            for (HDB_Officer.OfficerRegistration reg : officer.getOfficerRegistrations()) {
                if (reg.getProject() != null && reg.getProject().getManager() == this) {
                    hasRegistrations = true;
                    break;
                }
            }
            if (hasRegistrations) break;
        }
        
        if (hasRegistrations) {
            System.out.printf("%-10s %-20s %-15s %-25s%n", "ID", "Name", "Status", "Project ID");
            System.out.println("------------------------------------------------------------");
            // Iterate over all officers and their registration entries
            for (HDB_Officer officer : BTOsystem.officers) {
                for (HDB_Officer.OfficerRegistration reg : officer.getOfficerRegistrations()) {
                    if (reg.getProject() != null && reg.getProject().getManager() == this) {
                        System.out.printf("%-10s %-20s %-15s %-20s%n",
                            officer.getOfficerId(),
                            officer.get_firstname() + " " + officer.get_lastname(),
                            reg.getStatus(),
                            reg.getProject().getId());
                    }
                }
            }
        } else {
            System.out.println("No officers available.");
        }
    }    

    public void handleBTOapplication(Project project, BTOapplication application, String flatType) {
        if (!project.getFlatTypes().contains(flatType)) {
            System.out.println("Error: Invalid flat type '" + flatType + "'. Available types: " + project.getFlatTypes());
            application.setStatus("REJECTED");
            return;
        }
        int index = project.getFlatTypes().indexOf(flatType);
        if (project.getAvailableUnits().get(index) <= 0) {
            application.setStatus("REJECTED");
            System.out.println("No available " + flatType + " units. Application rejected.");
            return;
        }
        application.setStatus("SUCCESSFUL");
        System.out.println("Application ID: " + application.getId() + " for project " + project.getProjectName() + " has been approved.");
    }

    public void handleWithdrawalRequest_application(Project project, BTOapplication application, Scanner sc) {
        if (!application.getWithdrawalRequested()) {
            System.out.println("Error: Withdrawal was not requested.");
            return;
        }
    
        if (application.getStatus().equals("WITHDRAWN")) {
            System.out.println("Application has already been withdrawn.");
            return;
        }
    
        while (true) {
            System.out.println("Do you want to approve the withdrawal request? (yes/no): ");
            String input = Input.getStringInput(sc).toLowerCase();
            if (input.equals("yes") || input.equals("no")) {
                if (input.equals("yes")) {
                    int index = project.getFlatTypes().indexOf(application.getFlatType());
                    int currentAvailable = project.getAvailableUnits().get(index);
                    project.updateAvailableUnits(application.getFlatType(), currentAvailable + 1);
                    application.setStatus("WITHDRAWN");
                    // Clear the applicant's active application so they can apply again
                    application.getApplicant().application = null;
                    System.out.println("Withdrawal request approved.");
                } else {
                    System.out.println("Withdrawal request rejected.");
                }
                break;
            } else {
                System.out.println("Error: Please enter either 'yes' or 'no'.");
            }
        }
    }

    public void handleWithdrawalRequest_officer(Project project, HDB_Officer officer, Scanner sc) {
        if (!officer.getwithdrawalRequested()) {
            System.out.println("Error: Withdrawal was not requested.");
            return;
        }
        // Here, remove the registration entry corresponding to the project
        HDB_Officer.OfficerRegistration reg = officer.getRegistrationForProject(project);
        if (reg == null) {
            System.out.println("Officer is not registered for this project.");
            return;
        }
        if (reg.getStatus().equals("WITHDRAWN")) {
            System.out.println("Officer's registration has already been withdrawn.");
            return;
        }
        while (true) {
            System.out.println("Do you want to approve the withdrawal request? (yes/no): ");
            String input = Input.getStringInput(sc).toLowerCase();
            if (input.equals("yes") || input.equals("no")) {
                if (input.equals("yes")) {
                    reg.setStatus("WITHDRAWN");
                    // Remove the registration entry so the officer can apply again
                    officer.getOfficerRegistrations().remove(reg);
                    System.out.println("Withdrawal request approved.");
                } else {
                    System.out.println("Withdrawal request rejected.");
                }
                break;
            } else {
                System.out.println("Error: Please enter either 'yes' or 'no'.");
            }
        }
    }

    public void generateReport(Scanner sc) {
        List<BTOapplication> applicationList = BTOsystem.applications;
        if (applicationList.isEmpty()) {
            System.out.println("No applications available.");
            return;
        }
        System.out.printf("%-10s %-20s %-15s %-25s%n", "ID", "Name", "Status", "Project ID");
        System.out.println("------------------------------------------------------------");
        for (BTOapplication application : applicationList) {
            Project assignedProject = application.getProject();
            if (assignedProject != null && assignedProject.getManager() == this) {
                System.out.printf("%-10s %-20s %-15s %-20s%n",
                        application.getId(),
                        application.getApplicant().get_firstname() + " " + application.getApplicant().get_lastname(),
                        application.getStatus(),
                        assignedProject.getId());
            }
        }
        menu.printReportMenu();
        int choice = Input.getIntInput(sc);
        switch (choice) {
            case 1:
                printApplicationList(applicationList);
                break;
            case 2:
                System.out.print("Enter marital status (Single/Married): ");
                String status = Input.getStringInput(sc).trim().toLowerCase();
                List<BTOapplication> filteredByMarital = new ArrayList<>();
                for (BTOapplication app : applicationList) {
                    String applicantStatus = app.getApplicant().get_maritalstatus().toLowerCase();
                    if (applicantStatus.equals(status)) {
                        filteredByMarital.add(app);
                    }
                }
                printApplicationList(filteredByMarital);
                break;
            case 3:
                System.out.print("Enter flat type (2-Room/3-Room): ");
                String flatType = Input.getStringInput(sc).trim();
                List<BTOapplication> filteredByFlat = new ArrayList<>();
                for (BTOapplication app : applicationList) {
                    if (app.getFlatType().equalsIgnoreCase(flatType)) {
                        filteredByFlat.add(app);
                    }
                }
                printApplicationList(filteredByFlat);
                break;
            case 4:
                System.out.print("Enter marital status (Single/Married): ");
                status = Input.getStringInput(sc).trim().toLowerCase();
                System.out.print("Enter flat type (2-Room/3-Room): ");
                flatType = Input.getStringInput(sc).trim();
                List<BTOapplication> filteredByBoth = new ArrayList<>();
                for (BTOapplication app : applicationList) {
                    String applicantStatus = app.getApplicant().get_maritalstatus().toLowerCase();
                    if (applicantStatus.equals(status) && app.getFlatType().equalsIgnoreCase(flatType)) {
                        filteredByBoth.add(app);
                    }
                }
                printApplicationList(filteredByBoth);
                break;
            case 5:
                System.out.println("Returning...");
                return;
            default:
                System.out.println("Invalid choice. Please try again.");
        }
    }

    private void printApplicationList(List<BTOapplication> apps) {
        if (apps.isEmpty()) {
            System.out.println("No matching applications found.");
            return;
        }
        System.out.printf("%-20s %-5s %-15s %-10s %-15s%n", "Applicant Name", "Age", "Marital Status", "Flat", "Project");
        for (BTOapplication app : apps) {
            ApplicantBase applicant = app.getApplicant();
            String fullName = applicant.get_firstname() + " " + applicant.get_lastname();
            int age = applicant.get_age();
            String marital = applicant.get_maritalstatus();
            String flat = app.getFlatType();
            String project = app.getProjectName();
            System.out.printf("%-20s %-5d %-15s %-10s %-15s%n", fullName, age, marital, flat, project);
        }
    }

    public void reply_enquiry(Enquiry enquiry, String response) {
        if (enquiry == null) {
            System.out.println("Error: Enquiry not found.");
            return;
        }
        if (response == null || response.trim().isEmpty()) {
            System.out.println("Error: Response cannot be empty.");
            return;
        }
        enquiry.setResponse(response);
        enquiry.setStaffReply(this);
        System.out.println("Successfully replied to enquiry ID: " + enquiry.getId());
    }

    public void viewAllEnquiries(Scanner sc) {
        System.out.println("\n============================================");
        System.out.println("              ALL ENQUIRIES");
        System.out.println("============================================");
        if (BTOsystem.enquiries.isEmpty()) {
            System.out.println("No enquiries found in the system.");
            return;
        }
        System.out.println("Filter options:");
        System.out.println("1. View all enquiries");
        System.out.println("2. View only general (non-project) enquiries");
        System.out.println("3. View only enquiries for my projects");
        int choice = 0;
        try {
            System.out.print("Enter your choice: ");
            choice = Input.getIntInput(sc);
        } catch (InputMismatchException e) {
            System.out.println("Invalid input. Showing all enquiries.");
            choice = 1;
            sc.nextLine();
        }
        List<Enquiry> filteredEnquiries = new ArrayList<>();
        switch (choice) {
            case 1:
                filteredEnquiries = new ArrayList<>(BTOsystem.enquiries);
                System.out.println("Showing all enquiries in the system");
                break;
            case 2:
                for (Enquiry e : BTOsystem.enquiries) {
                    if (e.getProject() == null) {
                        filteredEnquiries.add(e);
                    }
                }
                System.out.println("Showing general (non-project) enquiries only");
                break;
            case 3:
                for (Project p : managerProjects) {
                    for (Enquiry e : p.getEnquiries()) {
                        filteredEnquiries.add(e);
                    }
                }
                System.out.println("Showing enquiries for your projects only");
                break;
            default:
                filteredEnquiries = new ArrayList<>(BTOsystem.enquiries);
                System.out.println("Invalid choice. Showing all enquiries.");
        }
        if (filteredEnquiries.isEmpty()) {
            System.out.println("No enquiries match your filter criteria.");
            return;
        }
        System.out.printf("%-5s %-20s %-20s %-30s %-15s%n", "ID", "Project", "Created By", "Enquiry", "Status");
        System.out.println("------------------------------------------------------------------------------------");
        for (Enquiry enquiry : filteredEnquiries) {
            User creator = enquiry.getCreatedByUser();
            String creatorName = creator.get_firstname() + " " + creator.get_lastname();
            System.out.printf("%-5d %-20s %-20s %-30s %-15s%n",
                    enquiry.getId(),
                    (enquiry.getProject() != null ? enquiry.getProject().getProjectName() : "General Enquiry"),
                    creatorName,
                    Input.truncateText(enquiry.getEnquiry(), 30),
                    enquiry.getResponse().isEmpty() ? "Pending" : "Answered");
            if (enquiry.getflatType() != null && !enquiry.getflatType().isEmpty()) {
                System.out.println("   Flat Type: " + enquiry.getflatType());
            }
            if (!enquiry.getResponse().isEmpty()) {
                System.out.println("   Response: " + Input.truncateText(enquiry.getResponse(), 50));
                if (enquiry.getStaff() != null) {
                    System.out.println("   Replied by: " + enquiry.getStaff().get_firstname() + " " +
                            enquiry.getStaff().get_lastname());
                }
            }
        }
    }

    public void handleProjectEnquiries(Scanner sc) {
        System.out.println("\n============================================");
        System.out.println("            HANDLE ENQUIRIES");
        System.out.println("============================================");
        System.out.println("1. Handle project-specific enquiries");
        System.out.println("2. Handle general (non-project) enquiries");
        int typeChoice;
        try {
            System.out.print("Enter your choice: ");
            typeChoice = Input.getIntInput(sc);
        } catch (InputMismatchException e) {
            System.out.println("Invalid input. Defaulting to project-specific enquiries.");
            typeChoice = 1;
            sc.nextLine();
        }
        if (typeChoice == 1) {
            handleProjectSpecificEnquiries(sc);
        } else if (typeChoice == 2) {
            handleGeneralEnquiries(sc);
        } else {
            System.out.println("Invalid choice. Returning to main menu.");
        }
    }

    private void handleProjectSpecificEnquiries(Scanner sc) {
        List<Project> projectsWithEnquiries = new ArrayList<>();
        for (Project p : managerProjects) {
            boolean hasPendingEnquiries = false;
            for (Enquiry e : p.getEnquiries()) {
                if (e.getResponse().isEmpty()) {
                    hasPendingEnquiries = true;
                    break;
                }
            }
            if (hasPendingEnquiries) {
                projectsWithEnquiries.add(p);
            }
        }
        if (projectsWithEnquiries.isEmpty()) {
            System.out.println("No pending enquiries for your projects.");
            return;
        }
        System.out.println("Select a project to view its pending enquiries:");
        printProjectTable(projectsWithEnquiries);
        int projChoice = -1;
        try {
            System.out.print("Enter project index: ");
            projChoice = Input.getIntInput(sc);
        } catch (InputMismatchException e) {
            System.out.println("Invalid input. Returning to menu.");
            sc.nextLine();
            return;
        }
        if (projChoice < 0 || projChoice >= projectsWithEnquiries.size()) {
            System.out.println("Invalid project index. Returning to menu.");
            return;
        }
        Project selectedProject = projectsWithEnquiries.get(projChoice);
        System.out.println("Pending enquiries for " + selectedProject.getProjectName() + ":");
        for (Enquiry e : selectedProject.getEnquiries()) {
            System.out.printf("ID: %d, Question: %s%s%n", e.getId(), Input.truncateText(e.getEnquiry(), 30),
                    (e.getStaff() != null ? " (Replied)" : ""));
        }
        int enquiryChoice;
        try {
            System.out.print("Select an enquiry to reply to (or enter -1 to cancel): ");
            enquiryChoice = Input.getIntInput(sc);
        } catch (InputMismatchException e) {
            System.out.println("Invalid input. Returning to menu.");
            sc.nextLine();
            return;
        }
        if (enquiryChoice == -1) {
            System.out.println("Operation cancelled.");
            return;
        }
        Enquiry selected = BTOsystem.searchById(selectedProject.getEnquiries(), enquiryChoice, Enquiry::getId);
        if (selected == null) {
            System.out.println("Invalid index.");
            return;
        }
        selected.display();
        System.out.print("Enter your reply: ");
        String reply = Input.getStringInput(sc);
        reply_enquiry(selected, reply);
        System.out.println("Reply submitted successfully.");
    }

    private void handleGeneralEnquiries(Scanner sc) {
        List<Enquiry> generalEnquiries = new ArrayList<>();
        for (Enquiry e : BTOsystem.enquiries) {
            if (e.getProject() == null && e.getResponse().isEmpty()) {
                generalEnquiries.add(e);
            }
        }
        if (generalEnquiries.isEmpty()) {
            System.out.println("No pending general enquiries.");
            return;
        }
        System.out.println("\nPending general enquiries:");
        printApplicationTableForEnquiries(generalEnquiries); // reusing a helper for enquiries
        int enquiryChoice;
        try {
            System.out.print("Select an enquiry to reply to (or enter -1 to cancel): ");
            enquiryChoice = Input.getIntInput(sc);
        } catch (InputMismatchException e) {
            System.out.println("Invalid input. Returning to menu.");
            sc.nextLine();
            return;
        }
        if (enquiryChoice == -1) {
            System.out.println("Operation cancelled.");
            return;
        }
        if (enquiryChoice < 0 || enquiryChoice > generalEnquiries.size()) {
            System.out.println("Invalid enquiry selection.");
            return;
        }
        Enquiry selectedEnquiry = generalEnquiries.get(enquiryChoice - 1); // Adjusting for numbering
        System.out.println("Selected enquiry: " + selectedEnquiry.getEnquiry());
        System.out.print("Enter your reply: ");
        String reply = Input.getStringInput(sc);
        reply_enquiry(selectedEnquiry, reply);
    }

    // New helper method to print a table of projects for selection
    private void printProjectTable(List<Project> projects) {
        System.out.printf("%-5s %-10s %-25s %-15s %-12s %-12s%n", "Idx", "ID", "Project Name", "Neighbourhood", "Open Date", "Close Date");
        System.out.println("--------------------------------------------------------------------------------");
        for (int i = 0; i < projects.size(); i++) {
            Project p = projects.get(i);
            System.out.printf("%-5d %-10d %-25s %-15s %-12s %-12s%n",
                    i,
                    p.getId(),
                    p.getProjectName(),
                    p.getneighbourhood(),
                    p.getOpenDate(),
                    p.getCloseDate());
        }
    }

    // New helper method to print a table of officers for selection
    private void printOfficerTable(List<HDB_Officer> officers) {
        System.out.printf("%-5s %-10s %-25s%n", "Idx", "OfficerID", "Name");
        System.out.println("-----------------------------------------------------");
        for (int i = 0; i < officers.size(); i++) {
            HDB_Officer o = officers.get(i);
            System.out.printf("%-5d %-10d %-25s%n",
                    i,
                    o.getOfficerId(),
                    o.get_firstname() + " " + o.get_lastname());
        }
    }

    // New helper method to print a table of applications for selection
    private void printApplicationTable(List<BTOapplication> applications) {
        System.out.printf("%-5s %-10s %-25s %-15s %-10s%n", "Idx", "AppID", "Applicant Name", "Status", "ProjectID");
        System.out.println("-----------------------------------------------------------------------");
        for (int i = 0; i < applications.size(); i++) {
            BTOapplication app = applications.get(i);
            System.out.printf("%-5d %-10d %-25s %-15s %-10d%n",
                    i,
                    app.getId(),
                    app.getApplicant().get_firstname() + " " + app.getApplicant().get_lastname(),
                    app.getStatus(),
                    app.getProject().getId());
        }
    }
    
    // New helper method to print enquiries in a table format (for general enquiries)
    private void printApplicationTableForEnquiries(List<Enquiry> enquiries) {
        System.out.printf("%-5s %-10s %-25s%n", "Idx", "EnqID", "Enquiry Preview");
        System.out.println("-----------------------------------------------------");
        for (int i = 0; i < enquiries.size(); i++) {
            Enquiry enq = enquiries.get(i);
            System.out.printf("%-5d %-10d %-25s%n",
                    i,
                    enq.getId(),
                    Input.truncateText(enq.getEnquiry(), 25));
        }
    }
}
