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
                menu.printManagerMenu();
                
                choice = Input.getIntInput(sc);
                
                switch (choice) {
                    case 1:     // Create a new project via keyboard input
                        createProject(sc);
                        break;
    
                    case 2:     // Edit a Project
                        System.out.print("Enter the ID of the project you wish to edit: ");
                        Project projectToEdit = findAndValidateProject(sc);
                        if (projectToEdit != null) {
                            editProject(projectToEdit, sc);
                        }
                        break;
                        
                    case 3: // Delete a Project
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
                        System.out.print("Enter the Project ID to manage officer registrations: ");
                        Project projectForOfficer = findAndValidateProject(sc);
                        if (projectForOfficer == null) break;
                        List<HDB_Officer> pendingOfficers = new ArrayList<>();
                        for (HDB_Officer o : BTOsystem.officers) {
                            if (o.officerProject != null &&
                                    o.officerProject.equals(projectForOfficer) &&
                                    o.registrationStatus.equals("PENDING")) {
                                pendingOfficers.add(o);
                            }
                        }
                        if (pendingOfficers.isEmpty()) {
                            System.out.println("No pending officer registrations for this project.");
                            break;
                        }
                        System.out.println("Pending officer registrations:");
                        for (int i = 0; i < pendingOfficers.size(); i++) {
                            System.out.println("[" + i + "] Officer ID: " + pendingOfficers.get(i).getOfficerId() +
                                               ", Name: " + pendingOfficers.get(i).get_firstname());
                        }
                        System.out.print("Enter index of officer to review: ");
                        int idx = Input.getIntInput(sc);
                        if (idx < 0 || idx >= pendingOfficers.size()) {
                            System.out.println("Invalid index.");
                            break;
                        }
                        HDB_Officer officerToHandle = pendingOfficers.get(idx);
                        handleOfficerRegistration(projectForOfficer, officerToHandle);
                        break;
                        
                    case 8:     // Handle officer withdrawal requests
                        System.out.print("Enter the Project ID to manage: ");
                        Project projectForWithdrawal_o = findAndValidateProject(sc);
                        if(projectForWithdrawal_o == null) break;

                        System.out.print("Enter Officer's ID: ");
                        int withdrawalOfficerId = Input.getIntInput(sc);
                        HDB_Officer officer_withdrawal = null;
                        for (HDB_Officer o : BTOsystem.officers) {
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
                        System.out.print("Enter the Project ID to manage: ");
                        Project projectForBTO = findAndValidateProject(sc);
                        if(projectForBTO == null) break;
                        System.out.print("Enter Applicant's ID: ");
                        int applicationId = Input.getIntInput(sc);
                        String flatType = null;
                        BTOapplication application = null;
                        for (BTOapplication app : BTOsystem.applications) {
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
                            }
                        } else {
                            System.out.println("Error: Application not found.");
                        }
                        break;
                        
                    case 10:    // Handle application withdrawal requests
                        System.out.print("Enter the Project ID to manage: ");
                        Project projectForWithdrawal_a = findAndValidateProject(sc);
                        if(projectForWithdrawal_a == null) break;
                        System.out.print("Enter application ID: "); 
                        int withdrawalApplicationId = Input.getIntInput(sc);
                        BTOapplication withdrawalApplication = null;
                        for (BTOapplication app : BTOsystem.applications) {
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
                }
            } catch (InputMismatchException e) {
                System.out.println("---------------------------------------------------");
                System.out.println("Error: Invalid input. Please try again.");
                System.out.println("---------------------------------------------------");
                sc.nextLine();  // Consume leftover newline
            }
        }
    }

    // Find and validate project by ID ensuring that the manager is responsible for it.
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

    // Display current flat types for a project
    public void FlatTypesMenu(Project p) {
        System.out.println("\n============================================");
        System.out.println("            CURRENT FLAT TYPES");
        System.out.println("============================================");
        System.out.printf("%-5s %-12s %-13s %-15s %-10s%n", "No.", "Flat Type", "Prices", "Total Units", "Available");
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

    // Create a new project
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
                    if (units >= 0) break;
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
                    if (price >= 0) break;
                    System.out.println("Error: Price cannot be negative.");
                } catch (InputMismatchException e) {
                    System.out.println("Error: Please enter a valid number.");
                }
            }
            flatTypes.add(flatType);
            totalUnits.add(units);
            availableUnits.add(units); // Initially available equals total
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
        // Open date input with exit handling
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
        // Close date input
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
        // Check for project overlap with manager's existing projects
        for (Project existingProject : managerProjects) {
            LocalDate existingOpen = existingProject.getOpenDate();
            LocalDate existingClose = existingProject.getCloseDate();
            if (!(closeDate.isBefore(existingOpen) || openDate.isAfter(existingClose))) {
                System.out.println("Error: Failed to create " + projectName + ". You can only manage one project within an application period.");
                return null;
            }
        }
        // Create and register the new project
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
                if (p.getManager() == this) { // Verify that the manager is in charge
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

    // Edit project attributes with proper try/catch handling for exit/back commands.
    public void editProject(Project project, Scanner sc) {
        int option = 0;
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
                                currentType = null;  // Declare currentType variable
                                while (true) {
                                    try {
                                        System.out.print("\nSelect flat type to edit price (No. 1-" + p.getFlatTypes().size() + "): ");
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
                                // If the operation was cancelled, currentType might be null. Check before proceeding.
                                if (currentType == null) {
                                    break;
                                }
                                double newPrice;
                                while (true) {
                                    try {
                                        System.out.print("Enter new price for " + currentType + ": ");
                                        newPrice = Input.getIntInput(sc);
                                        if (newPrice >= 0) {
                                            System.out.println(currentType + " updated successfully.");
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
                                loop = false;
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
        List<HDB_Officer> officersToRemove = new ArrayList<>();
        for (HDB_Officer officer : officerList) {
            if (officer.officerProject != null && officer.officerProject.equals(project)) {
                officer.officerProject = null;
                officer.registrationStatus = "UNREGISTERED";
                officersToRemove.add(officer);
            }
        }
        officerList.removeAll(officersToRemove);
        System.out.println("---------------------------------------------------");
        System.out.println("Successfully deleted.");
    }
    
    // Approve or reject an officer's registration.
    public void handleOfficerRegistration(Project project, HDB_Officer officer) {
        if (!project.equals(officer.officerProject)) {
            System.out.println("Error: Officer " + officer.get_firstname() + " " + officer.get_lastname() +
                    " did not register for Project " + project.getId() + ".");
            return;
        }
        if (officer.isApplicationPeriodOverlapping(project)) {
            officer.registrationStatus = "REJECTED";
            System.out.println("Officer " + officer.get_firstname() + " " + officer.get_lastname() +
                    "'s registration rejected. Officer is already assigned to another project during this application period.");
            return;
        }
        if (officer.hasAppliedAsApplicant()) {
            officer.registrationStatus = "REJECTED";
            System.out.println("Officer " + officer.get_firstname() + " " + officer.get_lastname() +
                    "'s registration rejected. Officer has already applied as an applicant.");
            return;
        }
        if (project.addOfficer(officer)) {
            officer.registrationStatus = "APPROVED";
            officer.officerProject = project;
            System.out.println("Officer " + officer.get_firstname() + " " + officer.get_lastname() +
                    "'s registration approved and assigned to Project " + project.getId() + ".");
        } else {
            officer.registrationStatus = "REJECTED";
            System.out.println("Maximum number of officers already assigned to Project " + project.getId() +
                    ". Officer's registration rejected.");
        }
    }
    
    // View officer registrations under the manager.
    public void viewOfficerRegistration() {
        System.out.println("\n============================================================");
        System.out.println("                    OFFICER REGISTRATION");
        System.out.println("============================================================");
        boolean hasOfficers = false;
        for (Project project : this.managerProjects) {
            if (!project.assignedOfficers.isEmpty()) {
                hasOfficers = true;
                break;
            }
        }
        if (hasOfficers) {
            System.out.printf("%-10s %-20s %-15s %-25s%n", "ID", "Name", "Status", "Project ID");
            System.out.println("------------------------------------------------------------");
            for (HDB_Officer officer : BTOsystem.officers) {
                Project assignedProject = officer.officerProject;
                if (assignedProject != null && assignedProject.getManager() == this) {
                    System.out.printf("%-10s %-20s %-15s %-20s%n", 
                            officer.getOfficerId(), 
                            officer.get_firstname() + " " + officer.get_lastname(),
                            officer.registrationStatus,
                            assignedProject.getId());
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
        if (officer.registrationStatus.equals("WITHDRAWN")) {
            System.out.println("Officer's registration has already been withdrawn.");
            return;
        }
        while (true) {
            System.out.println("Do you want to approve the withdrawal request? (yes/no): ");
            String input = Input.getStringInput(sc).toLowerCase();
            if (input.equals("yes") || input.equals("no")) {
                if (input.equals("yes")) {
                    officer.registrationStatus = "WITHDRAWN";
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
            Applicant applicant = app.getApplicant();
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
                    truncateText(enquiry.getEnquiry(), 30),
                    enquiry.getResponse().isEmpty() ? "Pending" : "Answered");
            if (enquiry.getflatType() != null && !enquiry.getflatType().isEmpty()) {
                System.out.println("   Flat Type: " + enquiry.getflatType());
            }
            if (!enquiry.getResponse().isEmpty()) {
                System.out.println("   Response: " + truncateText(enquiry.getResponse(), 50));
                if (enquiry.getStaff() != null) {
                    System.out.println("   Replied by: " + enquiry.getStaff().get_firstname() + " " + enquiry.getStaff().get_lastname());
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
        for (int i = 0; i < projectsWithEnquiries.size(); i++) {
            System.out.printf("[%d] %s%n", i, projectsWithEnquiries.get(i).getProjectName());
        }
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
            System.out.printf("ID: %d, Question: %s%s%n", e.getId(), truncateText(e.getEnquiry(), 30), 
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
        for (int i = 0; i < generalEnquiries.size(); i++) {
            Enquiry e = generalEnquiries.get(i);
            User creator = e.getCreatedByUser();
            System.out.println((i + 1) + ". From: " + creator.get_firstname() + " " + 
                    creator.get_lastname() + " | ID: " + e.getId());
            System.out.println("   Enquiry: " + e.getEnquiry());
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
        if (enquiryChoice < 0 || enquiryChoice >= generalEnquiries.size()) {
            System.out.println("Invalid enquiry selection.");
            return;
        }
        Enquiry selectedEnquiry = generalEnquiries.get(enquiryChoice);
        System.out.println("Selected enquiry: " + selectedEnquiry.getEnquiry());
        System.out.print("Enter your reply: ");
        String reply = Input.getStringInput(sc);
        reply_enquiry(selectedEnquiry, reply);
    }
}
