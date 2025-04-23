// SC2002/Project/control/persistence/CSVWriter.java
package SC2002.Project.control.persistence;

import SC2002.Project.entity.*;
import SC2002.Project.entity.enums.RegistrationStatus;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.*;
import java.util.List;
import java.util.stream.Collectors;

public final class CSVWriter {
    private CSVWriter() {
    }

    private static final Path BASE = Paths.get("SC2002/Project/files");

    public static void saveAll() {
        DataStore ds = DataStore.getInstance();
        writeUsers(ds, Applicant.class, "ApplicantListNew.csv");
        writeUsers(ds, HDB_Officer.class, "OfficerListNew.csv");
        writeUsers(ds, HDB_Manager.class, "ManagerListNew.csv");
        writeProjects(ds, "ProjectListNew.csv");
        writeRegistrations(ds, "RegistrationsNew.csv");
        writeApplications(ds, "ApplicationsNew.csv");
        writeEnquiries(ds, "EnquiriesNew.csv");
        writeFlats(ds, "FlatsNew.csv");
        System.out.println("All data has been saved to the system snapshot files successfully.");
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
            bw.write(
                    "Project Name,Neighborhood,Type 1,Number of units for Type 1,Selling price for Type 1,Type 2,Number of units for Type 2,Selling price for Type 2,Application opening date,Application closing date,Manager,Officer Slot,Officer");
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
                String openDate = p.getOpenDate() != null
                        ? p.getOpenDate().format(java.time.format.DateTimeFormatter.ofPattern("d/M/yyyy"))
                        : "";
                String closeDate = p.getCloseDate() != null
                        ? p.getCloseDate().format(java.time.format.DateTimeFormatter.ofPattern("d/M/yyyy"))
                        : "";
                String managerName = p.getManager() != null ? p.getManager().getFirstName() : "";
                String officerSlot = String.valueOf(p.getOfficerSlotLimit());

                // Get all assigned officers - ensure we only include approved registrations
                String officers = ds.getRegistrations().stream()
                        .filter(reg -> reg.getProject().equals(p) &&
                                reg.getStatus() == RegistrationStatus.APPROVED)
                        .map(reg -> reg.getOfficer().getFirstName())
                        .distinct() // Ensure no duplicates
                        .collect(Collectors.joining(","));

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
                        '"' + officers + '"');
                bw.write(line);
                bw.newLine();
            }
        } catch (IOException e) {
            System.err.println("Could not write " + filename + ": " + e.getMessage());
        }
    }

    /**
     * Save all registrations to RegistrationsNew.csv
     */
    private static void writeRegistrations(DataStore ds, String filename) {
        Path out = BASE.resolve(filename);
        try (BufferedWriter bw = Files.newBufferedWriter(out)) {
            // Write header
            bw.write("Registration ID,Officer NRIC,Project ID,Status,WithdrawalRequested");
            bw.newLine();

            for (Registration reg : ds.getRegistrations()) {
                String line = String.join(",",
                        String.valueOf(reg.getId()),
                        reg.getOfficer().getNric(),
                        String.valueOf(reg.getProject().getId()),
                        reg.getStatus().name(),
                        String.valueOf(reg.isWithdrawalRequested()));
                bw.write(line);
                bw.newLine();
            }
        } catch (IOException e) {
            System.err.println("Could not write " + filename + ": " + e.getMessage());
        }
    }

    /**
     * Save all applications to ApplicationsNew.csv
     */
    private static void writeApplications(DataStore ds, String filename) {
        Path out = BASE.resolve(filename);
        try (BufferedWriter bw = Files.newBufferedWriter(out)) {
            // Write header
            bw.write("Application ID,Applicant NRIC,Project ID,Flat Type,Status,WithdrawalRequested");
            bw.newLine();

            for (BTOApplication app : ds.getApplications()) {
                String line = String.join(",",
                        String.valueOf(app.getId()),
                        app.getApplicant().getNric(),
                        String.valueOf(app.getProject().getId()),
                        app.getRoomType(),
                        app.getStatus().name(),
                        String.valueOf(app.isWithdrawalRequested()));
                bw.write(line);
                bw.newLine();
            }
        } catch (IOException e) {
            System.err.println("Could not write " + filename + ": " + e.getMessage());
        }
    }

    /**
     * Save all enquiries to EnquiriesNew.csv
     */
    private static void writeEnquiries(DataStore ds, String filename) {
        Path out = BASE.resolve(filename);
        try (BufferedWriter bw = Files.newBufferedWriter(out)) {
            // Write header
            bw.write("Enquiry ID,Creator NRIC,Project ID,Flat Type,Content,Response,Respondent NRIC,Status");
            bw.newLine();

            for (Enquiry enq : ds.getEnquiries()) {
                // Handle potential nulls
                String projectId = enq.getProject() != null ? String.valueOf(enq.getProject().getId()) : "";
                String responderNric = enq.getRespondent() != null ? enq.getRespondent().getNric() : "";

                String line = String.join(",",
                        String.valueOf(enq.getId()),
                        enq.getCreator().getNric(),
                        projectId,
                        enq.getFlatType() != null ? enq.getFlatType() : "",
                        '"' + enq.getContent().replace("\"", "\"\"") + '"', // Escape quotes in content
                        '"' + enq.getResponse().replace("\"", "\"\"") + '"', // Escape quotes in response
                        responderNric,
                        enq.isAnswered() ? "ANSWERED" : "PENDING");
                bw.write(line);
                bw.newLine();
            }
        } catch (IOException e) {
            System.err.println("Could not write " + filename + ": " + e.getMessage());
        }
    }

    /**
     * Save all flats to FlatsNew.csv
     */
    private static void writeFlats(DataStore ds, String filename) {
        Path out = BASE.resolve(filename);
        try (BufferedWriter bw = Files.newBufferedWriter(out)) {
            // Write header
            bw.write("Flat ID,Project ID,Flat Type,Price,Booked Status");
            bw.newLine();

            for (Flat flat : ds.getFlats()) {
                String line = String.join(",",
                        String.valueOf(flat.getId()),
                        String.valueOf(flat.getProject().getId()),
                        flat.getFlatType(),
                        String.valueOf(flat.getPrice()),
                        String.valueOf(flat.isBooked()));
                bw.write(line);
                bw.newLine();
            }
        } catch (IOException e) {
            System.err.println("Could not write " + filename + ": " + e.getMessage());
        }
    }
}
