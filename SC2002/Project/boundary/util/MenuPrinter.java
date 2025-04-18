package SC2002.Project.boundary.util;

import SC2002.Project.boundary.Menu;
import SC2002.Project.control.ApplicantController;
import SC2002.Project.entity.Applicant;
import SC2002.Project.entity.Project;
import java.util.List;

public final class MenuPrinter {

    /* ────────────── existing menu methods ────────────── */

    public static void printWelcomeMenu()     { Menu.printMenu(Menu.getWelcomeMenu()); }
    public static void printLoginError()      { Menu.printMenu(Menu.getLoginError()); }
    public static void printRoleSelection()   { Menu.printMenu(Menu.getRoleMenu()); }
    public static void printApplicantMenu()   { Menu.printMenu(Menu.getApplicantMenu()); }
    public static void printOfficerMenu()     { Menu.printMenu(Menu.getOfficerMenu()); }
    public static void printManagerMenu()     { Menu.printMenu(Menu.getManagerMenu()); }
    public static void printEnquiryMenu()     { Menu.printMenu(Menu.getEnquiryMenu()); }
    public static void printReportMenu()      { Menu.printMenu(Menu.getReportMenu()); }
    public static void printEditProjectMenu() { Menu.printMenu(Menu.getEditProjectMenu()); }

    /* ────────────── new table‑printing methods ────────────── */

    /**
     * Prints only those flat‑type rows the applicant is eligible for.
     */
    public static void printProjectTableEligible(List<Project> projects,
                                                 Applicant applicant,
                                                 ApplicantController ctrl) {
        // header
        System.out.printf(
            "%-" + Menu.COL_ID    + "s " +
            "%-" + Menu.COL_NAME  + "s " +
            "%-" + Menu.COL_HOOD  + "s " +
            "%-" + Menu.COL_FLAT  + "s " +
            "%-" + Menu.COL_PRICE + "s " +
            "%-" + Menu.COL_OPEN  + "s " +
            "%-" + Menu.COL_CLOSE + "s " +
            "%-" + Menu.COL_ELIG  + "s%n",
            "ID", "Name", "Neighbourhood", "FlatType(Avail/Total)",
            "Price", "Open Date", "Close Date", "Eligibility"
        );
        System.out.println("─".repeat(Menu.PROJECT_TABLE_WIDTH));

        for (Project p : projects) {
            boolean first = true;
            for (int i = 0; i < p.getFlatTypes().size(); i++) {
                String ft   = p.getFlatTypes().get(i);
                if (!ctrl.isEligibleForRoomType(ft)) continue;

                String info   = String.format("%s:(%d/%d)",
                                              ft,
                                              p.getAvailableUnits().get(i),
                                              p.getTotalUnits().get(i));
                double price  = p.getPrices().get(i);
                String elig   = "Yes";

                if (first) {
                    System.out.printf(
                        "%-" + Menu.COL_ID    + "d " +
                        "%-" + Menu.COL_NAME  + "s " +
                        "%-" + Menu.COL_HOOD  + "s " +
                        "%-" + Menu.COL_FLAT  + "s " +
                        "%-" + Menu.COL_PRICE + ".2f " +
                        "%-" + Menu.COL_OPEN  + "s " +
                        "%-" + Menu.COL_CLOSE + "s " +
                        "%-" + Menu.COL_ELIG  + "s%n",
                        p.getId(),
                        p.getName(),
                        p.getNeighbourhood(),
                        info,
                        price,
                        p.getOpenDate(),
                        p.getCloseDate(),
                        elig
                    );
                    first = false;
                } else {
                    System.out.printf(
                        "%-" + Menu.COL_ID    + "s " +
                        "%-" + Menu.COL_NAME  + "s " +
                        "%-" + Menu.COL_HOOD  + "s " +
                        "%-" + Menu.COL_FLAT  + "s " +
                        "%-" + Menu.COL_PRICE + ".2f " +
                        "%-" + Menu.COL_OPEN  + "s " +
                        "%-" + Menu.COL_CLOSE + "s " +
                        "%-" + Menu.COL_ELIG  + "s%n",
                        "", "", "", info,
                        price,
                        "", "",
                        elig
                    );
                }
            }
            System.out.println();
        }
        System.out.println("─".repeat(Menu.PROJECT_TABLE_WIDTH));
    }

