// SC2002/Project/control/OfficerController.java
package SC2002.Project.control;

import SC2002.Project.entity.BTOApplication;
import SC2002.Project.entity.Enquiry;
import SC2002.Project.entity.HDB_Officer;
import SC2002.Project.entity.Project;
import SC2002.Project.entity.enums.RegistrationStatus;
import java.util.List;

public class OfficerController {
    private final HDB_Officer officer;

    public OfficerController(HDB_Officer officer) {
        // TODO
        this.officer = officer;
    }

    // --- Applicant capabilities (same as above) ---
    public boolean applyForProject(int projectId, String flatType) { /* TODO */ return false; }
    public BTOApplication viewCurrentApplication()               { /* TODO */ return null; }
    public boolean withdrawApplication()                         { /* TODO */ return false; }
    public List<Project> listEligibleProjects()                  { /* TODO */ return null; }
    public List<Project> listAllVisibleProjects()                { /* TODO */ return null; }
    public boolean requestBookingForCurrentApplication()         { /* TODO */ return false; }

    // --- Enquiry capabilities ---
    /** 6. Manage user enquiries (for projects they handle) */
    public List<Enquiry> listUserEnquiries()                     { /* TODO */ return null; }
    public boolean replyToEnquiry(int enquiryId, String response) { /* TODO */ return false; }

    /** 7. Manage own enquiries (as applicant) */
    public List<Enquiry> listOwnEnquiries()                      { /* TODO */ return null; }
    public boolean editOwnEnquiry(int enquiryId, String newContent) { /* TODO */ return false; }
    public boolean deleteOwnEnquiry(int enquiryId)               { /* TODO */ return false; }

    /** 8. View account details */
    public HDB_Officer getOfficer()                              { return officer; }

    /** 9. Change account password */
    public boolean changePassword(String oldPwd, String newPwd)  { /* TODO */ return false; }

    /** 10. Register to be an officer of a project */
    public boolean registerForProject(int projectId)             { /* TODO */ return false; }

    /** 11. Check registration status */
    public RegistrationStatus viewRegistrationStatus()           { /* TODO */ return null; }

    /** 12. View project details (regardless of visibility) */
    public Project viewProjectDetails(int projectId)             { /* TODO */ return null; }

    /** 13. Process flat booking */
    public boolean processBooking(int applicationId)             { /* TODO */ return false; }

    /** 14. View applications for assigned project */
    public List<BTOApplication> listAssignedProjectApplications() { /* TODO */ return null; }
}
