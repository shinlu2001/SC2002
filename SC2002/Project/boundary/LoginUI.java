// SC2002/Project/boundary/LoginUI.java
package SC2002.Project.boundary;

import java.util.Scanner;
import SC2002.Project.control.AuthController;
import SC2002.Project.entity.*;

public class LoginUI {
    private final AuthController auth = AuthController.getInstance();
    private final Scanner sc = new Scanner(System.in);

    public void start() {
        while (true) {
            System.out.println("===== LOGIN (type exit to quit) =====");
            System.out.print("NRIC: ");
            String nric = sc.nextLine().trim();
            if (nric.equalsIgnoreCase("exit")) return;
            System.out.print("Password: ");
            String pwd  = sc.nextLine().trim();
            User u = auth.login(nric, pwd);
            if (u==null){ System.out.println("Invalid credentials\n"); continue; }

            if (u instanceof HDB_Manager m)   new ManagerUI(m, sc).menu();
            else if (u instanceof HDB_Officer o) new OfficerUI(o, sc).menu();
            else if (u instanceof Applicant a)   new ApplicantUI(a, sc).menu();
        }
    }
}
