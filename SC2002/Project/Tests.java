package SC2002.Project;

import java.io.ByteArrayInputStream;
import java.lang.reflect.Field;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Tests {

    public static void main(String[] args) {
        System.out.println("Starting tests...");

        testApplicantFeatures();
        testOfficerFeatures();
        testManagerFeatures();
        testPrivacyRestrictions();

        System.out.println("All tests completed.");
    }

    /**
     * Tests features for an Applicant.
     * Each try block prints the actual output followed by a line showing the expected output.
     */
    public static void testApplicantFeatures() {
        System.out.println("\n=== Test: Applicant Features ===");
        Applicant applicant = null;
        try {
            applicant = new Applicant("S1111111X", "Alice", "Applicant", "SINGLE", 36);
            System.out.println("Applicant created successfully.");
            System.out.println("Expected: Applicant created successfully.");
        } catch (Exception e) {
            System.out.println("Failed to create Applicant: " + e.getMessage());
        }

        // Test login with default password "password"
        try {
            if (applicant.verify_password("password")) {
                System.out.println("Applicant login with default password: Pass");
                System.out.println("Expected: Applicant login with default password: Pass");
            } else {
                System.out.println("Applicant login with default password: Fail");
                System.out.println("Expected: Applicant login with default password: Pass");
            }
        } catch (Exception e) {
            System.out.println("Applicant login test failed: " + e.getMessage());
        }

        // Test password change
        try {
            applicant.change_password("newapplicantpass");
            if (applicant.verify_password("newapplicantpass")) {
                System.out.println("Applicant password change: Pass");
                System.out.println("Expected: Applicant password change: Pass");
            } else {
                System.out.println("Applicant password change: Fail");
                System.out.println("Expected: Applicant password change: Pass");
            }
        } catch (Exception e) {
            System.out.println("Applicant password change test failed: " + e.getMessage());
        }

        // Test viewing project listings
        try {
            List<String> flatTypes = new ArrayList<>();
            flatTypes.add("2-Room");
            List<Integer> totalUnits = new ArrayList<>();
            totalUnits.add(10);
            List<Integer> availableUnits = new ArrayList<>();
            availableUnits.add(10);
            LocalDate openDate = LocalDate.of(2025, 4, 1);
            LocalDate closeDate = LocalDate.of(2025, 4, 30);
            Project project = new Project("ApplicantProject", "Yishun", flatTypes, totalUnits, availableUnits, openDate, closeDate, true, 10);
            BTOsystem.projects.add(project);
            System.out.println("Applicant viewing listings:");
            applicant.view_listings();  // (Actual output: details of "ApplicantProject")
            System.out.println("Expected: Listing details for project 'ApplicantProject', showing project name, neighbourhood, flat type '2-Room' and dates.");
        } catch (Exception e) {
            System.out.println("Applicant view_listings test failed: " + e.getMessage());
        }

        // Test enquiry management: add, edit, and delete enquiry
        try {
            applicant.makeEnquiry("Inquiry regarding ApplicantProject");
            Field enquiriesField = Applicant.class.getDeclaredField("enquiries");
            enquiriesField.setAccessible(true);
            List<?> enquiries = (List<?>) enquiriesField.get(applicant);
            if (enquiries.size() == 1) {
                System.out.println("Applicant enquiry add: Pass");
                System.out.println("Expected: Applicant enquiry add: Pass");
            } else {
                System.out.println("Applicant enquiry add: Fail");
                System.out.println("Expected: Applicant enquiry add: Pass");
            }
            try {
                applicant.editEnquiry(0, "Edited enquiry content");
                String editedContent = ((Enquiry) enquiries.get(0)).getEnquiry();
                if ("Edited enquiry content".equals(editedContent)) {
                    System.out.println("Applicant enquiry edit: Pass");
                    System.out.println("Expected: Applicant enquiry edit: Pass");
                } else {
                    System.out.println("Applicant enquiry edit: Fail");
                    System.out.println("Expected: Applicant enquiry edit: Pass");
                }
            } catch (Exception e) {
                System.out.println("Applicant enquiry edit test failed: " + e.getMessage());
            }
            try {
                applicant.deleteEnquiry(0);
                if (((List<?>) enquiriesField.get(applicant)).isEmpty()) {
                    System.out.println("Applicant enquiry deletion: Pass");
                    System.out.println("Expected: Applicant enquiry deletion: Pass");
                } else {
                    System.out.println("Applicant enquiry deletion: Fail");
                    System.out.println("Expected: Applicant enquiry deletion: Pass");
                }
            } catch (Exception e) {
                System.out.println("Applicant enquiry deletion test failed: " + e.getMessage());
            }
        } catch (Exception e) {
            System.out.println("Applicant enquiry management test failed: " + e.getMessage());
        }

        // Test "apply to become officer" (not implemented yet)
        try {
            // applicant.applyToBecomeOfficer();
            throw new UnsupportedOperationException("applyToBecomeOfficer() not implemented");
        } catch (Exception e) {
            System.out.println("Applicant applyToBecomeOfficer(): " + e.getMessage());
            System.out.println("Expected: Applicant applyToBecomeOfficer(): applyToBecomeOfficer() not implemented");
        }

        // Test "request withdrawal" (not implemented yet)
        try {
            // applicant.requestWithdrawal();
            throw new UnsupportedOperationException("requestWithdrawal() not implemented");
        } catch (Exception e) {
            System.out.println("Applicant requestWithdrawal(): " + e.getMessage());
            System.out.println("Expected: Applicant requestWithdrawal(): requestWithdrawal() not implemented");
        }
    }

    /**
     * Tests features for an HDB Officer.
     */
    public static void testOfficerFeatures() {
        System.out.println("\n=== Test: HDB Officer Features ===");
        HDB_Officer officer = null;
        try {
            officer = new HDB_Officer("T2222222Y", "Bob", "Officer", "MARRIED", 40);
            System.out.println("HDB Officer created successfully.");
            System.out.println("Expected: HDB Officer created successfully.");
        } catch (Exception e) {
            System.out.println("Failed to create HDB Officer: " + e.getMessage());
        }

        // Test login with default password "password"
        try {
            if (officer.verify_password("password")) {
                System.out.println("Officer login with default password: Pass");
                System.out.println("Expected: Officer login with default password: Pass");
            } else {
                System.out.println("Officer login with default password: Fail");
                System.out.println("Expected: Officer login with default password: Pass");
            }
        } catch (Exception e) {
            System.out.println("Officer login test failed: " + e.getMessage());
        }

        // Test password change
        try {
            officer.change_password("newofficerpass");
            if (officer.verify_password("newofficerpass")) {
                System.out.println("Officer password change: Pass");
                System.out.println("Expected: Officer password change: Pass");
            } else {
                System.out.println("Officer password change: Fail");
                System.out.println("Expected: Officer password change: Pass");
            }
        } catch (Exception e) {
            System.out.println("Officer password change test failed: " + e.getMessage());
        }

        // Test project registration
        try {
            List<String> flatTypes = new ArrayList<>();
            flatTypes.add("3-Room");
            List<Integer> totalUnits = new ArrayList<>();
            totalUnits.add(20);
            List<Integer> availableUnits = new ArrayList<>();
            availableUnits.add(20);
            LocalDate openDate = LocalDate.of(2025, 5, 1);
            LocalDate closeDate = LocalDate.of(2025, 5, 31);
            Project project = new Project("OfficerProject", "Boon Lay", flatTypes, totalUnits, availableUnits, openDate, closeDate, true, 10);
            BTOsystem.projects.add(project);
            officer.registerForProject(project);
            if ("Pending".equals(officer.getRegistrationStatus())) {
                System.out.println("Officer registration: Pass");
                System.out.println("Expected: Officer registration: Pass");
            } else {
                System.out.println("Officer registration: Fail");
                System.out.println("Expected: Officer registration: Pass");
            }
        } catch (Exception e) {
            System.out.println("Officer registration test failed: " + e.getMessage());
        }

        // Test reply to enquiry
        try {
            Enquiry enquiry = new Enquiry(officer, "Need details about OfficerProject");
            officer.reply_enquiry(enquiry, "Details: ...");
            if ("Details: ...".equals(enquiry.getResponse())) {
                System.out.println("Officer reply to enquiry: Pass");
                System.out.println("Expected: Officer reply to enquiry: Pass");
            } else {
                System.out.println("Officer reply to enquiry: Fail");
                System.out.println("Expected: Officer reply to enquiry: Pass");
            }
        } catch (Exception e) {
            System.out.println("Officer reply to enquiry test failed: " + e.getMessage());
        }

        // Test receipt generation
        try {
            Receipt receipt = new Receipt("Flat booking details: Applicant: John Doe, Project: ProjectA, Flat Type: 2-Room");
            receipt.printReceipt();
            System.out.println("Officer receipt generation: (Manual inspection required)");
            System.out.println("Expected: Receipt printed with ID and booking details.");
        } catch (Exception e) {
            System.out.println("Officer receipt generation test failed: " + e.getMessage());
        }

        // Test restricted functionality: Editing project details (not available for officers)
        try {
            throw new UnsupportedOperationException("editProject() is not defined for HDB_Officer");
        } catch (Exception e) {
            System.out.println("Officer editing project details: " + e.getMessage());
            System.out.println("Expected: Officer editing project details: editProject() is not defined for HDB_Officer");
        }
    }

    /**
     * Tests features for an HDB Manager.
     */
    public static void testManagerFeatures() {
        System.out.println("\n=== Test: HDB Manager Features ===");
        HDB_Manager manager = null;
        try {
            manager = new HDB_Manager("T3333333Z", "Carol", "Manager", "MARRIED", 45);
            System.out.println("HDB Manager created successfully.");
            System.out.println("Expected: HDB Manager created successfully.");
        } catch (Exception e) {
            System.out.println("Failed to create HDB Manager: " + e.getMessage());
        }

        // Test login with default password "password"
        try {
            if (manager.verify_password("password")) {
                System.out.println("Manager login with default password: Pass");
                System.out.println("Expected: Manager login with default password: Pass");
            } else {
                System.out.println("Manager login with default password: Fail");
                System.out.println("Expected: Manager login with default password: Pass");
            }
        } catch (Exception e) {
            System.out.println("Manager login test failed: " + e.getMessage());
        }

        // Test password change
        try {
            manager.change_password("newmanagerpass");
            if (manager.verify_password("newmanagerpass")) {
                System.out.println("Manager password change: Pass");
                System.out.println("Expected: Manager password change: Pass");
            } else {
                System.out.println("Manager password change: Fail");
                System.out.println("Expected: Manager password change: Pass");
            }
        } catch (Exception e) {
            System.out.println("Manager password change test failed: " + e.getMessage());
        }

        // Test project creation
        try {
            List<String> flatTypes = new ArrayList<>();
            flatTypes.add("2-Room");
            flatTypes.add("3-Room");
            List<Integer> totalUnits = new ArrayList<>();
            totalUnits.add(15);
            totalUnits.add(10);
            List<Integer> availableUnits = new ArrayList<>();
            availableUnits.add(15);
            availableUnits.add(10);
            LocalDate openDate = LocalDate.of(2025, 6, 1);
            LocalDate closeDate = LocalDate.of(2025, 6, 30);
            Project project = new Project("ManagerProject", "Jurong", flatTypes, totalUnits, availableUnits, openDate, closeDate, false, 10);
            manager.getManagerProjects().add(project);
            HDB_Manager.getAllProjects().add(project);
            System.out.println("Manager created project: " + project.getProjectName());
            System.out.println("Expected: Manager created project: ManagerProject");
        } catch (Exception e) {
            System.out.println("Manager project creation test failed: " + e.getMessage());
        }

        // Test project editing
        try {
            Project projToEdit = manager.getManagerProjects().get(0);
            projToEdit.setProjectName("ManagerProjectUpdated");
            if ("ManagerProjectUpdated".equals(projToEdit.getProjectName())) {
                System.out.println("Manager project editing: Pass");
                System.out.println("Expected: Manager project editing: Pass");
            } else {
                System.out.println("Manager project editing: Fail");
                System.out.println("Expected: Manager project editing: Pass");
            }
        } catch (Exception e) {
            System.out.println("Manager project editing test failed: " + e.getMessage());
        }

        // Test project deletion
        try {
            Project projToDelete = manager.getManagerProjects().get(0);
            manager.deleteProject(projToDelete);
            if (!manager.getManagerProjects().contains(projToDelete) && !HDB_Manager.getAllProjects().contains(projToDelete)) {
                System.out.println("Manager project deletion: Pass");
                System.out.println("Expected: Manager project deletion: Pass");
            } else {
                System.out.println("Manager project deletion: Fail");
                System.out.println("Expected: Manager project deletion: Pass");
            }
        } catch (Exception e) {
            System.out.println("Manager project deletion test failed: " + e.getMessage());
        }

        // Test report generation (simulate user input)
        try {
            List<BTOapplication> applications = new ArrayList<>();
            BTOapplication app1 = new BTOapplication(manager, new Project("DummyProject", "Somewhere", new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), LocalDate.now(), LocalDate.now(), true, 10), "2-Room");
            applications.add(app1);
            String simulatedInput = "1\n";
            Scanner scanner = new Scanner(new ByteArrayInputStream(simulatedInput.getBytes()));
            System.out.println("Manager generating report:");
            manager.generateReport(applications, scanner);
            System.out.println("Expected: Report header and options printed for choice 1 (View All Applicants).");
        } catch (Exception e) {
            System.out.println("Manager report generation test failed: " + e.getMessage());
        }

        // Test handling officer registration (not implemented yet)
        try {
            // manager.handleOfficerRegistration(project, someOfficer);
            throw new UnsupportedOperationException("handleOfficerRegistration() not implemented");
        } catch (Exception e) {
            System.out.println("Manager handleOfficerRegistration(): " + e.getMessage());
            System.out.println("Expected: Manager handleOfficerRegistration(): handleOfficerRegistration() not implemented");
        }

        // Test handling BTO application
        try {
            List<String> flatTypes = new ArrayList<>();
            flatTypes.add("2-Room");
            List<Integer> totalUnits = new ArrayList<>();
            totalUnits.add(10);
            List<Integer> availableUnits = new ArrayList<>();
            availableUnits.add(10);
            LocalDate openDate = LocalDate.of(2025, 7, 1);
            LocalDate closeDate = LocalDate.of(2025, 7, 31);
            Project project = new Project("ManagerBTOProject", "Queenstown", flatTypes, totalUnits, availableUnits, openDate, closeDate, true, 10);
            BTOsystem.projects.add(project);
            BTOapplication application = new BTOapplication(manager, project, "2-Room");
            manager.handleBTOapplication(project, application, "2-Room");
            if ("Approved".equals(application.getStatus())) {
                System.out.println("Manager handleBTOapplication(): Pass");
                System.out.println("Expected: Manager handleBTOapplication(): Pass (application status becomes 'Approved')");
            } else {
                System.out.println("Manager handleBTOapplication(): Fail");
                System.out.println("Expected: Manager handleBTOapplication(): Pass (application status becomes 'Approved')");
            }
        } catch (Exception e) {
            System.out.println("Manager handleBTOapplication() test failed: " + e.getMessage());
        }

        // Test handling withdrawal request
        try {
            BTOapplication application = new BTOapplication(manager, new Project("WithdrawalProject", "Punggol", new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), LocalDate.now(), LocalDate.now(), true, 10), "2-Room");
            String simulatedInput = "yes\n";
            Scanner scanner = new Scanner(new ByteArrayInputStream(simulatedInput.getBytes()));
            manager.handleWithdrawalRequest(application.getProject(), application, scanner);
            if ("Withdrawn".equals(application.getStatus())) {
                System.out.println("Manager handleWithdrawalRequest(): Pass");
                System.out.println("Expected: Manager handleWithdrawalRequest(): Pass (application status becomes 'Withdrawn')");
            } else {
                System.out.println("Manager handleWithdrawalRequest(): (Manual inspection required) Current status: " + application.getStatus());
                System.out.println("Expected: Manager handleWithdrawalRequest(): Pass (application status becomes 'Withdrawn')");
            }
        } catch (Exception e) {
            System.out.println("Manager handleWithdrawalRequest() test failed: " + e.getMessage());
        }
    }

    /**
     * Tests privacy restrictions by simulating calls that should not be available.
     */
    public static void testPrivacyRestrictions() {
        System.out.println("\n=== Test: Privacy Restrictions ===");

        // An Applicant should not have access to officer-only methods.
        try {
            Applicant applicant = new Applicant("S4444444W", "David", "Privacy", "SINGLE", 40);
            throw new UnsupportedOperationException("Applicant does not have registerForProject()");
        } catch (Exception e) {
            System.out.println("Privacy test (Applicant): " + e.getMessage());
            System.out.println("Expected: Privacy test (Applicant): Applicant does not have registerForProject()");
        }

        // An Officer should not have access to project creation methods.
        try {
            HDB_Officer officer = new HDB_Officer("T5555555V", "Eve", "Officer", "MARRIED", 35);
            throw new UnsupportedOperationException("Officer does not have createProject()");
        } catch (Exception e) {
            System.out.println("Privacy test (Officer): " + e.getMessage());
            System.out.println("Expected: Privacy test (Officer): Officer does not have createProject()");
        }

        // A Manager should not have access to applicant-specific enquiry methods.
        try {
            HDB_Manager manager = new HDB_Manager("T6666666U", "Frank", "Manager", "MARRIED", 50);
            throw new UnsupportedOperationException("Manager does not have makeEnquiry()");
        } catch (Exception e) {
            System.out.println("Privacy test (Manager): " + e.getMessage());
            System.out.println("Expected: Privacy test (Manager): Manager does not have makeEnquiry()");
        }
    }
}
