package SC2002.Project.control;

import SC2002.Project.control.persistence.DataStore;
import SC2002.Project.entity.*;
import SC2002.Project.entity.enums.*;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Controller responsible for applicant-specific operations in the BTO Management System.
 * <p>
 * This controller handles all business logic related to applicant actions:
 * <ul>
 *   <li>Eligibility checks for projects and flat types</li>
 *   <li>Application creation and management</li>
 *   <li>Project listing and filtering</li>
 *   <li>Application status management (booking requests, withdrawals)</li>
 *   <li>Enquiry management</li>
 * </ul>
 * </p>
 * <p>
 * The controller enforces business rules such as:
 * <ul>
 *   <li>Single applicants ≥35 years old can only apply for 2-ROOM flats</li>
 *   <li>Married applicants ≥21 years old can apply for any flat type</li>
 *   <li>Applicants can only have one active application at a time</li>
 *   <li>Officers cannot apply for projects they are registered to handle</li>
 * </ul>
 * </p>
 * 
 * @author Group 1
 * @version 1.0
 * @since 2025-04-24
 */
public class ApplicantController {
    private final DataStore dataStore = DataStore.getInstance();
    private final Applicant applicant;

    /**
     * Constructs a new ApplicantController for the specified applicant.
     * 
     * @param applicant The applicant this controller will operate on
     */
    public ApplicantController(Applicant applicant) {
        this.applicant = applicant;
    }

    /**
     * Gets the applicant associated with this controller.
     * 
     * @return The Applicant instance
     */
    public Applicant getApplicant() {
        return applicant;
    }

    /**
     * Checks if the applicant is eligible for a specific room type based on marital status and age.
     * <p>
     * Eligibility rules:
     * <ul>
     *   <li>SINGLE applicants ≥35 years old can only apply for 2-ROOM flats</li>
     *   <li>MARRIED applicants ≥21 years old can apply for any flat type</li>
     * </ul>
     * </p>
     * 
     * @param roomType The room type to check eligibility for (e.g., "2-ROOM", "3-ROOM")
     * @return true if the applicant is eligible for the specified room type, false otherwise
     */
    public boolean isEligibleForRoomType(String roomType) {
        MaritalStatus ms = applicant.getMaritalStatus();
        int age = applicant.getAge();
        if (ms == MaritalStatus.SINGLE) {
            return age >= 35 && "2-ROOM".equalsIgnoreCase(roomType);
        } else if (ms == MaritalStatus.MARRIED) {
            return age >= 21;
        }
        return false;
    }

    /**
     * Checks if the applicant has an active application (PENDING, SUCCESS, or BOOKED).
     * <p>
     * This method enforces the requirement that applicants can only have one active 
     * application at a time. If an active application exists, an error message is displayed.
     * </p>
     * 
     * @return true if an active application exists, false otherwise
     */
    public boolean hasActiveApplication() {
        Optional<BTOApplication> current = applicant.getCurrentApplication();
        if (current.isPresent()) {
            ApplicationStatus st = current.get().getStatus();
            if (st == ApplicationStatus.PENDING
                    || st == ApplicationStatus.SUCCESS
                    || st == ApplicationStatus.BOOKED) {
                String projName = current.get().getProject().getName();
                System.out
                        .println("You may not apply for another project as you already have an active application for "
                                + projName + " (Status: " + st + ").");
                return true;
            }
        }
        return false;
    }

    /**
     * Creates a new BTO application for the applicant.
     * <p>
     * This method performs comprehensive validation before creating an application:
     * <ul>
     *   <li>Checks for existing active applications</li>
     *   <li>Verifies officer registration conflicts</li>
     *   <li>Validates project visibility and open status</li>
     *   <li>Confirms eligibility for the requested room type</li>
     *   <li>Ensures flat availability</li>
     * </ul>
     * </p>
     * 
     * @param project The project to apply for
     * @param roomType The flat type to apply for
     * @return true if the application was successfully created, false otherwise
     */
    public boolean createApplication(Project project, String roomType) {
        if (hasActiveApplication()) {
            return false;
        }

        // Check if the applicant is an officer and is registered for this project
        if (applicant instanceof HDB_Officer officer) {
            boolean isRegistered = dataStore.getRegistrations().stream()
                    .anyMatch(reg -> reg.getOfficer().equals(officer) &&
                            reg.getProject().equals(project) &&
                            (reg.getStatus() == RegistrationStatus.PENDING
                                    || reg.getStatus() == RegistrationStatus.APPROVED));
            if (isRegistered) {
                System.out.println("Error: Cannot apply for project '" + project.getName() +
                        "' as you have a pending or approved officer registration for it.");
                return false;
            }
        }

        // Check Project Visibility and Open Status
        if (!project.isVisible()) {
            System.out.println("Error: Project is not currently visible.");
            return false;
        }
        if (!project.isOpen()) {
            System.out.println("Error: Project application period is not open (Open: " + project.getOpenDate()
                    + ", Close: " + project.getCloseDate() + ").");
            return false;
        }
        if (!isEligibleForRoomType(roomType)) {
            System.out.println("Error: Not eligible for room type based on age/marital status.");
            return false;
        }
        if (!project.getFlatTypes().contains(roomType.toUpperCase())) {
            System.out.println("Error: Room type not offered in that project.");
            return false;
        }
        // Check flat availability
        int idx = project.getFlatTypes().indexOf(roomType.toUpperCase());
        if (idx == -1 || project.getAvailableUnits().get(idx) <= 0) {
            System.out.println("Error: No available flats of this type in the selected project.");
            return false;
        }
        // Remove the decrement of available units - this should only happen when
        // manager approves
        BTOApplication app = new BTOApplication(applicant, project, roomType);
        dataStore.getApplications().add(app);
        applicant.setCurrentApplication(app);
        System.out.println("Application submitted! ID=" + app.getId() + " Status=PENDING");
        return true;
    }

