package SC2002.Project.control;

import SC2002.Project.control.persistence.DataStore;
import SC2002.Project.entity.*;
import SC2002.Project.entity.enums.*;
import java.util.List;
import java.util.function.Supplier;

public class AuthController {
    private final DataStore ds = DataStore.getInstance();

    public LoginResult login(String nric, String pwd) {
        List<User> users = ds.getUsers();

        // 1) managers first
        for (User u : users) {
            if (u instanceof HDB_Manager && u.getNric().equalsIgnoreCase(nric)) {
                return verify(u, pwd, LoginResult.SUCCESS_MANAGER);
            }
        }

        // 2) then officers
        for (User u : users) {
            if (u instanceof HDB_Officer && u.getNric().equalsIgnoreCase(nric)) {
                return verify(u, pwd, LoginResult.SUCCESS_OFFICER);
            }
        }

        // 3) then applicants
        for (User u : users) {
            if (u instanceof Applicant && u.getNric().equalsIgnoreCase(nric)) {
                return verify(u, pwd, LoginResult.SUCCESS_APPLICANT);
            }
        }

        // 4) nothing matched
        return LoginResult.USER_NOT_FOUND;
    }

    /** helper to check password and return either the given success code or INVALID_PASSWORD */
    private LoginResult verify(User u, String pwd, LoginResult success) {
        return u.verifyPassword(pwd) ? success : LoginResult.INVALID_PASSWORD;
    }

    public RegistrationResult registerApplicant(String nric,
                                                String first,
                                                String last,
                                                MaritalStatus ms,
                                                int age) {
        return registerNewUser(() -> new Applicant(nric, first, last, ms, age));
    }

    public RegistrationResult registerOfficer(String nric,
                                              String first,
                                              String last,
                                              MaritalStatus ms,
                                              int age) {
        return registerNewUser(() -> new HDB_Officer(nric, first, last, ms, age));
    }

    public RegistrationResult registerManager(String nric,
                                              String first,
                                              String last,
                                              MaritalStatus ms,
                                              int age) {
        return registerNewUser(() -> new HDB_Manager(nric, first, last, ms, age));
    }

    private RegistrationResult registerNewUser(Supplier<User> factory) {
        User newUser = factory.get();                     // build exactly once
        if (ds.findUserByNric(newUser.getNric()).isPresent()) {
            return RegistrationResult.DUPLICATE_NRIC;
        }
        ds.getUsers().add(newUser);                       // add the same instance
        return RegistrationResult.SUCCESS;
    }
    /**
     * Changes the password for a given user after verifying the old password.
     * @param user The user whose password needs changing.
     * @param oldPassword The current password for verification.
     * @param newPassword The desired new password.
     * @return true if the password was successfully changed, false otherwise.
     */
    public boolean changePassword(User user, String oldPassword, String newPassword) {
        if (user == null || oldPassword == null || newPassword == null || newPassword.isEmpty()) {
            return false;
        }
        if (user.verifyPassword(oldPassword)) {
            user.setPassword(newPassword); // Assuming User has setPassword
            // Consider saving changes immediately or relying on periodic saves
            // CSVWriter.saveUser(user); // Example: If immediate save is needed
            return true;
        }
        return false;
    }
}
