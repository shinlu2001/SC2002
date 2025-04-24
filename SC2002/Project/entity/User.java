package SC2002.Project.entity;

import SC2002.Project.entity.enums.MaritalStatus;

/**
 * Represents a system user with basic identity and authentication capabilities.
 * <p>
 * This abstract class serves as the base for all user types in the BTO Management System,
 * providing common attributes and behaviors shared across different user roles.
 * It includes personal information, authentication mechanisms, and basic user operations.
 * </p>
 * <p>
 * User is extended by concrete classes such as {@link Applicant}, {@link HDB_Officer}, 
 * and {@link HDB_Manager}, each adding role-specific functionality.
 * </p>
 * 
 * @author Group 1
 * @version 1.0
 * @since 2025-04-24
 */
public abstract class User {
    private String nric;
    private String firstName;
    private String lastName;
    private String password = "password"; // Default password
    private MaritalStatus maritalStatus;
    private int age;

    /**
     * Constructs a new User with the specified details.
     * 
     * @param nric          The National Registration Identity Card number, unique identifier for the user
     * @param firstName     The user's first name
     * @param lastName      The user's last name
     * @param maritalStatus The marital status of the user (SINGLE or MARRIED)
     * @param age           The age of the user in years
     */
    public User(String nric, String firstName, String lastName, MaritalStatus maritalStatus, int age) {
        this.nric = nric;
        this.firstName = firstName;
        this.lastName = lastName;
        this.maritalStatus = maritalStatus;
        this.age = age;
    }

    // --- Getters ---
    /**
     * Gets the user's NRIC (National Registration Identity Card) number.
     * 
     * @return The NRIC number as a String
     */
    public String getNric() {
        return nric;
    }

    /**
     * Gets the user's first name.
     * 
     * @return The first name as a String
     */
    public String getFirstName() {
        return firstName;
    }

    /**
     * Gets the user's last name.
     * 
     * @return The last name as a String
     */
    public String getLastName() {
        return lastName;
    }

    /**
     * Gets the user's current password.
     * 
     * @return The password as a String
     */
    public String getPassword() {
        return password;
    }

    /**
     * Gets the user's marital status.
     * 
     * @return The marital status (SINGLE or MARRIED)
     */
    public MaritalStatus getMaritalStatus() {
        return maritalStatus;
    }

    /**
     * Gets the user's age in years.
     * 
     * @return The age as an integer
     */
    public int getAge() {
        return age;
    }

    // --- Setters / Actions ---
    /**
     * Updates the user's password.
     * 
     * @param newPassword The new password to set
     */
    public void setPassword(String newPassword) {
        this.password = newPassword;
    }

    /**
     * Verifies if the provided password matches the user's current password.
     * 
     * @param password The password to verify
     * @return {@code true} if the password matches, {@code false} otherwise
     */
    public boolean verifyPassword(String password) {
        return this.password.equals(password);
    }

    /**
     * Returns a string representation of the user.
     * 
     * @return A formatted string containing user details
     */
    @Override
    public String toString() {
        // Reverted toString
        return String.format("NRIC: %s%nFirst name: %s%nLast name: %s%nAge: %d%nMarital status: %s", nric, firstName, lastName, age, maritalStatus);
    }
}
