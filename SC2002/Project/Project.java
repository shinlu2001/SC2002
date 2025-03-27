package SC2002.Project;

import java.time.LocalDate;
import java.util.*;

public class Project {
    private static int nextId = -1;
    private String ProjectName;
    private String neighbourhood;
    private List<String> flatTypes;
    // private List<Double> flatPrice;
    private List<Integer> totalUnits;
    private List<Integer> availableUnits;
    private int projectId;

    // // Total units when project starts
    // private int total2Room; 
    // private int total3Room;
    // // Remaining units
    // private int available2Room;
    // private int available3Room;
    private LocalDate openDate;
    private LocalDate closeDate;
    private HDB_Manager manager;
    private List<HDB_Officer> assignedOfficers;  // List to hold up to 10 assignedOfficers
    private static int maxOfficerSlots;
    private int availableOfficerSlots;
    private boolean visibility;
    // private String flatType1, flatType2;

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

    // public Project(String ProjectName, String neighbourhood, int total2Room, int total3Room, LocalDate openDate, LocalDate closeDate, boolean visibility, int availableOfficerSlots) {
    public Project(String projectName, String neighbourhood, List<String> flatTypes, List<Integer> totalUnits, List<Integer> availableUnits,LocalDate openDate, LocalDate closeDate, boolean visibility, int availableOfficerSlots) {
        this.ProjectName = projectName;
        this.neighbourhood = neighbourhood;
        // this.total2Room = total2Room;
        // this.total3Room = total3Room;
        // available2Room = total2Room;
        // available3Room = total3Room;`
        this.flatTypes = new ArrayList<>(flatTypes);
        this.totalUnits = new ArrayList<>(totalUnits);
        this.availableUnits = new ArrayList<>(availableUnits);
        this.openDate = openDate;
        this.closeDate = closeDate;
        this.visibility = visibility;
        
        assignedOfficers = new ArrayList<>();  // Initialize the list to hold assignedOfficers
        maxOfficerSlots = 10;
        this.availableOfficerSlots = availableOfficerSlots;
        projectId = ++nextId; // auto-increment ID
    }

    public Project(String string, String string2, String string3, int i, String string4, int j, double d,
            LocalDate openDate2, LocalDate closeDate2, HDB_Manager manager2, int k, List<String> assignedOfficers2) {
        //TODO Auto-generated constructor stub
    }
    // public Project(String string, String string2, String string3, int i, String string4, int j, double d,
    //         LocalDate openDate2, LocalDate closeDate2, HDB_Manager manager2, int k, List<String> assignedOfficers2) {
    //     //TODO Auto-generated constructor stub
    // }
    // Getter and Setter methods
    public int getProjectID() {
        return projectId;
    }
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

    public List<String> getFlatTypes() {
        return new ArrayList<>(flatTypes);
    }
    
    public List<Integer> getTotalUnits() {
        return new ArrayList<>(totalUnits);
    }
    
    public List<Integer> getAvailableUnits() {
        return new ArrayList<>(availableUnits);
    }

    public void addFlatType(String flatType, int units) {
        flatTypes.add(flatType);
        totalUnits.add(units);
        availableUnits.add(units);
    }
    public void removeFlatType(String flatType, int units) {
        int index = flatTypes.indexOf(flatType);
        if (index != -1 && totalUnits.get(index) == units) {
            flatTypes.remove(index);
            totalUnits.remove(index);
            availableUnits.remove(index);
        }
    }

    public void setFlatTypes(List<String> flatTypes) {
        this.flatTypes = new ArrayList<>(flatTypes);
    }
    
    public void setTotalUnits(List<Integer> totalUnits) {
        this.totalUnits = new ArrayList<>(totalUnits);
    }
    
    public void setAvailableUnits(List<Integer> availableUnits) {
        this.availableUnits = new ArrayList<>(availableUnits);
    }
    
    public void updateFlatTypeUnits(String flatType, int newTotalUnits) {
        int index = flatTypes.indexOf(flatType);
        if (index >= 0) {
            // Calculate the difference between new and old total units
            int diff = newTotalUnits - totalUnits.get(index);
            
            // Update total units
            totalUnits.set(index, newTotalUnits);
            
            // Update available units by the same difference
            int newAvailable = availableUnits.get(index) + diff;
            if (newAvailable < 0) newAvailable = 0;
            if (newAvailable > newTotalUnits) newAvailable = newTotalUnits;
            
            availableUnits.set(index, newAvailable);
        } else {
            System.out.println("Error: Flat type not found.");
        }
    }
    
    public void updateAvailableUnits(String flatType, int newAvailableUnits) {
        int index = flatTypes.indexOf(flatType);
        if (index >= 0) {
            if (newAvailableUnits >= 0 && newAvailableUnits <= totalUnits.get(index)) {
                availableUnits.set(index, newAvailableUnits);
            } else {
                System.out.println("Error: Available units must be between 0 and total units.");
            }
        } else {
            System.out.println("Error: Flat type not found.");
        }
    }
    // public int getTotal2Room()
    // {
    //     return total2Room;
    // }

    // public int getTotal3Room()
    // {
    //     return total3Room;
    // }

    // public int getavailable2Room() {
    //     return available2Room;
    // }

