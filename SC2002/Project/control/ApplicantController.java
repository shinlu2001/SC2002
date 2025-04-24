package SC2002.Project.control;

import SC2002.Project.control.persistence.DataStore;
import SC2002.Project.entity.*;
import SC2002.Project.entity.enums.*;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Controller for applicant-specific operations:
 * eligibility checks, application creation, viewing, booking, and withdrawal.
 */
public class ApplicantController {
    private final DataStore dataStore = DataStore.getInstance();
    private final Applicant applicant;

    public ApplicantController(Applicant applicant) {
        this.applicant = applicant;
    }

    /**
     * @return the Applicant this controller manages
     */
    public Applicant getApplicant() {
        return applicant;
    }

    /**
     * Eligibility rule: SINGLE >=35 only for 2-ROOM, MARRIED >=21 for any.
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
     * Checks if an active application exists (PENDING, SUCCESS, or BOOKED).
     * Prints error and returns true if so.
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
     * Submits a new BTO application if no active one exists.
     * Includes checks for officer registration conflicts.
     * 
     * @return true if submitted, false otherwise.
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
     * Finds the project by ID and delegates to createApplication().
     * Includes active-application guard.
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
     * @return optional current application
     */
    public Optional<BTOApplication> viewCurrentApplication() {
        return applicant.getCurrentApplication();
    }

    /**
     * Requests booking if application status is SUCCESS.
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
     * Requests withdrawal if status allows it (not REJECTED/WITHDRAWN).
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

    // enquiry matters

    public void addEnquiry(Enquiry en) {
        applicant.getEnquiries().add(en);
    }

    public void deleteEnquiry(Enquiry en) {
        applicant.getEnquiries().remove(en);
    }

    /**
     * Lists all visible projects for which the applicant is eligible,
     * excluding projects the applicant (if an officer) is registered for.
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
     * Check if an applicant is eligible to apply for a given project.
     * For officers, also checks registration status.
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
     * Get a descriptive eligibility reason for a project
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
     * Lists all visible projects in the system.
     */
    public List<Project> listAllVisibleProjects() {
        return dataStore.getProjects().stream()
                .filter(Project::isVisible)
                .collect(Collectors.toList());
    }

    /* ────────── UI‐Friendly Overloads for ApplicantUI ────────── */

    public List<Project> getEligibleProjects(Applicant ignored) {
        return listEligibleProjects();
    }

    public boolean isEligibleForRoomType(Applicant ignored, String roomType) {
        return isEligibleForRoomType(roomType);
    }

    public boolean createApplication(Applicant ignored, Project project, String roomType) {
        return createApplication(project, roomType);
    }

    public boolean requestWithdrawal(Applicant ignored) {
        return requestWithdrawal();
    }
}
