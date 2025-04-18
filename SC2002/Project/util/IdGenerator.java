package SC2002.Project.util;

/**
 * Utility class for generating unique IDs for different entities.
 */
public final class IdGenerator {
    private static int nextProjectId      = 1;
    private static int nextApplicationId  = 1;
    private static int nextEnquiryId      = 1;
    private static int nextReportId       = 1;
    private static int nextFlatId         = 1;
    private static int nextRegistrationId = 1; 
    private static int nextManagerId      = 1;
    private static int nextOfficerId      = 1;
    private static int nextApplicantId    = 1;

    public static int nextProjectId() {
        return nextProjectId++;
    }

    public static int nextApplicationId() {
        return nextApplicationId++;
    }

    public static int nextEnquiryId() {
        return nextEnquiryId++;
    }

    public static int nextReportId() {
        return nextReportId++;
    }

    public static int nextFlatId() {
        return nextFlatId++;
    }

    /** Generates a new unique ID for officer‐project registrations */
    public static int nextRegistrationId() {
        return nextRegistrationId++;
    }

    public static int nextManagerId() {
        return nextManagerId++;
    }

    public static int nextOfficerId() {
        return nextOfficerId++;
    }

    public static int nextApplicantId() {
        return nextApplicantId++;
    }   

    // Reset all counters (useful for testing)
    public static void resetAll() {
        nextProjectId = 1;
        nextApplicationId = 1;
        nextEnquiryId = 1;
        nextReportId = 1;
        nextFlatId = 1;
        nextRegistrationId = 1;
    }

    // No instances allowed
    private IdGenerator() {}
}
