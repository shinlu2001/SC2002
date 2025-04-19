package SC2002.Project.control;

import java.util.List;

import SC2002.Project.entity.*;
public interface StaffControllerInterface {
    public List<Project> getAssignedProjects();
    public List<Enquiry> getPendingEnquiries(EnquiryController enCtrl);
}
