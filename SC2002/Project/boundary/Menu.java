package SC2002.Project.boundary;

import java.util.Arrays;
import java.util.List;

/**
 * Holds all CLI menu strings for the BTO system.
 * <p>Everything is <b>static</b>; the class need not be instantiated.</p>
 */
public final class Menu {

    /* ────────────── static menus ────────────── */
    private static final List<String> WELCOME_MENU = Arrays.asList(
        "Welcome to the BTO management system!",
        "Please choose an option:",
        "1. Log in",
        "2. Register user",
        "3. Fetch data from excel sheets (autoloaded on startup)",
        "4. Exit program",
        "Enter your choice: "
    );

    private static final List<String> LOGIN_ERROR = Arrays.asList(
        "No accounts in database.",
        "Please register a new user or load data from excel sheets.",
        "--------------------------------"
    );

    private static final List<String> ROLE_MENU = Arrays.asList(
        "Pick an option:",
        "1. Applicant",
        "2. HDB Officer",
        "3. HDB Manager",
        "Enter your choice:"
    );

    private static final List<String> APPLICANT_MENU = Arrays.asList(
        "==== BTO Application ====",
        "1. Apply for project",
        "2. View active application",
        "3. View eligible listings",
        "4. View all listings",
        "5. Withdraw application",
        "==== Enquiries ====",
        "6. Manage enquiries",
        "==== Account ====",
        "7. View account details",
        "8. Change password",
        "0. Log out",
        "Enter choice: "
    );

    private static final List<String> OFFICER_MENU = Arrays.asList(
        "==== BTO Application ====",
        "1. Apply for project",
        "2. View active application",
        "3. View eligible listings",
        "4. View all listings",
        "5. Withdraw application",
        "==== Enquiries ====",
        "6. View all user submitted enquiries (as an OFFICER)",
        "7. Manage user enquiries (as an OFFICER)",
        "8. Manage own enquiries (as an APPLICANT)",
        "==== Officer Functions ====",
        "9. Register for project",
        "10. Manage officer registration",
        "11. View project details",
        "12. Process flat booking",
        "13. View assigned project applications",
        "==== Account ====",
        "14. View account details",
        "15. Change password",
        "0. Log out",
        "Enter choice: "
    );

    private static final List<String> MANAGER_MENU = Arrays.asList(
        "==== Project Management ====",
        "1. Create project",
        "2. Edit project",
        "3. Delete project",
        "4. View all projects",
        "5. View my projects",
        "==== Officer Management ====",
        "6. View officer registrations",
        "7. Handle officer registration",
        "8. Handle officer withdrawal requests",
        "9. View assigned officers",
        "==== Application Management ====",
        "10. Handle BTO applications",
        "11. Handle withdrawal requests",
        "12. Generate applicant report",
        "==== Enquiries ====",
        "13. View all enquiries",
        "14. Handle project enquiries",
        "==== Account ====",
        "15. View account details",
        "16. Change password",
        "0. Log out",
        "Enter choice: "
    );

    private static final List<String> ENQUIRY_MENU = Arrays.asList(
        "Please choose an option:",
        "1. Make General enquiry (select if not regarding a specific project)",
        "2. Make Project-related enquiry",
        "3. Edit enquiry",
        "4. View all enquiry",
        "5. Delete enquiry",
        "6. Return to User menu",
        "Enter your choice: "
    );

    private static final List<String> REPORT_MENU = Arrays.asList(
        "\n---- Generate Report ----",
        "1. View All Applicants",
        "2. Filter by Marital Status",
        "3. Filter by Flat Type",
        "4. Filter by Both Marital Status & Flat Type",
        "Enter your choice: "
    );

    private static final List<String> EDIT_PROJECT_MENU = Arrays.asList(
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

    public static final int COL_ID    = 5;
    public static final int COL_NAME  = 40;
    public static final int COL_HOOD  = 20;
    public static final int COL_FLAT  = 25;
    public static final int COL_PRICE = 15;
    public static final int COL_OPEN  = 15;
    public static final int COL_CLOSE = 15;
    public static final int COL_ELIG  = 10;

    public static final int PROJECT_TABLE_WIDTH =
        COL_ID + COL_NAME + COL_HOOD + COL_FLAT
      + COL_PRICE + COL_OPEN + COL_CLOSE
      + ( /* eligibility? */ COL_ELIG )
      + /* spaces between 8 columns */ 7;

    /* ────────────── static getters ────────────── */
    public static List<String> getWelcomeMenu()     { return WELCOME_MENU; }
    public static List<String> getLoginError()      { return LOGIN_ERROR; }
    public static List<String> getRoleMenu()        { return ROLE_MENU; }
    public static List<String> getApplicantMenu()   { return APPLICANT_MENU; }
    public static List<String> getOfficerMenu()     { return OFFICER_MENU; }
    public static List<String> getManagerMenu()     { return MANAGER_MENU; }
    public static List<String> getEnquiryMenu()     { return ENQUIRY_MENU; }
    public static List<String> getReportMenu()      { return REPORT_MENU; }
    public static List<String> getEditProjectMenu() { return EDIT_PROJECT_MENU; }

    /** Generic helper to dump any menu to <kbd>stdout</kbd>. */
    public static void printMenu(List<String> menu) {
        menu.forEach(System.out::println);
    }

    // prevent instantiation
    private Menu() { }
}
