package SC2002.Project.control.persistence;

import SC2002.Project.entity.*;
import SC2002.Project.entity.enums.ApplicationStatus;
import SC2002.Project.entity.enums.MaritalStatus;
import SC2002.Project.entity.enums.RegistrationStatus;
import SC2002.Project.entity.enums.Visibility;
import SC2002.Project.util.IdGenerator;
import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * Loads data from CSV files into in-memory DataStore
 */
public final class CSVReader {
    private CSVReader() {}

    private static final Path BASE = Paths.get("SC2002/Project/files");
    private static final DateTimeFormatter DF = DateTimeFormatter.ofPattern("d/M/yyyy");
    /** Track project names to avoid duplicates */
    private static final Set<String> PROJECT_NAMES = new HashSet<>();
    
    // Flag to determine whether to load data from snapshot files or original files
    private static boolean useSnapshot = false;
    
    /**
     * Set whether to load from snapshot files or original files
     * @param useSnapshotFiles true to use snapshot files, false to use original files
     */
    public static void setUseSnapshot(boolean useSnapshotFiles) {
        useSnapshot = useSnapshotFiles;
    }
    
    /**
     * Get current snapshot usage mode
     * @return true if using snapshot files, false if using original files
     */
    public static boolean isUsingSnapshot() {
        return useSnapshot;
    }
    
    /**
     * Load all data based on the current snapshot setting
     */
    public static void loadAll() {
        // Clear all data before loading
        DataStore ds = DataStore.getInstance();
        ds.getUsers().clear();
        ds.getProjects().clear();
        ds.getApplications().clear();
        ds.getEnquiries().clear();
        ds.getFlats().clear();
        ds.getRegistrations().clear();
        PROJECT_NAMES.clear();
        
        if (useSnapshot) {
            loadAllFromSnapshot();
        } else {
            loadAllFromOriginal();
        }
    }
    
    /**
     * Load data from original CSV files (minimal data)
     */
    private static void loadAllFromOriginal() {
        DataStore ds = DataStore.getInstance();
        readUsers("ApplicantList.csv", "APPLICANT", ds);
        readUsers("OfficerList.csv",   "OFFICER",   ds);
        readUsers("ManagerList.csv",   "MANAGER",   ds);
        readProjects(ds);
        
        System.out.println("Loaded data from original files.");
    }
    
    /**
     * Load data from snapshot CSV files (complete system state)
     */
    private static void loadAllFromSnapshot() {
        DataStore ds = DataStore.getInstance();
        
        // Load basic entities first (users, projects)
        readUsers("ApplicantListNew.csv", "APPLICANT", ds);
        readUsers("OfficerListNew.csv",   "OFFICER",   ds);
        readUsers("ManagerListNew.csv",   "MANAGER",   ds);
        readProjects(ds, "ProjectListNew.csv");
        
        // Now load relationship and derived data
        readRegistrations(ds, "RegistrationsNew.csv");
        readApplications(ds, "ApplicationsNew.csv");
        readEnquiries(ds, "EnquiriesNew.csv");
        readFlats(ds, "FlatsNew.csv");
        
        System.out.println("Loaded complete system snapshot from new files.");
    }

    private static void readUsers(String file, String role, DataStore ds) {
        Path p = BASE.resolve(file);
        try (BufferedReader br = Files.newBufferedReader(p)) {
            br.readLine(); // skip header
            String line;
            while ((line = br.readLine()) != null && !line.isBlank()) {
                String[] cols = line.replace("\uFEFF", "").split(",", -1);
                String fullName = cols[0].trim();
                String nric     = cols[1].trim();
                int age;
                try {
                    age = Integer.parseInt(cols[2].trim());
                    if (age < 0) {
                        System.err.println("Invalid age (" + age + ") for NRIC " + nric + ", skipping user.");
                        continue;
                    }
                } catch (NumberFormatException e) {
                    System.err.println("Invalid age format for NRIC " + nric + ", skipping user.");
                    continue;
                }
                MaritalStatus ms= MaritalStatus.valueOf(cols[3].trim().toUpperCase());
                String[] parts  = fullName.split("\\s+", 2);
                String firstName = parts[0];
                String lastName  = parts.length > 1 ? parts[1] : "";

                if (ds.findUserByNric(nric).isPresent()) {
                    System.err.println("Duplicate NRIC " + nric + ", skipping");
                    continue;
                }

                switch (role) {
                    case "APPLICANT" -> ds.getUsers().add(
                        new Applicant(nric, firstName, lastName, ms, age)
                    );
                    case "OFFICER"   -> ds.getUsers().add(
                        new HDB_Officer(nric, firstName, lastName, ms, age)
                    );
                    case "MANAGER"   -> ds.getUsers().add(
                        new HDB_Manager(nric, firstName, lastName, ms, age)
                    );
                }
            }
        } catch (IOException e) {
            System.err.println("Could not read " + file + ": " + e.getMessage());
        }
    }

