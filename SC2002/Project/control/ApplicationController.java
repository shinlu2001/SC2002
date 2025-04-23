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
     * 
     * @return the newly created BTOApplication, or null if creation failed.
     */
    public BTOApplication createApplication(Applicant applicant, Project project, String flatType) {
        // Delegate eligibility, uniqueness, etc., to the BTOApplication constructor and
        // domain logic
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
     * Supports transitions: PENDING→SUCCESS, PENDING→REJECTED, SUCCESS→BOOKED,
     * PENDING→WITHDRAWN, SUCCESS→WITHDRAWN
     * 
     * @return true if the transition was performed; false otherwise.
     */
    public boolean changeStatus(int applicationId, ApplicationStatus newStatus) {
        BTOApplication app = findById(applicationId);
        if (app == null) {
            return false;
        }
        ApplicationStatus current = app.getStatus();
        Project project = app.getProject();
        String flatType = app.getRoomType();

        switch (newStatus) {
            case SUCCESS -> {
                if (current == ApplicationStatus.PENDING) {
                    // Check if there are available units before approving
                    int flatIndex = project.getFlatTypeIndex(flatType);
                    if (flatIndex == -1) {
                        System.out.println("Error: Flat type " + flatType + " not found in project.");
                        return false;
                    }

                    int availableUnits = project.getAvailableUnits().get(flatIndex);
                    if (availableUnits <= 0) {
                        System.out.println(
                                "Error: No available units of type " + flatType + " in project " + project.getName());
                        app.reject(); // Auto-reject due to no availability
                        app.getApplicant().clearCurrentApplicationReference();
                        return false;
                    }

                    // Decrement available flats count when approving application
                    project.decrementAvailableUnits(flatType);
                    app.approve();
                    System.out.println("Application approved. Reserved 1 unit of " + flatType + " in project "
                            + project.getName());
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
                    // Note: We no longer need to decrement units here since they were
                    // already decremented when the application was approved
                    return true;
                }
            }
            case WITHDRAWN -> {
                // Allow withdrawal from PENDING or SUCCESS states
                if (current == ApplicationStatus.PENDING) {
                    app.confirmWithdrawal();
                    // Clean up applicant reference to allow new applications
                    app.getApplicant().clearCurrentApplicationReference();
                    System.out.println(
                            "Application ID " + app.getId() + " withdrawn. Applicant can now apply for a new project.");
                    return true;
                } else if (current == ApplicationStatus.SUCCESS) {
                    // Increment the available units when an approved application is withdrawn
                    project.incrementAvailableUnits(flatType);
                    app.confirmWithdrawal();
                    app.getApplicant().clearCurrentApplicationReference();
                    System.out.println("Application ID " + app.getId()
                            + " withdrawn. Flat unit returned to available pool. Applicant can now apply for a new project.");
                    return true;
                }
            }
            default -> {
            }
        }
        // Unsupported transition
        return false;
    }

    /**
     * Approve a pending application
     * 
     * @param application The application to approve
     * @return true if successful, false otherwise
     */
    public boolean approveApplication(BTOApplication application) {
        if (application == null || application.getStatus() != ApplicationStatus.PENDING) {
            return false;
        }
        return changeStatus(application.getId(), ApplicationStatus.SUCCESS);
    }

    /**
     * Reject a pending application
     * 
     * @param application The application to reject
     * @return true if successful, false otherwise
     */
    public boolean rejectApplication(BTOApplication application) {
        if (application == null || application.getStatus() != ApplicationStatus.PENDING) {
            return false;
        }
        return changeStatus(application.getId(), ApplicationStatus.REJECTED);
    }

    /**
     * Set a pending or approved application status to withdrawn
     * 
     * @param application The application to withdraw
     * @return true if successful, false otherwise
     */
    public boolean withdrawApplication(BTOApplication application) {
        if (application == null) {
            return false;
        }

        if (application.getStatus() != ApplicationStatus.PENDING &&
                application.getStatus() != ApplicationStatus.SUCCESS) {
            return false;
        }

        return changeStatus(application.getId(), ApplicationStatus.WITHDRAWN);
    }

    /**
     * Request booking for an approved application
     * 
     * @param application The application to book
     * @return true if successful, false otherwise
     */
    public boolean bookApplication(BTOApplication application) {
        if (application == null || application.getStatus() != ApplicationStatus.SUCCESS) {
            return false;
        }
        return changeStatus(application.getId(), ApplicationStatus.BOOKED);
    }

    /**
     * Request withdrawal for an application
     * 
     * @param application The application to request withdrawal for
     * @return true if successful, false otherwise
     */
    public boolean requestWithdrawal(BTOApplication application) {
        if (application == null ||
                (application.getStatus() != ApplicationStatus.PENDING &&
                        application.getStatus() != ApplicationStatus.SUCCESS)) {
            return false;
        }

        application.requestWithdrawal();
        return true;
    }

    /**
     * Confirm a withdrawal request
     * 
     * @param application The application to confirm withdrawal for
     * @return true if successful, false otherwise
     */
    public boolean confirmWithdrawalRequest(BTOApplication application) {
        if (application == null || !application.isWithdrawalRequested()) {
            return false;
        }

        if (application.getStatus() == ApplicationStatus.BOOKED) {
            // Cannot withdraw a booked application without officer intervention
            return false;
        }

        return withdrawApplication(application);
    }

    /**
     * Reject a withdrawal request
     * 
     * @param application The application to reject withdrawal for
     * @return true if successful, false otherwise
     */
    public boolean rejectWithdrawalRequest(BTOApplication application) {
        if (application == null || !application.isWithdrawalRequested()) {
            return false;
        }

        application.setWithdrawalRequested(false);
        return true;
    }

    /**
     * Get all applications with withdrawal requests
     * 
     * @return List of applications with withdrawal requests
     */
    public List<BTOApplication> getWithdrawalRequests() {
        return dataStore.getApplications().stream()
                .filter(BTOApplication::isWithdrawalRequested)
                .collect(Collectors.toList());
    }

    /**
     * Get all pending applications
     * 
     * @return List of pending applications
     */
    public List<BTOApplication> getPendingApplications() {
        return dataStore.getApplications().stream()
                .filter(app -> app.getStatus() == ApplicationStatus.PENDING)
                .collect(Collectors.toList());
    }

    /**
     * Get all applications managed by a specific manager
     * 
     * @param managerId The ID of the manager
     * @return List of applications for projects managed by the specified manager
     */
    public List<BTOApplication> getApplicationsByManager(int managerId) {
        return dataStore.getApplications().stream()
                .filter(app -> app.getProject().getManager().getManagerId() == managerId)
                .collect(Collectors.toList());
    }
}
