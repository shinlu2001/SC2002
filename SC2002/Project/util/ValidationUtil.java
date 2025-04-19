// SC2002/Project/util/ValidationUtil.java
package SC2002.Project.util;

import java.time.LocalDate;
import java.util.regex.Pattern;

public final class ValidationUtil {
    // Updated regex to match spec: ^[A-Za-z]\d{7}[A-Za-z]$
    private static final Pattern NRIC = Pattern.compile("^[A-Za-z]\\d{7}[A-Za-z]$");

    private ValidationUtil() {}

    /**
     * Validates if a string is a properly formatted NRIC
     * @param nric The NRIC string to validate
     * @return true if the NRIC is valid, false otherwise
     */
    public static boolean isValidNric(String nric) {
        if (nric == null) return false;
        return NRIC.matcher(nric.toUpperCase()).matches(); // Ensure case-insensitivity for letters
    }
    
    /**
     * Validates if a string is a properly formatted NRIC and provides a detailed error message if not
     * @param nric The NRIC string to validate
     * @return A validation result containing success status and error message if applicable
     */
    public static ValidationResult validateNric(String nric) {
        if (nric == null) {
            return new ValidationResult(false, "NRIC cannot be null");
        }
        if (nric.isEmpty()) {
            return new ValidationResult(false, "NRIC cannot be empty");
        }
        if (!NRIC.matcher(nric.toUpperCase()).matches()) {
            return new ValidationResult(false, 
                "Invalid NRIC format. NRIC must start with a letter, followed by 7 digits, and end with a letter (e.g., S1234567A)");
        }
        return new ValidationResult(true, null);
    }
    
    /**
     * Validates a project date range
     * @param openDate The opening date
     * @param closeDate The closing date
     * @return A validation result containing success status and error message if applicable
     */
    public static ValidationResult validateProjectDates(LocalDate openDate, LocalDate closeDate) {
        if (openDate == null) {
            return new ValidationResult(false, "Opening date cannot be null");
        }
        if (closeDate == null) {
            return new ValidationResult(false, "Closing date cannot be null");
        }
        if (openDate.isAfter(closeDate)) {
            return new ValidationResult(false, 
                "Invalid date range: Opening date (" + openDate + ") cannot be after closing date (" + closeDate + ")");
        }
        if (openDate.isBefore(LocalDate.now().minusYears(1))) {
            return new ValidationResult(false, 
                "Opening date cannot be more than one year in the past");
        }
        return new ValidationResult(true, null);
    }
    
    /**
     * Validates price value
     * @param price The price to validate
     * @return A validation result containing success status and error message if applicable
     */
    public static ValidationResult validatePrice(double price) {
        if (price <= 0) {
            return new ValidationResult(false, 
                "Price must be greater than zero, received: " + price);
        }
        return new ValidationResult(true, null);
    }
    
    /**
     * Validates unit count 
     * @param units The number of units to validate
     * @return A validation result containing success status and error message if applicable
     */
    public static ValidationResult validateUnitCount(int units) {
        if (units <= 0) {
            return new ValidationResult(false, 
                "Number of units must be greater than zero, received: " + units);
        }
        return new ValidationResult(true, null);
    }
    
    /**
     * A class representing the result of a validation check
     */
    public static class ValidationResult {
        private final boolean valid;
        private final String errorMessage;
        
        public ValidationResult(boolean valid, String errorMessage) {
            this.valid = valid;
            this.errorMessage = errorMessage;
        }
        
        public boolean isValid() {
            return valid;
        }
        
        public String getErrorMessage() {
            return errorMessage;
        }
    }
}
