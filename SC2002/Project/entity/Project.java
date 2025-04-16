package SC2002.Project.entity;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class Project {
    protected static int nextId = -1;
    private String projectName;
    private String neighbourhood;
    private List<String> flatTypes;
    private List<Integer> totalUnits;
    private List<Integer> availableUnits;
    private int projectId;
    private LocalDate openDate;
    private LocalDate closeDate;
    private HDB_Manager manager;
    protected List<HDB_Officer> assignedOfficers;
    private static int maxOfficerSlots = 10;
    private int totalOfficerSlots;
    private boolean visibility;
    private List<Enquiry> enquiries = new ArrayList<>();
    
    public Project(String projectName, String neighbourhood, List<String> flatTypes, List<Integer> totalUnits,
                   LocalDate openDate, LocalDate closeDate, boolean visibility, int totalOfficerSlots) {
        this.projectName = projectName;
        this.neighbourhood = neighbourhood;
        this.flatTypes = new ArrayList<>(flatTypes);
        this.totalUnits = new ArrayList<>(totalUnits);
        this.availableUnits = new ArrayList<>(totalUnits);
        this.openDate = openDate;
        this.closeDate = closeDate;
        this.visibility = visibility;
        this.assignedOfficers = new ArrayList<>();
        this.totalOfficerSlots = totalOfficerSlots;
        this.projectId = ++nextId;
    }
    
    public void toggleVisibility() { visibility = !visibility; }
    public void setManager(HDB_Manager man) { this.manager = man; }
    public int getId() { return projectId; }
    public String getProjectName() { return projectName; }
    public void setProjectName(String projectName) { this.projectName = projectName; }
    public String getNeighbourhood() { return neighbourhood; }
    public void setNeighbourhood(String neighbourhood) { this.neighbourhood = neighbourhood; }
    public List<String> getFlatTypes() { return new ArrayList<>(flatTypes); }
    public List<Integer> getTotalUnits() { return new ArrayList<>(totalUnits); }
    public List<Integer> getAvailableUnits() { return new ArrayList<>(availableUnits); }
    public List<Enquiry> getEnquiries() { return enquiries; }
    public void addEnquiry(Enquiry enquiry) { enquiries.add(enquiry); }
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
    public void updateFlatTypeUnits(String flatType, int newTotalUnits) {
        int index = flatTypes.indexOf(flatType);
        if (index >= 0) {
            int diff = newTotalUnits - totalUnits.get(index);
            totalUnits.set(index, newTotalUnits);
            int newAvailable = availableUnits.get(index) + diff;
            if (newAvailable < 0) newAvailable = 0;
            if (newAvailable > newTotalUnits) newAvailable = newTotalUnits;
            availableUnits.set(index, newAvailable);
        } else {
            System.out.println("Error: Flat type '" + flatType + "' not found.");
        }
    }
    public void updateAvailableUnits(String flatType, int newAvailableUnits) {
        int index = flatTypes.indexOf(flatType.toUpperCase());
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
    public LocalDate getOpenDate() { return openDate; }
    public void setOpenDate(LocalDate openDate) { this.openDate = openDate; }
    public LocalDate getCloseDate() { return closeDate; }
    public void setCloseDate(LocalDate closeDate) { this.closeDate = closeDate; }
    public boolean isVisible() { return visibility; }
    public void setVisibility(boolean visibility) { this.visibility = visibility; }
    public boolean addOfficer(HDB_Officer officer) {
        if (assignedOfficers.size() < totalOfficerSlots) {
            assignedOfficers.add(officer);
            return true;
        }
        return false;
    }
    public HDB_Manager getManager() { return manager; }
    public static int getMaxOfficerSlots() { return maxOfficerSlots; }
    public int getTotalOfficerSlots() { return totalOfficerSlots; }
    public void setTotalOfficerSlots(int totalOfficerSlots) { this.totalOfficerSlots = totalOfficerSlots; }
    
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("%-5s %-20s %-15s %-15s %-10s %-15s %-15s %-10s %-15s %-15s%n",
            projectId,
            projectName,
            neighbourhood,
            flatTypes.size() > 0 ? flatTypes.get(0) + ": " + (totalUnits.get(0) - availableUnits.get(0)) + "/" + totalUnits.get(0) : "",
            flatTypes.size() > 0 ? getFlatPrice(flatTypes.get(0)) : 0,
            openDate,
            closeDate,
            visibility,
            manager.getFirstName(),
            assignedOfficers.size() + "/" + totalOfficerSlots));
    
        for (int i = 1; i < flatTypes.size(); i++) {
            sb.append(String.format("%-5s %-20s %-15s %-15s %-15s %-15s %-10s %-15s %-15s%n",
                "", "", "",
                flatTypes.get(i) + ": " + (totalUnits.get(i) - availableUnits.get(i)) + "/" + totalUnits.get(i),
                flatTypes.size() > 0 ? getFlatPrice(flatTypes.get(i)) : 0,
                "", "", "", ""));
        }
        sb.append("\n");
        return sb.toString();
    }
    public double getFlatPrice(String flatType) {
        // Assumes a global list of flats is maintained elsewhere (e.g., in MainUI)
        // For simplicity, return 0 if not found.
        return 0;
    }
}
