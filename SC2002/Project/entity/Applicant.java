package SC2002.Project.entity;

import java.util.Scanner;

import SC2002.Project.entity.enums.MaritalStatus;

public class Applicant extends User {
    public Applicant(String nric,String fn,String ln,MaritalStatus ms,int age){
        super(nric,fn,ln,ms,age);
    }

    public void start_menu(Scanner sc) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'start_menu'");
    }
}
