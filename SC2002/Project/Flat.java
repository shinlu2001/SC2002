package SC2002.Project;
/**
 * Represents an individual flat unit.
 */
public class Flat {
    private static int nextId = -1;
    private Project project;
    private int flatID;
    private String flatType;   // "2-ROOM" or "3-ROOM"
    private double price;
    private boolean isBooked;
    // should flat be tied to an application?

    public Flat(Project p, String flatType, double price) {
        this.flatID = ++nextId;
        this.flatType = flatType;
        this.price = price;
        this.isBooked = false;
        this.project = p;
    }

    public int getFlatID() {
        return flatID;
    }

    public String getFlatType() {
        return flatType;
    }

    public double getPrice() {
        return price;
    }

    public boolean isBooked() {
        return isBooked;
    }

    public void setBooked(boolean booked) {
        isBooked = booked;
    }

    public Project getProject() {
        return project;
    }
}
