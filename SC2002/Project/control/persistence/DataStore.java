// SC2002/Project/control/persistence/DataStore.java
package SC2002.Project.control.persistence;

import SC2002.Project.entity.BTOApplication;
import SC2002.Project.entity.Enquiry;
import SC2002.Project.entity.Flat;
import SC2002.Project.entity.HDB_Manager;
import SC2002.Project.entity.Project;
import SC2002.Project.entity.Registration;
import SC2002.Project.entity.User;
import SC2002.Project.entity.enums.FlatType;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public final class DataStore {
    private static final DataStore INSTANCE = new DataStore();

    private final List<User>           users         = new ArrayList<>();
    private final List<Project>        projects      = new ArrayList<>();
    private final List<BTOApplication> applications  = new ArrayList<>();
    private final List<Enquiry>        enquiries     = new ArrayList<>();
    private final List<Flat>           flats         = new ArrayList<>();
    private final List<Registration>   registrations = new ArrayList<>();

    private DataStore() { }

    public static DataStore getInstance() {
        return INSTANCE;
    }

    /** All users in the system */
    public List<User> getUsers() {
        return users;
    }

    /** All projects in the system */
    public List<Project> getProjects() {
        return projects;
    }

    public void setProjects(List<Project> projects) {
        this.projects.clear();
        this.projects.addAll(projects);
    }

    /** All BTO applications */
    public List<BTOApplication> getApplications() {
        return applications;
    }

    public void setApplications(List<BTOApplication> applications) {
        this.applications.clear();
        this.applications.addAll(applications);
    }

    /** All enquiries */
    public List<Enquiry> getEnquiries() {
        return enquiries;
    }

    public void setEnquiries(List<Enquiry> enquiries) {
        this.enquiries.clear();
        this.enquiries.addAll(enquiries);
    }

    /** All flats */
    public List<Flat> getFlats() {
        return flats;
    }

    public void setFlats(List<Flat> flats) {
        this.flats.clear();
        this.flats.addAll(flats);
    }

    /** All officer registrations */
    public List<Registration> getRegistrations() {
        return registrations;
    }

    public void setRegistrations(List<Registration> registrations) {
        this.registrations.clear();
        this.registrations.addAll(registrations);
    }

    // ───────── Convenience Finders ─────────

    /** Find the first Flat whose `getType()` matches the given string (case‑insensitive). */
    public Optional<Flat> findFlatByType(FlatType wanted) {
        return flats.stream()
                    .filter(f -> f.getType() == wanted)
                    .findFirst();
    }

    /** Find a user by NRIC. */
    public Optional<User> findUserByNric(String nric) {
        return users.stream()
                    .filter(u -> u.getNric().equalsIgnoreCase(nric))
                    .findFirst();
    }

    /** Find a project by its ID. */
    public Optional<Project> findProjectById(int id) {
        return projects.stream()
                       .filter(p -> p.getId() == id)
                       .findFirst();
    }

    /** Find a manager by first name (case-insensitive). */
    public Optional<HDB_Manager> findManagerByName(String firstName) {
        return users.stream()
                    .filter(u -> u instanceof HDB_Manager)
                    .map(u -> (HDB_Manager) u)
                    .filter(m -> m.getFirstName().equalsIgnoreCase(firstName))
                    .findFirst();
    }

    /** All applications for a given applicant NRIC. */
    public List<BTOApplication> getApplicationsForApplicant(String nric) {
        return applications.stream()
                           .filter(app -> app.getApplicant().getNric().equalsIgnoreCase(nric))
                           .collect(Collectors.toList());
    }

    /** Find the first available Flat object matching this project/type. */
    public Optional<Flat> findAvailableFlat(Project project, String wanted) {
        return flats.stream()
                    .filter(f -> f.getProject().equals(project))
                    .filter(f -> f.getFlatType().equalsIgnoreCase(wanted))
                    .filter(f -> !f.isBooked())
                    .findFirst();
    }

    // …add more helpers as needed…
}
