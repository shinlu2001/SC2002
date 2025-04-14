package SC2002.Project;

import java.util.*;

import SC2002.Project.boundary.BTOService;
import SC2002.Project.boundary.applicantProjectDisplayer;
import SC2002.Project.control.PasswordChanger;
import SC2002.Project.control.applicant.EnquiryService;
import SC2002.Project.control.applicant.WithdrawApplicationService;
import SC2002.Project.control.applicant.applyBTOServiceApplicant;

public class Applicant extends ApplicantBase {
    protected static int nextId = -1;
    private int applicantID;
    private String type = "APPLICANT";
    public Applicant(String nric, String firstname, String lastname, String marital_status, int age) {
        super(nric, firstname, lastname, marital_status, age);
        applicantID = ++nextId;
    }

    enum ApplicantOption {
        APPLY, VIEW_APPLICATION, VIEW_ELIGIBLE, VIEW_LISTINGS, WITHDRAW, ENQUIRY, ACCOUNT, CHANGE_PASSWORD, EXIT;
    }

    public void to_string() {
        super.to_string();
        System.out.println("Account type: " + type);
        System.out.println("ApplicantID: " + applicantID);
    }

    @Override
    public void start_menu(Scanner sc) {
        System.out.println("Welcome to HDB BTO Management System, " + this.get_firstname() + "!");
        int choice = 0;
        applicantProjectDisplayer displayer = new applicantProjectDisplayer(this);
        // Continue displaying the applicant menu until the user selects the explicit exit option.
        while (true) {
            try {
                System.out.println("====================================================================================================================");
                System.out.println("                                          A P P L I C A N T   M E N U");
                System.out.println("====================================================================================================================");
                menu.printApplicantMenu();
                try {
                    choice = Input.getIntInput(sc);
                } catch (Input.InputExitException e) {
                    System.out.println("Operation cancelled. Returning to Applicant menu.");
                    continue; // re-display the applicant menu
                }
                System.out.println("====================================================================================================================");
                if (choice >= 1 && choice <= ApplicantOption.values().length) {
                    ApplicantOption selectedOption = ApplicantOption.values()[choice - 1];
                    switch (selectedOption) {
                        case APPLY:
                            try {
                                applyBTOServiceApplicant applyer = new applyBTOServiceApplicant(this);
                                applyer.applyProject(sc, this, displayer);
                            } catch (Input.InputExitException e) {
                                System.out.println("Operation cancelled. Returning to Applicant menu.");
                            }
                            break;
                            
                        case VIEW_APPLICATION:
                            try {
                                BTOService.displayApplication(sc, this);
                            } catch (Input.InputExitException e) {
                                System.out.println("Operation cancelled. Returning to Applicant menu.");
                            }
                            break;
                            
                        case VIEW_ELIGIBLE:
                            try {
                                int countEligible = displayer.view_eligible_listings();
                                if (countEligible == 0) {
                                    System.out.println("You are not eligible to apply for any project.");
                                }
                            } catch (Input.InputExitException e) {
                                System.out.println("Operation cancelled. Returning to Applicant menu.");
                            }
                            break;
                            
                        case VIEW_LISTINGS:
                            displayer.view_listings();
                            break;
                            
                        case WITHDRAW:
                            try {
                                WithdrawApplicationService.withdraw(sc, application, this);
                            } catch (Input.InputExitException e) {
                                System.out.println("Operation cancelled. Returning to Applicant menu.");
                            }
                            break;
                            
                        case ENQUIRY:
                            try {
                                new EnquiryService(this, displayer).manage_enquiry(sc);
                            } catch (Input.InputExitException e) {
                                System.out.println("Operation cancelled. Returning to Applicant menu.");
                            }
                            break;
                            
                        case ACCOUNT:
                            try {
                                System.out.println("                                               Account details");
                                System.out.println("====================================================================================================================");
                                to_string();
                            } catch (Input.InputExitException e) {
                                System.out.println("Operation cancelled. Returning to Applicant menu.");
                            }
                            break;
                            
                        case CHANGE_PASSWORD:
                            try {
                                new PasswordChanger(this);
                            } catch (Input.InputExitException e) {
                                System.out.println("Operation cancelled. Returning to Applicant menu.");
                            }
                            break;
                            
                        case EXIT:
                            System.out.println("Logged out. Returning to role menu...");
                            return;
                            
                        default:
                            System.out.println("Invalid choice. Please try again.");
                    }
                }
            } catch (Input.InputExitException e) {
                System.out.println("Operation cancelled by user. Returning to Applicant menu.");
                continue;
            } catch (InputMismatchException e) {
                System.out.println("Invalid input. Please enter a number.");
                sc.nextLine();
            }
        }
    }    
}