// File: SC2002/Project/entity/BTOApplication.java
package SC2002.Project.entity;

import SC2002.Project.entity.enums.ApplicationStatus;
import SC2002.Project.util.IdGenerator;
import java.time.LocalDateTime;

/**
 * Represents a Build‑To‑Order (BTO) application submitted by an Applicant.
 */
public class BTOApplication {
    private final int id;
    private final Applicant applicant;
    private final Project project;
    private final String roomType;      // e.g., "2-ROOM", "3-ROOM"
    private ApplicationStatus status;
    private boolean withdrawalRequested = false;
    private boolean bookingRequested    = false;
    private Flat bookedFlat             = null;
    private final LocalDateTime submissionDate;
    private LocalDateTime lastUpdateDate;

    public BTOApplication(Applicant applicant, Project project, String roomType) {
        this.id              = IdGenerator.nextApplicationId();
        this.applicant       = applicant;
        this.project         = project;
        this.roomType        = roomType.toUpperCase();
        this.status          = ApplicationStatus.PENDING;
        this.submissionDate  = LocalDateTime.now();
        this.lastUpdateDate  = this.submissionDate;
    }

    // ─── Getters ──────────────────────────────────────────────────────────────

    public int                getId()                 { return id; }
    public Applicant          getApplicant()          { return applicant; }
    public Project            getProject()            { return project; }
    public String             getRoomType()           { return roomType; }
    public ApplicationStatus  getStatus()             { return status; }
    public boolean            isWithdrawalRequested() { return withdrawalRequested; }
    public boolean            isBookingRequested()    { return bookingRequested; }
    public Flat               getBookedFlat()         { return bookedFlat; }
    public LocalDateTime      getSubmissionDate()     { return submissionDate; }
    public LocalDateTime      getLastUpdateDate()     { return lastUpdateDate; }

    // ─── Mutators ─────────────────────────────────────────────────────────────

    /** Package‑private status setter (for controllers). */
    void setStatus(ApplicationStatus newStatus) {
        this.status = newStatus;
        this.lastUpdateDate = LocalDateTime.now();
    }

    /** Allows manager/UI to clear the withdrawal flag (e.g. on reject). */
    public void setWithdrawalRequested(boolean flag) {
        this.withdrawalRequested = flag;
        this.lastUpdateDate = LocalDateTime.now();
    }

    /** Allows manual clearing of the bookingRequested flag if needed. */
    public void setBookingRequested(boolean flag) {
        this.bookingRequested = flag;
        this.lastUpdateDate = LocalDateTime.now();
    }

    // ─── Business Actions ────────────────────────────────────────────────────

    /** Applicant requests withdrawal (moves flag). */
    public void requestWithdrawal() {
        if (status != ApplicationStatus.BOOKED
         && status != ApplicationStatus.REJECTED
         && status != ApplicationStatus.WITHDRAWN) {
            this.withdrawalRequested = true;
            this.lastUpdateDate = LocalDateTime.now();
        }
    }

    /** Manager confirms a withdrawal request. */
    public void confirmWithdrawal() {
        if (status != ApplicationStatus.WITHDRAWN) {
            this.withdrawalRequested = false;
            setStatus(ApplicationStatus.WITHDRAWN);
            
            // If this application has a booked flat, we need to release it
            if (bookedFlat != null) {
                bookedFlat.setBooked(false);
                project.incrementAvailableUnits(roomType);
                this.bookedFlat = null;
            }
        }
    }

    /** Manager approves the application. */
    public void approve() {
        if (status == ApplicationStatus.PENDING) {
            setStatus(ApplicationStatus.SUCCESS);
        }
    }

    /** Manager rejects the application. */
    public void reject() {
        if (status == ApplicationStatus.PENDING) {
            setStatus(ApplicationStatus.REJECTED);
        }
    }

    /** Applicant requests a booking after approval. */
    public void requestBooking() {
        if (status == ApplicationStatus.SUCCESS && !bookingRequested) {
            this.bookingRequested = true;
            this.lastUpdateDate = LocalDateTime.now();
        }
    }

    /** Officer actually books a flat for the applicant. */
    public void bookFlat(Flat flat) {
        if (status == ApplicationStatus.SUCCESS && flat != null && !flat.isBooked()) {
            this.bookedFlat = flat;
            flat.setBooked(true);         // also decrements project availability
            setStatus(ApplicationStatus.BOOKED);
        } else {
            System.err.println("Cannot book flat for application ID " + id
                             + "; status=" + status + " or flat invalid/booked.");
        }
    }

    /** Future hook: cancel a booked flat and revert status, re‑increment units. */ //NOT IN USE
    public boolean cancelBooking() {
        if (status == ApplicationStatus.BOOKED && bookedFlat != null) {
            // Store temporary references to avoid partial updates
            Flat flatToReset = this.bookedFlat;
            String roomTypeToIncrement = this.roomType;
            
            // Check if flat can be unbooked
            if (flatToReset == null) {
                System.err.println("Error: Cannot cancel booking for application ID " + id + " - flat reference is null");
                return false;
            }
            
            try {
                // Attempt to reset the flat booking status
                flatToReset.setBooked(false);
                // Attempt to increment available units
                project.incrementAvailableUnits(roomTypeToIncrement);
                // If both operations succeed, update application state
                this.bookedFlat = null;
                setStatus(ApplicationStatus.SUCCESS);
                return true;
            } catch (Exception e) {
                System.err.println("Error cancelling booking for application ID " + id + ": " + e.getMessage());
                // If an error occurs, attempt to restore original state if needed
                if (!flatToReset.isBooked()) {
                    flatToReset.setBooked(true); // Restore the flat's booked status
                }
                return false;
            }
        } else {
            System.err.println("Cannot cancel booking for application ID " + id 
                + " - application not in BOOKED state or has no booked flat");
            return false;
        }
    }

    @Override
    public String toString() {
        return String.format(
            "Application ID: %d | Applicant Name: %s %s | Project Name: %s | Room: %s | Status: %s%s%s",
            id,
            applicant.getFirstName(),
            applicant.getLastName(),
            project.getName(),
            roomType,
            status,
            (withdrawalRequested ? " (Withdrawal Requested)" : ""),
            (bookedFlat != null  ? " | Booked Flat ID: " + bookedFlat.getId() : "")
        );
    }
}
