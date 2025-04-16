package SC2002.Project.ui;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.function.Function;
import SC2002.Project.boundary.Menu;
import SC2002.Project.entity.Applicant;
import SC2002.Project.entity.HDB_Officer;
import SC2002.Project.entity.HDB_Manager;
import SC2002.Project.entity.Project;
import SC2002.Project.entity.BTOapplication;
import SC2002.Project.entity.Enquiry;
import SC2002.Project.entity.Flat;
import SC2002.Project.util.Input;

public class MainUI {
    private static Menu menu = new Menu();
    public static List<Applicant> applicants = new ArrayList<>();
    public static List<HDB_Officer> officers = new ArrayList<>();
    public static List<HDB_Manager> managers = new ArrayList<>();
    public static List<Project> projects = new ArrayList<>();
    public static List<Enquiry> enquiries = new ArrayList<>();
    public static List<Flat> flats = new ArrayList<>();
    public static List<BTOapplication> applications = new ArrayList<>();
    
    public static void mainMenu() {
        System.out.println("Welcome to the BTO Management System!");
        int choice = 0;
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
                        registerUser(sc);
                        break;
                    case 3:
                        clearData();
                        System.out.println("Loading data from CSV files...");
                        loadData("SC2002/Project/files/ManagerList.csv", 'm');
                        loadData("SC2002/Project/files/OfficerList.csv", 'o');
                        loadData("SC2002/Project/files/ApplicantList.csv", 'a');
                        loadData("SC2002/Project/files/ProjectList.csv", 'p');
                        System.out.println("Data loaded!");
                        System.out.println("--------------------------------");
                        break;
                    case 4:
                        saveData();
                        System.out.println("Exiting program...");
                        sc.close();
                        return;
                    default:
                        System.out.println("Error: Invalid choice. Please try again.");
                        System.out.println("--------------------------------");
                }
            } catch (Input.InputExitException e) {
                System.out.println("Exiting current menu as per user request.");
            } catch (InputMismatchException e) {
                System.out.println("--------------------------------");
                System.out.println("Error: Invalid input. Please enter a number.");
                System.out.println("--------------------------------");
                sc.next();
            }
        } while (true);
    }
    
    private static void login(Scanner sc) {
        if (applicants.isEmpty() && officers.isEmpty() && managers.isEmpty()) {
            menu.printLoginError();
        } else {
            System.out.println("Please log in to your account.");
            System.out.print("UserID (NRIC): ");
            String nric = Input.getStringInput(sc);
            Object user = searchUser(applicants, nric);
            if (user == null) {
                user = searchUser(officers, nric);
            }
            if (user == null) {
                user = searchUser(managers, nric);
            }
            if (user == null) {
                System.out.println("UserID does not exist.");
                System.out.println("--------------------------------");
            } else {
                System.out.print("Password: ");
                String password = Input.getStringInput(sc);
                if (user instanceof SC2002.Project.entity.User && ((SC2002.Project.entity.User) user).verifyPassword(password)) {
                    System.out.println("--------------------------------");
                    if (user instanceof Applicant)
                        ApplicantUI.start(sc, (Applicant) user);
                    else if (user instanceof HDB_Officer)
                        HDB_OfficerUI.start(sc, (HDB_Officer) user);
                    else if (user instanceof HDB_Manager)
                        HDB_ManagerUI.start(sc, (HDB_Manager) user);
                } else {
                    System.out.println("Wrong password!");
                    System.out.println("--------------------------------");
                }
            }
        }
    }
    
    private static <T> T searchUser(List<T> list, String nric) {
        for (T u : list) {
            if (u instanceof SC2002.Project.entity.User) {
                if (((SC2002.Project.entity.User)u).getNRIC().equals(nric))
                    return u;
            }
        }
        return null;
    }
    
    private static void registerUser(Scanner sc) {
        System.out.println("Register new user");
        sc.nextLine();
        String nric = "";
        do {
            try {
                System.out.print("NRIC: ");
                nric = Input.getStringInput(sc);
                if (nric.length() == 9 && (nric.charAt(0) == 'S' || nric.charAt(0) == 'T')
                    && Character.isLetter(nric.charAt(nric.length() - 1)))
                    break;
                else
                    System.out.println("Error: Invalid NRIC format.");
            } catch (Input.InputExitException e) {
                System.out.println("Exiting registration.");
                return;
            }
        } while (true);
        
        System.out.print("First name: ");
        String firstname = Input.getStringInput(sc);
        System.out.print("Last name: ");
        String lastname = Input.getStringInput(sc);
        System.out.print("Marital status (s: single, m: married): ");
        String maritalInput = "";
        char marital_status = 'a';
        do {
            try {
                maritalInput = Input.getStringInput(sc).toLowerCase();
                if (maritalInput.equals("m") || maritalInput.equals("married")) {
                    marital_status = 'm';
                    break;
                } else if (maritalInput.equals("s") || maritalInput.equals("single")) {
                    marital_status = 's';
                    break;
                } else {
                    System.out.println("Error: Please enter 'm' or 's'.");
                }
            } catch (Input.InputExitException e) {
                System.out.println("Exiting registration.");
                return;
            }
        } while (true);
        String mar_stat = (marital_status == 'm') ? "MARRIED" : "SINGLE";
        System.out.print("Age: ");
        int age = Input.getIntInput(sc);
        
        menu.printSelectRole();
        int choice = Input.getIntInput(sc);
        System.out.println("--------------------------------");
        switch (choice) {
            case 1:
                Applicant a = new Applicant(nric, firstname, lastname, mar_stat, age);
                applicants.add(a);
                System.out.println("Applicant created. Please log in.");
                break;
            case 2:
                HDB_Officer o = new HDB_Officer(nric, firstname, lastname, mar_stat, age);
                officers.add(o);
                System.out.println("Officer created. Please log in.");
                break;
            case 3:
                HDB_Manager m = new HDB_Manager(nric, firstname, lastname, mar_stat, age);
                managers.add(m);
                System.out.println("Manager created. Please log in.");
                break;
            default:
                System.out.println("Invalid role selection.");
                break;
        }
    }
    
    private static void clearData() {
        applicants.clear();
        officers.clear();
        managers.clear();
        projects.clear();
        enquiries.clear();
        applications.clear();
        flats.clear();
    }
    
    public static void loadData(String filePath, char type) {
    List<String[]> rows = new ArrayList<>();
    try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
        String line;
        // Read header line first
        line = br.readLine();
        while ((line = br.readLine()) != null) {
            String[] values = line.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)");
            rows.add(values);
        }
    } catch (IOException e) {
        e.printStackTrace();
    }
    if (type == 'a') {
        for (String[] row : rows) {
            // Assumes row[1]=First Name, row[0]=Last Name, row[2]=Age, row[3]=Marital Status
            Applicant a = new Applicant(
                    row[1].trim(),  // first name
                    row[0].trim(),  // last name
                    "",             // placeholder (if needed)
                    row[3].trim().toUpperCase(), 
                    Integer.parseInt(row[2].trim())
            );
            applicants.add(a);
        }
    } else if (type == 'o') {
        for (String[] row : rows) {
            HDB_Officer o = new HDB_Officer(
                    row[1].trim(), 
                    row[0].trim(), 
                    "", 
                    row[3].trim().toUpperCase(), 
                    Integer.parseInt(row[2].trim())
            );
            officers.add(o);
        }
    } else if (type == 'm') {
        for (String[] row : rows) {
            HDB_Manager m = new HDB_Manager(
                    row[1].trim(), 
                    row[0].trim(), 
                    "", 
                    row[3].trim(), 
                    Integer.parseInt(row[2].trim())
            );
            managers.add(m);
        }
    } else if (type == 'p') {
        DateTimeFormatter inputFormatter = DateTimeFormatter.ofPattern("d/M/yyyy");
        for (String[] row : rows) {
            // Expected columns:
            // row[0] = Project Name,
            // row[1] = Neighborhood,
            // row[2] = Flat Type 1,
            // row[3] = Number of units for Type 1,
            // row[4] = Selling price for Type 1,
            // row[5] = Flat Type 2 (optional),
            // row[6] = Number of units for Type 2 (if exists),
            // row[7] = Selling price for Type 2 (if exists),
            // row[8] = Application opening date,
            // row[9] = Application closing date,
            // row[10] = Manager first name,
            // row[11] = Officer Slot,
            // row[12] = Comma‐separated list of officer first names.
            LocalDate formattedDate1 = LocalDate.parse(row[8].trim(), inputFormatter);
            LocalDate formattedDate2 = LocalDate.parse(row[9].trim(), inputFormatter);
            List<String> flatTypes = new ArrayList<>();
            List<Integer> totalUnits = new ArrayList<>();
            flatTypes.add(row[2].trim().toUpperCase());
            totalUnits.add(Integer.parseInt(row[3].trim()));
            if (row.length > 5 && !row[5].trim().isEmpty()) {
                flatTypes.add(row[5].trim().toUpperCase());
                totalUnits.add(Integer.parseInt(row[6].trim()));
            }
            Project p = new Project(
                    row[0].trim(),
                    row[1].trim(),
                    flatTypes,
                    totalUnits,
                    formattedDate1,
                    formattedDate2,
                    true,
                    Integer.parseInt(row[11].trim())
            );
            // Link manager using new getter getFirstName()
            for (HDB_Manager man : managers) {
                if (man.getFirstName().equalsIgnoreCase(row[10].trim())) {
                    p.setManager(man);
                    man.getManagerProjects().add(p);
                    break;
                }
            }            
            // Process officer registrations
            String[] project_officer = row[12].replace("\"", "").split(",");
            for (String name : project_officer) {
                name = name.trim();
                for (HDB_Officer off : officers) {
                    if (off.getFirstName().equalsIgnoreCase(name)) {
                        off.forceRegisterAndApprove(p);
                        break;
                    }
                }
            }
            // Create Flat objects for each flat type.
            // For Type 1:
            for (int j = 0; j < p.getAvailableUnits().get(0); j++) {
                Flat f = new Flat(p, p.getFlatTypes().get(0), Double.parseDouble(row[4].trim()));
                flats.add(f);
            }
            // For Type 2, if exists:
            if (p.getFlatTypes().size() > 1) {
                for (int j = 0; j < p.getAvailableUnits().get(1); j++) {
                    Flat f = new Flat(p, p.getFlatTypes().get(1), Double.parseDouble(row[7].trim()));
                    flats.add(f);
                }
            }
            projects.add(p);
            System.out.println("Loaded project: " + p.getProjectName());
        }
    }
}

    
    public static void saveData() {
    String folderPath = "/workspaces/SC2002/SC2002/Project/files";
    File folder = new File(folderPath);
    if (!folder.exists()) {
        folder.mkdirs();
    }
    String applicantFile = folderPath + File.separator + "ApplicantListNew.csv";
    try (BufferedWriter writer = new BufferedWriter(new FileWriter(applicantFile))) {
        writer.write("Name,NRIC,Age,Marital Status,Password");
        writer.newLine();
        for (Applicant a : applicants) {
            String line = String.format("%s,%s,%d,%s,%s",
                    a.getFirstName(), a.getNRIC(), a.getAge(), a.getMaritalStatus(), a.getPassword());
            writer.write(line);
            writer.newLine();
        }
    } catch (IOException e) {
        System.out.println("Error writing ApplicantListNew.csv: " + e.getMessage());
    }
    String managerFile = folderPath + File.separator + "ManagerListNew.csv";
    try (BufferedWriter writer = new BufferedWriter(new FileWriter(managerFile))) {
        writer.write("Name,NRIC,Age,Marital Status,Password");
        writer.newLine();
        for (HDB_Manager m : managers) {
            String line = String.format("%s,%s,%d,%s,%s",
                    m.getFirstName(), m.getNRIC(), m.getAge(), m.getMaritalStatus(), m.getPassword());
            writer.write(line);
            writer.newLine();
        }
    } catch (IOException e) {
        System.out.println("Error writing ManagerListNew.csv: " + e.getMessage());
    }
    String officerFile = folderPath + File.separator + "OfficerListNew.csv";
    try (BufferedWriter writer = new BufferedWriter(new FileWriter(officerFile))) {
        writer.write("Name,NRIC,Age,Marital Status,Password");
        writer.newLine();
        for (HDB_Officer o : officers) {
            String line = String.format("%s,%s,%d,%s,%s",
                    o.getFirstName(), o.getNRIC(), o.getAge(), o.getMaritalStatus(), o.getPassword());
            writer.write(line);
            writer.newLine();
        }
    } catch (IOException e) {
        System.out.println("Error writing OfficerListNew.csv: " + e.getMessage());
    }
    String projectFile = folderPath + File.separator + "ProjectListNew.csv";
    try (BufferedWriter writer = new BufferedWriter(new FileWriter(projectFile))) {
        writer.write("Project Name,Neighborhood,Type 1,Number of units for Type 1,Selling price for Type 1," +
                "Type 2,Number of units for Type 2,Selling price for Type 2,Application opening date,Application closing date,Manager,Officer Slot,Officer");
        writer.newLine();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("d/M/yyyy");
        for (Project p : projects) {
            String type1 = p.getFlatTypes().size() > 0 ? p.getFlatTypes().get(0) : "";
            String units1 = p.getTotalUnits().size() > 0 ? p.getTotalUnits().get(0).toString() : "";
            String price1 = "0";
            String type2 = p.getFlatTypes().size() > 1 ? p.getFlatTypes().get(1) : "";
            String units2 = p.getTotalUnits().size() > 1 ? p.getTotalUnits().get(1).toString() : "";
            String price2 = "0";
            String openDate = p.getOpenDate().format(formatter);
            String closeDate = p.getCloseDate().format(formatter);
            String managerName = (p.getManager() != null) ? p.getManager().getFirstName() : "";
            String officerSlot = String.valueOf(p.getTotalOfficerSlots());
            String officersStr = p.getAssignedOfficersNames();
            if (!officersStr.isEmpty()) {
                officersStr = "\"" + officersStr + "\"";
            }
            if (p.getAssignedOfficers() != null && !p.getAssignedOfficers().isEmpty()) {
                StringBuilder sb = new StringBuilder();
                for (HDB_Officer o : p.getAssignedOfficers()) {
                    if (sb.length() > 0) sb.append(",");
                    sb.append(o.getFirstName());
                }
                officersStr = "\"" + sb.toString() + "\"";
            }
            String line = String.format("%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s",
                    p.getProjectName(), p.getNeighbourhood(), type1, units1, price1, type2, units2, price2,
                    openDate, closeDate, managerName, officerSlot, officersStr);
            writer.write(line);
            writer.newLine();
        }
    } catch (IOException e) {
        System.out.println("Error writing ProjectListNew.csv: " + e.getMessage());
    }
}

}