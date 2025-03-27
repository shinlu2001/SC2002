package SC2002.Project;
/**
 * Represents an individual flat unit.
 */
public class Flat {
    private static int nextId = -1;
    private Project project;
    private int flatID;
    private String flatType;   // "2-Room" or "3-Room"
    private double price;
    private boolean isBooked;

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

    // public void setAppliedBy(Applicant a) {
    //     appliedBy=a;
    // }

    // public Applicant getAppliedBy() {
    //     return applieddBy;
    // }

    public Project getProject() {
        return project;
    }
}