    private static void readProjects(DataStore ds) {
        Path p = BASE.resolve("ProjectList.csv");
        String line = null; // Declare line outside the try block
        try (BufferedReader br = Files.newBufferedReader(p)) {
            br.readLine(); // header
            while ((line = br.readLine()) != null && !line.isBlank()) {
                String[] t = line.replace("\uFEFF", "").split(",", -1);

                String name = t[0].trim();
                // skip duplicates by project name
                if (!PROJECT_NAMES.add(name)) {
                    System.err.println("Duplicate project name '" + name + "', skipping");
                    continue;
                }

                String neighbourhood = t[1].trim();
                String type1         = t[2].trim().toUpperCase();
                int units1           = Integer.parseInt(t[3].trim());
                double price1        = Double.parseDouble(t[4].trim());
                String type2         = t[5].trim().toUpperCase();
                int units2           = Integer.parseInt(t[6].trim());
                double price2        = Double.parseDouble(t[7].trim());
                LocalDate openDate   = LocalDate.parse(t[8].trim(), DF);
                LocalDate closeDate  = LocalDate.parse(t[9].trim(), DF);
                String mgrName       = t[10].trim();
                int slotCount        = Integer.parseInt(t[11].trim());

                // create Project with generated ID:
                int projId = IdGenerator.nextProjectId();
                Project prj = new Project(
                    projId,
                    name,
                    neighbourhood,
                    List.of(type1, type2),
                    List.of(units1, units2),
                    List.of(price1, price2),
                    openDate,
                    closeDate,
                    Visibility.ON,
                    slotCount
                );

                // link manager using name lookup
                Optional<HDB_Manager> mgrOpt = ds.findManagerByName(mgrName);
                if (mgrOpt.isEmpty()) {
                    System.err.println("Error: Manager with name '" + mgrName + "' not found for project '" + name + 
                        "'. The project will be created without a manager assignment.");
                    System.err.println("To fix this issue: 1) Make sure the manager exists in ManagerList.csv, " + 
                        "2) Check the spelling of the manager name in ProjectList.csv");
                } else {
                    mgrOpt.ifPresent(m -> {
                        try {
                            prj.setManager(m);
                            m.addManagedProject(prj);
                            System.out.println("Manager " + m.getFirstName() + " successfully assigned to Project " + prj.getName() + ".");
                        } catch (Exception e) {
                            System.err.println("Error assigning Manager " + m.getFirstName() + 
                                " to Project " + prj.getName() + ": " + e.getMessage());
                        }
                    });
                }

                ds.getProjects().add(prj);

                // Initialize the available units to match the total units from the CSV
                List<String> flatTypes = prj.getFlatTypes();
                List<Integer> totalUnits = prj.getTotalUnits();
                List<Double> prices = prj.getPrices();
                
                // Create Flat objects for each unit in each flat type
                for (int i = 0; i < flatTypes.size(); i++) {
                    String type = flatTypes.get(i);
                    int numUnits = totalUnits.get(i);
                    double price = prices.get(i);
                    
                    // Create Flat objects for this flat type
                    for (int j = 0; j < numUnits; j++) {
                        // Use the simpler Flat constructor with just the essential information
                        Flat flat = new Flat(IdGenerator.nextFlatId(), prj, type, price);
                        ds.getFlats().add(flat);
                    }
                    
                    System.out.println("DEBUG: Created " + numUnits + " flats for Project " + prj.getName() + 
                                      ", Type: " + type);
                }

                // initial approved officers (col 12) - Names in quotes, potentially separated by ; or ,
                if (t.length > 12 && !t[12].isBlank()) {
                    String rawOfficerNames = t[12].replace("\"", "").trim(); // Remove quotes
                    
                    // Enhanced split to handle various delimiter patterns with better robustness
                    // This will split on commas or semicolons with any amount of surrounding whitespace
                    for (String offName : rawOfficerNames.split("\\s*[,;]\\s*")) {
                        if (offName.isBlank()) continue; // Skip empty entries if any

                        // Find officer by first name (assuming names in CSV are first names)
                        Optional<HDB_Officer> foundOfficer = ds.getUsers().stream()
                            .filter(u -> u instanceof HDB_Officer)
                            .map(u -> (HDB_Officer) u)
                            .filter(officer -> officer.getFirstName().equalsIgnoreCase(offName.trim()))
                            .findFirst();

                        if (foundOfficer.isPresent()) {
                            HDB_Officer off = foundOfficer.get();
                            // Check if already assigned (e.g., via explicit registration column)
                            boolean alreadyAssigned = prj.getAssignedOfficers().contains(off);
                            if (!alreadyAssigned) {
                                try {
                                    // Check if project has reached its officer slot limit
                                    if (prj.getAssignedOfficers().size() >= prj.getOfficerSlotLimit()) {
                                        System.err.println("Error: Cannot assign Officer " + off.getFirstName() + 
                                            " to Project " + prj.getName() + ". Officer slot limit (" + 
                                            prj.getOfficerSlotLimit() + ") has been reached.");
                                        continue;
                                    }
                                    
                                    // Create and register the officer
                                    int regId = IdGenerator.nextRegistrationId();
                                    Registration reg = new Registration(regId, off, prj);
                                    reg.setStatus(RegistrationStatus.APPROVED); // Directly approve based on CSV column
                                    ds.getRegistrations().add(reg);
                                    off.addRegistration(reg);
                                    prj.addAssignedOfficer(off); // Add to project's list
                                    off.addAssignedProject(prj); // Add to officer's assigned projects list
                                    System.out.println("Officer " + off.getFirstName() + " successfully assigned to Project " + prj.getName() + ".");
                                } catch (Exception e) {
                                    System.err.println("Error assigning Officer " + off.getFirstName() + 
                                        " to Project " + prj.getName() + ": " + e.getMessage());
                                }
                            } else {
                                System.out.println("Officer " + off.getFirstName() + " already assigned to Project " + prj.getName() + ".");
                            }
                        } else {
                             System.err.println("Error: Officer with name '" + offName + "' not found for project '" + 
                                name + "'. Please check that the officer exists and the name is spelled correctly in OfficerList.csv.");
                        }
                    }
                }

                // explicit registration column (13–15)
                if (t.length > 15) {
                    String offNric  = t[13].trim();
                    String projIdStr= t[14].trim();
                    String rs       = t[15].trim().toUpperCase();
                    try {
                        int pid = Integer.parseInt(projIdStr);
                        Optional<HDB_Officer> offOpt = ds.findUserByNric(offNric)
                                .filter(u -> u instanceof HDB_Officer)
                                .map(u -> (HDB_Officer)u);
                        Optional<Project> pOpt = ds.findProjectById(pid);

                        if (offOpt.isPresent() && pOpt.isPresent()) {
                            int regId = IdGenerator.nextRegistrationId();
                            Registration reg = new Registration(regId, offOpt.get(), pOpt.get());
                            if (rs.equals("APPROVED")) {
                                reg.approve();
                                pOpt.get().addAssignedOfficer(offOpt.get());
                            } else if (rs.equals("REJECTED")) {
                                reg.reject();
                            } else if (rs.equals("WITHDRAWN")) {
                                reg.withdraw();
                            } else if (rs.equals("PENDING")) {
                                // PENDING is default for new registrations
                                // No specific action needed
                            } else {
                                System.err.println("Unknown registration status: " + rs + " for registration ID: " + regId);
                            }
                            ds.getRegistrations().add(reg);
                            offOpt.get().addRegistration(reg);
                        }
                    } catch (NumberFormatException ex) {
                        System.err.println("Invalid Project ID in registration: " + projIdStr);
                    }
                }
            }
        } catch (IOException e) {
            System.err.println("Could not read ProjectList.csv: " + e.getMessage());
        } catch (Exception e) { // Catch broader exceptions during parsing
             System.err.println("Error processing line in ProjectList.csv: " + line + " - " + e.getMessage());
             e.printStackTrace(); // Print stack trace for debugging
        }
    }

