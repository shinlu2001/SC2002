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
    private String bookingId;  // Will be application ID or empty if not booked

    public Flat(int id, Project project, String flatType, double price) {
        this.id = id;
        this.project = project;
        this.flatType = flatType.toUpperCase();
        this.price = price;
        this.booked = false;
        this.bookingId = "";
    }

    public int getId() { return id; }
    public String getFlatType() { return flatType; }
    public double getPrice() { return price; }
    public boolean isBooked() { return booked; }
    public void setBooked(boolean booked) { this.booked = booked; }
    public Project getProject() { return project; }
    public String getBookingId() { return bookingId; }
    public void setBookingId(String bookingId) { this.bookingId = bookingId; }

    /**
     * Returns the flat's type as the FlatType enum.
     */
    public FlatType getType() {
        return FlatType.valueOf(flatType);
    }

    /**
     * Books this flat with the given application ID.
     * @param applicationId The ID of the application booking this flat
     */
    public void book(String applicationId) {
        this.booked = true;
        this.bookingId = applicationId;
    }

    /**
     * Unbooks this flat, freeing it for other applications.
     */
    public void unbook() {
        this.booked = false;
        this.bookingId = "";
    }
}
