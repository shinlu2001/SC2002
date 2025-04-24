package SC2002.Project.control;

import SC2002.Project.control.persistence.DataStore;
import SC2002.Project.entity.BTOApplication;
import SC2002.Project.entity.Enquiry;
import SC2002.Project.entity.Flat;
import SC2002.Project.entity.HDB_Manager;
import SC2002.Project.entity.HDB_Officer;
import SC2002.Project.entity.Project;
import SC2002.Project.entity.enums.ApplicationStatus;
import SC2002.Project.entity.enums.Visibility;
import SC2002.Project.util.IdGenerator;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;
import java.util.ArrayList;

/**
 * Controller for managing BTO housing projects in the system.
 * <p>
 * This controller handles all operations related to projects:
 * <ul>
 * <li>Creating new projects with proper validation</li>
 * <li>Managing project details and flat types</li>
 * <li>Controlling project visibility to applicants</li>
 * <li>Officer assignment and management</li>
 * <li>Project enquiry management</li>
 * </ul>
 * </p>
 * <p>
 * Fulfills the following requirements:
 * <ul>
 * <li>Allows managers to create and manage BTO projects</li>
 * <li>Supports flat type and unit management</li>
 * <li>Controls officer assignment to projects with proper validation</li>
 * <li>Manages project visibility to control which projects applicants can
 * view</li>
 * <li>Validates project dates and prevents scheduling conflicts</li>
 * <li>Provides filtering capabilities for project discovery</li>
 * </ul>
 * </p>
 */
public class ProjectController {

    private final DataStore dataStore;

    /**
     * Creates a new ProjectController instance.
     * Initializes the controller with access to the central data store.
     */
    public ProjectController() {
        this.dataStore = DataStore.getInstance();
    }

    /*
     * ------------------------------------------------------------------
     * Creation
     * ------------------------------------------------------------------
     */

    /**
     * Creates a new BTO housing project with the specified details.
     * This fulfills the requirement for managers to create projects.
     * Performs validation to ensure project dates don't overlap for a manager
     * and that close date is after open date.
     * 
     * @param name          Project name
     * @param neighbourhood Location of the project
     * @param flatTypes     List of flat types offered (e.g., "2-ROOM", "3-ROOM")
     * @param totalUnits    Number of units available for each flat type
     * @param prices        Price of each flat type
     * @param open          Opening date for applications
     * @param close         Closing date for applications
     * @param visible       Whether the project is visible to applicants
     * @param officerSlots  Number of officer slots available for this project
     * @param manager       Manager responsible for this project
     * @return The created Project object, or null if creation failed
     */
    public Project createProject(String name,
            String neighbourhood,
            List<String> flatTypes,
            List<Integer> totalUnits,
            List<Double> prices,
            LocalDate open,
            LocalDate close,
            boolean visible,
            int officerSlots,
            HDB_Manager manager) {
        // Validate that close date is after open date
        if (close.isBefore(open)) {
            System.out.println("Error: Failed to create " + name + ". Closing date must be after the opening date.");
            return null;
        }

        // Check for overlapping project management periods for the same manager
        for (Project existingProject : manager.getManagedProjects()) {
            LocalDate existingOpen = existingProject.getOpenDate();
            LocalDate existingClose = existingProject.getCloseDate();
            // Check if the new project's period overlaps with an existing one
            if (!(close.isBefore(existingOpen) || open.isAfter(existingClose))) {
                System.out.println("Error: Failed to create " + name
                        + ". You can only manage one project within an application period.");
                return null; // Indicate failure due to overlap
            }
        }

        int id = IdGenerator.nextProjectId();
        Project p = new Project(
                id,
                name,
                neighbourhood,
                flatTypes,
                totalUnits,
                prices,
                open,
                close,
                visible ? Visibility.ON : Visibility.OFF,
                officerSlots);
        p.setManager(manager);
        manager.addManagedProject(p);
        dataStore.getProjects().add(p);
        return p;
    }

