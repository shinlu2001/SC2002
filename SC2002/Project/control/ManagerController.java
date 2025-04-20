package SC2002.Project.control;

import SC2002.Project.boundary.StaffControllerInterface;
import SC2002.Project.control.persistence.DataStore;
import SC2002.Project.entity.BTOApplication;
import SC2002.Project.entity.Enquiry;
import SC2002.Project.entity.HDB_Manager;
import SC2002.Project.entity.Project;
import SC2002.Project.entity.*;
import SC2002.Project.entity.enums.ApplicationStatus;
import SC2002.Project.entity.enums.RegistrationStatus;
import SC2002.Project.entity.enums.Visibility;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Controller for manager-specific operations: approving/rejecting applications,
 * handling withdrawals, managing visibility of managed projects, and handling officer registrations.
 */
public class ManagerController implements StaffControllerInterface {
    private final DataStore dataStore = DataStore.getInstance();
    private final ApplicationController appController = new ApplicationController();
    private final ProjectController projectController = new ProjectController();
    public final RegistrationController registrationController = new RegistrationController();
    private final HDB_Manager manager;

    public ManagerController(HDB_Manager manager) {
        this.manager = manager;
    }

    public HDB_Manager getManager() {
        return this.manager;
    }

    // ---- Approve / Reject BTO Applications ----

    /**
     * Lists all pending applications for projects managed by this manager.
     */
    public List<BTOApplication> getPendingApplications() {
        return manager.getManagedProjects().stream()
            .flatMap(proj -> appController.listApplicationsForProject(proj.getId()).stream())
            .filter(app -> app.getStatus() == ApplicationStatus.PENDING)
            .collect(Collectors.toList());
    }

    /**
     * Finds an application by ID, ensuring it's for a project this manager manages.
     * Use this generic version for looking up applications of any status.
     */
    public BTOApplication findManagedApplicationById(int appId) {
        BTOApplication app = appController.findById(appId);
        if (app != null && manager.getManagedProjects().contains(app.getProject())) {
            return app;
        }
        return null;
    }

    public boolean approveApplication(BTOApplication app) {
        if (app == null
         || app.getStatus() != ApplicationStatus.PENDING
         || !manager.getManagedProjects().contains(app.getProject())) {
            return false;
        }
        return appController.changeStatus(app.getId(), ApplicationStatus.SUCCESS);
    }

    public boolean rejectApplication(BTOApplication app) {
        if (app == null
         || app.getStatus() != ApplicationStatus.PENDING
         || !manager.getManagedProjects().contains(app.getProject())) {
            return false;
        }
        boolean result = appController.changeStatus(app.getId(), ApplicationStatus.REJECTED);
        if (result) {
            app.getApplicant().clearCurrentApplicationReference();
        }
        return result;
    }

    // ---- Handle Withdrawal Requests ----

    /**
     * Lists all withdrawal requests for projects managed by this manager.
     */
    public List<BTOApplication> getWithdrawalRequests() {
        return manager.getManagedProjects().stream()
            .flatMap(proj -> appController.listApplicationsForProject(proj.getId()).stream())
            .filter(BTOApplication::isWithdrawalRequested)
            .collect(Collectors.toList());
    }

    public boolean confirmWithdrawal(BTOApplication app) {
        if (app == null
         || !app.isWithdrawalRequested()
         || !manager.getManagedProjects().contains(app.getProject())
         || app.getStatus() == ApplicationStatus.BOOKED) {
            return false;
        }
        boolean ok = appController.changeStatus(app.getId(), ApplicationStatus.WITHDRAWN);
        if (ok) {
            app.getApplicant().finalizeWithdrawal();
        }
        return ok;
    }

    public boolean rejectWithdrawalRequest(BTOApplication app) {
        if (app == null
         || !app.isWithdrawalRequested()
         || !manager.getManagedProjects().contains(app.getProject())) {
            return false;
        }
        app.setWithdrawalRequested(false);
        return true;
    }

    // ---- Project Management ----

    /** Create a new project */
    public Project createProject(String name, String neighbourhood, List<String> flatTypes,
                                 List<Integer> totalUnits, List<Double> prices, LocalDate open,
                                 LocalDate close, boolean visible, int officerSlots) {
        // Delegate creation to ProjectController, passing the current manager
        return projectController.createProject(name, neighbourhood, flatTypes, totalUnits, prices,
                                               open, close, visible, officerSlots, this.manager);
    }

    /** Edit an existing project - delegates checks and updates to ProjectController */
    public boolean renameProject(int projectId, String newName) {
        if (!isManagerOfProject(projectId)) return false;
        return projectController.renameProject(projectId, newName);
    }

    public boolean changeNeighbourhood(int projectId, String newHood) {
        if (!isManagerOfProject(projectId)) return false;
        return projectController.changeNeighbourhood(projectId, newHood);
    }

