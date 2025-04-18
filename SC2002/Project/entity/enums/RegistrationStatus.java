// SC2002/Project/entity/enums/ApplicationStatus.java
package SC2002.Project.entity.enums;

/**
 * Represents the possible statuses of an HDB Officer's registration for a Project.
 */
public enum RegistrationStatus {
    PENDING,    // Officer requested registration, awaiting Manager approval
    APPROVED,   // Manager approved registration
    REJECTED,   // Manager rejected registration
    WITHDRAWN   // Officer withdrew registration (after approval/pending)
}
