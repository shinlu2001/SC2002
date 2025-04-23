package SC2002.Project.control;

import SC2002.Project.control.persistence.DataStore;
import SC2002.Project.entity.Applicant;
import SC2002.Project.entity.BTOApplication;
import SC2002.Project.entity.Project;
import SC2002.Project.entity.enums.ApplicationStatus;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Centralized controller for managing the entire BTO application lifecycle.
 * <p>
 * This controller handles all operations related to BTO applications:
 * <ul>
 * <li>Creating new applications</li>
 * <li>Managing application status transitions</li>
 * <li>Processing withdrawal requests</li>
 * <li>Querying applications by various criteria</li>
 * <li>Coordinating with project availability</li>
 * </ul>
 * </p>
 * <p>
 * Fulfills the following requirements:
 * <ul>
 * <li>Allows applicants to apply for a single project at a time</li>
 * <li>Manages application status transitions (pending →
 * successful/unsuccessful/withdrawn)</li>
 * <li>Supports manager approval/rejection of applications</li>
 * <li>Supports flat booking for successful applications</li>
 * <li>Handles withdrawal requests at various stages</li>
 * <li>Updates flat availability based on application status changes</li>
 * </ul>
 * </p>
 */
public class ApplicationController {

    private final DataStore dataStore = DataStore.getInstance();

    /**
     * Creates a new application for an applicant on a project with a specified flat
     * type.
     * This fulfills the requirement that applicants can apply for projects,
     * with appropriate validation of eligibility to be done at the UI level.
     * 
     * @param applicant The applicant submitting the application
     * @param project   The project being applied for
     * @param flatType  The type of flat being requested (e.g., "2-ROOM")
     * @return The newly created BTOApplication, or null if creation failed
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
     * Finds an application by its unique ID.
     * 
     * @param id The ID of the application to find
     * @return The application with the specified ID, or null if not found
     */
    public BTOApplication findById(int id) {
        return dataStore.getApplications().stream()
                .filter(app -> app.getId() == id)
                .findFirst()
                .orElse(null);
    }

    /**
     * Lists all applications submitted for a given project.
     * This supports the requirement for managers to view
     * applications for their managed projects.
     * 
     * @param projectId The ID of the project
     * @return A list of applications for the specified project
     */
    public List<BTOApplication> listApplicationsForProject(int projectId) {
        return dataStore.getApplications().stream()
                .filter(app -> app.getProject().getId() == projectId)
                .collect(Collectors.toList());
    }

    /**
     * Lists all applications submitted by a given applicant.
     * This supports the requirement for applicants to view
     * their application status.
     * 
     * @param applicantId The ID of the applicant
     * @return A list of applications from the specified applicant
     */
    public List<BTOApplication> listApplicationsByApplicant(int applicantId) {
        return dataStore.getApplications().stream()
                .filter(app -> app.getApplicant().getId() == applicantId)
                .collect(Collectors.toList());
    }

    /**
     * Changes the status of an existing application.
     * Supports various status transitions and manages flat availability:
     * <ul>
     * <li>PENDING→SUCCESS: Reserves a flat unit (decreases available count)</li>
     * <li>PENDING→REJECTED: Releases applicant to apply again</li>
     * <li>SUCCESS→BOOKED: Confirms flat booking</li>
     * <li>PENDING→WITHDRAWN: Releases applicant to apply again</li>
     * <li>SUCCESS→WITHDRAWN: Returns flat to available pool (increases available
     * count)</li>
     * </ul>
     * 
     * @param applicationId The ID of the application to update
     * @param newStatus     The desired new status
     * @return True if the transition was performed, false otherwise
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
                    // This is a manager action, so we modify the flat count
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
                    // No need to decrement units here - they were already decremented when approved
                    // by manager
                    return true;
                }
            }
            case WITHDRAWN -> {
                // Allow withdrawal from PENDING, SUCCESS, or BOOKED states
                if (current == ApplicationStatus.PENDING) {
                    app.confirmWithdrawal();
                    // Clean up applicant reference to allow new applications
                    app.getApplicant().clearCurrentApplicationReference();
                    System.out.println(
                            "Application ID " + app.getId() + " withdrawn. Applicant can now apply for a new project.");
                    // No change to flat count for pending applications
                    return true;
                } else if (current == ApplicationStatus.SUCCESS || current == ApplicationStatus.BOOKED) {
                    // Increment the available units when an approved/booked application is
                    // withdrawn
                    // This is a manager action, so we modify the flat count
                    project.incrementAvailableUnits(flatType);
                    app.confirmWithdrawal();
                    app.getApplicant().clearCurrentApplicationReference();
                    System.out.println("Application ID " + app.getId()
                            + " withdrawn. Flat unit returned to available pool. Applicant can now apply for a new project.");
                    return true;
                }
            }
            default -> {
                // Unsupported transition
            }
        }
        return false;
    }

    /**
     * Approves a pending application.
     * Fulfills the requirement for managers to approve applicant applications.
     * Checks for flat availability before approval.
     * 
     * @param application The application to approve
     * @return True if successful, false otherwise
     */
    public boolean approveApplication(BTOApplication application) {
        if (application == null || application.getStatus() != ApplicationStatus.PENDING) {
            return false;
        }
        return changeStatus(application.getId(), ApplicationStatus.SUCCESS);
    }

