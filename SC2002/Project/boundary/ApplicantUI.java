// SC2002/Project/boundary/role/ApplicantUI.java
package SC2002.Project.boundary;

import java.util.Scanner;
import SC2002.Project.entity.Applicant;

public class ApplicantUI {
    private final Applicant user;
    private final Scanner sc;
    public ApplicantUI(Applicant u, Scanner sc){ this.user=u; this.sc=sc; }

    public void menu(){ user.start_menu(sc); }   // reuse old loop
}
