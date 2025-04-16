package SC2002.ProjectOLD;

import java.util.*;
import java.time.LocalDate;

public class Applicant extends User implements Input {
    protected static int nextId = -1;
    private int applicantID;
    protected BTOapplication application = null;
    protected List<BTOapplication> applicationHistory = new ArrayList<>();
    private String type = "APPLICANT";
    protected List<Enquiry> enquiries = new ArrayList<>();

    public Applicant(String nric, String firstname, String lastname, String marital_status, int age) {
        super(nric, firstname, lastname, marital_status, age);
        applicantID = ++nextId;
    }

    enum ApplicantOption {
        APPLY, VIEW_APPLICATION, VIEW_ELIGIBLE, VIEW_LISTINGS, WITHDRAW, ENQUIRY, ACCOUNT, CHANGE_PASSWORD, EXIT;
    }

    public BTOapplication getApplication() {
        return application;
    }
    
    @Override
    public void start_menu(Scanner sc) {
        System.out.println("Welcome to HDB BTO Management System, " + this.get_firstname() + "!");
        int choice = 0;
        // Continue displaying the applicant menu until the user selects the explicit exit option.
        while (true) {
            try {
                System.out.println("====================================================================================================================");
                System.out.println("                                          A P P L I C A N T   M E N U");
                System.out.println("====================================================================================================================");
                menu.printApplicantMenu();
                try {
                    choice = Input.getIntInput(sc);
                } catch (Input.InputExitException e) {
                    System.out.println("Operation cancelled. Returning to Applicant menu.");
                    continue; // re-display the applicant menu
                }
                System.out.println("====================================================================================================================");
                if (choice >= 1 && choice <= ApplicantOption.values().length) {
                    ApplicantOption selectedOption = ApplicantOption.values()[choice - 1];
                    switch (selectedOption) {
                        case APPLY:
                            try {
                                if (application == null || application.getStatus().equals("WITHDRAWN" ) || application.getStatus().equals("REJECTED")) {
                                    if (application != null) {
                                        applicationHistory.add(application);
                                    }
                                    System.out.println("Apply for a project");
                                    int count = view_eligible_listings();
                                    if (count == 0) {
                                        System.out.println("You are not eligible to apply for any project.");
                                        break;
                                    }
                                    System.out.println("Enter ProjectID: ");
                                    int id = Input.getIntInput(sc);
                                    Project p = BTOsystem.searchById(BTOsystem.projects, id, Project::getId);
                                    if (p == null || !p.isVisible()) {
                                        System.out.println("No such project.");
                                    } else {
                                        System.out.println("Enter room type (2-Room, 3-Room, etc): ");
                                        String roomtype = Input.getStringInput(sc);
                                        if (!getEligibility(roomtype)) {
                                            System.out.println("Not eligible for this project and room type.");
                                        } else {
                                            BTOapplication b = new BTOapplication(this, p, roomtype.toUpperCase());
                                            BTOsystem.applications.add(b);
                                            System.out.println("Application submitted!");
                                            application = b;
                                        }
                                    }
                                } else {
                                    System.out.println("You already have an active application. You may not create a new one." + application.getStatus());
                                }
                            } catch (Input.InputExitException e) {
                                System.out.println("Operation cancelled. Returning to Applicant menu.");
                            }
                            break;
                            
                        case VIEW_APPLICATION:
                            try {
                                if (application == null) {
                                    System.out.println("You have no active application. Please create a new application.");
                                } else {
                                    application.get_details();
                                    if (application.getStatus().equalsIgnoreCase("Successful")) {
                                        System.out.println("Congrats! Your application is successful!");
                                        System.out.println("Enter 1 to book a flat (any other key to exit): ");
                                        int book = Input.getIntInput(sc);
                                        if (book == 1) {
                                            System.out.println("Your request to book a flat has been submitted.");
                                            System.out.println("Our friendly HDB officer will assist you in the booking of a flat");
                                            application.requestBooking();
                                        }
                                    }
                                }
                            } catch (Input.InputExitException e) {
                                System.out.println("Operation cancelled. Returning to Applicant menu.");
                            }
                            break;
                            
                        case VIEW_ELIGIBLE:
                            try {
                                int countEligible = view_eligible_listings();
                                if (countEligible == 0) {
                                    System.out.println("You are not eligible to apply for any project.");
                                }
                            } catch (Input.InputExitException e) {
                                System.out.println("Operation cancelled. Returning to Applicant menu.");
                            }
                            break;
                            
                        case VIEW_LISTINGS:
                            view_listings();
                            break;
                            
                        case WITHDRAW:
                            try {
                                if (application.getStatus().equals("REJECTED")) {
                                    System.out.println("Application already rejected. Press enter to return to role menu");
                                    sc.nextLine();
                                    break;
                                }
                                System.out.println("Withdraw Request");
                                if (application != null) {
                                    application.get_details();
                                    // Allow user to press Enter to continue
                                    System.out.println("(Press Enter to continue)");
                                    sc.nextLine();
                                    System.out.println("Enter NRIC to confirm withdrawal: ");
                                    String confirm = Input.getStringInput(sc);
                                    if (confirm.equals(get_nric())) {
                                        application.withdraw();
                                        System.out.println("Withdrawal request has been submitted.");
                                    } else {
                                        System.out.println("Wrong NRIC, Withdrawal Unsuccessful.");
                                    }
                                    System.out.println("====================================================================================================================");
                                } else {
                                    System.out.println("Nothing to withdraw.");
                                }
                            } catch (Input.InputExitException e) {
                                System.out.println("Operation cancelled. Returning to Applicant menu.");
                            }
                            break;
                            
                        case ENQUIRY:
                            try {
                                manage_enquiry(sc);
                            } catch (Input.InputExitException e) {
                                System.out.println("Operation cancelled. Returning to Applicant menu.");
                            }
                            break;
                            
                        case ACCOUNT:
                            try {
                                System.out.println("                                               Account details");
                                System.out.println("====================================================================================================================");
                                to_string();
                            } catch (Input.InputExitException e) {
                                System.out.println("Operation cancelled. Returning to Applicant menu.");
                            }
                            break;
                            
                        case CHANGE_PASSWORD:
                            try {
                                System.out.println("                                            Change your password");
                                System.out.println("====================================================================================================================");
                                System.out.print("Enter current password: ");
                                String oldpass = Input.getStringInput(sc);
                                System.out.print("Enter new password: ");
                                String new_pass1 = Input.getStringInput(sc);
                                System.out.print("Enter new password again to confirm: ");
                                String new_pass2 = Input.getStringInput(sc);
                                if (!verify_password(oldpass)) {
                                    System.out.println("Current password is wrong. Password change unsuccessful.");
                                } else if (!new_pass1.equals(new_pass2)) {
                                    System.out.println("New passwords do not match.");
                                } else {
                                    change_password(new_pass2);
                                    System.out.println("Password changed successfully.");
                                }
                            } catch (Input.InputExitException e) {
                                System.out.println("Operation cancelled. Returning to Applicant menu.");
                            }
                            break;
                            
                        case EXIT:
                            System.out.println("Logged out. Returning to role menu...");
                            return;
                            
                        default:
                            System.out.println("Invalid choice. Please try again.");
                    }
                }
            } catch (Input.InputExitException e) {
                System.out.println("Operation cancelled by user. Returning to Applicant menu.");
                continue;
            } catch (InputMismatchException e) {
                System.out.println("Invalid input. Please enter a number.");
                sc.nextLine();
            }
        }
    }    

