// SC2002/Project/boundary/EnquiryUI.java
package SC2002.Project.boundary;

import SC2002.Project.control.EnquiryController;
import SC2002.Project.entity.*;
import SC2002.Project.boundary.util.Input;
import SC2002.Project.boundary.util.MenuPrinter;

import java.util.List;
import java.util.Scanner;

/**
 * Boundary class for handling enquiry-related user interactions.
 */
public class EnquiryUI {
    private static final EnquiryController enquiryController = new EnquiryController();

    public static void start(Scanner sc, User user) {
        boolean exit = false;
        while (!exit) {
            MenuPrinter.printEnquiryMenu();
            try {
                int choice = Input.getIntInput(sc);
                switch (choice) {
                    case 1 -> makeGeneralEnquiry(sc, user);
                    case 2 -> makeProjectEnquiry(sc, user);
                    case 3 -> viewEnquiries(sc, user);
                    case 4 -> editEnquiry(sc, user);
                    case 5 -> deleteEnquiry(sc, user);
                    case 6 -> exit = true;
                    default -> System.out.println("Invalid choice. Please try again.");
                }
            } catch (Input.InputExitException e) {
                System.out.println("Returning to previous menu.");
                exit = true;
            }
        }
    }

    private static void makeGeneralEnquiry(Scanner sc, User user) {
        try {
            System.out.println("\nMaking General Enquiry");
            System.out.println("=====================");
            System.out.print("Enter your enquiry: ");
            String content = Input.getStringInput(sc);
            
            if (enquiryController.createGeneralEnquiry(user, content)) {
                System.out.println("Enquiry submitted successfully!");
            } else {
                System.out.println("Failed to submit enquiry. Please try again.");
            }
        } catch (Input.InputExitException e) {
            System.out.println("Enquiry creation cancelled.");
        }
    }

    private static void makeProjectEnquiry(Scanner sc, User user) {
        try {
            System.out.println("\nMaking Project-Related Enquiry");
            System.out.println("============================");
            
            // Show available projects
            List<Project> projects = enquiryController.getVisibleProjects();
            if (projects.isEmpty()) {
                System.out.println("No projects available for enquiry.");
                return;
            }

            System.out.println("\nAvailable Projects:");
            for (Project project : projects) {
                System.out.println(project.toString());
            }

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
            
            if (enquiryController.createProjectEnquiry(user, selectedProject, content, flatType)) {
                System.out.println("Project enquiry submitted successfully!");
            } else {
                System.out.println("Failed to submit enquiry. Please try again.");
            }
        } catch (Input.InputExitException e) {
            System.out.println("Enquiry creation cancelled.");
        }
    }

    private static void viewEnquiries(Scanner sc, User user) {
        List<Enquiry> enquiries = enquiryController.getUserEnquiries(user);
        if (enquiries.isEmpty()) {
            System.out.println("No enquiries found.");
            return;
        }

        System.out.println("\nYour Enquiries:");
        System.out.println("==============");
        for (Enquiry enquiry : enquiries) {
            System.out.println("\nEnquiry ID: " + enquiry.getId());
            System.out.println("Type: " + (enquiry.getProject() != null ? "Project-Related" : "General"));
            if (enquiry.getProject() != null) {
                System.out.println("Project: " + enquiry.getProject().getName());
                System.out.println("Flat Type: " + enquiry.getFlatType());
            }
            System.out.println("Content: " + enquiry.getContent());
            System.out.println("Status: " + (enquiry.getResponse() != null ? "Answered" : "Pending"));
            if (enquiry.getResponse() != null) {
                System.out.println("Response: " + enquiry.getResponse());
                System.out.println("Replied by: " + enquiry.getRespondent().getFirstName());
            }
            System.out.println("-------------------");
        }
    }

    private static void editEnquiry(Scanner sc, User user) {
        List<Enquiry> editableEnquiries = enquiryController.getEditableEnquiries(user);
        if (editableEnquiries.isEmpty()) {
            System.out.println("No editable enquiries found. (Note: Answered enquiries cannot be edited)");
            return;
        }

        System.out.println("\nEditable Enquiries:");
        System.out.println("==================");
        for (Enquiry enquiry : editableEnquiries) {
            System.out.println("\nEnquiry ID: " + enquiry.getId());
            System.out.println("Current content: " + enquiry.getContent());
            System.out.println("-------------------");
        }

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

    private static void deleteEnquiry(Scanner sc, User user) {
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
            System.out.println("Status: " + (enquiry.getResponse() != null ? "Answered" : "Pending"));
            System.out.println("-------------------");
        }

        try {
            System.out.print("\nEnter Enquiry ID to delete: ");
            int enquiryId = Input.getIntInput(sc);
            
            if (enquiryController.deleteEnquiry(user, enquiryId)) {
                System.out.println("Enquiry deleted successfully!");
            } else {
                System.out.println("Failed to delete enquiry. Please try again.");
            }
        } catch (Input.InputExitException e) {
            System.out.println("Delete operation cancelled.");
        }
    }
}
