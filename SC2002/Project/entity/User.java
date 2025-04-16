package SC2002.Project.entity;

public abstract class User {
    private String nric;
    private String firstname;
    private String lastname;
    private String password = "password";
    private String maritalStatus;
    private int age;
    
    public User(String nric, String firstname, String lastname, String maritalStatus, int age) {
        this.nric = nric;
        this.firstname = firstname;
        this.lastname = lastname;
        this.maritalStatus = maritalStatus;
        this.age = age;
    }
    
    public String getFirstName() { return firstname; }
    public String getLastName() { return lastname; }
    public String getNRIC() { return nric; }
    public String getPassword() { return password; }
    public String getMaritalStatus() { return maritalStatus; }
    public int getAge() { return age; }
    
    public void printDetails() {
        System.out.println("NRIC: " + nric);
        System.out.println("First name: " + firstname);
        System.out.println("Last name: " + lastname);
        System.out.println("Age: " + age);
        System.out.println("Marital status: " + maritalStatus);   
    }
    
    public void setPassword(String pass) { this.password = pass; }
    public boolean verifyPassword(String pass) { return pass.equals(password); }
}
