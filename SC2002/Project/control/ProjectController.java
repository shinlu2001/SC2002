package SC2002.Project.control;

import java.time.LocalDate;
import java.util.*;

import SC2002.Project.control.persistence.DataStore;
import SC2002.Project.entity.*;
import SC2002.Project.entity.enums.Visibility;

/**
 * Handles creation, mutation, and queries of Project objects.
 * All heavy business logic goes here; Project remains a data holder.
 */
public class ProjectController {

    private final DataStore ds = DataStore.getInstance();

    /* ------------------------------------------------------------------
       Creation
       ------------------------------------------------------------------ */

    public Project createProject(String name,
                                 String neighbourhood,
                                 List<String> flatTypes,
                                 List<Integer> totalUnits,
                                 List<Double>  prices,
                                 LocalDate open,
                                 LocalDate close,
                                 boolean visible,
                                 int officerSlots,
                                 HDB_Manager manager)
    {
        // TODO validation (unique name, manager active‑period rule, etc.)
        Project p = new Project(name, neighbourhood,
                                flatTypes, totalUnits, prices,
                                open, close, visible, officerSlots);
        p.setManager(manager);
        manager.addProject(p);
        ds.getProjects().add(p);
        return p;
    }

    /* ------------------------------------------------------------------
       Simple getters / finders
       ------------------------------------------------------------------ */

    public Project findById(int id) {
        // TODO
        return null;
    }

    public List<Project> listAll() {
        return List.copyOf(ds.getProjects());
    }

    /* ------------------------------------------------------------------
       Editing operations (mirror Menu.editProject submenu)
       ------------------------------------------------------------------ */

    public boolean renameProject(int projectId, String newName) {
        // TODO
        return false;
    }

    public boolean changeNeighbourhood(int projectId, String newHood) {
        // TODO
        return false;
    }

    public boolean updateUnitCount(int projectId, String flatType, int newTotal) {
        // TODO adjust availableUnits sensibly
        return false;
    }

    public boolean addNewFlatType(int projectId, String type, int units, double price) {
        // TODO
        return false;
    }

    public boolean removeFlatType(int projectId, String type) {
        // TODO
        return false;
    }

    public boolean changeFlatPrice(int projectId, String type, double newPrice) {
        // TODO
        return false;
    }

    public boolean updateOpenDate(int projectId, LocalDate newDate) {
        // TODO
        return false;
    }

    public boolean updateCloseDate(int projectId, LocalDate newDate) {
        // TODO
        return false;
    }

    public boolean setVisibility(int projectId, Visibility state) {
        // TODO
        return false;
    }

    public boolean setOfficerSlotLimit(int projectId, int newLimit) {
        // TODO
        return false;
    }

    /* ------------------------------------------------------------------
       Officer assignment helpers
       ------------------------------------------------------------------ */

    public boolean assignOfficer(int projectId, HDB_Officer officer) {
        // TODO check slot availability etc.
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
}