    /**
     * Rejects a pending application.
     * Fulfills the requirement for managers to reject applicant applications.
     * 
     * @param application The application to reject
     * @return True if successful, false otherwise
     */
    public boolean rejectApplication(BTOApplication application) {
        if (application == null || application.getStatus() != ApplicationStatus.PENDING) {
            return false;
        }
        return changeStatus(application.getId(), ApplicationStatus.REJECTED);
    }

    /**
     * Sets a pending or approved application status to withdrawn.
     * Fulfills the requirement for approving withdrawal requests.
     * Returns flat units to the available pool if the application was approved.
     * 
     * @param application The application to withdraw
     * @return True if successful, false otherwise
     */
    public boolean withdrawApplication(BTOApplication application) {
        if (application == null) {
            return false;
        }

        if (application.getStatus() != ApplicationStatus.PENDING &&
                application.getStatus() != ApplicationStatus.SUCCESS &&
                application.getStatus() != ApplicationStatus.BOOKED) {
            return false;
        }

        return changeStatus(application.getId(), ApplicationStatus.WITHDRAWN);
    }

    /**
     * Requests booking for an approved application.
     * Fulfills the requirement that successful applicants can book a flat.
     * 
     * @param application The application to book
     * @return True if successful, false otherwise
     */
    public boolean bookApplication(BTOApplication application) {
        if (application == null || application.getStatus() != ApplicationStatus.SUCCESS) {
            return false;
        }
        return changeStatus(application.getId(), ApplicationStatus.BOOKED);
    }

    /**
     * Requests withdrawal for an application.
     * Fulfills the requirement that applicants can request withdrawal
     * before/after flat booking.
     * Note: This does not change flat counts, it only sets the withdrawal request
     * flag
     * which will be processed by a manager.
     * 
     * @param application The application to request withdrawal for
     * @return True if successful, false otherwise
     */
    public boolean requestWithdrawal(BTOApplication application) {
        if (application == null ||
                (application.getStatus() != ApplicationStatus.PENDING &&
                        application.getStatus() != ApplicationStatus.SUCCESS &&
                        application.getStatus() != ApplicationStatus.BOOKED)) {
            return false;
        }

        application.requestWithdrawal();
        return true;
    }

    /**
     * Confirms a withdrawal request by a manager.
     * Fulfills the requirement for managers to approve withdrawal requests.
     * This is when flat counts are updated for successful applications.
     * 
     * @param application The application to confirm withdrawal for
     * @return True if successful, false otherwise
     */
    public boolean confirmWithdrawalRequest(BTOApplication application) {
        if (application == null || !application.isWithdrawalRequested()) {
            return false;
        }

        // Allow managers to approve withdrawals regardless of application status
        ApplicationStatus current = application.getStatus();
        Project project = application.getProject();
        String flatType = application.getRoomType();

        // Only increment available units if the application was successful or booked
        if (current == ApplicationStatus.SUCCESS || current == ApplicationStatus.BOOKED) {
            // Return the flat to the available pool
            project.incrementAvailableUnits(flatType);
        }

        // Set to withdrawn status and clean up applicant reference
        application.confirmWithdrawal();
        application.getApplicant().clearCurrentApplicationReference();

        System.out.println("Application ID " + application.getId()
                + " withdrawal approved. Applicant can now apply for a new project.");
        return true;
    }

    /**
     * Rejects a withdrawal request.
     * Fulfills the requirement for managers to reject withdrawal requests.
     * 
     * @param application The application to reject withdrawal for
     * @return True if successful, false otherwise
     */
    public boolean rejectWithdrawalRequest(BTOApplication application) {
        if (application == null || !application.isWithdrawalRequested()) {
            return false;
        }

        application.setWithdrawalRequested(false);
        return true;
    }

    /**
     * Gets all applications with pending withdrawal requests.
     * Helps managers view and process withdrawal requests.
     * 
     * @return List of applications with withdrawal requests
     */
    public List<BTOApplication> getWithdrawalRequests() {
        return dataStore.getApplications().stream()
                .filter(BTOApplication::isWithdrawalRequested)
                .collect(Collectors.toList());
    }

    /**
     * Gets all pending applications.
     * Helps managers view and process applications waiting for approval.
     * 
     * @return List of pending applications
     */
    public List<BTOApplication> getPendingApplications() {
        return dataStore.getApplications().stream()
                .filter(app -> app.getStatus() == ApplicationStatus.PENDING)
                .collect(Collectors.toList());
    }

    /**
     * Gets all applications for projects managed by a specific manager.
     * Fulfills the requirement for managers to view applications for their
     * projects.
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
