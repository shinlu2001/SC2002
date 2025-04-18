package SC2002.Project.entity;

import SC2002.Project.entity.enums.RegistrationStatus;
import SC2002.Project.util.IdGenerator;

/**
 * Represents an officer’s registration to handle a project.
 */
public class Registration {
    private final int id;
    private final HDB_Officer officer;
    private final Project project;
    private RegistrationStatus status;
    private boolean withdrawalRequested;

    /** Constructor for CSV loading (keeps given ID). */
    public Registration(int id, HDB_Officer officer, Project project) {
        this.id = id;
        this.officer = officer;
        this.project = project;
        this.status = RegistrationStatus.PENDING;
        this.withdrawalRequested = false;
    }

    /** Constructor for new registrations (auto‐generates ID). */
    public Registration(HDB_Officer officer, Project project) {
        this(IdGenerator.nextRegistrationId(), officer, project);
    }

    public int getId() { return id; }
    public HDB_Officer getOfficer() { return officer; }
    public Project getProject() { return project; }
    public RegistrationStatus getStatus() { return status; }
    public boolean isWithdrawalRequested() { return withdrawalRequested; }

    public void setWithdrawalRequested(boolean withdrawalRequested) {
        this.withdrawalRequested = withdrawalRequested;
    }

    /** Used by CSV loader to restore previous state. */
    public void setStatus(RegistrationStatus status) {
        this.status = status;
    }

    public void approve()   { this.status = RegistrationStatus.APPROVED;  }
    public void reject()    { this.status = RegistrationStatus.REJECTED;  }
    public void withdraw()  { this.status = RegistrationStatus.WITHDRAWN; }

    @Override
    public String toString() {
        return "Registration ID: " + id
             + " | Officer: " + officer.getFirstName()
             + " | Project: " + project.getName()
             + " | Status: " + status
             + (withdrawalRequested ? " (Withdrawal Requested)" : "");
    }
}
