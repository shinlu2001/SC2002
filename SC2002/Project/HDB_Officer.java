package SC2002.Project;

import java.util.InputMismatchException;
import java.util.List;
import java.util.Scanner;

public class HDB_Officer extends Applicant {
    private String type = "OFFICER";
    protected static int nextId = -1;
    private int officer_id;
    protected String registrationStatus;
    protected Project officerProject;
    private boolean withdrawalRequested;

    public HDB_Officer(String nric, String firstname, String lastname, String marital_status, int age) {
        super(nric, firstname, lastname, marital_status, age);
        this.registrationStatus = "Unregistered"; // Default status
        officer_id = ++nextId;
    }
    
    public int getOfficerId() {
        return officer_id;
    }

    public void start_menu(Scanner scanner) {
        int choice = 0;
        do {
            try {
                menu.printOfficerMenu();
                choice = Input.getIntInput(scanner);
                System.out.println("--------------------------------");
                switch (choice) {
                    case 1:
                    if (application != null) {
                        System.out.println("You already have an active application. You may not create a new one.");
                    } else {
                        System.out.println("Apply for a project");
                        int count = view_eligible_listings();
                        if (count == 0) {
                            System.out.println("You are not eligible to apply for any project.");
                            break;
                        }
                        System.out.println("Enter ProjectID: ");
                        int id = Input.getIntInput(scanner);
                        Project p = BTOsystem.searchProjectById(id);
                        if (p == null || !p.isVisible()) {
                            System.out.println("No such project.");
                        } else {
                            System.out.println("Enter room type (2-Room, 3-Room, etc): ");
                            String roomtype = Input.getStringInput(scanner);
                            if (!getEligibility(roomtype)) {
                                System.out.println("Not eligible for this project and room type.");
                            } else {
                                BTOapplication b = new BTOapplication(this, p, roomtype);
                                BTOsystem.applications.add(b);
                                System.out.println("Application submitted!");
                                application = b;
                            }
                        }
                    }
                    System.out.println("--------------------------------");
                    break;
                    case 2:
                        if (application == null) {
                            System.out.println("You have no active application. Please create a new application.");
                        } else {
                            application.get_details();
                        }
                        System.out.println("--------------------------------");
                        break;
                    case 3:
                        HDB_Manager.viewAllProjects();
                        break;
                    case 4:
                        System.out.println("Withdraw application");
                        // (Withdrawal functionality can be implemented if needed.)
                        System.out.println("--------------------------------");
                        break;
                    case 5:
                        // Officers do not create/edit enquiries; they only reply.
                        manage_enquiry(scanner);
                        System.out.println("--------------------------------");
                        break;
                    case 6:
                        System.out.println("Account details");
                        to_string();
                        System.out.println("--------------------------------");
                        break;
                    case 7:
                        System.out.println("Change your password");
                        System.out.print("Enter current password: ");
                        String oldpass = Input.getStringInput(scanner);
                        System.out.print("Enter new password: ");
                        String new_pass1 = Input.getStringInput(scanner);
                        System.out.print("Enter new password again to confirm: ");
                        String new_pass2 = Input.getStringInput(scanner);
                        if (!verify_password(oldpass)) {
                            System.out.println("Current password is wrong. Password change unsuccessful.");
                        } else if (!new_pass1.equals(new_pass2)) {
                            System.out.println("New passwords do not match.");
                        } else {
                            change_password(new_pass2);
                            System.out.println("Password changed successfully.");
                        }
                        System.out.println("--------------------------------");
                        break;
                    case 8:
                        List<Project> allProjects = BTOsystem.projects;
                        System.out.println("=== Choose a project to register as Officer ===");
                        for (int i = 0; i < allProjects.size(); i++) {
                            System.out.println("[" + i + "] " + allProjects.get(i).getProjectName());
                        }
                        System.out.print("Enter index: ");
                        int projChoice = Input.getIntInput(scanner);
                        if (projChoice < 0 || projChoice >= allProjects.size()) {
                            System.out.println("Invalid choice. Returning...");
                            break;
                        }
                        Project target = allProjects.get(projChoice);
                        // Check if the officer has applied as an Applicant for the same project
                        if (application != null && application.getProject() != null 
                                && application.getProject().equals(target)) {
                            System.out.println("Cannot register as Officer for a project you have applied for as an Applicant!");
                            break;
                        }
                        // Check for overlapping application periods
                        if (isApplicationPeriodOverlapping(target)) {
                            System.out.println("You are already assigned or pending another project overlapping these dates!");
                            break;
                        }
                        registerForProject(target);
                        break;
                    case 9:
                        System.out.println("Your Officer Registration Status: " + registrationStatus);
                        if (officerProject != null) {
                            System.out.println("Assigned Project: " + officerProject.getProjectName());
                        } else {
                            System.out.println("No assigned project currently.");
                        }
                        System.out.println("--------------------------------");
                        break;
                    case 10:
                        if (officerProject != null) {
                            System.out.println(officerProject.toString());
                        } else {
                            System.out.println("You have no assigned project. You may view any visible projects:");
                            allProjects = BTOsystem.projects;
                            for (int i = 0; i < allProjects.size(); i++) {
                                Project p = allProjects.get(i);
                                if (p.isVisible()) {
                                    System.out.println("[" + i + "] " + p.getProjectName());
                                }
                            }
                            System.out.print("Enter index or -1 to cancel: ");
                            int idx = Input.getIntInput(scanner);
                            if (idx >= 0 && idx < allProjects.size() && allProjects.get(idx).isVisible()) {
                                System.out.println(allProjects.get(idx));
                            } else {
                                System.out.println("No such project or canceled.");
                            }
                        }
                        System.out.println("--------------------------------");
                        break;
                    case 11:
                        System.out.println("Processing flat booking...");
                        System.out.print("Enter applicant NRIC for booking: ");
                        String applicantNRIC = Input.getStringInput(scanner);
                        System.out.print("Enter desired flat type (e.g., 2-Room, 3-Room): ");
                        String chosenFlatType = Input.getStringInput(scanner);
                        processFlatBooking(applicantNRIC, chosenFlatType);
                        break;
                    case 12:
                        System.out.println("Viewing all applications for your assigned project:");
                        viewApplicationsForAssignedProject();
                        break;
                    case 13:
                        System.out.println("Logged out. Returning to main menu...");
                        break;
                    default:
                        System.out.println("Invalid choice. Please try again.");
                        break;
                }
            } catch (InputMismatchException e) {
                System.out.println("Invalid input. Please enter a number.");
                scanner.nextLine();
            }
        } while (choice != 13);
    }
    
