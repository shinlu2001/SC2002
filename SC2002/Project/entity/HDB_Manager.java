package SC2002.Project.entity;

import SC2002.Project.entity.enums.MaritalStatus;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents an HDB Manager user in the BTO system.
 */
public class HDB_Manager extends User {
    private final int managerId;
    private final List<Project> managedProjects;

    public HDB_Manager(String nric, String firstName, String lastName, MaritalStatus maritalStatus, int age) {
        super(nric, firstName, lastName, maritalStatus, age);
        // Assuming ID generation is handled elsewhere or not needed for managers
        // this.managerId = IdGenerator.nextManagerId(); // If needed
        this.managerId = -1; // Placeholder if ID not strictly needed
        this.managedProjects = new ArrayList<>();
    }

    public int getManagerId() {
        return managerId;
    }

    /**
     * Gets the list of projects managed by this manager.
     * @return A modifiable list of managed projects.
     */
    public List<Project> getManagedProjects() {
        return managedProjects; // Return the actual list for modification
    }

    public void addManagedProject(Project project) {
        if (!managedProjects.contains(project)) {
            managedProjects.add(project);
            project.setManager(this); // Ensure back-reference is set
        }
    }

    public void removeManagedProject(Project project) {
        managedProjects.remove(project);
        // Optionally clear the manager from the project if needed
        // if (project.getManager() == this) { project.setManager(null); }
    }
}
