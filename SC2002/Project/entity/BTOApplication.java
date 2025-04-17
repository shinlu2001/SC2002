// SC2002/Project/entity/BTOApplication.java
package SC2002.Project.entity;

import SC2002.Project.entity.enums.*;
import SC2002.Project.util.IdGenerator;

public class BTOApplication {
    private final int id = IdGenerator.nextApplicationId();
    private final Applicant applicant;
    private final Project project;
    private final FlatType requestedType;
    private ApplicationStatus status = ApplicationStatus.PENDING;
    private boolean withdrawalReq=false;
    private Flat bookedFlat=null;

    public BTOApplication(Applicant app, Project prj, FlatType type){
        this.applicant=app; this.project=prj; this.requestedType=type;
    }

    public int getId(){return id;}
    public ApplicationStatus getStatus(){return status;}
    public void approve(){ status=ApplicationStatus.SUCCESS; }
    public void reject(){ status=ApplicationStatus.REJECTED; }
    public void withdraw(){ withdrawalReq=true; status=ApplicationStatus.WITHDRAWN; }
    public void book(Flat f){ bookedFlat=f; status=ApplicationStatus.BOOKED; }
}
