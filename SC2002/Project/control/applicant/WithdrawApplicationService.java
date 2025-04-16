package SC2002.Project.control.applicant;

import java.util.Scanner;
import SC2002.Project.entity.BTOapplication;
import SC2002.Project.entity.ApplicantBase;
import SC2002.Project.util.Input;

/**
 * Allows an applicant to withdraw their submitted application.
 */
public class WithdrawApplicationService {
    /**
     * Withdraws the active application after verifying the applicant's NRIC.
     *
     * @param sc the Scanner for input.
     * @param application the BTOapplication to be withdrawn.
     * @param applicant the applicant performing the withdrawal.
     */
    public static void withdraw(Scanner sc, BTOapplication application, ApplicantBase applicant) {
        if (application == null) {
            System.out.println("No active application found. Withdrawal aborted.");
            return;
        }
        System.out.print("Type your NRIC to confirm withdrawal: ");
        String confirmation = Input.getStringInput(sc);
        if (confirmation.equals(applicant.getNRIC())) {
            application.withdraw();
            System.out.println("Application withdrawn successfully.");
        } else {
            System.out.println("NRIC does not match. Withdrawal cancelled.");
        }
    }
}
