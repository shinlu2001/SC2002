package SC2002.Project.boundary;

import SC2002.Project.boundary.util.Input;
import SC2002.Project.boundary.util.MenuPrinter;
import SC2002.Project.control.ApplicantController;
import SC2002.Project.control.ProjectController;
import SC2002.Project.entity.Applicant;
import SC2002.Project.entity.BTOApplication;
import SC2002.Project.entity.Project;
import SC2002.Project.entity.enums.ApplicationStatus;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;

public class ApplicantUI {

    public static void start(Scanner sc, Applicant applicant) {
        ApplicantController applicantController = new ApplicantController(applicant);
        ProjectController   projectController   = new ProjectController();
        boolean exit = false;

        while (!exit) {
            System.out.println("\nWelcome, " + applicant.getFirstName() + "!");
            MenuPrinter.printRoleMenuHeader(0);
            MenuPrinter.printApplicantMenu();
            try {
                int choice = Input.getIntInput(sc);
                switch (choice) {
                    case 1 -> applyForProject(sc, applicant, applicantController, projectController);
                    case 2 -> viewActiveApplication(applicant); // done
                    case 3 -> viewEligibleListings(applicant, applicantController); // done
                    case 4 -> viewAllListings(projectController, applicantController, applicant); // done
                    case 5 -> withdrawApplication(sc, applicant, applicantController);
                    case 6 -> EnquiryUI.start(sc, applicant);
                    case 7 -> viewAccountDetails(applicant); // done
                    case 8 -> AuthUI.changePassword(sc, applicant); // done
                    case 9 -> exit = true;
                    default -> System.out.println("Invalid choice. Please try again.");
                }
            } catch (Input.InputExitException e) {
                System.out.println("Returning to previous menu.");
                exit = true;
            }
        }
    }

    public static void applyForProject(Scanner sc,
                                        Applicant applicant,
                                        ApplicantController ctrl,
                                        ProjectController projCtrl) {
        List<Project> eligible = ctrl.listEligibleProjects();
        if (eligible.isEmpty()) {
            System.out.println("You are not eligible for any current projects.");
            return;
        }

        if (ctrl.hasActiveApplication()) {
            return;
        }
        
        System.out.println("\nEligible Projects for Application:");
        MenuPrinter.printProjectTableEligible(eligible, applicant, ctrl);

        try {
            System.out.print("Project ID: ");
            int pid = Input.getIntInput(sc);
            Project p = projCtrl.findById(pid);
            if (p == null || !eligible.contains(p)) {
                System.out.println("Invalid Project ID or not eligible.");
                return;
            }

            List<String> types = p.getFlatTypes().stream()
                                  .filter(ctrl::isEligibleForRoomType)
                                  .toList();
            System.out.println("\nChoose flat type:");
            types.forEach(t -> System.out.println(" - " + t));

            String ft = Input.getStringInput(sc).toUpperCase();
            if (!types.contains(ft)) {
                System.out.println("Invalid flat type.");
                return;
            }
            ctrl.createApplication(p, ft);
        } catch (Input.InputExitException e) {
            System.out.println("Cancelled.");
        }
    }

    public static void viewActiveApplication(Applicant applicant) {
        Optional<BTOApplication> oa = applicant.getCurrentApplication();
        if (oa.isEmpty()) {
            System.out.println("You have no active application.");
            return;
        }
        BTOApplication app = oa.get();
        System.out.println("\nCurrent Application:");
        System.out.println(app);
        if (app.getStatus() == ApplicationStatus.SUCCESS) {
            System.out.println("Your application succeeded! An officer will be in touch.");
        } else if (app.isWithdrawalRequested()) {
            System.out.println("Withdrawal is pending approval.");
        }
    }

    protected static void viewEligibleListings(Applicant applicant,
                                             ApplicantController ctrl) {
        List<Project> eligible = ctrl.listEligibleProjects();
        if (eligible.isEmpty()) {
            System.out.println("No eligible projects.");
            return;
        }
        System.out.println("\nEligible Projects:");
        MenuPrinter.printProjectTableEligible(eligible, applicant, ctrl);
    }

    protected static void viewAllListings(ProjectController projCtrl,
                                        ApplicantController ctrl,
                                        Applicant applicant) {
        List<Project> all = projCtrl.listAll()
                                            .stream()
                                            .filter(Project::isVisible)
                                            .toList();
        if (all.isEmpty()) {
            System.out.println("No projects available.");
            return;
        }
        System.out.println("\nAll Projects:");
        MenuPrinter.printProjectTableAll(all, applicant, ctrl);
    }

    public static void withdrawApplication(Scanner sc,
                                            Applicant applicant,
                                            ApplicantController ctrl) {
        Optional<BTOApplication> oa = applicant.getCurrentApplication();
        if (oa.isEmpty()) {
            System.out.println("No application to withdraw.");
            return;
        }
        BTOApplication app = oa.get();
        System.out.println(app);

        try {
            System.out.print("Confirm by entering your NRIC: ");
            String n = Input.getStringInput(sc);
            if (n.equalsIgnoreCase(applicant.getNric())) {
                ctrl.requestWithdrawal();
            } else {
                System.out.println("NRIC mismatch.");
            }
        } catch (Input.InputExitException e) {
            System.out.println("Cancelled.");
        }
    }

    public static void viewAccountDetails(Applicant applicant) {
        System.out.println("\nAccount Details:");
        System.out.println(applicant);
    }
}