    /**
     * Prints **all** flat‑type rows, marking “Yes”/“No” for eligibility.
     */
    public static void printProjectTableAll(List<Project> projects,
                                            Applicant applicant,
                                            ApplicantController ctrl) {
        // header (with Eligibility column)
        System.out.printf(
            "%-" + Menu.COL_ID    + "s " +
            "%-" + Menu.COL_NAME  + "s " +
            "%-" + Menu.COL_HOOD  + "s " +
            "%-" + Menu.COL_FLAT  + "s " +
            "%-" + Menu.COL_PRICE + "s " +
            "%-" + Menu.COL_OPEN  + "s " +
            "%-" + Menu.COL_CLOSE + "s " +
            "%-" + Menu.COL_ELIG  + "s%n",
            "ID", "Name", "Neighbourhood", "FlatType(Avail/Total)",
            "Price", "Open Date", "Close Date", "Eligibility"
        );
        System.out.println("─".repeat(Menu.PROJECT_TABLE_WIDTH));

        for (Project p : projects) {
            boolean first = true;
            for (int i = 0; i < p.getFlatTypes().size(); i++) {
                String ft    = p.getFlatTypes().get(i);
                String info  = String.format("%s:(%d/%d)",
                                             ft,
                                             p.getAvailableUnits().get(i),
                                             p.getTotalUnits().get(i));
                double price = p.getPrices().get(i);
                String elig  = ctrl.isEligibleForRoomType(ft) ? "Yes" : "No";

                if (first) {
                    System.out.printf(
                        "%-" + Menu.COL_ID    + "d " +
                        "%-" + Menu.COL_NAME  + "s " +
                        "%-" + Menu.COL_HOOD  + "s " +
                        "%-" + Menu.COL_FLAT  + "s " +
                        "%-" + Menu.COL_PRICE + ".2f " +
                        "%-" + Menu.COL_OPEN  + "s " +
                        "%-" + Menu.COL_CLOSE + "s " +
                        "%-" + Menu.COL_ELIG  + "s%n",
                        p.getId(),
                        p.getName(),
                        p.getNeighbourhood(),
                        info,
                        price,
                        p.getOpenDate(),
                        p.getCloseDate(),
                        elig
                    );
                    first = false;
                } else {
                    System.out.printf(
                        "%-" + Menu.COL_ID    + "s " +
                        "%-" + Menu.COL_NAME  + "s " +
                        "%-" + Menu.COL_HOOD  + "s " +
                        "%-" + Menu.COL_FLAT  + "s " +
                        "%-" + Menu.COL_PRICE + ".2f " +
                        "%-" + Menu.COL_OPEN  + "s " +
                        "%-" + Menu.COL_CLOSE + "s " +
                        "%-" + Menu.COL_ELIG  + "s%n",
                        "", "", "", info,
                        price,
                        "", "",
                        elig
                    );
                }
            }
            System.out.println();
        }
        System.out.println("─".repeat(Menu.PROJECT_TABLE_WIDTH));
    }

    /**
     * Prints a simpler table of projects, suitable for selection lists.
     */
    public static void printProjectTableSimple(List<Project> projects) {
        // Simplified header
        System.out.printf(
            "%-" + Menu.COL_ID    + "s " +
            "%-" + Menu.COL_NAME  + "s " +
            "%-" + Menu.COL_HOOD  + "s%n",
            "ID", "Name", "Neighbourhood"
        );
        int simpleWidth = Menu.COL_ID + Menu.COL_NAME + Menu.COL_HOOD + 2; // Adjust width
        System.out.println("─".repeat(simpleWidth));

        for (Project p : projects) {
            System.out.printf(
                "%-" + Menu.COL_ID    + "d " +
                "%-" + Menu.COL_NAME  + "s " +
                "%-" + Menu.COL_HOOD  + "s%n",
                p.getId(),
                p.getName(),
                p.getNeighbourhood()
            );
        }
        System.out.println("─".repeat(simpleWidth));
    }

