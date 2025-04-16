package SC2002.Project.control.applicant;

import java.util.Scanner;
import SC2002.Project.entity.ApplicantBase;
import SC2002.Project.entity.BTOapplication;
import SC2002.Project.util.Input;

/**
 * Provides functionality for an applicant (or officer using applicant mode) to submit a BTO application.
 */
public class applyBTOServiceApplicant {
    private ApplicantBase applicant;

    public applyBTOServiceApplicant(ApplicantBase applicant) {
        this.applicant = applicant;
    }

    /**
     * Simulates the process of applying for a BTO project.
     *
     * @param sc the Scanner instance for input.
     * @param applicant the applicant applying for the project.
     * @param projectDisplayer an object used for displaying projects (not implemented fully here).
     */
    public void applyProject(Scanner sc, ApplicantBase applicant, Object projectDisplayer) {
        System.out.print("Enter the Project Name you wish to apply for: ");
        String projectName = Input.getStringInput(sc);
        // In a full implementation, you would locate the project.
        System.out.println("Simulated: Applying to project '" + projectName + "'.");
        // Create a dummy application:
        // BTOapplication application = new BTOapplication(applicant, foundProject, "2-Room");
        // applicant.setApplication(application);
        System.out.println("Application submitted successfully (simulation).");
    }
}
