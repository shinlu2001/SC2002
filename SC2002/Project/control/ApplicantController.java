package SC2002.Project.control;

import SC2002.Project.control.persistence.DataStore;
import SC2002.Project.entity.Applicant;
import SC2002.Project.entity.BTOApplication;
import SC2002.Project.entity.Project;
import SC2002.Project.entity.enums.ApplicationStatus;
import SC2002.Project.entity.enums.MaritalStatus;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class ApplicantController {
    private final DataStore dataStore = DataStore.getInstance();
    private final Applicant applicant;

    public ApplicantController(Applicant applicant) {
        this.applicant = applicant;
    }

    /* ────────── Existing methods ────────── */

    public Applicant getApplicant() {
        return applicant;
    }

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

    public boolean createApplication(Project project, String roomType) {
        Optional<BTOApplication> current = applicant.getCurrentApplication();
        if (current.isPresent()) {
            ApplicationStatus st = current.get().getStatus();
            if (st == ApplicationStatus.PENDING
             || st == ApplicationStatus.SUCCESS
             || st == ApplicationStatus.BOOKED) {
                System.out.println("Error: You already have an active application (Status: " + st + ").");
                return false;
            }
        }
        if (!isEligibleForRoomType(roomType)) {
            System.out.println("Error: Not eligible for room type.");
            return false;
        }
        if (!project.getFlatTypes().contains(roomType.toUpperCase())) {
            System.out.println("Error: Room type not offered in that project.");
            return false;
        }
        BTOApplication app = new BTOApplication(applicant, project, roomType);
        dataStore.getApplications().add(app);
        applicant.setCurrentApplication(app);
        System.out.println("Application submitted! ID=" + app.getId() + " Status=PENDING");
        return true;
    }

    public boolean applyForProject(int projectId, String flatType) {
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

    public Optional<BTOApplication> viewCurrentApplication() {
        return applicant.getCurrentApplication();
    }

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

    public boolean requestWithdrawal() {
        Optional<BTOApplication> opt = applicant.getCurrentApplication();
        if (opt.isEmpty()) {
            System.out.println("Error: No active application.");
            return false;
        }
        BTOApplication app = opt.get();
        ApplicationStatus st = app.getStatus();
        if (st == ApplicationStatus.BOOKED
         || st == ApplicationStatus.REJECTED
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

    public List<Project> listEligibleProjects() {
        return dataStore.getProjects().stream()
                        .filter(Project::isVisible)
                        .filter(p -> p.getFlatTypes().stream().anyMatch(this::isEligibleForRoomType))
                        .collect(Collectors.toList());
    }

    public List<Project> listAllVisibleProjects() {
        return dataStore.getProjects().stream()
                        .filter(Project::isVisible)
                        .collect(Collectors.toList());
    }

    /* ────────── New “UI‐friendly” overloads to match your ApplicantUI ────────── */

    /** Called by UI: get all projects this applicant is eligible for */
    public List<Project> getEligibleProjects(Applicant ignored) {
        return listEligibleProjects();
    }

    /** Called by UI: check eligibility for a given flat type */
    public boolean isEligibleForRoomType(Applicant ignored, String roomType) {
        return isEligibleForRoomType(roomType);
    }

    /** Called by UI: wrap createApplication(...) */
    public boolean createApplication(Applicant ignored, Project project, String roomType) {
        return createApplication(project, roomType);
    }

    /** Called by UI: wrap requestWithdrawal() */
    public boolean requestWithdrawal(Applicant ignored) {
        return requestWithdrawal();
    }
}
