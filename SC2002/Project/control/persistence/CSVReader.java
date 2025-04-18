package SC2002.Project.control.persistence;

import SC2002.Project.entity.*;
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

    public static void loadAll() {
        DataStore ds = DataStore.getInstance();
        readUsers("ApplicantList.csv", "APPLICANT", ds);
        readUsers("OfficerList.csv",   "OFFICER",   ds);
        readUsers("ManagerList.csv",   "MANAGER",   ds);
        readProjects(ds);
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
                int age         = Integer.parseInt(cols[2].trim());
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
        try (BufferedReader br = Files.newBufferedReader(p)) {
            br.readLine(); // header
            String line;
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
                String mgrNric       = t[10].trim();
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

                // link manager
                Optional<HDB_Manager> mgrOpt = ds.findUserByNric(mgrNric)
                        .filter(u -> !(u instanceof HDB_Officer))
                        .map(u -> (HDB_Manager)u);
                mgrOpt.ifPresent(m -> {
                    prj.setManager(m);
                    m.addManagedProject(prj);
                });

                ds.getProjects().add(prj);

                // initial approved officers (col 12)
                if (t.length > 12 && !t[12].isBlank()) {
                    String raw = t[12].replace("\"", "").trim();
                    for (String offName : raw.split("\\s*[,;]\\s*")) {
                        ds.findUserByNric(offName)
                          .filter(u -> u instanceof HDB_Officer)
                          .map(u -> (HDB_Officer)u)
                          .ifPresent(off -> {
                              int regId = IdGenerator.nextRegistrationId();
                              Registration reg = new Registration(regId, off, prj);
                              reg.setStatus(RegistrationStatus.APPROVED);
                              ds.getRegistrations().add(reg);
                              off.addRegistration(reg);
                              prj.addOfficer(off);
                          });
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
                                pOpt.get().addOfficer(offOpt.get());
                            } else if (rs.equals("REJECTED")) {
                                reg.reject();
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
        }
    }
}
