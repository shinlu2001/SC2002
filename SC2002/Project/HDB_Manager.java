package SC2002.Project;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.InputMismatchException;
import java.util.List;
import java.util.Scanner;

public class HDB_Manager extends User {
    private static int hdb_man_id = -1;
    private int manager_id;
    private String type="MANAGER";

    private static List<Project> allProjects = new ArrayList<>();  // Static list to store all projects - for manager to view all projects
    private List<Project> managerProjects = new ArrayList<>();  // List to store own projects - for manager to view own projects
    private List<Project> projects; //store the projects list
    
    // static Scanner scan = new Scanner(System.in);
    public HDB_Manager(String nric, String firstname, String lastname, String marital_status, int age) {
        super(nric, firstname, lastname, marital_status, age);
        this.managerProjects = new ArrayList<>();
        // this.projects = projects;  // Initialize the projects list
        manager_id = ++hdb_man_id;
    }
    public void to_string() {
        super.to_string();
        System.out.println("ManagerID: " + manager_id);
    }
    public List<Project> getManagerProjects() {
        return managerProjects;
    }
    
    public static List<Project> getAllProjects() {
        return allProjects;
    }
    public void start_menu(Scanner sc) {
        System.out.println("Welcome to HDB BTO Management System, " + this.get_firstname() + "!");

        int choice = 0;
        boolean loop = true;  // Control variable for menu loop

        while (loop) {
        {
            try {
                
                System.out.println("============================================");
                System.out.println("         M A N A G E R   M E N U");
                menu.printManagerMenu();
                System.out.println("============================================");
                

                choice = sc.nextInt();
                sc.nextLine(); // Consume leftover newline
                
                switch (choice) {
                    case 1:     // create a new project based by keying input in
                        
                        createProject(sc);

                        System.out.println("---------------------------------------------------");
                        break;
    
                    case 2:     // Edit a Project - editProject(project)
                        System.out.print("Enter the name of the project you wish to edit: ");
                        
                        // find if project exists in allProjects then check if the project is under the current manager
                        Project projectToEdit = findAndValidateProject(sc);
                        if (projectToEdit != null) {
                            editProject(projectToEdit, sc);
                        }
                        
                        System.out.println("---------------------------------------------------");
                        break;
                        
                    case 3: // Delete a Project - deleteProject(project)
                        System.out.print("Enter the name of the project you wish to delete: ");
                        
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
                        
                        case 7:     // handle Officer Registration - handleOfficerRegistration(project, officer)
                            //WHEN CHECK IF PROJECT EXIST OR JUST NOT OWNER OF PROJ REFER TO DELETE SIMILAR^ (see below too)
                            // System.out.print("Enter Project Name: ");
                            // // find if project exists in allProjects then check if the project is under the current manager                                                                       
                            // Project projectForOfficer = findAndValidateProject(sc);
                            // if (projectForOfficer != null) {
                            //     // Rest of officer handling logic
                            // } else {
                            //     System.out.print("Enter Officer's First Name: ");
                            //     String officerFirstName = sc.nextLine();
                            //     HDB_Officer officer = null;
                            
                            //     // Find the officer by first name
                            //     for (HDB_Officer o : HDB_Officer.getOfficerList()) {
                            //         if (o.get_firstname().equalsIgnoreCase(officerFirstName)) {
                            //             officer = o;
                            //             break;
                            //         }
                            //     }
                            
                            //     if (officer != null) {
                            //         // Handle the officer registration
                            //         handleOfficerRegistration(projectForOfficer, officer);
                            //     } else {
                            //         System.out.println("Officer not found.");
                            //     }
                            // }
                            System.out.println("---------------------------------------------------");
                            break;
                            
                        case 8:     // handle BTO registration - handleBTOapplication(projectToEdit, application, type)
                            // System.out.print("Enter Project Name: ");
                            // // find if project exists in allProjects then check if the project is under the current manager
                            // Project projectForBTO = findAndValidateProject(sc);
                            // if (projectForBTO != null) {
                            //     // Rest of BTO handling logic
                            // } else {
                            //     // Here you would need to add code to select an application
                            //     // This is just a placeholder - you'll need to implement the actual application selection logic
                            //     System.out.println("BTO application handling functionality would go here");
                            // }
                        
                            System.out.println("---------------------------------------------------");
                            break;
                        
                        case 9:     // handle withdrawel requests - handleWithdrawalRequest(project, application)
                            // System.out.print("Enter Project Name: ");
                            // // find if project exists in allProjects then check if the project is under the current manager
                            // Project projectForWithdrawal = findAndValidateProject(sc);
                            // if (projectForWithdrawal != null) {
                            //     // Rest of withdrawal handling logic
                            // } else {
                            //     // Here you would need to add code to select an application with a withdrawal request
                            //     // This is just a placeholder - you'll need to implement the actual application selection logic
                            //     System.out.println("Withdrawal request handling functionality would go here");
                            // }
                    
                            System.out.println("---------------------------------------------------");
                            break;
                        
                        case 10:     // generate appplicant report - generateReport(applicationList)

                            System.out.println("---------------------------------------------------");
                            break;
                        
                        case 11:     // view all enquiries (across all projects)
                            System.out.println("---------------------------------------------------");
                            break;
                        
                        case 12:     // handle project enquires (view and reply to enquiries for your projects)
                            System.out.println("---------------------------------------------------");
                            break;
                        case 13:     //view manager account details
                            System.out.println("---------------------------------------------------");
                            to_string();
                            System.out.println("---------------------------------------------------");
                            sc.nextLine();
                            break;
                        case 14:
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
        String projectName = sc.nextLine();
        
        // Find project in allProjects
        Project project = null;
        for (Project p : allProjects) {
            if (p.getProjectName().equalsIgnoreCase(projectName)) {
                project = p;
                break;
            }
        }
        
        // Validate project
        if (project == null) {
            System.out.println("---------------------------------------------------");
            System.out.println("Error: Project does not exist.");
            return null;
        }
        // check if current manager is in charge of the project
        if (!managerProjects.contains(project)) {
            System.out.println("---------------------------------------------------");
            System.out.println("Error: You are not the manager of " + project.getProjectName() + ".");
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
        String projectName = sc.nextLine();

        System.out.print("Enter Neighbourhood: ");
        String neighbourhood = sc.nextLine();

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
            String flatType = sc.nextLine();

            int units;
            while (true) {
                try {
                    System.out.print("Enter number of " + flatType + " units (none = 0): ");
                    units = sc.nextInt();
                    sc.nextLine(); // Consume newline

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
        //         total2Room = (sc.nextInt());
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
        //         total3Room = sc.nextInt();
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
                System.out.print("Enter Application Opening Date for Project " + projectName + " (yyyy-MM-dd): ");
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
                System.out.print("Enter  Application Closing Date for " + projectName + " (yyyy-MM-dd): ");
                closeDate = LocalDate.parse(sc.next());  // Convert string to LocalDate
                break;
            } catch (DateTimeParseException e) {
                System.out.println("Error: Invalid format. Use yyyy-MM-dd.");
            }
        }
        // Continuously loop until a valid (availableOfficerSlots input btw 1-10) input is entered
        int availableOfficerSlots;
        while (true)
        {   try{
                System.out.print("Enter Available HDB Officer Slots (MAX " + Project.getmaxOfficerSlots() + "): ");
                availableOfficerSlots = sc.nextInt();
                sc.nextLine(); // Consume newline

                // If input(availableOfficerSlots) is NOT btw 1-10, keep looping
                if (availableOfficerSlots > Project.getmaxOfficerSlots() || availableOfficerSlots <= 0 ) {
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
        Project project = new Project(projectName, neighbourhood, flatTypes, totalUnits, availableUnits,
                                openDate, closeDate, false, availableOfficerSlots);
        project.setManager(this);  // assign the current manager
        managerProjects.add(project);  // Add the project to the manager's list
        allProjects.add(project);      // Add the project to the global list
        System.out.println("---------------------------------------------------");
        System.out.println(projectName + " successfully created.");
    
        return project;
    }

    public void viewOwnProjects()
    {
        System.out.println("\n============================================");
        System.out.println("                 MY PROJECTS");
        System.out.println("============================================");
        
if (!managerProjects.isEmpty())
        {
            System.out.printf("%-20s %-15s %-15s %-15s %-15s %-10s %-15s %-15s%n", "Project Name", "Neighbourhood", "Flat Types", "Open Date", "Close Date", "Visible", "Manager", "Officer Slots");
            System.err.println("-----------------------------------------------------------------------------------------------------------------------------");
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
        
        if (!allProjects.isEmpty())
                {
                    System.out.printf("%-20s %-15s %-15s %-15s %-15s %-10s %-15s %-15s%n", "Project Name", "Neighbourhood", "Flat Types", "Open Date", "Close Date", "Visible", "Manager", "Officer Slots");
                    System.err.println("-----------------------------------------------------------------------------------------------------------------------------");
                    for (Project p : allProjects) {
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
        for (Project p: allProjects)
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
                        System.err.println("-----------------------------------------------------------------------------------------------------------------------------");
                        System.out.printf("%-20s %-15s %-15s %-15s %-15s %-10s %-15s %-15s%n", "Project Name", "Neighbourhood", "Flat Types", "Open Date", "Close Date", "Visible", "Manager", "Officer Slots");
                        System.err.println("-----------------------------------------------------------------------------------------------------------------------------");
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
                        option = sc.nextInt();
                        sc.nextLine(); // Consume newline

                        switch (option) {
                            case 1:
                                System.out.print("Enter new Project Name: ");
                                p.setProjectName(sc.nextLine());
                                break;

                            case 2:
                                System.out.print("Enter new Neighbourhood: ");
                                p.setneighbourhood(sc.nextLine());
                                break;

                            case 3:  
                                // int total2Room;
                                // while (true) {
                                //     try {
                                //         System.out.print("Enter updated number of 2-Room units (none = 0): ");
                                //         total2Room = sc.nextInt();
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
                                        flatChoice = sc.nextInt();
                                        sc.nextLine();
                                        
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
                                        newUnits = sc.nextInt();
                                        sc.nextLine();
                                        
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
                                //         total3Room = sc.nextInt();
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
                                String newType = sc.nextLine();
                                
                                // Check if flat type already exists
                                if (p.getFlatTypes().contains(newType)) {
                                    System.out.println("Error: This flat type already exists.");
                                    break;
                                }
                                

                                while (true) {
                                    try {
                                        System.out.print("Enter number of " + newType + " units (none = 0): ");
                                        newUnits = sc.nextInt();
                                        sc.nextLine();
                                        
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
                                        flatChoice = sc.nextInt();
                                        sc.nextLine();  // Consume the newline
                                        
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
                                int availableOfficerSlots;
                                while (true) {
                                    try {
                                        // System.out.println("Current available number of HDB Officer Slots is " + p.getAvailableOfficerSlots() + ".");
                                        System.out.print("Enter new available number of HDB Officer Slots (MAX " + Project.getmaxOfficerSlots() + "): ");
                                        availableOfficerSlots = sc.nextInt();
                                        sc.nextLine(); // Consume newline
                            
                                        // Ensure available slots do not exceed maximum slots
                                        if (availableOfficerSlots > Project.getmaxOfficerSlots() || availableOfficerSlots <= 0) {
                                            System.out.println("Error: Available officer slots must be between 1 and " + Project.getmaxOfficerSlots() + ".");
                                            continue;
                                        }
                                        
                                        // Check if the new maximum is less than the current number of assigned officers
                                        if (availableOfficerSlots < p.getAssignedOfficerList().size()) {
                                            System.out.println("Error: Cannot set slots below current assigned officers (" + p.getAssignedOfficerList().size() + ").");
                                            continue;
                                        }
                            
                                        // If all conditions are valid, change available no. of officer slots
                                        p.setAvailableOfficerSlots(availableOfficerSlots);
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
        allProjects.remove(project);

        // Unregister officer who had their project deleted
        List<HDB_Officer> officerList = HDB_Officer.getOfficerList();
        List<HDB_Officer> officersToRemove = new ArrayList<>(); // list to ollect officers to be removed
        for (HDB_Officer officer : officerList)
        {
            if (officer.getAssignedProject() != null && officer.getAssignedProject().equals(project)) {
                officer.setAssignedProject(null); // Remove the project reference
                officer.setRegistrationStatus("Unregistered"); // Reset registration status
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
        if (!project.equals(officer.getAssignedProject()))
        {
            System.out.println("Error: Officer " + officer.get_firstname() + " " + officer.get_lastname() + " did not register for " + project.getProjectName() + ".");
            return;
        }

        // Check if the officer is already assigned to another project within the same application period
        if (officer.isApplicationPeriodOverlapping(project)) {
            officer.setRegistrationStatus("Rejected");
            System.out.println("Officer " + officer.get_firstname() + " " + officer.get_lastname() + "'s registration rejected. Officer is already assigned to another project during this application period.");
            // System.out.println("Warning: Officer " + officer.get_firstname() + " " + officer.get_lastname() + " is already assigned to another project during this application period.");
            return;
        }

        // Check if officer has applied to be a Applicant (for this project or other projects)
        if (officer.hasAppliedAsApplicant()) {
            officer.setRegistrationStatus("Rejected");
            System.out.println("Officer " + officer.get_firstname() + " " + officer.get_lastname() + "'s registration rejected. Officer has already applied as an applicant.");
            return;
        }

        // System.out.print("Do you want to approve Officer " + officer.get_firstname() + " " + officer.get_lastname() + " for " + project.getProjectName() + "? (yes/no): ");
        // String input = scan.next().toLowerCase();

        // Registration Approved, assign officer into specified project
        // if (input.equals("yes")) 
        // {
            if (project.addOfficer(officer)) {
                officer.setRegistrationStatus("Approved");
                officer.setAssignedProject(project);
                System.out.println("Officer " + officer.get_firstname() + " " + officer.get_lastname() + "'s registration approved and assigned to " + project.getProjectName() + ".");
            } else
            {
                officer.setRegistrationStatus("Rejected");
                System.out.println("Maximum number of officers already assigned to " + project.getProjectName() + ". Officer's registration rejected. ");
            }
        // }else{
        //     officer.setRegistrationStatus("Rejected");
        //     System.out.println("Officer " + officer.get_firstname() + " " + officer.get_lastname() + "'s registration has been rejected.");
        // }
    }
    
    // View officer registration under them (manager)
    public void viewOfficerRegistration()
    {
        System.out.println("\n============================================");
        System.out.println("            OFFICER REGISTRATION");
        System.out.println("============================================");

        List<HDB_Officer> officerList = HDB_Officer.getOfficerList();
        if (!officerList.isEmpty())
        {
            System.out.printf("%-20s %-15s %-20s%n", "Name", "Status", "Project\n");
            System.out.println("---------------------------------------------------");
            for (HDB_Officer officer : officerList)
            {   
                // View all officer registration (including projects under other managers)
                Project assignedProject = officer.getAssignedProject();
                if (assignedProject != null && assignedProject.getManager() == this) 
                {
                    // Only display officers assigned to projects managed by the current manager
                    String projectName = assignedProject.getProjectName();
                    System.out.printf("%-20s %-15s %-20s%n", officer.get_firstname() + " " + officer.get_lastname(),officer.getRegistrationStatus(),projectName);
                }
            }
        }
        else{
            System.out.println("No officers available.");
        }
        // System.out.println("---------------------------------------------------");
    }
    
    // // Automatically approve or reject application according to specific conditions
    // public void handleBTOapplication(Project project, BTOapplication application, String flatType) {
  
    //     // Check if the flat type is valid
    //     if (!flatType.equals("2-Room") && !flatType.equals("3-Room")) 
    //     {
    //         System.out.println("Error: Invalid flat type. Please specify either '2-Room' or '3-Room'.");
    //         return;
    //     }
    
    //     // Check if there are available units for the requested flat type
    //     if (flatType.equals("2-Room") && project.getavailable2Room() <= 0) 
    //     {
    //         application.setApplicationStatus("Rejected");
    //         System.out.println("No available 2-Room units. Application rejected.");
    //         // System.out.println("Warning: No available 2-Room units. Application rejected.");
    //         return;

    //     } else if (flatType.equals("3-Room") && project.getavailable3Room() <= 0) 
    //     {
    //         application.setApplicationStatus("Rejected");
    //         System.out.println("No available 3-Room units. Application rejected.");
    //         // System.out.println("Warning: No available 3-Room units. Application rejected.");
    //         return;
    //     }
        
    //     // System.out.print("Do you want to approve the BTO application? (yes/no): ");
    //     // String input = scan.next().toLowerCase();

    //     // Application Approved, decrement the available units
    //     // if (input.equals("yes"))
    //     // {
    //         if (flatType.equals("2-Room")) 
    //         {
    //             application.setApplicationStatus("Approved");
    //             project.setavailable2Room(project.getavailable2Room() - 1);
    //             System.out.println("Application for 2-Room flat approved. Remaining 2-Room units: " + project.getavailable2Room());

    //         } else if (flatType.equals("3-Room")) 
    //         {
    //             application.setApplicationStatus("Approved");
    //             project.setavailable3Room(project.getavailable3Room() - 1);
    //             System.out.println("Application for 3-Room flat approved. Remaining 3-Room units: " + project.getavailable3Room());
    //         }
    //     // } else{
    //     //     application.setApplicationStatus("Rejected");
    //     //     System.out.println("BTO application rejected.");
    //     // }
    // }

        public void handleBTOapplication(Project project, BTOapplication application, String flatType) {
        // Check if the flat type exists in the project
        if (!project.getFlatTypes().contains(flatType)) {
            System.out.println("Error: Invalid flat type '" + flatType + "'. Available types: " + project.getFlatTypes());
            application.setApplicationStatus("Rejected");
            return;
        }
        
        // Get the index of the flat type
        int index = project.getFlatTypes().indexOf(flatType);
        
        // Check if there are available units for the requested flat type
        if (project.getAvailableUnits().get(index) <= 0) {
            application.setApplicationStatus("Rejected");
            System.out.println("No available " + flatType + " units. Application rejected.");
            return;
        }
        
        // Application Approved, decrement the available units
        application.setApplicationStatus("Approved");
        
        // Update available units using the Project class method
        int currentAvailable = project.getAvailableUnits().get(index);
        project.updateAvailableUnits(flatType, currentAvailable - 1);
        
        System.out.println("Application for " + flatType + " flat approved. Remaining units: " + 
                          project.getAvailableUnits().get(index));
    }

    public void handleWithdrawalRequest(Project project, BTOapplication application, Scanner sc) {

        if (!application.getwithdrawalRequested())
        {
            System.out.println("Error: Withdrawal was not requested.");
            return;
        }

        if (application.getApplicationStatus().equals("Withdrawn")) {
            System.out.println("Application has already been withdrawn.");
            return;
        }
        while(true){
            System.out.println("Do you want to approve the withdrawal request? (yes/no): ");
            String input = sc.next().toLowerCase();
            if (input.equals("yes") || input.equals("no")) {
                if (input.equals("yes")) {
                    application.setApplicationStatus("Withdrawn");
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
    
    public void generateReport(List<BTOapplication> applicationList, Scanner sc)
    {
        if (applicationList.isEmpty()) {
            System.out.println("No applications available.");
            return;
        }
        System.out.println("\n---- Generate Report ----");
        System.out.println("1. View All Applicants");
        System.out.println("2. Filter by Marital Status");
        System.out.println("3. Filter by Flat Type");
        System.out.println("4. Filter by Both Marital Status & Flat Type");
        System.out.println("Enter your choice: ");
        int choice = sc.nextInt();
        sc.nextLine(); // Consume newline

        switch (choice) {
            case 1:
                System.out.printf("%-20s %-10s %-15s %-15s %-15s%n", "Applicant Name", "Age", "Marital Status", "Flat Type", "Project Name\n");
            
                // for (BTOapplication application : applicationList)
                // {
                //     if (application.getManager() == this) // Check if the reference to the HDB_Manager is the same as this manager
                //     {
                //         System.out.println(p);
                //     }
                // }
                

                break;
        
            default:
                break;
        }
    }
    public void reply_enquiry(Enquiry enquiry, String response) {
        enquiry.setStaffReply(this);
        enquiry.setResponse(response);;
    }
}