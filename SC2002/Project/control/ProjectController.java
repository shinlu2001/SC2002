package SC2002.Project.control;

import SC2002.Project.control.persistence.DataStore;
import SC2002.Project.entity.Enquiry;
import SC2002.Project.entity.HDB_Manager;
import SC2002.Project.entity.HDB_Officer;
import SC2002.Project.entity.Project;
import SC2002.Project.entity.enums.Visibility;
import SC2002.Project.util.IdGenerator;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Handles creation, mutation, and queries of Project objects.
 * All heavy business logic goes here; Project remains a data holder.
 */
public class ProjectController {

    private final DataStore dataStore;

    public ProjectController() {
        this.dataStore = DataStore.getInstance();
    }

    /* ------------------------------------------------------------------
       Creation
       ------------------------------------------------------------------ */

    public Project createProject(String name,
                                 String neighbourhood,
                                 List<String> flatTypes,
                                 List<Integer> totalUnits,
                                 List<Double> prices,
                                 LocalDate open,
                                 LocalDate close,
                                 boolean visible,
                                 int officerSlots,
                                 HDB_Manager manager)
    {
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
                 System.out.println("Error: Failed to create " + name + ". You can only manage one project within an application period.");
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
            officerSlots
        );
        p.setManager(manager);
        manager.addManagedProject(p);
        dataStore.getProjects().add(p);
        return p;
    }

    /* ------------------------------------------------------------------
       Simple getters / finders
       ------------------------------------------------------------------ */

    public Project findById(int id) {
        return dataStore.getProjects().stream()
                        .filter(p -> p.getId() == id)
                        .findFirst()
                        .orElse(null);
    }

    public List<Project> listAll() {
        return List.copyOf(dataStore.getProjects());
    }

    /* ------------------------------------------------------------------
       Editing operations
       ------------------------------------------------------------------ */

    public boolean renameProject(int projectId, String newName) {
        Project p = findById(projectId);
        if (p != null) {
            p.setName(newName);
            return true;
        }
        return false;
    }

    public boolean changeNeighbourhood(int projectId, String newHood) {
        Project p = findById(projectId);
        if (p != null) {
            p.setNeighbourhood(newHood);
            return true;
        }
        return false;
    }

    public boolean updateFlatTypeUnits(int projectId, String flatType, int newUnits) {
        Project p = findById(projectId);
        if (p != null) {
            return p.updateFlatTypeUnits(flatType, newUnits);
        }
        return false;
    }

    public boolean addFlatType(int projectId, String flatType, int units, double price) {
        Project p = findById(projectId);
        if (p != null) {
            return p.addFlatType(flatType, units, price);
        }
        return false;
    }

    public boolean removeFlatType(int projectId, String flatType) {
        Project p = findById(projectId);
        if (p != null) {
            int index = p.getFlatTypes().indexOf(flatType);
            if (index != -1) {
                int currentUnits = p.getTotalUnits().get(index);
                return p.removeFlatType(flatType, currentUnits);
            }
        }
        return false;
    }

    public boolean updateFlatPrice(int projectId, String flatType, double newPrice) {
        Project p = findById(projectId);
        if (p != null) {
            return p.updateFlatPrice(flatType, newPrice);
        }
        return false;
    }

    public boolean setOpenDate(int projectId, LocalDate openDate) {
        Project p = findById(projectId);
        if (p != null) {
            // Validate that new openDate is not after closeDate
            if (openDate.isAfter(p.getCloseDate())) {
                System.out.println("Error: Failed to update opening date. Opening date cannot be after the closing date.");
                return false;
            }
            p.setOpenDate(openDate);
            return true;
        }
        return false;
    }

    public boolean setCloseDate(int projectId, LocalDate closeDate) {
        Project p = findById(projectId);
        if (p != null) {
            // Validate that new closeDate is not before openDate
            if (closeDate.isBefore(p.getOpenDate())) {
                System.out.println("Error: Failed to update closing date. Closing date cannot be before the opening date.");
                return false;
            }
            p.setCloseDate(closeDate);
            return true;
        }
        return false;
    }

    public boolean setVisibility(int projectId, Visibility state) {
        Project p = findById(projectId);
        if (p != null) {
            p.setVisibility(state);
            return true;
        }
        return false;
    }

    public boolean setOfficerSlotLimit(int projectId, int newLimit) {
        Project p = findById(projectId);
        if (p != null) {
            if (newLimit <= 0 || newLimit > Project.MAX_OFFICER_SLOTS) {
                System.out.println("Error: Available officer slots must be between 1 and " + Project.MAX_OFFICER_SLOTS + ".");
                return false;
            }
            p.setOfficerSlotLimit(newLimit);
            return true;
        }
        return false;
    }

    // enquiry matters
    public List<Enquiry> getEnquiries(int projectId) {
        Project project = findById(projectId);
        return project.getEnquiries();
    }

    public void addEnquiry(Project project, Enquiry en) {
        // Project project = findById(projectId);
        project.getEnquiries().add(en);
    }

    public void deleteEnquiry(Project project, Enquiry en) {
        // Project project = findById(projectId);
        project.getEnquiries().remove(en);
    }

    /* ------------------------------------------------------------------
       Officer assignment helpers
       ------------------------------------------------------------------ */

    /**
     * Checks if an officer can be assigned to a project
     * @param projectId The ID of the project
     * @param officer The officer to check
     * @return A validation result: true if assignment is possible, false otherwise (with error messaging)
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
            System.err.println("Error: Officer " + officer.getNric() + " is already assigned to project " + project.getName() + ".");
            return false;
        }

        // Check for overlapping assignment periods for the officer
        LocalDate projectOpen = project.getOpenDate();
        LocalDate projectClose = project.getCloseDate();
        
        for (Project assignedProject : officer.getAssignedProjects()) {
            LocalDate assignedOpen = assignedProject.getOpenDate();
            LocalDate assignedClose = assignedProject.getCloseDate();
            
            // Check if the new project's period overlaps with an existing one for this officer
            if (!(projectClose.isBefore(assignedOpen) || projectOpen.isAfter(assignedClose))) {
                System.err.println("Error: Failed to assign officer " + officer.getNric() + " to " + 
                    project.getName() + ". Officer has an overlapping project assignment with " +
                    assignedProject.getName() + " (" + assignedOpen + " to " + assignedClose + ").");
                return false;
            }
        }

        return true;
    }

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
            System.out.println("Error: Officer " + officer.getNric() + " is already assigned to project " + project.getName() + ".");
            return false;
        }

        // Check for overlapping assignment periods for the officer
        LocalDate projectOpen = project.getOpenDate();
        LocalDate projectClose = project.getCloseDate();
        for (Project assignedProject : officer.getAssignedProjects()) {
            LocalDate assignedOpen = assignedProject.getOpenDate();
            LocalDate assignedClose = assignedProject.getCloseDate();
            // Check if the new project's period overlaps with an existing one for this officer
            if (!(projectClose.isBefore(assignedOpen) || projectOpen.isAfter(assignedClose))) {
                 System.out.println("Error: Failed to assign officer " + officer.getNric() + " to " + project.getName() + ". Officer has an overlapping project assignment.");
                 return false; // Indicate failure due to overlap
            }
        }

        // Assign officer to project and vice versa
        project.addAssignedOfficer(officer);
        officer.addAssignedProject(project);
        System.out.println("Successfully assigned officer " + officer.getNric() + " to project " + project.getName() + ".");
        return true;
    }

    public boolean unassignOfficer(int projectId, HDB_Officer officer) {
        Project project = findById(projectId);
        if (project == null || officer == null) {
            System.out.println("Error: Project or Officer not found.");
            return false;
        }

        // Check if the officer is actually assigned to this project
        if (!project.getAssignedOfficers().contains(officer)) {
            System.out.println("Error: Officer " + officer.getNric() + " is not assigned to project " + project.getName() + ".");
            return false;
        }

        // Unassign officer from project and vice versa
        project.removeAssignedOfficer(officer);
        officer.removeAssignedProject(project);
        System.out.println("Successfully unassigned officer " + officer.getNric() + " from project " + project.getName() + ".");
        return true;
    }

    /* ------------------------------------------------------------------
       Filtering helpers for UI
       ------------------------------------------------------------------ */

    public List<Project> filterByNeighbourhood(String hood) {
        return dataStore.getProjects().stream()
                        .filter(p -> p.getNeighbourhood().equalsIgnoreCase(hood))
                        .collect(Collectors.toList());
    }

    public List<Project> filterByFlatType(String type) {
        return dataStore.getProjects().stream()
                        .filter(p -> p.getFlatTypes().stream().anyMatch(ft -> ft.equalsIgnoreCase(type)))
                        .collect(Collectors.toList());
    }

    public List<Project> filterVisible() {
        return dataStore.getProjects().stream()
                        .filter(p -> p.getVisibility() == Visibility.ON)
                        .collect(Collectors.toList());
    }

    /* ------------------------------------------------------------------
       Manager helpers
       ------------------------------------------------------------------ */

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
            if (existingProject == p) continue; // Skip self-comparison
            
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

    public boolean toggleVisibility(int projectId) {
        Project p = findById(projectId);
        if (p != null) {
            p.setVisibility(p.getVisibility() == Visibility.ON ? Visibility.OFF : Visibility.ON);
            return true;
        }
        return false;
    }

    public boolean deleteProject(int projectId, HDB_Manager manager) {
        Project p = findById(projectId);
        if (p != null && p.getManager().equals(manager)) {
            manager.removeManagedProject(p);
            dataStore.getProjects().remove(p);
            return true;
        }
        return false;
    }

}
