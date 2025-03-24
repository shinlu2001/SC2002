package SC2002.Project;

import java.util.Arrays;
import java.util.List;

public class Menu {
    // Using a generic List of Strings for the welcome menu
    private List<String> welcomeMenu;
    private List<String> loginError;
    private List<String> role;
    private List<String> applicantMenu;
    private List<String> enquiryMenu;

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
        this.enquiryMenu = Arrays.asList(
                "Please choose an option:",
                "1. Make enquiry",
                "2. Edit enquiry",
                "3. View all enquiry",
                "4. Delete enquiry",
                "5. Return to applicant menu",
                "Enter your choice: ");
    }

    // Optional: Constructor to allow passing a custom welcome menu
    public Menu(List<String> welcomeMenu, List<String> loginError, List<String> role, List<String> applicantMenu, List<String> enquiryMenu) {
        this.welcomeMenu = welcomeMenu;
        this.loginError = loginError;
        this.role = role;
        this.applicantMenu = applicantMenu;
        this.enquiryMenu= enquiryMenu;

    }

    public void printWelcomeMenu() {
        for (String menuItem : welcomeMenu) {
            System.out.println(menuItem);
        }
    }

    public void printloginError() {
        for (String loginErrorItem : loginError) {
            System.out.println(loginErrorItem);
        }
    }

    public void printSelectRole() {
        for (String selectRole : role) {
            System.out.println(selectRole);
        }
    }

    public void printApplicantMenu() {
        for (String option : applicantMenu) {
            System.out.println(option);
        }
    }

    public void printEnquiryMenu() {
        for (String option : enquiryMenu) {
            System.out.println(option);
        }
    }
}
