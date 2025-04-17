// SC2002/Project/control/RegistrationController.java
package SC2002.Project.control;

import SC2002.Project.entity.HDB_Officer;
import SC2002.Project.entity.Registration;
import SC2002.Project.entity.enums.RegistrationStatus;

import java.util.List;

public class RegistrationController {
    /** Register as officer for a project */
    public Registration register(HDB_Officer officer, int projectId) {
        // TODO
        return null;
    }

    /** Find registration by ID */
    public Registration findById(int id) {
        // TODO
        return null;
    }

    /** List registrations for a project */
    public List<Registration> listForProject(int projectId) {
        // TODO
        return null;
    }

    /** Change registration status (approve/reject) */
    public boolean changeStatus(int registrationId, RegistrationStatus status) {
        // TODO
        return false;
    }

    /** Withdraw a registration (if allowed) */
    public boolean withdrawRegistration(int registrationId) {
        // TODO
        return false;
    }
}
