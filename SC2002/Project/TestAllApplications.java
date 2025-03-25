package SC2002.Project;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

public class TestAllApplications {
    public static void main(String[] args) {
        // Create a DateTimeFormatter to match "15/2/2025" format (day/month/year)
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("d/M/yyyy");
        LocalDate openDate = LocalDate.parse("15/2/2025", formatter);
        LocalDate closeDate = LocalDate.parse("20/3/2025", formatter);

        // Create a manager (using name Jessica from the project example)
        HDB_Manager manager = new HDB_Manager("S1111111A", "Jessica", "Tan", "Married", 40);

        // Parse the assigned officers from the string "Daniel,Emily"
        List<String> assignedOfficers = new ArrayList<>(Arrays.asList("Daniel", "Emily"));

        // Create a sample project using your CSV example data
        Project project = new Project(
            "Acacia Breeze",    // Project Name
            "Yishun",           // Neighborhood
            "2-Room",           // First flat type
            2,                  // Units for 2-Room
            "3-Room",           // Second flat type
            3,                  // Units for 3-Room
            450000.0,           // Price for 3-Room
            openDate,           // Application opening date
            closeDate,          // Application closing date
            manager,            // Manager in charge
            3,                  // Available HDB Officer Slots
            assignedOfficers    // Assigned officers list
        );

        // Create a sample applicant from your CSV example "John,S1234567A,35,Single"
        Applicant applicant1 = new Applicant("S1234567A", "John", "Doe", "Single", 35);
        
        // You can create more applicants if needed
        Applicant applicant2 = new Applicant("T2345678B", "Alice", "Lim", "Married", 42);

        // Create sample BTO applications
        BTOapplication app1 = new BTOapplication(applicant1, project, "2-Room");
        app1.setStatus("Booked");  // Mark this application as booked so it shows up in the report

        BTOapplication app2 = new BTOapplication(applicant2, project, "3-Room");
        app2.setStatus("Booked");

        // Create a list of applications to simulate the allApplications field
        List<BTOapplication> allApplications = new ArrayList<>();
        allApplications.add(app1);
        allApplications.add(app2);

        // Create a Scanner to read input from the console
        Scanner sc = new Scanner(System.in);

        // Call generateReport from the manager instance, passing our sample applications list
        manager.generateReport(allApplications, sc);

        sc.close();
    }
}
