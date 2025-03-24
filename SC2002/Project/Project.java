package SC2002.Project;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class Project {
    private String ProjectName;
    private String neighbourhood;
    //------------START-----------------
    // Total units when project starts
    private int total2Room; 
    private int total3Room;
    // Remaining units
    private int available2Room;
    private int available3Room;
    private LocalDate openDate;
    private LocalDate closeDate;
    private HDB_Manager manager;
    private List<HDB_Officer> assignedOfficers;  // List to hold up to 10 assignedOfficers
    private static int maxOfficerSlots;     //CHANGED
    private int availableOfficerSlots;
    //-------------END-----------------
    private boolean visibility=true;
    public void toggle_visibility() {
        if (visibility==true) {
            visibility=false;
        } else {
            visibility=true;
        }
    }
    public void assignOfficer(HDB_Officer off) {
        assignedOfficers.add(off);
    }
    public void setManager(HDB_Manager man) {
        this.manager = man;
    }

    //------------START-----------------
    public Project(String ProjectName, String neighbourhood, int total2Room, int total3Room, LocalDate openDate, LocalDate closeDate, boolean visibility, int availableOfficerSlots) {

        this.ProjectName = ProjectName;
        this.neighbourhood = neighbourhood;
        this.total2Room = total2Room;
        this.total3Room = total3Room;
        available2Room = total2Room;
        available3Room = total3Room;
        this.openDate = openDate;
        this.closeDate = closeDate;
        this.visibility = visibility;
        // this.manager = manager;
        // this.officer = officer;
        assignedOfficers = new ArrayList<>();  // Initialize the list to hold assignedOfficers
        maxOfficerSlots = 10;
        this.availableOfficerSlots = availableOfficerSlots;

    }

    // Getter and Setter methods
    public String getProjectName() {
        return ProjectName;
    }

    public void setProjectName(String ProjectName) {
        this.ProjectName = ProjectName;
    }

    public String getneighbourhood() {
        return neighbourhood;
    }

    public void setneighbourhood(String neighbourhood) {
        this.neighbourhood = neighbourhood;
    }

    public int getTotal2Room()
    {
        return total2Room;
    }

    public int getTotal3Room()
    {
        return total3Room;
    }
    // public void getTotal2Room(int total2Room)
    // {
    //     this.total2Room = total2Room;
    // }

    public int getavailable2Room() {
        return available2Room;
    }

    public int getavailable3Room() {
        return available3Room;
    }

    //CHANGED
    public void setTotal2Room(int total2Room) {
        if (total2Room < 0) {
            System.out.println("Invalid Input. Number of units has to be 1 and above.");
            return;
        }
        // Calculate the difference and update available rooms
        int difference = total2Room - this.total2Room;
        this.total2Room = total2Room;
        this.available2Room += difference;
        System.out.println("Updated number of 2-Room units is " + this.total2Room + ".");
    }
    //CHANGED
    public void setTotal3Room(int total3Room) {
        if (total3Room < 0) {
            System.out.println("Invalid Input. Number of units has to be 1 and above.");
            return;
        }
        // Calculate the difference and update available rooms
        int difference = total3Room - this.total3Room;
        this.total3Room = total3Room;
        this.available3Room += difference;
        System.out.println("Updated number of 3-Room units is " + this.total3Room + ".");
    }

    public void setavailable2Room(int available2Room) {
        if (available2Room < 0) {
            System.out.println("Invalid input. Number of 2-Room units cannot be negative.");
            return;
        }
        this.available2Room = available2Room;
    }
    
    public void setavailable3Room(int available3Room) {
        if (available3Room < 0) {
            System.out.println("Invalid input. Number of 3-Room units cannot be negative.");
            return;
        }
        this.available3Room = available3Room;
    }

    public LocalDate getOpenDate() {
        return openDate;
    }

    public void setOpenDate(LocalDate openDate) {
        this.openDate = openDate;
    }

    public LocalDate getCloseDate() {
        return closeDate;
    }

    public void setCloseDate(LocalDate closeDate) {
        this.closeDate = closeDate;
    }

    public boolean isVisibility() {
        return visibility;
    }

    public void setVisibility(boolean visibility) {
        this.visibility = visibility;
    }

    // Add max officer to the project
    public boolean addOfficer(HDB_Officer officer) {
        if (assignedOfficers.size() < availableOfficerSlots) {
            assignedOfficers.add(officer);
            availableOfficerSlots--;
            return true;
        }
        return false;  // Return false if there are already 10 assignedOfficers
    }

    // Getter method for manager
    public HDB_Manager getManager() {
        return manager;
    }

    //CHANGED
    public static int getmaxOfficerSlots() {
        return maxOfficerSlots;
    }

    public void setmaxOfficerSlots(int maxOfficerSlots) {
        this.maxOfficerSlots = maxOfficerSlots;
    }

    public int getAvailableOfficerSlots() {
        return availableOfficerSlots;
    }

    public void setAvailableOfficerSlots(int availableOfficerSlots, Project p) {
        // Ensure available slots do not exceed maximum slots
        if (availableOfficerSlots > maxOfficerSlots || availableOfficerSlots <= 0 ) {
            System.out.println("Invalid input. Available officer slots must be between 1 and " + maxOfficerSlots + ".");
            return;
        }
        // Check if the new maximum is less than the current number of assigned officers
        if (availableOfficerSlots < p.getAssignedOfficerList().size()) {
            System.out.println("Invalid input. The new maximum number of officer slots is less than the current number of assigned officers.");
            return; 
        }

        // If all conditions are valid, change available no. of officer slots
        this.availableOfficerSlots = availableOfficerSlots;
        System.out.println("Updated number of available HDB Officer Slots is " + this.availableOfficerSlots + ".");
    }
    
    // Get the list of assigned officers
    public List<HDB_Officer> getAssignedOfficerList() {
        return assignedOfficers;
    }

    // @Override
    // public String toString() {
    //     return "{" + ProjectName + ", " + neighbourhood + ", " + available2Room + ", " + available3Room + ", " + openDate + ", " + closeDate + ", " + visibility + ", " + manager.get_firstname() + " " +  manager.get_lastname() + ", " + (10-assignedOfficers.size()) + "}";
    // }
    @Override
public String toString() {
    String managerName = (manager != null) ? manager.get_firstname() + " " + manager.get_lastname() : "No Manager Assigned";
    // System.out.println(manager);
    return "{" + ProjectName + ", " + neighbourhood + ", " + available2Room + ", " + available3Room + ", " + openDate + ", " + closeDate + ", " + visibility + ", " + managerName+ ", " + availableOfficerSlots + "}";
}
    
    //-------------END-----------------
}
