package SC2002.Project.boundary;

import SC2002.Project.boundary.util.Input;
import SC2002.Project.boundary.util.MenuPrinter;
import SC2002.Project.control.ApplicantController;
import SC2002.Project.control.EnquiryController;
import SC2002.Project.control.ProjectController;
import SC2002.Project.entity.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * Boundary class for handling enquiry-related user interactions.
 */
public class EnquiryUI {  //this is more for applicants
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
                    case 4 -> viewEnquiries(sc, enquiryController.getUserEnquiries(user), true);
                    case 5 -> deleteEnquiry(sc, user, applicantController);
                    case 6 -> exit = true;
                    default -> System.out.println("Invalid choice. Please try again.");
                }
            } catch (Input.InputExitException e) {
                System.out.println("Returning to previous menu.");
                exit = true;
            }
        }
    }

    private static void makeGeneralEnquiry(Scanner sc, Applicant user, ApplicantController applicantController) {  //user  applicant  controller  because  we  are  making  changes  to  applicant's  attributes (enquiries list)                                                                                                                
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
            System.out.println("(select 0 after viewing/filtering projects to start making enquiry)");
            ApplicantUI.viewAllListings(sc, projectController, applicantController, user);

            System.out.print("Enter Project ID: ");
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

            if (enquiryController.createProjectEnquiry(user, selectedProject, content, flatType, applicantController,
                    projectController)) {
                System.out.println("Project enquiry submitted successfully!");
            } else {
                System.out.println("Failed to submit enquiry. Please try again.");
            }
        } catch (Input.InputExitException e) {
            System.out.println("Enquiry creation cancelled.");
        }
    }

    public static void viewSingleEnquiry(Enquiry en) {
        System.out.println("Enquiry: " + en.getContent());
        System.out.println("Project: " + (en.getProject() != null ? en.getProject().getName() : null));
        System.out.println("Flat Type: " + en.getFlatType());
        if (en.getRespondent() == null) {
            System.out.println("No reply to this enquiry yet.");
        } else {
            System.out.println("Response: " + en.getResponse());
            System.out.println("Replied by: " + en.getRespondent().getFirstName());
        }
    }

    public static void viewEnquiries(Scanner sc, List<Enquiry> enquiries, boolean expand) {
        System.out.printf("%-5s %-15s %-15s %-35s %-35s %-15s %-20s%n",
                "ID", "Type", "Enquirer", "Enquiry", "Reply", "Status", "Replied by");
        System.out.println("=".repeat(150));
        for (Enquiry enquiry : enquiries) {
            System.out.printf("%-5d %-15s %-15s %-35s %-35s %-15s %-20s%n",
                    enquiry.getId(),
                    enquiry.getProject() != null ? Input.truncateText(enquiry.getProject().getName(), 13) : "General",
                    enquiry.getCreator() != null ? Input
                            .truncateText(
                                    enquiry.getCreator().getFirstName() + " " + enquiry.getCreator().getLastName(), 12)
                            .trim() : "",
                    Input.truncateText(enquiry.getContent(), 32),
                    Input.truncateText(enquiry.getResponse(), 32),
                    enquiry.getResponse().isEmpty() ? "Pending" : "Answered",
                    enquiry.getRespondent() != null ? Input.truncateText(
                            enquiry.getRespondent().getFirstName() + " " + enquiry.getRespondent().getLastName(), 17)
                            .trim() : "");
            if (enquiry.getFlatType() != null && !enquiry.getFlatType().isEmpty()) {
                System.out.printf("%-5s %-20s %-15s %-35s%n", "", "", "", "Flat type: " + enquiry.getFlatType());
            }
            System.out.println();
        }
        if (expand) {  //allows the above part to be reused in editEnquiry
            System.out.println("Select enquiry to view (-1 to cancel)");
            int en_id = Input.getIntInput(sc);
            if (en_id == -1) {
                return;
            }
            Enquiry en = enquiryController.findEnquiryById(en_id);
            if (en == null || !enquiries.contains(en)) {
                System.err.println("Invalid ID or enquiry not actionable by you.");
                return;
            }
            viewSingleEnquiry(en);
        }
    }

    public static void viewEnquiriesStaff(Scanner sc, EnquiryController enctrl, StaffControllerInterface manctrl) {
        List<Enquiry> filteredEnquiry = new ArrayList<>();
        filteredEnquiry.addAll(enctrl.getGeneralEnquiries());
        for (Project p : manctrl.getAssignedProjects()) {
            if (p.getEnquiries().size() == 0) {
                continue;
            }
            filteredEnquiry.addAll(p.getEnquiries());
        }
        viewEnquiries(sc, filteredEnquiry, true);
    }

    private static void editEnquiry(Scanner sc, Applicant user) {
        List<Enquiry> editableEnquiries = enquiryController.getEditableEnquiries(user);
        if (editableEnquiries.isEmpty()) {
            System.out.println("No editable enquiries found. (Note: Answered enquiries cannot be edited)");
            return;
        }

        viewEnquiries(sc, editableEnquiries, false);

        try {
            System.out.print("\nEnter Enquiry ID to edit: ");
            int enquiryId = Input.getIntInput(sc);
            Enquiry en = enquiryController.findEnquiryById(enquiryId);
            if (en != null) {
                viewSingleEnquiry(en);
                System.out.print("Enter new content: ");
                String newContent = Input.getStringInput(sc);

                if (enquiryController.editEnquiry(user, enquiryId, newContent)) {
                    System.out.println("Enquiry updated successfully!");
                } else {
                    System.out.println("Failed to update enquiry. Please try again.");
                }
            }
        } catch (Input.InputExitException e) {
            System.out.println("Edit operation cancelled.");
        }
    }

    private static void deleteEnquiry(Scanner sc, Applicant user, ApplicantController applicantController) {
        List<Enquiry> userEnquiries = enquiryController.getUserEnquiries(user);
        if (userEnquiries.isEmpty()) {
            System.out.println("No enquiries found to delete.");
            return;
        }

        System.out.println("\nYour Enquiries:");
        System.out.println("==============");
        for (Enquiry enquiry : userEnquiries) {
            System.out.println("\nEnquiry ID: " + enquiry.getId());
            System.out.println("Content: " + enquiry.getContent());
            System.out.println("Status: " + (!enquiry.getResponse().isBlank() ? "Answered" : "Pending"));
            System.out.println("-------------------");
        }

        try {
            System.out.print("\nEnter Enquiry ID to delete: ");
            int enquiryId = Input.getIntInput(sc);

            if (enquiryController.deleteEnquiry(user, enquiryId, applicantController, projectController)) {
                System.out.println("Enquiry deleted successfully!");
            } else {
                System.out.println("Failed to delete enquiry. Please try again.");
            }
        } catch (Input.InputExitException e) {
            System.out.println("Delete operation cancelled.");
        }
    }
}
