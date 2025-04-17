// SC2002/Project/boundary/role/ManagerUI.java
package SC2002.Project.boundary;

import java.util.Scanner;
import SC2002.Project.entity.HDB_Manager;

public class ManagerUI {
    private final HDB_Manager user;
    private final Scanner sc;
    public ManagerUI(HDB_Manager u, Scanner sc){ this.user=u; this.sc=sc; }

    public void menu(){ user.start_menu(sc); }
}
