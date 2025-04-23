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
 * <p>
 * This class implements the Applicant role in the BTO system, which includes:
 * <ul>
 * <li>Viewing eligible projects based on marital status and age</li>
 * <li>Applying for BTO projects</li>
 * <li>Managing application status (including withdrawals)</li>
 * <li>Booking flats through HDB Officers</li>
 * <li>Creating and managing enquiries</li>
 * </ul>
 * </p>
 * <p>
 * Fulfills the following requirements:
 * <ul>
 * <li>Can apply for projects (only one at a time)</li>
 * <li>Singles (35+ years) can only apply for 2-Room flats</li>
 * <li>Married (21+ years) can apply for any flat type (2-Room or 3-Room)</li>
 * <li>View project details and application status</li>
 * <li>Book flats if application is successful</li>
 * <li>Request withdrawal of BTO applications</li>
 * <li>Submit and manage enquiries</li>
 * </ul>
 * </p>
 */
public class Applicant extends User {
    private final int id; // unique applicant ID
    private BTOApplication currentApplication; // may be null
    private final List<BTOApplication> applicationHistory;
    private List<Enquiry> applicantEnquiries = new ArrayList<>();

    /**
     * Constructs a new Applicant and assigns a unique ID.
     * 
     * @param nric          The applicant's NRIC (unique identifier)
     * @param firstName     The applicant's first name
     * @param lastName      The applicant's last name (can be empty)
     * @param maritalStatus The applicant's marital status (SINGLE or MARRIED)
     * @param age           The applicant's age
     */
    public Applicant(String nric, String firstName, String lastName, MaritalStatus maritalStatus, int age) {
        super(nric, firstName, lastName, maritalStatus, age);
        this.id = IdGenerator.nextApplicantId();
        this.applicationHistory = new ArrayList<>();
    }

    /**
     * Gets the applicant's unique ID.
     * 
     * @return This applicant's unique ID
     */
    public int getId() {
        return id;
    }

    /**
     * Gets the applicant's unique ID.
     * Added to avoid confusion with User ID if User gets one.
     * 
     * @return This applicant's unique ID
     */
    public int getApplicantId() {
        return id;
    }

    /**
     * Gets the applicant's current active application if one exists.
     * This is used to enforce the requirement that applicants can only
     * have one active application at a time.
     * 
     * @return An Optional containing the active BTO application, or empty if none
     *         exists
     */
    public Optional<BTOApplication> getCurrentApplication() {
        return Optional.ofNullable(this.currentApplication);
    }

    /**
     * Sets a new active application for the applicant.
     * If a previous application existed, it is added to history.
     * This method helps fulfill the requirement that applicants can only
     * have one active application at a time.
     * 
     * @param application The new application to set as current
     */
    public void setCurrentApplication(BTOApplication application) {
        if (this.currentApplication != null) {
            applicationHistory.add(this.currentApplication);
        }
        this.currentApplication = application;
    }

    /**
     * Gets the history of the applicant's past applications.
     * 
     * @return An unmodifiable list of past applications
     */
    public List<BTOApplication> getApplicationHistory() {
        return Collections.unmodifiableList(this.applicationHistory);
    }

    /**
     * Moves a withdrawn application into history and clears the currentApplication.
     * Fulfills the requirement for applicants to withdraw their applications.
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
     * Ensures it's already in history.
     * This enables the applicant to apply for a new project after withdrawal
     * or rejection of their previous application.
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

    /**
     * Gets the applicant's first name.
     * 
     * @return The applicant's first name
     */
    public String getFirstName() {
        return super.getFirstName();
    }

    /**
     * Gets the applicant's last name.
     * 
     * @return The applicant's last name (can be empty)
     */
    public String getLastName() {
        return super.getLastName();
    }

    /**
     * Gets the applicant's NRIC.
     * 
     * @return The applicant's NRIC
     */
    public String getNric() {
        return super.getNric();
    }

    /**
     * Gets the applicant's age.
     * This is used to determine eligibility for certain flat types.
     * 
     * @return The applicant's age
     */
    public int getAge() {
        return super.getAge();
    }

    /**
     * Gets the list of enquiries submitted by this applicant.
     * Fulfills the requirement for applicants to submit and manage enquiries.
     * 
     * @return List of enquiries made by this applicant
     */
    public List<Enquiry> getEnquiries() {
        return applicantEnquiries;
    }

    /**
     * Gets the applicant's marital status.
     * This is used to determine eligibility for certain flat types.
     * 
     * @return The applicant's marital status
     */
    public MaritalStatus getMaritalStatus() {
        return super.getMaritalStatus();
    }

    /**
     * Returns a string representation of the applicant.
     * 
     * @return A string with the applicant's details
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
                id);
    }
}
