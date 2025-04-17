// SC2002/Project/control/EnquiryController.java
package SC2002.Project.control;

import SC2002.Project.entity.Enquiry;
import SC2002.Project.entity.Project;
import SC2002.Project.entity.Applicant;
import SC2002.Project.entity.HDB_Officer;

import java.util.List;

public class EnquiryController {
    /** General enquiry */
    public Enquiry createGeneralEnquiry(Applicant a, String content) {
        // TODO
        return null;
    }

    /** Project-related enquiry */
    public Enquiry createProjectEnquiry(Applicant a, Project p, String flatType, String content) {
        // TODO
        return null;
    }

    /** List by applicant */
    public List<Enquiry> listByApplicant(int applicantId) {
        // TODO
        return null;
    }

    /** List by project */
    public List<Enquiry> listByProject(int projectId) {
        // TODO
        return null;
    }

    /** Edit enquiry (only if no response yet) */
    public boolean editEnquiry(int enquiryId, String newContent) {
        // TODO
        return false;
    }

    /** Delete enquiry (only if no response yet) */
    public boolean deleteEnquiry(int enquiryId) {
        // TODO
        return false;
    }

    /** Reply to enquiry (officer/manager) */
    public boolean replyEnquiry(int enquiryId, HDB_Officer officer, String response) {
        // TODO
        return false;
    }
}
