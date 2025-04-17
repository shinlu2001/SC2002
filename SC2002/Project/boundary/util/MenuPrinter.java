package SC2002.Project.boundary.util;

import SC2002.Project.boundary.Menu;

public class MenuPrinter {
    private final Menu menu;

    public MenuPrinter(Menu menu) {
        this.menu = menu;
    }

    public void printWelcome() {
        menu.printMenu(menu.getWelcomeMenu());
    }

    public void printLoginError() {
        menu.printMenu(menu.getLoginError());
    }

    public void printRoleSelection() {
        menu.printMenu(menu.getRoleMenu());
    }

    public void printApplicant() {
        menu.printMenu(menu.getApplicantMenu());
    }

    public void printOfficer() {
        menu.printMenu(menu.getOfficerMenu());
    }

    public void printManager() {
        menu.printMenu(menu.getManagerMenu());
    }

    public void printEnquiry() {
        menu.printMenu(menu.getEnquiryMenu());
    }

    public void printReport() {
        menu.printMenu(menu.getReportMenu());
    }


}
