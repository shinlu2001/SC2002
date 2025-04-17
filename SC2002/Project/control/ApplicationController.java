// SC2002/Project/control/ApplicationController.java
package SC2002.Project.control;

import SC2002.Project.entity.Applicant;
import SC2002.Project.entity.BTOApplication;
import SC2002.Project.entity.Project;
import SC2002.Project.entity.enums.ApplicationStatus;

import java.util.List;

public class ApplicationController {
    /** Create a new application */
    public BTOApplication createApplication(Applicant a, Project p, String flatType) {
        // TODO
        return null;
    }

    /** Find by ID */
    public BTOApplication findById(int id) {
        // TODO
        return null;
    }

    /** List applications for a given project */
    public List<BTOApplication> listApplicationsForProject(int projectId) {
        // TODO
        return null;
    }

    /** List applications by applicant */
    public List<BTOApplication> listApplicationsByApplicant(int applicantId) {
        // TODO
        return null;
    }

    /** Change status (PENDING→SUCCESSFUL→BOOKED or PENDING→UNSUCCESSFUL) */
    public boolean changeStatus(int applicationId, ApplicationStatus status) {
        // TODO
        return false;
    }
}
