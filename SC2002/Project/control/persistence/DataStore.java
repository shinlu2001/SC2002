// SC2002/Project/control/persistence/DataStore.java
package SC2002.Project.control.persistence;

import java.util.*;
import SC2002.Project.entity.*;

public final class DataStore {
    private static final DataStore INSTANCE = new DataStore();

    public final List<User>           users        = new ArrayList<>();
    public final List<Project>        projects     = new ArrayList<>();
    public final List<BTOApplication> applications = new ArrayList<>();
    public final List<Enquiry>        enquiries    = new ArrayList<>();
    public final List<Flat>           flats        = new ArrayList<>();
    public final List<Registration> registrations = new ArrayList<>();


    private DataStore() {}

    public static DataStore getInstance() { return INSTANCE; }
}
