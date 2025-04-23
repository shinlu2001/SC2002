package SC2002.Project.entity;

import SC2002.Project.entity.enums.MaritalStatus;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents an HDB Manager user in the BTO system.
 * <p>
 * HDB Managers have the highest level of authority in the system, responsible
 * for:
 * <ul>
 * <li>Creating, editing, and deleting BTO project listings</li>
 * <li>Managing project visibility</li>
 * <li>Approving/rejecting officer registrations for projects</li>
 * <li>Approving/rejecting applicant BTO applications</li>
 * <li>Managing withdrawal requests</li>
 * <li>Generating reports</li>
 * <li>Responding to enquiries</li>
 * </ul>
 * </p>
 * <p>
 * Fulfills the following Manager requirements:
 * <ul>
 * <li>Can create, edit, and delete BTO project listings</li>
 * <li>Can only handle one project within an application period</li>
 * <li>Can toggle project visibility (on/off)</li>
 * <li>Can view all projects regardless of visibility</li>
 * <li>Can filter and view their own created projects</li>
 * <li>Can manage officer registrations (approve/reject)</li>
 * <li>Can manage applicant applications (approve/reject)</li>
 * <li>Can manage withdrawal requests (approve/reject)</li>
 * <li>Can generate reports with various filters</li>
 * <li>Cannot apply for BTO projects as an Applicant</li>
 * <li>Can view and respond to all project enquiries</li>
 * </ul>
 * </p>
 */
public class HDB_Manager extends User {
    private final int managerId;
    private final List<Project> managedProjects;

    /**
     * Constructs a new HDB Manager.
     * 
     * @param nric          The manager's NRIC (unique identifier)
     * @param firstName     The manager's first name
     * @param lastName      The manager's last name (can be empty)
     * @param maritalStatus The manager's marital status
     * @param age           The manager's age
     */
    public HDB_Manager(String nric, String firstName, String lastName, MaritalStatus maritalStatus, int age) {
        super(nric, firstName, lastName, maritalStatus, age);
        // Assuming ID generation is handled elsewhere or not needed for managers
        // this.managerId = IdGenerator.nextManagerId(); // If needed
        this.managerId = -1; // Placeholder if ID not strictly needed
        this.managedProjects = new ArrayList<>();
    }

    /**
     * Gets the manager's unique ID.
     * 
     * @return This manager's unique ID
     */
    public int getManagerId() {
        return managerId;
    }

    /**
     * Gets the list of projects managed by this manager.
     * This supports the requirement that managers can filter
     * and view the list of projects they have created.
     * 
     * @return A modifiable list of managed projects
     */
    public List<Project> getManagedProjects() {
        return managedProjects; // Return the actual list for modification
    }

    /**
     * Adds a project to this manager's list of managed projects.
     * This is called when a manager creates a new project.
     * Supports the requirement that managers can create BTO project listings.
     * 
     * @param project The project to add to the manager's portfolio
     */
    public void addManagedProject(Project project) {
        if (!managedProjects.contains(project)) {
            managedProjects.add(project);
            project.setManager(this); // Ensure back-reference is set
        }
    }

    /**
     * Removes a project from this manager's list of managed projects.
     * This supports the requirement that managers can delete project listings.
     * 
     * @param project The project to remove from the manager's portfolio
     */
    public void removeManagedProject(Project project) {
        managedProjects.remove(project);
        // Optionally clear the manager from the project if needed
        // if (project.getManager() == this) { project.setManager(null); }
    }
}
