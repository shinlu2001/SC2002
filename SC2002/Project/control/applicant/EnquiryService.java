package SC2002.Project.control.applicant;

import java.util.Scanner;
import SC2002.Project.entity.ApplicantBase;
import SC2002.Project.entity.Enquiry;
import SC2002.Project.util.Input;

/**
 * Handles creation and management of enquiries for an applicant.
 */
public class EnquiryService {
    private ApplicantBase applicant;
    private Object projectDisplayer; // This could be an interface for displaying project listings

    public EnquiryService(ApplicantBase applicant, Object projectDisplayer) {
        this.applicant = applicant;
        this.projectDisplayer = projectDisplayer;
    }

    /**
     * Provides a simple menu to manage enquiries.
     *
     * @param sc the Scanner instance for input.
     */
    public void manage_enquiry(Scanner sc) {
        System.out.println("=== Enquiry Service ===");
        System.out.println("1. Create a new enquiry");
        System.out.println("2. View your existing enquiries");
        System.out.print("Enter your choice: ");
        int choice = Input.getIntInput(sc);
        switch (choice) {
            case 1:
                System.out.print("Enter your enquiry: ");
                String content = Input.getStringInput(sc);
                Enquiry enquiry = new Enquiry(applicant, content);
                applicant.getEnquiries().add(enquiry);
                System.out.println("Enquiry submitted successfully.");
                break;
            case 2:
                if (applicant.getEnquiries().isEmpty()) {
                    System.out.println("No enquiries found.");
                } else {
                    applicant.getEnquiries().forEach(e -> {
                        e.display();
                        System.out.println("--------------------------------");
                    });
                }
                break;
            default:
                System.out.println("Invalid choice. Operation aborted.");
        }
    }
}
