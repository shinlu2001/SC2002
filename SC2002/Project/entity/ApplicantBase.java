package SC2002.Project.entity;

import java.util.ArrayList;
import java.util.List;
import SC2002.Project.util.Input;

public abstract class ApplicantBase extends User implements Input {
    protected BTOapplication application = null;
    protected List<BTOapplication> applicationHistory = new ArrayList<>();
    protected List<Enquiry> enquiries = new ArrayList<>();

    public ApplicantBase(String nric, String firstname, String lastname, String maritalStatus, int age) {
        super(nric, firstname, lastname, maritalStatus, age);
    }
    
    public void printDetails() {
        super.printDetails();
    }
    
    public BTOapplication getApplication() {
        return application;
    }
    
    public List<BTOapplication> getApplicationHistory() {
        return applicationHistory;
    }
    
    public List<Enquiry> getEnquiries() {
        return enquiries;
    }
    
    public boolean isEligible(String flatType) {
        if (getMaritalStatus().equalsIgnoreCase("SINGLE") && getAge() >= 35 && flatType.equalsIgnoreCase("2-Room")) {
            return true;
        } else if (getMaritalStatus().equalsIgnoreCase("MARRIED") && getAge() >= 21) {
            return true;
        }
        return false;
    }
    
    public void setApplication(BTOapplication a) {
        this.application = a;
    }
}
