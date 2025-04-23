package SC2002.Project.entity;

import SC2002.Project.entity.enums.Visibility;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Entity representing a BTO housing project in the system.
 * <p>
 * This class is central to the system and contains all information about a
 * specific
 * BTO housing project including:
 * <ul>
 * <li>Project details (name, neighborhood, dates)</li>
 * <li>Available flat types, quantities, and prices</li>
 * <li>Officer assignments and slot management</li>
 * <li>Visibility control for applicant access</li>
 * <li>Enquiry tracking</li>
 * </ul>
 * </p>
 * <p>
 * Fulfills the following requirements:
 * <ul>
 * <li>Stores complete BTO project information as required by managers</li>
 * <li>Tracks available units for each flat type</li>
 * <li>Controls project visibility to applicants</li>
 * <li>Manages officer assignments with slot limits</li>
 * <li>Maintains application opening/closing dates</li>
 * <li>Tracks project-specific enquiries</li>
 * </ul>
 * </p>
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
     * Constructs a new Project with all required details.
     * This constructor is used by managers when creating new projects.
     *
     * @param id               unique project ID
     * @param name             project name
     * @param neighbourhood    neighbourhood name
     * @param flatTypes        list of flat type names (e.g., "2-ROOM")
     * @param totalUnits       total units per flat type
     * @param prices           selling price per flat type
     * @param openDate         application opening date
     * @param closeDate        application closing date
     * @param visibility       initial visibility (ON/OFF)
     * @param officerSlotLimit max number of officers (max 10)
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

    /**
     * Gets the unique ID of this project.
     * 
     * @return The project's ID
     */
    public int getId() {
        return id;
    }

    /**
     * Gets the name of this project.
     * 
     * @return The project name
     */
    public String getName() {
        return name;
    }

    /**
     * Gets the neighborhood where this project is located.
     * 
     * @return The neighborhood name
     */
    public String getNeighbourhood() {
        return neighbourhood;
    }

    /**
     * Gets the list of flat types offered in this project.
     * 
     * @return An unmodifiable list of flat types
     */
    public List<String> getFlatTypes() {
        return List.copyOf(flatTypes);
    }

    /**
     * Gets the total number of units for each flat type.
     * 
     * @return An unmodifiable list of unit counts
     */
    public List<Integer> getTotalUnits() {
        return List.copyOf(totalUnits);
    }

    /**
     * Gets the number of available (unbooked) units for each flat type.
     * 
     * @return An unmodifiable list of available unit counts
     */
    public List<Integer> getAvailableUnits() {
        return List.copyOf(availableUnits);
    }

    /**
     * Gets the prices for each flat type.
     * 
     * @return An unmodifiable list of prices
     */
    public List<Double> getPrices() {
        return List.copyOf(prices);
    }

    /**
     * Gets the application opening date for this project.
     * 
     * @return The opening date
     */
    public LocalDate getOpenDate() {
        return openDate;
    }

    /**
     * Gets the application closing date for this project.
     * 
     * @return The closing date
     */
    public LocalDate getCloseDate() {
        return closeDate;
    }

    /**
     * Gets the current visibility status of this project.
     * 
     * @return The visibility status (ON/OFF)
     */
    public Visibility getVisibility() {
        return visibility;
    }

    /**
     * Gets the maximum number of officers that can be assigned to this project.
     * 
     * @return The officer slot limit
     */
    public int getOfficerSlotLimit() {
        return officerSlotLimit;
    }

    /**
     * Gets the manager in charge of this project.
     * 
     * @return The HDB manager
     */
    public HDB_Manager getManager() {
        return manager;
    }

    /**
     * Gets the list of officers assigned to this project.
     * 
     * @return An unmodifiable list of assigned officers
     */
    public List<HDB_Officer> getAssignedOfficers() {
        return List.copyOf(assignedOfficers);
    }

    /**
     * Gets the list of enquiries related to this project.
     * 
     * @return The list of enquiries
     */
    public List<Enquiry> getEnquiries() {
        return enquiries;
    }

    // ─────────── Setters (for controllers) ───────────

    /**
     * Sets the name of this project.
     * Part of the requirement for managers to edit project details.
     * 
     * @param name The new project name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Sets the neighborhood of this project.
     * Part of the requirement for managers to edit project details.
     * 
     * @param neighbourhood The new neighborhood name
     */
    public void setNeighbourhood(String neighbourhood) {
        this.neighbourhood = neighbourhood;
    }

    /**
     * Sets the list of flat types for this project.
     * Part of the requirement for managers to edit project details.
     * 
     * @param flatTypes The new list of flat types
     */
    public void setFlatTypes(List<String> flatTypes) {
        this.flatTypes = new ArrayList<>(flatTypes);
    }

    /**
     * Sets the total number of units for each flat type.
     * Part of the requirement for managers to edit project details.
     * 
     * @param totalUnits The new list of total unit counts
     */
    public void setTotalUnits(List<Integer> totalUnits) {
        this.totalUnits = new ArrayList<>(totalUnits);
    }

    /**
     * Sets the number of available units for each flat type.
     * 
     * @param availableUnits The new list of available unit counts
     */
    public void setAvailableUnits(List<Integer> availableUnits) {
        this.availableUnits = new ArrayList<>(availableUnits);
    }

    /**
     * Sets the prices for each flat type.
     * Part of the requirement for managers to edit project details.
     * 
     * @param prices The new list of prices
     */
    public void setPrices(List<Double> prices) {
        this.prices = new ArrayList<>(prices);
    }

    /**
     * Sets the application opening date for this project.
     * Part of the requirement for managers to edit project details.
     * 
     * @param openDate The new opening date
     */
    public void setOpenDate(LocalDate openDate) {
        this.openDate = openDate;
    }

    /**
     * Sets the application closing date for this project.
     * Part of the requirement for managers to edit project details.
     * 
     * @param closeDate The new closing date
     */
    public void setCloseDate(LocalDate closeDate) {
        this.closeDate = closeDate;
    }

    /**
     * Sets the visibility status of this project.
     * Fulfills the requirement for managers to toggle project visibility.
     * 
     * @param visibility The new visibility status
     */
    public void setVisibility(Visibility visibility) {
        this.visibility = visibility;
    }

    /**
     * Sets the manager in charge of this project.
     * 
     * @param manager The new manager
     */
    public void setManager(HDB_Manager manager) {
        this.manager = manager;
    }

    /**
     * Sets the maximum number of officers that can be assigned to this project.
     * Part of the requirement for managers to edit project details.
     * 
     * @param officerSlotLimit The new officer slot limit
     */
    public void setOfficerSlotLimit(int officerSlotLimit) {
        this.officerSlotLimit = officerSlotLimit;
    }

    // ─────────── Mutators ───────────

    /**
     * Assigns a new HDB officer to this project.
     * Fulfills the requirement for managers to approve officer registrations.
     * 
     * @param officer The officer to assign
     */
    public void addAssignedOfficer(HDB_Officer officer) {
        if (!assignedOfficers.contains(officer)) {
            assignedOfficers.add(officer);
        }
    }

    /**
     * Removes an HDB officer from this project.
     * 
     * @param officer The officer to remove
     */
    public void removeAssignedOfficer(HDB_Officer officer) {
        assignedOfficers.remove(officer);
    }

    /**
     * Records a new enquiry related to this project.
     * Fulfills the requirement for users to submit enquiries about projects.
     * 
     * @param enquiry The enquiry to add
     */
    public void addEnquiry(Enquiry enquiry) {
        enquiries.add(enquiry);
    }

    /**
     * Removes an enquiry from this project.
     * 
     * @param enquiry The enquiry to remove
     */
    public void removeEnquiry(Enquiry enquiry) {
        enquiries.remove(enquiry);
    }

    // ─────────── Flat Type/Unit/Price Mutators ───────────

    /**
     * Updates the number of units for a specific flat type.
     * Part of the requirement for managers to edit project details.
     * 
     * @param flatType The flat type to update
     * @param newUnits The new number of total units
     * @return True if successful, false otherwise
     */
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
            long diff = (long) newUnits - (long) oldTotalUnits;

            // Calculate new available units, ensuring it stays within valid bounds
            long newAvailable = Math.max(0, currentAvailable + diff);
            newAvailable = Math.min(newAvailable, newUnits); // Can't have more available than total

            // Update values after all calculations are safely completed
            totalUnits.set(index, newUnits);
            availableUnits.set(index, (int) newAvailable);

            return true;
        }
        System.err.println("Error: Flat type " + flatType + " not found in project " + name);
        return false;
    }

    /**
     * Adds a new flat type to this project.
     * Part of the requirement for managers to edit project details.
     * 
     * @param flatType The flat type to add
     * @param units    The number of units
     * @param price    The price per unit
     * @return True if successful, false if flat type already exists
     */
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

    /**
     * Removes a flat type from this project.
     * Part of the requirement for managers to edit project details.
     * 
     * @param flatType     The flat type to remove
     * @param currentUnits Unused but kept for signature match
     * @return True if successful, false if flat type not found
     */
    public boolean removeFlatType(String flatType, int currentUnits /* Unused but kept for signature match */) {
        int index = flatTypes.indexOf(flatType.toUpperCase());
        if (index != -1) {
            // Consider implications: What happens to applications for this flat type?
            // For now, just remove it. Add checks if needed (e.g., cannot remove if
            // applications exist).
            flatTypes.remove(index);
            totalUnits.remove(index);
            availableUnits.remove(index);
            prices.remove(index);
            return true;
        }
        return false;
    }

    /**
     * Updates the price for a specific flat type.
     * Part of the requirement for managers to edit project details.
     * 
     * @param flatType The flat type to update
     * @param newPrice The new price
     * @return True if successful, false if flat type not found
     */
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
     * Gets the index of a flat type in the flatTypes list.
     * 
     * @param flatType The flat type to look for
     * @return The index of the flat type, or -1 if not found
     */
    public int getFlatTypeIndex(String flatType) {
        return this.flatTypes.indexOf(flatType.toUpperCase());
    }

    /**
     * Checks if the project is currently open for applications.
     * This is used to determine if applicants can apply for this project.
     * 
     * @return True if today is between openDate and closeDate (inclusive)
     */
    public boolean isOpen() {
        LocalDate today = LocalDate.now();
        return !today.isBefore(openDate) && !today.isAfter(closeDate);
    }

    /**
     * Gets the price for a specific flat type.
     * 
     * @param flatType The flat type to check
     * @return The price for the specified flat type, or 0 if not found
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
     * Gets the total number of units for a specific flat type.
     * 
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
     * Gets the number of remaining (available) units for a specific flat type.
     * This is used by managers when approving applications to check if units are
     * available.
     * 
     * @param flatType The flat type to check
     * @return Number of remaining units for the specified flat type, or 0 if not
     *         found
     */
    public int getRemainingUnits(String flatType) {
        int idx = this.flatTypes.indexOf(flatType.toUpperCase());
        if (idx >= 0 && idx < availableUnits.size()) {
            return availableUnits.get(idx);
        }
        return 0;
    }

    /**
     * Gets the price for a specific flat type.
     * 
     * @param flatType The flat type to check
     * @return Price for the specified flat type, or 0 if not found
     */
    public double getFlatTypePrice(String flatType) {
        return getFlatPrice(flatType);
    }

    /**
     * Checks if the project is currently visible to applicants.
     * Fulfills the requirement for toggling project visibility.
     * 
     * @return True if the project is marked visible
     */
    public boolean isVisible() {
        return visibility == Visibility.ON;
    }

    /**
     * Decrements the number of available units for a specific flat type.
     * This method should only be called by managers during the final booking
     * approval process.
     * 
     * @param flatType The flat type to decrement
     * @return True if units were successfully decremented, false otherwise
     */
    public boolean decrementAvailableUnits(String flatType) {
        int idx = this.flatTypes.indexOf(flatType.toUpperCase());
        if (idx == -1) {
            System.err.println("Warning: Flat type " + flatType + " not found in project " + name);
            return false;
        }

        if (availableUnits.get(idx) > 0) {
            availableUnits.set(idx, availableUnits.get(idx) - 1);
            return true;
        } else {
            System.err.println("Warning: Cannot decrement units for " + flatType + " in project " + name
                    + " - no available units");
            return false;
        }
    }

    /**
     * Increments the number of available units for a specific flat type.
     * Called when a booking is cancelled or withdrawn.
     * 
     * @param flatType The flat type to increment
     */
    public void incrementAvailableUnits(String flatType) {
        int idx = this.flatTypes.indexOf(flatType.toUpperCase());
        if (idx == -1) {
            System.err.println("Warning: Flat type " + flatType + " not found in project " + name);
            return;
        }

        if (availableUnits.get(idx) < totalUnits.get(idx)) {
            availableUnits.set(idx, availableUnits.get(idx) + 1);
        } else {
            System.err.println("Warning: Cannot increment units for " + flatType + " in project " + name
                    + " - already at maximum capacity");
        }
    }

    /**
     * Checks if this project's application period overlaps with another project's
     * period.
     * This is used to enforce the requirement that managers can only handle one
     * project
     * within an application period.
     * 
     * @param other The other project to compare against
     * @return True if the periods overlap, false otherwise
     */
    public boolean overlapsWith(Project other) {
        if (other == null)
            return false;
        // No overlap if this project ends before the other starts OR this project
        // starts after the other ends.
        return !(this.closeDate.isBefore(other.openDate) || this.openDate.isAfter(other.closeDate));
    }
}
