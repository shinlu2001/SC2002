package SC2002.Project.entity;

import java.util.ArrayList;
import java.util.List;

public class BTOapplication {
    private static int nextId = -1;
    private int applicationId;
    private boolean withdrawalRequested;
    private List<BTOapplication> approvedApplications = new ArrayList<>();
    private static List<BTOapplication> applicationList = new ArrayList<>();
    private boolean requestBooking = false;
    private Flat bookedFlat = null;
    
    private ApplicantBase applicant;
    private Project project;
    private HDB_Manager manager;
    private String flatType;
    private String status = "PENDING";
    
    public BTOapplication(ApplicantBase applicant, Project project, String flatType) {
        this.applicationId = ++nextId;
        this.applicant = applicant;
        this.project = project;
        this.flatType = flatType.toUpperCase();
        this.status = "PENDING";
        this.manager = project.getManager();
        applicationList.add(this);
    }
    
    public int getId() { return applicationId; }
    public ApplicantBase getApplicant() { return applicant; }
    public Project getProject() { return project; }
    public String getFlatType() { return flatType; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status.toUpperCase(); }
    public void bookFlat(Flat flat) { bookedFlat = flat; }
    public void requestBooking() { requestBooking = true; }
    public boolean getWithdrawalRequested() { return withdrawalRequested; }
    public void withdraw() { withdrawalRequested = true; }
    public List<BTOapplication> getApprovedApplications() { return approvedApplications; }
    
    public void printDetails() {
        System.out.println("---------------------------------------------------");
        System.out.println("Application Details:");
        if (withdrawalRequested) {
            System.out.println("| WITHDRAWAL Requested |");
        }
        System.out.println("Application ID: " + applicationId);
        if (applicant != null) {
            System.out.println("Applicant: " + applicant.getFirstName() + " " + applicant.getLastName());
            System.out.println("NRIC: " + applicant.getNRIC());
            System.out.println("Manager In Charge: " + manager.getFirstName());
            System.out.println("Project: " + project.getProjectName());
            System.out.println("Status: " + status);
            System.out.println("Flat Type: " + flatType);
            if (bookedFlat != null) {
                System.out.println("Flat Price: " + bookedFlat.getPrice());
                System.out.println("Booked Flat ID: " + bookedFlat.getFlatID());
            }
        }
        System.out.println("---------------------------------------------------");
    }
    
    public static List<BTOapplication> getApplicationList() { return applicationList; }
}
