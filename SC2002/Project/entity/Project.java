// SC2002/Project/entity/Project.java
package SC2002.Project.entity;

import java.time.LocalDate;
import java.util.*;
import SC2002.Project.util.IdGenerator;

public class Project {
    private final int id = IdGenerator.nextProjectId();
    private String name;
    private String neighbourhood;
    private final List<String>   flatTypes;
    private final List<Integer>  totalUnits;
    private final List<Integer>  availableUnits;
    private LocalDate open, close;
    private boolean visible=true;
    private int officerSlots;

    public Project(String name,String nhood,List<String> flatTypes,List<Integer> totals,
                   LocalDate open, LocalDate close, boolean vis,int slots){
        this.name=name; this.neighbourhood=nhood; this.flatTypes=new ArrayList<>(flatTypes);
        this.totalUnits=new ArrayList<>(totals);
        this.availableUnits=new ArrayList<>(totals);
        this.open=open; this.close=close; this.visible=vis; this.officerSlots=slots;
    }

    /* basic getters / setters */
    public int getId(){ return id; }
    public String getProjectName(){ return name; }
    public boolean isVisible(){ return visible; }
    public void toggleVisibility(){ visible=!visible; }

    public boolean isOpen(LocalDate now){
        return !now.isBefore(open) && !now.isAfter(close);
    }
}
