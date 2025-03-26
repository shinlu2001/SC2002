// package SC2002.Project;
import java.util.ArrayList;
import java.util.List;

public class BTOapplication {
    //---------------------add--------------------------
    private String applicationStatus;
    private boolean withdrawalRequested;
    private List<BTOapplication> approvedApplications = new ArrayList<>();
    //--------------------------------------------------
    private static int nextId = 1;   // auto-incrementing ID
    private int applicationId;
    
    // References to the applicant and project
    private Applicant applicant;
    private Project project;
    
    // The flat type the applicant is applying for (e.g., "2-Room", "3-Room")
    private String flatType;
    
    // Application status (e.g., "Pending", "Successful", "Booked", "Withdrawn", etc.)
    private String status;

    /**
     * Constructor to create a new BTOapplication.
     * 
     * @param applicant the applicant who is applying
     * @param project   the project being applied for
     * @param flatType  the flat type chosen (e.g., "2-Room" or "3-Room")
     */
    public BTOapplication(Applicant applicant, Project project, String flatType) {
        this.applicationId = nextId++;
        this.applicant = applicant;
        this.project = project;
        this.flatType = flatType;
        this.status = "Pending"; // default status
    }
    //---------------------add--------------------------
    public void setApplicationStatus(String applicationStatus)
    {
        this.applicationStatus = applicationStatus;
    }

    public String getApplicationStatus(){
        return this.applicationStatus;
    }

    public boolean getwithdrawalRequested()
    {
        return withdrawalRequested;
    }

    public List<BTOapplication> getapprovedApplications()
    {
        return approvedApplications;
    }

    //--------------------------------------------------

    // Getters
    public int getApplicationId() {
        return applicationId;
    }

    public Applicant getApplicant() {
        return applicant;
    }

    public Project getProject() {
        return project;
    }

    public String getFlatType() {
        return flatType;
    }

    public String getStatus() {
        return status;
    }

    /**
     * Returns the name of the project, or "N/A" if no project is set.
     */
    public String getProjectName() {
        return (project != null) ? project.getProjectName() : "N/A";
    }

    // Setters
    public void setStatus(String status) {
        this.status = status;
    }

    /**
     * Print application details for debugging or user output.
     */
    public void get_details() {
        System.out.println("=== Application Details ===");
        System.out.println("Application ID: " + applicationId);
        if (applicant != null) {
            System.out.println("Applicant: " 
                + applicant.get_firstname() + " " + applicant.get_lastname()
                + " (NRIC: " + applicant.get_nric() + ")");
        }
        System.out.println("Project: " + getProjectName());
        System.out.println("Flat Type: " + flatType);
        System.out.println("Status: " + status);
        System.out.println("===========================");
    }
}
