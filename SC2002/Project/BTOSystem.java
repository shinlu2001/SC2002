package SC2002.Project;

import SC2002.Project.boundary.LoginUI;
import SC2002.Project.boundary.RegistrationUI;
import SC2002.Project.boundary.util.Input;
import SC2002.Project.boundary.util.Input.InputExitException;
import SC2002.Project.boundary.util.MenuPrinter;
import SC2002.Project.control.persistence.CSVReader;
import SC2002.Project.control.persistence.CSVWriter;
import SC2002.Project.control.persistence.DataStore;
import SC2002.Project.entity.*;
import SC2002.ProjectOLD.BTOapplication;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
public final class BTOSystem{
    public static final Project officerProject = null;
    protected static List<Applicant> applicants = new ArrayList<>();
    protected static List<HDB_Officer> officers = new ArrayList<>();
    protected static List<HDB_Manager> managers = new ArrayList<>();
    protected static List<Project> projects = new ArrayList<>();
    protected static List<Enquiry> enquiries = new ArrayList<>();
    protected static List<Flat> flats = new ArrayList<>();
    protected static List<BTOapplication> applications = new ArrayList<>();

    public static void main(String[] args) {
        DataStore.getInstance().getUsers().clear();  // ensure clean slate
        CSVReader.loadAll();
        System.out.println("Loaded users: " 
            + DataStore.getInstance().getUsers().size());
        System.out.println("Loaded projects: " 
            + DataStore.getInstance().getProjects().size());
        System.out.println("Loaded registrations: " 
            + DataStore.getInstance().getRegistrations().size());

        MainMenu();
    }

    public static void MainMenu() {
        DataStore ds = DataStore.getInstance();
        Scanner sc = new Scanner(System.in);
        boolean exit = false;
    
        while (!exit) {
            MenuPrinter.printWelcomeMenu();
            try {
                int choice = Input.getIntInput(sc);
                switch (choice) {
                    case 1 -> {
                        if (ds.getUsers().isEmpty()) {
                            MenuPrinter.printLoginError();
                        } else {
                            try {
                                LoginUI.start(sc);
                            } catch (InputExitException e) {
                                System.out.println("Returning to main menu.");
                            }
                            // ← no exit = true here, so we stay in this loop
                        }
                    }
                    case 2 -> {
                        try {
                            RegistrationUI.start(sc);
                        } catch (InputExitException e) {
                            System.out.println("Returning to main menu.");
                        }
                    }
                    case 3 -> {
                        CSVReader.loadAll();
                        System.out.println("Data loaded.");
                    }
                    case 4 -> {
                        CSVWriter.saveAll();
                        System.out.println("Goodbye!");
                        exit = true;
                    }
                    default -> System.out.println("Invalid choice. Please try again.");
                }
            } catch (InputExitException e) {
                // user typed “exit” or “back” at the welcome prompt
                System.out.println("Exiting program.");
                exit = true;
            }
        }
        sc.close();
    }    
    // This class is not intended to be instantiated.
    private BTOSystem() { }
}
