// SC2002/Project/boundary/util/MenuPrinter.java
package SC2002.Project.boundary.util;

import SC2002.Project.boundary.Menu;

public class MenuPrinter {
    private final Menu menu;

    public MenuPrinter(Menu menu) {
        this.menu = menu;
    }

    public void printWelcomeMenu() {
        menu.printMenu(menu.getWelcomeMenu());
    }

    public void printLoginError() {
        menu.printMenu(menu.getLoginError());
    }

    public void printRoleMenu() {
        menu.printMenu(menu.getRoleMenu());
    }

    public void printApplicantMenu() {
        menu.printMenu(menu.getApplicantMenu());
    }

    public void printOfficerMenu() {
        menu.printMenu(menu.getOfficerMenu());
    }

    public void printManagerMenu() {
        menu.printMenu(menu.getManagerMenu());
    }

    public void printEnquiryMenu() {
        menu.printMenu(menu.getEnquiryMenu());
    }

    public void printReportMenu() {
        menu.printMenu(menu.getReportMenu());
    }

    /** new: for the manager’s edit‑project submenu */
    public void printEditProjectMenu() {
        menu.printMenu(menu.getEditProjectMenu());
    }
}
