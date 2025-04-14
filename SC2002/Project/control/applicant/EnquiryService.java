package SC2002.Project.control.applicant;

// import java.util.ArrayList;
import java.util.InputMismatchException;
import java.util.List;
import java.util.Scanner;

import SC2002.Project.ApplicantBase;
import SC2002.Project.BTOsystem;
import SC2002.Project.Enquiry;
import SC2002.Project.Input;
import SC2002.Project.Menu;
import SC2002.Project.Project;
import SC2002.Project.Input.InputExitException;
import SC2002.Project.boundary.ApplicantEnquiryDisplayer;
import SC2002.Project.boundary.applicantProjectDisplayer;

public class EnquiryService {
    private final ApplicantBase applicant;
    private applicantProjectDisplayer projectDisplayer;
    private List<Enquiry> enquiries = null;
    protected Menu menu=new Menu();
    enum EnquiryOption {
        GENERAL, PROJECT_RELATED, EDIT, VIEW_ALL, DELETE, RETURN;
    }
    //dependency injection, allows enquiry service to interact with an applicant class without tightly coupling itself to a specific instance
    public EnquiryService(ApplicantBase a, applicantProjectDisplayer displayer) {
        applicant = a;
        enquiries = a.getEnquiry();
        projectDisplayer = displayer;
    }

    public void manage_enquiry(Scanner sc) {
        ApplicantEnquiryDisplayer enquiryDisplayer = new ApplicantEnquiryDisplayer(applicant);
        ApplicantEnquiryHandler enquiryHandler = new ApplicantEnquiryHandler(applicant);
        int choice = 0;
        do {
            try {
                System.out.println("====================================================================================================================");
                System.out.println("                                         E N Q U I R Y   M E N U");
                System.out.println("====================================================================================================================");
                menu.printEnquiryMenu();
                choice = Input.getIntInput(sc);
                System.out.println("====================================================================================================================");
                if (choice >= 1 && choice <= EnquiryOption.values().length) {
                    EnquiryOption selectedOption = EnquiryOption.values()[choice - 1];
                    switch (selectedOption) {
                        case GENERAL:
                            System.out.println("Enquiry: ");
                            String content = Input.getStringInput(sc);
                            enquiryHandler.makeEnquiry(content);
                            System.out.println("Enquiry sent!");
                            break;
                        case PROJECT_RELATED:
                            projectDisplayer.view_listings();
                            System.out.println("Enter ID of project to enquire about: ");
                            int projectId = Input.getIntInput(sc);
                            Project p = BTOsystem.searchById(BTOsystem.projects, projectId, Project::getId);
                            while (p == null) {
                                System.out.println("Invalid ID, try again: ");
                                projectId = Input.getIntInput(sc);
                                p = BTOsystem.projects.get(projectId);
                            }
                            System.out.println("Enter flat type (2-Room, 3-Room, etc.): ");
                            String flatType = Input.getStringInput(sc).toUpperCase();
                            System.out.println("Enquiry: ");
                            String project_content = Input.getStringInput(sc);
                            enquiryHandler.makeEnquiry(p, project_content, flatType);
                            System.out.println("Enquiry sent!");
                            break;
                        case EDIT:
                            System.out.println("Edit enquiry");
                            enquiryDisplayer.viewEditableEnquiry();
                            System.out.println("Enter ID of enquiry to edit: ");
                            int id = Input.getIntInput(sc);
                            Enquiry result = enquiries.stream()
                                    .filter(en -> en.getId() == id)
                                    .findFirst()
                                    .orElse(null);
                            if (result == null) {
                                System.out.println("No such enquiry.");
                                break;
                            } else if (result.getStaff() != null) {
                                System.out.println("Enquiry has already been replied to. Please make a new enquiry instead.");
                                break;
                            }
                            enquiryDisplayer.view_enquiry(result);
                            System.out.print("Enquiry: ");
                            String userInput = Input.getStringInput(sc);
                            enquiryHandler.editEnquiry(result, userInput);
                            System.out.println("Enquiry edited!");
                            break;
                        case VIEW_ALL:
                            System.out.println("                                              All Enquiries");
                            System.out.println("====================================================================================================================");
                            enquiryDisplayer.view_all_enquiry_for_user();
                            System.out.println("Select enquiry to view (-1 to cancel)");
                            int en_id = Input.getIntInput(sc);
                            if (en_id == -1) {
                                break;
                            }
                            Enquiry en = BTOsystem.searchById(enquiries, en_id, Enquiry::getId);
                            if (en == null) {
                                System.err.println("Invalid ID");
                                break;
                            }
                            enquiryDisplayer.view_enquiry(en);
                            break;
                        case DELETE:
                            System.out.println("                                           Delete enquiry");
                            System.out.println("================================================================================================================");
                            enquiryDisplayer.view_all_enquiry_for_user();
                            System.out.print("Enter ID of enquiry to delete: ");
                            int del_id = Input.getIntInput(sc);
                            enquiryHandler.deleteEnquiry(del_id);
                            System.out.println("Enquiry deleted!");
                            break;
                        case RETURN:
                            System.out.println("Returning to applicant menu...");
                            break;
                        default:
                            System.out.println("Invalid choice. Please try again.");
                    }
                }
            } catch (Input.InputExitException e) {
                System.out.println("User requested exit/back in enquiry menu. Returning to applicant menu.");
                break;
            } catch (InputMismatchException e) {
                System.out.println("Invalid input. Please enter a number.");
                sc.nextLine();
            }
        } while (choice != 6);
    }

