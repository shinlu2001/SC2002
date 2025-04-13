package SC2002.Project;

import java.io.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.function.Function;

public class BTOsystem implements Input {
    public static final Project officerProject = null;
    protected static List<Applicant> applicants = new ArrayList<>();
    protected static List<HDB_Officer> officers = new ArrayList<>();
    protected static List<HDB_Manager> managers = new ArrayList<>();
    protected static List<Project> projects = new ArrayList<>();
    protected static List<Enquiry> enquiries = new ArrayList<>();
    protected static List<Flat> flats = new ArrayList<>();
    protected static List<BTOapplication> applications = new ArrayList<>();

    static Menu menu = new Menu();

    public static void main(String args[]) {
        mainMenu();
    }

    public static void mainMenu() {
        System.out.println("Welcome to the BTO management system!");
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
                        register_user(sc);
                        break;
                    case 3:
                        // Clear all existing data
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
                        BTOapplication.nextId = -1;
                        flats.clear();
                        System.out.println("Loading data from excel sheets...");
                        load_data("SC2002/Project/files/ManagerList.csv", 'm');
                        load_data("SC2002/Project/files/OfficerList.csv", 'o');
                        load_data("SC2002/Project/files/ApplicantList.csv", 'a');
                        load_data("SC2002/Project/files/ProjectList.csv", 'p');
                        System.out.println("Data loaded!");
                        System.out.println("--------------------------------");
                        break;
                    case 4:
                        saveData();
                        System.out.println("Exiting program...");
                        System.out.println("--------------------------------");
                        sc.close();
                        break;
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
        } while (choice != 4);
    }

    public static void login(Scanner sc) {
        if (applicants.size() + officers.size() + managers.size() == 0) {
            menu.printloginError();
        } else {
            System.out.println("Welcome, login to your account to continue");
            System.out.print("UserID (NRIC): ");
            String nric = Input.getStringInput(sc);
            User user = search_user(applicants, nric);
            if (user == null) {
                user = search_user(officers, nric);
            }
            if (user == null) {
                user = search_user(managers, nric);
            }
            if (user == null) {
                System.out.println("UserID does not exist.");
                System.out.println("--------------------------------");
            } else {
                System.out.print("Password: ");
                String password = Input.getStringInput(sc);
                if (user.verify_password(password)) {
                    System.out.println("--------------------------------");
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
        sc.nextLine(); // Clear leftover newline
        String nric = "";
        do {
            try {
                System.out.print("NRIC: ");
                nric = Input.getStringInput(sc);
                if (nric.length() == 9 && (nric.charAt(0) == 'S' || nric.charAt(0) == 'T') && Character.isLetter(nric.charAt(nric.length() - 1))) {
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
                    System.out.println("Error: Please enter 'm', 's', 'married', or 'single'.");
                }
            } catch (Input.InputExitException e) {
                System.out.println("Exiting registration input as requested.");
                return;
            } catch (InputMismatchException e) {
                System.out.println("Error: Invalid input. Please try again.");
                sc.nextLine(); 
            }
            System.out.println("Invalid input. Please enter either 'm' or 's'.");
        } while (true);

        String mar_stat = "SINGLE";
        if (marital_status == 'm') {
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
                        Applicant a = new Applicant(nric, firstname, lastname, mar_stat, age);
                        applicants.add(a);
                        System.out.println("Applicant created. Proceed to log in.");
                        break;
                    }
                    case 2: {
                        HDB_Officer o = new HDB_Officer(nric, firstname, lastname, mar_stat, age);
                        officers.add(o);
                        System.out.println("Officer created. Proceed to log in.");
                        break;
                    }
                    case 3: {
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
            } catch (Input.InputExitException e) {
                System.out.println("Exiting role selection as requested.");
                return;
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
        if (type == 'a') {
            for (String[] row : rows) {
                Applicant a = new Applicant(row[1], row[0], "", row[3].toUpperCase(), Integer.parseInt(row[2]));
                applicants.add(a);
            }
        } else if (type == 'o') {
            for (String[] row : rows) {
                HDB_Officer a = new HDB_Officer(row[1], row[0], "", row[3].toUpperCase(), Integer.parseInt(row[2]));
                officers.add(a);
            }
        } else if (type == 'm') {
            for (String[] row : rows) {
                HDB_Manager a = new HDB_Manager(row[1], row[0], "", row[3], Integer.parseInt(row[2]));
                managers.add(a);
            }
        } else if (type == 'p') {
            for (String[] row : rows) {
                String dateStr1 = row[8];
                String dateStr2 = row[9];
                DateTimeFormatter inputFormatter = DateTimeFormatter.ofPattern("d/M/yyyy");
                LocalDate formattedDate1 = LocalDate.parse(dateStr1, inputFormatter);
                LocalDate formattedDate2 = LocalDate.parse(dateStr2, inputFormatter);
                List<String> flatTypes = new ArrayList<>();
                List<Integer> totalUnits = new ArrayList<>();
                flatTypes.add(row[2].toUpperCase());
                totalUnits.add(Integer.parseInt(row[3]));
                if (row.length > 5 && !row[5].isEmpty()) {
                    flatTypes.add(row[5].toUpperCase());
                    totalUnits.add(Integer.parseInt(row[6]));
                }
                Project a = new Project(
                    row[0],
                    row[1],
                    flatTypes,
                    totalUnits,
                    formattedDate1,
                    formattedDate2,
                    true,
                    Integer.parseInt(row[11])
                );
                for (HDB_Manager man : managers) {
                    if (man.get_firstname().equals(row[10])) {
                        a.setManager(man);
                        man.managerProjects.add(a);
                        break;
                    }
                }
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
                for (int j = 0; j < a.getAvailableUnits().get(0); j++) {
                    Flat f = new Flat(a, a.getFlatTypes().get(0), Double.parseDouble(row[4]));
                    flats.add(f);
                }
                for (int j = 0; j < a.getAvailableUnits().get(1); j++) {
                    Flat f = new Flat(a, a.getFlatTypes().get(1), Double.parseDouble(row[7]));
                    flats.add(f);
                }
                projects.add(a);
                System.out.println(projects);
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
            for (Applicant a : BTOsystem.applicants) {
                String line = String.format("%s,%s,%d,%s,%s",
                        a.get_firstname(), a.get_nric(), a.get_age(), a.get_maritalstatus(), a.get_password());
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
            for (HDB_Manager m : BTOsystem.managers) {
                String line = String.format("%s,%s,%d,%s,%s",
                        m.get_firstname(), m.get_nric(), m.get_age(), m.get_maritalstatus(), m.get_password());
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
            for (HDB_Officer o : BTOsystem.officers) {
                String line = String.format("%s,%s,%d,%s,%s",
                        o.get_firstname(), o.get_nric(), o.get_age(), o.get_maritalstatus(), o.get_password());
                writer.write(line);
                writer.newLine();
            }
        } catch (IOException e) {
            System.out.println("Error writing OfficerListNew.csv: " + e.getMessage());
        }
        String projectFile = folderPath + File.separator + "ProjectListNew.csv";
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(projectFile))) {
            writer.write("Project Name,Neighborhood,Type 1,Number of units for Type 1,Selling price for Type 1,"
                    + "Type 2,Number of units for Type 2,Selling price for Type 2,Application opening date,Application closing date,Manager,Officer Slot,Officer");
            writer.newLine();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("d/M/yyyy");
            for (Project p : BTOsystem.projects) {
                String type1 = p.getFlatTypes().size() > 0 ? p.getFlatTypes().get(0) : "";
                String units1 = p.getTotalUnits().size() > 0 ? p.getTotalUnits().get(0).toString() : "";
                String price1 = "0";
                String type2 = p.getFlatTypes().size() > 1 ? p.getFlatTypes().get(1) : "";
                String units2 = p.getTotalUnits().size() > 1 ? p.getTotalUnits().get(1).toString() : "";
                String price2 = "0";
                String openDate = p.getOpenDate().format(formatter);
                String closeDate = p.getCloseDate().format(formatter);
                String managerName = (p.getManager() != null) ? p.getManager().get_firstname() : "";
                String officerSlot = String.valueOf(p.getTotalOfficerSlots());
                String officers = "";
                if (p.assignedOfficers != null && !p.assignedOfficers.isEmpty()) {
                    StringBuilder sb = new StringBuilder();
                    for (HDB_Officer o : p.assignedOfficers) {
                        if (sb.length() > 0) {
                            sb.append(",");
                        }
                        sb.append(o.get_firstname());
                    }
                    officers = "\"" + sb.toString() + "\"";
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
