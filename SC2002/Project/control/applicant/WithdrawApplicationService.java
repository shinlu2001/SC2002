package SC2002.Project.control.applicant;
import java.util.*;

import SC2002.Project.ApplicantBase;
import SC2002.Project.BTOapplication;
import SC2002.Project.Input;

public class WithdrawApplicationService {
    public static void withdraw(Scanner sc, BTOapplication application, ApplicantBase applicant) {
    if (application==null) {
        System.out.println("Nothing to withdraw.");
        return;
    }
    else if (application.getStatus().equals("REJECTED")) {
        System.out.println("Application already rejected. Press enter to return to role menu");
        sc.nextLine();
        return;
    }
    System.out.println("Withdraw Application");
    if (application != null) {
        application.get_details();
        // Allow user to press Enter to continue
        System.out.println("(Press Enter to continue)");
        sc.nextLine();
        System.out.println("Enter NRIC to confirm withdrawal: ");
        String confirm = Input.getStringInput(sc);
        if (confirm.equals(applicant.get_nric())) {
            application.withdraw();
            System.out.println("Withdrawal request has been submitted.");
        } else {
            System.out.println("Wrong NRIC, Withdrawal Unsuccessful.");
        }
        System.out.println("====================================================================================================================");
    } 
}
}
