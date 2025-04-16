package SC2002.Project.entity;

public class Applicant extends ApplicantBase {
    protected static int nextId = -1;
    private int applicantID;
    private String type = "APPLICANT";
    
    public Applicant(String nric, String firstname, String lastname, String maritalStatus, int age) {
        super(nric, firstname, lastname, maritalStatus, age);
        this.applicantID = ++nextId;
    }
    
    @Override
    public void printDetails() {
        super.printDetails();
        System.out.println("Account type: " + type);
        System.out.println("ApplicantID: " + applicantID);
    }
    
    // Removed start_menu() to separate UI.
}
