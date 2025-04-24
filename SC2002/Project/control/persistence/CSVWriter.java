// SC2002/Project/control/persistence/CSVWriter.java
package SC2002.Project.control.persistence;

import SC2002.Project.entity.*;
import SC2002.Project.entity.enums.RegistrationStatus;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.*;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Saves data from in-memory DataStore to CSV files
 */
public final class CSVWriter {
    private CSVWriter() {
    }

    private static final Path BASE = Paths.get("SC2002/Project/files");
    private static final DateTimeFormatter DF = DateTimeFormatter.ofPattern("d/M/yyyy");

    /**
     * Saves all data to system snapshot files in the proper order:
     * 1. Users (Applicants, Officers, Managers)
     * 2. Projects
     * 3. Flats (related to Projects)
     * 4. Registrations (relating Officers to Projects)
     * 5. Applications (relating Applicants to Projects)
     * 6. Enquiries (relating Users to optional Projects)
     */
    public static void saveAll() {
        DataStore ds = DataStore.getInstance();

        // Step 1: Save all user types first
        writeUsers(ds, HDB_Officer.class, "OfficerListNew.csv");
        writeUsers(ds, Applicant.class, "ApplicantListNew.csv");
        writeUsers(ds, HDB_Manager.class, "ManagerListNew.csv");

        // Step 2: Save projects (they can exist independently)
        writeProjects(ds, "ProjectListNew.csv");

        // Step 3: Save flats (dependent on projects)
        writeFlats(ds, "FlatsNew.csv");

        // Step 4: Save registrations (relate officers to projects)
        writeRegistrations(ds, "RegistrationsNew.csv");

        // Step 5: Save applications (relate applicants to projects)
        writeApplications(ds, "ApplicationsNew.csv");

        // Step 6: Save enquiries (relate users and optionally projects)
        writeEnquiries(ds, "EnquiriesNew.csv");

        System.out.println("All data has been saved to the system snapshot files successfully:");
        System.out.println(" - Users: " + ds.getUsers().size() +
                "\n - Projects: " + ds.getProjects().size() +
                "\n - Flats: " + ds.getFlats().size() +
                "\n - Registrations: " + ds.getRegistrations().size() +
                "\n - Applications: " + ds.getApplications().size() +
                "\n - Enquiries: " + ds.getEnquiries().size());
    }