    public void to_string() {
        super.to_string();
        System.out.println("Account type: " + type);
        System.out.println("Officer ID: " + officer_id);
    }
    
    // Public reply method accessible to officers (and managers) for replying to enquiries.
    public void reply_enquiry(Enquiry enquiry, String response) {
        enquiry.setStaffReply(this);
        enquiry.setResponse(response);
    }
    
    // Officers typically reply to enquiries from their assigned project.
    private void manage_enquiry(Scanner scanner) {
        if (officerProject == null) {
            System.out.println("You are not assigned to any project; no enquiries available.");
            return;
        }
        List<Enquiry> projectEnquiries = officerProject.getEnquiries();
        if (projectEnquiries == null || projectEnquiries.isEmpty()) {
            System.out.println("No enquiries available for your project.");
            return;
        }
        System.out.println("Enquiries for project " + officerProject.getProjectName() + ":");
        for (int i = 0; i < projectEnquiries.size(); i++) {
            Enquiry en = projectEnquiries.get(i);
            System.out.printf("[%d] ID: %d, Question: %s%s%n",
                    i, en.getEnId(), en.getEnquiry(), (en.getStaff() != null ? " (Replied)" : ""));
        }
        System.out.print("Enter enquiry index to reply (or -1 to cancel): ");
        int idx = Input.getIntInput(scanner);
        if (idx == -1) {
            System.out.println("Operation cancelled.");
            return;
        }
        if (idx < 0 || idx >= projectEnquiries.size()) {
            System.out.println("Invalid index.");
            return;
        }
        Enquiry selected = projectEnquiries.get(idx);
        System.out.println("Selected enquiry: " + selected.getEnquiry());
        System.out.print("Enter your reply: ");
        String reply = Input.getStringInput(scanner);
        reply_enquiry(selected, reply);
        System.out.println("Reply submitted successfully.");
    }
    
    public void registerForProject(Project project) {
        if (project == null) {
            System.out.println("Project does not exist. Registration unsuccessful.");
            return;
        }
        
        if (registrationStatus.equals("Unregistered")) {
            // Set status to Pending and assign the project
            officerProject = project;
            registrationStatus = "Pending";
            if (!BTOsystem.officers.contains(this)) {
                BTOsystem.officers.add(this);
            }
            System.out.println("Registration for " + project.getProjectName() + " submitted. Awaiting manager approval.");
        } else {
            switch (registrationStatus) {
                case "Approved":
                    System.out.println("You are already registered for a project.");
                    break;
                case "Pending":
                    System.out.println("Your registration is already pending approval.");
                    break;
                default:
                    break;
            }
        }
    }
    
