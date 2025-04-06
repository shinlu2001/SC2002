package SC2002.Project;

import java.util.*;

public class Applicant extends User implements Input {
    protected static int nextId = -1;
    private int applicantID;
    protected BTOapplication application=null;
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
                System.out.println("============================================");
                System.out.println("         A P P L I C A N T   M E N U");
                System.out.println("============================================");
                menu.printApplicantMenu();
                
                choice = Input.getIntInput(sc);
                System.out.println("============================================");
                if (choice >= 1 && choice <= ApplicantOption.values().length) {
                    ApplicantOption selectedOption = ApplicantOption.values()[choice - 1];
                    switch (selectedOption) {
                        case APPLY:
                            if (application != null) {
                                System.out.println("You already have an active application. You may not create a new one.");
                                System.out.println("============================================");
                                // sc.nextLine();
                            } else {
                                System.out.println("Apply for a project");
                                int count = view_eligible_listings();
                                if (count==0) {
                                    System.out.println("You are not eligible to apply for any project.");
                                    break;
                                }
                                System.out.println("Enter ProjectID: ");
                                int id = Input.getIntInput(sc);
                                // sc.nextLine();
                                Project p = BTOsystem.searchProjectById(id);
                                if (p==null || !p.isVisible()) {
                                    System.out.println("No such project.");
                                } else {
                                    System.out.println("Enter room type (2-Room, 3-Room, etc): ");
                                    String roomtype = Input.getStringInput(sc);
                                    
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
                            break;
                        case VIEW_APPLICATION:
                            if (application == null) {
                                System.out.println("You have no active application. Please create a new application.");
                                System.out.println("============================================");
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
                                System.out.println("============================================");
                            } else {
                                System.out.println("Nothing to withdraw.");
                            }
                            break;
                        case ENQUIRY:
                            manage_enquiry(sc);
                            System.out.println("============================================");
                            break;
                        case ACCOUNT:
                            System.out.println("             Account details");
                            System.out.println("============================================");
                            to_string();
                            System.out.println("============================================");
                            break;
                        case CHANGE_PASSWORD:
                            System.out.println("          Change your password");
                            System.out.println("============================================");
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
                            System.out.println("============================================");

                            break;
                        case EXIT:
                            System.out.println("Logged out. Returning to main menu...");
                            System.out.println("============================================");
                            break;
                        default:
                            System.out.println("Invalid choice. Please try again.");
                    }
                // sc.nextLine();
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

    private void manage_enquiry(Scanner sc) {
        int choice=0;
        do {
            try {
                System.out.println("============================================");
                System.out.println("         E N Q U I R Y   M E N U");
                System.out.println("============================================");
                menu.printEnquiryMenu();
                choice = Input.getIntInput(sc);
                // scanner.nextLine();
                System.out.println("============================================");
                if (choice >= 1 && choice <= EnquiryOption.values().length) {
                    EnquiryOption selectedOption = EnquiryOption.values()[choice - 1];
                    switch (selectedOption) {
                        case GENERAL:
                            System.out.println("Enquiry: ");
                            String content = Input.getStringInput(sc);
                            makeEnquiry(content);
                            System.out.println("Enquiry sent!");
                            System.out.println("============================================");
                            break;
                        case PROJECT_RELATED:
                            view_listings();
                            System.out.println("Enter ID of project to enquire about: ");
                            int projectId = Input.getIntInput(sc);
                            Project p = BTOsystem.projects.get(projectId);
                            while (p==null) {
                                System.out.println("Invalid ID, try again: ");
                                projectId = Input.getIntInput(sc);
                                // scanner.nextLine();
                                p = BTOsystem.projects.get(projectId);
                            } 
                            sc.nextLine();
                            System.out.println("Enter flat type (2-Room, 3-Room, etc.): ");
                            String flatType = Input.getStringInput(sc);
                            System.out.println("Enquiry: ");
                            String project_content = Input.getStringInput(sc);
                            makeEnquiry(p, project_content, flatType);
                            System.out.println("Enquiry sent!");
                            System.out.println("============================================");
                            break;
                        case EDIT:
                            System.out.println("Edit enquiry");
                            viewEditableEnquiry();
                            System.out.println("Enter ID of enquiry to edit: ");
                            int id = Input.getIntInput(sc);
                            // cannot edit enquiries that have been replied to 
                            Enquiry result = enquiries.stream()
                                .filter(en -> en.getEnId() == id)
                                .findFirst()
                                .orElse(null);
                            if (result.getStaff()!=null) {
                                System.out.println("Enquiry has already been replied to. Please make a new enquiry instead.");
                                System.out.println("============================================");
                                break;
                            } 
                            // scanner.nextLine();
                            System.out.print("Enquiry: ");
                            String userInput = Input.getStringInput(sc);
                            // maybe add confirmation?
                            editEnquiry(id, userInput);
                            System.out.println("Enquiry edited!");
                            System.out.println("============================================");
                            break;
                        case VIEW_ALL:
                            System.out.println("All enquiries");
                            System.out.println("============================================");
                            view_all_enquiry_for_user();
                            break;
                        case DELETE:
                            System.out.println("Delete enquiry");
                            System.out.println("============================================");
                            view_all_enquiry_for_user();
                            System.out.print("Enter ID of enquiry to delete: ");
                            // add confirmation before deleting
                            int del_id = Input.getIntInput(sc);
                            // scanner.nextLine();
                            deleteEnquiry(del_id);
                            System.out.println("Enquiry deleted!");
                            System.out.println("============================================");
                            break;
                        case RETURN:
                            System.out.println("Returning to applicant menu...");
                            break;
                        default:
                            System.out.println("Invalid choice. Please try again.");
                    }
                }
            sc.nextLine();
            } catch (InputMismatchException e) {
                System.out.println("Invalid input. Please enter a number.");
                sc.nextLine(); 
            }
        } while (choice != 6);
    }

    // check eligibility of user to apply for flat
    protected boolean getEligibility(String flatType) {
        if (get_maritalstatus().equals("SINGLE") && get_age()>=35 && flatType.equals("2-Room")) {
            return true;
        } else if (get_maritalstatus().equals("MARRIED") && get_age()>=21) {
            return true;
        }
        return false;
    }

    private void view_listings() {
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
        System.out.println("\n================================================================================================================");
        System.out.println("                                                  ELIGIBLE PROJECTS");
        System.out.println("================================================================================================================");
        System.out.printf("%-5s %-20s %-15s %-15s %-10s %-15s %-15s %-10s %n", "ID","Project Name", "Neighbourhood", "Flat Types", "Price","Open Date", "Close Date", "Eligibilty");
        System.err.println("----------------------------------------------------------------------------------------------------------------");
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
        System.err.println("----------------------------------------------------------------------------------------------------------------");
        return count;
    }

    private String viewEligibleProjectsApplicant(Project p) {
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
                p.getProjectID(),
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

    private String viewProjectsApplicant(Project p) {
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("%-5s %-20s %-15s %-15s %-10s %-15s %-15s %-10s %n",
            p.getProjectID(),
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

    private void makeEnquiry(String content) {
        Enquiry en = new Enquiry(this, content);
        enquiries.add(en);
        // BTOsystem.getEnquiries().add(en); // add enquiry to global enquiry list to be accessed by staff
        BTOsystem.enquiries.add(en); // add enquiry to global enquiry list to be accessed by staff
    }
    //to be revised, enquiries tagged to a project
    private void makeEnquiry(Project project, String content, String flatType) {
        Enquiry en = new Enquiry(this, content);
        en.setProject(project);
        en.setflatType(flatType);
        enquiries.add(en);
        BTOsystem.enquiries.add(en);
        project.addEnquiry(en);  // Ensure enquiry is tied to the project
    }
    

    private void view_enquiry(Enquiry en) {
        System.out.println("Enquiry: "+ en.getEnquiry());
        System.out.println("Project: "+ (en.getProject()!=null?en.getProject().getProjectName():null));
        System.out.println("Flat Type: "+en.getflatType());
        if (en.getStaff()==null) {
            System.out.println("No reply to your enquiry yet.");
        } else {
            System.out.println("Response: "+en.getResponse());

        }
    }
    private void view_all_enquiry_for_user() {
        for (Enquiry en : enquiries) {
            System.out.println("#"+en.getEnId());
            view_enquiry(en);
            System.out.println("--------------------------------");
        }
    }
    private void viewEditableEnquiry() {
        for (Enquiry en : enquiries) {
            if (en.getStaff()==null){
                System.out.println("#"+en.getEnId());
                view_enquiry(en);
                System.out.println("--------------------------------");
            }
            
        }
    }
    private void editEnquiry(int id, String content) { // can only edit when no response from staff yett
        Iterator<Enquiry> iterator = enquiries.iterator();
        Enquiry en = iterator.next();
        while (iterator.hasNext()) {
            en = iterator.next();
            if (en.getEnId() == id) {
                en.setEnquiry(content);
                break; 
            }
        }
    }
    private void deleteEnquiry(int id) {
        Enquiry removedElement = null;  
        Iterator<Enquiry> iterator = enquiries.iterator();
        Enquiry en = iterator.next();
        while (iterator.hasNext()) {
            en = iterator.next();
            if (en.getEnId() == id) {
                removedElement = en;
                iterator.remove();  
                break;
            }
        }
        System.out.println("Deleted enquiry: " + removedElement.getEnquiry());
        System.out.println("Deleted response: " + removedElement.getResponse());
        removedElement = null;
    }
}

// for filtering enquiries/projects/application
// List<Enquiry> matching = enquiries.stream()
//     .filter(en -> en.getStatus().equals("Pending"))
//     .collect(Collectors.toList());

// System.out.println("Matching Enquiries: " + matching);