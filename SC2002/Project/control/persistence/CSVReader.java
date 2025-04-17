// SC2002/Project/control/persistence/CSVReader.java
package SC2002.Project.control.persistence;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

import SC2002.Project.entity.*;
import SC2002.Project.entity.enums.MaritalStatus;
import SC2002.Project.entity.enums.RegistrationStatus;

public final class CSVReader {
    private CSVReader() {}

    private static final Path BASE = Paths.get("SC2002/Project/files");
    private static final DateTimeFormatter DF = DateTimeFormatter.ofPattern("d/M/yyyy");

    public static void loadAll() {
        DataStore ds = DataStore.getInstance();

        readUsers("ApplicantList.csv", "APPLICANT", ds);
        readUsers("OfficerList.csv",   "OFFICER",   ds);
        readUsers("ManagerList.csv",   "MANAGER",   ds);

        readProjects(ds);
        // …you can add readFlats, readApplications, readEnquiries here…
    }

    private static void readUsers(String file, String role, DataStore ds) {
        Path p = BASE.resolve(file);
        try (BufferedReader br = Files.newBufferedReader(p)) {
            br.readLine(); // skip header
            String line;
            while ((line = br.readLine()) != null && !line.isBlank()) {
                line = line.replace("\uFEFF", "");
                String[] cols = line.split(",", -1);

                String fullName      = cols[0].trim();
                String nric          = cols[1].trim();
                int    age           = Integer.parseInt(cols[2].trim());
                MaritalStatus ms     = MaritalStatus.valueOf(cols[3].trim().toUpperCase());
                String[] parts       = fullName.split("\\s+", 2);
                String firstName     = parts[0];
                String lastName      = parts.length > 1 ? parts[1] : "";

                switch (role) {
                    case "APPLICANT" -> ds.getUsers()
                                          .add(new Applicant(nric, firstName, lastName, ms, age));
                    case "OFFICER"   -> ds.getUsers()
                                          .add(new HDB_Officer(nric, firstName, lastName, ms, age));
                    case "MANAGER"   -> ds.getUsers()
                                          .add(new HDB_Manager(nric, firstName, lastName, ms, age));
                }
            }
        } catch (IOException e) {
            System.err.println("Could not read " + file + ": " + e.getMessage());
        }
    }

    private static void readProjects(DataStore ds) {
        Path p = BASE.resolve("ProjectList.csv");
        try (BufferedReader br = Files.newBufferedReader(p)) {
            br.readLine(); // skip header
            String line;
            while ((line = br.readLine()) != null && !line.isBlank()) {
                line = line.replace("\uFEFF", "");
                String[] t = line.split(",", -1);

                String  name         = t[0].trim();
                String  neighbourhood= t[1].trim();
                String  type1        = t[2].trim().toUpperCase();
                int     units1       = Integer.parseInt(t[3].trim());
                double  price1       = Double.parseDouble(t[4].trim());
                String  type2        = t[5].trim().toUpperCase();
                int     units2       = Integer.parseInt(t[6].trim());
                double  price2       = Double.parseDouble(t[7].trim());
                LocalDate openDate   = LocalDate.parse(t[8].trim(), DF);
                LocalDate closeDate  = LocalDate.parse(t[9].trim(), DF);
                String  mgrFirstName = t[10].trim();
                int     slotCount    = Integer.parseInt(t[11].trim());

                Project prj = new Project(
                    name,
                    neighbourhood,
                    List.of(type1, type2),
                    List.of(units1, units2),
                    List.of(price1, price2),
                    openDate,
                    closeDate,
                    true,
                    slotCount
                );

                // link manager by first name
                ds.getUsers().stream()
                  .filter(u -> u instanceof HDB_Manager
                            && u.getFirstName().equalsIgnoreCase(mgrFirstName))
                  .map(u -> (HDB_Manager) u)
                  .findFirst()
                  .ifPresent(mgr -> {
                      prj.setManager(mgr);
                      mgr.addProject(prj);
                  });

                ds.getProjects().add(prj);

                // optional initial officers (column 12)
                if (t.length > 12 && !t[12].isBlank()) {
                    String raw = t[12].replace("\"", "").trim();
                    for (String offName : raw.split("\\s*[,;]\\s*")) {
                        ds.getUsers().stream()
                          .filter(u -> u instanceof HDB_Officer
                                    && u.getFirstName().equalsIgnoreCase(offName))
                          .map(u -> (HDB_Officer) u)
                          .findFirst()
                          .ifPresent(off -> {
                              Registration reg = new Registration(off, prj);
                              reg.setStatus(RegistrationStatus.SUCCESS);
                              ds.getRegistrations().add(reg);
                              off.setCurrentRegistration(reg);
                          });
                    }
                }
            }
        } catch (IOException e) {
            System.err.println("Could not read ProjectList.csv: " + e.getMessage());
        }
    }
}
