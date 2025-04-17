// SC2002/Project/entity/HDB_Officer.java
package SC2002.Project.entity;

import SC2002.Project.entity.enums.MaritalStatus;

public class HDB_Officer extends Applicant {
    public HDB_Officer(String n,String f,String l,MaritalStatus ms,int age){
        super(n,f,l,ms,age);
    }
}