    /**
     * Write users of a specific type to a CSV file
     */
    private static void writeUsers(DataStore ds,
            Class<? extends User> roleClass,
            String filename) {
        Path out = BASE.resolve(filename);
        try (BufferedWriter bw = Files.newBufferedWriter(out)) {
            bw.write("Name,NRIC,Age,Marital Status,Password");
            bw.newLine();

            ds.getUsers().stream()
                    .filter(roleClass::isInstance)
                    .filter(u -> roleClass.isInstance(u)
                            && (u.getClass() == roleClass || !Applicant.class.equals(roleClass)))
                    .forEach(u -> {
                        String fullName = u.getFirstName() + " " + u.getLastName();
                        String line = String.join(",",
                                fullName.trim(),
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

    /**
     * Save all projects to CSV
     */
    private static void writeProjects(DataStore ds, String filename) {
        Path out = BASE.resolve(filename);
        try (BufferedWriter bw = Files.newBufferedWriter(out)) {
            // Write header
            bw.write("Project Name,Neighborhood,Type 1,Number of units for Type 1,Selling price for Type 1," +
                    "Type 2,Number of units for Type 2,Selling price for Type 2," +
                    "Application opening date,Application closing date,Manager,Officer Slot,Officers,Visibility,ProjectID,"
                    +
                    "Available Units 1,Available Units 2"); // Added available units fields
            bw.newLine();

            for (Project p : ds.getProjects()) {
                // Only support up to 2 flat types for CSV compatibility
                List<String> types = p.getFlatTypes();
                List<Integer> totalUnits = p.getTotalUnits();
                List<Integer> availableUnits = p.getAvailableUnits(); // Get current available units
                List<Double> prices = p.getPrices();

                String type1 = types.size() > 0 ? types.get(0) : "";
                String type2 = types.size() > 1 ? types.get(1) : "";
                String units1 = totalUnits.size() > 0 ? String.valueOf(totalUnits.get(0)) : "";
                String units2 = totalUnits.size() > 1 ? String.valueOf(totalUnits.get(1)) : "";
                String price1 = prices.size() > 0 ? String.valueOf(prices.get(0)) : "";
                String price2 = prices.size() > 1 ? String.valueOf(prices.get(1)) : "";
                // Save current available units explicitly
                String avail1 = availableUnits.size() > 0 ? String.valueOf(availableUnits.get(0)) : "";
                String avail2 = availableUnits.size() > 1 ? String.valueOf(availableUnits.get(1)) : "";

                String openDate = p.getOpenDate() != null
                        ? p.getOpenDate().format(DF)
                        : "";
                String closeDate = p.getCloseDate() != null
                        ? p.getCloseDate().format(DF)
                        : "";

                String managerName = p.getManager() != null ? p.getManager().getFirstName() : "";
                String officerSlot = String.valueOf(p.getOfficerSlotLimit());
                String visibility = p.getVisibility().name();

                // Get all assigned officers - only include approved registrations
                String officers = ds.getRegistrations().stream()
                        .filter(reg -> reg.getProject().equals(p) &&
                                reg.getStatus() == RegistrationStatus.APPROVED)
                        .map(reg -> reg.getOfficer().getFirstName())
                        .distinct() // Ensure no duplicates
                        .collect(Collectors.joining(","));

                // Include project ID at the end for snapshot loading
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
                        '"' + officers + '"',
                        visibility,
                        String.valueOf(p.getId()),
                        avail1,
                        avail2); // Added available units
                bw.write(line);
                bw.newLine();
            }
        } catch (IOException e) {
            System.err.println("Could not write " + filename + ": " + e.getMessage());
        }
    }

    /**
     * Save all registrations to CSV
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
     * Save all applications to CSV
     */
    private static void writeApplications(DataStore ds, String filename) {
        Path out = BASE.resolve(filename);
        try (BufferedWriter bw = Files.newBufferedWriter(out)) {
            // Write header
            bw.write("Application ID,Applicant NRIC,Project ID,Flat Type,Status,WithdrawalRequested,BookedFlatID");
            bw.newLine();

            for (BTOApplication app : ds.getApplications()) {
                // Include booked flat ID if applicable
                String bookedFlatId = app.getBookedFlat() != null ? String.valueOf(app.getBookedFlat().getId()) : "";

                String line = String.join(",",
                        String.valueOf(app.getId()),
                        app.getApplicant().getNric(),
                        String.valueOf(app.getProject().getId()),
                        app.getRoomType(),
                        app.getStatus().name(),
                        String.valueOf(app.isWithdrawalRequested()),
                        bookedFlatId);
                bw.write(line);
                bw.newLine();
            }
        } catch (IOException e) {
            System.err.println("Could not write " + filename + ": " + e.getMessage());
        }
    }

    /**
     * Save all enquiries to CSV
     */
    private static void writeEnquiries(DataStore ds, String filename) {
        Path out = BASE.resolve(filename);
        try (BufferedWriter bw = Files.newBufferedWriter(out)) {
            // Write header
            bw.write("Enquiry ID,Creator NRIC,Project ID,Flat Type,Content,Response,Respondent NRIC");
            bw.newLine();

            for (Enquiry enq : ds.getEnquiries()) {
                // Handle potential nulls
                String projectId = enq.getProject() != null ? String.valueOf(enq.getProject().getId()) : "";
                String flatType = enq.getFlatType() != null ? enq.getFlatType() : "";
                String responderNric = enq.getRespondent() != null ? enq.getRespondent().getNric() : "";

                // Properly escape any commas or quotes in content/response
                String content = '"' + enq.getContent().replace("\"", "\"\"") + '"';
                String response = '"' + enq.getResponse().replace("\"", "\"\"") + '"';

                String line = String.join(",",
                        String.valueOf(enq.getId()),
                        enq.getCreator().getNric(),
                        projectId,
                        flatType,
                        content,
                        response,
                        responderNric);
                bw.write(line);
                bw.newLine();
            }
        } catch (IOException e) {
            System.err.println("Could not write " + filename + ": " + e.getMessage());
        }
    }

    /**
     * Save all flats to CSV, ensuring no duplicate bookings
     */
    private static void writeFlats(DataStore ds, String filename) {
        Path out = BASE.resolve(filename);
        try (BufferedWriter bw = Files.newBufferedWriter(out)) {
            // Write header
            bw.write("Flat ID,Project ID,Flat Type,Price,Booked Status,Booking ID");
            bw.newLine();

            // Create a set to track already processed booking IDs for each project/flat
            // type combination
            java.util.Set<String> processedBookings = new java.util.HashSet<>();

            for (Flat flat : ds.getFlats()) {
                // Create a unique key for this booking to prevent duplicates
                String bookingKey = "";
                if (flat.isBooked() && !flat.getBookingId().isEmpty()) {
                    bookingKey = flat.getProject().getId() + "-" + flat.getFlatType() + "-" + flat.getBookingId();
                    // Skip if we've already processed this booking
                    if (processedBookings.contains(bookingKey)) {
                        continue;
                    }
                    processedBookings.add(bookingKey);
                }

                String line = String.join(",",
                        String.valueOf(flat.getId()),
                        String.valueOf(flat.getProject().getId()),
                        flat.getFlatType(),
                        String.valueOf(flat.getPrice()),
                        String.valueOf(flat.isBooked()),
                        flat.getBookingId());
                bw.write(line);
                bw.newLine();
            }
        } catch (IOException e) {
            System.err.println("Could not write " + filename + ": " + e.getMessage());
        }
    }
}
