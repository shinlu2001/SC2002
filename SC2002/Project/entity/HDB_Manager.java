package SC2002.Project.entity;

import java.util.ArrayList;
import java.util.List;

public class HDB_Manager extends User {
    protected static int nextId = -1;
    private final int managerID;
    private String type = "MANAGER";
    protected List<Project> managerProjects = new ArrayList<>();
    
    public HDB_Manager(String nric, String firstname, String lastname, String maritalStatus, int age) {
        super(nric, firstname, lastname, maritalStatus, age);
        this.managerID = ++nextId;
    }
    
    public int getManagerID() { return managerID; }
    public List<Project> getManagerProjects() { return managerProjects; }
    public void addProject(Project project) { managerProjects.add(project); }
    
    @Override
    public void printDetails() {
        super.printDetails();
        System.out.println("Account type: " + type);
        System.out.println("Manager ID: " + managerID);
    }
    
    // UI logic removed.
}
