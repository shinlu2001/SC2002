package SC2002.Project.entity;

import SC2002.Project.entity.enums.MaritalStatus;
import SC2002.Project.util.IdGenerator;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents an HDB Officer user in the BTO system.
 * <p>
 * HDB Officers extend the Applicant class and possess all applicant
 * capabilities
 * while also having additional responsibilities for:
 * <ul>
 * <li>Registering to join specific BTO projects</li>
 * <li>Managing project details and responding to enquiries</li>
 * <li>Helping applicants with flat selection and booking</li>
 * <li>Generating receipts for flat bookings</li>
 * </ul>
 * </p>
 * <p>
 * Fulfills the following Officer requirements:
 * <ul>
 * <li>Possesses all Applicant capabilities</li>
 * <li>Can register to join projects not applying to as an Applicant</li>
 * <li>Cannot be an Officer for multiple projects with overlapping application
 * periods</li>
 * <li>Can view the status of registration to be an Officer for a project</li>
 * <li>Registration is subject to Manager approval</li>
 * <li>Can view project details regardless of visibility settings</li>
 * <li>Can respond to project enquiries</li>
 * <li>Handles flat selection for approved applications (updates availability,
 * application status)</li>
 * <li>Generates receipts for flat bookings</li>
 * </ul>
 * </p>
 */
public class HDB_Officer extends Applicant {
    private final int officerId;
    private final List<Registration> registrations = new ArrayList<>();
    private final List<Project> assignedProjects = new ArrayList<>();

    /**
     * Constructs a new HDB Officer with a unique Officer ID.
     * 
     * @param nric          The officer's NRIC (unique identifier)
     * @param firstName     The officer's first name
     * @param lastName      The officer's last name (can be empty)
     * @param maritalStatus The officer's marital status
     * @param age           The officer's age
     */
    public HDB_Officer(String nric, String firstName, String lastName,
            MaritalStatus maritalStatus, int age) {
        super(nric, firstName, lastName, maritalStatus, age);
        this.officerId = IdGenerator.nextOfficerId();
    }

    /**
     * Gets the officer's unique ID.
     * 
     * @return This officer's unique ID
     */
    public int getOfficerId() {
        return officerId;
    }

    // --- Project Assignment Methods ---

    /**
     * Gets the list of projects this officer is assigned to.
     * This supports the requirement that officers can view details
     * of projects they're handling regardless of visibility.
     * 
     * @return An unmodifiable list of assigned projects
     */
    public List<Project> getAssignedProjects() {
        return List.copyOf(assignedProjects);
    }

    /**
     * Assigns a project to this officer.
     * Called when a registration is approved by a manager.
     * 
     * @param project The project to assign to this officer
     */
    public void addAssignedProject(Project project) {
        if (!assignedProjects.contains(project)) {
            assignedProjects.add(project);
        }
    }

    /**
     * Removes a project assignment from this officer.
     * Called when a registration is withdrawn or rejected.
     * 
     * @param project The project to remove from this officer's assignments
     */
    public void removeAssignedProject(Project project) {
        assignedProjects.remove(project);
    }

    // --- Registration Methods ---

    /**
     * Gets all project registrations submitted by this officer.
     * This supports the requirement for officers to see their
     * registration status for projects.
     * 
     * @return An unmodifiable list of registrations
     */
    public List<Registration> getRegistrations() {
        return List.copyOf(registrations);
    }

    /**
     * Adds a new registration to this officer's registrations list.
     * Called when the officer registers interest in handling a project.
     * 
     * @param reg The registration to add
     */
    public void addRegistration(Registration reg) {
        if (!registrations.contains(reg)) {
            registrations.add(reg);
        }
    }

    /**
     * Returns a string representation of the HDB Officer.
     * 
     * @return A string with the officer's details
     */
    @Override
    public String toString() {
        return String.format("Officer %s %s | NRIC: %s | Age: %d | Marital: %s",
                getFirstName(), getLastName(), getNric(), getAge(), getMaritalStatus());
    }
}
