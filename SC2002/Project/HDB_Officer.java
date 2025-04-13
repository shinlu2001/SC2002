package SC2002.Project;

import java.util.ArrayList;
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
        this.registrationStatus = "UNREGISTERED"; // Default status
        officer_id = ++nextId;
    }

    public int getOfficerId() {
        return officer_id;
    }

    @Override
    protected int view_eligible_listings() {
        System.out.println("\n================================================================================================================");
        System.out.println("                                                  ELIGIBLE PROJECTS");
        System.out.println("================================================================================================================");
        System.out.printf("%-5s %-20s %-15s %-15s %-10s %-15s %-15s %-10s %n",
                "ID", "Project Name", "Neighbourhood", "Flat Types", "Price", "Open Date", "Close Date", "Eligibilty");
        System.err.println("----------------------------------------------------------------------------------------------------------------");
        List<Project> list = BTOsystem.projects;
        int count = 0;
        for (Project p : list) {
            // Exclude project if officer is already assigned to it.
            if (p.isVisible() && (officerProject == null || !p.equals(officerProject))) {
                String str = viewEligibleProjectsApplicant(p);
                if (!str.isBlank()) {
                    count++;
                }
                System.out.print(str);
            }
        }
        System.err.println("----------------------------------------------------------------------------------------------------------------");
        return count;
    }

    @Override
    public void start_menu(Scanner scanner) {
        int choice = 0;
        do {
            try {
                menu.printOfficerMenu(); // Officer menu with 15 options.
                choice = Input.getIntInput(scanner);
                System.out.println("--------------------------------");
                switch (choice) {
                    case 1: // Apply for a project
                        try {
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
                                Project p = BTOsystem.searchById(BTOsystem.projects, id, Project::getId);
                                if (p == null || !p.isVisible()) {
                                    System.out.println("No such project.");
                                } else {
                                    if (officerProject != null && officerProject.equals(p)) {
                                        System.out.println("You are already assigned as an officer for this project and cannot apply as an applicant.");
                                        break;
                                    }
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
                        } catch (Input.InputExitException e) {
                            System.out.println("Operation cancelled. Returning to Officer menu.");
                        }
                        break;
                    case 2: // View active application
                        try {
                            if (application == null) {
                                System.out.println("You have no active application. Please create a new application.");
                            } else {
                                application.get_details();
                            }
                        } catch (Input.InputExitException e) {
                            System.out.println("Operation cancelled. Returning to Officer menu.");
                        }
                        break;
                    case 3: // View only eligible listings
                        try {
                            int countElig = view_eligible_listings();
                            if (countElig == 0) {
                                System.out.println("No eligible projects found.");
                            }
                        } catch (Input.InputExitException e) {
                            System.out.println("Operation cancelled. Returning to Officer menu.");
                        }
                        break;
                    case 4: // View all listings
                        try {
                            view_listings();
                        } catch (Input.InputExitException e) {
                            System.out.println("Operation cancelled. Returning to Officer menu.");
                        }
                        break;
                    case 5: // Withdraw application
                        try {
                            System.out.println("Withdraw application");
                            if (application != null) {
                                application.get_details();
                                System.out.print("Confirm withdrawal? (Enter NRIC to confirm): ");
                                String confirm = Input.getStringInput(scanner);
                                if (confirm.equals(get_nric())) {
                                    application.withdraw();
                                    System.out.println("Withdrawal submitted.");
                                } else {
                                    System.out.println("Wrong NRIC, withdrawal unsuccessful.");
                                }
                            } else {
                                System.out.println("Nothing to withdraw.");
                            }
                        } catch (Input.InputExitException e) {
                            System.out.println("Operation cancelled. Returning to Officer menu.");
                        }
                        break;
                    case 6: // Manage user enquiries
                        try {
                            manage_other_enquiry(scanner);
                        } catch (Input.InputExitException e) {
                            System.out.println("Operation cancelled. Returning to Officer menu.");
                        }
                        break;
                    case 7: // Manage own enquiries
                        try {
                            manage_own_enquiry(scanner);
                        } catch (Input.InputExitException e) {
                            System.out.println("Operation cancelled. Returning to Officer menu.");
                        }
                        break;
                    case 8: // View account details
                        try {
                            System.out.println("Account details");
                            to_string();
                        } catch (Input.InputExitException e) {
                            System.out.println("Operation cancelled. Returning to Officer menu.");
                        }
                        break;
                    case 9: // Change account password
                        try {
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
                        } catch (Input.InputExitException e) {
                            System.out.println("Operation cancelled. Returning to Officer menu.");
                        }
                        break;
                    case 10: // Register to be a HDB officer of a project
                        try {
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
                            if (application != null && application.getProject() != null &&
                                    application.getProject().equals(target)) {
                                System.out.println("Cannot register as Officer for a project you have applied for as an Applicant!");
                                break;
                            }
                            if (isApplicationPeriodOverlapping(target)) {
                                System.out.println("You are already assigned or pending another project overlapping these dates!");
                                break;
                            }
                            registerForProject(target);
                        } catch (Input.InputExitException e) {
                            System.out.println("Operation cancelled. Returning to Officer menu.");
                        }
                        break;
                    case 11: // Check status of registration to be an officer
                        try {
                            System.out.println("Your Officer Registration Status: " + registrationStatus);
                            if (officerProject != null) {
                                System.out.println("Assigned Project: " + officerProject.getProjectName());
                            } else {
                                System.out.println("No assigned project currently.");
                            }
                        } catch (Input.InputExitException e) {
                            System.out.println("Operation cancelled. Returning to Officer menu.");
                        }
                        break;
                        case 12: // View project details
                        try {
                            if (officerProject != null) {
                                System.out.println(officerProject.toString());
                            } else {
                                System.out.println("You have no assigned project. You may view any visible projects:");
                                List<Project> allProjects = BTOsystem.projects;  // Declare local variable
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
                        } catch (Input.InputExitException e) {
                            System.out.println("Operation cancelled. Returning to Officer menu.");
                        }
                        break;
                    case 13: // Process flat booking
                        try {
                            System.out.println("Processing flat booking...");
                            System.out.print("Enter applicant NRIC for booking: ");
                            String applicantNRIC = Input.getStringInput(scanner);
                            System.out.print("Enter desired flat type (e.g., 2-Room, 3-Room): ");
                            String chosenFlatType = Input.getStringInput(scanner);
                            processFlatBooking(applicantNRIC, chosenFlatType);
                        } catch (Input.InputExitException e) {
                            System.out.println("Operation cancelled. Returning to Officer menu.");
                        }
                        break;
                    case 14: // View applications for assigned project
                        try {
                            System.out.println("Viewing all applications for your assigned project:");
                            viewApplicationsForAssignedProject();
                        } catch (Input.InputExitException e) {
                            System.out.println("Operation cancelled. Returning to Officer menu.");
                        }
                        break;
                    case 15: // Log out
                        System.out.println("Logged out. Returning to main menu...");
                        break;
                    default:
                        System.out.println("Invalid choice. Please try again.");
                        break;
                }
            } catch (Input.InputExitException e) {
                System.out.println("User requested exit/back in Officer menu. Returning to previous menu.");
                break;
            } catch (InputMismatchException e) {
                System.out.println("Invalid input. Please enter a number.");
                scanner.nextLine();
            }
        } while (choice != 15);
    }

    @Override
    public void to_string() {
        super.to_string();
        System.out.println("Account type: " + type);
        System.out.println("Officer ID: " + officer_id);
    }

    // Public reply method.
    public void reply_enquiry(Enquiry enquiry, String response) {
        enquiry.setStaffReply(this);
        enquiry.setResponse(response);
    }

    private void manage_own_enquiry(Scanner sc) {
        super.manage_enquiry(sc); // Use Applicant’s enquiry management for own enquiries.
    }

    private void manage_other_enquiry(Scanner scanner) {
        List<Enquiry> filteredEnquiries = new ArrayList<>();
        if (officerProject == null) { // If not assigned, show general enquiries.
            if (BTOsystem.enquiries.size() != 0) {
                for (Enquiry e : BTOsystem.enquiries) {
                    if (e.getProject() == null && !e.getCreatedByUser().equals(this)) {
                        filteredEnquiries.add(e);
                    }
                }
                System.out.println("You are not assigned to any project. Showing general (non-project) enquiries only:");
            }
        } else {
            for (Enquiry en : officerProject.getEnquiries()) {
                if (!en.getCreatedByUser().equals(this)) {
                    filteredEnquiries.add(en);
                }
            }
            System.out.println("Enquiries for project " + officerProject.getProjectName() + ":");
        }
        if (filteredEnquiries.isEmpty()) {
            System.out.println("No enquiries available.");
            return;
        }
        for (Enquiry en : filteredEnquiries) {
            System.out.printf("ID: %d, Question: %s%s%n",
                    en.getId(), truncateText(en.getEnquiry(), 30), (en.getStaff() != null ? " (Replied)" : ""));
        }
        System.out.print("Enter enquiry index to reply (or -1 to cancel): ");
        int idx = Input.getIntInput(scanner);
        if (idx == -1) {
            System.out.println("Operation cancelled.");
            return;
        }
        Enquiry selected = BTOsystem.searchById(filteredEnquiries, idx, Enquiry::getId);
        if (selected == null) {
            System.out.println("Invalid index.");
            return;
        }
        selected.display();
        System.out.print("Enter your reply: ");
        String reply = Input.getStringInput(scanner);
        reply_enquiry(selected, reply);
        System.out.println("Reply submitted successfully.");
    }

    public void registerForProject(Project project) {
        if (hasAppliedAsApplicant()) {
            System.out.println("You cannot register as an officer for a project in which you've applied as an applicant.");
            return;
        }
        if (project == null) {
            System.out.println("Project does not exist. Registration unsuccessful.");
            return;
        }
        if (officerProject != null && officerProject.equals(project)) {
            System.out.println("You are already registered as an officer for this project.");
            return;
        }
        if (officerProject != null && isApplicationPeriodOverlapping(project)) {
            System.out.println("You are already assigned or pending another project that overlaps in application period.");
            return;
        }
        if (registrationStatus.equals("UNREGISTERED")) {
            officerProject = project;
            registrationStatus = "PENDING";
            if (!BTOsystem.officers.contains(this)) {
                BTOsystem.officers.add(this);
            }
            System.out.println("Registration for " + project.getProjectName() + " submitted. Awaiting manager approval.");
        } else {
            switch (registrationStatus) {
                case "APPROVED":
                    System.out.println("You are already registered for a project.");
                    break;
                case "PENDING":
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
                !this.application.getStatus().equals("WITHDRAWN") &&
                !this.application.getStatus().contains("REJECTED")) {
            return true;
        }
        return false;
    }

    public void forceRegisterAndApprove(Project project) {
        if (registrationStatus.equals("UNREGISTERED")) {
            registrationStatus = "APPROVED";
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
        if (!targetApp.getStatus().equals("SUCCESSFUL")) {
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
