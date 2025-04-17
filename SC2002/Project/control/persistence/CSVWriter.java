// SC2002/Project/control/persistence/CSVWriter.java
package SC2002.Project.control.persistence;

import java.io.*;
import java.nio.file.*;
import SC2002.Project.entity.*;

public final class CSVWriter {
    private CSVWriter() {}

    public static void saveAll() {
        DataStore ds = DataStore.getInstance();
        Path base = Paths.get("SC2002/Project/files");
        try (BufferedWriter bw = Files.newBufferedWriter(base.resolve("ApplicantList.csv"))) {
            bw.write("firstname,nric,age,marital\n");
            for (User u : ds.users) if (u instanceof Applicant a && !(u instanceof HDB_Officer))
                bw.write(String.join(",", a.get_firstname(), a.get_nric(),
                        String.valueOf(a.get_age()), a.get_maritalstatus()) + "\n");
        } catch (IOException e) { System.err.println("CSV save error: " + e.getMessage()); }
        // Repeat for other lists as needed …
    }
}