    /**
     * Applies for a project using the project ID and flat type.
     * <p>
     * This is a convenience method that finds the project by ID and delegates
     * to {@link #createApplication(Project, String)}.
     * </p>
     * 
     * @param projectId The ID of the project to apply for
     * @param flatType The flat type to apply for
     * @return true if the application was successfully created, false otherwise
     */
    public boolean applyForProject(int projectId, String flatType) {
        if (hasActiveApplication()) {
            return false;
        }
        Project p = dataStore.getProjects().stream()
                .filter(x -> x.getId() == projectId)
                .findFirst()
                .orElse(null);
        if (p == null) {
            System.out.println("Error: Project not found.");
            return false;
        }
        return createApplication(p, flatType);
    }

    /**
     * Gets the applicant's current application.
     * 
     * @return An Optional containing the current application, or empty if none exists
     */
    public Optional<BTOApplication> viewCurrentApplication() {
        return applicant.getCurrentApplication();
    }

    /**
     * Requests a booking for the applicant's current successful application.
     * <p>
     * This can only be done if the application status is SUCCESS.
     * </p>
     * 
     * @return true if the booking request was successful, false otherwise
     */
    public boolean requestBookingForCurrentApplication() {
        Optional<BTOApplication> opt = applicant.getCurrentApplication();
        if (opt.isPresent() && opt.get().getStatus() == ApplicationStatus.SUCCESS) {
            opt.get().requestBooking();
            System.out.println("Booking requested.");
            return true;
        }
        System.out.println("No successful application to book.");
        return false;
    }

    /**
     * Requests withdrawal of the applicant's current application.
     * <p>
     * This method validates that:
     * <ul>
     *   <li>An active application exists</li>
     *   <li>The application status allows withdrawal (not already REJECTED or WITHDRAWN)</li>
     *   <li>A withdrawal has not already been requested</li>
     * </ul>
     * </p>
     * 
     * @return true if the withdrawal request was successful, false otherwise
     */
    public boolean requestWithdrawal() {
        Optional<BTOApplication> opt = applicant.getCurrentApplication();
        if (opt.isEmpty()) {
            System.out.println("Error: No active application.");
            return false;
        }
        BTOApplication app = opt.get();
        ApplicationStatus st = app.getStatus();
        if (st == ApplicationStatus.REJECTED
                || st == ApplicationStatus.WITHDRAWN) {
            System.out.println("Cannot withdraw at status " + st);
            return false;
        }
        if (app.isWithdrawalRequested()) {
            System.out.println("Withdrawal already requested.");
            return false;
        }
        app.requestWithdrawal();
        System.out.println("Withdrawal requested for ID=" + app.getId());
        return true;
    }

    /**
     * Adds an enquiry to the applicant's list of enquiries.
     * 
     * @param en The enquiry to add
     */
    public void addEnquiry(Enquiry en) {
        applicant.getEnquiries().add(en);
    }

    /**
     * Removes an enquiry from the applicant's list of enquiries.
     * 
     * @param en The enquiry to remove
     */
    public void deleteEnquiry(Enquiry en) {
        applicant.getEnquiries().remove(en);
    }

    /**
     * Lists all projects for which the applicant is eligible to apply.
     * <p>
     * A project is considered eligible if:
     * <ul>
     *   <li>It is visible and currently open for applications</li>
     *   <li>It offers at least one flat type the applicant is eligible for</li>
     *   <li>The applicant (if an officer) is not registered to handle this project</li>
     * </ul>
     * </p>
     * 
     * @return A list of eligible projects
     */
    public List<Project> listEligibleProjects() {
        List<Project> potentiallyEligible = dataStore.getProjects().stream()
                .filter(Project::isVisible)
                .filter(Project::isOpen) // Also check if open
                .filter(p -> p.getFlatTypes().stream().anyMatch(this::isEligibleForRoomType))
                .toList();

        // Only filter out registered projects if the applicant is an officer
        if (applicant instanceof HDB_Officer officer) {
            List<Integer> registeredProjectIds = dataStore.getRegistrations().stream()
                    .filter(reg -> reg.getOfficer().equals(officer) &&
                            (reg.getStatus() == RegistrationStatus.PENDING ||
                                    reg.getStatus() == RegistrationStatus.APPROVED))
                    .map(reg -> reg.getProject().getId())
                    .toList();

            return potentiallyEligible.stream()
                    .filter(p -> !registeredProjectIds.contains(p.getId()))
                    .collect(Collectors.toList());
        } else {
            // Not an officer, return all potentially eligible projects
            return potentiallyEligible;
        }
    }

