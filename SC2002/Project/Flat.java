package SC2002.Project;
/**
 * Represents an individual flat unit.
 */
public class Flat {
    private int flatID;
    private String flatType;   // "2-Room" or "3-Room"
    private boolean isBooked;
    private double price;

    public Flat(int flatID, String flatType, double price) {
        this.flatID = flatID;
        this.flatType = flatType;
        this.isBooked = false;
        this.price = price;
    }

    public int getFlatID() {
        return flatID;
    }

    public String getFlatType() {
        return flatType;
    }



    public boolean isBooked() {
        return isBooked;
    }

    public void setBooked(boolean booked) {
        isBooked = booked;
    }
}