    /*
     * ------------------------------------------------------------------
     * Simple getters / finders
     * ------------------------------------------------------------------
     */

    /**
     * Finds a project by its unique ID.
     * 
     * @param id The ID of the project to find
     * @return The project with the specified ID, or null if not found
     */
    public Project findById(int id) {
        return dataStore.getProjects().stream()
                .filter(p -> p.getId() == id)
                .findFirst()
                .orElse(null);
    }

    /**
     * Lists all projects in the system.
     * Provides a copy to prevent modification of the underlying list.
     * 
     * @return An unmodifiable list of all projects
     */
    public List<Project> listAll() {
        return List.copyOf(dataStore.getProjects());
    }

    /*
     * ------------------------------------------------------------------
     * Editing operations
     * ------------------------------------------------------------------
     */

    /**
     * Updates the name of an existing project.
     * Ensures the change is propagated to all related applications and bookings.
     * 
     * @param projectId The ID of the project to update
     * @param newName   The new name for the project
     * @return True if successful, false if project not found
     */
    public boolean renameProject(int projectId, String newName) {
        Project p = findById(projectId);
        if (p != null) {
            String oldName = p.getName();
            p.setName(newName);
            System.out.println("Project name updated from '" + oldName + "' to '" + newName + "'");
            return true;
        }
        return false;
    }

    /**
     * Updates the neighbourhood (location) of an existing project.
     * 
     * @param projectId The ID of the project to update
     * @param newHood   The new neighbourhood for the project
     * @return True if successful, false if project not found
     */
    public boolean changeNeighbourhood(int projectId, String newHood) {
        Project p = findById(projectId);
        if (p != null) {
            p.setNeighbourhood(newHood);
            return true;
        }
        return false;
    }

    /**
     * Updates the number of units available for a specific flat type.
     * Ensures that units can't be reduced below the number already booked.
     * 
     * @param projectId The ID of the project to update
     * @param flatType  The flat type to update (e.g., "2-ROOM")
     * @param newUnits  The new number of units available
     * @return True if successful, false if project or flat type not found
     */
    public boolean updateFlatTypeUnits(int projectId, String flatType, int newUnits) {
        Project p = findById(projectId);
        if (p != null) {
            int index = p.getFlatTypeIndex(flatType.toUpperCase());
            if (index != -1) {
                int totalUnits = p.getTotalUnits().get(index);
                int availableUnits = p.getAvailableUnits().get(index);
                int bookedUnits = totalUnits - availableUnits;

                // Ensure we don't reduce below the number already booked
                if (newUnits < bookedUnits) {
                    System.out.println("Error: Cannot reduce units below the number already booked (" +
                            bookedUnits + " units currently booked)");
                    return false;
                }

                return p.updateFlatTypeUnits(flatType, newUnits);
            }
            System.err.println("Error: Flat type " + flatType + " not found in project " + p.getName());
            return false;
        }
        return false;
    }

    /**
     * Adds a new flat type to an existing project.
     * 
     * @param projectId The ID of the project to update
     * @param flatType  The flat type to add (e.g., "4-ROOM")
     * @param units     The number of units available for this flat type
     * @param price     The price for this flat type
     * @return True if successful, false if project not found or flat type already
     *         exists
     */
    public boolean addFlatType(int projectId, String flatType, int units, double price) {
        Project p = findById(projectId);
        if (p != null) {
            return p.addFlatType(flatType, units, price);
        }
        return false;
    }

