// SC2002/Project/control/persistence/CSVWriter.java
package SC2002.Project.control.persistence;

import SC2002.Project.entity.*;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.*;
import java.util.List;

public final class CSVWriter {
    private CSVWriter() {}

    private static final Path BASE = Paths.get("SC2002/Project/files");

    public static void saveAll() {
        DataStore ds = DataStore.getInstance();
        writeUsers(ds, Applicant.class,   "ApplicantListNew.csv");
        writeUsers(ds, HDB_Officer.class,  "OfficerListNew.csv");
        writeUsers(ds, HDB_Manager.class,  "ManagerListNew.csv");
        writeProjects(ds, "ProjectListNew.csv");
        // …add writeApplications, writeEnquiries, etc.
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
                      u.getPassword() // Save actual password
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

    // Save all projects to ProjectListNew.csv in the same format as read
    private static void writeProjects(DataStore ds, String filename) {
        Path out = BASE.resolve(filename);
        try (BufferedWriter bw = Files.newBufferedWriter(out)) {
            // Write header
            bw.write("Project Name,Neighborhood,Type 1,Number of units for Type 1,Selling price for Type 1,Type 2,Number of units for Type 2,Selling price for Type 2,Application opening date,Application closing date,Manager,Officer Slot,Officer");
            bw.newLine();
            for (Project p : ds.getProjects()) {
                // Only support up to 2 flat types for CSV compatibility
                List<String> types = p.getFlatTypes();
                List<Integer> units = p.getTotalUnits();
                List<Double> prices = p.getPrices();
                String type1 = types.size() > 0 ? types.get(0) : "";
                String type2 = types.size() > 1 ? types.get(1) : "";
                String units1 = units.size() > 0 ? String.valueOf(units.get(0)) : "";
                String units2 = units.size() > 1 ? String.valueOf(units.get(1)) : "";
                String price1 = prices.size() > 0 ? String.valueOf(prices.get(0)) : "";
                String price2 = prices.size() > 1 ? String.valueOf(prices.get(1)) : "";
                String openDate = p.getOpenDate() != null ? p.getOpenDate().format(java.time.format.DateTimeFormatter.ofPattern("d/M/yyyy")) : "";
                String closeDate = p.getCloseDate() != null ? p.getCloseDate().format(java.time.format.DateTimeFormatter.ofPattern("d/M/yyyy")) : "";
                String managerName = p.getManager() != null ? p.getManager().getFirstName() : "";
                String officerSlot = String.valueOf(p.getOfficerSlotLimit());
                // Officer names (first names, comma separated)
                String officers = p.getAssignedOfficers().stream().map(o -> o.getFirstName()).reduce((a, b) -> a + "," + b).orElse("");
                String line = String.join(",",
                    p.getName(),
                    p.getNeighbourhood(),
                    type1,
                    units1,
                    price1,
                    type2,
                    units2,
                    price2,
                    openDate,
                    closeDate,
                    managerName,
                    officerSlot,
                    '"' + officers + '"'
                );
                bw.write(line);
                bw.newLine();
            }
        } catch (IOException e) {
            System.err.println("Could not write " + filename + ": " + e.getMessage());
        }
    }
}
