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
import java.util.Map;
import java.util.HashMap;
import java.util.Optional;
import java.util.Set;

/**
 * Loads data from CSV files into in-memory DataStore
 */
public final class CSVReader {
    private CSVReader() {
    }

    private static final Path BASE = Paths.get("SC2002/Project/files");
    private static final DateTimeFormatter DF = DateTimeFormatter.ofPattern("d/M/yyyy");
    /** Track project names to avoid duplicates */
    private static final Set<String> PROJECT_NAMES = new HashSet<>();
    /** Track loaded users by NRIC for quick reference */
    private static final Map<String, User> USER_MAP = new HashMap<>();
    /** Track loaded projects by ID for quick reference */
    private static final Map<Integer, Project> PROJECT_MAP = new HashMap<>();

    // Flag to determine whether to load data from snapshot files or original files
    private static boolean useSnapshot = false;

    /**
     * Set whether to load from snapshot files or original files
     * 
     * @param useSnapshotFiles true to use snapshot files, false to use original
     *                         files
     */
    public static void setUseSnapshot(boolean useSnapshotFiles) {
        useSnapshot = useSnapshotFiles;
    }

    /**
     * Get current snapshot usage mode
     * 
     * @return true if using snapshot files, false if using original files
     */
    public static boolean isUsingSnapshot() {
        return useSnapshot;
    }

    /**
     * Load all data based on the current snapshot setting in the proper order:
     * 1. Users (Applicants, Officers, Managers)
     * 2. Projects
     * 3. Flats (related to Projects)
     * 4. Registrations (relating Officers to Projects)
     * 5. Applications (relating Applicants to Projects)
     * 6. Enquiries (relating Users to optional Projects)
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
        USER_MAP.clear();
        PROJECT_MAP.clear();

        if (useSnapshot) {
            loadAllFromSnapshot();
        } else {
            loadAllFromOriginal();
        }

        // Update the ID generators to prevent collisions with future IDs
        updateIdGenerators(ds);

        System.out.println("Loaded users: " + ds.getUsers().size() +
                ", projects: " + ds.getProjects().size() +
                ", registrations: " + ds.getRegistrations().size() +
                ", applications: " + ds.getApplications().size() +
                ", flats: " + ds.getFlats().size() +
                ", enquiries: " + ds.getEnquiries().size());
    }

    /**
     * Updates all ID generators to be higher than any existing ID in the system
     */
    private static void updateIdGenerators(DataStore ds) {
        // Ensure Project IDs don't collide
        ds.getProjects().forEach(p -> IdGenerator.ensureProjectIdAtLeast(p.getId()));

        // Ensure Registration IDs don't collide
        ds.getRegistrations().forEach(r -> IdGenerator.ensureRegistrationIdAtLeast(r.getId()));

        // Ensure Application IDs don't collide
        ds.getApplications().forEach(a -> IdGenerator.ensureApplicationIdAtLeast(a.getId()));

        // Ensure Flat IDs don't collide
        ds.getFlats().forEach(f -> IdGenerator.ensureFlatIdAtLeast(f.getId()));

        // Ensure Enquiry IDs don't collide
        ds.getEnquiries().forEach(e -> IdGenerator.ensureEnquiryIdAtLeast(e.getId()));
    }

    /**
     * Load data from original CSV files (minimal data)
     */
    private static void loadAllFromOriginal() {
        DataStore ds = DataStore.getInstance();

        // Step 1: Load all user types first
        readUsers("ApplicantList.csv", "APPLICANT", ds);
        readUsers("OfficerList.csv", "OFFICER", ds);
        readUsers("ManagerList.csv", "MANAGER", ds);

        // Build the user map for quick reference
        ds.getUsers().forEach(u -> USER_MAP.put(u.getNric().toLowerCase(), u));

        // Step 2: Load projects
        readProjects(ds);

        // Build the project map for quick reference
        ds.getProjects().forEach(p -> PROJECT_MAP.put(p.getId(), p));

        System.out.println("Loaded data from original files.");
    }