    /**
     * Read projects from the specified file
     */
    private static void readProjects(DataStore ds, String filename) {
        Path p = BASE.resolve(filename);
        String line = null; // Declare line outside the try block
        try (BufferedReader br = Files.newBufferedReader(p)) {
            br.readLine(); // header
            while ((line = br.readLine()) != null && !line.isBlank()) {
                String[] t = line.replace("\uFEFF", "").split(",", -1);

                String name = t[0].trim();
                // skip duplicates by project name
                if (!PROJECT_NAMES.add(name)) {
                    System.err.println("Duplicate project name '" + name + "', skipping");
                    continue;
                }

                String neighbourhood = t[1].trim();
                String type1         = t[2].trim().toUpperCase();
                int units1           = Integer.parseInt(t[3].trim());
                double price1        = Double.parseDouble(t[4].trim());
                String type2         = t[5].trim().toUpperCase();
                int units2           = Integer.parseInt(t[6].trim());
                double price2        = Double.parseDouble(t[7].trim());
                LocalDate openDate   = LocalDate.parse(t[8].trim(), DF);
                LocalDate closeDate  = LocalDate.parse(t[9].trim(), DF);
                String mgrName       = t[10].trim();
                int slotCount        = Integer.parseInt(t[11].trim());

                // create Project with generated ID:
                int projId = IdGenerator.nextProjectId();
                Project prj = new Project(
                    projId,
                    name,
                    neighbourhood,
                    List.of(type1, type2),
                    List.of(units1, units2),
                    List.of(price1, price2),
                    openDate,
                    closeDate,
                    Visibility.ON,
                    slotCount
                );

                // link manager using name lookup
                Optional<HDB_Manager> mgrOpt = ds.findManagerByName(mgrName);
                if (mgrOpt.isEmpty()) {
                    System.err.println("Error: Manager with name '" + mgrName + "' not found for project '" + name + 
                        "'. The project will be created without a manager assignment.");
                } else {
                    mgrOpt.ifPresent(m -> {
                        try {
                            prj.setManager(m);
                            m.addManagedProject(prj);
                        } catch (Exception e) {
                            System.err.println("Error assigning Manager " + m.getFirstName() + 
                                " to Project " + prj.getName() + ": " + e.getMessage());
                        }
                    });
                }

                ds.getProjects().add(prj);

                // When reading from original files, we need to create Flat objects
                if (!useSnapshot) {
                    // Initialize the available units to match the total units from the CSV
                    List<String> flatTypes = prj.getFlatTypes();
                    List<Integer> totalUnits = prj.getTotalUnits();
                    List<Double> prices = prj.getPrices();
                    
                    // Create Flat objects for each unit in each flat type
                    for (int i = 0; i < flatTypes.size(); i++) {
                        String type = flatTypes.get(i);
                        int numUnits = totalUnits.get(i);
                        double price = prices.get(i);
                        
                        // Create Flat objects for this flat type
                        for (int j = 0; j < numUnits; j++) {
                            // Use the simpler Flat constructor with just the essential information
                            Flat flat = new Flat(IdGenerator.nextFlatId(), prj, type, price);
                            ds.getFlats().add(flat);
                        }
                    }
                }

                // initial approved officers (col 12) - Names in quotes, potentially separated by ; or ,
                if (t.length > 12 && !t[12].isBlank()) {
                    String rawOfficerNames = t[12].replace("\"", "").trim(); // Remove quotes
                    
                    // Enhanced split to handle various delimiter patterns with better robustness
                    // This will split on commas or semicolons with any amount of surrounding whitespace
                    for (String offName : rawOfficerNames.split("\\s*[,;]\\s*")) {
                        if (offName.isBlank()) continue; // Skip empty entries if any

                        // Find officer by first name (assuming names in CSV are first names)
                        Optional<HDB_Officer> foundOfficer = ds.getUsers().stream()
                            .filter(u -> u instanceof HDB_Officer)
                            .map(u -> (HDB_Officer) u)
                            .filter(officer -> officer.getFirstName().equalsIgnoreCase(offName.trim()))
                            .findFirst();

                        if (foundOfficer.isPresent() && !useSnapshot) {
                            HDB_Officer off = foundOfficer.get();
                            // Check if already assigned (e.g., via explicit registration column)
                            boolean alreadyAssigned = prj.getAssignedOfficers().contains(off);
                            if (!alreadyAssigned) {
                                try {
                                    // Check if project has reached its officer slot limit
                                    if (prj.getAssignedOfficers().size() >= prj.getOfficerSlotLimit()) {
                                        System.err.println("Error: Cannot assign Officer " + off.getFirstName() + 
                                            " to Project " + prj.getName() + ". Officer slot limit (" + 
                                            prj.getOfficerSlotLimit() + ") has been reached.");
                                        continue;
                                    }
                                    
                                    // Create and register the officer
                                    int regId = IdGenerator.nextRegistrationId();
                                    Registration reg = new Registration(regId, off, prj);
                                    reg.setStatus(RegistrationStatus.APPROVED); // Directly approve based on CSV column
                                    ds.getRegistrations().add(reg);
                                    off.addRegistration(reg);
                                    prj.addAssignedOfficer(off); // Add to project's list
                                    off.addAssignedProject(prj); // Add to officer's assigned projects list
                                } catch (Exception e) {
                                    System.err.println("Error assigning Officer " + off.getFirstName() + 
                                        " to Project " + prj.getName() + ": " + e.getMessage());
                                }
                            }
                        } else if (!foundOfficer.isPresent()) {
                            System.err.println("Error: Officer with name '" + offName + "' not found for project '" + 
                                name + "'. Please check that the officer exists and the name is spelled correctly.");
                        }
                    }
                }

                // explicit registration column (13–15)
                if (t.length > 15 && !useSnapshot) {
                    String offNric  = t[13].trim();
                    String projIdStr= t[14].trim();
                    String rs       = t[15].trim().toUpperCase();
                    try {
                        int pid = Integer.parseInt(projIdStr);
                        Optional<HDB_Officer> offOpt = ds.findUserByNric(offNric)
                                .filter(u -> u instanceof HDB_Officer)
                                .map(u -> (HDB_Officer)u);
                        Optional<Project> pOpt = ds.findProjectById(pid);

                        if (offOpt.isPresent() && pOpt.isPresent()) {
                            int regId = IdGenerator.nextRegistrationId();
                            Registration reg = new Registration(regId, offOpt.get(), pOpt.get());
                            if (rs.equals("APPROVED")) {
                                reg.approve();
                                pOpt.get().addAssignedOfficer(offOpt.get());
                            } else if (rs.equals("REJECTED")) {
                                reg.reject();
                            } else if (rs.equals("WITHDRAWN")) {
                                reg.withdraw();
                            } else if (rs.equals("PENDING")) {
                                // PENDING is default for new registrations
                                // No specific action needed
                            } else {
                                System.err.println("Unknown registration status: " + rs + " for registration ID: " + regId);
                            }
                            ds.getRegistrations().add(reg);
                            offOpt.get().addRegistration(reg);
                        }
                    } catch (NumberFormatException ex) {
                        System.err.println("Invalid Project ID in registration: " + projIdStr);
                    }
                }
            }
        } catch (IOException e) {
            System.err.println("Could not read " + filename + ": " + e.getMessage());
        } catch (Exception e) { // Catch broader exceptions during parsing
             System.err.println("Error processing line in " + filename + ": " + line + " - " + e.getMessage());
             e.printStackTrace(); // Print stack trace for debugging
        }
    }

