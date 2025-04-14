package SC2002.Project;

import java.util.ArrayList;
import java.util.List;

public abstract class ApplicantBase extends User implements Input{
    protected BTOapplication application = null;
    protected List<BTOapplication> applicationHistory = new ArrayList<>();
    protected List<Enquiry> enquiries = new ArrayList<>();

    public ApplicantBase(String nric, String firstname, String lastname, String marital_status, int age) {
        super(nric, firstname, lastname, marital_status, age);
    }
    
    public void to_string() {
        super.to_string();
    }

    public BTOapplication getApplication() {
        return application;
    }
    
    public List<BTOapplication> getApplicationHistory() {
        return applicationHistory;
    }

    public List<Enquiry> getEnquiry() {
        return enquiries;
    }

    public boolean getEligibility(String flatType) {
        if (get_maritalstatus().equalsIgnoreCase("SINGLE") && get_age() >= 35 && flatType.equalsIgnoreCase("2-Room")) {
            return true;
        } else if (get_maritalstatus().equalsIgnoreCase("MARRIED") && get_age() >= 21) {
            return true;
        }
        return false;
    }
    
    public void setApplication(BTOapplication a) {
        application = a;
    }
    
    
}
