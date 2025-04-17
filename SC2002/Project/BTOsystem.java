// SC2002/Project/BTOsystem.java
package SC2002.Project;

import SC2002.Project.boundary.LoginUI;
import SC2002.Project.control.persistence.CSVReader;
import SC2002.Project.control.persistence.CSVWriter;

/** Boots the CLI; loads CSVs before any UI and auto‑saves on exit. */
public final class BTOsystem {
    private BTOsystem() {}

    public static void main(String[] args) {
        Runtime.getRuntime().addShutdownHook(new Thread(() -> CSVWriter.saveAll()));
        CSVReader.loadAll();              // fill DataStore from /files/*.csv
        new LoginUI().start();            // blocking loop
    }
}
