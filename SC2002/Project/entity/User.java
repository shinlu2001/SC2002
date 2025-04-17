// SC2002/Project/entity/User.java
package SC2002.Project.entity;

import java.util.Scanner;
import SC2002.Project.entity.enums.MaritalStatus;   // <‑‑ add this

public abstract class User {
    protected final String nric;
    protected final String firstname;
    protected final String lastname;
    protected String password = "password";
    protected int age;
    protected MaritalStatus marital;

    protected User(String nric,String fn,String ln,MaritalStatus ms,int age){
        this.nric=nric; this.firstname=fn; this.lastname=ln;
        this.marital=ms; this.age=age;
    }

    public String get_nric()        { return nric; }
    public String get_firstname()   { return firstname; }
    public String get_lastname()    { return lastname; }
    public int    get_age()         { return age; }
    public String get_maritalstatus(){ return marital.name(); }

    public void changePassword(String newPwd){ password=newPwd; }
    public boolean verify_password(String pwd){ return password.equals(pwd); }

    public abstract void start_menu(Scanner sc);
}
