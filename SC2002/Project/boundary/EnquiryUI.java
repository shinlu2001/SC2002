// SC2002/Project/boundary/EnquiryUI.java
package SC2002.Project.boundary;

import SC2002.Project.boundary.util.Input;
import SC2002.Project.boundary.util.MenuPrinter;
import SC2002.Project.control.ApplicantController;
import SC2002.Project.control.EnquiryController;
import SC2002.Project.control.ProjectController;
import SC2002.Project.entity.*;
import java.util.List;
import java.util.Scanner;

/**
 * Boundary class for handling enquiry-related user interactions.
 */
public class EnquiryUI {
    private static final ProjectController projectController = new ProjectController();
    private static final EnquiryController enquiryController = new EnquiryController();
    

    public static void start(Scanner sc, Applicant user) {
        ApplicantController applicantController = new ApplicantController(user);
        boolean exit = false;
        while (!exit) {
            MenuPrinter.printEnquiryMenu();
            try {
                int choice = Input.getIntInput(sc);
                switch (choice) {
                    case 1 -> makeGeneralEnquiry(sc, user, applicantController);
                    case 2 -> makeProjectEnquiry(sc, user, applicantController);
                    case 3 -> editEnquiry(sc, user);
                    case 4 -> viewEnquiries(sc, user, enquiryController.getUserEnquiries(user), true);
                    case 5 -> exit = true;
                    default -> System.out.println("Invalid choice. Please try again.");
                }
            } catch (Input.InputExitException e) {
                System.out.println("Returning to previous menu.");
                exit = true;
            }
        }
    }

    private static void makeGeneralEnquiry(Scanner sc, Applicant user, ApplicantController applicantController) {
        try {
            System.out.println("\nMaking General Enquiry");
            System.out.println("=====================");
            System.out.print("Enter your enquiry: ");
            String content = Input.getStringInput(sc);
            
            if (enquiryController.createGeneralEnquiry(user, content, applicantController)) {
                System.out.println("Enquiry submitted successfully!");
            } else {
                System.out.println("Failed to submit enquiry. Please try again.");
            }
        } catch (Input.InputExitException e) {
            System.out.println("Enquiry creation cancelled.");
        }
    }

    private static void makeProjectEnquiry(Scanner sc, Applicant user, ApplicantController applicantController) {
        try {
            System.out.println("\nMaking Project-Related Enquiry");
            System.out.println("============================");
            ApplicantUI.viewAllListings(projectController, applicantController, user);

            System.out.print("\nEnter Project ID: ");
            int projectId = Input.getIntInput(sc);
            
            Project selectedProject = enquiryController.getProjectById(projectId);
            if (selectedProject == null) {
                System.out.println("Invalid Project ID.");
                return;
            }

            System.out.print("Enter flat type (2-Room, 3-Room, etc): ");
            String flatType = Input.getStringInput(sc).toUpperCase();

            System.out.print("Enter your enquiry: ");
            String content = Input.getStringInput(sc);
            
            if (enquiryController.createProjectEnquiry(user, selectedProject, content, flatType, applicantController)) {
                System.out.println("Project enquiry submitted successfully!");
            } else {
                System.out.println("Failed to submit enquiry. Please try again.");
            }
        } catch (Input.InputExitException e) {
            System.out.println("Enquiry creation cancelled.");
        }
    }

    public static void viewSingleEnquiry(Enquiry en) {
        System.out.println("Enquiry: "+ en.getContent());
        System.out.println("Project: "+ (en.getProject()!=null?en.getProject().getName():null));
        System.out.println("Flat Type: "+en.getFlatType());
        if (en.getRespondent()==null) {
            System.out.println("No reply to this enquiry yet.");
        } else {
            System.out.println("Response: "+en.getResponse());
            System.out.println("Replied by: " + en.getRespondent().getFirstName());
        }
    }

