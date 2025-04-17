// SC2002/Project/control/persistence/CSVReader.java
package SC2002.Project.control.persistence;

import java.io.*;
import java.nio.file.*;
import java.time.LocalDate;
import java.util.*;
import SC2002.Project.entity.*;
import SC2002.Project.entity.enums.*;

public final class CSVReader {
    private CSVReader() {}

    public static void loadAll() {
        DataStore ds = DataStore.getInstance();
        Path base = Paths.get("SC2002/Project/files");
        try {
            // === users ===
            Files.lines(base.resolve("ApplicantList.csv")).skip(1).forEach(l -> {
                String[] t = l.split(",");
                ds.users.add(new Applicant(t[1], t[0], "", MaritalStatus.valueOf(t[3].toUpperCase()), Integer.parseInt(t[2])));
            });
            Files.lines(base.resolve("OfficerList.csv")).skip(1).forEach(l -> {
                String[] t = l.split(",");
                ds.users.add(new HDB_Officer(t[1], t[0], "", MaritalStatus.valueOf(t[3].toUpperCase()), Integer.parseInt(t[2])));
            });
            Files.lines(base.resolve("ManagerList.csv")).skip(1).forEach(l -> {
                String[] t = l.split(",");
                ds.users.add(new HDB_Manager(t[1], t[0], "", MaritalStatus.valueOf(t[3].toUpperCase()), Integer.parseInt(t[2])));
            });

            // === projects (trimmed) ===
            Files.lines(base.resolve("ProjectList.csv")).skip(1).forEach(l -> {
                String[] t = l.split(",");
                List<String> flatTypes = List.of("2-ROOM","3-ROOM");
                List<Integer> totals   = List.of(Integer.parseInt(t[3]), Integer.parseInt(t[4]));
                Project p = new Project(
                        t[0],                       // name
                        t[1],                       // neighbourhood
                        flatTypes,
                        totals,
                        LocalDate.parse(t[5]),
                        LocalDate.parse(t[6]),
                        Boolean.parseBoolean(t[7]),
                        Integer.parseInt(t[8]));
                ds.projects.add(p);
            });
        } catch (IOException e) {
            System.err.println("CSV load error: " + e.getMessage());
        }
    }
}
