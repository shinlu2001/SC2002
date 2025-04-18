package SC2002.Project.util;

import SC2002.Project.entity.*;
import java.util.List;
import java.util.OptionalInt;

/**
 * Utility class to generate unique IDs for various entities.
 */
public class IdGenerator {
    // Removed nextUserId
    private static int nextApplicantId = 1; // Added back
    private static int nextProjectId = 1;
    private static int nextApplicationId = 1;
    private static int nextEnquiryId = 1;
    // Removed nextFlatId
    private static int nextOfficerId = 1; // Added for Officer IDs (distinct from User ID)
    private static int nextRegistrationId = 1; // Added for Registration IDs
    private static int nextReportId = 1; // Added for Report IDs

    /**
     * Initializes the next available IDs based on the maximum ID found in the loaded data.
     * This should be called once after loading all data from CSV files.
     */
    public static void initializeIds(List<User> users, List<Project> projects, List<BTOApplication> applications, List<Enquiry> enquiries, List<Registration> registrations) {
        // Removed User ID initialization

        // Initialize Applicant ID based on Applicant instances
        OptionalInt maxApplicantId = users.stream()
                                        .filter(u -> u instanceof Applicant)
                                        .map(u -> (Applicant) u)
                                        .mapToInt(Applicant::getApplicantId) // Assuming Applicant has getApplicantId()
                                        .max();
        nextApplicantId = maxApplicantId.orElse(0) + 1;

        OptionalInt maxProjectId = projects.stream().mapToInt(Project::getId).max();
        nextProjectId = maxProjectId.orElse(0) + 1;

        OptionalInt maxAppId = applications.stream().mapToInt(BTOApplication::getId).max();
        nextApplicationId = maxAppId.orElse(0) + 1;

        OptionalInt maxEnquiryId = enquiries.stream().mapToInt(Enquiry::getId).max();
        nextEnquiryId = maxEnquiryId.orElse(0) + 1;

        // Removed Flat ID initialization

        // Initialize Officer ID based on HDB_Officer instances
        OptionalInt maxOfficerId = users.stream()
                                        .filter(u -> u instanceof HDB_Officer)
                                        .map(u -> (HDB_Officer) u)
                                        .mapToInt(HDB_Officer::getOfficerId)
                                        .max();
        nextOfficerId = maxOfficerId.orElse(0) + 1;

        // Initialize Registration ID
        OptionalInt maxRegId = registrations.stream().mapToInt(Registration::getId).max();
        nextRegistrationId = maxRegId.orElse(0) + 1;

        // TODO: Initialize Report ID if reports are persisted

        System.out.println("ID Generators Initialized: nextApplicant=" + nextApplicantId +
                           ", nextProject=" + nextProjectId + ", nextApp=" + nextApplicationId + 
                           ", nextEnquiry=" + nextEnquiryId + 
                           ", nextOfficer=" + nextOfficerId + ", nextReg=" + nextRegistrationId);
                           // Removed nextUser and nextFlat from printout
    }


    // Removed nextUserId()
    public static int nextApplicantId() { return nextApplicantId++; } // Added back
    public static int nextProjectId() { return nextProjectId++; }
    public static int nextApplicationId() { return nextApplicationId++; }
    public static int nextEnquiryId() { return nextEnquiryId++; }
    // Removed nextFlatId()
    public static int nextOfficerId() { return nextOfficerId++; } // Added getter
    public static int nextRegistrationId() { return nextRegistrationId++; } // Added getter
    public static int nextReportId() { return nextReportId++; } // Added getter for Report IDs
}
