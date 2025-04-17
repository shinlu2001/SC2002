// SC2002/Project/entity/Flat.java
package SC2002.Project.entity;

import SC2002.Project.entity.enums.FlatType;
import SC2002.Project.util.IdGenerator;

public class Flat {
    private final int id = IdGenerator.nextFlatId();
    private final Project project;
    private final FlatType type;
    private final double price;
    private boolean booked=false;

    public Flat(Project prj, FlatType type, double price){
        this.project=prj; this.type=type; this.price=price;
    }
    public int getId(){ return id; }
    public double getPrice(){ return price; }
    public boolean isBooked(){ return booked; }
    public void setBooked(boolean b){ booked=b; }
    public FlatType getType() { return type; }
    public Project getProject() { return project; }
}
