// SC2002/Project/entity/enums/ApplicationStatus.java
package SC2002.Project.entity.enums;

/**
 * Represents the possible statuses of a BTO application.
 */
public enum ApplicationStatus {
    PENDING,      // Initial state after submission
    SUCCESS,      // Approved by Manager
    REJECTED,     // Rejected by Manager
    BOOKED,       // Flat booked by Officer after SUCCESS
    WITHDRAWN     // Application withdrawn by applicant (and confirmed by Manager)
}
