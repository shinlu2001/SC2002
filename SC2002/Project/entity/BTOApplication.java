// File: SC2002/Project/entity/BTOApplication.java
package SC2002.Project.entity;

import SC2002.Project.entity.enums.ApplicationStatus;
import SC2002.Project.util.IdGenerator;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Represents a Build‑To‑Order (BTO) application submitted by an Applicant.
 * <p>
 * This class manages the lifecycle of a BTO application including:
 * <ul>
 * <li>Initial application submission</li>
 * <li>Status transitions (pending → success/rejected/withdrawn)</li>
 * <li>Booking requests and flat selection</li>
 * <li>Withdrawal requests and processing</li>
 * </ul>
 * </p>
 * <p>
 * The application status follows this progression:
 * <ul>
 * <li>PENDING: Initial state upon submission, awaiting manager decision</li>
 * <li>SUCCESS: Application approved, waiting for booking</li>
 * <li>BOOKED: Flat successfully selected and booked</li>
 * <li>REJECTED: Application denied by manager</li>
 * <li>WITHDRAWN: Application withdrawn by applicant</li>
 * </ul>
 * </p>
 * <p>
 * Fulfills the following requirements:
 * <ul>
 * <li>Tracks application status from submission through approval/rejection</li>
 * <li>Supports flat booking for successful applications</li>
 * <li>Supports application withdrawal at any stage</li>
 * <li>Maintains history of status changes with timestamps</li>
 * <li>Links applicants to their selected projects and flat types</li>
 * </ul>
 * </p>
 */
public class BTOApplication {
    private final int id;
    private final Applicant applicant;
    private final Project project;
    private final String roomType; // e.g., "2-ROOM", "3-ROOM"
    private ApplicationStatus status;
    private boolean withdrawalRequested = false;
    private boolean bookingRequested = false;
    private Flat bookedFlat = null;
    private final LocalDateTime submissionDate;
    private LocalDateTime lastUpdateDate;

    /**
     * Creates a new BTOApplication with an auto-generated ID.
     * Initial status is PENDING, and timestamp is set to current time.
     * 
     * @param applicant The applicant submitting the application
     * @param project   The project being applied for
     * @param roomType  The type of flat being requested (e.g., "2-ROOM")
     */
    public BTOApplication(Applicant applicant, Project project, String roomType) {
        this.id = IdGenerator.nextApplicationId();
        this.applicant = applicant;
        this.project = project;
        this.roomType = roomType.toUpperCase();
        this.status = ApplicationStatus.PENDING;
        this.submissionDate = LocalDateTime.now();
        this.lastUpdateDate = this.submissionDate;
    }

    /**
     * Creates a BTOApplication with a specified ID (used for loading from
     * snapshot).
     * This constructor allows the system to recreate applications with specific IDs
     * when loading from saved data.
     * 
     * @param id        The specific ID to assign to this application
     * @param applicant The applicant submitting the application
     * @param project   The project being applied for
     * @param roomType  The type of flat being requested (e.g., "2-ROOM")
     */
    public BTOApplication(int id, Applicant applicant, Project project, String roomType) {
        this.id = id;
        this.applicant = applicant;
        this.project = project;
        this.roomType = roomType.toUpperCase();
        this.status = ApplicationStatus.PENDING;
        this.submissionDate = LocalDateTime.now();
        this.lastUpdateDate = this.submissionDate;
    }

    // ─── Getters ──────────────────────────────────────────────────────────────

    /**
     * Gets the unique ID of this application.
     * 
     * @return The application's ID
     */
    public int getId() {
        return id;
    }

    /**
     * Gets the applicant who submitted this application.
     * 
     * @return The applicant
     */
    public Applicant getApplicant() {
        return applicant;
    }

    /**
     * Gets the project this application is for.
     * 
     * @return The project
     */
    public Project getProject() {
        return project;
    }

    /**
     * Gets the flat type requested in this application.
     * 
     * @return The room type (e.g., "2-ROOM", "3-ROOM")
     */
    public String getRoomType() {
        return roomType;
    }

    /**
     * Gets the current status of this application.
     * 
     * @return The application status
     */
    public ApplicationStatus getStatus() {
        return status;
    }

    /**
     * Checks if withdrawal has been requested for this application.
     * 
     * @return True if withdrawal is requested, false otherwise
     */
    public boolean isWithdrawalRequested() {
        return withdrawalRequested;
    }

    /**
     * Checks if booking has been requested for this application.
     * 
     * @return True if booking is requested, false otherwise
     */
    public boolean isBookingRequested() {
        return bookingRequested;
    }

    /**
     * Gets the flat that has been booked for this application (if any).
     * 
     * @return The booked flat, or null if no flat is booked
     */
    public Flat getBookedFlat() {
        return bookedFlat;
    }

    /**
     * Gets the date and time when this application was submitted.
     * 
     * @return The submission timestamp
     */
    public LocalDateTime getSubmissionDate() {
        return submissionDate;
    }

