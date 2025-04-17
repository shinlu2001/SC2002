// SC2002/Project/control/AuthController.java
package SC2002.Project.control;

import SC2002.Project.control.persistence.DataStore;
import SC2002.Project.entity.*;

public class AuthController {
    private final DataStore ds = DataStore.getInstance();

    /**
     * Attempt to log in with given NRIC and password.
     * @return the user’s role ("APPLICANT", "OFFICER", "MANAGER"), or null on failure.
     */
    public String login(String nric, String password) {
        for (User u : ds.users) {
            if (u.getNric().equalsIgnoreCase(nric)) {
                if (u.verifyPassword(password)) {
                    if (u instanceof Applicant)    return "APPLICANT";
                    if (u instanceof HDB_Officer)   return "OFFICER";
                    if (u instanceof HDB_Manager)   return "MANAGER";
                }
                // found NRIC but password mismatch
                return null;
            }
        }
        // no user with that NRIC
        return null;
    }

    /**
     * Change password for a logged‐in user.
     * @return true if oldPwd matched and password was changed
     */
    public boolean changePassword(String nric, String oldPwd, String newPwd) {
        for (User u : ds.users) {
            if (u.getNric().equalsIgnoreCase(nric) && u.verifyPassword(oldPwd)) {
                u.setPassword(newPwd);
                return true;
            }
        }
        return false;
    }

    /**
     * Register a brand new applicant into the system.
     * Returns the new Applicant, or null if NRIC already exists.
     */
    public Applicant registerApplicant(String nric,
                                       String first,
                                       String last,
                                       SC2002.Project.entity.enums.MaritalStatus ms,
                                       int age)
    {
        // prevent duplicates
        for (User u : ds.users) {
            if (u.getNric().equalsIgnoreCase(nric)) {
                return null;
            }
        }
        Applicant a = new Applicant(nric, first, last, ms, age);
        ds.users.add(a);
        return a;
    }

    // (You can add similar registerOfficer/registerManager if you need them.)
}
