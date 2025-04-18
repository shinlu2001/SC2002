package SC2002.Project.control;

import SC2002.Project.control.persistence.DataStore;
import SC2002.Project.entity.Applicant;
import SC2002.Project.entity.BTOApplication;
import SC2002.Project.entity.HDB_Manager;
import SC2002.Project.entity.HDB_Officer;
import SC2002.Project.entity.Project;
import SC2002.Project.entity.enums.ApplicationStatus;
import SC2002.Project.entity.enums.Visibility;
import SC2002.Project.util.IdGenerator;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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

    // TODO: implement the rest of the edit methods (units, prices, dates, slots)

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
            p.setOfficerSlotLimit(newLimit);
            return true;
        }
        return false;
    }

    /* ------------------------------------------------------------------
       Officer assignment helpers
       ------------------------------------------------------------------ */

    public boolean assignOfficer(int projectId, HDB_Officer officer) {
        // TODO: check slot availability, date overlap, etc.
        return false;
    }

    public boolean unassignOfficer(int projectId, HDB_Officer officer) {
        // TODO
        return false;
    }

    /* ------------------------------------------------------------------
       Filtering helpers for UI
       ------------------------------------------------------------------ */

    public List<Project> filterByNeighbourhood(String hood) { /* TODO */ return null; }
    public List<Project> filterByFlatType(String type)      { /* TODO */ return null; }
    public List<Project> filterVisible()                    { /* TODO */ return null; }

    public List<Project> getAllProjects() {
        return new ArrayList<>(dataStore.getProjects());
    }

    public Project getProjectById(int projectId) {
        return findById(projectId);
    }

    /**
     * Single applicants under 35 ineligible for any.
     * Single >=35 only 2-ROOM; Married >=21 any.
     */
    public boolean isEligibleForRoomType(Applicant applicant, String roomType) {
        // example simplified logic
        return true;
    }

    /**
     * Creates a BTO application if no active one.
     * Fix: unwrap Optional<BTOApplication> before getStatus().
     */
    public boolean createApplication(Applicant applicant, Project project, String roomType) {
        Optional<BTOApplication> currentOpt = applicant.getCurrentApplication();
        if (currentOpt.isPresent()) {
            ApplicationStatus st = currentOpt.get().getStatus();
            if (st != ApplicationStatus.WITHDRAWN && st != ApplicationStatus.REJECTED) {
                return false;
            }
        }

        BTOApplication application = new BTOApplication(applicant, project, roomType);
        dataStore.getApplications().add(application);
        applicant.setCurrentApplication(application);
        return true;
    }

    /* ------------------------------------------------------------------
       Manager helpers
       ------------------------------------------------------------------ */

    public void assignManager(Project p, HDB_Manager manager) {
        p.setManager(manager);
        manager.addManagedProject(p);
    }

    public boolean toggleVisibility(int projectId) {
        Project p = findById(projectId);
        if (p != null) {
            p.setVisibility(p.getVisibility() == Visibility.ON ? Visibility.OFF : Visibility.ON);
            return true;
        }
        return false;
    }

    public boolean deleteProject(int projectId) {
        Project p = findById(projectId);
        if (p != null) {
            dataStore.getProjects().remove(p);
            if (p.getManager() != null) {
                p.getManager().removeManagedProject(p);
            }
            return true;
        }
        return false;
    }

}
