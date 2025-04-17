package SC2002.Project.control;

import java.util.function.Supplier;

import SC2002.Project.control.persistence.DataStore;
import SC2002.Project.entity.*;
import SC2002.Project.entity.enums.*;

public class AuthController {
    private final DataStore ds = DataStore.getInstance();

    public LoginResult login(String nric, String pwd) {
        return ds.findUserByNric(nric)
                 .map(u -> {
                     if (!u.verifyPassword(pwd)) {
                         return LoginResult.INVALID_PASSWORD;
                     }
                     if (u instanceof Applicant) {
                         return LoginResult.SUCCESS_APPLICANT;
                     }
                     if (u instanceof HDB_Officer) {
                         return LoginResult.SUCCESS_OFFICER;
                     }
                     if (u instanceof HDB_Manager) {
                         return LoginResult.SUCCESS_MANAGER;
                     }
                     return LoginResult.USER_NOT_FOUND;
                 })
                 .orElse(LoginResult.USER_NOT_FOUND);
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
    
}
