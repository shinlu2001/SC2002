package SC2002.Project.entity;

import SC2002.Project.entity.enums.RegistrationStatus;

/**
 * Represents an officer’s registration to handle a project.
 */
public class Registration {
    private final int id;
    private final HDB_Officer officer;
    private final Project project;
    private RegistrationStatus status;

    public Registration(int id, HDB_Officer officer, Project project) {
        this.id = id;
        this.officer = officer;
        this.project = project;
        this.status = RegistrationStatus.PENDING;
    }

    public int getId() { return id; }
    public HDB_Officer getOfficer() { return officer; }
    public Project getProject() { return project; }
    public RegistrationStatus getStatus() { return status; }

    /** Approve this registration. */
    public void approve() {
        this.status = RegistrationStatus.APPROVED;
    }

    /** Reject this registration. */
    public void reject() {
        this.status = RegistrationStatus.REJECTED;
    }

    /**
     * Public setter so CSVReader can set initial statuses.
     */
    public void setStatus(RegistrationStatus status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "Registration ID: " + id
             + " | Officer: " + officer.getFirstName()
             + " | Project: " + project.getName()
             + " | Status: " + status;
    }
}
