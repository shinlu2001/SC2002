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
    private String type="MANAGER";

    //protected static List<Project> allProjects = new ArrayList<>();  // Static list to store all projects - for manager to view all projects
    protected List<Project> managerProjects = new ArrayList<>();  // List to store own projects - for manager to view own projects
    
    // static Scanner scan = new Scanner(System.in);
    public HDB_Manager(String nric, String firstname, String lastname, String marital_status, int age) {
        super(nric, firstname, lastname, marital_status, age);
        this.managerProjects = new ArrayList<>();
        // this.projects = projects;  // Initialize the projects list
        manager_id = ++nextId;
    }
    public void to_string() {
        super.to_string();
        System.out.println("Account type: " + type);
        System.out.println("ManagerID: " + manager_id);
    }
    // public List<Project> getManagerProjects() {
    //     return managerProjects;
    // }
    
    // public static List<Project> getAllProjects() {
    //     return allProjects;
    // }
    public void start_menu(Scanner sc) {
        System.out.println("Welcome to HDB BTO Management System, " + this.get_firstname() + "!");

        int choice = 0;
        boolean loop = true;  // Control variable for menu loop

        while (loop) {
        {
            try {
                
                System.out.println("============================================");
                System.out.println("         M A N A G E R   M E N U");
                System.out.println("============================================");
                menu.printManagerMenu();
                
                

                choice = Input.getIntInput(sc);
                
                
                switch (choice) {
                    case 1:     // create a new project based by keying input in
                        
                        createProject(sc);

                        System.out.println("---------------------------------------------------");
                        break;
    
                    case 2:     // Edit a Project - editProject(project)
                        System.out.print("Enter the ID of the project you wish to edit: ");
                        
                        // find if project exists in allProjects then check if the project is under the current manager
                        Project projectToEdit = findAndValidateProject(sc);
                        if (projectToEdit != null) {
                            editProject(projectToEdit, sc);
                        }
                        
                        System.out.println("---------------------------------------------------");
                        break;
                        
                    case 3: // Delete a Project - deleteProject(project)
                        System.out.print("Enter the ID of the project you wish to delete: ");
                        
                        // find if project exists in allProjects then check if the project is under the current manager
                        Project projectToDelete = findAndValidateProject(sc);
                        if (projectToDelete != null) {
                            deleteProject(projectToDelete);
                        }                           
                    
                        System.out.println("---------------------------------------------------");
                        break;

                        case 4:     // view all projects
                            viewAllProjects();
                            System.out.println("---------------------------------------------------");
                            break;
                        
                        case 5:     // view own projects
                            viewOwnProjects();
                            System.out.println("---------------------------------------------------");
                            break;
                        
                        case 6:     // view officer registration
                            viewOfficerRegistration();
                            System.out.println("---------------------------------------------------");
                            break;
                        
                        case 7:     // handle Officer Registration
                            System.out.print("Enter the Project ID to manage officer registrations: ");
                            Project projectForOfficer = findAndValidateProject(sc);
                            if (projectForOfficer == null)
                                break;
                            // Display pending officer registrations for the selected project
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
                        case 8:     // handle withdrawel requests - handleWithdrawalRequest_officer(project, officer, sc)
                            System.out.print("Enter the Project ID to manage: ");
                            // find if project exists in allProjects then check if the project is under the current manager
                            Project projectForWithdrawal_o = findAndValidateProject(sc);
                            if(projectForWithdrawal_o == null)
                                break;

                            System.out.print("Enter Officer's ID: ");
                            int withdrawalofficerId = Input.getIntInput(sc);
                            

                            HDB_Officer officer_withdrawal = null;
                            // Find the officer by their ID
                            for (HDB_Officer o : BTOsystem.officers) {
                                if (o.getOfficerId() == withdrawalofficerId) {
                                    officer_withdrawal = o;
                                    break;
                                }
                            }
                        
                            if (officer_withdrawal != null) {
                                handleWithdrawalRequest_officer(projectForWithdrawal_o, officer_withdrawal, sc);

                            } else {
                                System.out.println("Error: Officer not found.");
                            }

                    
                            // System.out.println("---------------------------------------------------");
                            break;  
                        case 9:     // handle BTO registration - handleBTOapplication(projectToEdit, application, type)
                            System.out.print("Enter the Project ID to manage: ");
                            // find if project exists in allProjects then check if the project is under the current manager
                            Project projectForBTO = findAndValidateProject(sc);
                            if(projectForBTO == null)
                                break;

                            System.out.print("Enter Applicant's ID: ");
                            int applicationId = Input.getIntInput(sc);
                            

                            String flatType = null;
                            BTOapplication application = null;
                            for (BTOapplication a : BTOsystem.applications)
                            {
                                if (a.getId() == applicationId){
                                    application = a;
                                    flatType = a.getFlatType();
                                    break;
                                }
                            }

                            if (application != null){
                                System.out.println("Do you want to approve this application? Enter 'y/n': ");
                                String confirm = Input.getStringInput(sc);
                                if (confirm.equals('y')) {
                                handleBTOapplication(projectForBTO, application, flatType);
                                }
                            }else {
                                System.out.println("Error: Application not found.");
                            }
                            
                            // System.out.println("---------------------------------------------------");
                            break;
                        
                        case 10:     // handle withdrawel requests - handleWithdrawalRequest_application(project, application, sc)
                            System.out.print("Enter the Project ID to manage: ");
                            // find if project exists in allProjects then check if the project is under the current manager
                            Project projectForWithdrawal_a = findAndValidateProject(sc);
                            if(projectForWithdrawal_a == null)
                                break;

                            System.out.print("Enter application ID: "); 
                            int withdrawalApplicationId = Input.getIntInput(sc);
                            

                            BTOapplication  withdrawalApplication = null;
                            for (BTOapplication a : BTOsystem.applications)
                            {
                                if (a.getId() == withdrawalApplicationId){
                                    withdrawalApplication = a;
                                    break;
                                }
                            }

                            if (withdrawalApplication != null){
                                handleWithdrawalRequest_application(projectForWithdrawal_a, withdrawalApplication, sc);
                            }else {
                                System.out.println("Error: Application not found.");
                            }

                    
                            // System.out.println("---------------------------------------------------");
                            break;
                        
                        case 11:     // generate appplicant report - generateReport(applicationList)
                            generateReport(sc);
                            System.out.println("---------------------------------------------------");
                            break;
                        
                        case 12:     // view all enquiries (across all projects)
                            viewAllEnquiries(sc);
                            System.out.println("---------------------------------------------------");
                            break;
                        
                        case 13:     // handle project enquires (view and reply to enquiries for your projects)
                            handleProjectEnquiries(sc);
                            System.out.println("---------------------------------------------------");
                            break;
                        case 14:     //view manager account details
                            System.out.println("---------------------------------------------------");
                            to_string();
                            System.out.println("---------------------------------------------------");
                            break;
                        case 15:
                            System.out.println("---------------------------------------------------");
                            System.out.println("Logged out. Returning to main menu...");
                            System.out.println("---------------------------------------------------");
                            loop = false;  // Exit the loop
                            break;
                    default:    // executed when input entered is some int that is not btw 1-13
                        System.out.println("---------------------------------------------------");
                        System.out.println("Error: Invalid choice. Please try again.");
                        System.out.println("---------------------------------------------------");
                
                }
                // sc.nextLine();
            } catch (InputMismatchException e) {    // executed when input entered is any type of input (str,char, etc)
                System.out.println("---------------------------------------------------");
                System.out.println("Error: Invalid input. Please try again.");
                System.out.println("---------------------------------------------------");
                sc.nextLine();  // Consume leftover newline
                }
            }
        }
    }
    // find if project exists in allProjects then check if the project is under the current manager
    private Project findAndValidateProject(Scanner sc) {
        int projectID = Input.getIntInput(sc);
        
        
        // Find project in allProjects
        Project project = null;
        // for (Project p : allProjects) {
        for (Project p : BTOsystem.projects) {
            if (p.getId() == projectID) {
                project = p;
                break;
            }
        }

        // String projectName = sc.nextLine();
        
        // // Find project in allProjects
        // Project project = null;
        // for (Project p : allProjects) {
        //     if (p.getProjectName().equalsIgnoreCase(projectName)) {
        //         project = p;
        //         break;
        //     }
        // }
        
        // Validate project
        if (project == null) {
            System.out.println("---------------------------------------------------");
            System.out.println("Error: Project does not exist.");
            return null;
        }
        // check if current manager is in charge of the project
        if (!managerProjects.contains(project)) {
            System.out.println("---------------------------------------------------");
            System.out.println("Error: You are not the manager of Project " + project.getId() + ".");
            return null;
        }
        
        return project;
    }

    public void FlatTypesMenu(Project p)
    {
        System.out.println("\n============================================");
        System.out.println("            CURRENT FLAT TYPES");
        System.out.println("============================================");
        System.out.printf("%-5s %-12s %-15s %-10s%n", "No.", "Flat Type", "Total Units", "Available");
        System.out.println("--------------------------------------------");
        for (int i = 0; i < p.getFlatTypes().size(); i++) {
            System.out.printf("%-5s %-12s %-15s %-10s%n", i+1, p.getFlatTypes().get(i), p.getTotalUnits().get(i), p.getAvailableUnits().get(i));
            
        }
    }
    // public Project createProject(String projectName, String neighbourhood, int total2Room, int total3Room, LocalDate openDate, LocalDate closeDate, boolean visibility, int availableOfficerSlots) {
    public Project createProject(Scanner sc) 
    {
        //change createProject to load in flat type instead of total2room etc
        // then from there (flat type) use conditions to input the necessary variables into the Project parameters
        System.out.println("\n============================================");
        System.out.println("            CREATING A PROJECT");
        System.out.println("============================================");
        System.out.print("Enter Project Name: ");
        String projectName = Input.getStringInput(sc);

        System.out.print("Enter Neighbourhood: ");
        String neighbourhood = Input.getStringInput(sc);

        // System.out.print("Enter Flat Type (2-Room/3-Room):");
        // // String flatType = scan.next().toLowerCase();
        // String flatType = scan.next();

        // System.out.print("Enter Number of Units for " + flatType + ".");
        // int numberUnits = scan.nextInt();

        // int total2Room = 0, total3Room = 0;
        // if (flatType.equals("2-Room")){
        //     total2Room = numberUnits;
        //     total3Room = 0;                                
        // }
        // else if (flatType.equals("3-Room")){
        //     total2Room = 0;
        //     total3Room = numberUnits;                                
        // }
        // List to store all flat types and their details
        List<String> flatTypes = new ArrayList<>();
        List<Integer> totalUnits = new ArrayList<>();
        List<Integer> availableUnits = new ArrayList<>();

        // Get flat types and units dynamically
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
                    sc.nextLine(); // Clear invalid input
                }
            }

            flatTypes.add(flatType);
            totalUnits.add(units);
            availableUnits.add(units); // Initially available equals total
            while (true){
                System.out.print("Add another flat type? (yes/no): ");
                String response = sc.nextLine().toLowerCase();
                if (response.equals("yes") || response.equals("no")) {
                    if (response.equals("no")) {
                        addMoreFlats = false;
                    }
                    break;
                } else {
                    System.out.println("Error: Please enter either 'yes' or 'no'.");
                }
            }
        }
        // // Continuously loop until a valid (positive) input is entered
        // int total2Room;
        // while (true)
        // {
        //     try {
        //         System.out.print("Enter number of 2-Room units (none = 0): ");
        //         // int total2Room = scan.nextInt();
        //         total2Room = (Input.getIntInput(sc));
        //         sc.nextLine(); // Consume newline

        //         if (total2Room >= 0) // if input is positive no. = valid, so break the loop and proceed
        //             break;

        //         // else output error msg and continue looping
        //         System.out.println("Error: Number of 2-Room units cannot be negative.");
        //     } catch (InputMismatchException e) {
        //         System.out.println("Error: Please enter a valid number.");
        //         sc.nextLine(); // Clear invalid input
        //     }

        // }

        // // Continuously loop until a valid (positive) input is entered
        // int total3Room;
        // while (true)
        // {
        //     try {
        //         System.out.print("Enter number of 3-Room units (none = 0): ");
        //         total3Room = Input.getIntInput(sc);
        //         sc.nextLine(); // Consume newline
                
        //         if (total3Room >= 0) // if input is positive no. = valid, so break the loop and proceed
        //             break;

        //         // else output error msg and continue looping
        //         System.out.println("Error: Number of 3-Room units cannot be negative.");
        //     } catch (InputMismatchException e) {
        //         System.out.println("Error: Please enter a valid number.");
        //         sc.nextLine(); // Clear invalid input
        //     }
        // }
        
        // Continuously loop until a valid (date format) input is entered
        LocalDate openDate = null;
        while(true){
            try {
                System.out.print("Enter Application Opening Date for Project (yyyy-MM-dd): ");
                openDate = LocalDate.parse(sc.next());  // Convert string to LocalDate
                break;

            } catch (DateTimeParseException e) {
                System.out.println("Error: Invalid format. Use yyyy-MM-dd.");
            }
        }

        // Continuously loop until a valid (date format) input is entered
        LocalDate closeDate;
        while(true){
            try {
                System.out.print("Enter  Application Closing Date for Project (yyyy-MM-dd): ");
                closeDate = LocalDate.parse(sc.next());  // Convert string to LocalDate
                break;
            } catch (DateTimeParseException e) {
                System.out.println("Error: Invalid format. Use yyyy-MM-dd.");
            }
        }
        // Continuously loop until a valid (availableOfficerSlots input btw 1-10) input is entered
        int totalOfficerSlots;
        while (true)
        {   try{
                System.out.print("Enter Available HDB Officer Slots (MAX " + Project.getmaxOfficerSlots() + "): ");
                totalOfficerSlots = Input.getIntInput(sc);
                sc.nextLine(); // Consume newline

                // If input(availableOfficerSlots) is NOT btw 1-10, keep looping
                if (totalOfficerSlots > Project.getmaxOfficerSlots() || totalOfficerSlots <= 0 ) {
                    System.out.println("Error: Available officer slots must be between 1 and " + Project.getmaxOfficerSlots() + ".");

                }else{  // if input is btw of 1-10 break the loop and proceed
                    break;
                }
            } catch (InputMismatchException e) {
                System.out.println("Error: Please enter a valid number.");
                sc.nextLine(); // Clear invalid input
            }
        }

        // Check if the manager already has a project within the same application period
        for (Project existingProject : managerProjects) {
            LocalDate existingOpen = existingProject.getOpenDate();
            LocalDate existingClose = existingProject.getCloseDate();
    
            if (!(closeDate.isBefore(existingOpen) || openDate.isAfter(existingClose))) {  // Check for overlap
                System.out.println("Error: Failed to create " + projectName + ". You can only manage one project within an application period.");
                return null;
            }
        }
    
        // Create project if no conflicts
        // Project project = new Project(projectName, neighbourhood, total2Room, total3Room, openDate, closeDate, false, availableOfficerSlots);
        Project project = new Project(projectName, neighbourhood, flatTypes, totalUnits,
                                openDate, closeDate, false, totalOfficerSlots);
        project.setManager(this);  // assign the current manager
        managerProjects.add(project);  // Add the project to the manager's list
        // allProjects.add(project);      // Add the project to the global list
        BTOsystem.projects.add(project);      // Add the project to the global list
        System.out.println("---------------------------------------------------");
        System.out.println(projectName + " successfully created with ID: " + project.getId());
    
        return project;
    }

    public void viewOwnProjects()
    {
        System.out.println("\n============================================");
        System.out.println("                 MY PROJECTS");
        System.out.println("============================================");
        
if (!managerProjects.isEmpty())
        {
            System.out.printf("%-5s %-20s %-15s %-15s %-10s %-15s %-15s %-10s %-15s %-15s%n", "ID", "Project Name", "Neighbourhood", "Flat Types", "Price","Open Date", "Close Date", "Visible", "Manager", "Officer Slots");
            System.err.println("-------------------------------------------------------------------------------------------------------------------------------");
            for (Project p : managerProjects)
            {
                if (p.getManager() == this) // Check if the reference to the HDB_Manager is the same as this manager
                {
                    System.out.println(p);
                }
            }
        }
        else{
            System.out.println("No projects available.");
        }
    }

    public static void viewAllProjects()
    {
        System.out.println("\n============================================");
        System.out.println("               ALL PROJECTS");
        System.out.println("============================================");
        
        // if (!allProjects.isEmpty())
        if (!BTOsystem.projects.isEmpty())
                {
                    System.out.printf("%-5s %-20s %-15s %-15s %-10s %-15s %-15s %-10s %-15s %-15s%n", "ID","Project Name", "Neighbourhood", "Flat Types","Price", "Open Date", "Close Date", "Visible", "Manager", "Officer Slots");
                    System.err.println("-------------------------------------------------------------------------------------------------------------------------------");
                    // for (Project p : allProjects) {
                    for (Project p : BTOsystem.projects) {
                        System.out.println(p);
                    }
                }
                else{
                    System.out.println("No projects available.");
                }
            }

    // public void editProject(String projectName)
    public void editProject(Project project, Scanner sc)
    {
        int option = 0;
        // for (Project p: managerProjects)
        for (Project p: BTOsystem.projects)
        {
            // if (p.getProjectName().equals(projectName))
            if (p.equals(project))
            {
                boolean loop = true;

                while (loop) {
                    try {
                        System.out.println("\n============================================");
                        System.out.println("              EDITING PROJECT");
                        System.out.println("============================================");
                        System.out.println("Currently Editing:");
                        System.err.println("-------------------------------------------------------------------------------------------------------------------------------");
                        System.out.printf("%-5s %-20s %-15s %-15s %-10s %-15s %-15s %-10s %-15s %-15s%n", "ID","Project Name", "Neighbourhood", "Flat Types","Price", "Open Date", "Close Date", "Visible", "Manager", "Officer Slots");
                        System.err.println("-------------------------------------------------------------------------------------------------------------------------------");
                        System.out.println(project.toString());

                        System.out.println("Select attribute to edit: ");
                        System.out.println("1. Project Name");
                        System.out.println("2. Neighbourhood");
                        System.out.println("3. Edit Existing Flat Types");
                        System.out.println("4. Add New Flat Type");
                        System.out.println("5. Remove Flat Type");
                        System.out.println("6. Application opening date");
                        System.out.println("7. Application closing date");
                        System.out.println("8. Toggle Visibility");
                        System.out.println("9. Available HDB Officer Slots");
                        System.out.println("10. Return to Manager Menu");

                        // int option = scan.nextInt();
                        System.out.println("Enter your choice: ");
                        option = Input.getIntInput(sc);
                        

                        switch (option) {
                            case 1:
                                System.out.print("Enter new Project Name: ");
                                p.setProjectName(Input.getStringInput(sc));
                                break;

                            case 2:
                                System.out.print("Enter new Neighbourhood: ");
                                p.setneighbourhood(Input.getStringInput(sc));
                                break;

                            case 3:  
                                // int total2Room;
                                // while (true) {
                                //     try {
                                //         System.out.print("Enter updated number of 2-Room units (none = 0): ");
                                //         total2Room = Input.getIntInput(sc);
                                //         sc.nextLine(); // Consume newline
                            
                                //         if (total2Room >= 0) {
                                //             p.setTotal2Room(total2Room);
                                //             break;
                                //         }
                                //         System.out.println("Error: Number of 2-Room units cannot be negative.");
                                //     } catch (InputMismatchException e) {
                                //         System.out.println("Error: Please enter a valid number.");
                                //         sc.nextLine(); // Clear invalid input
                                //     }
                                // }
                                // break;

                                // if (p.getFlatTypes().isEmpty()) {
                                //     System.out.println("No flat types available to edit.");
                                //     break;
                                // }
                                
                                FlatTypesMenu(p);
                                
                                
                                String currentType;
                                int index;
                                int flatChoice;
                                
                                while (true) {
                                    try {
                                        System.out.print("\nSelect flat type to edit (1-" + p.getFlatTypes().size() + "): ");
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
                                    }
                                }

                                int newUnits;
                                while (true) {
                                    try {
                                        System.out.print("Enter new unit count for " + currentType + ": ");
                                        newUnits = Input.getIntInput(sc);
                                        
                                        
                                        if (newUnits >= 0) {
                                            // Update both total and available units
                                            p.updateFlatTypeUnits(currentType, newUnits);
                                            System.out.println(currentType + " updated successfully.");
                                            
                                            // System.out.println("\nUpdated Project Status:");
                                            // System.err.println("--------------------------------------------------------------------------------------------------------------------");
                                            // System.out.printf("%-20s %-15s %-15s %-15s %-15s %-10s %-15s %-15s%n", "Project Name", "Neighbourhood", "Flat Types", "Open Date", "Close Date", "Visible", "Manager", "Officer Slots");
                                            // System.err.println("--------------------------------------------------------------------------------------------------------------------");
                                            // System.out.println(p.toString());
                                            break;
                                        }
                                        System.out.println("Error: Units cannot be negative.");
                                    } catch (InputMismatchException e) {
                                        System.out.println("Error: Please enter a valid number.");
                                        sc.nextLine();
                                    }
                                }
                                
                                break;

                            case 4:
                                // int total3Room;
                                // while (true) {
                                //     try {
                                //         System.out.print("Enter updated number of 3-Room units (none = 0): ");
                                //         total3Room = Input.getIntInput(sc);
                                //         sc.nextLine(); // Consume newline
                            
                                //         if (total3Room >= 0) {
                                //             p.setTotal3Room(total3Room);
                                //             break;
                                //         }
                                //         System.out.println("Error: Number of 3-Room units cannot be negative.");
                                //     } catch (InputMismatchException e) {
                                //         System.out.println("Error: Please enter a valid number.");
                                //         sc.nextLine(); // Clear invalid input
                                //     }
                                // }
                                // break;
                                FlatTypesMenu(p);                                                 
                            
                                System.out.print("\nEnter new Flat Type: ");
                                String newType = Input.getStringInput(sc);
                                
                                // Check if flat type already exists
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
                                        // System.out.print("Select flat type to edit (1-" + p.getFlatTypes().size() + ") (0 to cancel): ");
                                        System.out.print("Select flat type to edit (1-" + p.getFlatTypes().size() + "): ");
                                        flatChoice = Input.getIntInput(sc);
                                        
                                        
                                            // if (flatChoice == 0) {
                                            //     System.out.println("Operation cancelled.");
                                            //     break;
                                            // }

                                            if (flatChoice > 0 && flatChoice <= p.getFlatTypes().size()) {
                                                index = flatChoice - 1;
                                                typeToRemove = p.getFlatTypes().get(index);
                                                int currentUnit = p.getTotalUnits().get(index);
                                                
                                                p.removeFlatType(typeToRemove, currentUnit);
                                                
                                                System.out.println(typeToRemove + " removed successfully.");
                                                break;
                                            } else {
                                                System.out.println("Error: Invalid selection. Please enter a number between 1 and " + 
                                                                p.getFlatTypes().size());
                                            }
                                            
                                        } catch (InputMismatchException e) {
                                            System.out.println("Error: Please enter a valid number.");
                                            sc.nextLine();  // Clear the invalid input
                                        }
                                    }
                                break;
                            case 6:
                                LocalDate openDate;
                                while(true){
                                    try {
                                        System.out.print("Enter new application opening date (yyyy-MM-dd): ");
                                        openDate = LocalDate.parse(sc.next());  // Convert string to LocalDate
                                        p.setOpenDate(openDate);
                                        break;

                                    } catch (DateTimeParseException e) {
                                        System.out.println("Error: Invalid format. Use yyyy-MM-dd.");
                                    }
                                }
                                break;

                            case 7:
                                LocalDate closeDate;
                                while(true){
                                    try {
                                        System.out.print("Enter new application closing date (yyyy-MM-dd): ");
                                        closeDate = LocalDate.parse(sc.next());  // Convert string to LocalDate
                                        p.setCloseDate(closeDate);
                                        break;
                                    } catch (DateTimeParseException e) {
                                        System.out.println("Error: Invalid format. Use yyyy-MM-dd.");
                                    }
                                }
                                break;

                            case 8:
                                p.toggle_visibility();
                                // toggleVisibility(p);
                                // p.setVisibility(!p.isVisibility());
                                break;

                            case 9:
                                int totalOfficerSlots;
                                while (true) {
                                    try {
                                        // System.out.println("Current available number of HDB Officer Slots is " + p.getAvailableOfficerSlots() + ".");
                                        System.out.print("Enter new available number of HDB Officer Slots (MAX " + Project.getmaxOfficerSlots() + "): ");
                                        totalOfficerSlots = Input.getIntInput(sc);
                                        sc.nextLine(); // Consume newline
                            
                                        // Ensure available slots do not exceed maximum slots
                                        if (totalOfficerSlots > Project.getmaxOfficerSlots() || totalOfficerSlots <= 0) {
                                            System.out.println("Error: Available officer slots must be between 1 and " + Project.getmaxOfficerSlots() + ".");
                                            continue;
                                        }
                                        
                                        // Check if the new maximum is less than the current number of assigned officers
                                        if (totalOfficerSlots < p.assignedOfficers.size()) {
                                            System.out.println("Error: Cannot set slots below current assigned officers (" + p.assignedOfficers.size() + ").");
                                            continue;
                                        }
                            
                                        // If all conditions are valid, change total no. of officer slots
                                        p.setTotalOfficerSlots(totalOfficerSlots);
                                        break;
                                        
                                    } catch (InputMismatchException e) {
                                        System.out.println("Error: Please enter a valid number.");
                                        sc.nextLine(); // Clear invalid input
                                    }
                                }
                                break;
                                
                            case 10:
                                System.out.println("---------------------------------------------------");
                                System.out.println("Exiting editing mode...");
                                loop = false;  // Exit the loop
                                break;
                            
                            default:    // executed when input entered is some int that is not btw 1-13
                                System.out.println("Error: Invalid choice. Please try again.");
                        }
                    } catch (InputMismatchException e) {    // executed when input entered is any type of input (str,char, etc)
                        System.out.println("Error: Invalid input. Please try again.");
                        sc.nextLine();  // Consume leftover newline
                    }
                }
            }
        }
        // System.err.println("Invalid project.");
    }

    public void deleteProject(Project project)
    {
        managerProjects.remove(project);
        // allProjects.remove(project);
        BTOsystem.projects.remove(project);

        // Unregister officer who had their project deleted
        List<HDB_Officer> officerList = BTOsystem.officers;
        //BTOsystem.officers.remove()
        List<HDB_Officer> officersToRemove = new ArrayList<>(); // list to ollect officers to be removed
        for (HDB_Officer officer : officerList)
        {
            if (officer.officerProject != null && officer.officerProject.equals(project)) {
                officer.officerProject = null; // Remove the project reference
                officer.registrationStatus  = "UNREGISTERED"; // Reset registration status
                officersToRemove.add(officer); // Collect officers to remove
            }
        }
        // Remove the collected officers from the officerList
        officerList.removeAll(officersToRemove);
        System.out.println("---------------------------------------------------");
        System.out.println("Successfully deleted.");
    }

    // Approve or reject an officer's registration according to specific conditions
    public void handleOfficerRegistration(Project project, HDB_Officer officer) {
        if (!project.equals(officer.officerProject))
        {
            System.out.println("Error: Officer " + officer.get_firstname() + " " + officer.get_lastname() + " did not register for Project " + project.getId() + ".");
            return;
        }

        // Check if the officer is already assigned to another project within the same application period
        if (officer.isApplicationPeriodOverlapping(project)) {
            officer.registrationStatus = "REJECTED";
            System.out.println("Officer " + officer.get_firstname() + " " + officer.get_lastname() + "'s registration rejected. Officer is already assigned to another project during this application period.");
            // System.out.println("Warning: Officer " + officer.get_firstname() + " " + officer.get_lastname() + " is already assigned to another project during this application period.");
            return;
        }

        // Check if officer has applied to be a Applicant (for this project or other projects)
        if (officer.hasAppliedAsApplicant()) {
            officer.registrationStatus = "REJECTED";
            System.out.println("Officer " + officer.get_firstname() + " " + officer.get_lastname() + "'s registration rejected. Officer has already applied as an applicant.");
            return;
        }

        // System.out.print("Do you want to approve Officer " + officer.get_firstname() + " " + officer.get_lastname() + " for " + project.getProjectName() + "? (yes/no): ");
        // String input = scan.next().toLowerCase();

        // Registration Approved, assign officer into specified project
        // if (input.equals("yes")) 
        // {
        if (project.addOfficer(officer)) {
            officer.registrationStatus  = "APPROVED";
            officer.officerProject = project;
            System.out.println("Officer " + officer.get_firstname() + " " + officer.get_lastname() + "'s registration approved and assigned to Project " + project.getId() + ".");
        } else {
            officer.registrationStatus = "REJECTED";
            System.out.println("Maximum number of officers already assigned to Project " + project.getId() + ". Officer's registration rejected.");
        }    
    }
    
    // View officer registration under them (manager)
    public void viewOfficerRegistration()
    {
        System.out.println("\n============================================================");
        System.out.println("                    OFFICER REGISTRATION");
        System.out.println("============================================================");
        // List<HDB_Officer> officerList = BTOsystem.officers;
        // List<HDB_Officer> officerList = HDB_Officer.getOfficerList();
        
        boolean hasOfficers = false;
        for (Project project : this.managerProjects) {
            if (!project.assignedOfficers.isEmpty()) {
                hasOfficers = true;
                break;
            }
        }
        
        if (hasOfficers)
        {
            System.out.printf("%-10s %-20s %-15s %-25s%n", "ID", "Name", "Status", "Project ID");
            System.out.println("------------------------------------------------------------");
            // System.out.println(BTOsystem.officers.size());
            for (HDB_Officer officer : BTOsystem.officers)
            {// {   System.out.println("1");
                // View all officer registration (including projects under other managers)
                Project assignedProject = officer.officerProject;
                if (assignedProject != null && assignedProject.getManager() == this) 
                {
                    // Only display officers assigned to projects managed by the current manager
                    System.out.printf("%-10s %-20s %-15s %-20s%n", officer.getOfficerId(), officer.get_firstname() + " " + officer.get_lastname(),officer.registrationStatus ,assignedProject.getId());
                }
            }
        }
        else{
            System.out.println("No officers available.");
        }
        // System.out.println("---------------------------------------------------");
    }

    public void handleBTOapplication(Project project, BTOapplication application, String flatType) {
    // Check if the flat type exists in the project
    if (!project.getFlatTypes().contains(flatType)) {
        System.out.println("Error: Invalid flat type '" + flatType + "'. Available types: " + project.getFlatTypes());
        application.setStatus("REJECTED");
        return;
    }
    
    // Get the index of the flat type
    int index = project.getFlatTypes().indexOf(flatType);
    
    // Check if there are available units for the requested flat type
    if (project.getAvailableUnits().get(index) <= 0) {
        application.setStatus("REJECTED");
        System.out.println("No available " + flatType + " units. Application rejected.");
        return;
    }

    
    // Application Approved, decrement the available units
    application.setStatus("APPROVED");
    
    // Update available units using the Project class method
    int currentAvailable = project.getAvailableUnits().get(index);
    project.updateAvailableUnits(flatType, currentAvailable - 1);
    
    System.out.println("Application for " + flatType + " flat approved. Remaining units: " + 
                        project.getAvailableUnits().get(index));
    
}
    public void handleWithdrawalRequest_application(Project project, BTOapplication application, Scanner sc) {

        if (!application.getWithdrawalRequested())
        {
            System.out.println("Error: Withdrawal was not requested.");
            return;
        }

        if (application.getStatus().equals("WITHDRAWN")) {
            System.out.println("Application has already been withdrawn.");
            return;
        }
        while(true){
            System.out.println("Do you want to approve the withdrawal request? (yes/no): ");
            String input = sc.next().toLowerCase();
            if (input.equals("yes") || input.equals("no")) {
                if (input.equals("yes")) {
                    // Get the index of the flat type
                    int index = project.getFlatTypes().indexOf(application.getFlatType());
                    // Update available units using the Project class method
                    int currentAvailable = project.getAvailableUnits().get(index);
                    project.updateAvailableUnits(application.getFlatType(), currentAvailable + 1);

                    application.setStatus("WITHDRAWN");
                    System.out.println("Withdrawal request approved.");   
                }else{
                    System.out.println("Withdrawal request rejected.");
                }
                break;
            } else {
                System.out.println("Error: Please enter either 'yes' or 'no'.");
            }
        }
    }
    public void handleWithdrawalRequest_officer(Project project, HDB_Officer officer, Scanner sc) {

        if (!officer.getwithdrawalRequested())
        {
            System.out.println("Error: Withdrawal was not requested.");
            return;
        }

        if (officer.registrationStatus.equals("WITHDRAWN")) {
            System.out.println("Officer's registration has already been withdrawn.");
            return;
        }
        while(true){
            System.out.println("Do you want to approve the withdrawal request? (yes/no): ");
            String input = sc.next().toLowerCase();
            if (input.equals("yes") || input.equals("no")) {
                if (input.equals("yes")) {
                    officer.registrationStatus = "WITHDRAWN";
                    System.out.println("Withdrawal request approved.");   
                }else{
                    System.out.println("Withdrawal request rejected.");
                }
                break;
            } else {
                System.out.println("Error: Please enter either 'yes' or 'no'.");
            }
        }
    }
    
    public void generateReport(Scanner sc)
    {
        // BTOapplication.get_details();   // helpz
        // OR
        
        List<BTOapplication> applicationList = BTOsystem.applications;
      

        if (applicationList.isEmpty()) {
            System.out.println("No applications available.");
            return;
        }

        // View application list
        if (!applicationList.isEmpty())
        
        {
            System.out.printf("%-10s %-20s %-15s %-25s%n", "ID", "Name", "Status", "Project ID");
            System.out.println("------------------------------------------------------------");
            for (BTOapplication application : applicationList)
            {   
                // View all officer registration (including projects under other managers)
                Project assignedProject = application.getProject();
                if (assignedProject != null && assignedProject.getManager() == this) 
                {
                    // Only display officers assigned to projects managed by the current manager
                    System.out.printf("%-10s %-20s %-15s %-20s%n", application.getId(), application.getApplicant().get_firstname() + " " + application.getApplicant().get_lastname(),application.getStatus() ,assignedProject.getId());
                }
            }
        }
        else{
            System.out.println("No applications available.");
        }

         System.out.println("\n---- Generate Report ----");
        System.out.println("1. View All Applicants");
        System.out.println("2. Filter by Marital Status");
        System.out.println("3. Filter by Flat Type");
        System.out.println("4. Filter by Both Marital Status & Flat Type");
        System.out.println("Enter your choice: "); 

        menu.printReportMenu();
        int choice = Input.getIntInput(sc);
        // sc.nextLine(); // Consume newline

        switch (choice) {
            case 1:
            // 1. Show all applications (no filtering)
            printApplicationList(applicationList);
            break;
        case 2:
            // 2. Filter by marital status
            System.out.print("Enter marital status (Single/Married): ");
            String status = sc.nextLine().trim().toLowerCase();
            
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
            // 3. Filter by flat type
            System.out.print("Enter flat type (2-Room/3-Room): ");
            String flatType = sc.nextLine().trim();
            
            List<BTOapplication> filteredByFlat = new ArrayList<>();
            for (BTOapplication app : applicationList) {
                if (app.getFlatType().equalsIgnoreCase(flatType)) {
                    filteredByFlat.add(app);
                }
            }
            printApplicationList(filteredByFlat);
            break;
        case 4:
            // 4. Filter by both marital status & flat type
            System.out.print("Enter marital status (Single/Married): ");
            status = sc.nextLine().trim().toLowerCase();
            
            System.out.print("Enter flat type (2-Room/3-Room): ");
            flatType = sc.nextLine().trim();
            
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
            // 5. Return to the previous menu
            System.out.println("Returning...");
            return;
        default:
            System.out.println("Invalid choice. Please try again.");
    }
    }
