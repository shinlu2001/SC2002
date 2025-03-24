package SC2002.Project;


import java.util.*;
import java.time.LocalDate;
// import java.util.ArrayList;
// import java.util.List;

public abstract class User {
    private String nric;
    private String firstname;
    private String lastname;
    private String password="password";
    private String marital_status;
    private int age;
    private String type;
    // private boolean logged_in=false;
    public User(String nric, String firstname, String lastname, String marital_status, int age) {
        this.nric = nric;
        this.firstname = firstname;
        this.lastname = lastname;
        this.marital_status = marital_status;
        this.age = age;
    }
    public String get_firstname() {
        return firstname;
    }
    public String get_lastname() {
        return lastname;
    }
    public String get_nric() {
        return nric;
    }
    public String get_password() {
        return password;
    }
    public String get_maritalstatus() {
        return marital_status;
    }
    public int get_age() {
        return age;
    }
    public void to_string() {
        System.out.println("NRIC: " + nric);
        System.out.println("First name: " + firstname);
        System.out.println("Last name: " + lastname);
        System.out.println("Age: "+ age);
        System.out.println("Marital status: " + marital_status);   
    }
    public void change_password(String newpass) {
        this.password = newpass;
    }
    public boolean verify_password(String password) {
        if (this.password.equals(password)) {
            return true;
        } else {
            return false;
        }
    }
    public abstract void start_menu(Scanner scanner);
    public void makeEnquiry() {}
}

class Applicant extends User {
    private static int nextId = -1;
    private int applicantID;
    private BTOapplication application=null;
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
                System.out.println("Please choose an option:");
                System.out.println("1. Apply for a project");
                System.out.println("2. View active application");
                System.out.println("3. View all listings");
                System.out.println("4. Withdraw application");
                System.out.println("5. Manage enquiries");
                System.out.println("6. View account details");
                System.out.println("7. Change account password");
                System.out.println("8. Apply to become an officer");
                System.out.println("9. Log out and return to main program");
                System.out.print("Enter your choice: ");
                
                choice = scanner.nextInt();
                // scanner.nextLine();
                System.out.println("--------------------------------");
                switch (choice) {
                    case 1:
                        if (application != null) {
                            System.out.println("You already have an active application. You may not create a new one.");
                            System.out.println("--------------------------------");
                        } else {
                            view_listings();
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
                System.out.println("Please choose an option:");
                System.out.println("1. Make enquiry");
                System.out.println("2. Edit enquiry");
                System.out.println("3. View all enquiry");
                System.out.println("4. Delete enquiry");
                System.out.println("5. Return to applicant menu");
                System.out.print("Enter your choice: ");

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
                        // scanner.nextLine();
                        break;
                    default:
                        System.out.println("Invalid choice. Please try again.");
                }
            } catch (InputMismatchException e) {
                System.out.println("Invalid input. Please enter a number.");
                scanner.next(); 
            }
        } while (choice != 5);
        // scanner.nextLine();
        // this.start_menu(scanner);
    }
    public void view_listings() {
        
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

class HDB_Manager extends User {
    private static int hdb_man_id = -1;
    private int manager_id;
    private String type="MANAGER";
    public HDB_Manager(String nric, String firstname, String lastname, String marital_status, int age) {
        super(nric, firstname, lastname, marital_status, age);
        manager_id = ++hdb_man_id;
    }
    public void reply_enquiry(Enquiry enquiry, String response) {
        enquiry.setStaffReply(this);
        enquiry.setResponse(response);;
    }
    public void start_menu(Scanner sc) {

    }
}

class HDB_Officer extends Applicant {
    private String type="Officer";
    private static int hdb_off_id = -1;
    private int officer_id;
    public HDB_Officer(String nric, String firstname, String lastname, String marital_status, int age) {
        super(nric, firstname, lastname, marital_status, age);
        officer_id = ++hdb_off_id;
    }
    public void reply_enquiry(Enquiry enquiry, String response) {
        enquiry.setStaffReply(this);
        enquiry.setResponse(response);;
    }
    public void start_menu(Scanner sc) {
        //if want to apply, invoke applicant menu
    }
}

class BTOapplication {
    public void get_details() {
        System.out.println("a");
    }
}
class Enquiry {
    private String content;
    private String response="";
    private Project project=null;
    private User createdBy;
    private User repliedBy=null;
    public Enquiry(User user, String content) {
        createdBy = user;
        this.content = content;
    }
    public void setEnquiry(String r) {
        content = r;
    }
    public void setResponse(String r) {
        response = r;
    }
    public String getResponse() {
        return response;
    }
    public String getEnquiry() {
        return content;
    }
    public void setStaffReply(User staff) {
        repliedBy = staff;
    }
    public User getStaff() {
        return repliedBy;
    }
    public User getCreatedByUser() {
        return createdBy;
    }
    public void setProject(Project p) {
        project = p;
    }
    public Project getProject() {
        return project;
    }
}