    /**
     * Read applications from CSV
     */
    private static void readApplications(DataStore ds, String filename) {
        Path p = BASE.resolve(filename);
        try (BufferedReader br = Files.newBufferedReader(p)) {
            br.readLine(); // skip header
            String line;
            while ((line = br.readLine()) != null && !line.isBlank()) {
                try {
                    String[] cols = line.replace("\uFEFF", "").split(",", -1);
                    int id = Integer.parseInt(cols[0].trim());
                    String applicantNric = cols[1].trim();
                    int projectId = Integer.parseInt(cols[2].trim());
                    String flatType = cols[3].trim();
                    String status = cols[4].trim();
                    boolean withdrawalRequested = Boolean.parseBoolean(cols[5].trim());
                    
                    // Find referenced entities
                    Optional<User> applicantOpt = ds.findUserByNric(applicantNric);
                    Optional<Project> projectOpt = ds.findProjectById(projectId);
                    
                    if (applicantOpt.isPresent() && applicantOpt.get() instanceof Applicant && 
                        projectOpt.isPresent()) {
                        
                        Applicant applicant = (Applicant) applicantOpt.get();
                        Project project = projectOpt.get();
                        
                        // Make sure ID generator is updated to avoid collisions
                        IdGenerator.ensureApplicationIdAtLeast(id);
                        
                        // Create application with minimal details using constructor
                        BTOApplication app = new BTOApplication(applicant, project, flatType);
                        
                        // Use the setter methods to restore the complete state
                        try {
                            ApplicationStatus appStatus = ApplicationStatus.valueOf(status);
                            
                            // Apply appropriate state changes based on status
                            switch (appStatus) {
                                case SUCCESS -> app.approve();
                                case REJECTED -> app.reject();
                                case WITHDRAWN -> app.confirmWithdrawal();
                                case BOOKED -> {
                                    app.approve(); // First approve
                                    app.requestBooking(); // Then book
                                }
                                case PENDING -> {
                                    // PENDING is default for new applications
                                    // No specific action needed
                                }
                                default -> {
                                    System.err.println("Unknown application status: " + appStatus + " for application ID: " + id);
                                }
                            }
                            
                            // Set withdrawal request flag if needed
                            if (withdrawalRequested && appStatus == ApplicationStatus.PENDING) {
                                app.requestWithdrawal();
                            }
                            
                            // Ensure applicant is linked to the application
                            applicant.setCurrentApplication(app);
                            
                            // Add to datastore
                            ds.getApplications().add(app);
                        } catch (IllegalArgumentException e) {
                            System.err.println("Invalid application status: " + status);
                        }
                    } else {
                        System.err.println("Could not find applicant or project for application ID " + id);
                    }
                } catch (Exception e) {
                    System.err.println("Error parsing application line: " + line + " - " + e.getMessage());
                }
            }
        } catch (IOException e) {
            System.err.println("Could not read " + filename + ": " + e.getMessage());
        }
    }

