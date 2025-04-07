package SC2002.Project;
import java.io.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
// import java.security.SecureRandom;
// import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;

public class BTOsystem implements Input{
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
    // protected static User currentUser = null;  // Holds the currently logged-in user

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
                choice = Input.getIntInput(sc);
                System.out.println("--------------------------------");
                
                switch (choice) {
                    case 1:
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
                        saveData();
                        System.out.println("Exiting program...");
                        System.out.println("--------------------------------");
                        sc.close();
                        break;
                    default:
                        System.out.println("Error: Invalid choice. Please try again.");
                        System.out.println("--------------------------------");
                }
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
            System.out.println("Welcome, login to your account to continue");
            System.out.print("UserID (NRIC): ");
            String nric = Input.getStringInput(sc);
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
                String password = Input.getStringInput(sc);
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
    // public static Project searchProjectById(int id) {
    //     for (Project p : projects) {
    //         if (p.getId()== id) {
    //             return p;
    //         }
    //     }
    //     return null;
    // }
    // public static Enquiry searchEnquiryById(List<Enquiry> list,int id) {
    //     for (Enquiry p : list) {
    //         if (p.getId()== id) {
    //             return p;
    //         }
    //     }
    //     return null;
    // }
    public static <T> T searchById(List<T> list, int id, Function<T, Integer> getId) {
        for (T item : list) {
            if (getId.apply(item) == id) {
                return item;
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
                nric = Input.getStringInput(sc);
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
        String firstname = Input.getStringInput(sc);
        System.out.print("Last name: ");
        String lastname = Input.getStringInput(sc);
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
                age = Input.getIntInput(sc);
                break;
            } catch (InputMismatchException e) {
                System.out.println("Error: Invalid input. Please enter a number.");
                sc.next(); 
            }
        } while (true);
        
        do {
            try {
                menu.printSelectRole();
                int choice = Input.getIntInput(sc);
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
                flatTypes.add(row[2].toUpperCase());
                totalUnits.add(Integer.parseInt(row[3]));
                // availableUnits.add(Integer.parseInt(row[3]));

                // Add second flat type (3-Room) if exists
                if (row.length > 5 && !row[5].isEmpty()) {
                    flatTypes.add(row[5].toUpperCase());
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
                    true,               // visibility
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
    public static void saveData() {
        // Define the output folder (subdirectory "file" of current directory)
        String folderPath = "/workspaces/SC2002/SC2002/Project/files";
        File folder = new File(folderPath);
        if (!folder.exists()) {
            folder.mkdirs();  // Create folder if it doesn't exist
        }
    
        // Save Applicants
        String applicantFile = folderPath + File.separator + "ApplicantListNew.csv";
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(applicantFile))) {
            writer.write("Name,NRIC,Age,Marital Status,Password");
            writer.newLine();
            for (Applicant a : BTOsystem.applicants) {
                // Using only first name as per sample format; adjust if needed.
                String line = String.format("%s,%s,%d,%s,%s",
                        a.get_firstname(), a.get_nric(), a.get_age(), a.get_maritalstatus(), a.get_password());
                writer.write(line);
                writer.newLine();
            }
        } catch (IOException e) {
            System.out.println("Error writing ApplicantListNew.csv: " + e.getMessage());
        }
    
        // Save Managers
        String managerFile = folderPath + File.separator + "ManagerListNew.csv";
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(managerFile))) {
            writer.write("Name,NRIC,Age,Marital Status,Password");
            writer.newLine();
            for (HDB_Manager m : BTOsystem.managers) {
                String line = String.format("%s,%s,%d,%s,%s",
                        m.get_firstname(), m.get_nric(), m.get_age(), m.get_maritalstatus(), m.get_password());
                writer.write(line);
                writer.newLine();
            }
        } catch (IOException e) {
            System.out.println("Error writing ManagerListNew.csv: " + e.getMessage());
        }
    
        // Save Officers
        String officerFile = folderPath + File.separator + "OfficerListNew.csv";
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(officerFile))) {
            writer.write("Name,NRIC,Age,Marital Status,Password");
            writer.newLine();
            for (HDB_Officer o : BTOsystem.officers) {
                String line = String.format("%s,%s,%d,%s,%s",
                        o.get_firstname(), o.get_nric(), o.get_age(), o.get_maritalstatus(), o.get_password());
                writer.write(line);
                writer.newLine();
            }
        } catch (IOException e) {
            System.out.println("Error writing OfficerListNew.csv: " + e.getMessage());
        }
    
        // Save Projects
        String projectFile = folderPath + File.separator + "ProjectListNew.csv";
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(projectFile))) {
            // Header as per sample CSV format
            writer.write("Project Name,Neighborhood,Type 1,Number of units for Type 1,Selling price for Type 1,"
                    + "Type 2,Number of units for Type 2,Selling price for Type 2,Application opening date,Application closing date,Manager,Officer Slot,Officer");
            writer.newLine();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("d/M/yyyy");
            for (Project p : BTOsystem.projects) {
                // For this example, assume we output at most two flat types.
                String type1 = p.getFlatTypes().size() > 0 ? p.getFlatTypes().get(0) : "";
                String units1 = p.getTotalUnits().size() > 0 ? p.getTotalUnits().get(0).toString() : "";
                // We don't have selling price in Project – use 0 as a placeholder.
                String price1 = "0";
                String type2 = p.getFlatTypes().size() > 1 ? p.getFlatTypes().get(1) : "";
                String units2 = p.getTotalUnits().size() > 1 ? p.getTotalUnits().get(1).toString() : "";
                String price2 = "0";
                String openDate = p.getOpenDate().format(formatter);
                String closeDate = p.getCloseDate().format(formatter);
                String managerName = (p.getManager() != null) ? p.getManager().get_firstname() : "";
                String officerSlot = String.valueOf(p.getTotalOfficerSlots());
                // Join officer first names, if any
                String officers = "";
                if (p.assignedOfficers != null && !p.assignedOfficers.isEmpty()) {
                    StringBuilder sb = new StringBuilder();
                    for (HDB_Officer o : p.assignedOfficers) {
                        if (sb.length() > 0) {
                            sb.append(",");
                        }
                        sb.append(o.get_firstname());
                    }
                    officers = "\"" + sb.toString() + "\""; // Enclose in quotes
                }
                String line = String.format("%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s",
                        p.getProjectName(), p.getneighbourhood(), type1, units1, price1, type2, units2, price2,
                        openDate, closeDate, managerName, officerSlot, officers);
                writer.write(line);
                writer.newLine();
            }
        } catch (IOException e) {
            System.out.println("Error writing ProjectListNew.csv: " + e.getMessage());
        }
    }
}
