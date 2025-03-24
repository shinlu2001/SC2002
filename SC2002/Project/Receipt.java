package SC2002.Project;

/**
 * Represents a receipt generated after a flat booking.
 */
public class Receipt {
    private static int counter = 1;
    private int receiptID;
    private String details;

    public Receipt(String details) {
        this.receiptID = counter++;
        this.details = details;
    }

    public void printReceipt() {
        System.out.println("Receipt ID: " + receiptID);
        System.out.println(details);
    }
}
