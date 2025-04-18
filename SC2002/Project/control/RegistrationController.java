// SC2002/Project/control/RegistrationController.java
package SC2002.Project.control;

import SC2002.Project.control.persistence.DataStore;
import SC2002.Project.entity.BTOApplication;
import SC2002.Project.entity.HDB_Officer;
import SC2002.Project.entity.Project;
import SC2002.Project.entity.Registration;
import SC2002.Project.entity.enums.ApplicationStatus;
import SC2002.Project.entity.enums.RegistrationStatus;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class RegistrationController {
    private final DataStore dataStore = DataStore.getInstance();
    private final ProjectController projectController = new ProjectController(); // Needed to find projects

    /** Register an officer for a project */
    public Registration register(HDB_Officer officer, int projectId) {
        Project projectToRegister = projectController.findById(projectId);
        if (projectToRegister == null || officer == null) {
            System.out.println("Error: Project or Officer not found.");
            return null;
        }

        // Check if already registered (any status) for this specific project
        boolean alreadyRegistered = dataStore.getRegistrations().stream()
                .anyMatch(reg -> reg.getOfficer().equals(officer) && reg.getProject().equals(projectToRegister));
        if (alreadyRegistered) {
            System.out.println("Error: You are already registered (or have a pending/past registration) for this project: " + projectToRegister.getName());
            return null;
        }

        // Check if the officer has an active BTO application for the same project
        Optional<BTOApplication> existingApp = officer.getCurrentApplication();
        if (existingApp.isPresent() && existingApp.get().getProject().equals(projectToRegister)) {
            ApplicationStatus appStatus = existingApp.get().getStatus();
            if (appStatus == ApplicationStatus.PENDING || appStatus == ApplicationStatus.SUCCESS || appStatus == ApplicationStatus.BOOKED) {
                System.out.println("Error: Cannot register as officer for project '" + projectToRegister.getName() + 
                                   "' because you have an active application (Status: " + appStatus + ") for it.");
                return null;
            }
        }

        // Check if the officer has other PENDING registrations for projects with overlapping timeframes
        List<Registration> pendingRegistrations = dataStore.getRegistrations().stream()
            .filter(reg -> reg.getOfficer().equals(officer) && reg.getStatus() == RegistrationStatus.PENDING)
            .toList();

        for (Registration pendingReg : pendingRegistrations) {
            if (projectToRegister.overlapsWith(pendingReg.getProject())) {
                System.out.println("Error: Cannot register for project '" + projectToRegister.getName() + 
                                   "' because its timeframe overlaps with your pending registration for project '" + 
                                   pendingReg.getProject().getName() + "'.");
                return null;
            }
        }

        // Check officer slots (is it already full even before approval?)
        long approvedCount = listForProject(projectToRegister.getId()).stream()
                            .filter(r -> r.getStatus() == RegistrationStatus.APPROVED)
                            .count();
        if (approvedCount >= projectToRegister.getOfficerSlotLimit()) {
             System.out.println("Error: Project officer slots are already full (" + projectToRegister.getOfficerSlotLimit() + "). Cannot submit registration.");
             return null;
        }

        Registration newReg = new Registration(officer, projectToRegister); // Use constructor that generates ID
        dataStore.getRegistrations().add(newReg);
        officer.addRegistration(newReg); // Link back to officer
        System.out.println("Registration submitted for Project '" + projectToRegister.getName() + "'. Awaiting manager approval.");
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

    /** Change registration status (approve/reject/withdraw) - Typically Manager initiated */
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
                    // Check officer slots again at time of approval
                    long approvedCount = listForProject(project.getId()).stream()
                            .filter(r -> r.getStatus() == RegistrationStatus.APPROVED)
                            .count();
                    if (approvedCount >= project.getOfficerSlotLimit()) {
                        System.out.println("Error: Project officer slots are full (" + project.getOfficerSlotLimit() + "). Cannot approve.");
                        yield false;
                    }

                    // Check for overlapping APPROVED assignments for the same officer
                    List<Registration> approvedRegistrations = dataStore.getRegistrations().stream()
                        .filter(r -> r.getOfficer().equals(officer) && r.getStatus() == RegistrationStatus.APPROVED)
                        .toList();
                    for (Registration approvedReg : approvedRegistrations) {
                        if (project.overlapsWith(approvedReg.getProject())) {
                            System.out.println("Error: Cannot approve registration for project '" + project.getName() + 
                                               "' because its timeframe overlaps with an existing approved assignment for this officer on project '" + 
                                               approvedReg.getProject().getName() + "'.");
                            yield false;
                        }
                    }

                    reg.approve();
                    project.addAssignedOfficer(officer); // Add officer to project's list
                    officer.addAssignedProject(project); // Add project to officer's list
                    System.out.println("Registration ID " + registrationId + " approved. Officer " + officer.getFirstName() + " assigned to Project " + project.getName());
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
                    yield true;
                } else {
                    System.out.println("Error: Can only reject PENDING registrations.");
                    yield false;
                }
            }
            case WITHDRAWN -> {
                 // This case is now primarily handled by withdrawRegistration for officers
                 // Manager might still force withdraw an APPROVED one?
                 if (currentStatus == RegistrationStatus.PENDING || currentStatus == RegistrationStatus.APPROVED) {
                    reg.withdraw(); // Sets status to WITHDRAWN
                    if (currentStatus == RegistrationStatus.APPROVED) {
                        project.removeAssignedOfficer(officer);
                        officer.removeAssignedProject(project);
                    }
                    System.out.println("Registration ID " + registrationId + " withdrawn by manager.");
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

    /**
     * Request withdrawal of a registration (pending manager approval)
     * For both PENDING and APPROVED registrations, set withdrawal request flag for manager review
     * @param registrationId The ID of the registration to request withdrawal for
     * @param requestingOfficer The officer requesting withdrawal
     * @return true if withdrawal request was successfully submitted
     */
    public boolean requestWithdrawalForApproval(int registrationId, HDB_Officer requestingOfficer) {
        Registration reg = findById(registrationId);
        if (reg == null || !reg.getOfficer().equals(requestingOfficer)) {
            System.out.println("Error: Registration not found or does not belong to this officer.");
            return false;
        }
        
        // For both PENDING and APPROVED registrations, set flag to request withdrawal
        if (reg.getStatus() == RegistrationStatus.PENDING || reg.getStatus() == RegistrationStatus.APPROVED) {
            // Check if already requested
            if (reg.isWithdrawalRequested()) {
                System.out.println("Withdrawal already requested for this registration.");
                return false;
            }
            
            reg.setWithdrawalRequested(true);
            System.out.println("Withdrawal request submitted for approval by project manager.");
            return true;
        }
        
        System.out.println("Error: Cannot request withdrawal for registration with status: " + reg.getStatus());
        return false;
    }
    
    /**
     * This method is ONLY for manager use to directly withdraw an officer registration
     * after reviewing and approving a withdrawal request
     */
    public boolean managerApproveWithdrawal(int registrationId) {
         Registration reg = findById(registrationId);
         if (reg == null) {
             System.out.println("Error: Registration not found.");
             return false;
         }
         
         if (!reg.isWithdrawalRequested()) {
             System.out.println("Error: No withdrawal requested for this registration.");
             return false;
         }

         // Set status to WITHDRAWN
         reg.withdraw();
         
         // If it was APPROVED, remove from project/officer assigned lists
         if (reg.getStatus() == RegistrationStatus.APPROVED) {
             Project project = reg.getProject();
             HDB_Officer officer = reg.getOfficer();
             project.removeAssignedOfficer(officer);
             officer.removeAssignedProject(project);
         }
         
         System.out.println("Registration ID " + registrationId + " withdrawal approved.");
         return true;
    }
    
    /**
     * This method is ONLY for manager use to reject a withdrawal request
     */
    public boolean managerRejectWithdrawal(int registrationId) {
         Registration reg = findById(registrationId);
         if (reg == null) {
             System.out.println("Error: Registration not found.");
             return false;
         }
         
         if (!reg.isWithdrawalRequested()) {
             System.out.println("Error: No withdrawal requested for this registration.");
             return false;
         }

         // Clear the withdrawal request flag
         reg.setWithdrawalRequested(false);
         
         System.out.println("Registration ID " + registrationId + " withdrawal request rejected.");
         return true;
    }
}