    public void to_string() {
        super.to_string();
        System.out.println("Account type: " + type);
        System.out.println("ApplicantID: " + applicantID);
    }

    enum EnquiryOption {
        GENERAL, PROJECT_RELATED, EDIT, VIEW_ALL, DELETE, RETURN;
    }

    protected void manage_enquiry(Scanner sc) {
        int choice = 0;
        do {
            try {
                System.out.println("====================================================================================================================");
                System.out.println("                                         E N Q U I R Y   M E N U");
                System.out.println("====================================================================================================================");
                menu.printEnquiryMenu();
                choice = Input.getIntInput(sc);
                System.out.println("====================================================================================================================");
                if (choice >= 1 && choice <= EnquiryOption.values().length) {
                    EnquiryOption selectedOption = EnquiryOption.values()[choice - 1];
                    switch (selectedOption) {
                        case GENERAL:
                            System.out.println("Enquiry: ");
                            String content = Input.getStringInput(sc);
                            makeEnquiry(content);
                            System.out.println("Enquiry sent!");
                            break;
                        case PROJECT_RELATED:
                            view_listings();
                            System.out.println("Enter ID of project to enquire about: ");
                            int projectId = Input.getIntInput(sc);
                            Project p = BTOsystem.searchById(BTOsystem.projects, projectId, Project::getId);
                            while (p == null) {
                                System.out.println("Invalid ID, try again: ");
                                projectId = Input.getIntInput(sc);
                                p = BTOsystem.projects.get(projectId);
                            }
                            System.out.println("Enter flat type (2-Room, 3-Room, etc.): ");
                            String flatType = Input.getStringInput(sc).toUpperCase();
                            System.out.println("Enquiry: ");
                            String project_content = Input.getStringInput(sc);
                            makeEnquiry(p, project_content, flatType);
                            System.out.println("Enquiry sent!");
                            break;
                        case EDIT:
                            System.out.println("Edit enquiry");
                            viewEditableEnquiry();
                            System.out.println("Enter ID of enquiry to edit: ");
                            int id = Input.getIntInput(sc);
                            Enquiry result = enquiries.stream()
                                    .filter(en -> en.getId() == id)
                                    .findFirst()
                                    .orElse(null);
                            if (result == null) {
                                System.out.println("No such enquiry.");
                                break;
                            } else if (result.getStaff() != null) {
                                System.out.println("Enquiry has already been replied to. Please make a new enquiry instead.");
                                break;
                            }
                            view_enquiry(result);
                            System.out.print("Enquiry: ");
                            String userInput = Input.getStringInput(sc);
                            editEnquiry(result, userInput);
                            System.out.println("Enquiry edited!");
                            break;
                        case VIEW_ALL:
                            System.out.println("                                              All Enquiries");
                            System.out.println("====================================================================================================================");
                            view_all_enquiry_for_user();
                            System.out.println("Select enquiry to view (-1 to cancel)");
                            int en_id = Input.getIntInput(sc);
                            if (en_id == -1) {
                                break;
                            }
                            Enquiry en = BTOsystem.searchById(enquiries, en_id, Enquiry::getId);
                            if (en == null) {
                                System.err.println("Invalid ID");
                                break;
                            }
                            view_enquiry(en);
                            break;
                        case DELETE:
                            System.out.println("                                           Delete enquiry");
                            System.out.println("================================================================================================================");
                            view_all_enquiry_for_user();
                            System.out.print("Enter ID of enquiry to delete: ");
                            int del_id = Input.getIntInput(sc);
                            deleteEnquiry(del_id);
                            System.out.println("Enquiry deleted!");
                            break;
                        case RETURN:
                            System.out.println("Returning to applicant menu...");
                            break;
                        default:
                            System.out.println("Invalid choice. Please try again.");
                    }
                }
            } catch (Input.InputExitException e) {
                System.out.println("User requested exit/back in enquiry menu. Returning to applicant menu.");
                break;
            } catch (InputMismatchException e) {
                System.out.println("Invalid input. Please enter a number.");
                sc.nextLine();
            }
        } while (choice != 6);
    }

