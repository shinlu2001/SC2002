package SC2002.Project.boundary;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;

import SC2002.Project.boundary.util.Input;
import SC2002.Project.control.EnquiryController;
import SC2002.Project.control.StaffControllerInterface;
import SC2002.Project.entity.*;

public class StaffUI {
    // works for both manager AND officer controller
    public static void manageUserEnquiries(Scanner sc, User staff, EnquiryController enquiryCtrl, StaffControllerInterface officerCtrl) {
        List<Enquiry> relevantEnquiries = officerCtrl.getPendingEnquiries(enquiryCtrl);

        System.out.println("\nPending Enquiries (General / Project-Related):");
        System.out.println("------------------");
        EnquiryUI.viewEnquiries(sc, relevantEnquiries, false); // Use false to prevent immediate selection prompt

        try {
            System.out.print("Enter Enquiry ID to respond (or type 'back' to return): ");
            String input = sc.nextLine().trim();
            
            if (input.equalsIgnoreCase("back")) {
                return;
            }
            
            try {
                int enquiryId = Integer.parseInt(input);
                
                Optional<Enquiry> selectedEnquiryOpt = relevantEnquiries.stream()
                        .filter(e -> e.getId() == enquiryId)
                        .findFirst();
    
                if (selectedEnquiryOpt.isEmpty()) {
                    System.out.println("Invalid Enquiry ID or enquiry not actionable by you.");
                    return;
                }
    
                Enquiry selectedEnquiry = selectedEnquiryOpt.get();
                System.out.println("\nSelected Enquiry:");
                EnquiryUI.viewSingleEnquiry(selectedEnquiry); // Display full details
    
                System.out.print("Enter your response: ");
                String response = Input.getStringInput(sc);
    
                if (enquiryCtrl.respondToEnquiry(staff, enquiryId, response)) {
                    System.out.println("Response submitted successfully!");
                } else {
                    System.out.println("Failed to submit response. Please try again.");
                }
            } catch (NumberFormatException e) {
                System.out.println("Please enter a valid enquiry ID number.");
            }
        } catch (Input.InputExitException e) {
            System.out.println("Response process cancelled.");
        } catch (Exception e) {
            System.err.println("Error managing enquiries: " + e.getMessage());
        }
    }
}
