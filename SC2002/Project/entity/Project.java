package SC2002.Project.entity;

import SC2002.Project.entity.enums.Visibility;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Entity representing a BTO project.
 */
public class Project {
    private final int id;
    private String name;
    private String neighbourhood;
    private List<String> flatTypes;
    private List<Integer> totalUnits;
    private List<Integer> availableUnits;
    private List<Double> prices;
    private LocalDate openDate;
    private LocalDate closeDate;
    private Visibility visibility;
    private int officerSlotLimit;
    private HDB_Manager manager;

    private final List<HDB_Officer> assignedOfficers = new ArrayList<>();
    private final List<Enquiry> enquiries = new ArrayList<>();

    /**
     * Constructs a new Project.
     *
     * @param id                unique project ID
     * @param name              project name
     * @param neighbourhood     neighbourhood name
     * @param flatTypes         list of flat type names (e.g., "2-ROOM")
     * @param totalUnits        total units per flat type
     * @param prices            selling price per flat type
     * @param openDate          application opening date
     * @param closeDate         application closing date
     * @param visibility        initial visibility (ON/OFF)
     * @param officerSlotLimit  max number of officers
     */
    public Project(int id,
                   String name,
                   String neighbourhood,
                   List<String> flatTypes,
                   List<Integer> totalUnits,
                   List<Double> prices,
                   LocalDate openDate,
                   LocalDate closeDate,
                   Visibility visibility,
                   int officerSlotLimit) {
        this.id = id;
        this.name = name;
        this.neighbourhood = neighbourhood;
        this.flatTypes = new ArrayList<>(flatTypes);
        this.totalUnits = new ArrayList<>(totalUnits);
        this.availableUnits = new ArrayList<>(totalUnits);
        this.prices = new ArrayList<>(prices);
        this.openDate = openDate;
        this.closeDate = closeDate;
        this.visibility = visibility;
        this.officerSlotLimit = officerSlotLimit;
    }

    // ─────────── Getters ───────────

    public int getId() { return id; }
    public String getName() { return name; }
    public String getNeighbourhood() { return neighbourhood; }
    public List<String> getFlatTypes() { return List.copyOf(flatTypes); }
    public List<Integer> getTotalUnits() { return List.copyOf(totalUnits); }
    public List<Integer> getAvailableUnits() { return List.copyOf(availableUnits); }
    public List<Double> getPrices() { return List.copyOf(prices); }
    public LocalDate getOpenDate() { return openDate; }
    public LocalDate getCloseDate() { return closeDate; }
    public Visibility getVisibility() { return visibility; }
    public int getOfficerSlotLimit() { return officerSlotLimit; }
    public HDB_Manager getManager() { return manager; }
    public List<HDB_Officer> getAssignedOfficers() { return List.copyOf(assignedOfficers); }
    public List<Enquiry> getEnquiries() { return List.copyOf(enquiries); }

    // ─────────── Setters (for controllers) ───────────

    public void setName(String name) { this.name = name; }
    public void setNeighbourhood(String neighbourhood) { this.neighbourhood = neighbourhood; }
    public void setFlatTypes(List<String> flatTypes) { this.flatTypes = new ArrayList<>(flatTypes); }
    void setTotalUnits(List<Integer> totalUnits) { this.totalUnits = new ArrayList<>(totalUnits); }
    void setAvailableUnits(List<Integer> availableUnits) { this.availableUnits = new ArrayList<>(availableUnits); }
    void setPrices(List<Double> prices) { this.prices = new ArrayList<>(prices); }
    void setOpenDate(LocalDate openDate) { this.openDate = openDate; }
    void setCloseDate(LocalDate closeDate) { this.closeDate = closeDate; }
    public void setVisibility(Visibility visibility) { this.visibility = visibility; }
    public void setManager(HDB_Manager manager) { this.manager = manager; }
    public void setOfficerSlotLimit(int officerSlotLimit) { this.officerSlotLimit = officerSlotLimit; }

    // ─────────── Mutators ───────────

    /**
     * Assigns a new HDB officer to this project.
     */
    public void addOfficer(HDB_Officer officer) {
        assignedOfficers.add(officer);
    }

    /**
     * Removes an HDB officer from this project.
     */
    void removeOfficer(HDB_Officer officer) {
        assignedOfficers.remove(officer);
    }

    /**
     * Records a new enquiry related to this project.
     */
    public void addEnquiry(Enquiry enquiry) {
        enquiries.add(enquiry);
    }

    /**
     * Removes an enquiry from this project.
     */
    void removeEnquiry(Enquiry enquiry) {
        enquiries.remove(enquiry);
    }

    // ─────────── Derived Helpers ───────────

    /**
     * @return true if today is between openDate and closeDate (inclusive)
     */
    public boolean isOpen() {
        LocalDate today = LocalDate.now();
        return !today.isBefore(openDate) && !today.isAfter(closeDate);
    }

    /**
     * @return the price for the given flat type, or 0 if not found
     */
    public double getFlatPrice(String flatType) {
        int idx = this.flatTypes.indexOf(flatType.toUpperCase());
        return idx >= 0 ? prices.get(idx) : 0.0;
    }

    /**
     * @return true if the project is marked visible
     */
    public boolean isVisible() {
        return visibility == Visibility.ON;
    }

    /**
     * Decrements available units for the specified flat type.
     */
    void decrementAvailableUnits(String flatType) {
        int idx = this.flatTypes.indexOf(flatType.toUpperCase());
        if (idx != -1 && availableUnits.get(idx) > 0) {
            availableUnits.set(idx, availableUnits.get(idx) - 1);
        } else {
            System.err.println("Warning: cannot decrement units for " + flatType + " in project " + name);
        }
    }

    /**
     * Increments available units for the specified flat type.
     */
    void incrementAvailableUnits(String flatType) {
        int idx = this.flatTypes.indexOf(flatType.toUpperCase());
        if (idx != -1 && availableUnits.get(idx) < totalUnits.get(idx)) {
            availableUnits.set(idx, availableUnits.get(idx) + 1);
        } else {
            System.err.println("Warning: cannot increment units for " + flatType + " in project " + name);
        }
    }
}