    /**
     * Read registrations from CSV
     */
    private static void readRegistrations(DataStore ds, String filename) {
        Path p = BASE.resolve(filename);
        try (BufferedReader br = Files.newBufferedReader(p)) {
            br.readLine(); // skip header
            String line;
            while ((line = br.readLine()) != null && !line.isBlank()) {
                try {
                    String[] cols = line.replace("\uFEFF", "").split(",", -1);
                    int id = Integer.parseInt(cols[0].trim());
                    String officerNric = cols[1].trim();
                    int projectId = Integer.parseInt(cols[2].trim());
                    String status = cols[3].trim();
                    boolean withdrawalRequested = Boolean.parseBoolean(cols[4].trim());
                    
                    // Find referenced entities
                    Optional<User> officerOpt = ds.findUserByNric(officerNric);
                    Optional<Project> projectOpt = ds.findProjectById(projectId);
                    
                    if (officerOpt.isPresent() && officerOpt.get() instanceof HDB_Officer && 
                        projectOpt.isPresent()) {
                        
                        HDB_Officer officer = (HDB_Officer) officerOpt.get();
                        Project project = projectOpt.get();
                        
                        // Make sure ID generator is updated to avoid collisions
                        IdGenerator.ensureRegistrationIdAtLeast(id);
                        
                        // Create registration with ID
                        Registration reg = new Registration(id, officer, project);
                        
                        // Apply status
                        try {
                            RegistrationStatus regStatus = RegistrationStatus.valueOf(status);
                            
                            // Apply appropriate state changes based on status
                            switch (regStatus) {
                                case APPROVED -> {
                                    reg.approve();
                                    project.addAssignedOfficer(officer);
                                    officer.addAssignedProject(project);
                                }
                                case REJECTED -> reg.reject();
                                case WITHDRAWN -> reg.withdraw();
                                case PENDING -> {
                                    // PENDING is the default status for new registrations
                                    // No specific action needed
                                }
                                default -> {
                                    System.err.println("Unknown registration status: " + regStatus + " for registration ID: " + id);
                                }
                            }
                            
                            // Set withdrawal request flag if needed
                            if (withdrawalRequested) {
                                reg.setWithdrawalRequested(true);
                            }
                            
                            // Add to officer's registrations and datastore
                            officer.addRegistration(reg);
                            ds.getRegistrations().add(reg);
                        } catch (IllegalArgumentException e) {
                            System.err.println("Invalid registration status: " + status);
                        }
                    } else {
                        System.err.println("Could not find officer or project for registration ID " + id);
                    }
                } catch (Exception e) {
                    System.err.println("Error parsing registration line: " + line + " - " + e.getMessage());
                }
            }
        } catch (IOException e) {
            System.err.println("Could not read " + filename + ": " + e.getMessage());
        }
    }

