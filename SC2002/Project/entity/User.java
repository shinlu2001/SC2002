package SC2002.Project.entity;

import SC2002.Project.entity.enums.MaritalStatus;

/**
 * Represents a system user with basic identity and authentication.
 */
public abstract class User {
    private String nric;
    private String firstName;
    private String lastName;
    private String password = "password"; // Default password
    private MaritalStatus maritalStatus;
    private int age;

    public User(String nric, String firstName, String lastName, MaritalStatus maritalStatus, int age) {
        this.nric = nric;
        this.firstName = firstName;
        this.lastName = lastName;
        this.maritalStatus = maritalStatus;
        this.age = age;
    }

    // Getters
    public String getNric() {
        return nric;
    }

    public String getFirstname() {
        return firstName;
    }

    public String getLastname() {
        return lastName;
    }

    public String getPassword() {
        return password;
    }

    public MaritalStatus getMaritalStatus() {
        return maritalStatus;
    }

    public int getAge() {
        return age;
    }

    // Setters / Actions
    public void setPassword(String newPassword) {
        this.password = newPassword;
    }

    public boolean verifyPassword(String password) {
        return this.password.equals(password);
    }

    @Override
    public String toString() {
        return String.format("NRIC: %s%nFirst name: %s%nLast name: %s%nAge: %d%nMarital status: %s", 
                             nric, firstName, lastName, age, maritalStatus);
    }
}