    /**
     * Removes a flat type from an existing project.
     * Ensures all applications for this flat type are updated to REJECTED status.
     * 
     * @param projectId The ID of the project to update
     * @param flatType  The flat type to remove
     * @return True if successful, false if project or flat type not found
     */
    public boolean removeFlatType(int projectId, String flatType) {
        Project p = findById(projectId);
        if (p != null) {
            int index = p.getFlatTypes().indexOf(flatType.toUpperCase());
            if (index != -1) {
                // Find all applications for this project and flat type
                List<BTOApplication> affectedApplications = dataStore.getApplications().stream()
                        .filter(app -> app.getProject().getId() == projectId
                                && app.getRoomType().equalsIgnoreCase(flatType)
                                && (app.getStatus() == ApplicationStatus.PENDING
                                        || app.getStatus() == ApplicationStatus.SUCCESS
                                        || app.getStatus() == ApplicationStatus.BOOKED))
                        .collect(Collectors.toList());

                // If there are active applications, reject them
                if (!affectedApplications.isEmpty()) {
                    System.out.println("Warning: Rejecting " + affectedApplications.size() +
                            " active applications for " + flatType + " in project " + p.getName());
                    for (BTOApplication app : affectedApplications) {
                        // For booked applications, first cancel the booking
                        if (app.getStatus() == ApplicationStatus.BOOKED && app.getBookedFlat() != null) {
                            Flat bookedFlat = app.getBookedFlat();
                            bookedFlat.unbook(); // Clean up flat booking state

                            // Remove the flat reference and directly set status to REJECTED
                            app.setBookedFlat(null);
                            app.setStatus(ApplicationStatus.REJECTED);

                            // Increment available units (already handled by unbook)
                            p.incrementAvailableUnits(flatType);
                        } else {
                            // For non-booked applications, just set to REJECTED
                            app.setStatus(ApplicationStatus.REJECTED);
                        }
                    }
                }

                int currentUnits = p.getTotalUnits().get(index);
                return p.removeFlatType(flatType, currentUnits);
            }
        }
        return false;
    }

    /**
     * Updates the price of a specific flat type.
     * 
     * @param projectId The ID of the project to update
     * @param flatType  The flat type to update
     * @param newPrice  The new price for this flat type
     * @return True if successful, false if project or flat type not found
     */
    public boolean updateFlatPrice(int projectId, String flatType, double newPrice) {
        Project p = findById(projectId);
        if (p != null) {
            return p.updateFlatPrice(flatType, newPrice);
        }
        return false;
    }

    /**
     * Sets the application opening date for a project.
     * Validates that the open date is not after the close date.
     * 
     * @param projectId The ID of the project to update
     * @param openDate  The new opening date for applications
     * @return True if successful, false if project not found or date invalid
     */
    public boolean setOpenDate(int projectId, LocalDate openDate) {
        Project p = findById(projectId);
        if (p != null) {
            // Validate that new openDate is not after closeDate
            if (openDate.isAfter(p.getCloseDate())) {
                System.out.println(
                        "Error: Failed to update opening date. Opening date cannot be after the closing date.");
                return false;
            }
            p.setOpenDate(openDate);
            return true;
        }
        return false;
    }

    /**
     * Sets the application closing date for a project.
     * Validates that the close date is not before the open date.
     * 
     * @param projectId The ID of the project to update
     * @param closeDate The new closing date for applications
     * @return True if successful, false if project not found or date invalid
     */
    public boolean setCloseDate(int projectId, LocalDate closeDate) {
        Project p = findById(projectId);
        if (p != null) {
            // Validate that new closeDate is not before openDate
            if (closeDate.isBefore(p.getOpenDate())) {
                System.out.println(
                        "Error: Failed to update closing date. Closing date cannot be before the opening date.");
                return false;
            }
            p.setCloseDate(closeDate);
            return true;
        }
        return false;
    }

    /**
     * Sets the visibility of a project to control whether applicants can see it.
     * Fulfills the requirement for managers to control project visibility.
     * 
     * @param projectId The ID of the project to update
     * @param state     The new visibility state (ON or OFF)
     * @return True if successful, false if project not found
     */
    public boolean setVisibility(int projectId, Visibility state) {
        Project p = findById(projectId);
        if (p != null) {
            p.setVisibility(state);
            return true;
        }
        return false;
    }

