// SC2002/Project/control/persistence/CSVWriter.java
package SC2002.Project.control.persistence;

import java.io.*;
import java.nio.file.*;
import java.util.stream.*;

import SC2002.Project.entity.*;

public final class CSVWriter {
    private CSVWriter() {}

    private static final Path BASE = Paths.get("SC2002/Project/files");

    public static void saveAll() {
        DataStore ds = DataStore.getInstance();

        writeUsers(ds, Applicant.class, "ApplicantListNew.csv");
        writeUsers(ds, HDB_Officer.class, "OfficerListNew.csv");
        writeUsers(ds, HDB_Manager.class, "ManagerListNew.csv");
        // Add similar methods for projects / applications / enquiries if needed.
    }

    /* -------- helpers -------- */

    private static void writeUsers(DataStore ds, Class<? extends User> clazz, String outFile) {
        Path out = BASE.resolve(outFile);
        try (BufferedWriter bw = Files.newBufferedWriter(out)) {
            bw.write("Name,NRIC,Age,Marital Status,Password\n");
            for (User u : ds.users.stream().filter(clazz::isInstance).collect(Collectors.toList())) {
                String fullName = u.getFirstname() + " " + u.getFirstname();
                bw.write(String.join(",",
                        fullName,
                        u.getNric(),
                        String.valueOf(u.getAge()),
                        u.getMaritalStatus(),
                        "password") + "\n");
            }
        } catch (IOException e) {
            System.err.println("Could not write " + outFile + ": " + e.getMessage());
        }
    }
}
