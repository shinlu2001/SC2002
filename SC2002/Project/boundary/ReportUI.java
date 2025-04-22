// SC2002/Project/boundary/ReportUI.java
package SC2002.Project.boundary;

import SC2002.Project.boundary.util.Input;
import SC2002.Project.boundary.util.MenuPrinter;
import SC2002.Project.control.ReportController;
import SC2002.Project.entity.*;
import java.util.List;
import java.util.Scanner;

/**
 * Boundary class for handling report generation and viewing.
 */
public class ReportUI {
    private static final ReportController reportController = new ReportController();

    public static void start(Scanner sc, HDB_Manager manager) {
        boolean exit = false;
        while (!exit) {
            MenuPrinter.printReportMenu();
            try {
                int choice = Input.getIntInput(sc);
                switch (choice) {
                    case 1 -> viewAllApplicants(sc, manager);
                    case 2 -> filterByMaritalStatus(sc, manager);
                    case 3 -> filterByFlatType(sc, manager);
                    case 4 -> filterByBoth(sc, manager);
                    case 5 -> exit = true;
                    default -> System.out.println("Invalid choice. Please try again.");
                }
            } catch (Input.InputExitException e) {
                System.out.println("Returning to previous menu.");
                exit = true;
            }
        }
    }

    private static void viewAllApplicants(Scanner sc, HDB_Manager manager) {
        List<BTOApplication> applications = reportController.getAllApplications(manager);
        if (applications.isEmpty()) {
            System.out.println("No applications found.");
            return;
        }
        printApplications(applications);
    }

    private static void filterByMaritalStatus(Scanner sc, HDB_Manager manager) {
        try {
            System.out.print("Enter marital status (SINGLE/MARRIED): ");
            String status = Input.getStringInput(sc).toUpperCase();
            
            List<BTOApplication> applications = reportController.filterByMaritalStatus(manager, status);
            if (applications.isEmpty()) {
                System.out.println("No applications found for " + status + " applicants.");
                return;
            }
            printApplications(applications);
        } catch (Input.InputExitException e) {
            System.out.println("Filter operation cancelled.");
        }
    }

    private static void filterByFlatType(Scanner sc, HDB_Manager manager) {
        try {
            System.out.print("Enter flat type (2-ROOM/3-ROOM/etc): ");
            String flatType = Input.getStringInput(sc).toUpperCase();
            
            List<BTOApplication> applications = reportController.filterByFlatType(manager, flatType);
            if (applications.isEmpty()) {
                System.out.println("No applications found for " + flatType + " flats.");
                return;
            }
            printApplications(applications);
        } catch (Input.InputExitException e) {
            System.out.println("Filter operation cancelled.");
        }
    }

    private static void filterByBoth(Scanner sc, HDB_Manager manager) {
        try {
            System.out.print("Enter marital status (SINGLE/MARRIED): ");
            String status = Input.getStringInput(sc).toUpperCase();
            
            System.out.print("Enter flat type (2-ROOM/3-ROOM/etc): ");
            String flatType = Input.getStringInput(sc).toUpperCase();
            
            List<BTOApplication> applications = reportController.filterByBoth(manager, status, flatType);
            if (applications.isEmpty()) {
                System.out.println("No applications found for " + status + " applicants and " + flatType + " flats.");
                return;
            }
            printApplications(applications);
        } catch (Input.InputExitException e) {
            System.out.println("Filter operation cancelled.");
        }
    }

    private static void printApplications(List<BTOApplication> applications) {
        System.out.println("\nApplication Report");
        System.out.println("=================");
        System.out.printf("%-5s %-20s %-5s %-10s %-15s %-15s %-10s%n",
            "ID", "Applicant Name", "Age", "Status", "Flat Type", "Project", "Status");
        System.out.println("-----------------------------------------------------------------------------------");
        
        for (BTOApplication app : applications) {
            Applicant applicant = (Applicant) app.getApplicant();

            if (!app.isWithdrawalRequested()){
                System.out.printf("%-5d %-20s %-5d %-10s %-15s %-15s %-10s%n",
                    app.getId(),
                    applicant.getFirstName() + " " + applicant.getLastName(),
                    applicant.getAge(),
                    applicant.getMaritalStatus(),
                    app.getRoomType(),
                    Input.truncateText(app.getProject().getName(), 15),
                    app.getStatus());
            }
            else{
                System.out.printf("%-5d %-20s %-5d %-10s %-15s %-15s %-10s%n",
                app.getId(),
                applicant.getFirstName() + " " + applicant.getLastName(),
                applicant.getAge(),
                applicant.getMaritalStatus(),
                app.getRoomType(),
                Input.truncateText(app.getProject().getName(), 15),
                app.getStatus()+ " (Withdrawal Req)");
            }
        }
        System.out.println("-----------------------------------------------------------------------------------");
    }
}
