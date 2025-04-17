package SC2002.Project.entity;

import java.util.*;
import java.util.Scanner;

import SC2002.Project.boundary.ManagerUI;
import SC2002.Project.entity.enums.MaritalStatus;

public class HDB_Manager extends User {
    private final Set<Project> managed = new HashSet<>();

    public HDB_Manager(String n, String f, String l, MaritalStatus ms, int age) {
        super(n, f, l, ms, age);
    }

    /** Called by CSVReader to link this manager to a project. */
    public void addProject(Project prj) {
        managed.add(prj);
    }

    /** For UI/reporting: returns a snapshot of this manager’s projects. */
    public List<Project> getMyProjects() {
        return new ArrayList<>(managed);
    }

    /** Entry point when this user logs in. */
    public static void start_menu(Scanner sc) {
        ManagerUI.start(sc);
    }
}
