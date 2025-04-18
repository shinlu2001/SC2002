// SC2002/Project/control/EnquiryController.java
package SC2002.Project.control;

import SC2002.Project.control.persistence.DataStore;
import SC2002.Project.entity.*;

import java.util.ArrayList;
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

    public boolean createGeneralEnquiry(User user, String content) {
        if (content == null || content.trim().isEmpty()) {
            return false;
        }

        Enquiry enquiry = new Enquiry(user, content);
        dataStore.getEnquiries().add(enquiry);
        return true;
    }

    public boolean createProjectEnquiry(User user, Project project, String content, String flatType) {
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
        
        dataStore.getEnquiries().add(enquiry);
        project.addEnquiry(enquiry);
        return true;
    }

    public List<Project> getVisibleProjects() {
        return projectController.getAllProjects().stream()
                .filter(Project::isVisible)
                .collect(Collectors.toList());
    }

    public Project getProjectById(int projectId) {
        return projectController.getProjectById(projectId);
    }

    public List<Enquiry> getUserEnquiries(User user) {
        return dataStore.getEnquiries().stream()
                .filter(e -> e.getCreator().equals(user))
                .collect(Collectors.toList());
    }

    public List<Enquiry> getEditableEnquiries(User user) {
        return dataStore.getEnquiries().stream()
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

    public boolean deleteEnquiry(User user, int enquiryId) {
        Enquiry enquiry = findEnquiryById(enquiryId);
        if (enquiry == null || !enquiry.getCreator().equals(user)) {
            return false;
        }

        dataStore.getEnquiries().remove(enquiry);
        if (enquiry.getProject() != null) {
            enquiry.getProject().getEnquiries().remove(enquiry);
        }
        return true;
    }

    public boolean respondToEnquiry(User staff, int enquiryId, String response) {
        if (response == null || response.trim().isEmpty()) {
            return false;
        }

        Enquiry enquiry = findEnquiryById(enquiryId);
        if (enquiry == null || enquiry.isAnswered()) {
            return false;
        }

        enquiry.setResponse(response);
        enquiry.setRespondent(staff);
        return true;
    }

    private Enquiry findEnquiryById(int enquiryId) {
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
}
