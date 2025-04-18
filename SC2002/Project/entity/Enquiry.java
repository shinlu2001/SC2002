// SC2002/Project/entity/Enquiry.java
package SC2002.Project.entity;

import java.util.Optional;
import SC2002.Project.util.IdGenerator;
import SC2002.Project.entity.*;

/**
 * Entity class representing an enquiry in the system.
 */
public class Enquiry {
    private final int id;
    private String content;
    private String response="";
    private Project project;  // null for general enquiries
    private String flatType;  // null for general enquiries
    private User creator;
    private User respondent;

    public Enquiry(User creator, String content) {
        this.id = IdGenerator.nextEnquiryId();
        this.creator = creator;
        this.content = content;
    }

    public int getId() {
        return id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getResponse() {
        return response;
    }

    public void setResponse(String response) {
        this.response = response;
    }

    public Project getProject() {
        return project;
    }

    public void setProject(Project project) {
        this.project = project;
    }

    public String getFlatType() {
        return flatType;
    }

    public void setFlatType(String flatType) {
        this.flatType = flatType;
    }

    public User getCreator() {
        return creator;
    }

    public User getRespondent() {
        return respondent;
    }

    public void setRespondent(User respondent) {
        this.respondent = respondent;
    }

    public boolean isAnswered() {
        return response != null && !response.isEmpty();
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Enquiry ID: ").append(id).append("\n");
        if (project != null) {
            sb.append("Project: ").append(project.getName()).append("\n");
            sb.append("Flat Type: ").append(flatType).append("\n");
        }
        sb.append("Content: ").append(content).append("\n");
        if (isAnswered()) {
            sb.append("Response: ").append(response).append("\n");
            sb.append("Replied by: ").append(respondent.getFirstName()).append("\n");
        } else {
            sb.append("Status: Pending\n");
        }
        return sb.toString();
    }
}