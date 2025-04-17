// SC2002/Project/control/persistence/CSVReader.java
package SC2002.Project.control.persistence;

import java.io.*;
import java.nio.file.*;
import java.time.LocalDate;
import java.util.*;

import SC2002.Project.entity.*;
import SC2002.Project.entity.enums.MaritalStatus;

public final class CSVReader {
    private CSVReader() {}

    private static final Path BASE = Paths.get("SC2002/Project/files");

    public static void loadAll() {
        DataStore ds = DataStore.getInstance();
        readUsers("ApplicantList.csv", "APPLICANT", ds);
        readUsers("OfficerList.csv",  "OFFICER",   ds);
        readUsers("ManagerList.csv",  "MANAGER",   ds);
        readProjects(ds);
    }

    /* -------- helpers -------- */

    private static void readUsers(String file, String role, DataStore ds) {
        Path p = BASE.resolve(file);
        try (BufferedReader br = Files.newBufferedReader(p)) {
            br.readLine();                            // skip header
            String line;
            while ((line = br.readLine()) != null && !line.isBlank()) {
                // remove potential BOM on first line
                line = line.replace("\uFEFF", "");
                String[] t = line.split(",", -1);     // keep empty columns
                String fullName = t[0].trim();
                String nric     = t[1].trim();
                int age         = Integer.parseInt(t[2].trim());
                MaritalStatus ms= MaritalStatus.valueOf(t[3].trim().toUpperCase());
                String[] nameParts = fullName.split("\\s+",2);
                String first = nameParts[0];
                String last  = nameParts.length>1 ? nameParts[1] : "";

                switch (role) {
                    case "APPLICANT" -> ds.users.add(new Applicant(nric,first,last,ms,age));
                    case "OFFICER"   -> ds.users.add(new HDB_Officer(nric,first,last,ms,age));
                    case "MANAGER"   -> ds.users.add(new HDB_Manager(nric,first,last,ms,age));
                }
            }
        } catch (IOException e) {
            System.err.println("Could not read " + file + ": " + e.getMessage());
        }
    }

    private static void readProjects(DataStore ds) {
        Path p = BASE.resolve("ProjectList.csv");
        try (BufferedReader br = Files.newBufferedReader(p)) {
            br.readLine();                            // skip header
            String line;
            while ((line = br.readLine()) != null && !line.isBlank()) {
                line = line.replace("\uFEFF", "");
                String[] t = line.split(",", -1);
                String name         = t[0].trim();
                String hood         = t[1].trim();
                String type1        = t[2].trim().toUpperCase();
                int    units1       = Integer.parseInt(t[3].trim());
                String type2        = t[4].trim().toUpperCase();
                int    units2       = Integer.parseInt(t[5].trim());
                LocalDate open      = LocalDate.parse(t[6].trim());
                LocalDate close     = LocalDate.parse(t[7].trim());
                boolean visible     = true;
                int officerSlots    = Integer.parseInt(t[9].trim());

                Project prj = new Project(
                        name,
                        hood,
                        List.of(type1,type2),
                        List.of(units1,units2),
                        open, close,
                        visible,
                        officerSlots);
                ds.projects.add(prj);
            }
        } catch (IOException e) {
            System.err.println("Could not read ProjectList.csv: " + e.getMessage());
        }
    }
}
