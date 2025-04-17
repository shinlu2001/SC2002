// SC2002/Project/control/ApplicantController.java
package SC2002.Project.control;

import SC2002.Project.entity.Applicant;
import SC2002.Project.entity.BTOApplication;
import SC2002.Project.entity.Enquiry;
import SC2002.Project.entity.Project;

import java.util.List;

public class ApplicantController {
    private final Applicant applicant;

    public ApplicantController(Applicant applicant) {
        // TODO
        this.applicant = applicant;
    }

    /** 1. Apply for a project */
    public boolean applyForProject(int projectId, String flatType) {
        // TODO
        return false;
    }

    /** 2. Manage active application: view details */
    public BTOApplication viewCurrentApplication() {
        // TODO
        return null;
    }

    /** 2b. Manage active application: book if status==SUCCESSFUL */
    public boolean requestBookingForCurrentApplication() {
        // TODO
        return false;
    }

    /** 3. View only eligible listings */
    public List<Project> listEligibleProjects() {
        // TODO
        return null;
    }

    /** 4. View all listings */
    public List<Project> listAllVisibleProjects() {
        // TODO
        return null;
    }

    /** 5. Withdraw application */
    public boolean withdrawApplication() {
        // TODO
        return false;
    }

    /** 6a. Make general enquiry */
    public void submitGeneralEnquiry(String content) {
        // TODO
    }

    /** 6b. Make project-related enquiry */
    public void submitProjectEnquiry(int projectId, String content, String flatType) {
        // TODO
    }

    /** 6c. View all enquiries */
    public List<Enquiry> listEnquiries() {
        // TODO
        return null;
    }

    /** 6d. Edit an enquiry (if not replied) */
    public boolean editEnquiry(int enquiryId, String newContent) {
        // TODO
        return false;
    }

    /** 6e. Delete an enquiry (if not replied) */
    public boolean deleteEnquiry(int enquiryId) {
        // TODO
        return false;
    }

    /** 7. View account details */
    public Applicant getApplicant() {
        return applicant;
    }

    /** 8. Change account password */
    public boolean changePassword(String oldPwd, String newPwd) {
        // TODO
        return false;
    }
}
