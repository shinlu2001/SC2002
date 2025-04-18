// SC2002/Project/control/RegistrationController.java
package SC2002.Project.control;

import SC2002.Project.control.persistence.DataStore;
import SC2002.Project.entity.HDB_Officer;
import SC2002.Project.entity.Project;
import SC2002.Project.entity.Registration;
import SC2002.Project.entity.enums.RegistrationStatus;
import SC2002.Project.util.IdGenerator;
import java.util.List;
import java.util.stream.Collectors;

public class RegistrationController {
    private final DataStore dataStore = DataStore.getInstance();
    private final ProjectController projectController = new ProjectController(); // Needed to find projects

    /** Register an officer for a project */
    public Registration register(HDB_Officer officer, int projectId) {
        Project project = projectController.findById(projectId);
        if (project == null || officer == null) {
            System.out.println("Error: Project or Officer not found.");
            return null;
        }

        // Check if already registered for this project
        boolean alreadyRegistered = dataStore.getRegistrations().stream()
                .anyMatch(reg -> reg.getOfficer().equals(officer) && reg.getProject().equals(project));
        if (alreadyRegistered) {
            System.out.println("Error: Officer already registered (or pending) for this project.");
            return null;
        }

        // TODO: Add checks for overlapping periods, officer slots, applicant status for same project

        Registration newReg = new Registration(IdGenerator.nextRegistrationId(), officer, project);
        dataStore.getRegistrations().add(newReg);
        officer.addRegistration(newReg); // Link back to officer
        System.out.println("Registration submitted for Officer " + officer.getFirstName() + " for Project " + project.getName() + ". Awaiting manager approval.");
        return newReg;
    }

    /** Find registration by ID */
    public Registration findById(int id) {
        return dataStore.getRegistrations().stream()
                .filter(reg -> reg.getId() == id)
                .findFirst()
                .orElse(null);
    }

    /** List all registrations */
    public List<Registration> listAll() {
        return dataStore.getRegistrations();
    }

    /** List registrations for a specific project */
    public List<Registration> listForProject(int projectId) {
        return dataStore.getRegistrations().stream()
                .filter(reg -> reg.getProject().getId() == projectId)
                .collect(Collectors.toList());
    }

    /** List registrations for a specific officer */
    public List<Registration> listForOfficer(int officerId) {
        return dataStore.getRegistrations().stream()
                .filter(reg -> reg.getOfficer().getOfficerId() == officerId)
                .collect(Collectors.toList());
    }


    /** Change registration status (approve/reject/withdraw) */
    public boolean changeStatus(int registrationId, RegistrationStatus status) {
        Registration reg = findById(registrationId);
        if (reg == null) {
            System.out.println("Error: Registration not found.");
            return false;
        }

        RegistrationStatus currentStatus = reg.getStatus();
        Project project = reg.getProject();
        HDB_Officer officer = reg.getOfficer();

        return switch (status) {
            case APPROVED -> {
                if (currentStatus == RegistrationStatus.PENDING) {
                    // Check officer slots
                    long approvedCount = listForProject(project.getId()).stream()
                            .filter(r -> r.getStatus() == RegistrationStatus.APPROVED)
                            .count();
                    if (approvedCount >= project.getOfficerSlotLimit()) {
                        System.out.println("Error: Project officer slots are full (" + project.getOfficerSlotLimit() + "). Cannot approve.");
                        yield false;
                    }
                    // TODO: Check for overlapping approved registrations for the same officer

                    reg.approve();
                    project.addAssignedOfficer(officer); // Use correct method name
                    System.out.println("Registration ID " + registrationId + " approved.");
                    yield true;
                } else {
                    System.out.println("Error: Can only approve PENDING registrations.");
                    yield false;
                }
            }
            case REJECTED -> {
                if (currentStatus == RegistrationStatus.PENDING) {
                    reg.reject();
                    System.out.println("Registration ID " + registrationId + " rejected.");
                    // Optionally remove the registration object entirely? Or keep it as REJECTED? Keep for now.
                    yield true;
                } else {
                    System.out.println("Error: Can only reject PENDING registrations.");
                    yield false;
                }
            }
            case WITHDRAWN -> {
                 if (currentStatus == RegistrationStatus.PENDING || currentStatus == RegistrationStatus.APPROVED) {
                    reg.withdraw(); // Sets status to WITHDRAWN
                    if (currentStatus == RegistrationStatus.APPROVED) {
                        project.removeAssignedOfficer(officer); // Use correct method name
                    }
                    System.out.println("Registration ID " + registrationId + " withdrawn.");
                    // Officer might need to initiate this, or manager confirms. Let's assume manager forces it for now.
                    yield true;
                } else {
                    System.out.println("Error: Cannot withdraw a registration with status " + currentStatus);
                    yield false;
                }
            }
            default -> {
                System.out.println("Error: Unknown status change requested.");
                yield false;
            }
        };
    }

    // This method might belong more in OfficerController or be initiated from OfficerUI
    /** Withdraw a registration (if allowed) - Officer initiated */
    public boolean requestWithdrawal(int registrationId, HDB_Officer requestingOfficer) {
         Registration reg = findById(registrationId);
         if (reg == null || !reg.getOfficer().equals(requestingOfficer)) {
             System.out.println("Error: Registration not found or does not belong to this officer.");
             return false;
         }
         // For now, let's make withdrawal immediate via changeStatus(id, WITHDRAWN)
         // If a PENDING_WITHDRAWAL state is needed, this logic would change.
         System.out.println("Withdrawal request received. Use changeStatus to finalize."); // Placeholder message
         return changeStatus(registrationId, RegistrationStatus.WITHDRAWN); // Direct withdrawal for now
    }
}