    /**
     * Load data from snapshot CSV files (complete system state)
     */
    private static void loadAllFromSnapshot() {
        DataStore ds = DataStore.getInstance();

        // Step 1: Load all user types first
        readUsers("OfficerListNew.csv", "OFFICER", ds);
        readUsers("ApplicantListNew.csv", "APPLICANT", ds);
        readUsers("ManagerListNew.csv", "MANAGER", ds);

        // Build the user map for quick reference
        ds.getUsers().forEach(u -> USER_MAP.put(u.getNric().toLowerCase(), u));

        // Step 2: Load projects (after users so managers can be assigned)
        readProjects(ds, "ProjectListNew.csv");

        // Build the project map for quick reference
        ds.getProjects().forEach(p -> PROJECT_MAP.put(p.getId(), p));

        // Step 3: Load flats (dependent on projects)
        readFlats(ds, "FlatsNew.csv");

        // Step 4: Load registrations (relate officers to projects)
        readRegistrations(ds, "RegistrationsNew.csv");

        // Step 5: Load applications (relate applicants to projects)
        readApplications(ds, "ApplicationsNew.csv");

        // Step 6: Load enquiries (can reference users and projects)
        readEnquiries(ds, "EnquiriesNew.csv");

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
                String nric = cols[1].trim();
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
                MaritalStatus ms;
                try {
                    ms = MaritalStatus.valueOf(cols[3].trim().toUpperCase());
                } catch (IllegalArgumentException e) {
                    System.err.println("Invalid marital status for NRIC " + nric + ", skipping user.");
                    continue;
                }
                String[] parts = fullName.split("\\s+", 2);
                String firstName = parts[0];
                String lastName = parts.length > 1 ? parts[1] : "";

                // Check for duplicate NRIC (case-insensitive)
                if (ds.findUserByNric(nric).isPresent()) {
                    System.err.println("Duplicate NRIC " + nric + ", skipping");
                    continue;
                }

                // Get password or set to default if not provided
                String password = cols.length > 4 && !cols[4].trim().isEmpty() ? cols[4].trim() : "password";

                User newUser = null;
                switch (role) {
                    case "APPLICANT" -> newUser = new Applicant(nric, firstName, lastName, ms, age);
                    case "OFFICER" -> newUser = new HDB_Officer(nric, firstName, lastName, ms, age);
                    case "MANAGER" -> newUser = new HDB_Manager(nric, firstName, lastName, ms, age);
                }

                if (newUser != null) {
                    // Set the password explicitly if it's not the default
                    if (!password.equals("password")) {
                        newUser.setPassword(password);
                    }
                    ds.getUsers().add(newUser);
                }
            }
        } catch (IOException e) {
            System.err.println("Could not read " + file + ": " + e.getMessage());
        }
    }

    private static void readProjects(DataStore ds) {
        readProjects(ds, "ProjectList.csv");
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
                try {
                    String[] t = line.replace("\uFEFF", "").split(",", -1);

                    String name = t[0].trim();
                    // skip duplicates by project name
                    if (!PROJECT_NAMES.add(name)) {
                        System.err.println("Duplicate project name '" + name + "', skipping");
                        continue;
                    }

                    String neighbourhood = t[1].trim();
                    String type1 = t[2].trim().toUpperCase();
                    int units1 = Integer.parseInt(t[3].trim());
                    double price1 = Double.parseDouble(t[4].trim());
                    String type2 = t[5].trim().toUpperCase();
                    int units2 = Integer.parseInt(t[6].trim());
                    double price2 = Double.parseDouble(t[7].trim());
                    LocalDate openDate = LocalDate.parse(t[8].trim(), DF);
                    LocalDate closeDate = LocalDate.parse(t[9].trim(), DF);
                    String mgrName = t[10].trim();
                    int slotCount = Integer.parseInt(t[11].trim());
                    
                    // Parse visibility (new field) if present, default to ON if not
                    Visibility visibility = Visibility.ON;
                    if (t.length > 13 && !t[13].trim().isEmpty()) {
                        try {
                            visibility = Visibility.valueOf(t[13].trim());
                        } catch (IllegalArgumentException e) {
                            System.err.println("Invalid visibility value for project '" + name + "', using default ON");
                        }
                    }

                    // create Project with generated ID or use existing ID if provided
                    int projId = IdGenerator.nextProjectId();
                    if (t.length > 14 && !t[14].trim().isEmpty()) {
                        try {
                            projId = Integer.parseInt(t[14].trim());
                            IdGenerator.ensureProjectIdAtLeast(projId);
                        } catch (NumberFormatException e) {
                            // Use the generated ID if parsing fails
                        }
                    }

                    // Get available units if provided
                    int availUnits1 = units1; // Default to total units
                    int availUnits2 = units2;
                    
                    // Read available units from file (new field)
                    if (t.length > 15 && !t[15].trim().isEmpty()) {
                        try {
                            availUnits1 = Integer.parseInt(t[15].trim());
                        } catch (NumberFormatException e) {
                            System.err.println("Invalid available units value for type 1 in project '" + name + "', using default (total units)");
                        }
                    }
                    
                    if (t.length > 16 && !t[16].trim().isEmpty()) {
                        try {
                            availUnits2 = Integer.parseInt(t[16].trim());
                        } catch (NumberFormatException e) {
                            System.err.println("Invalid available units value for type 2 in project '" + name + "', using default (total units)");
                        }
                    }

                    Project prj = new Project(
                            projId,
                            name,
                            neighbourhood,
                            List.of(type1, type2),
                            List.of(units1, units2),
                            List.of(price1, price2),
                            openDate,
                            closeDate,
                            visibility,
                            slotCount);
                    
                    // Set available units explicitly rather than initializing to total
                    prj.setAvailableUnits(List.of(availUnits1, availUnits2));

                    // link manager using name lookup - try to be more flexible with name matching
                    Optional<HDB_Manager> mgrOpt = ds.getUsers().stream()
                            .filter(u -> u instanceof HDB_Manager)
                            .map(u -> (HDB_Manager) u)
                            .filter(m -> m.getFirstName().equalsIgnoreCase(mgrName) ||
                                    (m.getFirstName() + " " + m.getLastName()).equalsIgnoreCase(mgrName))
                            .findFirst();

                    if (mgrOpt.isEmpty()) {
                        System.err.println("Error: Manager with name '" + mgrName + "' not found for project '" + name +
                                "'. The project will be created without a manager assignment.");
                    } else {
                        HDB_Manager manager = mgrOpt.get();
                        try {
                            prj.setManager(manager);
                            manager.addManagedProject(prj);
                        } catch (Exception e) {
                            System.err.println("Error assigning Manager " + manager.getFirstName() +
                                    " to Project " + prj.getName() + ": " + e.getMessage());
                        }
                    }

                    ds.getProjects().add(prj);
                    PROJECT_MAP.put(prj.getId(), prj);

                    // Create Flat objects for each flat type based on total units
                    // We'll create all flats, but mark some as booked based on availability counts
                    List<String> flatTypes = prj.getFlatTypes();
                    List<Integer> totalUnits = prj.getTotalUnits();
                    List<Integer> availableUnits = prj.getAvailableUnits();
                    List<Double> prices = prj.getPrices();

                    // Create Flat objects for each unit in each flat type
                    for (int i = 0; i < flatTypes.size(); i++) {
                        String type = flatTypes.get(i);
                        int numUnits = totalUnits.get(i);
                        int availUnits = availableUnits.get(i);
                        double price = prices.get(i);
                        int bookedUnits = numUnits - availUnits;

                        // Create Flat objects for this flat type
                        for (int j = 0; j < numUnits; j++) {
                            Flat flat = new Flat(IdGenerator.nextFlatId(), prj, type, price);
                            
                            // Mark flats as booked if they exceed available count
                            // e.g., if 10 total units but only 8 available, the first 2 should be marked booked
                            if (j < bookedUnits) {
                                flat.setBooked(true);
                            }
                            
                            ds.getFlats().add(flat);
                        }
                    }

                    // Process officer assignments from column 12 (Only for original loading, not
                    // snapshots)
                    if (t.length > 12 && !t[12].isBlank() && !useSnapshot) {
                        processOfficerAssignments(ds, prj, t[12]);
                    }

                    // Process explicit registration (columns 13-15) (Only for original loading, not
                    // snapshots)
                    if (t.length > 15 && !useSnapshot) {
                        processExplicitRegistration(ds, t[13], t[14], t[15]);
                    }
                } catch (Exception e) {
                    System.err.println("Error processing line in " + filename + ": " + line);
                    e.printStackTrace();
                }
            }
        } catch (IOException e) {
            System.err.println("Could not read " + filename + ": " + e.getMessage());
        }
    }

    /**
     * Process officer assignments from a comma-separated or semicolon-separated
     * list of names
     */
    private static void processOfficerAssignments(DataStore ds, Project project, String officerNames) {
        String rawOfficerNames = officerNames.replace("\"", "").trim();

        for (String offName : rawOfficerNames.split("\\s*[,;]\\s*")) {
            if (offName.isBlank())
                continue;

            // Find officer by name (try first name first, then full name)
            Optional<HDB_Officer> foundOfficer = ds.getUsers().stream()
                    .filter(u -> u instanceof HDB_Officer)
                    .map(u -> (HDB_Officer) u)
                    .filter(officer -> officer.getFirstName().equalsIgnoreCase(offName.trim()) ||
                            (officer.getFirstName() + " " + officer.getLastName()).equalsIgnoreCase(offName.trim()))
                    .findFirst();

            if (foundOfficer.isPresent()) {
                HDB_Officer off = foundOfficer.get();
                // Check if already assigned
                boolean alreadyAssigned = project.getAssignedOfficers().contains(off);
                if (!alreadyAssigned) {
                    try {
                        // Check for slot availability
                        if (project.getAssignedOfficers().size() >= project.getOfficerSlotLimit()) {
                            System.err.println("Error: Cannot assign Officer " + off.getFirstName() +
                                    " to Project " + project.getName() + ". Officer slot limit (" +
                                    project.getOfficerSlotLimit() + ") has been reached.");
                            continue;
                        }

                        // Create registration
                        int regId = IdGenerator.nextRegistrationId();
                        Registration reg = new Registration(regId, off, project);
                        reg.setStatus(RegistrationStatus.APPROVED);
                        ds.getRegistrations().add(reg);
                        off.addRegistration(reg);
                        project.addAssignedOfficer(off);
                        off.addAssignedProject(project);
                    } catch (Exception e) {
                        System.err.println("Error assigning Officer " + off.getFirstName() +
                                " to Project " + project.getName() + ": " + e.getMessage());
                    }
                }
            } else {
                System.err.println("Error: Officer with name '" + offName + "' not found for project '" +
                        project.getName()
                        + "'. Please check that the officer exists and the name is spelled correctly.");
            }
        }
    }

    /**
     * Process explicit registration data
     */
    private static void processExplicitRegistration(DataStore ds, String offNric, String projIdStr, String statusStr) {
        if (offNric.isEmpty() || projIdStr.isEmpty() || statusStr.isEmpty()) {
            return;
        }

        try {
            int pid = Integer.parseInt(projIdStr);
            Optional<User> userOpt = ds.findUserByNric(offNric);
            Optional<Project> pOpt = ds.findProjectById(pid);

            if (userOpt.isPresent() && userOpt.get() instanceof HDB_Officer && pOpt.isPresent()) {
                HDB_Officer officer = (HDB_Officer) userOpt.get();
                Project project = pOpt.get();

                int regId = IdGenerator.nextRegistrationId();
                Registration reg = new Registration(regId, officer, project);

                String rs = statusStr.toUpperCase();
                if (rs.equals("APPROVED")) {
                    reg.approve();
                    project.addAssignedOfficer(officer);
                    officer.addAssignedProject(project);
                } else if (rs.equals("REJECTED")) {
                    reg.reject();
                } else if (rs.equals("WITHDRAWN")) {
                    reg.withdraw();
                } else if (!rs.equals("PENDING")) {
                    System.err.println("Unknown registration status: " + rs + " for registration ID: " + regId);
                }

                ds.getRegistrations().add(reg);
                officer.addRegistration(reg);
            } else {
                System.err.println("Could not process registration: officer or project not found. NRIC: " +
                        offNric + ", Project ID: " + pid);
            }
        } catch (NumberFormatException ex) {
            System.err.println("Invalid Project ID in registration: " + projIdStr);
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

                    // Find referenced entities using our maps for faster lookup
                    User user = USER_MAP.get(applicantNric.toLowerCase());
                    Project project = PROJECT_MAP.get(projectId);

                    if (user instanceof Applicant && project != null) {
                        Applicant applicant = (Applicant) user;

                        // Make sure ID generator is updated
                        IdGenerator.ensureApplicationIdAtLeast(id);

                        // Create application with specified ID and basic details
                        BTOApplication app = new BTOApplication(id, applicant, project, flatType);
                        
                        // The manager association is managed through the project
                        // No need to set manager directly on application since it uses project.getManager()

                        // Apply appropriate state changes based on status
                        try {
                            ApplicationStatus appStatus = ApplicationStatus.valueOf(status);

                            switch (appStatus) {
                                case SUCCESS -> {
                                    app.approve();
                                    // No need to manually set manager as it's obtained through project
                                }
                                case REJECTED -> app.reject();
                                case WITHDRAWN -> app.confirmWithdrawal();
                                case BOOKED -> {
                                    app.approve(); // First approve
                                    app.requestBooking(); // Then book

                                    // Find an available flat and book it
                                    Optional<Flat> flat = ds.findAvailableFlat(project, flatType);
                                    if (flat.isPresent()) {
                                        app.bookFlat(flat.get());
                                    } else {
                                        System.err.println(
                                                "Warning: No available flat found for booked application ID: " + id);
                                    }
                                }
                                case PENDING -> {
                                    /* Default state, no action needed */ }
                                default -> System.err.println(
                                        "Unknown application status: " + appStatus + " for application ID: " + id);
                            }

                            // Set withdrawal request flag if needed
                            if (withdrawalRequested) {
                                app.requestWithdrawal();
                            }

                            // Ensure applicant is linked to the application
                            if (appStatus != ApplicationStatus.WITHDRAWN &&
                                    appStatus != ApplicationStatus.REJECTED) {
                                applicant.setCurrentApplication(app);
                            }

                            // Add to datastore
                            ds.getApplications().add(app);
                        } catch (IllegalArgumentException e) {
                            System.err.println("Invalid application status: " + status);
                        }
                    } else {
                        System.err.println("Could not find applicant or project for application ID " + id);
                    }
                } catch (Exception e) {
                    System.err.println("Error parsing application line: " + line);
                    e.printStackTrace();
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

                    // Find referenced entities using our maps for faster lookup
                    User user = USER_MAP.get(officerNric.toLowerCase());
                    Project project = PROJECT_MAP.get(projectId);

                    if (user instanceof HDB_Officer && project != null) {
                        HDB_Officer officer = (HDB_Officer) user;

                        // Make sure ID generator is updated
                        IdGenerator.ensureRegistrationIdAtLeast(id);

                        // Create registration with ID
                        Registration reg = new Registration(id, officer, project);

                        // Apply status
                        try {
                            RegistrationStatus regStatus = RegistrationStatus.valueOf(status);

                            switch (regStatus) {
                                case APPROVED -> {
                                    reg.approve();
                                    project.addAssignedOfficer(officer);
                                    officer.addAssignedProject(project);
                                }
                                case REJECTED -> reg.reject();
                                case WITHDRAWN -> reg.withdraw();
                                case PENDING -> {
                                    /* Default state, no action needed */ }
                                default -> System.err.println("Unknown registration status: " + regStatus +
                                        " for registration ID: " + id);
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
                    System.err.println("Error parsing registration line: " + line);
                    e.printStackTrace();
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
            int lineNum = 1;
            while ((line = br.readLine()) != null && !line.isBlank()) {
                lineNum++;
                try {
                    // Handle quoted fields correctly
                    String[] cols = line.replace("\uFEFF", "").split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)", -1);
                    int id = Integer.parseInt(cols[0].trim());
                    String creatorNric = cols[1].trim();
                    String projectIdStr = cols[2].trim();
                    String flatType = cols[3].trim();
                    String content = cols[4].trim().replace("\"", ""); // Remove quotes
                    String response = cols[5].trim().replace("\"", ""); // Remove quotes
                    String responderNric = cols[6].trim();

                    // Find referenced entities using our maps for faster lookup
                    User creator = USER_MAP.get(creatorNric.toLowerCase());
                    Project project = null;
                    User responder = null;

                    if (!projectIdStr.isEmpty()) {
                        try {
                            int projectId = Integer.parseInt(projectIdStr);
                            project = PROJECT_MAP.get(projectId);
                        } catch (NumberFormatException e) {
                            System.err
                                    .println("Invalid project ID in enquiry at line " + lineNum + ": " + projectIdStr);
                        }
                    }

                    if (!responderNric.isEmpty()) {
                        responder = USER_MAP.get(responderNric.toLowerCase());
                    }

                    if (creator != null) {
                        // Make sure ID generator is updated
                        IdGenerator.ensureEnquiryIdAtLeast(id);

                        // Create enquiry with minimal details
                        Enquiry enquiry = new Enquiry(id, creator, content);

                        // Set optional fields
                        if (project != null) {
                            enquiry.setProject(project);
                            project.getEnquiries().add(enquiry);
                        }

                        if (!flatType.isEmpty()) {
                            enquiry.setFlatType(flatType);
                        }

                        // Set response if present
                        if (!response.isEmpty() && responder != null) {
                            enquiry.setResponse(response);
                            enquiry.setRespondent(responder);
                        }

                        // Add to applicant's enquiries if creator is an applicant
                        if (creator instanceof Applicant) {
                            ((Applicant) creator).getEnquiries().add(enquiry);
                        }

                        // Add to datastore
                        ds.getEnquiries().add(enquiry);
                    } else {
                        System.err.println("Could not find creator for enquiry ID " + id);
                    }
                } catch (Exception e) {
                    System.err.println("Error parsing enquiry line " + lineNum + ": " + line);
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
                    
                    // Read booking ID if available
                    String bookingId = cols.length > 5 ? cols[5].trim() : "";

                    // Find referenced project
                    Project project = PROJECT_MAP.get(projectId);

                    if (project != null) {
                        // Make sure ID generator is updated
                        IdGenerator.ensureFlatIdAtLeast(id);

                        // Create flat
                        Flat flat = new Flat(id, project, flatType, price);

                        // Set booked status and booking ID
                        if (booked) {
                            flat.setBooked(true);
                            flat.setBookingId(bookingId);
                        }

                        // Add to datastore
                        ds.getFlats().add(flat);
                    } else {
                        System.err.println("Could not find project for flat ID " + id);
                    }
                } catch (Exception e) {
                    System.err.println("Error parsing flat line: " + line);
                    e.printStackTrace();
                }
            }
        } catch (IOException e) {
            System.err.println("Could not read " + filename + ": " + e.getMessage());
        }
    }
}
