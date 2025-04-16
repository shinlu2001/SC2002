package SC2002.ProjectOLD;
import java.util.ArrayList;
import java.util.List;

public class BTOapplication {
    //---------------------add--------------------------
    // private String applicationStatus;
    private boolean withdrawalRequested;
    private List<BTOapplication> approvedApplications = new ArrayList<>();
    private static List<BTOapplication> applicationList = new ArrayList<>();
    private boolean requestBooking=false;
    private Flat bookedFlat=null;
    //--------------------------------------------------
    protected static int nextId = -1;   // auto-incrementing ID
    private int applicationId;    
    // References to the applicant and project
    private Applicant applicant;
    private Project project;    // protected or???
    private HDB_Manager manager;

    // The flat type the applicant is applying for (e.g., "2-Room", "3-Room")
    private String flatType;
    
    // Application status (e.g., "Pending", "Successful", "Booked", "Withdrawn", etc.)
    private String status = "PENDING";  // protected??
    
    /**
     * Constructor to create a new BTOapplication.
     * 
     * @param applicant the applicant who is applying
     * @param project   the project being applied for
     * @param flatType  the flat type chosen (e.g., "2-Room" or "3-Room")
     */
    public BTOapplication(Applicant applicant, Project project, String flatType) {
        applicationId = ++nextId;
        this.applicant = applicant;
        this.project = project;
        this.flatType = flatType.toUpperCase();
        this.status = "PENDING"; // default status
        manager=project.getManager();
    }
    // manager constructor version of btoapplication, for the sake of running tests
    // implement the actual changes later
    // public BTOapplication(HDB_Manager manager, Project project, String flatType) {
    //     this.applicationId = ++nextId;
    //     this.manager = manager;
    //     this.project = project;
    //     this.flatType = flatType;
    //     this.status = "Unregistered"; // default status
    // }
    //---------------------add--------------------------
    public void setApplicationStatus(String applicationStatus)
    {
        this.status = applicationStatus.toUpperCase();
    }

   public void bookFlat(Flat flat) {
        bookedFlat = flat;
   }

    public void requestBooking() {
        requestBooking=true;
    }
    
    public static  List<BTOapplication> getApplicationList(){
        return applicationList;
    }
    
    public boolean getWithdrawalRequested()
    {
        return withdrawalRequested;
    }

    public void withdraw()
    {
        withdrawalRequested = true;
    }

    public List<BTOapplication> getapprovedApplications()
    {
        return approvedApplications;
    }

    //--------------------------------------------------

    // Getters
    public int getId() {
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
        System.out.println("====================================================================================================================");
        System.out.println("                                            Application Details");
        if (withdrawalRequested) {
            System.out.println("|W I T H D R A W A L Requested|");
        }
        System.out.println("Application ID: " + applicationId);
        if (applicant != null) {
            
            System.out.println("Applicant: " 
                + applicant.get_firstname() + " " + applicant.get_lastname());
            System.out.println("NRIC: " + applicant.get_nric());
            System.out.println("Manager In Charge: " + manager.get_firstname());
            System.out.println("Project: " + getProjectName());
            System.out.println("Status: " + status);
            System.out.println("Flat Type: " + flatType);
            
            if (bookedFlat!=null) {
                System.out.println("Flat Price: " + bookedFlat.getPrice());
                System.out.println("Booked FlatID" + bookedFlat.getFlatID());
            }

        }
        
    }

    public void registerForProject(Project project) {
        if (project == null)
        {
            System.out.println("Project does not exit. Registration unsuccessful.");
                return;
        }

        if (status.equals("UNREGISTERED")) {
            applicationList.add(this);
            this.project = project;
            status = "PENDING";
            System.out.println("Registration for " + project.getProjectName() + " successful. Registration request has been sent to the HDB Manager for approval.");
        } else {
            switch (status) {
                case "APPROVED":
                    System.out.println("You are already registered for a project.");
                    break;
                
                case "PENDING":
                    System.out.println("You are already have a pending approval for a project.");
                    break;
            
                default:
                    break;
            }
            // System.out.println("You are already registered or have a pending approval for a project.");
        }
    }
}
