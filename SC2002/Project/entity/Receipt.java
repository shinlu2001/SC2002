// SC2002/Project/entity/Receipt.java
package SC2002.Project.entity;

import SC2002.Project.entity.enums.*;
import SC2002.Project.util.IdGenerator;
import java.time.LocalDateTime;

/**
 * Represents a receipt generated after a successful flat booking.
 */
public class Receipt {
    private final int id;
    private final BTOApplication application; // The application this receipt is for
    private final Flat bookedFlat; // The specific flat booked
    private final LocalDateTime generationDate;

    public Receipt(BTOApplication application, Flat bookedFlat) {
        if (application == null || bookedFlat == null || application.getStatus() != ApplicationStatus.BOOKED) {
            throw new IllegalArgumentException(
                    "Receipt can only be generated for a successfully booked application with a valid flat.");
        }
        // Use IdGenerator to generate a unique receipt ID
        this.id = IdGenerator.nextReceiptId();
        this.application = application;
        this.bookedFlat = bookedFlat;
        this.generationDate = LocalDateTime.now();
    }

    public int getId() {
        return id;
    }

    public BTOApplication getApplication() {
        return application;
    }

    public Flat getBookedFlat() {
        return bookedFlat;
    }

    public LocalDateTime getGenerationDate() {
        return generationDate;
    }

    /**
     * Generates the formatted content of the receipt.
     * 
     * @return A string representing the receipt details.
     */
    public String getReceiptDetails() {
        Applicant applicant = application.getApplicant();
        Project project = application.getProject();

        // Format the date/time to show only up to seconds (no decimal/nanoseconds)
        String formattedDate = generationDate.toString().split("\\.")[0]; // Remove decimal portion

        // Calculate the number of available flats of this type
        int availableFlats = project.getRemainingUnits(bookedFlat.getFlatType());

        return String.format(
                "========== FLAT BOOKING RECEIPT ==========\n" +
                        "Receipt ID: %d\n" +
                        "Generated On: %s\n" +
                        "------------------------------------------\n" +
                        "Applicant NRIC: %s\n" +
                        "Applicant Name: %s %s\n" +
                        "------------------------------------------\n" +
                        "Project Name: %s\n" +
                        "Project Location: %s\n" +
                        "Booked Flat ID: %d\n" +
                        "Booked Flat Type: %s\n" +
                        "Booking Price: $%.2f\n" +
                        "------------------------------------------\n" +
                        "%d flats of type %s still available\n" +
                        "==========================================",
                id, formattedDate,
                applicant.getNric(),
                applicant.getFirstName(), applicant.getLastName(),
                project.getName(),
                project.getNeighbourhood(),
                bookedFlat.getId(),
                bookedFlat.getFlatType(),
                bookedFlat.getPrice(),
                availableFlats,
                bookedFlat.getFlatType());
    }

    @Override
    public String toString() {
        // Simple toString, use getReceiptDetails() for full printout
        return String.format("Receipt ID: %d for Application ID: %d", id, application.getId());
    }
}
