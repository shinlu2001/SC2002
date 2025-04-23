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
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
public final class BTOSystem{
    protected static List<Applicant> applicants = new ArrayList<>();
    protected static List<HDB_Officer> officers = new ArrayList<>();
    protected static List<HDB_Manager> managers = new ArrayList<>();
    protected static List<Project> projects = new ArrayList<>();
    protected static List<Enquiry> enquiries = new ArrayList<>();
    protected static List<Flat> flats = new ArrayList<>();

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
                        System.out.println("Data Load Options:");
                        System.out.println("1. Load from original files (minimal data)");
                        System.out.println("2. Load from system snapshot (complete state)");
                        System.out.print("Enter choice: ");
                        try {
                            int loadChoice = Input.getIntInput(sc);
                            if (loadChoice == 1) {
                                CSVReader.setUseSnapshot(false);
                                CSVReader.loadAll();
                                System.out.println("Original data loaded.");
                            } else if (loadChoice == 2) {
                                CSVReader.setUseSnapshot(true);
                                CSVReader.loadAll();
                                System.out.println("System snapshot loaded.");
                            } else {
                                System.out.println("Invalid choice. No data loaded.");
                            }
                        } catch (InputExitException e) {
                            System.out.println("Data load cancelled.");
                        }
                    }
                    case 4 -> {
                        CSVWriter.saveAll();
                        System.out.println("Goodbye!");
                        exit = true;
                    }
                    default -> System.out.println("Invalid choice. Please try again.");
                }
            } catch (InputExitException e) {
                // user typed "exit" or "back" at the welcome prompt
                System.out.println("Exiting program.");
                exit = true;
            }
        }
        sc.close();
    }    
    // This class is not intended to be instantiated.
    private BTOSystem() { }
}
