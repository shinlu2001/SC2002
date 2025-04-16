package SC2002.Project.control.applicant;

import java.util.Scanner;
import SC2002.Project.entity.ApplicantBase;
import SC2002.Project.entity.BTOapplication;
import SC2002.Project.entity.Project;
import SC2002.Project.util.Input;
import SC2002.Project.ui.MainUI;

public class applyBTOServiceApplicant {
    private ApplicantBase applicant;

    public applyBTOServiceApplicant(ApplicantBase applicant) {
        this.applicant = applicant;
    }

    /**
     * Allows the applicant to choose a project from the database and apply.
     *
     * @param sc the Scanner instance for input.
     * @param applicant the applicant applying for a project.
     * @param projectDisplayer (Optional) can be used to show project details.
     */
    public void applyProject(Scanner sc, ApplicantBase applicant, Object projectDisplayer) {
        // Check that there is at least one project available
        if (MainUI.projects.isEmpty()) {
            System.out.println("No projects available in the database.");
            return;
        }
        
        // Display available projects with ID and project name
        System.out.println("Available Projects:");
        for (Project p : MainUI.projects) {
            System.out.println("ID: " + p.getId() + " - " + p.getProjectName());
        }
        
        System.out.print("Enter the project ID you want to apply for: ");
        int projId = Input.getIntInput(sc);
        Project selectedProject = null;
        for (Project p : MainUI.projects) {
            if (p.getId() == projId) {
                selectedProject = p;
                break;
            }
        }
        if (selectedProject == null) {
            System.out.println("Project not found.");
            return;
        }
        
        // Ask for the flat type the applicant wishes to apply for
        System.out.print("Enter flat type (e.g., 2-Room or 3-Room): ");
        String flatType = Input.getStringInput(sc);
        if (!applicant.isEligible(flatType)) {
            System.out.println("You are not eligible to apply for this flat type.");
            return;
        }
        
        // Create a BTOapplication with the selected project and flat type
        BTOapplication application = new BTOapplication(applicant, selectedProject, flatType);
        applicant.setApplication(application);
        MainUI.applications.add(application);
        System.out.println("Application submitted successfully to project " + selectedProject.getProjectName());
    }
}
