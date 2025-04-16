package SC2002.Project.boundary;

import java.util.Arrays;
import java.util.List;

public class Menu {
    private List<String> welcomeMenu;
    private List<String> loginError;
    private List<String> role;
    private List<String> applicantMenu;
    private List<String> officerMenu;
    private List<String> enquiryMenu;
    private List<String> managerMenu;
    private List<String> reportMenu;
    
    public Menu() {
        welcomeMenu = Arrays.asList(
            "Please choose an option:",
            "1. Log in",
            "2. Register user",
            "3. Fetch data from excel sheets (CAUTION: Entire database will be wiped)",
            "4. Exit program",
            "Enter your choice: ");
        loginError = Arrays.asList(
            "No accounts in database.",
            "Please register a new user or load data from excel sheets.",
            "--------------------------------");
        role = Arrays.asList(
            "Pick an option:",
            "1. Applicant",
            "2. HDB Officer",
            "3. HDB Manager",
            "Enter your choice:");
        applicantMenu = Arrays.asList(
            "Please choose an option:",
            "1. Apply for a project",
            "2. Manage active application",
            "3. View only eligible listings",
            "4. View all listings",
            "5. Withdraw application",
            "6. Manage enquiries",
            "7. View account details",
            "8. Change account password",
            "9. Log out and return to main program",
            "Enter your choice: ");
        officerMenu = Arrays.asList(
            "Please choose an option:",
            "1. Apply for a project",
            "2. View active application",
            "3. View only eligible listings",
            "4. View all listings",
            "5. Withdraw application",
            "6. Manage user enquiries",
            "7. Manage own enquiries",
            "8. View account details",
            "9. Change account password",
            "10. Register to be a HDB officer of a project",
            "11. Check status of registration to be an officer for a project",
            "12. View project details",
            "13. Process flat booking",
            "14. View applications for assigned project",
            "15. Log out and return to main program",
            "Enter your choice: ");
        managerMenu = Arrays.asList(
            "1. Create a Project",
            "2. Edit a Project",
            "3. Delete a Project",
            "4. View All Projects",
            "5. View My Projects",
            "6. View Officer Registrations",
            "7. Handle Officer Registration",
            "8. Handle Officer Registration Withdrawal Requests",
            "9. Handle BTO Applications",
            "10. Handle BTO Application Withdrawal Requests",
            "11. Generate Applicant Report",
            "12. View All Enquiries",
            "13. Handle Project Enquiries",
            "14. View account details",
            "15. Log out",
            "Enter your choice: ");
        enquiryMenu = Arrays.asList(
            "Please choose an option:",
            "1. Make General enquiry (if not regarding a specific project)",
            "2. Make Project-related enquiry",
            "3. Edit enquiry",
            "4. View all enquiry",
            "5. Delete enquiry",
            "6. Return to User menu",
            "Enter your choice: ");
        reportMenu = Arrays.asList(
            "\n---- Generate Report ----",
            "1. View All Applicants",
            "2. Filter by Marital Status",
            "3. Filter by Flat Type",
            "4. Filter by Both Marital Status & Flat Type",
            "Enter your choice: ");
    }
    
    public void printMenu(List<String> menuList) {
        for (String option : menuList) {
            System.out.println(option);
        }
    }
    
    public void printWelcomeMenu() { printMenu(welcomeMenu); }
    public void printLoginError() { printMenu(loginError); }
    public void printRoleMenu() { printMenu(role); }
    public void printApplicantMenu() { printMenu(applicantMenu); }
    public void printOfficerMenu() { printMenu(officerMenu); }
    public void printManagerMenu() { printMenu(managerMenu); }
    public void printEnquiryMenu() { printMenu(enquiryMenu); }
    public void printReportMenu() { printMenu(reportMenu); }
    public void printSelectRole() {
        System.out.println("Select role:");
        printMenu(role);
    }
}