    /**
     * Sets the maximum number of officers that can be assigned to a project.
     * 
     * @param projectId The ID of the project to update
     * @param newLimit  The new officer slot limit
     * @return True if successful, false if project not found or limit invalid
     */
    public boolean setOfficerSlotLimit(int projectId, int newLimit) {
        Project p = findById(projectId);
        if (p != null) {
            if (newLimit <= 0 || newLimit > Project.MAX_OFFICER_SLOTS) {
                System.out.println(
                        "Error: Available officer slots must be between 1 and " + Project.MAX_OFFICER_SLOTS + ".");
                return false;
            }
            p.setOfficerSlotLimit(newLimit);
            return true;
        }
        return false;
    }

    /**
     * Gets all enquiries for a specific project.
     * Fulfills the requirement for officers to view project-specific enquiries.
     * 
     * @param projectId The ID of the project
     * @return List of enquiries for the specified project
     */
    public List<Enquiry> getEnquiries(int projectId) {
        Project project = findById(projectId);
        return project.getEnquiries();
    }

    /**
     * Adds an enquiry to a project.
     * 
     * @param project The project to add the enquiry to
     * @param en      The enquiry to add
     */
    public void addEnquiry(Project project, Enquiry en) {
        project.getEnquiries().add(en);
    }

    /**
     * Deletes an enquiry from a project.
     * 
     * @param project The project to remove the enquiry from
     * @param en      The enquiry to remove
     */
    public void deleteEnquiry(Project project, Enquiry en) {
        project.getEnquiries().remove(en);
    }

    /*
     * ------------------------------------------------------------------
     * Officer assignment helpers
     * ------------------------------------------------------------------
     */

    /**
     * Checks if an officer can be assigned to a project without actually assigning
     * them.
     * Validates officer slot availability and checks for scheduling conflicts.
     * 
     * @param projectId The ID of the project
     * @param officer   The officer to check
     * @return True if assignment is possible, false otherwise (with error
     *         messaging)
     */
    public boolean canAssignOfficer(int projectId, HDB_Officer officer) {
        Project project = findById(projectId);
        if (project == null || officer == null) {
            System.err.println("Error: Project or Officer not found.");
            return false;
        }

        // Check slot availability
        if (project.getAssignedOfficers().size() >= project.getOfficerSlotLimit()) {
            System.err.println("Error: No available officer slots for project " + project.getName() +
                    ". Current: " + project.getAssignedOfficers().size() +
                    ", Maximum: " + project.getOfficerSlotLimit());
            return false;
        }

        // Check if officer is already assigned to this project
        if (project.getAssignedOfficers().contains(officer)) {
            System.err.println("Error: Officer " + officer.getNric() + " is already assigned to project "
                    + project.getName() + ".");
            return false;
        }

        // Check for overlapping assignment periods for the officer
        LocalDate projectOpen = project.getOpenDate();
        LocalDate projectClose = project.getCloseDate();

        for (Project assignedProject : officer.getAssignedProjects()) {
            LocalDate assignedOpen = assignedProject.getOpenDate();
            LocalDate assignedClose = assignedProject.getCloseDate();

            // Check if the new project's period overlaps with an existing one for this
            // officer
            if (!(projectClose.isBefore(assignedOpen) || projectOpen.isAfter(assignedClose))) {
                System.err.println("Error: Failed to assign officer " + officer.getNric() + " to " +
                        project.getName() + ". Officer has an overlapping project assignment with " +
                        assignedProject.getName() + " (" + assignedOpen + " to " + assignedClose + ").");
                return false;
            }
        }

        return true;
    }

