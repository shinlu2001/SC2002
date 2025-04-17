package SC2002.Project.entity;

import java.time.LocalDate;
import java.util.*;

import SC2002.Project.util.IdGenerator;
import SC2002.Project.entity.enums.Visibility;

public class Project {

    /* ---------- identity & basic data ---------- */
    private final int    id;
    private String       name;
    private String       neighbourhood;
    private List<String> flatTypes;          // "2-ROOM", "3-ROOM", ..
    private List<Integer> totalUnits;        // parallel list
    private List<Integer> availableUnits;    // parallel list
    private List<Double>  prices;            // parallel list

    private LocalDate    openDate;
    private LocalDate    closeDate;
    private Visibility visibility = Visibility.ON;   // default visible

    /* ---------- associations ---------- */
    private HDB_Manager          manager;            // set by CSVReader / ManagerController
    private final List<HDB_Officer> assignedOfficers = new ArrayList<>();

    /* ---------- configuration ---------- */
    private int officerSlotLimit;           // max 10 by requirement

    /* ---------- enquiries for this project ---------- */
    private final List<Enquiry> enquiries = new ArrayList<>();

    /* ---------- ctor ---------- */
    public Project(String name,
                   String neighbourhood,
                   List<String> flatTypes,
                   List<Integer> totalUnits,
                   List<Double>  prices,
                   LocalDate open,
                   LocalDate close,
                   boolean visible,
                   int officerSlots)
    {
        this.id            = IdGenerator.nextProjectId();
        this.name          = name;
        this.neighbourhood = neighbourhood;

        this.flatTypes     = new ArrayList<>(flatTypes);
        this.totalUnits    = new ArrayList<>(totalUnits);
        this.availableUnits= new ArrayList<>(totalUnits);     // initially all available
        this.prices        = new ArrayList<>(prices);

        this.openDate      = open;
        this.closeDate     = close;
        this.visibility    = visible ? Visibility.ON : Visibility.OFF;

        this.officerSlotLimit = officerSlots;
    }

    /* ---------- getters ---------- */
    public int          getId()               { return id; }
    public String       getName()             { return name; }
    public String       getNeighbourhood()    { return neighbourhood; }
    public List<String> getFlatTypes()        { return List.copyOf(flatTypes); }
    public List<Integer>getTotalUnits()       { return List.copyOf(totalUnits); }
    public List<Integer>getAvailableUnits()   { return List.copyOf(availableUnits); }
    public List<Double> getPrices()           { return List.copyOf(prices); }

    public LocalDate    getOpenDate()         { return openDate; }
    public LocalDate    getCloseDate()        { return closeDate; }
    public Visibility getVisibility()    { return visibility; }

    public HDB_Manager          getManager()  { return manager; }
    public List<HDB_Officer>    getAssignedOfficers() { return List.copyOf(assignedOfficers); }

    public int getOfficerSlotLimit()          { return officerSlotLimit; }
    public List<Enquiry> getEnquiries()       { return List.copyOf(enquiries); }

    /* ---------- setters used by controllers ---------- */
    /* (package‑private to limit direct UI usage) */
    void setName(String n)                    { this.name = n; }
    void setNeighbourhood(String n)           { this.neighbourhood = n; }
    void setFlatTypes(List<String> f)         { this.flatTypes = new ArrayList<>(f); }
    void setTotalUnits(List<Integer> u)       { this.totalUnits = new ArrayList<>(u); }
    void setAvailableUnits(List<Integer> a)   { this.availableUnits = new ArrayList<>(a); }
    void setPrices(List<Double> p)            { this.prices = new ArrayList<>(p); }
    void setOpenDate(LocalDate d)             { this.openDate = d; }
    void setCloseDate(LocalDate d)            { this.closeDate = d; }
    void setVisibility(Visibility v)     { this.visibility = v; }
    public void setManager(HDB_Manager m)            { this.manager = m; }
    void setOfficerSlotLimit(int s)           { this.officerSlotLimit = s; }

    /* add / remove mutators accessed by controllers */
    void addOfficer(HDB_Officer off)          { assignedOfficers.add(off); }
    void removeOfficer(HDB_Officer off)       { assignedOfficers.remove(off); }
    void addEnquiry(Enquiry e)                { enquiries.add(e); }

    /* ---------- derived helpers ---------- */
    public boolean isOpen() {
        LocalDate today = LocalDate.now();
        return !today.isBefore(openDate) && !today.isAfter(closeDate);
    }

    /** price for a given flat type (0 if not found) */
    public double getFlatPrice(String flatType) {
        int idx = flatTypes.indexOf(flatType.toUpperCase());
        return idx >= 0 ? prices.get(idx) : 0.0;
    }
}
