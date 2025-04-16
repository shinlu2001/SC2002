package SC2002.Project.ui;

import java.util.Scanner;
import SC2002.Project.boundary.Menu;
import SC2002.Project.entity.HDB_Officer;
import SC2002.Project.util.Input;
import SC2002.Project.control.applicant.applyBTOServiceApplicant;
import SC2002.Project.control.applicant.WithdrawApplicationService;
import SC2002.Project.control.PasswordChanger;
import SC2002.Project.control.applicant.EnquiryService;

public class HDB_OfficerUI {
    private static Menu menu = new Menu();
    
    public static void start(Scanner sc, HDB_Officer officer) {
        while (true) {
            try {
                System.out.println("================================================");
                System.out.println("                OFFICER MENU");
                System.out.println("================================================");
                menu.printOfficerMenu();
                int choice = Input.getIntInput(sc);
                System.out.println("--------------------------------");
                switch (choice) {
                    case 1:
                        try {
                            applyBTOServiceApplicant applyer = new applyBTOServiceApplicant(officer);
                            applyer.applyProject(sc, officer, null);
                        } catch (Input.InputExitException e) {
                            System.out.println("Operation cancelled.");
                        }
                        break;
                    case 2:
                        if (officer.getApplication() == null) {
                            System.out.println("No active application.");
                        } else {
                            officer.getApplication().printDetails();
                        }
                        break;
                    case 3:
                        System.out.println("Viewing eligible listings...");
                        // Implement eligible listings logic.
                        break;
                    case 4:
                        System.out.println("Viewing all listings...");
                        break;
                    case 5:
                        try {
                            WithdrawApplicationService.withdraw(sc, officer.getApplication(), officer);
                        } catch (Input.InputExitException e) {
                            System.out.println("Operation cancelled.");
                        }
                        break;
                    case 6:
                        try {
                            System.out.println("Managing user enquiries...");
                            // Code for managing user enquiries.
                        } catch (Input.InputExitException e) {
                            System.out.println("Operation cancelled.");
                        }
                        break;
                    case 7:
                        try {
                            new EnquiryService(officer, null).manage_enquiry(sc);
                        } catch (Input.InputExitException e) {
                            System.out.println("Operation cancelled.");
                        }
                        break;
                    case 8:
                        System.out.println("Account details:");
                        officer.printDetails();
                        break;
                    case 9:
                        try {
                            new PasswordChanger(officer);
                        } catch (Input.InputExitException e) {
                            System.out.println("Operation cancelled.");
                        }
                        break;
                    case 10:
                        System.out.println("Registering as an officer for a project...");
                        // Registration logic.
                        break;
                    case 11:
                        System.out.println("Viewing registration status...");
                        if (officer.getOfficerRegistrations().isEmpty()) {
                            System.out.println("No registrations found.");
                        } else {
                            for (HDB_Officer.OfficerRegistration reg : officer.getOfficerRegistrations()) {
                                System.out.println("Project: " + reg.getProject().getProjectName() + " | Status: " + reg.getStatus());
                            }
                        }
                        break;
                    case 12:
                        System.out.println("Viewing project details...");
                        // Code to view project details.
                        break;
                    case 13:
                        System.out.println("Processing flat booking...");
                        // Code for processing flat booking.
                        break;
                    case 14:
                        System.out.println("Viewing applications for assigned project...");
                        // Code for viewing applications.
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
}
