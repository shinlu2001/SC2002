package SC2002.Project.entity;

import SC2002.Project.entity.enums.Visibility;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Entity representing a BTO project.
 */
public class Project {
    public static final int MAX_OFFICER_SLOTS = 10; 

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
    public List<Enquiry> getEnquiries() { return enquiries; }

    // ─────────── Setters (for controllers) ───────────

    public void setName(String name) { this.name = name; }
    public void setNeighbourhood(String neighbourhood) { this.neighbourhood = neighbourhood; }
    public void setFlatTypes(List<String> flatTypes) { this.flatTypes = new ArrayList<>(flatTypes); }
    public void setTotalUnits(List<Integer> totalUnits) { this.totalUnits = new ArrayList<>(totalUnits); }
    public void setAvailableUnits(List<Integer> availableUnits) { this.availableUnits = new ArrayList<>(availableUnits); }
    public void setPrices(List<Double> prices) { this.prices = new ArrayList<>(prices); }
    public void setOpenDate(LocalDate openDate) { this.openDate = openDate; }
    public void setCloseDate(LocalDate closeDate) { this.closeDate = closeDate; }
    public void setVisibility(Visibility visibility) { this.visibility = visibility; }
    public void setManager(HDB_Manager manager) { this.manager = manager; }
    public void setOfficerSlotLimit(int officerSlotLimit) { this.officerSlotLimit = officerSlotLimit; }

    // ─────────── Mutators ───────────

    /**
     * Assigns a new HDB officer to this project.
     */
    public void addAssignedOfficer(HDB_Officer officer) {
        if (!assignedOfficers.contains(officer)) {
            assignedOfficers.add(officer);
        }
    }

    /**
     * Removes an HDB officer from this project.
     */
    public void removeAssignedOfficer(HDB_Officer officer) {
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
    public void removeEnquiry(Enquiry enquiry) {
        enquiries.remove(enquiry);
    }

    // ─────────── Flat Type/Unit/Price Mutators ───────────

    public boolean updateFlatTypeUnits(String flatType, int newUnits) {
        int index = flatTypes.indexOf(flatType.toUpperCase());
        if (index != -1) {
            if (newUnits < 0) {
                System.err.println("Error: Cannot set negative units for flat type " + flatType);
                return false;
            }
            
            // Adjust available units based on the change in total units
            int oldTotalUnits = totalUnits.get(index);
            int currentAvailable = availableUnits.get(index);
            
            // Guard against integer overflow by using long for intermediate calculations
            long diff = (long)newUnits - (long)oldTotalUnits;
            
            // Calculate new available units, ensuring it stays within valid bounds
            long newAvailable = Math.max(0, currentAvailable + diff);
            newAvailable = Math.min(newAvailable, newUnits);  // Can't have more available than total
            
            // Update values after all calculations are safely completed
            totalUnits.set(index, newUnits);
            availableUnits.set(index, (int)newAvailable);
            
            return true;
        }
        System.err.println("Error: Flat type " + flatType + " not found in project " + name);
        return false;
    }

    public boolean addFlatType(String flatType, int units, double price) {
        String upperFlatType = flatType.toUpperCase();
        if (!flatTypes.contains(upperFlatType)) {
            flatTypes.add(upperFlatType);
            totalUnits.add(units);
            availableUnits.add(units); // Initially, all units are available
            prices.add(price);
            return true;
        }
        return false; // Flat type already exists
    }

    public boolean removeFlatType(String flatType, int currentUnits /* Unused but kept for signature match */) {
        int index = flatTypes.indexOf(flatType.toUpperCase());
        if (index != -1) {
            // Consider implications: What happens to applications for this flat type?
            // For now, just remove it. Add checks if needed (e.g., cannot remove if applications exist).
            flatTypes.remove(index);
            totalUnits.remove(index);
            availableUnits.remove(index);
            prices.remove(index);
            return true;
        }
        return false;
    }

    public boolean updateFlatPrice(String flatType, double newPrice) {
        int index = flatTypes.indexOf(flatType.toUpperCase());
        if (index != -1) {
            prices.set(index, newPrice);
            return true;
        }
        return false;
    }

    // ─────────── Derived Helpers ───────────

    /**
     * Get the index of a flat type in the flatTypes list.
     * @param flatType The flat type to look for
     * @return The index of the flat type, or -1 if not found
     */
    public int getFlatTypeIndex(String flatType) {
        return this.flatTypes.indexOf(flatType.toUpperCase());
    }

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
        // Check index validity before accessing the list
        if (idx >= 0 && idx < prices.size()) {
            Double price = prices.get(idx);
            return price != null ? price : 0.0; // Handle potential null Double
        }
        return 0.0; // Return default if index is invalid
    }

    /**
     * Get the total number of units for a specific flat type
     * @param flatType The flat type to check
     * @return Total number of units for the specified flat type, or 0 if not found
     */
    public int getTotalUnits(String flatType) {
        int idx = this.flatTypes.indexOf(flatType.toUpperCase());
        if (idx >= 0 && idx < totalUnits.size()) {
            return totalUnits.get(idx);
        }
        return 0;
    }

    /**
     * Get the number of remaining (available) units for a specific flat type
     * @param flatType The flat type to check
     * @return Number of remaining units for the specified flat type, or 0 if not found
     */
    public int getRemainingUnits(String flatType) {
        int idx = this.flatTypes.indexOf(flatType.toUpperCase());
        if (idx >= 0 && idx < availableUnits.size()) {
            return availableUnits.get(idx);
        }
        return 0;
    }
    
    /**
     * Get the price for a specific flat type
     * @param flatType The flat type to check
     * @return Price for the specified flat type, or 0 if not found
     */
    public double getFlatTypePrice(String flatType) {
        return getFlatPrice(flatType);
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
    public void decrementAvailableUnits(String flatType) {
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
    public void incrementAvailableUnits(String flatType) {
        int idx = this.flatTypes.indexOf(flatType.toUpperCase());
        if (idx != -1 && availableUnits.get(idx) < totalUnits.get(idx)) {
            availableUnits.set(idx, availableUnits.get(idx) + 1);
        } else {
            System.err.println("Warning: cannot increment units for " + flatType + " in project " + name);
        }
    }

    /**
     * Checks if this project's application period overlaps with another project's period.
     * Overlap occurs if one starts before the other ends.
     * @param other The other project to compare against.
     * @return true if the periods overlap, false otherwise.
     */
    public boolean overlapsWith(Project other) {
        if (other == null) return false;
        // No overlap if this project ends before the other starts OR this project starts after the other ends.
        return !(this.closeDate.isBefore(other.openDate) || this.openDate.isAfter(other.closeDate));
    }
}
