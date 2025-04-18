package SC2002.Project.entity;

import SC2002.Project.entity.enums.MaritalStatus;
import SC2002.Project.util.IdGenerator;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents an HDB Officer user (inherits Applicant capabilities).
 */
public class HDB_Officer extends Applicant {
    private final int officerId;
    private final List<Registration> registrations = new ArrayList<>();

    public HDB_Officer(String nric, String firstName, String lastName,
                       MaritalStatus maritalStatus, int age) {
        super(nric, firstName, lastName, maritalStatus, age);
        this.officerId = IdGenerator.nextOfficerId();
    }

    public int getOfficerId() {
        return officerId;
    }

    public List<Registration> getRegistrations() {
        return registrations;
    }

    public void addRegistration(Registration reg) {
        if (!registrations.contains(reg)) {
            registrations.add(reg);
        }
    }

    @Override
    public String toString() {
        return String.format("Officer %s %s | NRIC: %s | Age: %d | Marital: %s",
            getFirstName(), getLastName(), getNric(), getAge(), getMaritalStatus());
    }
}
