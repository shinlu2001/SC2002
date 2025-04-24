package SC2002.Project.control;

import SC2002.Project.boundary.StaffControllerInterface;
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
public class OfficerController implements StaffControllerInterface {
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

    // enquiries that they can respond to, same as for manager
    public List<Enquiry> getPendingEnquiries(EnquiryController enquiryCtrl) {
        List<Enquiry> relevantEnquiries = new ArrayList<>();
        for (Project p : officer.getAssignedProjects()) {
            relevantEnquiries.addAll(enquiryCtrl.getProjectEnquiries(p));
        }
        // Get general enquiries (not project-specific)
        relevantEnquiries.addAll(enquiryCtrl.getGeneralEnquiries());

        // Filter out enquiries created by the officer themselves and already answered
        // ones
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

    /**
     * Determines if an application is ready for booking
     * 
     * @param app The application to check
     * @return true if the application can be booked
     */
    public boolean isApplicationBookable(BTOApplication app) {
        // An application is bookable if:
        // 1. It has been approved (SUCCESS status)
        // 2. It hasn't been booked yet
        // 3. It belongs to a project this officer is assigned to

        if (app == null || app.getStatus() != ApplicationStatus.SUCCESS) {
            return false;
        }

        // Check if the officer is assigned to this project
        boolean isAssignedToProject = getAssignedProjects().contains(app.getProject());
        if (!isAssignedToProject) {
            return false;
        }

        // Check if there are available flats of the requested type
        Optional<Flat> availableFlat = dataStore.getFlats().stream()
                .filter(f -> f.getProject().equals(app.getProject()))
                .filter(f -> f.getFlatType().equalsIgnoreCase(app.getRoomType()))
                .filter(f -> !f.isBooked())
                .findFirst();

        return availableFlat.isPresent();
    }

    /**
     * Process flat booking for an approved application.
     * Since the manager already decreased the unit count during application approval,
     * this just needs to mark the flat as booked.
     * 
     * @param app The application to book a flat for
     * @return A receipt for the booking, or null if booking failed
     */
    public Receipt processFlatBooking(BTOApplication app) {
        if (app == null) {
            System.err.println("Error: No application provided for booking");
            return null;
        }
        
        if (app.getStatus() != ApplicationStatus.SUCCESS) {
            System.err.println("Error: Application must be in SUCCESS status to book a flat, current status: " + app.getStatus());
            return null;
        }
        
        // Find an available flat
        Optional<Flat> opt = dataStore.getFlats().stream()
                .filter(f -> f.getProject().equals(app.getProject()))
                .filter(f -> f.getFlatType().equalsIgnoreCase(app.getRoomType()))
                .filter(f -> !f.isBooked())
                .findFirst();

        if (opt.isEmpty()) {
            System.err.println("Error: No available flats of type " + app.getRoomType() + 
                " found in project " + app.getProject().getName());
            return null;
        }

        Flat flat = opt.get();

        // Book the flat - no decrement needed as manager already did this during approval
        app.bookFlat(flat);
        
        // Verify that booking was successful
        if (app.getStatus() != ApplicationStatus.BOOKED || app.getBookedFlat() == null) {
            System.err.println("Error: Booking attempt failed. Status=" + app.getStatus());
            return null;
        }

        // Generate receipt
        try {
            return new Receipt(app, flat);
        } catch (Exception e) {
            System.err.println("Error generating receipt: " + e.getMessage());
            return null;
        }
    }
}
