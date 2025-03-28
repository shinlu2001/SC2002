package SC2002.Project;

import java.util.*;

public class Applicant extends User {
    protected static int nextId = -1;
    private int applicantID;
    protected BTOapplication application=null;
    private String type="APPLICANT";
    protected List<Enquiry> enquiries = new ArrayList<>();

    public Applicant(String nric, String firstname, String lastname, String marital_status, int age) {
        super(nric, firstname, lastname, marital_status, age);
        applicantID = ++nextId;
    }

    public void start_menu(Scanner scanner) {
        System.out.println("Welcome to HDB BTO Management System, " + this.get_firstname() + "!");
        int choice=0;
        do {
            try {
                System.out.println("============================================");
                System.out.println("         A P P L I C A N T   M E N U");
                System.out.println("============================================");
                menu.printApplicantMenu();
                
                choice = scanner.nextInt();
                System.out.println("============================================");
                switch (choice) {
                    case 1:
                        if (application != null) {
                            System.out.println("You already have an active application. You may not create a new one.");
                            System.out.println("============================================");
                        } else {
                            // view current application 
                            // application.getDetails;
                            // application.registerForProject(project); //helpz - yh
                            System.out.println("============================================");
                        }
                        break;
                    case 2:
                        if (application == null) {
                            System.out.println("You have no active application. Please create a new application.");
                            System.out.println("============================================");
                        } else {
                            application.get_details();
                            System.out.println("============================================");
                        }
                        break;
                    case 3:
                        view_listings();
                        scanner.nextLine();
                        break;
                    case 4:
                        System.out.println("Withdraw application");
                        System.out.println("============================================");
                        break;
                    case 5:
                        manage_enquiry(scanner);
                        scanner.nextLine();
                        System.out.println("============================================");
                        break;
                    case 6:
                        System.out.println("             Account details");
                        System.out.println("============================================");
                        to_string();
                        System.out.println("============================================");
                        scanner.nextLine();
                        break;
                    case 7:
                        System.out.println("          Change your password");
                        System.out.println("============================================");
                        scanner.nextLine();
                        System.out.print("Enter current password: ");
                        String oldpass = scanner.nextLine();
                        System.out.print("Enter new password: ");
                        String new_pass1 = scanner.nextLine();
                        System.out.print("Enter new password again to confirm: ");
                        String new_pass2 = scanner.nextLine();
                        // scanner.nextLine();
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
                    case 8: //apply to become officer
                        System.out.println("Application to become a HDB Officer");
                        System.out.println("============================================");
                        
                        break;
                    case 9:
                        System.out.println("Logged out. Returning to main menu...");
                        System.out.println("============================================");
                        break;
                    default:
                        System.out.println("Invalid choice. Please try again.");
                }
            } catch (InputMismatchException e) {
                System.out.println("Invalid input. Please enter a number.");
                scanner.next(); 
            }
        } while (choice != 9);
    }

    public void to_string() {
        super.to_string();
        System.out.println("Account type: " + type);
        System.out.println("ApplicantID: " + applicantID);
    }

    public void manage_enquiry(Scanner scanner) {
        int choice=0;
        // System.out.println("---Enquiry menu---");
        do {
            try {
                System.out.println("============================================");
                System.out.println("         E N Q U I R Y   M E N U");
                System.out.println("============================================");
                menu.printEnquiryMenu();
                choice = scanner.nextInt();
                System.out.println("============================================");
                switch (choice) {
                    case 1:
                        scanner.nextLine();
                        System.out.println("Enquiry: ");
                        String content = scanner.nextLine();
                        makeEnquiry(content);
                        System.out.println("Enquiry sent!");
                        System.out.println("============================================");
                        break;
                    case 2:
                        view_listings();
                        System.out.println("Enter ID of project to enquire about: ");
                        int projectId = scanner.nextInt();
                        // Project p = BTOsystem.getProjects().get(projectId);
                        Project p = BTOsystem.projects.get(projectId);
                        while (p==null) {
                            System.out.println("Invalid ID, try again: ");
                            projectId = scanner.nextInt();
                            // p = BTOsystem.getProjects().get(projectId);
                            p = BTOsystem.projects.get(projectId);
                        } 
                        scanner.nextLine();
                        System.out.println("Enter flat type (2-Room, 3-Room, etc.): ");
                        String flatType = scanner.nextLine();
                        System.out.println("Enquiry: ");
                        String project_content = scanner.nextLine();
                        makeEnquiry(p, project_content, flatType);
                        System.out.println("Enquiry sent!");
                        System.out.println("============================================");
                        break;
                    case 3:
                        System.out.println("Edit enquiry");
                        viewEditableEnquiry();
                        System.out.println("Enter ID of enquiry to edit: ");
                        int id = scanner.nextInt();
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
                        scanner.nextLine();
                        System.out.print("Enquiry: ");
                        String userInput = scanner.nextLine();
                        // maybe add confirmation?
                        editEnquiry(id, userInput);
                        System.out.println("Enquiry edited!");
                        System.out.println("============================================");
                        break;
                    case 4:
                        System.out.println("All enquiries");
                        System.out.println("============================================");
                        view_all_enquiry_for_user();
                        break;
                    case 5:
                        System.out.println("Delete enquiry");
                        System.out.println("============================================");
                        view_all_enquiry_for_user();
                        System.out.print("Enter ID of enquiry to delete: ");
                        // add confirmation before deleting
                        int del_id = scanner.nextInt();
                        deleteEnquiry(del_id);
                        System.out.println("Enquiry deleted!");
                        System.out.println("============================================");
                        break;
                    case 6:
                        System.out.println("Returning to applicant menu...");
                        break;
                    default:
                        System.out.println("Invalid choice. Please try again.");
                }
            } catch (InputMismatchException e) {
                System.out.println("Invalid input. Please enter a number.");
                scanner.next(); 
            }
        } while (choice != 6);
    }
    // check eligibility of user to apply for flat
    public boolean getEligibility(String flatType) {
        if (get_maritalstatus().equals("SINGLE") && get_age()>=35 && flatType.equals("2-Room")) {
            return true;
        } else if (get_maritalstatus().equals("MARRIED") && get_age()>=21) {
            return true;
        }
        return false;
    }

