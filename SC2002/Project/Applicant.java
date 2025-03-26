// package SC2002.Project;
import java.util.*;

public class Applicant extends User {
    private static int nextId = -1;
    private int applicantID;
    protected BTOapplication application=null;
    private String type="APPLICANT";
    private List<Enquiry> enquiries = new ArrayList<>();

    public Applicant(String nric, String firstname, String lastname, String marital_status, int age) {
        super(nric, firstname, lastname, marital_status, age);
        applicantID = ++nextId;
    }

    public void start_menu(Scanner scanner) {
        System.out.println("Welcome to HDB BTO Management System, " + this.get_firstname() + "!");
        System.out.println("---Applicant menu---");
        int choice=0;
        do {
            try {
                menu.printApplicantMenu();
                
                choice = scanner.nextInt();
                System.out.println("--------------------------------");
                switch (choice) {
                    case 1:
                        if (application != null) {
                            System.out.println("You already have an active application. You may not create a new one.");
                            System.out.println("--------------------------------");
                        } else {
                            // view current application 
                            // application.getDetails;
                            System.out.println("--------------------------------");
                        }
                        break;
                    case 2:
                        if (application == null) {
                            System.out.println("You have no active application. Please create a new application.");
                            System.out.println("--------------------------------");
                        } else {
                            application.get_details();
                            System.out.println("--------------------------------");
                        }
                        break;
                    case 3:
                        System.out.println(BTOsystem.getProjects());
                        view_listings();
                        break;
                    case 4:
                        System.out.println("Withdraw application");
                        System.out.println("--------------------------------");
                        break;
                    case 5:
                        manage_enquiry(scanner);
                        scanner.nextLine();
                        System.out.println("--------------------------------");
                        break;
                    case 6:
                        System.out.println("Account details");
                        System.out.println("--------------------------------");
                        to_string();
                        System.out.println("--------------------------------");
                        scanner.nextLine();
                        // scanner.next();
                        break;
                    case 7:
                        System.out.println("Change your password");
                        System.out.println("--------------------------------");
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
                        System.out.println("--------------------------------");
                        break;
                    case 8: //apply to become officer
                        System.out.println("Application to become a HDB Officer");
                        System.out.println("--------------------------------");
                        
                        break;
                    case 9:
                        // scanner.nextLine();
                        System.out.println("Logged out. Returning to main menu...");
                        System.out.println("--------------------------------");
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
        System.out.println("---Enquiry menu---");
        do {
            try {
                menu.printEnquiryMenu();
                choice = scanner.nextInt();
                System.out.println("--------------------------------");
                switch (choice) {
                    case 1:
                        scanner.nextLine();
                        System.out.println("Enquiry: ");
                        String content = scanner.nextLine();
                        makeEnquiry(content);
                        // en.setProject(project);
                        // enquiries.add(en);
                        System.out.println("Enquiry sent!");
                        // makeEnquiry(scanner);
                        System.out.println("--------------------------------");
                        break;
                    case 2:
                        System.out.println("Edit enquiry");
                        
                        view_all_enquiry_for_user();
                        System.out.println("Enter ID of enquiry to edit: ");
                        int id = scanner.nextInt();
                        scanner.nextLine();
                        System.out.print("Enquiry: ");
                        String userInput = scanner.nextLine();
                        // maybe add confirmation?
                        editEnquiry(id, userInput);
                        System.out.println("Enquiry edited!");
                        System.out.println("--------------------------------");
                        break;
                    case 3:
                        System.out.println("All enquiries");
                        System.out.println("--------------------------------");
                        view_all_enquiry_for_user();
                        break;
                    case 4:
                        System.out.println("Delete enquiry");
                        System.out.println("--------------------------------");
                        view_all_enquiry_for_user();
                        System.out.print("Enter ID of enquiry to delete: ");
                        // add confirmation before deleting
                        int del_id = scanner.nextInt();
                        deleteEnquiry(del_id);
                        System.out.println("Enquiry deleted!");
                        System.out.println("--------------------------------");
                        break;
                    case 5:
                        System.out.println("Returning to applicant menu...");
                        break;
                    default:
                        System.out.println("Invalid choice. Please try again.");
                }
            } catch (InputMismatchException e) {
                System.out.println("Invalid input. Please enter a number.");
                scanner.next(); 
            }
        } while (choice != 5);
    }
    public void view_listings() {
        List<Project> list = BTOsystem.getProjects();
        for (Project p : list) {
            if (p.isVisible()){
                System.out.println("Enquiry sent!");
                p.toString();
            }
        }
    }
    //to be revised, enruiries tagged to a project
    public void makeEnquiry(Scanner scanner, Project project) {
        System.out.println("Enquiry: ");
        String content = scanner.nextLine();
        Enquiry en = new Enquiry(this, content);
        en.setProject(project);
        enquiries.add(en);
        System.out.println("Enquiry sent!");
    }

    public void makeEnquiry(String content) {
        Enquiry en = new Enquiry(this, content);
        enquiries.add(en);
    }

    public void view_enquiry(Enquiry en) {
        System.out.println("Enquiry: "+en.getEnquiry());
        System.out.println("Project: "+en.getProject());
        if (en.getStaff()==null) {
            System.out.println("No reply to your enquiry yet.");
        } else {
            System.out.println("Response: "+en.getResponse());

        }
    }
    public void view_all_enquiry_for_user() {
        for (Enquiry en : enquiries) {
            System.out.println("#"+enquiries.indexOf(en));
            view_enquiry(en);
            System.out.println("--------------------------------");
        }
    }
    public void editEnquiry(int id, String content) {
        enquiries.get(id).setEnquiry(content);
        enquiries.get(id).setResponse(""); // for edited enquiry, the response field should be cleared
        enquiries.get(id).setStaffReply(null);
    }
    public void deleteEnquiry(int id) {
        Enquiry removedElement = enquiries.remove(id);
        System.out.println("Deleted enquiry: " + removedElement.getEnquiry());
        System.out.println("Deleted response: " + removedElement.getResponse());
        removedElement = null;
    }
}