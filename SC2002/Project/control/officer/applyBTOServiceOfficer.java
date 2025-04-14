package SC2002.Project.control.officer;

import java.util.Scanner;

import SC2002.Project.BTOapplication;
import SC2002.Project.BTOsystem;
import SC2002.Project.HDB_Officer;
import SC2002.Project.Input;
import SC2002.Project.Project;
import SC2002.Project.boundary.BTOService;
import SC2002.Project.boundary.applicantProjectDisplayer;

public class applyBTOServiceOfficer implements BTOService {
    static void applyProject(Scanner sc, HDB_Officer a, applicantProjectDisplayer projectDisplayer) { // different from applicant as there is an additional condition (cannot be assigned to the project)
        if (a.getApplication() != null) {
            System.out.println("You already have an active application. You may not create a new one.");
        } else {
            System.out.println("Apply for a project");
            int count = projectDisplayer.view_eligible_listings();
            if (count == 0) {
                System.out.println("You are not eligible to apply for any project.");
                return;
            }
            System.out.println("Enter ProjectID: ");
            int id = Input.getIntInput(sc);
            Project p = BTOsystem.searchById(BTOsystem.projects, id, Project::getId);
            if (p == null || !p.isVisible()) {
                System.out.println("No such project.");
            } else {
                if (a.isRegisteredForProject(p)) {
                    System.out.println("You are already registered as an officer for this project and cannot apply as an applicant.");
                    return;
                }
                System.out.println("Enter room type (2-Room, 3-Room, etc): ");
                String roomtype = Input.getStringInput(sc);
                if (!a.getEligibility(roomtype)) {
                    System.out.println("Not eligible for this project and room type.");
                } else {
                    BTOapplication b = new BTOapplication(a, p, roomtype);
                    BTOsystem.applications.add(b);
                    System.out.println("Application submitted!");
                    a.setApplication(b);
                }
            }
        }
    }
}
