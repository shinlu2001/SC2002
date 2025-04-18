package SC2002.Project.entity;

import SC2002.Project.entity.enums.ApplicationStatus;
import SC2002.Project.entity.enums.MaritalStatus;
import SC2002.Project.util.IdGenerator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * Represents an applicant user in the BTO system.
 */
public class Applicant extends User {
    private final int id;  // unique applicant ID
    private BTOApplication currentApplication; // may be null
    private final List<BTOApplication> applicationHistory;

    /**
     * Constructs a new Applicant and assigns a unique ID.
     */
    public Applicant(String nric, String firstName, String lastName, MaritalStatus maritalStatus, int age) {
        super(nric, firstName, lastName, maritalStatus, age);
        this.id = IdGenerator.nextApplicantId();
        this.applicationHistory = new ArrayList<>();
    }

    /**
     * @return this applicant's unique ID.
     */
    public int getId() {
        return id;
    }

    // Add specific getter for Applicant ID to avoid confusion with User ID if User gets one
    public int getApplicantId() {
        return id;
    }

    /**
     * @return an Optional containing the active BTO application, or empty if none.
     */
    public Optional<BTOApplication> getCurrentApplication() {
        return Optional.ofNullable(this.currentApplication);
    }

    /**
     * Sets a new active application.
     * If a previous application existed, it is added to history.
     */
    public void setCurrentApplication(BTOApplication application) {
        if (this.currentApplication != null) {
            applicationHistory.add(this.currentApplication);
        }
        this.currentApplication = application;
    }

    /**
     * @return an unmodifiable list of past applications.
     */
    public List<BTOApplication> getApplicationHistory() {
        return Collections.unmodifiableList(this.applicationHistory);
    }

    /**
     * Moves a withdrawn application into history and clears the currentApplication.
     */
    public void finalizeWithdrawal() {
        if (currentApplication != null
         && currentApplication.getStatus() == ApplicationStatus.WITHDRAWN) {
            applicationHistory.add(currentApplication);
            currentApplication = null;
        }
    }

    /**
     * Clears the current application reference after rejection/withdrawal.
     * Ensures it’s already in history.
     */
    public void clearCurrentApplicationReference() {
        if (currentApplication != null
         && (currentApplication.getStatus() == ApplicationStatus.WITHDRAWN
          || currentApplication.getStatus() == ApplicationStatus.REJECTED)) {
            if (!applicationHistory.contains(currentApplication)) {
                applicationHistory.add(currentApplication);
            }
            currentApplication = null;
        } else if (currentApplication != null) {
            System.err.println(
              "Warning: Tried to clear non-finalized application for NRIC " + getNric());
        }
    }

    // ───── Convenience getters for UI & controllers ─────

    public String getFirstName() {
        return super.getFirstName();
    }

    public String getLastName() {
        return super.getLastName();
    }

    public String getNric() {
        return super.getNric();
    }

    public int getAge() {
        return super.getAge();
    }

    public MaritalStatus getMaritalStatus() {
        return super.getMaritalStatus();
    }

    /**
     * A simple toString() for printing account details.
     */
    @Override
    public String toString() {
        return String.format(
            "Name: %s %s | NRIC: %s | Age: %d | Marital: %s | Applicant ID: %d",
            getFirstName(),
            getLastName(),
            getNric(),
            getAge(),
            getMaritalStatus(),
            id
        );
    }
}
