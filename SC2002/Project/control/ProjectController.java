// SC2002/Project/control/ProjectController.java
package SC2002.Project.control;

import java.util.List;
import SC2002.Project.control.persistence.DataStore;
import SC2002.Project.entity.Project;

public final class ProjectController {
    private static final ProjectController INSTANCE = new ProjectController();
    private final DataStore ds = DataStore.getInstance();
    private ProjectController(){}

    public static ProjectController getInstance(){ return INSTANCE; }

    public List<Project> visibleProjects(){ return ds.projects.stream().filter(Project::isVisible).toList(); }
}
