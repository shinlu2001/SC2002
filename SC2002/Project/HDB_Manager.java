package SC2002.Project;

import java.util.ArrayList;
import java.util.InputMismatchException;
import java.util.List;
import java.util.Scanner;

public class HDB_Manager extends User {
    private static int hdb_man_id = -1;
    private int manager_id;
    private String type="MANAGER";
    private List<BTOapplication> allApplications = new ArrayList<>();

    public HDB_Manager(String nric, String firstname, String lastname, String marital_status, int age) {
        super(nric, firstname, lastname, marital_status, age);
        manager_id = ++hdb_man_id;
    }
    public void reply_enquiry(Enquiry enquiry, String response) {
        enquiry.setStaffReply(this);
        enquiry.setResponse(response);;
    }
  

    @Override
    public void start_menu(Scanner sc) {
        int choice = 0;
        do {
            System.out.println("\n=== HDB Manager Menu ===");
            System.out.println("1.  Create BTO Project Listing");
            System.out.println("2.  Edit BTO Project Listing");
            System.out.println("3.  Delete BTO Project Listing");
            System.out.println("4.  Toggle Project Visibility");
            System.out.println("5.  View All Projects (Regardless of Visibility)");
            System.out.println("6.  View Only Projects I Created");
            System.out.println("7.  View HDB Officer Registrations (Pending/Approved)");
            System.out.println("8.  Approve or Reject Officer Registration");
            System.out.println("9.  Approve or Reject Applicant’s BTO Application");
            System.out.println("10. Approve or Reject Applicant’s Withdrawal Request");
            System.out.println("11. Generate Report of Booked Flats (with Filters)");
            System.out.println("12. View Enquiries for All Projects");
            System.out.println("13. Reply to Enquiries for My Project");
            System.out.println("14. Logout / Return to Main Menu");
            System.out.print("Enter your choice: ");

            try {
                choice = sc.nextInt();
                sc.nextLine(); // consume newline
            } catch (InputMismatchException e) {
                System.out.println("Invalid input. Please enter a number.");
                sc.nextLine();
                continue;
            }

            switch (choice) {
                case 1:
                    createProject(sc);
                    break;
                // ... Other cases for options 2-10 and 12-13 ...
                case 11:
                    // Call generateReport with the available list of applications
                    generateReport(allApplications, sc);
                    break;
                case 14:
                    System.out.println("Logging out. Returning to main menu...");
                    break;
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        } while (choice != 14);
    }

    // --- Placeholder methods for other functionalities ---
    private void createProject(Scanner sc) {
        System.out.println("Create BTO Project Listing (not yet implemented).");
    }
    // ... Other placeholder methods for editProject, deleteProject, etc. ...

    // --- The generateReport method with filtering options ---
    public void generateReport(List<BTOapplication> applicationList, Scanner sc) {
        if (applicationList.isEmpty()) {
            System.out.println("No applications available.");
            return;
        }

        while (true) {
            System.out.println("\n--- Generate Report ---");
            System.out.println("1. View All Applicants");
            System.out.println("2. Filter by Marital Status");
            System.out.println("3. Filter by Flat Type");
            System.out.println("4. Filter by Both Marital Status & Flat Type");
            System.out.println("5. Return");
            System.out.print("Enter your choice: ");

            int choice;
            try {
                choice = sc.nextInt();
                sc.nextLine(); // consume leftover newline
            } catch (InputMismatchException e) {
                System.out.println("Invalid input. Please enter a number.");
                sc.nextLine(); // clear invalid input
                continue;      // re-prompt
            }

            switch (choice) {
                case 1:
                    // 1. Show all applications (no filtering)
                    printApplicationList(applicationList);
                    break;
                case 2:
                    // 2. Filter by marital status
                    System.out.print("Enter marital status (Single/Married): ");
                    String status = sc.nextLine().trim().toLowerCase();
                    
                    List<BTOapplication> filteredByMarital = new ArrayList<>();
                    for (BTOapplication app : applicationList) {
                        String applicantStatus = app.getApplicant().get_maritalstatus().toLowerCase();
                        if (applicantStatus.equals(status)) {
                            filteredByMarital.add(app);
                        }
                    }
                    printApplicationList(filteredByMarital);
                    break;
                case 3:
                    // 3. Filter by flat type
                    System.out.print("Enter flat type (2-Room/3-Room): ");
                    String flatType = sc.nextLine().trim();
                    
                    List<BTOapplication> filteredByFlat = new ArrayList<>();
                    for (BTOapplication app : applicationList) {
                        if (app.getFlatType().equalsIgnoreCase(flatType)) {
                            filteredByFlat.add(app);
                        }
                    }
                    printApplicationList(filteredByFlat);
                    break;
                case 4:
                    // 4. Filter by both marital status & flat type
                    System.out.print("Enter marital status (Single/Married): ");
                    status = sc.nextLine().trim().toLowerCase();
                    
                    System.out.print("Enter flat type (2-Room/3-Room): ");
                    flatType = sc.nextLine().trim();
                    
                    List<BTOapplication> filteredByBoth = new ArrayList<>();
                    for (BTOapplication app : applicationList) {
                        String applicantStatus = app.getApplicant().get_maritalstatus().toLowerCase();
                        if (applicantStatus.equals(status) && app.getFlatType().equalsIgnoreCase(flatType)) {
                            filteredByBoth.add(app);
                        }
                    }
                    printApplicationList(filteredByBoth);
                    break;
                case 5:
                    // 5. Return to the previous menu
                    System.out.println("Returning...");
                    return;
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        }
    }

    private void printApplicationList(List<BTOapplication> apps) {
        if (apps.isEmpty()) {
            System.out.println("No matching applications found.");
            return;
        }

        // Print a header row
        System.out.printf("%-20s %-5s %-15s %-10s %-15s%n",
                "Applicant Name", "Age", "Marital Status", "Flat", "Project");

        // Print each record
        for (BTOapplication app : apps) {
            Applicant applicant = app.getApplicant();
            String fullName = applicant.get_firstname() + " " + applicant.get_lastname();
            int age = applicant.get_age();
            String marital = applicant.get_maritalstatus();
            String flat = app.getFlatType();       // e.g., "2-Room" or "3-Room"
            String project = app.getProjectName();   // or app.getProject().getProjectName()

            System.out.printf("%-20s %-5d %-15s %-10s %-15s%n",
                    fullName, age, marital, flat, project);
        }
    }
    
    // Additional methods (e.g., viewAllEnquiries, replyToEnquiriesForMyProject) can follow here.
    
    public void to_string() {
        super.to_string();
        System.out.println("Account type: " + type);
        System.out.println("Manager ID: " + manager_id);
    }
}