//this should be revised , change to more efficient way
    private void printApplicationList(List<BTOapplication> apps) {
        if (apps.isEmpty()) {
            System.out.println("No matching applications found.");
            return;
        }

        // Print a header row
        System.out.printf("%-20s %-5s %-15s %-10s %-15s%n",
                "Applicant Name", "Age", "Marital Status", "Flat", "Project");

        // Print each record
        for (BTOapplication app : apps) {
            Applicant applicant = app.getApplicant();
            String fullName = applicant.get_firstname() + " " + applicant.get_lastname();
            int age = applicant.get_age();
            String marital = applicant.get_maritalstatus();
            String flat = app.getFlatType();       // e.g., "2-Room" or "3-Room"
            String project = app.getProjectName();   // or app.getProject().getProjectName()

            System.out.printf("%-20s %-5d %-15s %-10s %-15s%n",
                    fullName, age, marital, flat, project);
        }
    }

//     public void reply_enquiry(Enquiry enquiry, String response) {
//         enquiry.setStaffReply(this);
//         enquiry.setResponse(response);;
//     }
// }

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
    enquiry.setStaffReply(this); // Changed from BTOsystem.currentUser to this
    
    System.out.println("Successfully replied to enquiry ID: " + enquiry.getId());
}

public void viewAllEnquiries(Scanner sc) {
    System.out.println("\n============================================");
    System.out.println("              ALL ENQUIRIES");
    System.out.println("============================================");
    
    if (BTOsystem.enquiries.size()==0) {
        System.out.println("No enquiries found in the system.");
        return;
    }
    
    // Ask user if they want to filter
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
        sc.nextLine(); // Clear invalid input
    }
    
    List<Enquiry> filteredEnquiries = new ArrayList<>();
    
    // Filter based on choice
    switch (choice) {
        case 1: // All enquiries
            filteredEnquiries = new ArrayList<>(BTOsystem.enquiries);
            System.out.println("Showing all enquiries in the system");
            break;
        case 2: // Only general (non-project)
            for (Enquiry e : BTOsystem.enquiries) {
                if (e.getProject() == null) {
                    filteredEnquiries.add(e);
                }
            }
            System.out.println("Showing general (non-project) enquiries only");
            break;
        case 3: // Only for my projects
            for (Project p: managerProjects) {
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
    
    // Display the filtered enquiries
    System.out.printf("%-5s %-20s %-20s %-30s %-15s%n", 
            "ID", "Project", "Created By", "Enquiry", "Status");
    System.out.println("------------------------------------------------------------------------------------");
    
    for (Enquiry enquiry : filteredEnquiries) {
        User creator = enquiry.getCreatedByUser();
        String creatorName = creator.get_firstname() + " " + creator.get_lastname();
        
        System.out.printf("%-5d %-20s %-20s %-30s %-15s%n",
                enquiry.getId(),
                enquiry.getProject() != null ? enquiry.getProject().getProjectName() : "General Enquiry",
                creatorName,
                truncateText(enquiry.getEnquiry(), 30),
                enquiry.getResponse().isEmpty() ? "Pending" : "Answered");
        
        // If there's a flat type specified
        if (enquiry.getflatType() != null && !enquiry.getflatType().isEmpty()) {
            System.out.println("   Flat Type: " + enquiry.getflatType());
        }
        
        // If there's a response, show it
        if (!enquiry.getResponse().isEmpty()) {
            System.out.println("   Response: " + truncateText(enquiry.getResponse(), 50));
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
    
    // First ask if the manager wants to handle project-specific or general enquiries
    System.out.println("1. Handle project-specific enquiries");
    System.out.println("2. Handle general (non-project) enquiries");
    
    int typeChoice;
    try {
        System.out.print("Enter your choice: ");
        typeChoice = Input.getIntInput(sc);
    } catch (InputMismatchException e) {
        System.out.println("Invalid input. Defaulting to project-specific enquiries.");
        typeChoice = 1;
        sc.nextLine(); // Clear invalid input
    }
    
    if (typeChoice == 1) {
        // Handle project-specific enquiries
        handleProjectSpecificEnquiries(sc);
    } else if (typeChoice == 2) {
        // Handle general enquiries
        handleGeneralEnquiries(sc);
    } else {
        System.out.println("Invalid choice. Returning to main menu.");
    }
}

private void handleProjectSpecificEnquiries(Scanner sc) {
    // List projects with pending enquiries
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
    List<Enquiry> pendingEnquiries = new ArrayList<>();
    for (Enquiry e : selectedProject.getEnquiries()) {
        if (e.getResponse().isEmpty()) {
            pendingEnquiries.add(e);
        }
    }
    
    if (pendingEnquiries.isEmpty()) {
        System.out.println("No pending enquiries for the selected project.");
        return;
    }
    
    System.out.println("Pending enquiries for " + selectedProject.getProjectName() + ":");
    for (int i = 0; i < pendingEnquiries.size(); i++) {
        Enquiry e = pendingEnquiries.get(i);
        System.out.printf("[%d] ID: %d, Question: %s%n", i, e.getId(), truncateText(e.getEnquiry(), 30));
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
    if (enquiryChoice < 0 || enquiryChoice >= pendingEnquiries.size()) {
        System.out.println("Invalid enquiry selection.");
        return;
    }
    
    Enquiry selectedEnquiry = pendingEnquiries.get(enquiryChoice);
    System.out.println("Selected enquiry: " + selectedEnquiry.getEnquiry());
    System.out.print("Enter your reply: ");
    String reply = Input.getStringInput(sc);
    reply_enquiry(selectedEnquiry, reply);
}

private void handleGeneralEnquiries(Scanner sc) {
    // Find general enquiries (those without project)
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