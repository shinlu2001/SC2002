package SC2002.Project.control;

import SC2002.Project.control.persistence.DataStore;
import SC2002.Project.entity.BTOApplication;
import SC2002.Project.entity.HDB_Manager;
import SC2002.Project.entity.Project;
import SC2002.Project.entity.enums.ApplicationStatus;
import SC2002.Project.entity.enums.Visibility;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Controller for manager-specific operations: approving/rejecting applications,
 * handling withdrawals, and managing visibility of managed projects.
 */
public class ManagerController {
    private final DataStore dataStore = DataStore.getInstance();
    private final ApplicationController appController = new ApplicationController();
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

    // ---- Project Visibility & Listing ----

    /** List all projects in the system */
    public List<Project> listAllProjects() {
        return dataStore.getProjects();
    }

    /** List projects managed by this manager */
    public List<Project> listMyProjects() {
        return manager.getManagedProjects();
    }

    /** Toggle visibility on/off for a managed project */
    public boolean toggleProjectVisibility(Project p, boolean on) {
        if (p == null || !manager.getManagedProjects().contains(p)) return false;
        p.setVisibility(on ? Visibility.ON : Visibility.OFF);
        return true;
    }

    // …plus stubs for other features: Create/Edit/Delete projects, handle officer regs, generate reports…
}