    public boolean updateFlatTypeUnits(int projectId, String flatType, int newUnits) {
        if (!isManagerOfProject(projectId)) return false;
        return projectController.updateFlatTypeUnits(projectId, flatType, newUnits);
    }

    public boolean addFlatType(int projectId, String flatType, int units, double price) {
        if (!isManagerOfProject(projectId)) return false;
        return projectController.addFlatType(projectId, flatType, units, price);
    }

    public boolean removeFlatType(int projectId, String flatType) {
        if (!isManagerOfProject(projectId)) return false;
        return projectController.removeFlatType(projectId, flatType);
    }

    public boolean updateFlatPrice(int projectId, String flatType, double newPrice) {
        if (!isManagerOfProject(projectId)) return false;
        return projectController.updateFlatPrice(projectId, flatType, newPrice);
    }

    public boolean setOpenDate(int projectId, LocalDate openDate) {
        if (!isManagerOfProject(projectId)) return false;
        return projectController.setOpenDate(projectId, openDate);
    }

    public boolean setCloseDate(int projectId, LocalDate closeDate) {
        if (!isManagerOfProject(projectId)) return false;
        return projectController.setCloseDate(projectId, closeDate);
    }

    public boolean setVisibility(int projectId, Visibility state) {
        if (!isManagerOfProject(projectId)) return false;
        return projectController.setVisibility(projectId, state);
    }

    public boolean setOfficerSlotLimit(int projectId, int newLimit) {
        if (!isManagerOfProject(projectId)) return false;
        return projectController.setOfficerSlotLimit(projectId, newLimit);
    }

    /** Delete a project managed by this manager */
    public boolean deleteProject(int projectId) {
        // Delegate deletion to ProjectController, passing the manager for validation
        return projectController.deleteProject(projectId, this.manager);
    }

    /** Find a project managed by this manager by its ID */
    public Project findManagedProjectById(int projectId) {
        Project p = projectController.findById(projectId);
        if (p != null && p.getManager().equals(this.manager)) {
            return p;
        }
        return null;
    }

    // ---- Project Visibility & Listing ----

    /** List all projects in the system */
    public List<Project> listAllProjects() {
        return projectController.listAll();
    }

    /** List projects managed by this manager */
    public List<Project> getAssignedProjects() {
        return manager.getManagedProjects();
    }

    /** Toggle visibility on/off for a managed project */
    public boolean toggleProjectVisibility(int projectId) {
        Project p = projectController.findById(projectId);
        if (p == null || !manager.getManagedProjects().contains(p)) return false;
        Visibility newState = (p.getVisibility() == Visibility.ON) ? Visibility.OFF : Visibility.ON;
        return projectController.setVisibility(projectId, newState);
    }

    /** Helper to check if the manager manages the project */
    private boolean isManagerOfProject(int projectId) {
        Project p = projectController.findById(projectId);
        return p != null && p.getManager().equals(this.manager);
    }

    // ---- Officer Registration Management ----

    /**
     * Lists all PENDING registration requests for projects managed by this manager.
     */
    public List<Registration> getPendingOfficerRegistrations() {
        return manager.getManagedProjects().stream()
            .flatMap(proj -> registrationController.listForProject(proj.getId()).stream())
            .filter(reg -> reg.getStatus() == RegistrationStatus.PENDING)
            .collect(Collectors.toList());
    }

    /**
     * Finds a PENDING registration by ID, ensuring it's for a project this manager manages.
     */
    public Registration findManagedPendingRegistrationById(int regId) {
        Registration reg = registrationController.findById(regId);
        if (reg != null && 
            reg.getStatus() == RegistrationStatus.PENDING &&
            manager.getManagedProjects().contains(reg.getProject())) {
            return reg;
        }
        return null;
    }

    /**
     * Approves a pending officer registration.
     * Delegates checks (slots, overlap) and status change to RegistrationController.
     * @param registration The registration to approve.
     * @return true if successfully approved, false otherwise.
     */
    public boolean approveOfficerRegistration(Registration registration) {
        if (registration == null || 
            registration.getStatus() != RegistrationStatus.PENDING ||
            !manager.getManagedProjects().contains(registration.getProject())) {
            System.out.println("Error: Registration not found, not pending, or not for a managed project.");
            return false;
        }
        // Delegate the actual approval logic (including overlap/slot checks) to RegistrationController
        return registrationController.changeStatus(registration.getId(), RegistrationStatus.APPROVED);
    }

