// package SC2002.Project;

import java.util.ArrayList;
import java.util.InputMismatchException;
import java.util.List;
import java.util.Scanner;
import java.util.*;

public class HDB_Officer extends Applicant {
    private String type="Officer";
    private static int hdb_off_id = -1;
    private int officer_id;
    private String registrationStatus;
    private Project officerProject;
    private static List<HDB_Officer> officerList = new ArrayList<>();

    public HDB_Officer(String nric, String firstname, String lastname, String marital_status, int age) {
        super(nric, firstname, lastname, marital_status, age);
        this.registrationStatus = "Unregistered"; // Default status
        officer_id = ++hdb_off_id;
    }
    Scanner sc = new Scanner(System.in);
    public void start_menu(Scanner sc) {
        //if want to apply, invoke officer menu
        menu.printOfficerMenu();
        int choice = sc.nextInt();
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
            manage_enquiry(sc);
            sc.nextLine();
            System.out.println("--------------------------------");
            break;
        case 6:
            System.out.println("Account details");
            System.out.println("--------------------------------");
            to_string();
            System.out.println("--------------------------------");
            sc.nextLine();
            // scanner.next();
            break;
        case 7:
            System.out.println("Change your password");
            System.out.println("--------------------------------");
            sc.nextLine();
            System.out.print("Enter current password: ");
            String oldpass = sc.nextLine();
            System.out.print("Enter new password: ");
            String new_pass1 = sc.nextLine();
            System.out.print("Enter new password again to confirm: ");
            String new_pass2 = sc.nextLine();
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

            case 8:
            List<Project> allProjects = BTOsystem.getProjects();
            System.out.println("=== Choose a project to register as Officer ===");
            for (int i = 0; i < allProjects.size(); i++) {
                System.out.println("[" + i + "] " + allProjects.get(i).getProjectName());
            }
            System.out.print("Enter index: ");
            choice = -1;
            try {
                choice = sc.nextInt();
            } catch (InputMismatchException e) {
                System.out.println("Please enter a valid number.");
                sc.nextLine();
                return;
            }
            if (choice < 0 || choice >= allProjects.size()) {
                System.out.println("Invalid choice. Returning...");
                return;
            }
            Project target = allProjects.get(choice);

            // Check if this officer has an application for that same project
            if (application != null && application.getProject() != null
                    && application.getProject().equals(target)) {
                System.out.println("Cannot register as Officer for a project you have applied for as an Applicant!");
                return;
            }

            // Basic "overlap" check
            if (isApplicationPeriodOverlapping(target)) {
                System.out.println("You are already assigned or pending another project overlapping these dates!");
                return;
            }

            // If pass checks, set "registrationStatus = 'Pending'" and "officerProject = target"
            // Manager must call manager.handleOfficerRegistration(...) to truly approve
            registerForProject(target);
            case 9:
                System.out.println("Your Officer Registration Status: " + registrationStatus);
                if (officerProject != null) {
                    System.out.println("Assigned Project: " + officerProject.getProjectName());
                } else {
                    System.out.println("No assigned project currently.");
                }
                break;

            case 10:
                if (officerProject != null) {
                    System.out.println(officerProject.toString());
                } else {
                    // Maybe let them pick any visible project
                    System.out.println("You have no assigned project. You may view any visible projects:");
                    allProjects = BTOsystem.getProjects();
                    for (int i = 0; i < allProjects.size(); i++) {
                        Project p = allProjects.get(i);
                        if (p.isVisible()) {
                            System.out.println("[" + i + "] " + p.getProjectName());
                        }
                    }
                    System.out.print("Enter index or -1 to cancel: ");
                    int idx = -1;
                    try {
                        idx = sc.nextInt();
                    } catch (InputMismatchException e) {
                        sc.nextLine();
                    }
                    if (idx >= 0 && idx < allProjects.size() && allProjects.get(idx).isVisible()) {
                        System.out.println(allProjects.get(idx));
                    } else {
                        System.out.println("No such project or canceled.");
                    }
                }

            case 11:
                System.out.println("Logged out. Returning to main menu...");
                System.out.println("--------------------------------");
                break;
            default:
                System.out.println("Invalid choice. Please try again.");
                break;
            
        }
    }
    
    public void reply_enquiry(Enquiry enquiry, String response) {
        enquiry.setStaffReply(this);
        enquiry.setResponse(response);;
    }

    public static List<HDB_Officer> getOfficerList()
    {
        return officerList;
    }

    public static void setOfficerList(List<HDB_Officer> newList)
    {
        officerList = newList;
    }

    public String getRegistrationStatus()
    {
        return registrationStatus;
    }

    public void setRegistrationStatus(String status)
    {
        registrationStatus = status;
    }

    public Project getAssignedProject()
    {
        return officerProject;
    }
    public void setAssignedProject(Project project)
    {
        officerProject = project;
    }

    public void registerForProject(Project project) {
        if (project == null)
        {
            System.out.println("Project does not exit. Registration unsuccessful.");
                return;
        }

        if (registrationStatus.equals("Unregistered")) {
            officerList.add(this);
            officerProject = project;
            registrationStatus = "Pending";
            System.out.println("Registration for " + project.getProjectName() + " successful. Registration request has been sent to the HDB Manager for approval.");
        } else {
            switch (registrationStatus) {
                case "Approved":
                    System.out.println("You are already registered for a project.");
                    break;
                
                case "Pending":
                    System.out.println("You are already have a pending approval for a project.");
                    break;
            
                default:
                    break;
            }
            // System.out.println("You are already registered or have a pending approval for a project.");
        }
    }


    // From pdf: Not a HDB Officer (registration not approved) for another project
    // within an application period (from application opening date,
    // inclusive, to application closing date, inclusive)
    public boolean isApplicationPeriodOverlapping(Project project) {
        if (officerProject == null) return false; // no assigned project => no overlap
        // Compare date ranges
        if (!(project.getCloseDate().isBefore(officerProject.getOpenDate()) 
           || project.getOpenDate().isAfter(officerProject.getCloseDate()))) {
            return true; 
        }
        return false;
    }

    // From pdf: No intention to apply for the project as an Applicant (Cannot apply
    // for the project as an Appplicant before and after becoming an HDB
    // Officer of the project)

    // Check if officer has applied to be a Applicant (for this project or other projects)
    public boolean hasAppliedAsApplicant() {
    // public boolean hasAppliedAsApplicant(Project project) {
        // Logic to check if officer has already applied for as an Applicant.
        if (this.application != null 
                && !this.application.getStatus().equalsIgnoreCase("Withdrawn")
                && !this.application.getStatus().contains("Rejected")) {
            return true;
        }
        return false;
    }
}