    protected boolean getEligibility(String flatType) {
        if (get_maritalstatus().equalsIgnoreCase("SINGLE") && get_age() >= 35 && flatType.equalsIgnoreCase("2-Room")) {
            return true;
        } else if (get_maritalstatus().equalsIgnoreCase("MARRIED") && get_age() >= 21) {
            return true;
        }
        return false;
    }

    protected void view_listings() {
        System.out.println("\n===================================================================================================================");
        System.out.println("                                                  ALL PROJECTS");
        System.out.println("===================================================================================================================");
        System.out.printf("%-5s %-20s %-15s %-15s %-10s %-15s %-15s %-10s %n", "ID", "Project Name", "Neighbourhood", "Flat Types", "Price", "Open Date", "Close Date", "Eligibilty");
        System.err.println("-------------------------------------------------------------------------------------------------------------------");

        List<Project> list = BTOsystem.projects;
        for (Project p : list) {
            if (p.isVisible()) {
                System.out.print(viewProjectsApplicant(p));
            }
        }
        System.err.println("-------------------------------------------------------------------------------------------------------------------");
    }

    protected int view_eligible_listings() {
        System.out.println("                                                  ELIGIBLE PROJECTS");
        System.out.println("====================================================================================================================");
        System.out.printf("%-5s %-20s %-15s %-15s %-10s %-15s %-15s %-10s %n", "ID", "Project Name", "Neighbourhood", "Flat Types", "Price", "Open Date", "Close Date", "Eligibilty");
        System.err.println("--------------------------------------------------------------------------------------------------------------------");
        List<Project> list = BTOsystem.projects;
        int count = 0;
        for (Project p : list) {
            if (p.isVisible()) {
                String str = viewEligibleProjectsApplicant(p);
                if (!str.isBlank()) {
                    count++;
                }
                System.out.print(str);
            }
        }
        System.err.println("--------------------------------------------------------------------------------------------------------------------");
        return count;
    }

