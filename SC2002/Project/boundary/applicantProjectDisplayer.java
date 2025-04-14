package SC2002.Project.boundary;

import java.util.ArrayList;
import java.util.List;

import SC2002.Project.Applicant;
import SC2002.Project.ApplicantBase;
import SC2002.Project.BTOsystem;
import SC2002.Project.HDB_Officer;
import SC2002.Project.Project;

public class applicantProjectDisplayer implements ProjectDisplayer {
    private ApplicantBase applicant;
    public applicantProjectDisplayer(ApplicantBase applicant) {
        this.applicant = applicant;
    }
    public void view_listings() { 
        System.out.println("\n===================================================================================================================");
        System.out.println("                                                  ALL PROJECTS");
        System.out.println("===================================================================================================================");
        System.out.printf("%-5s %-20s %-15s %-15s %-10s %-15s %-15s %-10s %n", "ID", "Project Name", "Neighbourhood", "Flat Types", "Price", "Open Date", "Close Date", "Eligibilty");
        System.err.println("-------------------------------------------------------------------------------------------------------------------");

        List<Project> list = BTOsystem.projects;

        if (applicant instanceof Applicant) {
            for (Project p : list) {
                if (p.isVisible()) {
                    System.out.print(viewProjectsApplicant(p));
                }
            }        
        } else if (applicant instanceof HDB_Officer officer) { //down-cast to officer so it works
            for (Project p : list) {
                // Display the project if it is visible or if the officer is registered for it.
                if (p.isVisible() || officer.isRegisteredForProject(p)) {
                    System.out.print(viewProjectsApplicant(p));
                }
            }
            
        }
        System.err.println("-------------------------------------------------------------------------------------------------------------------");
    }

    public String viewProjectsApplicant(Project p) {
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("%-5s %-20s %-15s %-15s %-10s %-15s %-15s %-10s %n",
                p.getId(),
                p.getProjectName(),
                p.getneighbourhood(),
                (p.getFlatTypes().size() > 0)
                        ? p.getFlatTypes().get(0) + ": " + (p.getTotalUnits().get(0) - p.getAvailableUnits().get(0)) + "/" + p.getTotalUnits().get(0)
                        : "",
                p.getFlatTypes().size() > 0 ? p.getFlatPrice(p.getFlatTypes().get(0)) : 0,
                p.getOpenDate(),
                p.getCloseDate(),
                applicant.getEligibility(p.getFlatTypes().get(0)) ? "Eligible" : "Not Eligible"
        ));
        for (int i = 1; i < p.getFlatTypes().size(); i++) {
            sb.append(String.format("%-5s %-20s %-15s %-15s %-10s %-15s %-15s %-10s %n",
                    "", "", "",
                    p.getFlatTypes().get(i) + ": " + (p.getTotalUnits().get(i) - p.getAvailableUnits().get(i)) + "/" + p.getTotalUnits().get(i),
                    p.getFlatTypes().size() > 0 ? p.getFlatPrice(p.getFlatTypes().get(i)) : 0,
                    "", "", applicant.getEligibility(p.getFlatTypes().get(i)) ? "Eligible" : "Not Eligible"));
        }
        sb.append("\n");
        return sb.toString();
    }

    public int view_eligible_listings() {
        System.out.println("                                                  ELIGIBLE PROJECTS");
        System.out.println("====================================================================================================================");
        System.out.printf("%-5s %-20s %-15s %-15s %-10s %-15s %-15s %-10s %n", "ID", "Project Name", "Neighbourhood", "Flat Types", "Price", "Open Date", "Close Date", "Eligibilty");
        System.err.println("--------------------------------------------------------------------------------------------------------------------");
        List<Project> list = BTOsystem.projects;
        int count = 0;
        if (applicant instanceof Applicant) {
            for (Project p : list) {
                if (p.isVisible()) {
                    String str = viewEligibleProjectsApplicant(p);
                    if (!str.isBlank()) {
                        count++;
                    }
                    System.out.print(str);
                }
            }            
        } else if (applicant instanceof HDB_Officer officer) { //down-cast to officer so it works
            for (Project p : list) {
                // Exclude project if officer is already registered for it.
                if (p.isVisible() && !officer.isRegisteredForProject(p)) {
                    String str = viewEligibleProjectsApplicant(p);
                    if (!str.isBlank()) {
                        count++;
                    }
                    System.out.print(str);
                }
            }
            
        }
        System.out.println("----------------------------------------------------------------------------------------------------------------");
        return count;
    }

    public String viewEligibleProjectsApplicant(Project p) {
        StringBuilder sb = new StringBuilder();
        List<String[]> eflatType = new ArrayList<>();
        for (String ft : p.getFlatTypes()) {
            if (applicant.getEligibility(ft)) {
                eflatType.add(new String[]{ft, String.valueOf(p.getFlatTypes().indexOf(ft))});
            }
        }
        if (!eflatType.isEmpty()) {
            sb.append(String.format("%-5s %-20s %-15s %-15s %-10s %-15s %-15s %-10s %n",
                    p.getId(),
                    p.getProjectName(),
                    p.getneighbourhood(),
                    (eflatType.size() > 0 && applicant.getEligibility(p.getFlatTypes().get(0)))
                            ? eflatType.get(0)[0] + ": " + (p.getTotalUnits().get(Integer.parseInt(eflatType.get(0)[1]))
                            - p.getAvailableUnits().get(Integer.parseInt(eflatType.get(0)[1]))) + "/" + p.getTotalUnits().get(Integer.parseInt(eflatType.get(0)[1]))
                            : "",
                    p.getFlatTypes().size() > 0 ? p.getFlatPrice(eflatType.get(0)[0]) : 0,
                    p.getOpenDate(),
                    p.getCloseDate(),
                    "Eligible"
            ));
        }
        for (int i = 1; i < eflatType.size(); i++) {
            sb.append(String.format("%-5s %-20s %-15s %-15s %-10s %-15s %-15s %-10s %n",
                    "", "", "",
                    eflatType.get(i)[0] + ": " + (p.getTotalUnits().get(Integer.parseInt(eflatType.get(i)[1]))
                            - p.getAvailableUnits().get(Integer.parseInt(eflatType.get(i)[1]))) + "/" + p.getTotalUnits().get(Integer.parseInt(eflatType.get(i)[1])),
                    p.getFlatTypes().size() > 0 ? p.getFlatPrice(eflatType.get(i)[0]) : 0,
                    "", "", "Eligible"));
        }
        sb.append("\n");
        return sb.toString();
    }
}