    /**
     * Read enquiries from CSV
     */
    private static void readEnquiries(DataStore ds, String filename) {
        Path p = BASE.resolve(filename);
        try (BufferedReader br = Files.newBufferedReader(p)) {
            br.readLine(); // skip header
            String line;
            while ((line = br.readLine()) != null && !line.isBlank()) {
                try {
                    String[] cols = line.replace("\uFEFF", "").split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)", -1);
                    int id = Integer.parseInt(cols[0].trim());
                    String creatorNric = cols[1].trim();
                    String projectId = cols[2].trim();
                    String flatType = cols[3].trim();
                    String content = cols[4].trim().replace("\"", ""); // Remove quotes
                    String response = cols[5].trim().replace("\"", ""); // Remove quotes
                    String responderNric = cols[6].trim();
                    
                    // Find referenced entities
                    Optional<User> creatorOpt = ds.findUserByNric(creatorNric);
                    Optional<Project> projectOpt = projectId.isEmpty() ? 
                        Optional.empty() : ds.findProjectById(Integer.parseInt(projectId));
                    Optional<User> responderOpt = responderNric.isEmpty() ? 
                        Optional.empty() : ds.findUserByNric(responderNric);
                    
                    if (creatorOpt.isPresent()) {
                        // Make sure ID generator is updated to avoid collisions
                        IdGenerator.ensureEnquiryIdAtLeast(id);
                        
                        // Create enquiry with minimal details
                        Enquiry enquiry = new Enquiry(id, creatorOpt.get(), content);
                        
                        // Set optional fields
                        if (projectOpt.isPresent()) {
                            enquiry.setProject(projectOpt.get());
                        }
                        
                        if (!flatType.isEmpty()) {
                            enquiry.setFlatType(flatType);
                        }
                        
                        // Set response if present
                        if (!response.isEmpty() && responderOpt.isPresent()) {
                            enquiry.setResponse(response);
                            enquiry.setRespondent(responderOpt.get());
                        }
                        
                        // Add to datastore
                        ds.getEnquiries().add(enquiry);
                    } else {
                        System.err.println("Could not find creator for enquiry ID " + id);
                    }
                } catch (Exception e) {
                    System.err.println("Error parsing enquiry line: " + line + " - " + e.getMessage());
                    e.printStackTrace();
                }
            }
        } catch (IOException e) {
            System.err.println("Could not read " + filename + ": " + e.getMessage());
        }
    }

    /**
     * Read flats from CSV
     */
    private static void readFlats(DataStore ds, String filename) {
        Path p = BASE.resolve(filename);
        try (BufferedReader br = Files.newBufferedReader(p)) {
            br.readLine(); // skip header
            String line;
            while ((line = br.readLine()) != null && !line.isBlank()) {
                try {
                    String[] cols = line.replace("\uFEFF", "").split(",", -1);
                    int id = Integer.parseInt(cols[0].trim());
                    int projectId = Integer.parseInt(cols[1].trim());
                    String flatType = cols[2].trim();
                    double price = Double.parseDouble(cols[3].trim());
                    boolean booked = Boolean.parseBoolean(cols[4].trim());
                    
                    // Find referenced project
                    Optional<Project> projectOpt = ds.findProjectById(projectId);
                    
                    if (projectOpt.isPresent()) {
                        // Make sure ID generator is updated to avoid collisions
                        IdGenerator.ensureFlatIdAtLeast(id);
                        
                        // Create flat
                        Flat flat = new Flat(id, projectOpt.get(), flatType, price);
                        
                        // Set booked status
                        if (booked) {
                            flat.setBooked(true);
                        }
                        
                        // Add to datastore
                        ds.getFlats().add(flat);
                    } else {
                        System.err.println("Could not find project for flat ID " + id);
                    }
                } catch (Exception e) {
                    System.err.println("Error parsing flat line: " + line + " - " + e.getMessage());
                }
            }
        } catch (IOException e) {
            System.err.println("Could not read " + filename + ": " + e.getMessage());
        }
    }
}
