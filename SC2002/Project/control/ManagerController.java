// SC2002/Project/control/ManagerController.java
package SC2002.Project.control;

import SC2002.Project.entity.BTOApplication;
import SC2002.Project.entity.Enquiry;
import SC2002.Project.entity.HDB_Manager;
import SC2002.Project.entity.Project;
import SC2002.Project.entity.Registration;
import SC2002.Project.entity.Report;
import SC2002.Project.entity.enums.FlatType;
import SC2002.Project.entity.enums.MaritalStatus;
import SC2002.Project.entity.enums.Visibility;

import java.time.LocalDate;
import java.util.List;

public class ManagerController {
    private final HDB_Manager manager;

    public ManagerController(HDB_Manager manager) {
        // TODO
        this.manager = manager;
    }

    /** 1. Create a Project */
    public Project createProject(String name,
                                 String neighbourhood,
                                 List<FlatType> flatTypes,
                                 List<Integer> totalUnits,
                                 List<Double> prices,
                                 LocalDate openDate,
                                 LocalDate closeDate,
                                 boolean visible,
                                 int officerSlots)
    {
        // TODO
        return null;
    }

    /** 2. Edit a Project – submenu of 10 options */
    public boolean editProjectName(int projectId, String newName) { /* TODO */ return false; }
    public boolean editProjectNeighbourhood(int projectId, String newHood) { /* TODO */ return false; }
    public boolean editUnitCount(int projectId, FlatType type, int newCount) { /* TODO */ return false; }
    public boolean addFlatType(int projectId, FlatType type, int count, double price) { /* TODO */ return false; }
    public boolean removeFlatType(int projectId, FlatType type) { /* TODO */ return false; }
    public boolean editFlatPrice(int projectId, FlatType type, double newPrice) { /* TODO */ return false; }
    public boolean editOpenDate(int projectId, LocalDate newDate) { /* TODO */ return false; }
    public boolean editCloseDate(int projectId, LocalDate newDate) { /* TODO */ return false; }
    public boolean toggleVisibility(int projectId, Visibility state) { /* TODO */ return false; }
    public boolean editOfficerSlots(int projectId, int slots) { /* TODO */ return false; }

    /** 3. Delete a Project */
    public boolean deleteProject(int projectId) { /* TODO */ return false; }

    /** 4. View All Projects */
    public List<Project> listAllProjects() { /* TODO */ return null; }

    /** 5. View My Projects */
    public List<Project> listMyProjects() { /* TODO */ return null; }

    /** 6. View Officer Registrations */
    public List<Registration> listOfficerRegistrations() { /* TODO */ return null; }

    /** 7. Handle Officer Registration */
    public boolean handleOfficerRegistration(int registrationId, boolean accept) { /* TODO */ return false; }

    /** 8. Handle Officer Registration Withdrawal Requests */
    public boolean handleOfficerRegistrationWithdrawal(int registrationId, boolean accept) { /* TODO */ return false; }

    /** 9. Handle BTO Applications */
    public boolean handleApplication(int applicationId, boolean accept) { /* TODO */ return false; }

    /** 10. Handle BTO Application Withdrawal Requests */
    public boolean handleApplicationWithdrawal(int applicationId, boolean accept) { /* TODO */ return false; }

    /** 11. Generate Applicant Report – 4 filter choices */
    public Report generateReportAll() { /* TODO */ return null; }
    public Report generateReportByMaritalStatus(MaritalStatus ms) { /* TODO */ return null; }
    public Report generateReportByFlatType(FlatType ft) { /* TODO */ return null; }
    public Report generateReportByMaritalAndFlat(MaritalStatus ms, FlatType ft) { /* TODO */ return null; }

    /** 12. View All Enquiries */
    public List<Enquiry> listAllEnquiries() { /* TODO */ return null; }

    /** 13. Handle Project Enquiries */
    public boolean replyToEnquiry(int enquiryId, String response) { /* TODO */ return false; }

    /** 14. View account details */
    public HDB_Manager getManager() { return manager; }

    /** 15. Change account password */
    public boolean changePassword(String oldPwd, String newPwd) { /* TODO */ return false; }
}
