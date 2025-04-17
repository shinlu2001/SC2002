package SC2002.Project.entity;

import java.time.LocalDate;
import SC2002.Project.entity.enums.RegistrationStatus;
import SC2002.Project.util.IdGenerator;

public class Registration {
    private final int id;
    private final HDB_Officer officer;
    private final Project project;
    private RegistrationStatus status;
    private final LocalDate requestDate;

    /**
     * Constructs a new registration request for the given officer and project,
     * with initial status PENDING and current date as requestDate.
     */
    public Registration(HDB_Officer officer, Project project) {
        this.id = IdGenerator.nextRegistrationId("registration");
        this.officer = officer;
        this.project = project;
        this.status = RegistrationStatus.PENDING;
        this.requestDate = LocalDate.now();
    }

    /** Unique registration ID. */
    public int getId() {
        return id;
    }

    /** The officer who wants to join the project. */
    public HDB_Officer getOfficer() {
        return officer;
    }

    /** The project the officer is requesting to join. */
    public Project getProject() {
        return project;
    }

    /** Current status: PENDING, APPROVED, or REJECTED. */
    public RegistrationStatus getStatus() {
        return status;
    }

    /** Date when the officer made this registration request. */
    public LocalDate getRequestDate() {
        return requestDate;
    }

    /** Change the registration’s status (called by a manager). */
    public void setStatus(RegistrationStatus newStatus) {
        this.status = newStatus;
    }

    @Override
    public String toString() {
        return String.format(
            "Registration[id=%d, officer=%s, project=%s, status=%s, date=%s]",
            id, officer.getFirstname() + " " + officer.getLastname(),
            project.getName(), status, requestDate
        );
    }
}
