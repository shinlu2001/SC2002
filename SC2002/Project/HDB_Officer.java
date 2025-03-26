// package SC2002.Project;

import java.util.*;

public class HDB_Officer extends Applicant {
    private String type="Officer";
    private static int hdb_off_id = -1;
    private int officer_id;
    private String registrationStatus;
    private Project officerProject;
    private static List<HDB_Officer> officerList = new ArrayList<>();

    public HDB_Officer(String nric, String firstname, String lastname, String marital_status, int age) {
        super(nric, firstname, lastname, marital_status, age);
        this.registrationStatus = "Unregistered"; // Default status
        officer_id = ++hdb_off_id;
    }

    public void start_menu(Scanner sc) {
        //if want to apply, invoke applicant menu
    }
    
    public void reply_enquiry(Enquiry enquiry, String response) {
        enquiry.setStaffReply(this);
        enquiry.setResponse(response);;
    }

    public static List<HDB_Officer> getOfficerList()
    {
        return officerList;
    }

    public static void setOfficerList(List<HDB_Officer> newList)
    {
        officerList = newList;
    }

    public String getRegistrationStatus()
    {
        return registrationStatus;
    }

    public void setRegistrationStatus(String status)
    {
        registrationStatus = status;
    }

    public Project getAssignedProject()
    {
        return officerProject;
    }
    public void setAssignedProject(Project project)
    {
        officerProject = project;
    }

    public void registerForProject(Project project) {
        if (project == null)
        {
            System.out.println("Project does not exit. Registration unsuccessful.");
                return;
        }

        if (registrationStatus.equals("Unregistered")) {
            officerList.add(this);
            officerProject = project;
            registrationStatus = "Pending";
            System.out.println("Registration for " + project.getProjectName() + " successful. Registration request has been sent to the HDB Manager for approval.");
        } else {
            switch (registrationStatus) {
                case "Approved":
                    System.out.println("You are already registered for a project.");
                    break;
                
                case "Pending":
                    System.out.println("You are already have a pending approval for a project.");
                    break;
            
                default:
                    break;
            }
            // System.out.println("You are already registered or have a pending approval for a project.");
        }
    }


    // From pdf: Not a HDB Officer (registration not approved) for another project
    // within an application period (from application opening date,
    // inclusive, to application closing date, inclusive)
    public boolean isApplicationPeriodOverlapping(Project project) {
        // Logic to check if officer's application period overlaps with another project 
        return false;   // Placeholder, replace with actual logic.
    }

    // From pdf: No intention to apply for the project as an Applicant (Cannot apply
    // for the project as an Appplicant before and after becoming an HDB
    // Officer of the project)

    // Check if officer has applied to be a Applicant (for this project or other projects)
    public boolean hasAppliedAsApplicant() {
    // public boolean hasAppliedAsApplicant(Project project) {
        // Logic to check if officer has already applied for as an Applicant.
        return false;   // Placeholder, replace with actual logic.
    }
}