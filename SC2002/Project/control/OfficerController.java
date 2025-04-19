package SC2002.Project.control;

import SC2002.Project.control.persistence.DataStore;
import SC2002.Project.entity.*;
import SC2002.Project.entity.enums.ApplicationStatus;
import SC2002.Project.entity.enums.RegistrationStatus;
import java.util.*;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Handles officer‑specific actions (flat booking, etc.).
 */
public class OfficerController implements StaffControllerInterface{
    private final DataStore dataStore = DataStore.getInstance();
    private final HDB_Officer officer;

    public OfficerController(HDB_Officer officer) {
        this.officer = officer;
    }

    public List<Project> getAssignedProjects() {
        return officer.getRegistrations().stream()
            .filter(r -> r.getStatus() == RegistrationStatus.APPROVED)
            .map(Registration::getProject)
            .collect(Collectors.toList());
    }

    //enquiries that they can respond to, same as for manager
    public List<Enquiry> getPendingEnquiries(EnquiryController enquiryCtrl) {
        List<Enquiry> relevantEnquiries = new ArrayList<>();
        for (Project p : officer.getAssignedProjects()) {
            relevantEnquiries.addAll(enquiryCtrl.getProjectEnquiries(p));
        }
        // Get general enquiries (not project-specific)
        relevantEnquiries.addAll(enquiryCtrl.getGeneralEnquiries());
    
        // Filter out enquiries created by the officer themselves and already answered ones
        List<Enquiry> actionableEnquiries = relevantEnquiries.stream()
                .filter(e -> !e.getCreator().equals(this.officer))
                .filter(e -> !e.isAnswered())
                .distinct() // Avoid duplicates if an enquiry somehow appears twice
                .collect(Collectors.toList());
    
        if (actionableEnquiries.isEmpty()) {
            System.out.println("No pending enquiries found for your assigned projects or general topics.");
            
        }
        return relevantEnquiries;
    }

    public List<BTOApplication> getSuccessfulApplicationsForManagedProjects() {
        List<Project> assigned = getAssignedProjects();
        return dataStore.getApplications().stream()
            .filter(a -> a.getStatus() == ApplicationStatus.SUCCESS)
            .filter(a -> assigned.contains(a.getProject()))
            .collect(Collectors.toList());
    }

    public Optional<BTOApplication> findBookableApplicationById(int id) {
        return getSuccessfulApplicationsForManagedProjects().stream()
            .filter(a -> a.getId() == id)
            .findFirst();
    }

    public Receipt processFlatBooking(BTOApplication app) {
        Optional<Flat> opt = dataStore.getFlats().stream()
            .filter(f -> f.getProject().equals(app.getProject()))
            .filter(f -> f.getFlatType().equalsIgnoreCase(app.getRoomType()))
            .filter(f -> !f.isBooked())
            .findFirst();
        if (opt.isEmpty()) return null;
        Flat flat = opt.get();
        app.bookFlat(flat);
        return new Receipt(app, flat);
    }
}