    /**
     * Checks if the applicant is eligible to apply for a specific project.
     * <p>
     * Considers project visibility, application period, flat type eligibility,
     * and officer registration conflicts.
     * </p>
     * 
     * @param project The project to check eligibility for
     * @return true if the applicant is eligible, false otherwise
     */
    public boolean isEligibleForProject(Project project) {
        // Basic eligibility: visible, open, and has eligible flat types
        boolean basicEligible = project.isVisible() &&
                project.isOpen() &&
                project.getFlatTypes().stream().anyMatch(this::isEligibleForRoomType);

        // If not even basically eligible, no need to check officer restrictions
        if (!basicEligible)
            return false;

        // Officers have additional restrictions - can't apply if registered
        if (applicant instanceof HDB_Officer officer) {
            boolean isRegistered = dataStore.getRegistrations().stream()
                    .anyMatch(reg -> reg.getOfficer().equals(officer) &&
                            reg.getProject().equals(project) &&
                            (reg.getStatus() == RegistrationStatus.PENDING ||
                                    reg.getStatus() == RegistrationStatus.APPROVED));

            return !isRegistered; // Not eligible if registered
        }

        // Regular applicants just need the basic eligibility
        return true;
    }

    /**
     * Gets a descriptive reason for the applicant's eligibility or ineligibility
     * for a specific project.
     * <p>
     * Possible reasons include:
     * <ul>
     *   <li>"Not Visible" - Project is not visible to applicants</li>
     *   <li>"Not Open" - Project application period is not currently open</li>
     *   <li>"Officer Registered" - Applicant is an officer registered for this project</li>
     *   <li>"Not Eligible" - No eligible flat types for this applicant</li>
     *   <li>"Eligible" - Applicant is eligible to apply</li>
     * </ul>
     * </p>
     * 
     * @param project The project to check
     * @return A string describing the eligibility status
     */
    public String getEligibilityReason(Project project) {
        if (!project.isVisible()) {
            return "Not Visible";
        }
        if (!project.isOpen()) {
            return "Not Open";
        }

        if (applicant instanceof HDB_Officer officer) {
            boolean isRegistered = dataStore.getRegistrations().stream()
                    .anyMatch(reg -> reg.getOfficer().equals(officer) &&
                            reg.getProject().equals(project) &&
                            (reg.getStatus() == RegistrationStatus.PENDING ||
                                    reg.getStatus() == RegistrationStatus.APPROVED));

            if (isRegistered) {
                return "Officer Registered";
            }
        }

        if (!project.getFlatTypes().stream().anyMatch(this::isEligibleForRoomType)) {
            return "Not Eligible";
        }

        return "Eligible";
    }

    /**
     * Lists all projects that are currently visible in the system.
     * 
     * @return A list of all visible projects
     */
    public List<Project> listAllVisibleProjects() {
        return dataStore.getProjects().stream()
                .filter(Project::isVisible)
                .collect(Collectors.toList());
    }

    /* ────────── UI‐Friendly Overloads for ApplicantUI ────────── */

    /**
     * UI-friendly overload for getting eligible projects.
     * 
     * @param ignored Ignored parameter (for UI convenience)
     * @return A list of eligible projects
     */
    public List<Project> getEligibleProjects(Applicant ignored) {
        return listEligibleProjects();
    }

    /**
     * UI-friendly overload for checking room type eligibility.
     * 
     * @param ignored Ignored parameter (for UI convenience)
     * @param roomType The room type to check
     * @return true if eligible, false otherwise
     */
    public boolean isEligibleForRoomType(Applicant ignored, String roomType) {
        return isEligibleForRoomType(roomType);
    }

    /**
     * UI-friendly overload for creating an application.
     * 
     * @param ignored Ignored parameter (for UI convenience)
     * @param project The project to apply for
     * @param roomType The room type to apply for
     * @return true if successful, false otherwise
     */
    public boolean createApplication(Applicant ignored, Project project, String roomType) {
        return createApplication(project, roomType);
    }

    /**
     * UI-friendly overload for requesting withdrawal.
     * 
     * @param ignored Ignored parameter (for UI convenience)
     * @return true if successful, false otherwise
     */
    public boolean requestWithdrawal(Applicant ignored) {
        return requestWithdrawal();
    }
}