    // protected void makeEnquiry(String content) {
    //     Enquiry en = new Enquiry(applicant, content);
    //     enquiries.add(en);
    //     BTOsystem.enquiries.add(en);
    // }

    // protected void makeEnquiry(Project project, String content, String flatType) {
    //     Enquiry en = new Enquiry(applicant, content);
    //     en.setProject(project);
    //     en.setflatType(flatType);
    //     enquiries.add(en);
    //     BTOsystem.enquiries.add(en);
    //     project.addEnquiry(en);
    // }

    // protected void view_all_enquiry_for_user() {
    //     System.out.printf("%-5s %-20s %-30s %-30s %-15s %-20s%n",
    //             "ID", "Project", "Enquiry", "Reply", "Status", "Replied by");
    //     System.out.println("====================================================================================================================");
    //     for (Enquiry enquiry : enquiries) {
    //         System.out.printf("%-5d %-20s %-30s %-30s %-15s %-20s%n",
    //                 enquiry.getId(),
    //                 enquiry.getProject() != null ? enquiry.getProject().getProjectName() : "General Enquiry",
    //                 Input.truncateText(enquiry.getEnquiry(), 30),
    //                 Input.truncateText(enquiry.getResponse(), 30),
    //                 enquiry.getResponse().isEmpty() ? "Pending" : "Answered",
    //                 enquiry.getStaff() != null ? enquiry.getStaff().get_firstname() : "");
    //         if (enquiry.getflatType() != null && !enquiry.getflatType().isEmpty()) {
    //             System.out.printf("%-5s %-20s %-30s%n", "", "", "Flat type: " + enquiry.getflatType());
    //         }
    //     }
    // }

    // protected void view_enquiry(Enquiry en) {
    //     en.display();
    // }

    // protected void viewEditableEnquiry() {
    //     System.out.printf("%-5s %-20s %-30s %-15s%n",
    //             "ID", "Project", "Enquiry", "Status");
    //     System.out.println("==================================================================================================================");
    //     for (Enquiry enquiry : enquiries) {
    //         if (!enquiry.getResponse().isBlank()) {
    //             continue;
    //         }
    //         System.out.printf("%-5d %-20s %-30s %-15s%n",
    //                 enquiry.getId(),
    //                 enquiry.getProject() != null ? enquiry.getProject().getProjectName() : "General Enquiry",
    //                 Input.truncateText(enquiry.getEnquiry(), 30),
    //                 "Pending");
    //         if (enquiry.getflatType() != null && !enquiry.getflatType().isEmpty()) {
    //             System.out.printf("%-5s %-20s %-30s%n", "", "", "Flat type: " + enquiry.getflatType());
    //         }
    //     }
    // }

    // protected void editEnquiry(Enquiry en, String content) {
    //     en.setEnquiry(content);
    // }

    // protected void deleteEnquiry(int id) {
    //     Enquiry toRemove = null;
    //     for (Enquiry en : enquiries) {
    //         if (en.getId() == id) {
    //             toRemove = en;
    //             break;
    //         }
    //     }
    //     if (toRemove != null) {
    //         new ApplicantEnquiryDisplayer(applicant).view_enquiry(toRemove);
    //         enquiries.remove(toRemove);
    //         System.out.println("Deleted enquiry: " + toRemove.getEnquiry());
    //         System.out.println("Deleted response: " + toRemove.getResponse());
    //     } else {
    //         System.out.println("No enquiry found with ID: " + id);
    //     }
    // }
}
