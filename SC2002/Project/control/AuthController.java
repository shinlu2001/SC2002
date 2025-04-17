// SC2002/Project/control/AuthController.java
package SC2002.Project.control;

import SC2002.Project.control.persistence.DataStore;
import SC2002.Project.entity.User;

public final class AuthController {
    private static final AuthController INSTANCE = new AuthController();
    private final DataStore ds = DataStore.getInstance();
    private AuthController(){}

    public static AuthController getInstance(){ return INSTANCE; }

    public User login(String nric, String pwd){
        return ds.users.stream()
                .filter(u -> u.get_nric().equalsIgnoreCase(nric) && u.verify_password(pwd))
                .findFirst().orElse(null);
    }
}