    /**
     * Gets the date and time when this application was last updated.
     * 
     * @return The last update timestamp
     */
    public LocalDateTime getLastUpdateDate() {
        return lastUpdateDate;
    }

    // ─── Mutators ─────────────────────────────────────────────────────────────

    /**
     * Package‑private status setter (for controllers).
     * Updates the lastUpdateDate to track when the status changed.
     * 
     * @param newStatus The new status to set
     */
    void setStatus(ApplicationStatus newStatus) {
        this.status = newStatus;
        this.lastUpdateDate = LocalDateTime.now();
    }

    /**
     * Allows manager/UI to clear the withdrawal flag (e.g. on reject).
     * 
     * @param flag The new value for the withdrawal requested flag
     */
    public void setWithdrawalRequested(boolean flag) {
        this.withdrawalRequested = flag;
        this.lastUpdateDate = LocalDateTime.now();
    }

    /**
     * Allows manual clearing of the bookingRequested flag if needed.
     * 
     * @param flag The new value for the booking requested flag
     */
    public void setBookingRequested(boolean flag) {
        this.bookingRequested = flag;
        this.lastUpdateDate = LocalDateTime.now();
    }

    // ─── Business Actions ────────────────────────────────────────────────────

    /**
     * Applicant requests withdrawal (sets withdrawal flag).
     * Fulfills the requirement that applicants can request withdrawal before/after
     * flat booking.
     * Allowed for all statuses except REJECTED or WITHDRAWN.
     */
    public void requestWithdrawal() {
        if (status != ApplicationStatus.REJECTED
                && status != ApplicationStatus.WITHDRAWN) {
            this.withdrawalRequested = true;
            this.lastUpdateDate = LocalDateTime.now();
        }
    }

    /**
     * Manager confirms a withdrawal request.
     * Fulfills the requirement that managers can approve withdrawal requests.
     * If a flat was booked, it is released and made available again.
     */
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

    /**
     * Manager approves the application.
     * Fulfills the requirement that managers can approve applications.
     * Only allowed if application is in PENDING status.
     */
    public void approve() {
        if (status == ApplicationStatus.PENDING) {
            setStatus(ApplicationStatus.SUCCESS);
        }
    }

    /**
     * Manager rejects the application.
     * Fulfills the requirement that managers can reject applications.
     * Only allowed if application is in PENDING status.
     */
    public void reject() {
        if (status == ApplicationStatus.PENDING) {
            setStatus(ApplicationStatus.REJECTED);
        }
    }

    /**
     * Applicant requests a booking after approval.
     * Sets a flag that the applicant wants to book a flat.
     * Only allowed if application is in SUCCESS status and booking not already
     * requested.
     */
    public void requestBooking() {
        if (status == ApplicationStatus.SUCCESS && !bookingRequested) {
            this.bookingRequested = true;
            this.lastUpdateDate = LocalDateTime.now();
        }
    }

    /**
     * Books a flat for this application.
     * Sets the status to BOOKED if the current status is SUCCESS.
     * No need to update project's available units as this was already handled during manager approval.
     * 
     * @param flat The flat to book
     */
    public void bookFlat(Flat flat) {
        if (status == ApplicationStatus.SUCCESS && flat != null && !flat.isBooked()) {
            this.bookedFlat = flat;
            flat.setBooked(true); // Mark flat as booked
            setStatus(ApplicationStatus.BOOKED);
        } else {
            System.err.println("Cannot book flat for application ID " + id
                    + "; status=" + status + " or flat invalid/booked.");
        }
    }

    /**
     * Future hook: cancel a booked flat and revert status, re‑increment units.
     * This method is NOT IN USE currently but provides functionality for future
     * enhancement.
     * 
     * @return True if booking was successfully canceled, false otherwise
     */
    public boolean cancelBooking() {
        if (status == ApplicationStatus.BOOKED && bookedFlat != null) {
            // Store temporary references to avoid partial updates
            Flat flatToReset = this.bookedFlat;
            String roomTypeToIncrement = this.roomType;

            // Check if flat can be unbooked
            if (flatToReset == null) {
                System.err
                        .println("Error: Cannot cancel booking for application ID " + id + " - flat reference is null");
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

    /**
     * Returns a string representation of the application.
     * 
     * @return A string with application details
     */
    @Override
    public String toString() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        return String.format(
                "Application ID: %d | Submission Date: %s | Last Update: %s | Applicant Name: %s %s | Project Name: %s | Room: %s | Status: %s%s%s",
                id,
                submissionDate.format(formatter),
                lastUpdateDate.format(formatter),
                applicant.getFirstName(),
                applicant.getLastName(),
                project.getName(),
                roomType,
                status,
                (withdrawalRequested ? " (Withdrawal Requested)" : ""),
                (bookedFlat != null ? " | Booked Flat ID: " + bookedFlat.getId() : ""));
    }
}
