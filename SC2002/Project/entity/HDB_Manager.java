// SC2002/Project/entity/HDB_Manager.java
package SC2002.Project.entity;

import java.util.Set;

import SC2002.Project.entity.enums.MaritalStatus;

import java.util.HashSet;
import java.util.List;
import java.util.Scanner;

public class HDB_Manager extends User {
    private final Set<Project> managed = new HashSet<>();

    public HDB_Manager(String n,String f,String l,MaritalStatus ms,int age){
        super(n,f,l,ms,age);
    }
    public void start_menu(Scanner sc) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'start_menu'");
    }
    public List<User> getMyProjects() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getMyProjects'");
    }
    public void addProject(Project prj) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'addProject'");
    }

    // TODO: manager‑specific behaviours
}
