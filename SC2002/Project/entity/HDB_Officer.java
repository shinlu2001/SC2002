package SC2002.Project.entity;

import java.util.ArrayList;
import java.util.List;

public class HDB_Officer extends ApplicantBase {
    private String type = "OFFICER";
    protected static int nextId = -1;
    private int officerID;
    private List<OfficerRegistration> officerRegistrations = new ArrayList<>();
    
    public static class OfficerRegistration {
        private Project project;
        private String status = "PENDING";
        
        public OfficerRegistration(Project project, String status) {
            this.project = project;
            this.status = status;
        }
        
        public Project getProject() { return project; }
        public String getStatus() { return status; }
        public void setStatus(String status) { this.status = status; }
    }
    
    public HDB_Officer(String nric, String firstname, String lastname, String maritalStatus, int age) {
        super(nric, firstname, lastname, maritalStatus, age);
        this.officerID = ++nextId;
    }
    
    public int getOfficerID() { return officerID; }
    public List<OfficerRegistration> getOfficerRegistrations() { return officerRegistrations; }
    
    public OfficerRegistration getRegistrationForProject(Project project) {
        for (OfficerRegistration reg : officerRegistrations) {
            if (reg.getProject().equals(project)) { return reg; }
        }
        return null;
    }
    
    public boolean isRegisteredForProject(Project project) {
        return getRegistrationForProject(project) != null;
    }
    
    public boolean hasAppliedAsApplicant() {
        if (this.application != null &&
            !this.application.getStatus().equalsIgnoreCase("WITHDRAWN") &&
            !this.application.getStatus().toUpperCase().contains("REJECTED")) {
            return true;
        }
        return false;
    }
    
    @Override
    public void printDetails() {
        super.printDetails();
        System.out.println("Account type: " + type);
        System.out.println("Officer ID: " + officerID);
    }
    
    // New method needed for CSV load
    public void forceRegisterAndApprove(Project project) {
        OfficerRegistration reg = new OfficerRegistration(project, "APPROVED");
        officerRegistrations.add(reg);
    }
}
