package SC2002.Project.ui;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Scanner;
import SC2002.Project.boundary.Menu;
import SC2002.Project.entity.HDB_Manager;
import SC2002.Project.entity.Project;
import SC2002.Project.util.Input;

public class HDB_ManagerUI {
    private static Menu menu = new Menu();
    
    public static void start(Scanner sc, HDB_Manager manager) {
        while (true) {
            try {
                System.out.println("================================================");
                System.out.println("                MANAGER MENU");
                System.out.println("================================================");
                menu.printManagerMenu();
                int choice = Input.getIntInput(sc);
                System.out.println("--------------------------------");
                switch (choice) {
                    case 1:
                        System.out.println("Creating a project...");
                        Project p = createProject(sc, manager);
                        if (p != null) {
                            System.out.println("Project created with ID: " + p.getId());
                        }
                        break;
                    case 2:
                        System.out.println("Editing a project...");
                        // Code to edit project.
                        break;
                    case 3:
                        System.out.println("Deleting a project...");
                        // Code to delete project.
                        break;
                    case 4:
                        System.out.println("Viewing all projects...");
                        // Code to view all projects.
                        break;
                    case 5:
                        System.out.println("Viewing my projects...");
                        // Code to view manager’s projects.
                        break;
                    case 6:
                        System.out.println("Viewing officer registrations...");
                        // Code to view registrations.
                        break;
                    case 7:
                        System.out.println("Handling officer registration...");
                        // Code to handle registration.
                        break;
                    case 8:
                        System.out.println("Handling officer withdrawal requests...");
                        // Code to handle withdrawals.
                        break;
                    case 9:
                        System.out.println("Handling BTO applications...");
                        // Code to handle applications.
                        break;
                    case 10:
                        System.out.println("Handling BTO application withdrawal requests...");
                        // Code to handle application withdrawals.
                        break;
                    case 11:
                        System.out.println("Generating applicant report...");
                        // Code to generate report.
                        break;
                    case 12:
                        System.out.println("Viewing all enquiries...");
                        // Code to view enquiries.
                        break;
                    case 13:
                        System.out.println("Handling project enquiries...");
                        // Code to handle enquiries.
                        break;
                    case 14:
                        System.out.println("Viewing account details...");
                        manager.printDetails();
                        break;
                    case 15:
                        System.out.println("Logging out...");
                        return;
                    default:
                        System.out.println("Invalid choice. Try again.");
                        break;
                }
            } catch (Input.InputExitException e) {
                System.out.println("Operation cancelled. Returning to menu.");
            }
        }
    }
    
    private static Project createProject(Scanner sc, HDB_Manager manager) {
        System.out.print("Enter Project Name: ");
        String projectName = Input.getStringInput(sc);
        System.out.print("Enter Neighbourhood: ");
        String neighbourhood = Input.getStringInput(sc);
        System.out.print("Enter Flat Type (e.g., 2-Room): ");
        String flatType = Input.getStringInput(sc);
        System.out.print("Enter number of " + flatType + " units: ");
        int units = Input.getIntInput(sc);
        LocalDate openDate = null;
        while (true) {
            try {
                System.out.print("Enter Application Opening Date (yyyy-MM-dd): ");
                String dateInput = Input.getStringInput(sc);
                openDate = LocalDate.parse(dateInput);
                break;
            } catch (Exception e) {
                System.out.println("Error: Use format yyyy-MM-dd.");
            }
        }
        LocalDate closeDate = null;
        while (true) {
            try {
                System.out.print("Enter Application Closing Date (yyyy-MM-dd): ");
                String dateInput = Input.getStringInput(sc);
                closeDate = LocalDate.parse(dateInput);
                break;
            } catch (Exception e) {
                System.out.println("Error: Use format yyyy-MM-dd.");
            }
        }
        int officerSlots = 10;
        Project p = new Project(projectName, neighbourhood, Arrays.asList(flatType), Arrays.asList(units), openDate, closeDate, false, officerSlots);
        p.setManager(manager);
        manager.getManagerProjects().add(p);
        MainUI.projects.add(p); // Add to global list if needed.
        return p;
    }
}
