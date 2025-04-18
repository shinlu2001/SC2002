// SC2002/Project/control/ReportController.java
package SC2002.Project.control;

import SC2002.Project.control.persistence.DataStore;
import SC2002.Project.entity.*;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Controller class for generating and managing reports.
 */
public class ReportController {
    private final DataStore dataStore;

    public ReportController() {
        this.dataStore = DataStore.getInstance();
    }

    public List<BTOApplication> getAllApplications(HDB_Manager manager) {
        return dataStore.getApplications().stream()
                .filter(app -> app.getProject().getManager().equals(manager))
                .collect(Collectors.toList());
    }

    public List<BTOApplication> filterByMaritalStatus(HDB_Manager manager, String status) {
        return getAllApplications(manager).stream()
                .filter(app -> ((Applicant)app.getApplicant()).getMaritalStatus().equals(status))
                .collect(Collectors.toList());
    }

    public List<BTOApplication> filterByFlatType(HDB_Manager manager, String flatType) {
        return getAllApplications(manager).stream()
                .filter(app -> app.getRoomType().equals(flatType))
                .collect(Collectors.toList());
    }

    public List<BTOApplication> filterByBoth(HDB_Manager manager, String status, String flatType) {
        return getAllApplications(manager).stream()
                .filter(app -> ((Applicant)app.getApplicant()).getMaritalStatus().equals(status)
                        && app.getRoomType().equals(flatType))
                .collect(Collectors.toList());
    }

    public Report generateReport(List<BTOApplication> applications) {
        StringBuilder content = new StringBuilder();
        content.append("Application Report\n");
        content.append("=================\n");
        
        // Add summary statistics
        content.append(String.format("Total Applications: %d\n", applications.size()));
        
        long singleCount = applications.stream()
                .filter(app -> ((Applicant)app.getApplicant()).getMaritalStatus().equals("SINGLE"))
                .count();
        content.append(String.format("Single Applicants: %d\n", singleCount));
        
        long marriedCount = applications.stream()
                .filter(app -> ((Applicant)app.getApplicant()).getMaritalStatus().equals("MARRIED"))
                .count();
        content.append(String.format("Married Applicants: %d\n", marriedCount));
        
        // Add detailed application list
        content.append("\nDetailed Application List:\n");
        content.append("-------------------------\n");
        for (BTOApplication app : applications) {
            Applicant applicant = (Applicant) app.getApplicant();
            content.append(String.format("Application ID: %d\n", app.getId()));
            content.append(String.format("Applicant: %s %s\n", 
                    applicant.getFirstName(), applicant.getLastName()));
            content.append(String.format("Age: %d\n", applicant.getAge()));
            content.append(String.format("Marital Status: %s\n", applicant.getMaritalStatus()));
            content.append(String.format("Project: %s\n", app.getProject().getName()));
            content.append(String.format("Flat Type: %s\n", app.getRoomType()));
            content.append(String.format("Status: %s\n", app.getStatus()));
            content.append("-------------------------\n");
        }

        return new Report(content.toString());
    }
}
