package SC2002.Project;
import java.io.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
// import java.security.SecureRandom;
// import java.util.concurrent.atomic.AtomicInteger;

public class BTOsystem {
    // private static final SecureRandom random = new SecureRandom();
    // private static final AtomicInteger counter = new AtomicInteger(0);

    // public static int generateUniqueId() {
    //     int timePart = (int) (System.currentTimeMillis() % 10000); // Last 4 digits of time
    //     int count = counter.getAndIncrement() % 10; // Rolling counter (0-9)
    //     int randomPart = random.nextInt(10); // Single random digit (0-9)

    //     return timePart * 10 + count + randomPart; // 5-digit unique ID
    // }

    public static final Project officerProject = null;
    protected static List<Applicant> applicants = new ArrayList<>();      //protected?
    protected static List<HDB_Officer> officers = new ArrayList<>();    //protected?
    protected static List<HDB_Manager> managers = new ArrayList<>();      //protected?
    protected static List<Project> projects = new ArrayList<>();    //protected?
    protected static List<Enquiry> enquiries = new ArrayList<>();   //protected?
    protected static List<Flat> flats = new ArrayList<>();          //protected?
    protected static List<BTOapplication> applications = new ArrayList<>(); 
    // public static List<Project> getProjects() {
    //     return projects;
    // }
    // public static List<Enquiry> getEnquiries() {
    //     return enquiries;
    // }
    // public static List<Flat> getFlats() {
    //     return flats;
    // }
    // list of applications submitted : submitted_app
    static Menu menu = new Menu(); 
        public static void main(String args[]) {
            mainMenu();
        }
    
