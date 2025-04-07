package SC2002.Project;

import java.util.*;
import java.time.LocalDate;
// import java.util.ArrayList;
// import java.util.List;

public abstract class User {
    private String nric;
    private String firstname;
    private String lastname;
    private String password="password";
    private String marital_status;
    private int age;
    private String type;
    // A Menu instance that will be used throughout the class and in its subclasses.
    protected Menu menu;
    // private boolean logged_in=false;
    public User(String nric, String firstname, String lastname, String marital_status, int age) {
        this.nric = nric;
        this.firstname = firstname;
        this.lastname = lastname;
        this.marital_status = marital_status;
        this.age = age;

        // Initialize the menu once so it is available for all methods
        this.menu = new Menu();
    }
    public String get_firstname() {
        return firstname;
    }
    public String get_lastname() {
        return lastname;
    }
    public String get_nric() {
        return nric;
    }
    public String get_password() {
        return password;
    }
    public String get_maritalstatus() {
        return marital_status;
    }
    public int get_age() {
        return age;
    }
    public void to_string() {
        System.out.println("NRIC: " + nric);
        System.out.println("First name: " + firstname);
        System.out.println("Last name: " + lastname);
        System.out.println("Age: "+ age);
        System.out.println("Marital status: " + marital_status);   
    }
    public void change_password(String newpass) {
        this.password = newpass;
    }
    public boolean verify_password(String password) {
        if (this.password.equals(password)) {
            return true;
        } else {
            return false;
        }
    }
    public abstract void start_menu(Scanner scanner);
    public void makeEnquiry() {}
    public String truncateText(String text, int maxLength) {
        if (text == null) return "N/A";
        if (text.length() <= maxLength) {
            return text;
        }
        return text.substring(0, maxLength - 3) + "...";
    }
}