package SC2002.Project;

import java.util.Arrays;
import java.util.List;

public class Menu {
    // Using a generic List of Strings for the welcome menu
    private List<String> welcomeMenu;
    private List<String> loginError;
    private List<String> role;
    private List<String> applicantMenu;
    private List<String> officerMenu;
    private List<String> enquiryMenu;
    private List<String> managerMenu;

    // Constructor that uses a default welcome menu
    public Menu() {
        this.welcomeMenu = Arrays.asList(
                "Please choose an option:",
                "1. Log in",
                "2. Register user",
                "3. Fetch data from excel sheets",
                "4. Exit program",
                "Enter your choice: ");
        this.loginError = Arrays.asList(
                "No accounts in database.",
                "Please register a new user or load data from excel sheets.",
                "--------------------------------");

        this.role = Arrays.asList(
                "Pick an option:",
                "1. Applicant",
                "2. HDB Officer",
                "3. HDB Manager",
                "Enter your choice:");
        this.applicantMenu = Arrays.asList(
                "Please choose an option:",
                "1. Apply for a project",
                "2. View active application",
                "3. View all listings",
                "4. Withdraw application",
                "5. Manage enquiries",
                "6. View account details",
                "7. Change account password",
                "8. Apply to become an officer",
                "9. Log out and return to main program",
                "Enter your choice: ");
        this.managerMenu = Arrays.asList(
                "1. Create a Project",
                "2. Edit a Project",
                "3. Delete a Project",
                "4. View All Projects",
                "5. View My Projects",
                "6. View Officer Registrations",
                "7. Handle Officer Registration",
                "8. Handle BTO Applications",
                "9. Handle Withdrawal Requests",
                "10. Generate Applicant Report",
                "11. View All Enquiries",
                "12. Handle Project Enquiries",
                "13. View account details",
                "14. Log out",
                "Enter your choice: ");
        this.officerMenu = Arrays.asList(
                "Please choose an option:",
                "1. Apply for a project",
                "2. View active application",
                "3. View all listings",
                "4. Withdraw application",
                "5. Manage enquiries",
                "6. View account details",
                "7. Change account password",
                "8. Register to be a HDB officer of a project",
                "9. Check status of registration to be an officer for a project",
                "10. View project details",
                "11. Log out and return to main program",
                "Enter your choice: ");
        this.enquiryMenu = Arrays.asList(
                "Please choose an option:",
                "1. Make enquiry (select if not regarding a specific project)",
                "2. Make Project-related enquiry",
                "3. Edit enquiry",
                "4. View all enquiry",
                "5. Delete enquiry",
                "6. Return to applicant menu",
                "Enter your choice: ");
    }

    // Optional: Constructor to allow passing a custom welcome menu
    public Menu(List<String> welcomeMenu, List<String> loginError, List<String> role, List<String> applicantMenu, List<String> enquiryMenu, List<String> managerMenu) {
        this.welcomeMenu = welcomeMenu;
        this.loginError = loginError;
        this.role = role;
        this.applicantMenu = applicantMenu;
        this.enquiryMenu= enquiryMenu;
        this.managerMenu= managerMenu;

    }

    public void printMenu(List<String> list) {
        for (String option : list) {
            System.out.println(option);
        }
    }

    public void printWelcomeMenu() {
        printMenu(welcomeMenu);
    }

    public void printloginError() {
        printMenu(loginError);
    }

    public void printSelectRole() {
        printMenu(role);
    }
    public void printApplicantMenu() {
        printMenu(applicantMenu);
    }
    public void printManagerMenu() {
        printMenu(managerMenu);
    }
    public void printEnquiryMenu() {
        printMenu(enquiryMenu);
    }
    public void printOfficerMenu(){
        printMenu(officerMenu);
    }
}
