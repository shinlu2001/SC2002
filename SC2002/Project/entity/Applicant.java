// SC2002/Project/entity/Applicant.java
package SC2002.Project.entity;

import java.util.Optional;
import SC2002.Project.entity.enums.MaritalStatus;

public class Applicant extends User {
    private BTOApplication active = null;

    public Applicant(String n,String f,String l,MaritalStatus ms,int age){
        super(n,f,l,ms,age);
    }

    public Optional<BTOApplication> getActiveApplication(){ return Optional.ofNullable(active); }
    public void setActiveApplication(BTOApplication app){ this.active = app; }

    public void start_menu(java.util.Scanner sc){ /* unused: handled by ApplicantUI */ }
}
