// SC2002/Project/control/EnquiryController.java
package SC2002.Project.control;

import SC2002.Project.control.persistence.DataStore;
import SC2002.Project.entity.*;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Controller class for managing enquiries in the system.
 */
public class EnquiryController {
    private final DataStore dataStore;
    private final ProjectController projectController;

    public EnquiryController() {
        this.dataStore = DataStore.getInstance();
        this.projectController = new ProjectController();
    }

    public boolean createGeneralEnquiry(User user, String content, ApplicantController applicantController) {
        if (content == null || content.trim().isEmpty()) {
            return false;
        }

        Enquiry enquiry = new Enquiry(user, content);
        applicantController.addEnquiry(enquiry); // stores enquiry for each applicant
        dataStore.getEnquiries().add(enquiry);
        return true;
    }

    public boolean createProjectEnquiry(User user, Project project, String content, String flatType, ApplicantController applicantController, ProjectController projectController) {
        if (content == null || content.trim().isEmpty() || project == null) {
            return false;
        }

        // Check if the flat type exists in the project
        if (!project.getFlatTypes().contains(flatType)) {
            return false;
        }

        Enquiry enquiry = new Enquiry(user, content);
        enquiry.setProject(project);
        enquiry.setFlatType(flatType);
        applicantController.addEnquiry(enquiry);
        dataStore.getEnquiries().add(enquiry);
        projectController.addEnquiry(project, enquiry);
        return true;
    }

    public List<Project> getVisibleProjects() {
        return projectController.listAll().stream()
                .filter(Project::isVisible)
                .collect(Collectors.toList());
    }

    public Project getProjectById(int projectId) {
        return projectController.findById(projectId);
    }

    public List<Enquiry> getUserEnquiries(Applicant user) {
        return user.getEnquiries();
    }

    public List<Enquiry> getEditableEnquiries(Applicant user) {
        return user.getEnquiries().stream()
                .filter(e -> e.getCreator().equals(user) && !e.isAnswered())
                .collect(Collectors.toList());
    }

    public boolean editEnquiry(User user, int enquiryId, String newContent) {
        if (newContent == null || newContent.trim().isEmpty()) {
            return false;
        }

        Enquiry enquiry = findEnquiryById(enquiryId);
        if (enquiry == null || !enquiry.getCreator().equals(user) || enquiry.isAnswered()) {
            return false;
        }

        enquiry.setContent(newContent);
        return true;
    }

    public boolean deleteEnquiry(User user, int enquiryId, ApplicantController applicantController, ProjectController projectController) {
        Enquiry enquiry = findEnquiryById(enquiryId);
        if (enquiry == null || !enquiry.getCreator().equals(user)) {
            return false;
        }
        applicantController.deleteEnquiry(enquiry); // remove from applicant enquiry list
        dataStore.getEnquiries().remove(enquiry); // remove from outer system
        if (enquiry.getProject() != null) {
            projectController.deleteEnquiry(enquiry.getProject(), enquiry); //remove from project enquiry list
        }
        
        return true;
    }

    public boolean respondToEnquiry(User staff, int enquiryId, String response) {
        if (response == null || response.trim().isEmpty()) {
            System.out.println("Error: Response cannot be empty.");
            return false;
        }

        Enquiry enquiry = findEnquiryById(enquiryId);
        if (enquiry == null) {
            System.out.println("Error: Enquiry not found.");
            return false;
        }
        if (enquiry.isAnswered()) {
            System.out.println("Error: Enquiry has already been answered.");
            return false;
        }

        // Check permissions based on user role and enquiry type
        boolean permitted = false;
        if (staff instanceof HDB_Manager) {
            // Managers can only answer enquiries for projects they manage
            Project project = enquiry.getProject();
            if (project == null) {
                // All managers can answer general enquiries
                permitted = true;
            } else if (project.getManager() != null && project.getManager().equals(staff)) {
                // Manager can only answer project enquiries they manage
                permitted = true;
            } else {
                System.out.println("Error: Manager is not assigned to this project.");
            }
        } else if (staff instanceof HDB_Officer) {
            Project project = enquiry.getProject();
            if (project != null) {
                // Officers can only answer enquiries for projects they are assigned to
                if (project.getAssignedOfficers().contains((HDB_Officer) staff)) {
                    permitted = true;
                } else {
                    System.out.println("Error: Officer is not assigned to this project.");
                }
            } else { // officers are able to respond to general enquiries
                permitted = true;
            }
        } else {
            // Other user types (e.g., Applicant) cannot respond
            System.out.println("Error: Only Managers or assigned Officers can respond to enquiries.");
        }

        if (!permitted) {
            return false;
        }

        enquiry.setResponse(response);
        enquiry.setRespondent(staff);
        System.out.println("Enquiry ID " + enquiryId + " responded successfully.");
        return true;
    }

    public Enquiry findEnquiryById(int enquiryId) {
        return dataStore.getEnquiries().stream()
                .filter(e -> e.getId() == enquiryId)
                .findFirst()
                .orElse(null);
    }

    public List<Enquiry> getProjectEnquiries(Project project) {
        return dataStore.getEnquiries().stream()
                .filter(e -> project.equals(e.getProject()))
                .collect(Collectors.toList());
    }

    public List<Enquiry> getGeneralEnquiries() {
        return dataStore.getEnquiries().stream()
                .filter(e -> e.getProject() == null)
                .collect(Collectors.toList());
    }

    /**
     * Retrieves all enquiries in the system
     * @return A list of all enquiries
     */
    public List<Enquiry> getAllEnquiries() {
        return dataStore.getEnquiries();
    }
}
