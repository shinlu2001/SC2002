package SC2002.Project.boundary.util;

import SC2002.Project.boundary.Menu;
import SC2002.Project.entity.enums.*;

/**
 * Convenience façade: all methods are static and simply forward to {@link Menu}.
 */
public final class MenuPrinter {

    public static void printWelcomeMenu()     { Menu.printMenu(Menu.getWelcomeMenu()); }
    public static void printLoginError()      { Menu.printMenu(Menu.getLoginError()); }
    public static void printRoleSelection()   { Menu.printMenu(Menu.getRoleMenu()); }
    public static void printApplicantMenu()   { Menu.printMenu(Menu.getApplicantMenu()); }
    public static void printOfficerMenu()     { Menu.printMenu(Menu.getOfficerMenu()); }
    public static void printManagerMenu()     { Menu.printMenu(Menu.getManagerMenu()); }
    public static void printEnquiryMenu()     { Menu.printMenu(Menu.getEnquiryMenu()); }
    public static void printReportMenu()      { Menu.printMenu(Menu.getReportMenu()); }
    public static void printEditProjectMenu() { Menu.printMenu(Menu.getEditProjectMenu()); }
    
    public static void printRoleMenu(LoginResult res) {
        if (res  == LoginResult.SUCCESS_APPLICANT) {
            MenuPrinter.printApplicantMenu();
        }
        if (res  == LoginResult.SUCCESS_OFFICER) {
            MenuPrinter.printOfficerMenu();
        }
        if (res  == LoginResult.SUCCESS_MANAGER) {
            MenuPrinter.printManagerMenu();
        }
    }

    // no instances allowed
    private MenuPrinter() { }
}