    /**
     * Assigns an officer to a project after validating the assignment.
     * Fulfills the requirement for managers to assign officers to projects.
     * 
     * @param projectId The ID of the project
     * @param officer   The officer to assign
     * @return True if assignment was successful, false otherwise
     */
    public boolean assignOfficer(int projectId, HDB_Officer officer) {
        Project project = findById(projectId);
        if (project == null || officer == null) {
            System.out.println("Error: Project or Officer not found.");
            return false;
        }

        // Check slot availability
        if (project.getAssignedOfficers().size() >= project.getOfficerSlotLimit()) {
            System.out.println("Error: No available officer slots for project " + project.getName() + ".");
            return false;
        }

        // Check if officer is already assigned to this project
        if (project.getAssignedOfficers().contains(officer)) {
            System.out.println("Error: Officer " + officer.getNric() + " is already assigned to project "
                    + project.getName() + ".");
            return false;
        }

        // Check for overlapping assignment periods for the officer
        LocalDate projectOpen = project.getOpenDate();
        LocalDate projectClose = project.getCloseDate();
        for (Project assignedProject : officer.getAssignedProjects()) {
            LocalDate assignedOpen = assignedProject.getOpenDate();
            LocalDate assignedClose = assignedProject.getCloseDate();
            // Check if the new project's period overlaps with an existing one for this
            // officer
            if (!(projectClose.isBefore(assignedOpen) || projectOpen.isAfter(assignedClose))) {
                System.out.println("Error: Failed to assign officer " + officer.getNric() + " to " + project.getName()
                        + ". Officer has an overlapping project assignment.");
                return false; // Indicate failure due to overlap
            }
        }

        // Assign officer to project and vice versa
        project.addAssignedOfficer(officer);
        officer.addAssignedProject(project);
        System.out.println(
                "Successfully assigned officer " + officer.getNric() + " to project " + project.getName() + ".");
        return true;
    }

    /**
     * Unassigns an officer from a project.
     * Fulfills the requirement for managers to manage officer assignments.
     * 
     * @param projectId The ID of the project
     * @param officer   The officer to unassign
     * @return True if unassignment was successful, false otherwise
     */
    public boolean unassignOfficer(int projectId, HDB_Officer officer) {
        Project project = findById(projectId);
        if (project == null || officer == null) {
            System.out.println("Error: Project or Officer not found.");
            return false;
        }

        // Check if the officer is actually assigned to this project
        if (!project.getAssignedOfficers().contains(officer)) {
            System.out.println(
                    "Error: Officer " + officer.getNric() + " is not assigned to project " + project.getName() + ".");
            return false;
        }

        // Unassign officer from project and vice versa
        project.removeAssignedOfficer(officer);
        officer.removeAssignedProject(project);
        System.out.println(
                "Successfully unassigned officer " + officer.getNric() + " from project " + project.getName() + ".");
        return true;
    }

    /*
     * ------------------------------------------------------------------
     * Filtering helpers for UI
     * ------------------------------------------------------------------
     */

    /**
     * Filters projects by neighbourhood/location.
     * Fulfills the requirement for applicants to search projects by location.
     * 
     * @param hood The neighbourhood to filter by
     * @return List of projects in the specified neighbourhood
     */
    public List<Project> filterByNeighbourhood(String hood) {
        return dataStore.getProjects().stream()
                .filter(p -> p.getNeighbourhood().equalsIgnoreCase(hood))
                .collect(Collectors.toList());
    }

    /**
     * Filters projects by flat type.
     * Fulfills the requirement for applicants to search projects by flat type.
     * 
     * @param type The flat type to filter by (e.g., "3-ROOM")
     * @return List of projects offering the specified flat type
     */
    public List<Project> filterByFlatType(String type) {
        return dataStore.getProjects().stream()
                .filter(p -> p.getFlatTypes().stream().anyMatch(ft -> ft.equalsIgnoreCase(type)))
                .collect(Collectors.toList());
    }

    /**
     * Filters projects to show only visible ones.
     * Fulfills the requirement that applicants can only see projects with
     * visibility set to ON.
     * 
     * @return List of visible projects
     */
    public List<Project> filterVisible() {
        return dataStore.getProjects().stream()
                .filter(p -> p.getVisibility() == Visibility.ON)
                .collect(Collectors.toList());
    }

    /*
     * ------------------------------------------------------------------
     * Manager helpers
     * ------------------------------------------------------------------
     */

