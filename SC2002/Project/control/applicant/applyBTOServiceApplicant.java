package SC2002.Project.control.applicant;

import java.util.Scanner;

import SC2002.Project.ApplicantBase;
import SC2002.Project.BTOapplication;
import SC2002.Project.BTOsystem;
import SC2002.Project.Input;
import SC2002.Project.Project;
import SC2002.Project.boundary.applicantProjectDisplayer;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class applyBTOServiceApplicant {
    private ApplicantBase a;
    LocalDate today = LocalDate.now();
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("d/M/yyyy");
    String formattedToday    = today.format(formatter);

    public applyBTOServiceApplicant(ApplicantBase a) {
        this.a = a;
    }
    public void applyProject(Scanner sc, ApplicantBase a, applicantProjectDisplayer displayer) {
        if (a.getApplication() == null || a.getApplication().getStatus().equals("WITHDRAWN" ) || a.getApplication().getStatus().equals("REJECTED")) {
            if (a.getApplication() != null) {
                a.getApplicationHistory().add(a.getApplication());
            }
            System.out.println("Apply for a project");
            int count = displayer.view_eligible_listings();
            if (count == 0) {
                System.out.println("You are not eligible to apply for any project.");
                return;
            }
            System.out.println("Enter ProjectID: ");
            int id = Input.getIntInput(sc);
            Project p = BTOsystem.searchById(BTOsystem.projects, id, Project::getId);
            if (p == null || !p.isVisible()) {
                System.out.println("No such project.");
            } else if (today.isBefore(p.getOpenDate()) || today.isAfter(p.getCloseDate())){
                System.out.println("Not in application period.");
            }
            else {
                System.out.println("Enter room type (2-Room, 3-Room, etc): ");
                String roomtype = Input.getStringInput(sc);
                if (!a.getEligibility(roomtype)) {
                    System.out.println("Not eligible for this project and room type.");
                } else {
                    BTOapplication b = new BTOapplication(a, p, roomtype.toUpperCase());
                    BTOsystem.applications.add(b);
                    System.out.println("Application submitted!");
                    a.setApplication(b);
                }
            }
        } else {
            System.out.println("You already have an active application. You may not create a new one." + a.getApplication().getStatus());
        }
    }
}
