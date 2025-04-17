// SC2002/Project/entity/Enquiry.java
package SC2002.Project.entity;

import java.util.Optional;
import SC2002.Project.util.IdGenerator;
import SC2002.Project.entity.HDB_Manager;

public class Enquiry {
    private final int id = IdGenerator.nextEnquiryId();
    private final Applicant asker;
    private final Project project;          // nullable for general enquiry
    private final Optional<String> flatType;
    private String question;
    private String reply=null;
    private HDB_Officer repliedBy=null;

    public Enquiry(Applicant asker, Project project, Optional<String> flatType, String q){
        this.asker=asker; this.project=project; this.flatType=flatType; this.question=q;
    }
    public int getId(){ return id; }
    public void answer(HDB_Officer o,String resp){ this.repliedBy=o; this.reply=resp; }
}