    public void view_listings() {
        System.out.println("\n================================================================================================================");
        System.out.println("                                                  ALL PROJECTS");
        System.out.println("================================================================================================================");
        System.out.printf("%-5s %-20s %-15s %-15s %-10s %-15s %-15s %-10s %n", "ID","Project Name", "Neighbourhood", "Flat Types", "Price","Open Date", "Close Date", "Eligibilty");
        System.err.println("----------------------------------------------------------------------------------------------------------------");
        // List<Project> list = BTOsystem.getProjects();
        List<Project> list = BTOsystem.projects;
        // System.out.println("DEBUG: Number of projects retrieved: " + list.size());
        for (Project p : list) {
            p.toggle_visibility(); //default is false, so second toggle will become false again (to test only)
            if (p.isVisible()) {
                // there are details we want to keep hidden from an applicant e.g. manager name, visibility, etc. so cannot just use toString()
                System.out.print(viewProjectsApplicant(p));
                
            }
        }
        System.err.println("----------------------------------------------------------------------------------------------------------------");
    }

    public String viewProjectsApplicant(Project p) {
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("%-5s %-20s %-15s %-15s %-10s %-15s %-15s %-10s %n",
            p.getProjectID(),
            p.getProjectName(),
            p.getneighbourhood(),
            p.getFlatTypes().size() > 0 ? 
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
            "", "", getEligibility(p.getFlatTypes().get(0)) ? "Eligible" : "Not Eligible"));  // Empty other fields
    }

    // Add blank line between projects
    sb.append("\n");

    return sb.toString();
    }

    public void makeEnquiry(String content) {
        Enquiry en = new Enquiry(this, content);
        enquiries.add(en);
        // BTOsystem.getEnquiries().add(en); // add enquiry to global enquiry list to be accessed by staff
        BTOsystem.enquiries.add(en); // add enquiry to global enquiry list to be accessed by staff
    }
    //to be revised, enquiries tagged to a project
    public void makeEnquiry(Project project, String content, String flatType) {
        // System.out.println("Enquiry: ");
        Enquiry en = new Enquiry(this, content);
        en.setProject(project);
        en.setflatType(flatType);
        enquiries.add(en);
        // BTOsystem.getEnquiries().add(en); // add enquiry to global enquiry list to be accessed by staff
        BTOsystem.enquiries.add(en); // add enquiry to global enquiry list to be accessed by staff
        // System.out.println("Enquiry sent!");
    }

    public void view_enquiry(Enquiry en) {
        System.out.println("Enquiry: "+ en.getEnquiry());
        System.out.println("Project: "+ (en.getProject()!=null?en.getProject().getProjectName():null));
        System.out.println("Flat Type: "+en.getflatType());
        if (en.getStaff()==null) {
            System.out.println("No reply to your enquiry yet.");
        } else {
            System.out.println("Response: "+en.getResponse());

        }
    }
    public void view_all_enquiry_for_user() {
        for (Enquiry en : enquiries) {
            System.out.println("#"+en.getEnId());
            view_enquiry(en);
            System.out.println("--------------------------------");
        }
    }
    public void viewEditableEnquiry() {
        for (Enquiry en : enquiries) {
            if (en.getStaff()==null){
                System.out.println("#"+en.getEnId());
                view_enquiry(en);
                System.out.println("--------------------------------");
            }
            
        }
    }
    public void editEnquiry(int id, String content) { // can only edit when no response from staff yett
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
    public void deleteEnquiry(int id) {
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