    protected String viewEligibleProjectsApplicant(Project p) {
        StringBuilder sb = new StringBuilder();
        List<String[]> eflatType = new ArrayList<>();
        for (String ft : p.getFlatTypes()) {
            if (getEligibility(ft)) {
                eflatType.add(new String[]{ft, String.valueOf(p.getFlatTypes().indexOf(ft))});
            }
        }
        if (!eflatType.isEmpty()) {
            sb.append(String.format("%-5s %-20s %-15s %-15s %-10s %-15s %-15s %-10s %n",
                    p.getId(),
                    p.getProjectName(),
                    p.getneighbourhood(),
                    (eflatType.size() > 0 && getEligibility(p.getFlatTypes().get(0)))
                            ? eflatType.get(0)[0] + ": " + (p.getTotalUnits().get(Integer.parseInt(eflatType.get(0)[1]))
                            - p.getAvailableUnits().get(Integer.parseInt(eflatType.get(0)[1]))) + "/" + p.getTotalUnits().get(Integer.parseInt(eflatType.get(0)[1]))
                            : "",
                    p.getFlatTypes().size() > 0 ? p.getFlatPrice(eflatType.get(0)[0]) : 0,
                    p.getOpenDate(),
                    p.getCloseDate(),
                    "Eligible"
            ));
        }
        for (int i = 1; i < eflatType.size(); i++) {
            sb.append(String.format("%-5s %-20s %-15s %-15s %-10s %-15s %-15s %-10s %n",
                    "", "", "",
                    eflatType.get(i)[0] + ": " + (p.getTotalUnits().get(Integer.parseInt(eflatType.get(i)[1]))
                            - p.getAvailableUnits().get(Integer.parseInt(eflatType.get(i)[1]))) + "/" + p.getTotalUnits().get(Integer.parseInt(eflatType.get(i)[1])),
                    p.getFlatTypes().size() > 0 ? p.getFlatPrice(eflatType.get(i)[0]) : 0,
                    "", "", "Eligible"));
        }
        sb.append("\n");
        return sb.toString();
    }

