package SC2002.Project.boundary;

import java.util.Scanner;
import SC2002.Project.entity.ApplicantBase;
import SC2002.Project.entity.BTOapplication;
import SC2002.Project.util.Input;

public class BTOService {

    /**
     * Displays the active application for the given applicant.
     *
     * @param sc the Scanner instance for input (if needed for further interactions)
     * @param applicant the applicant whose application details should be displayed.
     */
    public static void displayApplication(Scanner sc, ApplicantBase applicant) {
        BTOapplication application = applicant.getApplication();
        if (application == null) {
            System.out.println("No active application found.");
        } else {
            System.out.println("Displaying Application Details:");
            application.printDetails();
        }
    }
}
