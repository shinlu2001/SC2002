// SC2002/Project/boundary/ApplicantUI.java
package SC2002.Project.boundary;

import java.util.Scanner;

public final class ApplicantUI {

    private final Applicant user;        // the entity –
    private final Scanner  sc;           // handed over from BTOsystem

    public ApplicantUI(Applicant u, Scanner sc) { this.user = u; this.sc = sc; }

    /** Entry‑point that replaces Applicant.start_menu() */
    public void menu() {
        System.out.printf("Welcome to HDB BTO Management System, %s!%n", user.get_firstname());

        while (true) {
            try {
                System.out.println("""
                        ====================================================================================================================
                                                  A P P L I C A N T   M E N U
                        ====================================================================================================================""");
                user.menu.printApplicantMenu();                              // ◀ prints from old Menu class
                int choice = Input.getIntInput(sc);
                System.out.println("====================================================================================================================");

                switch (ApplicantOption.values()[choice-1]) {
                    case APPLY              -> doApply();
                    case VIEW_APPLICATION   -> doViewApplication();
                    case VIEW_ELIGIBLE      -> user.view_eligible_listings();
                    case VIEW_LISTINGS      -> user.view_listings();
                    case WITHDRAW           -> doWithdraw();
                    case ENQUIRY            -> user.manage_enquiry(sc);
                    case ACCOUNT            -> showAccount();
                    case CHANGE_PASSWORD    -> changePassword();
                    case EXIT               -> { System.out.println("Logged out."); return; }
                }

            } catch (Input.InputExitException e) {           // user typed “back” or “exit”
                System.out.println("Operation cancelled. Returning to Applicant menu.");
            } catch (Exception e) {
                System.out.println("Invalid input – please try again.");
            }
        }
    }

    /* ───────────────────────── helpers ───────────────────────── */

    private void doApply() {
        try {
            if (user.getApplication() != null &&
                !user.getApplication().getStatus().matches("WITHDRAWN|REJECTED"))
            {
                System.out.println("You already have an active application.");
                return;
            }

            System.out.println("Apply for a project");
            if (user.view_eligible_listings() == 0) return;

            System.out.print("Enter ProjectID: ");
            int pid = Input.getIntInput(sc);
            Project p = BTOsystem.searchById(BTOsystem.projects, pid, Project::getId);
            if (p==null || !p.isVisible()) { System.out.println("No such project."); return; }

            System.out.print("Enter room type (2‑Room, 3‑Room, etc): ");
            String room = Input.getStringInput(sc);

            if (!user.getEligibility(room)) {
                System.out.println("Not eligible for this project and room type.");
                return;
            }

            BTOapplication app = new BTOapplication(user, p, room.toUpperCase());
            BTOsystem.applications.add(app);
            user.application = app;               // maintain old state
            System.out.println("Application submitted!");

        } catch (Input.InputExitException ignored) { /* handled by outer loop */ }
    }

    private void doViewApplication() {
        if (user.getApplication() == null) {
            System.out.println("You have no active application.");
            return;
        }
        user.getApplication().get_details();

        if ("SUCCESSFUL".equalsIgnoreCase(user.getApplication().getStatus())) {
            System.out.print("Enter 1 to book a flat (any other key to exit): ");
            int ans = Input.getIntInput(sc);
            if (ans == 1) {
                System.out.println("Booking request sent – an officer will contact you.");
                user.getApplication().requestBooking();
            }
        }
    }

    private void doWithdraw() {
        if (user.getApplication()==null) { System.out.println("Nothing to withdraw."); return; }
        if ("REJECTED".equals(user.getApplication().getStatus())) {
            System.out.println("Application already rejected."); return;
        }

        user.getApplication().get_details();
        System.out.print("(Press Enter to continue)"); sc.nextLine();
        System.out.print("Enter NRIC to confirm withdrawal: ");
        String nric = Input.getStringInput(sc);
        if (nric.equals(user.get_nric())) {
            user.getApplication().withdraw();
            System.out.println("Withdrawal request submitted.");
        } else {
            System.out.println("Wrong NRIC – withdrawal cancelled.");
        }
    }

    private void showAccount() {
        System.out.println("Account details");
        System.out.println("====================================================================================================================");
        user.to_string();
    }

    private void changePassword() {
        System.out.println("Change your password");
        System.out.print("Enter current password: ");
        String old = Input.getStringInput(sc);
        System.out.print("Enter new password: ");
        String n1  = Input.getStringInput(sc);
        System.out.print("Enter new password again: ");
        String n2  = Input.getStringInput(sc);

        if (!user.verify_password(old))              System.out.println("Current password incorrect.");
        else if (!n1.equals(n2))                     System.out.println("New passwords do not match.");
        else { user.change_password(n1); System.out.println("Password changed."); }
    }

    /* keep enum in one place for clarity */
    private enum ApplicantOption {
        APPLY, VIEW_APPLICATION, VIEW_ELIGIBLE, VIEW_LISTINGS,
        WITHDRAW, ENQUIRY, ACCOUNT, CHANGE_PASSWORD, EXIT
    }
}