    protected String viewProjectsApplicant(Project p) {
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("%-5s %-20s %-15s %-15s %-10s %-15s %-15s %-10s %n",
                p.getId(),
                p.getProjectName(),
                p.getneighbourhood(),
                (p.getFlatTypes().size() > 0)
                        ? p.getFlatTypes().get(0) + ": " + (p.getTotalUnits().get(0) - p.getAvailableUnits().get(0)) + "/" + p.getTotalUnits().get(0)
                        : "",
                p.getFlatTypes().size() > 0 ? p.getFlatPrice(p.getFlatTypes().get(0)) : 0,
                p.getOpenDate(),
                p.getCloseDate(),
                getEligibility(p.getFlatTypes().get(0)) ? "Eligible" : "Not Eligible"
        ));
        for (int i = 1; i < p.getFlatTypes().size(); i++) {
            sb.append(String.format("%-5s %-20s %-15s %-15s %-10s %-15s %-15s %-10s %n",
                    "", "", "",
                    p.getFlatTypes().get(i) + ": " + (p.getTotalUnits().get(i) - p.getAvailableUnits().get(i)) + "/" + p.getTotalUnits().get(i),
                    p.getFlatTypes().size() > 0 ? p.getFlatPrice(p.getFlatTypes().get(i)) : 0,
                    "", "", getEligibility(p.getFlatTypes().get(i)) ? "Eligible" : "Not Eligible"));
        }
        sb.append("\n");
        return sb.toString();
    }

    protected void makeEnquiry(String content) {
        Enquiry en = new Enquiry(this, content);
        enquiries.add(en);
        BTOsystem.enquiries.add(en);
    }

    protected void makeEnquiry(Project project, String content, String flatType) {
        Enquiry en = new Enquiry(this, content);
        en.setProject(project);
        en.setflatType(flatType);
        enquiries.add(en);
        BTOsystem.enquiries.add(en);
        project.addEnquiry(en);
    }

    protected void view_all_enquiry_for_user() {
        System.out.printf("%-5s %-20s %-30s %-30s %-15s %-20s%n",
                "ID", "Project", "Enquiry", "Reply", "Status", "Replied by");
        System.out.println("====================================================================================================================");
        for (Enquiry enquiry : enquiries) {
            System.out.printf("%-5d %-20s %-30s %-30s %-15s %-20s%n",
                    enquiry.getId(),
                    enquiry.getProject() != null ? enquiry.getProject().getProjectName() : "General Enquiry",
                    truncateText(enquiry.getEnquiry(), 30),
                    truncateText(enquiry.getResponse(), 30),
                    enquiry.getResponse().isEmpty() ? "Pending" : "Answered",
                    enquiry.getStaff() != null ? enquiry.getStaff().get_firstname() : "");
            if (enquiry.getflatType() != null && !enquiry.getflatType().isEmpty()) {
                System.out.printf("%-5s %-20s %-30s%n", "", "", "Flat type: " + enquiry.getflatType());
            }
        }
    }

    protected void view_enquiry(Enquiry en) {
        en.display();
    }

    protected void viewEditableEnquiry() {
        System.out.printf("%-5s %-20s %-30s %-15s%n",
                "ID", "Project", "Enquiry", "Status");
        System.out.println("==================================================================================================================");
        for (Enquiry enquiry : enquiries) {
            if (!enquiry.getResponse().isBlank()) {
                continue;
            }
            System.out.printf("%-5d %-20s %-30s %-15s%n",
                    enquiry.getId(),
                    enquiry.getProject() != null ? enquiry.getProject().getProjectName() : "General Enquiry",
                    truncateText(enquiry.getEnquiry(), 30),
                    "Pending");
            if (enquiry.getflatType() != null && !enquiry.getflatType().isEmpty()) {
                System.out.printf("%-5s %-20s %-30s%n", "", "", "Flat type: " + enquiry.getflatType());
            }
        }
    }

    protected void editEnquiry(Enquiry en, String content) {
        en.setEnquiry(content);
    }

    protected void deleteEnquiry(int id) {
        Enquiry toRemove = null;
        for (Enquiry en : enquiries) {
            if (en.getId() == id) {
                toRemove = en;
                break;
            }
        }
        if (toRemove != null) {
            view_enquiry(toRemove);
            enquiries.remove(toRemove);
            System.out.println("Deleted enquiry: " + toRemove.getEnquiry());
            System.out.println("Deleted response: " + toRemove.getResponse());
        } else {
            System.out.println("No enquiry found with ID: " + id);
        }
    }
}