    /**
     * Prints a detailed table of projects, including all flat types and manager info.
     * Based on ProjectOLD HDB_Manager.viewAllProjects format.
     */
    public static void printProjectTableDetailed(List<Project> projects) {
        // Header similar to ProjectOLD
        String headerFormat = "%-5s %-25s %-15s %-25s %-15s %-12s %-12s %-10s %-15s %-15s%n";
        int detailedWidth = 5 + 25 + 15 + 25 + 15 + 12 + 12 + 10 + 15 + 15 + 9; // Sum of column widths + spaces

        System.out.printf(headerFormat,
                "ID", "Project Name", "Neighbourhood", "Flat Types (Units/Price)", "Manager",
                "Open Date", "Close Date", "Visible", "Officer Slots", "Assigned Officers");
        System.out.println("─".repeat(detailedWidth));

        for (Project p : projects) {
            boolean firstFlat = true;
            for (int i = 0; i < p.getFlatTypes().size(); i++) {
                String flatType = p.getFlatTypes().get(i);
                int units = p.getTotalUnits().get(i);
                double price = p.getPrices().get(i);
                String flatInfo = String.format("%s (%d / $%.2f)", flatType, units, price);

                if (firstFlat) {
                    System.out.printf(headerFormat,
                            p.getId(),
                            p.getName(),
                            p.getNeighbourhood(),
                            flatInfo,
                            (p.getManager() != null ? p.getManager().getFirstName() : "N/A"), // Assuming manager has getName()
                            p.getOpenDate(),
                            p.getCloseDate(),
                            p.getVisibility(),
                            p.getOfficerSlotLimit(),
                            p.getAssignedOfficers().size() // Use .size() on the list
                    );
                    firstFlat = false;
                } else {
                    // Print subsequent flat types aligned under the flat type column
                    System.out.printf("%-" + (5 + 25 + 15 + 1) + "s%s%n", "", flatInfo); // Adjust spacing
                }
            }
             if (p.getFlatTypes().isEmpty()) { // Handle projects with no flats yet
                 System.out.printf(headerFormat,
                         p.getId(),
                         p.getName(),
                         p.getNeighbourhood(),
                         "(No flat types defined)",
                         (p.getManager() != null ? p.getManager().getFirstName() : "N/A"),
                         p.getOpenDate(),
                         p.getCloseDate(),
                         p.getVisibility(),
                         p.getOfficerSlotLimit(),
                         p.getAssignedOfficers().size() // Use .size() on the list
                 );
             }
            System.out.println(); // Add a blank line between projects
        }
        System.out.println("─".repeat(detailedWidth));
    }

    /**
     * Prints a menu showing current flat types, units, and prices for a project.
     * Based on ProjectOLD HDB_Manager.FlatTypesMenu.
     */
    public static void printFlatTypesMenu(Project p) {
        System.out.println("\n--- Current Flat Types for Project: " + p.getName() + " ---");
        String format = "%-5s %-15s %-15s %-15s %-10s%n";
        int width = 5 + 15 + 15 + 15 + 10 + 4;
        System.out.printf(format, "No.", "Flat Type", "Price", "Total Units", "Available");
        System.out.println("─".repeat(width));
        List<String> flatTypes = p.getFlatTypes();
        List<Integer> totalUnits = p.getTotalUnits();
        List<Integer> availableUnits = p.getAvailableUnits();
        List<Double> prices = p.getPrices();

        for (int i = 0; i < flatTypes.size(); i++) {
            System.out.printf(format,
                    (i + 1),
                    flatTypes.get(i),
                    String.format("$%.2f", prices.get(i)),
                    totalUnits.get(i),
                    availableUnits.get(i));
        }
         if (flatTypes.isEmpty()) {
             System.out.println("(No flat types defined for this project)");
         }
        System.out.println("─".repeat(width));
    }

    // no instances
    private MenuPrinter() { }

    public static void printRoleMenuHeader(int role) {
        switch (role) {
            case 0 -> {
                System.out.println("====================================================================================================================");
                System.out.println("                                          A P P L I C A N T   M E N U");
                System.out.println("====================================================================================================================");
            }
            case 1 -> {
                System.out.println("====================================================================================================================");
                System.out.println("                                            O F F I C E R   M E N U");
                System.out.println("====================================================================================================================");
            }
            case 2 -> {
                System.out.println("====================================================================================================================");
                System.out.println("                                            M A N A G E R   M E N U");
                System.out.println("====================================================================================================================");
            }
        }
    }
}
