package SC2002.Project.boundary;

import SC2002.Project.ApplicantBase;
import SC2002.Project.Enquiry;
import SC2002.Project.Input;

public class ApplicantEnquiryDisplayer implements EnquiryDisplayer {
    private final ApplicantBase a;
    public ApplicantEnquiryDisplayer(ApplicantBase a) {
        this.a = a;
    }
    public void view_all_enquiry_for_user() {
        System.out.printf("%-5s %-20s %-30s %-30s %-15s %-20s%n",
                "ID", "Project", "Enquiry", "Reply", "Status", "Replied by");
        System.out.println("====================================================================================================================");
        for (Enquiry enquiry : a.getEnquiry()) {
            System.out.printf("%-5d %-20s %-30s %-30s %-15s %-20s%n",
                    enquiry.getId(),
                    enquiry.getProject() != null ? enquiry.getProject().getProjectName() : "General Enquiry",
                    Input.truncateText(enquiry.getEnquiry(), 30),
                    Input.truncateText(enquiry.getResponse(), 30),
                    enquiry.getResponse().isEmpty() ? "Pending" : "Answered",
                    enquiry.getStaff() != null ? enquiry.getStaff().get_firstname() : "");
            if (enquiry.getflatType() != null && !enquiry.getflatType().isEmpty()) {
                System.out.printf("%-5s %-20s %-30s%n", "", "", "Flat type: " + enquiry.getflatType());
            }
        }
    }

    public void view_enquiry(Enquiry en) {
        en.display();
    }

    public void viewEditableEnquiry() {
        System.out.printf("%-5s %-20s %-30s %-15s%n",
                "ID", "Project", "Enquiry", "Status");
        System.out.println("==================================================================================================================");
        for (Enquiry enquiry : a.getEnquiry()) {
            if (!enquiry.getResponse().isBlank()) {
                continue;
            }
            System.out.printf("%-5d %-20s %-30s %-15s%n",
                    enquiry.getId(),
                    enquiry.getProject() != null ? enquiry.getProject().getProjectName() : "General Enquiry",
                    Input.truncateText(enquiry.getEnquiry(), 30),
                    "Pending");
            if (enquiry.getflatType() != null && !enquiry.getflatType().isEmpty()) {
                System.out.printf("%-5s %-20s %-30s%n", "", "", "Flat type: " + enquiry.getflatType());
            }
        }
    }
}
