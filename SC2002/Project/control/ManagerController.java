package SC2002.Project.control;

import SC2002.Project.control.persistence.DataStore;
import SC2002.Project.entity.*;
import SC2002.Project.entity.enums.ApplicationStatus;
import SC2002.Project.entity.enums.Visibility;
import java.util.List;
import java.util.stream.Collectors;

public class ManagerController {
    private final DataStore dataStore = DataStore.getInstance();
    private final HDB_Manager manager;

    public ManagerController(HDB_Manager manager) {
        this.manager = manager;
    }

    // ---- Approve / Reject BTO Applications ----

    public List<BTOApplication> getPendingApplications() {
        return dataStore.getApplications().stream()
            .filter(app -> app.getStatus() == ApplicationStatus.PENDING)
            .filter(app -> manager.getManagedProjects().contains(app.getProject()))
            .collect(Collectors.toList());
    }

    public BTOApplication findManagedApplicationById(int appId) {
        return getPendingApplications().stream()
            .filter(a -> a.getId() == appId)
            .findFirst().orElse(null);
    }

    public boolean approveApplication(BTOApplication app) {
        if (app == null
         || app.getStatus() != ApplicationStatus.PENDING
         || !manager.getManagedProjects().contains(app.getProject())) {
            return false;
        }
        app.approve();
        return true;
    }

    public boolean rejectApplication(BTOApplication app) {
        if (app == null
         || app.getStatus() != ApplicationStatus.PENDING
         || !manager.getManagedProjects().contains(app.getProject())) {
            return false;
        }
        app.reject();
        app.getApplicant().clearCurrentApplicationReference();
        return true;
    }

    // ---- Handle Withdrawal Requests ----

    public List<BTOApplication> getWithdrawalRequests() {
        return dataStore.getApplications().stream()
            .filter(BTOApplication::isWithdrawalRequested)
            .filter(app -> manager.getManagedProjects().contains(app.getProject()))
            .collect(Collectors.toList());
    }

    public boolean confirmWithdrawal(BTOApplication app) {
        if (app == null
         || !app.isWithdrawalRequested()
         || !manager.getManagedProjects().contains(app.getProject())
         || app.getStatus() == ApplicationStatus.BOOKED) {
            return false;
        }
        app.confirmWithdrawal();
        app.getApplicant().finalizeWithdrawal();
        return true;
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

    // ---- (Stubs for other features TODO) ----

    public List<Project> listAllProjects() {
        return dataStore.getProjects();
    }

    public List<Project> listMyProjects() {
        return manager.getManagedProjects();
    }

    public boolean toggleProjectVisibility(Project p, boolean on) {
        if (p == null || !manager.getManagedProjects().contains(p)) return false;
        p.setVisibility(on ? Visibility.ON : Visibility.OFF);
        return true;
    }

    // …plus stubs for Create/Edit/Delete projects, handle officer regs, generate reports, etc.
}