    static void viewEnquiries(Scanner sc, Applicant user, List<Enquiry> enquiries, boolean expand) {
        System.out.printf("%-5s %-20s %-30s %-30s %-15s %-20s%n",
                "ID", "Project", "Enquiry", "Reply", "Status", "Replied by");
        System.out.println("====================================================================================================================");
        for (Enquiry enquiry : enquiries) {
            System.out.printf("%-5d %-20s %-30s %-30s %-15s %-20s%n",
                    enquiry.getId(),
                    enquiry.getProject() != null ? enquiry.getProject().getName() : "General Enquiry",
                    Input.truncateText(enquiry.getContent(), 30),
                    Input.truncateText(enquiry.getResponse(), 30),
                    enquiry.getResponse().isEmpty() ? "Pending" : "Answered",
                    enquiry.getRespondent() != null ? enquiry.getRespondent().getFirstName() : "");
            if (enquiry.getFlatType() != null && !enquiry.getFlatType().isEmpty()) {
                System.out.printf("%-5s %-20s %-30s%n", "", "", "Flat type: " + enquiry.getFlatType());
            }
        }
        if (expand) { // allows the above part to be reused in editEnquiry
            System.out.println("Select enquiry to view (-1 to cancel)");
            int en_id = Input.getIntInput(sc);
            if (en_id == -1) {
                return;
            }
            Enquiry en = enquiryController.findEnquiryById(en_id);
            if (en == null) {
                System.err.println("Invalid ID");
                return;
            }
            viewSingleEnquiry(en);
        }
        
    }

    /**
     * View enquiries for staff (managers, officers) 
     * @param sc Scanner for input
     * @param staff The staff member (Manager or Officer) viewing the enquiries
     * @param enquiries List of enquiries to display
     * @param expand Whether to allow selection and expansion of an enquiry
     */
    public static void viewEnquiryForStaff(Scanner sc, User staff, List<Enquiry> enquiries, boolean expand) {
        System.out.printf("%-5s %-20s %-30s %-30s %-15s %-20s%n",
                "ID", "Project", "Enquiry", "Reply", "Status", "Replied by");
        System.out.println("====================================================================================================================");
        for (Enquiry enquiry : enquiries) {
            System.out.printf("%-5d %-20s %-30s %-30s %-15s %-20s%n",
                    enquiry.getId(),
                    enquiry.getProject() != null ? enquiry.getProject().getName() : "General Enquiry",
                    Input.truncateText(enquiry.getContent(), 30),
                    Input.truncateText(enquiry.getResponse(), 30),
                    enquiry.getResponse().isEmpty() ? "Pending" : "Answered",
                    enquiry.getRespondent() != null ? enquiry.getRespondent().getFirstName() : "");
            if (enquiry.getFlatType() != null && !enquiry.getFlatType().isEmpty()) {
                System.out.printf("%-5s %-20s %-30s%n", "", "", "Flat type: " + enquiry.getFlatType());
            }
        }
        
        if (expand) {
            System.out.println("Select enquiry to view (-1 to cancel)");
            int en_id = Input.getIntInput(sc);
            if (en_id == -1) {
                return;
            }
            Enquiry en = enquiryController.findEnquiryById(en_id);
            if (en == null) {
                System.err.println("Invalid ID");
                return;
            }
            viewSingleEnquiry(en);
        }
    }

    private static void editEnquiry(Scanner sc, Applicant user) {
        List<Enquiry> editableEnquiries = enquiryController.getEditableEnquiries(user);
        if (editableEnquiries.isEmpty()) {
            System.out.println("No editable enquiries found. (Note: Answered enquiries cannot be edited)");
            return;
        }

        viewEnquiries(sc, user, editableEnquiries, false);

        try {
            System.out.print("\nEnter Enquiry ID to edit: ");
            int enquiryId = Input.getIntInput(sc);
            
            System.out.print("Enter new content: ");
            String newContent = Input.getStringInput(sc);
            
            if (enquiryController.editEnquiry(user, enquiryId, newContent)) {
                System.out.println("Enquiry updated successfully!");
            } else {
                System.out.println("Failed to update enquiry. Please try again.");
            }
        } catch (Input.InputExitException e) {
            System.out.println("Edit operation cancelled.");
        }
    }
}
