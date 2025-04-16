package SC2002.Project.ui;

import java.util.Scanner;
import SC2002.Project.boundary.Menu;
import SC2002.Project.entity.Applicant;
import SC2002.Project.util.Input;
import SC2002.Project.control.applicant.applyBTOServiceApplicant;
import SC2002.Project.control.applicant.EnquiryService;
import SC2002.Project.control.applicant.WithdrawApplicationService;
import SC2002.Project.boundary.BTOService;

public class ApplicantUI {
    private static Menu menu = new Menu();
    
    public static void start(Scanner sc, Applicant applicant) {
        System.out.println("Welcome, " + applicant.getFirstName() + "!");
        int choice = 0;
        while (true) {
            try {
                System.out.println("================================================");
                System.out.println("                APPLICANT MENU");
                System.out.println("================================================");
                menu.printApplicantMenu();
                choice = Input.getIntInput(sc);
                System.out.println("------------------------------------------------");
                switch (choice) {
                    case 1:
                        try {
                            applyBTOServiceApplicant applyer = new applyBTOServiceApplicant(applicant);
                            // Here, a project displayer could be passed if needed.
                            applyer.applyProject(sc, applicant, null);
                        } catch (Input.InputExitException e) {
                            System.out.println("Operation cancelled.");
                        }
                        break;
                    case 2:
                        try {
                            BTOService.displayApplication(sc, applicant);
                        } catch (Input.InputExitException e) {
                            System.out.println("Operation cancelled.");
                        }
                        break;
                    case 3:
                        System.out.println("Viewing eligible listings...");
                        // Implement eligible listings logic here.
                        break;
                    case 4:
                        System.out.println("Viewing all listings...");
                        break;
                    case 5:
                        try {
                            WithdrawApplicationService.withdraw(sc, applicant.getApplication(), applicant);
                        } catch (Input.InputExitException e) {
                            System.out.println("Operation cancelled.");
                        }
                        break;
                    case 6:
                        try {
                            new EnquiryService(applicant, null).manage_enquiry(sc);
                        } catch (Input.InputExitException e) {
                            System.out.println("Operation cancelled.");
                        }
                        break;
                    case 7:
                        System.out.println("Account details:");
                        applicant.printDetails();
                        break;
                    case 8:
                        try {
                            new SC2002.Project.control.PasswordChanger(applicant);
                        } catch (Input.InputExitException e) {
                            System.out.println("Operation cancelled.");
                        }
                        break;
                    case 9:
                        System.out.println("Logging out...");
                        return;
                    default:
                        System.out.println("Invalid choice. Please try again.");
                        break;
                }
            } catch (Input.InputExitException e) {
                System.out.println("Operation cancelled. Returning to menu.");
            }
        }
    }
}