    /**
     * Assigns a manager to a project after validation.
     * Checks for overlapping project management periods.
     * 
     * @param p       The project to assign a manager to
     * @param manager The manager to assign
     */
    public void assignManager(Project p, HDB_Manager manager) {
        if (p == null) {
            System.err.println("Error: Cannot assign manager - project is null");
            return;
        }

        if (manager == null) {
            System.err.println("Error: Cannot assign manager - manager is null");
            return;
        }

        // Check for overlapping project management periods for the manager
        LocalDate projectOpen = p.getOpenDate();
        LocalDate projectClose = p.getCloseDate();

        for (Project existingProject : manager.getManagedProjects()) {
            if (existingProject == p)
                continue; // Skip self-comparison

            LocalDate existingOpen = existingProject.getOpenDate();
            LocalDate existingClose = existingProject.getCloseDate();

            // Check for overlap
            if (!(projectClose.isBefore(existingOpen) || projectOpen.isAfter(existingClose))) {
                System.err.println("Error: Cannot assign manager " + manager.getNric() +
                        " to project " + p.getName() + ". Manager already manages project " +
                        existingProject.getName() + " during an overlapping period.");
                return;
            }
        }

        // If previous checks pass, assign the manager
        p.setManager(manager);
        manager.addManagedProject(p);
        System.out.println("Manager " + manager.getFirstName() + " " +
                manager.getLastName() + " successfully assigned to project " + p.getName());
    }

    /**
     * Toggles the visibility of a project (ON ↔ OFF).
     * Fulfills the requirement for managers to control which projects are visible
     * to applicants.
     * 
     * @param projectId The ID of the project to toggle
     * @return True if successful, false if project not found
     */
    public boolean toggleVisibility(int projectId) {
        Project p = findById(projectId);
        if (p != null) {
            p.setVisibility(p.getVisibility() == Visibility.ON ? Visibility.OFF : Visibility.ON);
            return true;
        }
        return false;
    }

    /**
     * Deletes a project from the system.
     * Verifies that the manager requesting deletion is the one assigned to the
     * project, and updates all related applications to REJECTED status.
     * 
     * @param projectId The ID of the project to delete
     * @param manager   The manager requesting deletion
     * @return True if successful, false if project not found or manager mismatch
     */
    public boolean deleteProject(int projectId, HDB_Manager manager) {
        Project p = findById(projectId);
        if (p != null && p.getManager().equals(manager)) {
            // Find all applications for this project
            List<BTOApplication> affectedApplications = dataStore.getApplications().stream()
                    .filter(app -> app.getProject().getId() == projectId
                            && (app.getStatus() == ApplicationStatus.PENDING
                                    || app.getStatus() == ApplicationStatus.SUCCESS
                                    || app.getStatus() == ApplicationStatus.BOOKED))
                    .collect(Collectors.toList());

            // If there are active applications, reject them
            if (!affectedApplications.isEmpty()) {
                System.out.println("Warning: Rejecting " + affectedApplications.size() +
                        " active applications for project " + p.getName());
                for (BTOApplication app : affectedApplications) {
                    // Handle BOOKED applications specially
                    if (app.getStatus() == ApplicationStatus.BOOKED && app.getBookedFlat() != null) {
                        Flat bookedFlat = app.getBookedFlat();
                        bookedFlat.unbook(); // Properly clean up flat booking state
                        app.cancelBooking(); // This properly resets the application's booking state
                    }
                    app.reject(); // Finally set status to REJECTED
                }
            }

            // Remove assigned officers from this project
            for (HDB_Officer officer : new ArrayList<>(p.getAssignedOfficers())) {
                officer.removeAssignedProject(p);
            }

            // Remove all flats associated with this project
            dataStore.getFlats().removeIf(flat -> flat.getProject().equals(p));

            // Remove all registrations associated with this project
            dataStore.getRegistrations().removeIf(reg -> reg.getProject().equals(p));

            // Remove from manager and datastore
            manager.removeManagedProject(p);
            dataStore.getProjects().remove(p);
            System.out.println("Project '" + p.getName() + "' successfully deleted");
            return true;
        }
        return false;
    }
}