    /**
     * Rejects a pending officer registration.
     * Delegates status change to RegistrationController.
     * @param registration The registration to reject.
     * @return true if successfully rejected, false otherwise.
     */
    public boolean rejectOfficerRegistration(Registration registration) {
        if (registration == null || 
            registration.getStatus() != RegistrationStatus.PENDING ||
            !manager.getManagedProjects().contains(registration.getProject())) {
            System.out.println("Error: Registration not found, not pending, or not for a managed project.");
            return false;
        }
        // Delegate the actual rejection logic to RegistrationController
        return registrationController.changeStatus(registration.getId(), RegistrationStatus.REJECTED);
    }

    /**
     * Lists all registrations with withdrawal requests for projects managed by this manager.
     */
    public List<Registration> getOfficerWithdrawalRequests() {
        return manager.getManagedProjects().stream()
            .flatMap(proj -> registrationController.listForProject(proj.getId()).stream())
            .filter(Registration::isWithdrawalRequested)
            .collect(Collectors.toList());
    }
    
    /**
     * Approve a registration withdrawal request.
     * @param registration The registration to approve withdrawal for.
     * @return true if successfully withdrawn, false otherwise.
     */
    public boolean approveRegistrationWithdrawal(Registration registration) {
        if (registration == null || 
            !registration.isWithdrawalRequested() ||
            !manager.getManagedProjects().contains(registration.getProject())) {
            System.out.println("Error: Registration not found, no withdrawal requested, or not for a managed project.");
            return false;
        }
        // Delegate the actual withdrawal logic to RegistrationController
        return registrationController.managerApproveWithdrawal(registration.getId());
    }
    
    /**
     * Reject a registration withdrawal request.
     * @param registration The registration to reject withdrawal for.
     * @return true if successfully rejected, false otherwise.
     */
    public boolean rejectRegistrationWithdrawal(Registration registration) {
        if (registration == null || 
            !registration.isWithdrawalRequested() ||
            !manager.getManagedProjects().contains(registration.getProject())) {
            System.out.println("Error: Registration not found, no withdrawal requested, or not for a managed project.");
            return false;
        }
        // Delegate the actual rejection logic to RegistrationController
        return registrationController.managerRejectWithdrawal(registration.getId());
    }

    // ---- Enquiry Management ----
    
    /**
     * Retrieves all enquiries in the system
     * @return List of all enquiries
     */
    public List<Enquiry> getAllEnquiries() {
        EnquiryController enquiryController = new EnquiryController();
        return enquiryController.getAllEnquiries();
    }
    
    /**
     * Retrieves all enquiries related to projects managed by this manager
     * @return List of enquiries for managed projects
     */
    public List<Enquiry> getManagedProjectEnquiries() {
        EnquiryController enquiryController = new EnquiryController();
        List<Enquiry> managedEnquiries = new ArrayList<>();
        
        // Get enquiries for each managed project
        for (Project project : manager.getManagedProjects()) {
            managedEnquiries.addAll(enquiryController.getProjectEnquiries(project));
        }
        
        return managedEnquiries;
    }

    /**
     * Responds to an enquiry with the given message
     * @param enquiryId The ID of the enquiry to respond to
     * @param response The response message
     * @return true if the response was successfully added, false otherwise
     */
    public boolean respondToEnquiry(int enquiryId, String response) {
        if (response == null || response.trim().isEmpty()) {
            return false;
        }
        
        EnquiryController enquiryController = new EnquiryController();
        Enquiry enquiry = enquiryController.findEnquiryById(enquiryId);
        
        if (enquiry == null) {
            return false;
        }
        
        // If it's a project-specific enquiry, verify the manager manages this project
        if (enquiry.getProject() != null && !manager.getManagedProjects().contains(enquiry.getProject())) {
            return false;
        }
        
        return enquiryController.respondToEnquiry(manager, enquiryId, response);
    }

    // …plus stubs for other features: handle officer regs, generate reports…
    // TODO: Implement methods for enquiry management (view all, handle project-specific)
    public List<Enquiry> getPendingEnquiries(EnquiryController enquiryCtrl) {
        List<Enquiry> relevantEnquiries = new ArrayList<>();
        for (Project p : manager.getManagedProjects()) {
            relevantEnquiries.addAll(enquiryCtrl.getProjectEnquiries(p));
        }
        // Get general enquiries (not project-specific)
        relevantEnquiries.addAll(enquiryCtrl.getGeneralEnquiries());
    
        // Filter out enquiries created by the officer themselves and already answered ones
        List<Enquiry> actionableEnquiries = relevantEnquiries.stream()
                .filter(e -> !e.getCreator().equals(this.manager))
                .filter(e -> !e.isAnswered())
                .distinct() // Avoid duplicates if an enquiry somehow appears twice
                .collect(Collectors.toList());
    
        if (actionableEnquiries.isEmpty()) {
            System.out.println("No pending enquiries found for your assigned projects or general topics.");
            
        }
        return relevantEnquiries;
    }
    
}
