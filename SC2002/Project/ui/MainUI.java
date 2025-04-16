package SC2002.Project.ui;

import java.time.LocalDate;
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
    
    private static void loadData(String filePath, char type) {
        // Implement CSV file reading if needed.
        System.out.println("Loading from: " + filePath);
    }
    
    private static void saveData() {
        // Implement saving data if needed.
        System.out.println("Data saved.");
    }
    
    public static void main(String[] args) {
        mainMenu();
    }
}