    // public int getavailable3Room() {
    //     return available3Room;
    // }

    // public void setTotal2Room(int total2Room) {
    //     // Calculate the difference and update available rooms
    //     int difference = total2Room - this.total2Room;
    //     this.total2Room = total2Room;
    //     this.available2Room += difference;
    //     // System.out.println("Updated number of 2-Room units is " + this.total2Room + ".");
    // }

    // public void setTotal3Room(int total3Room) {
    //     // Calculate the difference and update available rooms
    //     int difference = total3Room - this.total3Room;
    //     this.total3Room = total3Room;
    //     this.available3Room += difference;
    //     // System.out.println("Updated number of 3-Room units is " + this.total3Room + ".");
    // }

    // public void setavailable2Room(int available2Room) {
    //     if (available2Room < 0) {
    //         System.out.println("Error: Invalid input. Number of 2-Room units cannot be negative.");
    //         return;
    //     }
    //     this.available2Room = available2Room;
    // }
    
    // public void setavailable3Room(int available3Room) {
    //     if (available3Room < 0) {
    //         System.out.println("Error: Invalid input. Number of 3-Room units cannot be negative.");
    //         return;
    //     }
    //     this.available3Room = available3Room;
    // }

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

    public boolean isVisible() {
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

    public static int getmaxOfficerSlots() {
        return maxOfficerSlots;
    }

    public void setmaxOfficerSlots(int maxOfficerSlots) {
        Project.maxOfficerSlots = maxOfficerSlots;
    }
    

    public int getAvailableOfficerSlots() {
        return availableOfficerSlots;
    }

    public void setAvailableOfficerSlots(int availableOfficerSlots) {
        this.availableOfficerSlots = availableOfficerSlots;
        // System.out.println("Updated number of available HDB Officer Slots is " + this.availableOfficerSlots + ".");
    }
    
    // Get the list of assigned officers
    public List<HDB_Officer> getAssignedOfficerList() {
        return assignedOfficers;
    }


//     @Override
// public String toString() {
//     String managerName = (manager != null) ? manager.get_firstname() + " " + manager.get_lastname() : "No Manager Assigned";
//     // System.out.println(manager);
//     return "{" + ProjectName + ", " + neighbourhood + ", " + available2Room + ", " + available3Room + ", " + openDate + ", " + closeDate + ", " + visibility + ", " + managerName+ ", " + availableOfficerSlots + "}";
// }

    // @Override
    // public String toString() {
    //     StringBuilder sb = new StringBuilder();
    //     sb.append("{").append(ProjectName).append(", ").append(neighbourhood).append(", [");
        
    //     for (int i = 0; i < flatTypes.size(); i++) {
    //         if (i > 0) sb.append(", ");
    //         sb.append(flatTypes.get(i)).append(": ")
    //         .append(availableUnits.get(i)).append("/")
    //         .append(totalUnits.get(i));
    //     }
        
    //     sb.append("], ").append(openDate).append(", ")
    //     .append(closeDate).append(", ").append(visibility)
    //     .append(", ").append(manager.get_firstname()).append(", ")
    //     .append(availableOfficerSlots).append("}");
        
    //     return sb.toString();
    // }
    
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        
        // sb.append(String.format("%-20s %-15s %-15s %-15s %-15s %-10s %-15s %-15s%n", "Project Name", "Neighbourhood", "Flat Types", "Open Date", "Close Date", "Visible", "Manager", "Officer Slots"));
        // sb.append("--------------------------------------------------------------------------------------------------------------------\n");
        
        // First line with first flat type and all other details
        sb.append(String.format("%-5s %-20s %-15s %-15s %-10s %-15s %-15s %-10s %-15s %-15d%n",
        projectId,
        ProjectName,
        neighbourhood,
        flatTypes.size() > 0 ? 
            flatTypes.get(0) + ": " + (totalUnits.get(0) - availableUnits.get(0)) + "/" + totalUnits.get(0) : "",
        flatTypes.size() > 0 ? this.getFlatPrice(flatTypes.get(0)) : 0,
        openDate,
        closeDate,
        visibility,
        manager.get_firstname(),
        availableOfficerSlots));

    // Additional lines for remaining flat types
    for (int i = 1; i < flatTypes.size(); i++) {
        sb.append(String.format("%-5s %-20s %-15s %-15s %-15s %-15s %-10s %-15s %-15s%n",
            "", "", "",  // Empty project name and neighbourhood
            flatTypes.get(i) + ": " + (totalUnits.get(i) - availableUnits.get(i)) + "/" + totalUnits.get(i),
            flatTypes.size() > 0 ? this.getFlatPrice(flatTypes.get(i)) : 0,
            "", "", "", "", ""));  // Empty other fields
    }

    // Add blank line between projects
    sb.append("\n");

    return sb.toString();
    }

    public double getFlatPrice(String flatType) {
        Iterator<Flat> iterator = BTOsystem.getFlats().iterator();
        Flat f = iterator.next();
        while (iterator.hasNext()) {
            f = iterator.next();
            if (f.getProject().equals(this)) {
                if (f.getFlatType().equals(flatType)) {
                    return f.getPrice();
                }
            }
        }
        return 0;
    }
}