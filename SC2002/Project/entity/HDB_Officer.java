package SC2002.Project.entity;

import SC2002.Project.entity.enums.MaritalStatus;

public class HDB_Officer extends Applicant {
    private Registration currentRegistration;

    public HDB_Officer(String n, String f, String l, MaritalStatus ms, int age) {
        super(n, f, l, ms, age);
    }

    /**
     * Called by CSVReader when loading initial officers for a project.
     * You can also fetch via this getter in your controllers.
     */
    public void setCurrentRegistration(Registration reg) {
        this.currentRegistration = reg;
    }

    /** Optional helper if you need to inspect the current registration later. */
    public Registration getCurrentRegistration() {
        return currentRegistration;
    }
}