        public static void mainMenu() {
            
            System.out.println("Welcome to the BTO management system!");
            int choice=0;
            Scanner sc = new Scanner(System.in);
            do {
                try {
                menu.printWelcomeMenu();
                choice = sc.nextInt();

                // clearScreen();
                System.out.println("--------------------------------");
                
                switch (choice) {
                    case 1:
                        // clearScreen();
                        login(sc);
                        break;
                    case 2:
                        // clearScreen();
                        register_user(sc);
                        break;
                    case 3:
                        // clearScreen();
                        applicants.clear();
                        Applicant.nextId = -1;
                        officers.clear();
                        HDB_Officer.nextId = -1;
                        managers.clear();
                        HDB_Manager.nextId = -1;
                        projects.clear();
                        Project.nextId = -1;
                        enquiries.clear();
                        Enquiry.nextId = -1;
                        applications.clear();
                        BTOapplication.nextId =-1;
                        flats.clear();
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
                        // clearScreen();
                        System.out.println("Exiting program...");
                        System.out.println("--------------------------------");
                        sc.close();
                        break;
                    default:
                        System.out.println("Error: Invalid choice. Please try again.");
                        System.out.println("--------------------------------");
                }
                sc.nextLine();
            } catch (InputMismatchException e) {
                System.out.println("--------------------------------");
                System.out.println("Error: Invalid input. Please enter a number.");
                System.out.println("--------------------------------");
                sc.next(); 
            }
        } while (choice != 4);
    }
    public static void login(Scanner sc) {
        if (applicants.size()+officers.size()+managers.size() == 0) {
            menu.printloginError();
        } else {
            sc.nextLine();
            System.out.println("Welcome, login to your account to continue");
            System.out.print("UserID (NRIC): ");
            String nric = sc.nextLine();
            User user = search_user(applicants, nric);
            if (user==null) {
                // user = search_user(officers, nric);
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
    public static Project searchProjectById(int id) {
        for (Project p : projects) {
            if (p.getProjectID()== id) {
                return p;
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
                menu.printSelectRole();
                int choice = sc.nextInt();
                System.out.println("--------------------------------");
                switch (choice) {
                    case 1: {
                        //applicant
                        Applicant a = new Applicant(nric, firstname, lastname, mar_stat, age);
                        applicants.add(a);
                        System.out.println("Applicant created. Proceed to log in.");
                        break;
                    }
                    case 2: {
                        // officer
                        HDB_Officer o = new HDB_Officer(nric, firstname, lastname, mar_stat, age);
                        officers.add(o);
                        System.out.println("Officer created. Proceed to log in.");
                        break;
                    }
                    case 3: {
                        //manager
                        HDB_Manager m = new HDB_Manager(nric, firstname, lastname, mar_stat, age);
                        managers.add(m);
                        System.out.println("Manager created. Proceed to log in.");
                        break;
                    }
                    default: {
                        System.out.println("Invalid choice!");
                        break;
                    }
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
                String[] values = line.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)");
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
                // List<Integer> availableUnits = new ArrayList<>();

                // Add first flat type (2-Room)
                flatTypes.add(row[2]);
                totalUnits.add(Integer.parseInt(row[3]));
                // availableUnits.add(Integer.parseInt(row[3]));

                // Add second flat type (3-Room) if exists
                if (row.length > 5 && !row[5].isEmpty()) {
                    flatTypes.add(row[5]);
                    totalUnits.add(Integer.parseInt(row[6]));
                    // availableUnits.add(Integer.parseInt(row[6]));
                }

                // Create project with the new constructor
                Project a = new Project(
                    row[0],              // project name
                    row[1],              // neighborhood
                    flatTypes,           // list of flat types
                    totalUnits,          // list of total units
                    // availableUnits,      // list of available units
                    formattedDate1,      // open date
                    formattedDate2,      // close date
                    false,               // visibility
                    Integer.parseInt(row[11]) // available officer slots
                );

                // Assign manager
                for (HDB_Manager man: managers) {
                    if (man.get_firstname().equals(row[10])) {
                        a.setManager(man);
                        man.managerProjects.add(a);
                        
                        break;
                    }
                }

                // Assign officers using the helper method in HDB_Officer
                String[] project_officer = row[12].replace("\"", "").split(",");
                for (String name : project_officer) {
                    name = name.trim();
                    for (HDB_Officer off : officers) {
                        if (off.get_firstname().equalsIgnoreCase(name)) {
                            off.forceRegisterAndApprove(a);
                            break;
                        }
                    }
                }
                
                // HARD CODED ONLY WORKS FOR Daniel,Emily -> Name1,Name2

                // System.out.println("Raw data: " + row[12] + row[13] + row[14]);
                // System.out.println("Raw data: " + row[12] + row[13]);

                // // First check if we need to reconstruct a quoted field
                // String officerField;
                // if (row.length > 13 && row[12].startsWith("\"") && !row[12].endsWith("\"")) {
                //     // Reconstruct quoted field that was split across columns
                //     officerField = row[12] + "," + row[13];
                // } else {
                //     officerField = row[12];
                // }

                // // Now properly parse the officer names
                // String[] project_officer = officerField.replace("\"", "").split(",");
                // System.out.println("Officers after proper parsing: " + Arrays.toString(project_officer));

                // for (String officerName : project_officer) {
                //     officerName = officerName.trim();
                //     System.out.println("Processing officer: " + officerName);
                //     for (HDB_Officer off : officers) {
                //         if (off.get_firstname().equalsIgnoreCase(officerName)) {

                //             // off.registerForProject(a);
                //             // HDB_Officer.getOfficerList().add(off);  // does this work compared to registerForProject(a)??
                //             // officers.add(off);
                //             a.addOfficer(off);
                //             // a.assignOfficer(off);
                //             // a.handleOfficerRegistration();
                //             System.out.println("Assigned officer: " + off.get_firstname());
                //             break;
                //         }
                //     }
                // }

                // create flat objects for first type
                for (int j=0;j<a.getAvailableUnits().get(0);j++){
                    Flat f = new Flat(a, a.getFlatTypes().get(0), Double.parseDouble(row[4]));
                    flats.add(f);
                }
                
                // create flat objects for second type
                for (int j=0;j<a.getAvailableUnits().get(1);j++){
                    Flat f = new Flat(a, a.getFlatTypes().get(1), Double.parseDouble(row[7]));
                    flats.add(f);
                }
            
                projects.add(a);
                // System.out.println(HDB_Manager.allProjects);
                System.out.println(projects);
            }
        }
    }
}
