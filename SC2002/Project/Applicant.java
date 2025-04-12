package SC2002.Project;

import java.util.*;

public class Applicant extends User implements Input {
    protected static int nextId = -1;
    private int applicantID;
    protected BTOapplication application=null;
    protected List<BTOapplication> applicationHistory=new ArrayList<>();
    private String type="APPLICANT";
    protected List<Enquiry> enquiries = new ArrayList<>();

    public Applicant(String nric, String firstname, String lastname, String marital_status, int age) {
        super(nric, firstname, lastname, marital_status, age);
        applicantID = ++nextId;
    }
    enum ApplicantOption {
        APPLY, VIEW_APPLICATION, VIEW_ELIGIBLE, VIEW_LISTINGS, WITHDRAW, ENQUIRY, ACCOUNT, CHANGE_PASSWORD, EXIT;
    }
    public void start_menu(Scanner sc) {
        System.out.println("Welcome to HDB BTO Management System, " + this.get_firstname() + "!");
        int choice=0;
        do {
            try {
                System.out.println("====================================================================================================================");
                System.out.println("                                          A P P L I C A N T   M E N U");
                System.out.println("====================================================================================================================");
                menu.printApplicantMenu();
                
                choice = Input.getIntInput(sc);
                System.out.println("====================================================================================================================");
                if (choice >= 1 && choice <= ApplicantOption.values().length) {
                    ApplicantOption selectedOption = ApplicantOption.values()[choice - 1];
                    switch (selectedOption) {
                        case APPLY:
                            if (application == null || application.getStatus().equals("WITHDRAWN")) {
                                if (application!=null) {
                                    applicationHistory.add(application);
                                }
                                System.out.println("Apply for a project");
                                int count = view_eligible_listings();
                                if (count==0) {
                                    System.out.println("You are not eligible to apply for any project.");
                                    break;
                                }
                                System.out.println("Enter ProjectID: ");
                                int id = Input.getIntInput(sc);
                                // sc.nextLine();
                                Project p = BTOsystem.searchById(BTOsystem.projects, id, Project::getId);
                                if (p==null || !p.isVisible()) {
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
                            } else{
                                System.out.println("You already have an active application. You may not create a new one.");
                                
                            }
                            break;
                        case VIEW_APPLICATION:
                            if (application == null) {
                                System.out.println("You have no active application. Please create a new application.");
                            } else {
                                application.get_details();
                                if (application.getStatus().equalsIgnoreCase("Successful")) {
                                    System.out.println("Congrats! Your application is successful!");
                                    System.out.println("Enter 1 to book a flat (any other key to exit): ");
                                    int book = Input.getIntInput(sc);
                                    // sc.nextLine();
                                    if (book==1) {
                                        System.out.println("Your request to book a flat has been submitted.");
                                        System.out.println("Our friendly HDB officer will assist you in the booking of a flat");
                                        application.requestBooking();
                                        // on officer side, book a flat from the list of flats available, set application status to booked
                                    }
                                }
                            }
                            break;
                        case VIEW_ELIGIBLE:
                            int count = view_eligible_listings();
                            if (count==0) {
                                System.out.println("You are not eligible to apply for any project.");
                            }
                            break;
                        case VIEW_LISTINGS:
                            view_listings();
                            break;
                        case WITHDRAW:
                            System.out.println("Withdraw Request");
                            if (application != null) {
                                application.get_details();
                                System.out.println("(Enter to continue) ");
                                sc.nextLine();
                                System.out.println("Enter NRIC to confirm withdrawal: ");
                                String confirm = Input.getStringInput(sc);
                                if (confirm.equals(get_nric())) {
                                    application.withdraw();
                                    System.out.println("Withdrawal request has been submitted.");
                                } else {
                                    System.out.println("Wrong NRIC, Withdrawal Unsuccessful.");
                                }
                            } else {
                                System.out.println("Nothing to withdraw.");
                            }
                            break;
                        case ENQUIRY:
                            manage_enquiry(sc);
                            break;
                        case ACCOUNT:
                            System.out.println("                                               Account details");
                            System.out.println("====================================================================================================================");
                            to_string();
                            break;
                        case CHANGE_PASSWORD:
                            System.out.println("                                            Change your password");
                            System.out.println("====================================================================================================================");
                            // sc.nextLine();
                            System.out.print("Enter current password: ");
                            String oldpass = Input.getStringInput(sc);
                            System.out.print("Enter new password: ");
                            String new_pass1 = Input.getStringInput(sc);
                            System.out.print("Enter new password again to confirm: ");
                            String new_pass2 = Input.getStringInput(sc);
                            
                            if (verify_password(oldpass)!=true) {
                                System.out.println("Current password is wrong. Password change unsuccessful.");
                            } else if (new_pass1.equals(new_pass2)!=true) {
                                System.out.println("New passwords do not match.");
                            } else {
                                change_password(new_pass2);
                                System.out.println("Password changed successfully.");
                            }

                            break;
                        case EXIT:
                            System.out.println("Logged out. Returning to main menu...");
                            System.out.println("================================================================================================================");
                            break;
                        default:
                            System.out.println("Invalid choice. Please try again.");
                    }
                }
            } catch (InputMismatchException e) {
                System.out.println("Invalid input. Please enter a number.");
                sc.nextLine(); 
            }
        } while (choice != 9);
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
        int choice=0;
        do {
            try {
                System.out.println("====================================================================================================================");
                System.out.println("                                         E N Q U I R Y   M E N U");
                System.out.println("====================================================================================================================");
                menu.printEnquiryMenu();
                choice = Input.getIntInput(sc);
                // scanner.nextLine();
                System.out.println("====================================================================================================================");
                if (choice >= 1 && choice <= EnquiryOption.values().length) {
                    EnquiryOption selectedOption = EnquiryOption.values()[choice - 1];
                    switch (selectedOption) {
                        case GENERAL:
                            System.out.println("Enquiry: ");
                            String content = Input.getStringInput(sc);
                            makeEnquiry(content);
                            System.out.println("Enquiry sent!");
                            // System.out.println("============================================");
                            break;
                        case PROJECT_RELATED:
                            view_listings();
                            System.out.println("Enter ID of project to enquire about: ");
                            int projectId = Input.getIntInput(sc);
                            Project p = BTOsystem.searchById(BTOsystem.projects, projectId, Project::getId);
                            while (p==null) {
                                System.out.println("Invalid ID, try again: ");
                                projectId = Input.getIntInput(sc);
                                // scanner.nextLine();
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
                            // cannot edit enquiries that have been replied to 
                            Enquiry result = enquiries.stream()
                                .filter(en -> en.getId() == id)
                                .findFirst()
                                .orElse(null);
                            if (result==null) {
                                System.out.println("No such enquiry.");
                                break;
                            } else if (result.getStaff()!=null) {
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
                            if (en_id==-1) {
                                break;
                            }
                            Enquiry en = BTOsystem.searchById(enquiries, en_id, Enquiry::getId);
                            if (en==null) {
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
                            // add confirmation before deleting
                            int del_id = Input.getIntInput(sc);
                            // scanner.nextLine();
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
            // sc.nextLine();
            } catch (InputMismatchException e) {
                System.out.println("Invalid input. Please enter a number.");
                sc.nextLine(); 
            }
        } while (choice != 6);
    }

    // check eligibility of user to apply for flat
    protected boolean getEligibility(String flatType) {
        if (get_maritalstatus().equalsIgnoreCase("SINGLE") && get_age()>=35 && flatType.equalsIgnoreCase("2-Room")) {
            return true;
        } else if (get_maritalstatus().equalsIgnoreCase("MARRIED") && get_age()>=21) {
            return true;
        }
        return false;
    }

    protected void view_listings() {
        System.out.println("\n===================================================================================================================");
        System.out.println("                                                  ALL PROJECTS");
        System.out.println("===================================================================================================================");
        System.out.printf("%-5s %-20s %-15s %-15s %-10s %-15s %-15s %-10s %n", "ID","Project Name", "Neighbourhood", "Flat Types", "Price","Open Date", "Close Date", "Eligibilty");
        System.err.println("-------------------------------------------------------------------------------------------------------------------");
        
        List<Project> list = BTOsystem.projects;
        
        for (Project p : list) {
            if (p.isVisible()) {
                // there are details we want to keep hidden from an applicant e.g. manager name, visibility, etc. so cannot just use toString()
                System.out.print(viewProjectsApplicant(p));
                
            }
        }
        System.err.println("-------------------------------------------------------------------------------------------------------------------");
    }

    protected int view_eligible_listings() {
        // System.out.println("\n================================================================================================================");
        System.out.println("                                                  ELIGIBLE PROJECTS");
        System.out.println("====================================================================================================================");
        System.out.printf("%-5s %-20s %-15s %-15s %-10s %-15s %-15s %-10s %n", "ID","Project Name", "Neighbourhood", "Flat Types", "Price","Open Date", "Close Date", "Eligibilty");
        System.err.println("--------------------------------------------------------------------------------------------------------------------");
        List<Project> list = BTOsystem.projects;
        int count = 0;
        for (Project p : list) {
            if (p.isVisible()) {
                String str = viewEligibleProjectsApplicant(p);
                if (!str.isBlank()){
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
        // get all the flat-types applicants are eligible for
        List<String[]> eflatType = new ArrayList<>(); // max 10 flat-types per project

        for (String ft:p.getFlatTypes()) {
            if (getEligibility(ft)) {
                eflatType.add(new String[]{ft, String.valueOf(p.getFlatTypes().indexOf(ft))});
            }
        }
        
        if (!eflatType.isEmpty()) {
            sb.append(String.format("%-5s %-20s %-15s %-15s %-10s %-15s %-15s %-10s %n",
                p.getId(),
                p.getProjectName(),
                p.getneighbourhood(),
                (eflatType.size() > 0 && getEligibility(p.getFlatTypes().get(0))) ? 
                    eflatType.get(0)[0] + ": " + (p.getTotalUnits().get(Integer.parseInt(eflatType.get(0)[1])) - p.getAvailableUnits().get(Integer.parseInt(eflatType.get(0)[1]))) + "/" + p.getTotalUnits().get(Integer.parseInt(eflatType.get(0)[1])) : "",
                p.getFlatTypes().size() > 0 ? p.getFlatPrice(eflatType.get(0)[0]) : 0,
                p.getOpenDate(), 
                p.getCloseDate(), 
                "Eligible"
            ));
        }

        // additional lines for remaining flat types
        for (int i = 1; i < eflatType.size(); i++) {
        sb.append(String.format("%-5s %-20s %-15s %-15s %-10s %-15s %-15s %-10s %n",
            "", "", "",  // empty project name and neighbourhood
            eflatType.get(i)[0] + ": " + (p.getTotalUnits().get(Integer.parseInt(eflatType.get(i)[1])) - p.getAvailableUnits().get(Integer.parseInt(eflatType.get(i)[1]))) + "/" + p.getTotalUnits().get(Integer.parseInt(eflatType.get(i)[1])),
            p.getFlatTypes().size() > 0 ? p.getFlatPrice(eflatType.get(i)[0]) : 0,
            "", "", "Eligible"));  // empty other fields  
        }

        // add blank line between projects
        sb.append("\n");

        return sb.toString();
    }

    protected String viewProjectsApplicant(Project p) {
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("%-5s %-20s %-15s %-15s %-10s %-15s %-15s %-10s %n",
            p.getId(),
            p.getProjectName(),
            p.getneighbourhood(),
            (p.getFlatTypes().size() > 0) ? 
                p.getFlatTypes().get(0) + ": " + (p.getTotalUnits().get(0) - p.getAvailableUnits().get(0)) + "/" + p.getTotalUnits().get(0) : "",
            p.getFlatTypes().size() > 0 ? p.getFlatPrice(p.getFlatTypes().get(0)) : 0,
            p.getOpenDate(), 
            p.getCloseDate(), 
            getEligibility(p.getFlatTypes().get(0)) ? "Eligible" : "Not Eligible" 
        ));

    // Additional lines for remaining flat types
    for (int i = 1; i < p.getFlatTypes().size(); i++) {
        sb.append(String.format("%-5s %-20s %-15s %-15s %-10s %-15s %-15s %-10s %n",
            "", "", "",  // Empty project name and neighbourhood
            p.getFlatTypes().get(i) + ": " + (p.getTotalUnits().get(i) - p.getAvailableUnits().get(i)) + "/" + p.getTotalUnits().get(i),
            p.getFlatTypes().size() > 0 ? p.getFlatPrice(p.getFlatTypes().get(i)) : 0,
            "", "", getEligibility(p.getFlatTypes().get(i)) ? "Eligible" : "Not Eligible"));  // Empty other fields
    }

    // Add blank line between projects
    sb.append("\n");

    return sb.toString();
    }

    protected void makeEnquiry(String content) {
        Enquiry en = new Enquiry(this, content);
        enquiries.add(en);
        BTOsystem.enquiries.add(en); // add enquiry to global enquiry list to be accessed by staff
    }
    //to be revised, enquiries tagged to a project
    protected void makeEnquiry(Project project, String content, String flatType) {
        Enquiry en = new Enquiry(this, content);
        en.setProject(project);
        en.setflatType(flatType);
        enquiries.add(en);
        BTOsystem.enquiries.add(en);
        project.addEnquiry(en);  // Ensure enquiry is tied to the project
    }
    
    protected void view_all_enquiry_for_user() {
        System.out.printf("%-5s %-20s %-30s %-30s %-15s %-20s%n", 
            "ID", "Project", "Enquiry", "Reply", "Status", "Replied by");
            System.out.println("====================================================================================================================");
    
    for (Enquiry enquiry : enquiries) {
        // User creator = enquiry.getCreatedByUser();
        // String creatorName = creator.get_firstname() + " " + creator.get_lastname();
        
        System.out.printf("%-5d %-20s %-30s %-30s %-15s %-20s%n",
                enquiry.getId(),
                enquiry.getProject() != null ? enquiry.getProject().getProjectName() : "General Enquiry",
                truncateText(enquiry.getEnquiry(), 30),
                truncateText(enquiry.getResponse(), 30),
                enquiry.getResponse().isEmpty() ? "Pending" : "Answered",
                enquiry.getStaff()!=null? enquiry.getStaff().get_firstname():"");
        
        // If there's a flat type specified
        if (enquiry.getflatType() != null && !enquiry.getflatType().isEmpty()) {
            System.out.printf("%-5s %-20s %-30s%n", "","","Flat type: "+ enquiry.getflatType());
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
        if (!(enquiry.getResponse().isBlank())) {
            continue;
        }
        
        System.out.printf("%-5d %-20s %-30s %-15s%n",
                enquiry.getId(),
                enquiry.getProject() != null ? enquiry.getProject().getProjectName() : "General Enquiry",
                truncateText(enquiry.getEnquiry(), 30),
                "Pending");
        
        // If there's a flat type specified
        if (enquiry.getflatType() != null && !enquiry.getflatType().isEmpty()) {
            System.out.printf("%-5s %-20s %-30s%n", "","","Flat type: "+ enquiry.getflatType());
        }
    }
    }
    protected void editEnquiry(Enquiry en, String content) { // can only edit when no response from staff yett
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

// for filtering enquiries/projects/application
// List<Enquiry> matching = enquiries.stream()
//     .filter(en -> en.getStatus().equals("Pending"))
//     .collect(Collectors.toList());

// System.out.println("Matching Enquiries: " + matching);