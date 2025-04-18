package SC2002.Project.control;

import SC2002.Project.control.persistence.DataStore;
import SC2002.Project.entity.Applicant;
import SC2002.Project.entity.BTOApplication;
import SC2002.Project.entity.Project;
import SC2002.Project.entity.enums.ApplicationStatus;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Centralized controller for creating and querying BTO applications.
 */
public class ApplicationController {

    private final DataStore dataStore = DataStore.getInstance();

    /**
     * Create a new application for an applicant on a project with a flat type.
     * @return the newly created BTOApplication, or null if creation failed.
     */
    public BTOApplication createApplication(Applicant applicant, Project project, String flatType) {
        // Delegate eligibility, uniqueness, etc., to the BTOApplication constructor and domain logic
        BTOApplication application = new BTOApplication(applicant, project, flatType);
        dataStore.getApplications().add(application);
        applicant.setCurrentApplication(application);
        return application;
    }

    /**
     * Find an application by its unique ID.
     */
    public BTOApplication findById(int id) {
        return dataStore.getApplications().stream()
                .filter(app -> app.getId() == id)
                .findFirst()
                .orElse(null);
    }

    /**
     * List all applications submitted for a given project.
     */
    public List<BTOApplication> listApplicationsForProject(int projectId) {
        return dataStore.getApplications().stream()
                .filter(app -> app.getProject().getId() == projectId)
                .collect(Collectors.toList());
    }

    /**
     * List all applications submitted by a given applicant.
     */
    public List<BTOApplication> listApplicationsByApplicant(int applicantId) {
        return dataStore.getApplications().stream()
                .filter(app -> app.getApplicant().getId() == applicantId)
                .collect(Collectors.toList());
    }

    /**
     * Change the status of an existing application.
     * Supports transitions: PENDING→SUCCESS, PENDING→REJECTED, SUCCESS→BOOKED.
     * @return true if the transition was performed; false otherwise.
     */
    public boolean changeStatus(int applicationId, ApplicationStatus newStatus) {
        BTOApplication app = findById(applicationId);
        if (app == null) {
            return false;
        }
        ApplicationStatus current = app.getStatus();
        switch (newStatus) {
            case SUCCESS -> {
                if (current == ApplicationStatus.PENDING) {
                    app.approve();
                    return true;
                }
            }
            case REJECTED -> {
                if (current == ApplicationStatus.PENDING) {
                    app.reject();
                    // Clean up applicant reference if needed
                    app.getApplicant().clearCurrentApplicationReference();
                    return true;
                }
            }
            case BOOKED -> {
                if (current == ApplicationStatus.SUCCESS) {
                    app.requestBooking();
                    return true;
                }
            }
            default -> {
            }
        }
        // Unsupported transition
        return false;
    }
}
