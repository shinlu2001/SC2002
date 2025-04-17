// SC2002/Project/control/persistence/CSVWriter.java
package SC2002.Project.control.persistence;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.*;

import SC2002.Project.entity.*;

public final class CSVWriter {
    private CSVWriter() {}

    private static final Path BASE = Paths.get("SC2002/Project/files");

    public static void saveAll() {
        DataStore ds = DataStore.getInstance();
        writeUsers(ds, Applicant.class,   "ApplicantListNew.csv");
        writeUsers(ds, HDB_Officer.class,  "OfficerListNew.csv");
        writeUsers(ds, HDB_Manager.class,  "ManagerListNew.csv");
        // …add writeProjects, writeApplications, writeEnquiries, etc.
    }

    private static void writeUsers(DataStore ds,
                                   Class<? extends User> roleClass,
                                   String filename) {
        Path out = BASE.resolve(filename);
        try (BufferedWriter bw = Files.newBufferedWriter(out)) {
            bw.write("Name,NRIC,Age,Marital Status,Password");
            bw.newLine();

            ds.getUsers().stream()
              .filter(roleClass::isInstance)
              .forEach(u -> {
                  String fullName = u.getFirstName() + " " + u.getLastName();
                  String line = String.join(",",
                      fullName,
                      u.getNric(),
                      String.valueOf(u.getAge()),
                      u.getMaritalStatus().name(),
                      /* assuming default-password semantics */
                      "password"
                  );
                  try {
                      bw.write(line);
                      bw.newLine();
                  } catch (IOException e) {
                      throw new UncheckedIOException(e);
                  }
              });

        } catch (IOException e) {
            System.err.println("Could not write " + filename + ": " + e.getMessage());
        }
    }
}
