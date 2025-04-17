// SC2002/Project/boundary/role/OfficerUI.java
package SC2002.Project.boundary;

import java.util.Scanner;
import SC2002.Project.entity.HDB_Officer;

public class OfficerUI {
    private final HDB_Officer user;
    private final Scanner sc;
    public OfficerUI(HDB_Officer u, Scanner sc){ this.user=u; this.sc=sc; }

    public void menu(){ user.start_menu(sc); }
}