    public boolean isApplicationPeriodOverlapping(Project project) {
        if (officerProject == null) {
            return false;
        }
        if (project.getCloseDate().isBefore(officerProject.getOpenDate()) ||
            project.getOpenDate().isAfter(officerProject.getCloseDate())) {
            return false;
        }
        return true;
    }
    
    public boolean hasAppliedAsApplicant() {
        if (this.application != null &&
            !this.application.getStatus().equalsIgnoreCase("Withdrawn") &&
            !this.application.getStatus().contains("Rejected")) {
            return true;
        }
        return false;
    }
    
    public void forceRegisterAndApprove(Project project) {
        if (registrationStatus.equals("Unregistered")) {
            registrationStatus = "Approved";
            project.addOfficer(this);
        }
    }
    
    public void processFlatBooking(String applicantNRIC, String chosenFlatType) {
        BTOapplication targetApp = null;
        for (BTOapplication app : BTOsystem.applications) {
            if (app.getApplicant().get_nric().equalsIgnoreCase(applicantNRIC)) {
                targetApp = app;
                break;
            }
        }
        if (targetApp == null) {
            System.out.println("No application found for NRIC: " + applicantNRIC);
            return;
        }
        if (!targetApp.getStatus().equalsIgnoreCase("Successful")) {
            System.out.println("Application is not in a successful state for booking.");
            return;
        }
        Project proj = targetApp.getProject();
        int index = -1;
        List<String> flatTypes = proj.getFlatTypes();
        for (int i = 0; i < flatTypes.size(); i++) {
            if (flatTypes.get(i).equalsIgnoreCase(chosenFlatType)) {
                index = i;
                break;
            }
        }
        if (index == -1) {
            System.out.println("Flat type " + chosenFlatType + " is not available in this project.");
            return;
        }
        List<Integer> availableUnits = proj.getAvailableUnits();
        int currentAvailable = availableUnits.get(index);
        if (currentAvailable <= 0) {
            System.out.println("No available units for " + chosenFlatType + " in this project.");
            return;
        }
        proj.updateAvailableUnits(chosenFlatType, currentAvailable - 1);
        targetApp.setStatus("Booked");
        Flat bookedFlat = null;
        for (Flat flat : BTOsystem.flats) {
            if (flat.getProject().equals(proj) &&
                flat.getFlatType().equalsIgnoreCase(chosenFlatType) &&
                !flat.isBooked()) {
                bookedFlat = flat;
                break;
            }
        }
        if (bookedFlat == null) {
            System.out.println("Error: No available flat unit found despite availability count.");
            return;
        }
        bookedFlat.setBooked(true);
        targetApp.bookFlat(bookedFlat);
        Receipt receipt = generateBookingReceipt(targetApp, bookedFlat);
        System.out.println("Flat booking successful! Here is your receipt:");
        receipt.printReceipt();
    }
    
    public Receipt generateBookingReceipt(BTOapplication application, Flat flat) {
        String details = "Flat Booking Receipt\n" +
                         "---------------------\n" +
                         "Applicant: " + application.getApplicant().get_firstname() + " " + application.getApplicant().get_lastname() + "\n" +
                         "NRIC: " + application.getApplicant().get_nric() + "\n" +
                         "Age: " + application.getApplicant().get_age() + "\n" +
                         "Marital Status: " + application.getApplicant().get_maritalstatus() + "\n" +
                         "Project: " + application.getProjectName() + "\n" +
                         "Flat Type: " + application.getFlatType() + "\n" +
                         "Flat Price: " + flat.getPrice() + "\n";
        return new Receipt(details);
    }
    
    public void viewApplicationsForAssignedProject() {
        if (officerProject == null) {
            System.out.println("You are not assigned to any project.");
            return;
        }
        System.out.println("Applications for project " + officerProject.getProjectName() + ":");
        boolean found = false;
        for (BTOapplication app : BTOsystem.applications) {
            if (app.getProject().equals(officerProject)) {
                found = true;
                app.get_details();
                System.out.println("--------------------------------");
            }
        }
        if (!found) {
            System.out.println("No applications found for this project.");
        }
    }
    
    public boolean getwithdrawalRequested() {
        return withdrawalRequested;
    }
}
