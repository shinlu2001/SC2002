package SC2002.Project.boundary;

import java.util.Scanner;

import SC2002.Project.Applicant;
import SC2002.Project.Input;

public interface BTOService {
    static void applyProject(Scanner sc, Applicant a) {}
    static void displayApplication(Scanner sc, Applicant a) {
        if (a.getApplication() == null) {
            System.out.println("You have no active application. Please create a new application.");
        } else {
            a.getApplication().get_details();
            if (a.getApplication().getStatus().equalsIgnoreCase("Successful")) {
                System.out.println("Congrats! Your application is successful!");
                System.out.println("Enter 1 to book a flat (any other key to exit): ");
                int book = Input.getIntInput(sc);
                if (book == 1) {
                    System.out.println("Your request to book a flat has been submitted.");
                    System.out.println("Our friendly HDB officer will assist you in the booking of a flat");
                    a.getApplication().requestBooking();
                }
            }
        }
    }
}