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

    // no instances
    private MenuPrinter() { }
}
