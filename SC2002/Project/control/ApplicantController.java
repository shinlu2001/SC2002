// SC2002/Project/control/ApplicantController.java
package SC2002.Project.control;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;

import SC2002.Project.control.persistence.DataStore;
import SC2002.Project.entity.*;
import SC2002.Project.entity.enums.*;

/** Pure business logic for an Applicant (no console I/O). */
public final class ApplicantController {

    private static final ApplicantController INSTANCE = new ApplicantController();
    private final DataStore ds = DataStore.getInstance();
    private ApplicantController() {}
    public static ApplicantController getInstance() { return INSTANCE; }

    /* ────────────────────────── domain rules ────────────────────────── */

    /** Return the list of projects this applicant can *see* today. */
    public List<Project> eligibleProjects(Applicant a) {
        LocalDate today = LocalDate.now();
        return ds.projects.stream()
                .filter(Project::isVisible)
                .filter(p -> p.isOpen(today))
                .filter(p -> canApply(a, p))
                .toList();
    }

    /** Can the applicant apply for `flatType` in project p? */
    public boolean canApply(Applicant a, Project p, FlatType flatType) {
        if (a.get_maritalstatus().equals("SINGLE")) {
            return a.get_age() >= 35 && flatType == FlatType.TWO_ROOM;
        }
        return a.get_age() >= 21;                    // married
    }

    /** Overload just for project‑level eligibility (any flat). */
    private boolean canApply(Applicant a, Project p) {
        if (a.get_maritalstatus().equals("SINGLE"))
            return a.get_age() >= 35;
        return a.get_age() >= 21;
    }

    /** Creates an application if allowed; returns Optional of the new application. */
    public Optional<BTOApplication> apply(Applicant a, int projectId, FlatType type) {
        if (a.getActiveApplication().isPresent()) return Optional.empty();

        Project p = ds.projects.stream().filter(prj -> prj.getId() == projectId).findFirst().orElse(null);
        if (p == null || !p.isVisible())            return Optional.empty();
        if (!canApply(a, p, type))                  return Optional.empty();

        BTOApplication app = new BTOApplication(a, p, type);
        ds.applications.add(app);
        a.setActiveApplication(app);
        return Optional.of(app);
    }

    /** Withdraw the active application (if any). */
    public boolean withdraw(Applicant a) {
        return a.getActiveApplication().map(app -> { app.withdraw(); return true; }).orElse(false);
    }
}
