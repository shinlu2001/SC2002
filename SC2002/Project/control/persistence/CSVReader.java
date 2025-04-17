// SC2002/Project/control/persistence/CSVReader.java
package SC2002.Project.control.persistence;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

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
        readUsers("OfficerList.csv",  "OFFICER",   ds);
        readUsers("ManagerList.csv",  "MANAGER",   ds);
        readProjects(ds);
    }

    /* ---------- USERS ---------- */

    private static void readUsers(String file, String role, DataStore ds) {
        Path p = BASE.resolve(file);
        try (BufferedReader br = Files.newBufferedReader(p)) {
            br.readLine();                                 // header
            String line;
            while ((line = br.readLine()) != null && !line.isBlank()) {
                line = line.replace("\uFEFF", "");
                String[] t = line.split(",", -1);

                String fullName   = t[0].trim();
                String nric       = t[1].trim();
                int    age        = Integer.parseInt(t[2].trim());
                MaritalStatus ms  = MaritalStatus.valueOf(t[3].trim().toUpperCase());

                String[] parts = fullName.split("\\s+", 2);
                String first = parts[0];
                String last  = parts.length > 1 ? parts[1] : "";

                switch (role) {
                    case "APPLICANT" -> ds.users.add(new Applicant(nric, first, last, ms, age));
                    case "OFFICER"   -> ds.users.add(new HDB_Officer(nric, first, last, ms, age));
                    case "MANAGER"   -> ds.users.add(new HDB_Manager(nric, first, last, ms, age));
                }
            }
        } catch (IOException e) {
            System.err.println("Could not read " + file + ": " + e.getMessage());
        }
    }

    /* ---------- PROJECTS ---------- */

    private static void readProjects(DataStore ds) {
        Path p = BASE.resolve("ProjectList.csv");
        try (BufferedReader br = Files.newBufferedReader(p)) {
            br.readLine();                                 // header
            String line;
            while ((line = br.readLine()) != null && !line.isBlank()) {
                line = line.replace("\uFEFF", "");
                String[] t = line.split(",", -1);

                // columns
                String  name        = t[0].trim();
                String  hood        = t[1].trim();
                String  type1       = t[2].trim().toUpperCase();
                int     units1      = Integer.parseInt(t[3].trim());
                double  price1      = Double.parseDouble(t[4].trim());

                String  type2       = t[5].trim().toUpperCase();
                int     units2      = Integer.parseInt(t[6].trim());
                double  price2      = Double.parseDouble(t[7].trim());

                LocalDate open      = LocalDate.parse(t[8].trim(), DF);
                LocalDate close     = LocalDate.parse(t[9].trim(), DF);

                String  managerName = t[10].trim();                    // first name of manager
                int     officerSlots= Integer.parseInt(t[11].trim());

                // Build project
                Project prj = new Project(
                        name,
                        hood,
                        List.of(type1, type2),
                        List.of(units1, units2),
                        List.of(price1, price2),
                        open,
                        close,
                        true,
                        officerSlots);

                // — link manager —
                ds.users.stream()
                        .filter(u -> u instanceof HDB_Manager &&
                                     u.getFirstname().equalsIgnoreCase(managerName))
                        .map(u -> (HDB_Manager) u)
                        .findFirst()
                        .ifPresent(mgr -> {
                            prj.setManager(mgr);
                            mgr.addProject(prj);
                        });

                ds.projects.add(prj);

                // — link initial officers (optional column 12) —
                if (t.length > 12 && !t[12].isBlank()) {
                    String officersRaw = t[12].replace("\"","").trim();
                    for (String offName : officersRaw.split("\\s*[,;]\\s*")) {
                        ds.users.stream()
                                .filter(u -> u instanceof HDB_Officer &&
                                             u.getFirstname().equalsIgnoreCase(offName))
                                .map(u -> (HDB_Officer) u)
                                .findFirst()
                                .ifPresent(off -> {
                                    Registration reg = new Registration(off, prj);
                                    reg.setStatus(RegistrationStatus.SUCCESS);
                                    ds.registrations.add(reg);
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
