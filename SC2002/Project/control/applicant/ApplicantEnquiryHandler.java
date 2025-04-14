package SC2002.Project.control.applicant;

import SC2002.Project.ApplicantBase;
import SC2002.Project.BTOsystem;
import SC2002.Project.Enquiry;
import SC2002.Project.Project;
import SC2002.Project.boundary.ApplicantEnquiryDisplayer;

public class ApplicantEnquiryHandler {
    private ApplicantBase applicant=null;
    public ApplicantEnquiryHandler(ApplicantBase applicant) {
        this.applicant = applicant;
    }
    public void makeEnquiry(String content) {
        Enquiry en = new Enquiry(applicant, content);
        applicant.getEnquiry().add(en);
        BTOsystem.enquiries.add(en);
    }
    public void makeEnquiry(Project project, String content, String flatType) {
        Enquiry en = new Enquiry(applicant, content);
        en.setProject(project);
        en.setflatType(flatType);
        applicant.getEnquiry().add(en);
        BTOsystem.enquiries.add(en);
        project.addEnquiry(en);
    }

    public void editEnquiry(Enquiry en, String content) {
        en.setEnquiry(content);
    }

    public void deleteEnquiry(int id) {
        Enquiry toRemove = null;
        for (Enquiry en : applicant.getEnquiry()) {
            if (en.getId() == id) {
                toRemove = en;
                break;
            }
        }
        if (toRemove != null) {
            new ApplicantEnquiryDisplayer(applicant).view_enquiry(toRemove);
            applicant.getEnquiry().remove(toRemove);
            System.out.println("Deleted enquiry: " + toRemove.getEnquiry());
            System.out.println("Deleted response: " + toRemove.getResponse());
        } else {
            System.out.println("No enquiry found with ID: " + id);
        }
    }
}
