package SC2002.Project;



import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.InputMismatchException;
import java.util.List;
import java.util.Scanner;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class BTOsystem {
    private static List<Applicant> applicants = new ArrayList<>();
    private static List<HDB_Officer> officers = new ArrayList<>();
    private static List<HDB_Manager> managers = new ArrayList<>();
    private static List<Project> projects = new ArrayList<>();
    static Menu menu = new Menu();
    public static List<Project> getProjects() {
        return projects;
    }
        
        public static void main(String args[]) {
            mainMenu();
        }
    
        public static void mainMenu() {
            
            System.out.println("Welcome to the BTO management system!");
            int choice=0;
            Scanner scanner = new Scanner(System.in);
            do {
                try {
                    /* System.out.println("Please choose an option:");
                    System.out.println("1. Log in");
                    System.out.println("2. Register user");
                    System.out.println("3. Fetch data from excel sheets");
                    System.out.println("4. Exit program");
                    System.out.print("Enter your choice: "); */
                    menu.printWelcomeMenu();
                choice = scanner.nextInt();
                System.out.println("--------------------------------");
                switch (choice) {
                    case 1:
                        login(scanner);
                        break;
                    case 2:
                        register_user(scanner);
                        break;
                    case 3:
                        System.out.println("Loading data from excel sheets...");
                        //to test on your own system, change the file paths to match those of yours
                        load_data("SC2002/Project/files/ManagerList.csv", 'm');  // First
                        load_data("SC2002/Project/files/OfficerList.csv", 'o');
                        load_data("SC2002/Project/files/ApplicantList.csv", 'a');
                        load_data("SC2002/Project/files/ProjectList.csv", 'p');

                        System.out.println("Data loaded!");
                        System.out.println("--------------------------------");
                        break;
                    case 4:
                        System.out.println("Exiting program...");
                        System.out.println("--------------------------------");
                        scanner.close();
                        break;
                    default:
                        System.out.println("Error: Invalid choice. Please try again.");
                        System.out.println("--------------------------------");
                }
            } catch (InputMismatchException e) {
                System.out.println("--------------------------------");
                System.out.println("Error: Invalid input. Please enter a number.");
                System.out.println("--------------------------------");
                scanner.next(); 
            }
        } while (choice != 4);
    }
    public static void login(Scanner sc) {
        if (applicants.size() == 0) {
            menu.printloginError();
        } else {
            sc.nextLine();
            System.out.println("Welcome, login to your account to continue");
            System.out.print("UserID (NRIC): ");
            String nric = sc.nextLine();
            User user = search_user(applicants, nric);
            if (user==null) {
                user = search_user(officers, nric);
            }
            if (user==null) {
                user = search_user(managers, nric);
            }
            if (user==null) {
                System.out.println("UserID does not exist.");
                System.out.println("--------------------------------");
            } else {
                System.out.print("Password: ");
                String password = sc.nextLine();
                if (user.verify_password(password)) {
                    System.out.println("--------------------------------");
                    // user.login();
                    user.start_menu(sc);
                } else {
                    System.out.println("Wrong password!");
                    System.out.println("--------------------------------");
                }
            }

        }  
    }

    public static <T extends User> T search_user(List<T> list, String nric) {
        for (T u : list) {
            if (u.get_nric().equals(nric)) {
                return u;
            }
        }
        return null;
    }

    public static void register_user(Scanner sc) {
        System.out.println("Register new user");
        sc.nextLine();
        String nric="";
        do {
            try {
                System.out.print("NRIC: ");
                nric = sc.nextLine();
                if (nric.length()==9 && (nric.charAt(0)=='S' || nric.charAt(0)=='T') && Character.isLetter(nric.charAt(nric.length() - 1))) {
                    break;
                } else {
                    System.out.println("Error: Invalid input. Please enter a valid NRIC.");
                }
            } catch (InputMismatchException e) {
                System.out.println("Error: Invalid input. Please enter a valid NRIC.");
                sc.next(); 
            }
        } while (true);
        
        System.out.print("First name: ");
        String firstname = sc.nextLine();
        System.out.print("Last name: ");
        String lastname = sc.nextLine();
        System.out.print("Marital status (s: single, m: married): ");
        char marital_status='a';
        do {
            try {
                marital_status = sc.next().charAt(0);
                if (marital_status!='m' || marital_status!='s') {
                    break;
                }
            } catch (InputMismatchException e) {
                System.out.println("Error: Invalid input. Please enter either 'm' or 's'.");
                sc.next(); 
            }
        } while (true);
        String mar_stat = "SINGLE";
        if (marital_status=='m') {
            mar_stat = "MARRIED";
        }
        System.out.print("Age: ");
        int age;
        do {
            try {
                age = sc.nextInt();
                break;
            } catch (InputMismatchException e) {
                System.out.println("Error: Invalid input. Please enter a number.");
                sc.next(); 
            }
        } while (true);
        
        do {
            try {
                /* System.out.println("Pick an option:");
                System.out.println("1. Applicant");
                System.out.println("2. HDB Officer");
                System.out.println("3. HDB Manager");
                System.out.print("Enter your choice: "); */
                menu.printSelectRole();
                int choice = sc.nextInt();
                System.out.println("--------------------------------");
                switch (choice) {
                    case 1:
                        Applicant a = new Applicant(nric, firstname, lastname, mar_stat, age);
                        applicants.add(a);
                        System.out.println("User created. Proceed to log in.");
                        break;
                }
                break;
            } catch (InputMismatchException e) {
                System.out.println("Error: Invalid input. Please enter a number.");
                sc.next(); 
            }
        } while (true);
    }
    public static void load_data(String filePath, char type) {
        
        List<String[]> rows = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            line = br.readLine();
            while ((line = br.readLine()) != null) {
                String[] values = line.split(","); 
                rows.add(values);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (type=='a') { // for applicant
            for (String[] row : rows) {
                Applicant a = new Applicant(row[1], row[0], "", row[3].toUpperCase(), Integer.parseInt(row[2]));
                applicants.add(a);
            }
        } else if (type=='o') { // for officer
            for (String[] row : rows) {
                HDB_Officer a = new HDB_Officer(row[1], row[0], "", row[3], Integer.parseInt(row[2]));
                officers.add(a);
            }
        } else if (type=='m') { // for manager
            for (String[] row : rows) {
                // HDB_Manager a = new HDB_Manager(row[1], row[0], "", row[3], Integer.parseInt(row[2]), projects);
                HDB_Manager a = new HDB_Manager(row[1], row[0], "", row[3], Integer.parseInt(row[2]));
                managers.add(a);
            }
//         } else if (type=='p') { // for project
//             // System.out.println(managers.get(1).get_firstname());
//             for (String[] row : rows) {
//                 String dateStr1 = row[8];  // Example: "20/3/2025"
//                 String dateStr2 = row[9];  // Example: "15/7/2024"

//                 // Define a formatter matching the input format
//                 DateTimeFormatter inputFormatter = DateTimeFormatter.ofPattern("d/M/yyyy");

//                 // Parse both date strings to LocalDate
//                 LocalDate formattedDate1 = LocalDate.parse(dateStr1, inputFormatter);
//                 LocalDate formattedDate2 = LocalDate.parse(dateStr2, inputFormatter);

//                 // Now you can use formattedDate1 and formattedDate2
//                 Project a = new Project(row[0], row[1], Integer.parseInt(row[3]), Integer.parseInt(row[6]), formattedDate1, formattedDate2, false, Integer.parseInt(row[11]));
//                 // System.out.println(row[10]);
//                 for (HDB_Manager man: managers) {
//                     // System.out.println(man.get_firstname());
//                     if (man.get_firstname().equals(row[10])) {
//                         // System.out.println(a);
//                         a.setManager(man);

//                         // Add to manager's project list - to view the list of projects own by current manager
//                         man.getManagerProjects().add(a);    
//                         // Add to static allProjects list - to view the list of all projects
//                         HDB_Manager.getAllProjects().add(a);
                        
//                         break;
//                     }
//                 }

//                 String[] project_officer = row[12].split(",");
//                 for (int i=0;i<project_officer.length;i++) {
//                     for (HDB_Officer off: officers) {
//                         if (off.get_firstname().equals(project_officer[i])) {
//                             a.assignOfficer(off);
//                             break;
//                         }
//                     }
//                 }

//                 projects.add(a);
//                 System.out.println(a);   //prints tostring in project
//                 // System.out.println(a.toString());
//                 // System.out.println(a.getManager().get_firstname());
//             }
//         }
            
//     }
// }
// In BTOsystem.java, modify the load_data method for projects (type 'p')
        } else if (type=='p') { // for project
            for (String[] row : rows) {
                String dateStr1 = row[8];  // Application opening date
                String dateStr2 = row[9];  // Application closing date

                // Define a formatter matching the input format
                DateTimeFormatter inputFormatter = DateTimeFormatter.ofPattern("d/M/yyyy");

                // Parse both date strings to LocalDate
                LocalDate formattedDate1 = LocalDate.parse(dateStr1, inputFormatter);
                LocalDate formattedDate2 = LocalDate.parse(dateStr2, inputFormatter);

                // Create lists for flat types and units
                List<String> flatTypes = new ArrayList<>();
                List<Integer> totalUnits = new ArrayList<>();
                List<Integer> availableUnits = new ArrayList<>();

                // Add first flat type (2-Room)
                flatTypes.add(row[2]);
                totalUnits.add(Integer.parseInt(row[3]));
                availableUnits.add(Integer.parseInt(row[3]));

                // Add second flat type (3-Room) if exists
                if (row.length > 5 && !row[5].isEmpty()) {
                    flatTypes.add(row[5]);
                    totalUnits.add(Integer.parseInt(row[6]));
                    availableUnits.add(Integer.parseInt(row[6]));
                }

                // Create project with the new constructor
                Project a = new Project(
                    row[0],              // project name
                    row[1],              // neighborhood
                    flatTypes,           // list of flat types
                    totalUnits,          // list of total units
                    availableUnits,      // list of available units
                    formattedDate1,      // open date
                    formattedDate2,      // close date
                    false,               // visibility
                    Integer.parseInt(row[11]) // available officer slots
                );

                // Assign manager
                for (HDB_Manager man: managers) {
                    if (man.get_firstname().equals(row[10])) {
                        a.setManager(man);
                        man.getManagerProjects().add(a);
                        HDB_Manager.getAllProjects().add(a);
                        break;
                    }
                }

                // Assign officers
                String[] project_officer = row[12].split(",");
                for (int i=0;i<project_officer.length;i++) {
                    for (HDB_Officer off: officers) {
                        if (off.get_firstname().equals(project_officer[i])) {
                            a.assignOfficer(off);
                            break;
                        }
                    }
                }

                projects.add(a);
                System.out.println(a);
            }
        }
    }
}