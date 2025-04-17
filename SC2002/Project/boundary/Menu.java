package SC2002.Project.boundary;

import java.util.Arrays;
import java.util.List;

public class Menu {
    // existing menus…
    private final List<String> welcomeMenu;
    private final List<String> loginError;
    private final List<String> role;
    private final List<String> applicantMenu;
    private final List<String> officerMenu;
    private final List<String> enquiryMenu;
    private final List<String> managerMenu;
    private final List<String> reportMenu;

    // new: manager’s “edit project” submenu
    private final List<String> editProjectMenu;

    public Menu() {
        this.welcomeMenu = Arrays.asList(
            "Please choose an option:",
            "1. Log in",
            "2. Register user",
            "3. Fetch data from excel sheets (CAUTION: Entire database will be wiped)",
            "4. Exit program",
            "Enter your choice: "
        );
        this.loginError = Arrays.asList(
            "No accounts in database.",
            "Please register a new user or load data from excel sheets.",
            "--------------------------------"
        );
        this.role = Arrays.asList(
            "Pick an option:",
            "1. Applicant",
            "2. HDB Officer",
            "3. HDB Manager",
            "Enter your choice:"
        );
        this.applicantMenu = Arrays.asList(
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
            "Enter your choice: "
        );
        this.officerMenu = Arrays.asList(
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
            "Enter your choice: "
        );
        this.managerMenu = Arrays.asList(
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
            "Enter your choice: "
        );
        this.enquiryMenu = Arrays.asList(
            "Please choose an option:",
            "1. Make General enquiry (select if not regarding a specific project)",
            "2. Make Project-related enquiry",
            "3. Edit enquiry",
            "4. View all enquiry",
            "5. Delete enquiry",
            "6. Return to User menu",
            "Enter your choice: "
        );
        this.reportMenu = Arrays.asList(
            "\n---- Generate Report ----",
            "1. View All Applicants",
            "2. Filter by Marital Status",
            "3. Filter by Flat Type",
            "4. Filter by Both Marital Status & Flat Type",
            "Enter your choice: "
        );

        this.editProjectMenu = Arrays.asList(
            "Select attribute to edit:",
            "1. Project Name",
            "2. Neighbourhood",
            "3. Edit Unit Count for Existing Flats",
            "4. Add New Flat Type",
            "5. Remove Flat Type",
            "6. Edit Existing Flat Prices",
            "7. Application Opening Date",
            "8. Application Closing Date",
            "9. Toggle Visibility",
            "10. Available HDB Officer Slots",
            "11. Return to Manager Menu",
            "Enter your choice: "
        );
    }

    // getters for each menu
    public List<String> getWelcomeMenu()      { return welcomeMenu; }
    public List<String> getLoginError()       { return loginError; }
    public List<String> getRoleMenu()         { return role; }
    public List<String> getApplicantMenu()    { return applicantMenu; }
    public List<String> getOfficerMenu()      { return officerMenu; }
    public List<String> getEnquiryMenu()      { return enquiryMenu; }
    public List<String> getManagerMenu()      { return managerMenu; }
    public List<String> getReportMenu()       { return reportMenu; }
    public List<String> getEditProjectMenu()  { return editProjectMenu; }

    // unified printer helper
    public void printMenu(List<String> menu) {
        for (String line : menu) {
            System.out.println(line);
        }
    }
}
