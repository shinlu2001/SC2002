package SC2002.Project.control;

import SC2002.Project.control.persistence.DataStore;
import SC2002.Project.entity.BTOApplication;
import SC2002.Project.entity.HDB_Manager;
import SC2002.Project.entity.Project;
import SC2002.Project.entity.enums.ApplicationStatus;
import SC2002.Project.entity.enums.Visibility;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Controller for manager-specific operations: approving/rejecting applications,
 * handling withdrawals, and managing visibility of managed projects.
 */
public class ManagerController {
    private final DataStore dataStore = DataStore.getInstance();
    private final ApplicationController appController = new ApplicationController();
    private final ProjectController projectController = new ProjectController();
    private final HDB_Manager manager;

    public ManagerController(HDB_Manager manager) {
        this.manager = manager;
    }

    // ---- Approve / Reject BTO Applications ----

    /**
     * Lists all pending applications for projects managed by this manager.
     */
    public List<BTOApplication> getPendingApplications() {
        return manager.getManagedProjects().stream()
            .flatMap(proj -> appController.listApplicationsForProject(proj.getId()).stream())
            .filter(app -> app.getStatus() == ApplicationStatus.PENDING)
            .collect(Collectors.toList());
    }

    /**
     * Finds a pending application by ID, ensuring it's for a project this manager manages.
     */
    public BTOApplication findManagedApplicationById(int appId) {
        BTOApplication app = appController.findById(appId);
        if (app != null
         && app.getStatus() == ApplicationStatus.PENDING
         && manager.getManagedProjects().contains(app.getProject())) {
            return app;
        }
        return null;
    }

    public boolean approveApplication(BTOApplication app) {
        if (app == null
         || app.getStatus() != ApplicationStatus.PENDING
         || !manager.getManagedProjects().contains(app.getProject())) {
            return false;
        }
        return appController.changeStatus(app.getId(), ApplicationStatus.SUCCESS);
    }

    public boolean rejectApplication(BTOApplication app) {
        if (app == null
         || app.getStatus() != ApplicationStatus.PENDING
         || !manager.getManagedProjects().contains(app.getProject())) {
            return false;
        }
        boolean result = appController.changeStatus(app.getId(), ApplicationStatus.REJECTED);
        if (result) {
            app.getApplicant().clearCurrentApplicationReference();
        }
        return result;
    }

    // ---- Handle Withdrawal Requests ----

    /**
     * Lists all withdrawal requests for projects managed by this manager.
     */
    public List<BTOApplication> getWithdrawalRequests() {
        return manager.getManagedProjects().stream()
            .flatMap(proj -> appController.listApplicationsForProject(proj.getId()).stream())
            .filter(BTOApplication::isWithdrawalRequested)
            .collect(Collectors.toList());
    }

    public boolean confirmWithdrawal(BTOApplication app) {
        if (app == null
         || !app.isWithdrawalRequested()
         || !manager.getManagedProjects().contains(app.getProject())
         || app.getStatus() == ApplicationStatus.BOOKED) {
            return false;
        }
        boolean ok = appController.changeStatus(app.getId(), ApplicationStatus.WITHDRAWN);
        if (ok) {
            app.getApplicant().finalizeWithdrawal();
        }
        return ok;
    }

    public boolean rejectWithdrawalRequest(BTOApplication app) {
        if (app == null
         || !app.isWithdrawalRequested()
         || !manager.getManagedProjects().contains(app.getProject())) {
            return false;
        }
        app.setWithdrawalRequested(false);
        return true;
    }

    // ---- Project Management ----

    /** Create a new project */
    public Project createProject(String name, String neighbourhood, List<String> flatTypes,
                                 List<Integer> totalUnits, List<Double> prices, LocalDate open,
                                 LocalDate close, boolean visible, int officerSlots) {
        // Delegate creation to ProjectController, passing the current manager
        return projectController.createProject(name, neighbourhood, flatTypes, totalUnits, prices,
                                               open, close, visible, officerSlots, this.manager);
    }

    /** Edit an existing project - delegates checks and updates to ProjectController */
    public boolean renameProject(int projectId, String newName) {
        if (!isManagerOfProject(projectId)) return false;
        return projectController.renameProject(projectId, newName);
    }

    public boolean changeNeighbourhood(int projectId, String newHood) {
        if (!isManagerOfProject(projectId)) return false;
        return projectController.changeNeighbourhood(projectId, newHood);
    }

    public boolean updateFlatTypeUnits(int projectId, String flatType, int newUnits) {
        if (!isManagerOfProject(projectId)) return false;
        return projectController.updateFlatTypeUnits(projectId, flatType, newUnits);
    }

    public boolean addFlatType(int projectId, String flatType, int units, double price) {
        if (!isManagerOfProject(projectId)) return false;
        return projectController.addFlatType(projectId, flatType, units, price);
    }

    public boolean removeFlatType(int projectId, String flatType) {
        if (!isManagerOfProject(projectId)) return false;
        return projectController.removeFlatType(projectId, flatType);
    }

    public boolean updateFlatPrice(int projectId, String flatType, double newPrice) {
        if (!isManagerOfProject(projectId)) return false;
        return projectController.updateFlatPrice(projectId, flatType, newPrice);
    }

    public boolean setOpenDate(int projectId, LocalDate openDate) {
        if (!isManagerOfProject(projectId)) return false;
        return projectController.setOpenDate(projectId, openDate);
    }

    public boolean setCloseDate(int projectId, LocalDate closeDate) {
        if (!isManagerOfProject(projectId)) return false;
        return projectController.setCloseDate(projectId, closeDate);
    }

    public boolean setVisibility(int projectId, Visibility state) {
        if (!isManagerOfProject(projectId)) return false;
        return projectController.setVisibility(projectId, state);
    }

    public boolean setOfficerSlotLimit(int projectId, int newLimit) {
        if (!isManagerOfProject(projectId)) return false;
        return projectController.setOfficerSlotLimit(projectId, newLimit);
    }

    /** Delete a project managed by this manager */
    public boolean deleteProject(int projectId) {
        // Delegate deletion to ProjectController, passing the manager for validation
        return projectController.deleteProject(projectId, this.manager);
    }

    /** Find a project managed by this manager by its ID */
    public Project findManagedProjectById(int projectId) {
        Project p = projectController.findById(projectId);
        if (p != null && p.getManager().equals(this.manager)) {
            return p;
        }
        return null;
    }

    // ---- Project Visibility & Listing ----

    /** List all projects in the system */
    public List<Project> listAllProjects() {
        return projectController.listAll();
    }

    /** List projects managed by this manager */
    public List<Project> listMyProjects() {
        return List.copyOf(manager.getManagedProjects());
    }

    /** Toggle visibility on/off for a managed project */
    public boolean toggleProjectVisibility(int projectId) {
        Project p = projectController.findById(projectId);
        if (p == null || !manager.getManagedProjects().contains(p)) return false;
        Visibility newState = (p.getVisibility() == Visibility.ON) ? Visibility.OFF : Visibility.ON;
        return projectController.setVisibility(projectId, newState);
    }

    /** Helper to check if the manager manages the project */
    private boolean isManagerOfProject(int projectId) {
        Project p = projectController.findById(projectId);
        return p != null && p.getManager().equals(this.manager);
    }

    // …plus stubs for other features: handle officer regs, generate reports…
    // TODO: Implement methods for officer registration management (view, handle approval/rejection, handle withdrawal)
    // TODO: Implement methods for enquiry management (view all, handle project-specific)
}
