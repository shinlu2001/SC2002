package SC2002.Project.entity;

import SC2002.Project.entity.enums.FlatType;

/**
 * Represents an individual flat unit.
 */
public class Flat {
    private final int id;
    private String flatType;    // e.g. "2-ROOM"
    private double price;
    private boolean booked;
    private Project project;

    public Flat(int id, Project project, String flatType, double price) {
        this.id = id;
        this.project = project;
        this.flatType = flatType.toUpperCase();
        this.price = price;
        this.booked = false;
    }

    public int getId() { return id; }
    public String getFlatType() { return flatType; }
    public double getPrice() { return price; }
    public boolean isBooked() { return booked; }
    public void setBooked(boolean booked) { this.booked = booked; }
    public Project getProject() { return project; }

    /**
     * Returns the flat’s type as the FlatType enum.
     */
    public FlatType getType() {
        return FlatType.valueOf(flatType);
    }
}
