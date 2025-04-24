package sc2002.bto.entity;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import sc2002.bto.enums.ApplicationStatus;
import sc2002.bto.enums.EnquiryStatus;
import sc2002.bto.enums.FlatType;
import sc2002.bto.enums.MaritalStatus;
import sc2002.bto.enums.OfficerRegistrationStatus;
import sc2002.bto.interfaces.IApplicationProcessing;
import sc2002.bto.interfaces.IEnquiryManagement;
import sc2002.bto.repository.ApplicationRepository;
import sc2002.bto.repository.EnquiryRepository;

/**
 * Represents an HDB Officer in the BTO Management System.
 * This class extends the User class and implements IEnquiryManagement and IApplicationProcessing interfaces.
 * HDB Officers assist in processing applications, booking flats, and handling enquiries.
 */
public class HdbOfficer extends User implements IEnquiryManagement, IApplicationProcessing {
    /** The name of the officer */
    private String officerName;
    /** The project currently handled by this officer */
    private Project handlingProject;
    /** The project for which the officer has a pending registration */
    private Project pendingProject; 
    /** The registration status of this officer */
    private OfficerRegistrationStatus registrationStatus;
    /** Repository for application data */
    private ApplicationRepository applicationRepository;
    /** Repository for enquiry data */
    private EnquiryRepository enquiryRepository;
    /** The manager who registered this officer */
    private HdbManager registeringManager;

    /**
     * Creates a new HDB Officer with the specified details.
     * 
     * @param id The officer's NRIC as a unique identifier
     * @param name The officer's name
     * @param password The officer's password
     * @param age The officer's age
     * @param maritalStatus The officer's marital status
     * @param officerName The officer's full name
     * @param pendingProject The project for which the officer has a pending registration
     * @param handlingProject The project currently handled by this officer
     * @param regStatus The registration status of this officer
     * @param registeringManager The manager who registered this officer
     * @param appRepo The application repository
     * @param enqRepo The enquiry repository
     */
    public HdbOfficer(String id, String name, String password, int age, MaritalStatus maritalStatus,
            String officerName, Project pendingProject, Project handlingProject,
            OfficerRegistrationStatus regStatus, HdbManager registeringManager, 
            ApplicationRepository appRepo, EnquiryRepository enqRepo) {
    	super(id, name, password, age, maritalStatus);
    	this.officerName = officerName;
    	this.pendingProject = pendingProject;
    	this.handlingProject = handlingProject;
    	this.registrationStatus = regStatus;
    	this.registeringManager = registeringManager;
    	this.applicationRepository = appRepo;
    	this.enquiryRepository = enqRepo;
    }
    
    /**
     * Registers the officer to handle a project.
     * 
     * @param project The project to register for
     * @return true if registration was successful, false otherwise
     */
    public boolean registerForProject(Project project) {
        if (this.handlingProject != null) {
            System.out.println("Officer is already assigned to a project: " + handlingProject.getProjectName());
            return false;
        }

        if (this.pendingProject != null) {
            System.out.println("You have already requested to join a project (" 
                               + pendingProject.getProjectName() + "). Awaiting manager approval.");
            return false;
        }

        if (project.getAvailableOfficerSlots() <= 0) {
            System.out.println("No available slots in project: " + project.getProjectName());
            return false;
        }

        this.pendingProject = project;
        this.registrationStatus = OfficerRegistrationStatus.PENDING;
        return true;
    }

    /**
     * Views the details of a project.
     * 
     * @param project The project to view
     */
    public void viewProjectDetails(Project project) {
        if (project == null) {
            System.out.println("Project details are not available.");
            return;
        }
    
        boolean isHandlingProject = (handlingProject != null && handlingProject.equals(project));
    
        if (!project.isVisible() && (handlingProject == null || !handlingProject.getProjectID().equals(project.getProjectID()))) {
            System.out.println("Project is not visible and you are not handling this project");
            return;
        }        
    
        System.out.println("Project ID: " + project.getProjectID());
        System.out.println("Neighborhood: " + project.getNeighborhood());
        System.out.println("Available flat types:");
        for (FlatType flatType : project.getFlatType()) {
            System.out.println(" - " + flatType);
        }
        System.out.println("Price per flat: $" + project.getPricePerFlat());
        System.out.println("Threshold price: $" + project.getThresholdPrice());
        System.out.println("Application period: " + project.getApplicationOpenDate() + " to " + project.getApplicationCloseDate());
        System.out.println("Visibility: " + (project.isVisible() ? "Visible" : "Not visible"));
    
        System.out.println("Units available:");
        System.out.println(" - TWO_ROOM: " + project.getTwoRoomUnitsAvailable());
        System.out.println(" - THREE_ROOM: " + project.getThreeRoomUnitsAvailable());
    
        if (isHandlingProject) {
            System.out.println("\nYou are currently handling this project.");
            System.out.println("Available officer slots: " + project.getAvailableOfficerSlots());
        }
    }
    
    /**
     * Finds an application by the applicant and project.
     * 
     * @param applicant The applicant
     * @param project The project
     * @return The application if found, null otherwise
     */
    private Application findApplicationByApplicant(Applicant applicant, Project project) {
        if (applicant == null || project == null) {
            System.out.println("Error: Applicant and project cannot be null");
            return null;
        }
        
        // Use the application repository to find applications by this applicant
        List<Application> applicantApplications = applicationRepository.findByApplicant(applicant);
        
        // Filter to find the application for the specific project
        for (Application application : applicantApplications) {
            if (application.getProject().equals(project)) {
                return application;
            }
        }
        
        System.out.println("No application found for applicant " + applicant.getApplicantName() + 
                          " in project " + project.getProjectID());
        return null;
    }
    
    /**
     * Books a flat for an applicant.
     * 
     * @param applicant The applicant booking the flat
     * @param flatType The type of flat to book
     * @return true if booking was successful, false otherwise
     */
    public boolean bookFlat(Applicant applicant, FlatType flatType) {
        if (applicant == null || flatType == null) {
            System.out.println("Error: Applicant and flat type cannot be null");
            return false;
        }
        
        // Check if units are still available based on flat type
        int availableUnits = 0;
        
        if (flatType == FlatType.TWO_ROOM) {
            availableUnits = handlingProject.getTwoRoomUnitsAvailable();
        } else if (flatType == FlatType.THREE_ROOM) {
            availableUnits = handlingProject.getThreeRoomUnitsAvailable();
        } else {
            System.out.println("Error: Unsupported flat type: " + flatType);
            return false;
        }
        
        if (availableUnits <= 0) {
            System.out.println("Error: No units available for flat type: " + flatType);
            return false;
        }
        
        // Find applicant's application for the project
        Application application = findApplicationByApplicant(applicant, handlingProject);
        
        if (application == null) {
            System.out.println("Error: No application found for this applicant and project");
            return false;
        }
        
        // Debug: Print out both flat types to see if there's a mismatch
        System.out.println("Debug - Application flat type: " + application.getSelectedFlatType());
        System.out.println("Debug - Requested booking flat type: " + flatType);
        
        // Verify flat type matches what was applied for
        if (!application.getSelectedFlatType().equals(flatType)) {
            System.out.println("Error: Cannot book a different flat type than what was applied for");
            return false;
        }
        
        // Verify application status is SUCCESSFUL before booking
        if (application.getStatus() != ApplicationStatus.SUCCESSFUL) {
            System.out.println("Error: Application must be SUCCESSFUL before booking");
            return false;
        }
        
        // Update number of units available
        if (flatType == FlatType.TWO_ROOM) {
            handlingProject.setTwoRoomUnitsAvailable(availableUnits - 1);
        } else if (flatType == FlatType.THREE_ROOM) {
            handlingProject.setThreeRoomUnitsAvailable(availableUnits - 1);
        }
        
        // Update applicant's profile
        applicant.setBookedFlat(flatType);
        applicant.setBookedProject(handlingProject);
        
        // Update application status to BOOKED
        application.updateStatus(ApplicationStatus.BOOKED);
        
        System.out.println("Flat successfully booked for applicant: " + applicant.getApplicantName());
        System.out.println("Flat type: " + flatType);
        System.out.println("Remaining units of this type: " + (availableUnits - 1));
        
        return true;
    }
    
    /**
     * Saves a receipt to the ReceiptList.csv file.
     * Creates the file with a header if it doesn't exist and appends the new receipt.
     * 
     * @param receipt The Receipt object to be saved to the file
     */
    private void saveReceiptToFile(Receipt receipt) {
        try {
            File receiptFile = new File("data/ReceiptList.csv");
            
            // Check if file exists, if not create it with header
            boolean fileExists = receiptFile.exists() && receiptFile.length() > 0;
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(receiptFile, true))) {
                // Write header if file is new
                if (!fileExists) {
                    writer.write("ReceiptID,Name,NRIC,Age,MaritalStatus,ProjectID,Neighborhood,Price,FlatType,BookingDate\n");
                }
                
                // Escape CSV values that might contain commas
                String escapedName = escapeCSV(receipt.getName());
                String escapedNRIC = escapeCSV(receipt.getNRIC());
                String escapedNeighborhood = escapeCSV(receipt.getNeighborhood());
                
                // Append the new receipt
                writer.write(
                    receipt.getReceiptID() + "," +
                    escapedName + "," +
                    escapedNRIC + "," +
                    receipt.getAge() + "," +
                    escapeCSV(receipt.getMaritalStatus()) + "," +
                    escapeCSV(receipt.getProjectID()) + "," +
                    escapedNeighborhood + "," +
                    receipt.getPricePerFlat() + "," +
                    receipt.getFlatType() + "," +
                    escapeCSV(receipt.getBookingDate()) + "\n"
                );
            }
        } catch (IOException e) {
            System.err.println("Error saving receipt to file: " + e.getMessage());
        }
    }

    /**
     * Escapes special characters in CSV fields to prevent parsing issues.
     * Wraps the value in quotes if it contains commas, quotes, or newlines.
     * 
     * @param value The string to be escaped
     * @return The CSV-safe escaped string
     */
    private String escapeCSV(String value) {
        if (value == null) return "";
        
        // Check if value needs escaping
        if (value.contains(",") || value.contains("\"") || value.contains("\n")) {
            // Replace existing quotes with double quotes
            value = value.replace("\"", "\"\"");
            // Wrap in quotes
            return "\"" + value + "\"";
        }
        
        return value;
    }

     /**
     * Generates a receipt for a successful or booked application.
     * Creates a receipt with applicant and project details, 
     * prints the receipt, saves it to a file, and updates the application status.
     * 
     * @param application The application for which to generate a receipt
     * @return The generated Receipt object, or null if receipt generation fails
     */
    public Receipt generateReceipt(Application application) {
        // Validate application status
        if (application == null || 
            !(application.getStatus() == ApplicationStatus.SUCCESSFUL || 
              application.getStatus() == ApplicationStatus.BOOKED)) {
            System.out.println("Cannot generate receipt: Application must be SUCCESSFUL or BOOKED");
            return null;
        }
        
        Applicant applicant = application.getApplicant();
        Project project = application.getProject();
        FlatType flatType = application.getSelectedFlatType();
        
        // Use project's opening date as booking date
        String bookingDate = project.getApplicationOpenDate();
        
        // Create a new receipt with all the required details
        Receipt receipt = new Receipt();
        receipt.setName(applicant.getName());
        receipt.setNRIC(applicant.getId()); // Using ID as NRIC
        receipt.setAge(applicant.getAge());
        receipt.setMaritalStatus(applicant.getMaritalStatus().toString());
        receipt.setProjectID(project.getProjectID());
        receipt.setNeighborhood(project.getNeighborhood());
        receipt.setPricePerFlat(project.getPricePerFlat());
        receipt.setFlatType(flatType);
        receipt.setBookingDate(bookingDate);
        
        // Print receipt details
        receipt.printReceiptDetails();
        
        // Save receipt to file
        saveReceiptToFile(receipt);
        
        // Update application status to BOOKED
        application.updateStatus(ApplicationStatus.BOOKED);
        
        return receipt;
    }
    
    /**
     * Processes an application based on its current status.
     * Verifies that the officer is handling the project associated with the application.
     * Handles applications differently based on their status (PENDING, SUCCESSFUL, UNSUCCESSFUL, or BOOKED).
     * 
     * @param application The application to process
     */
    @Override
    public void processApplication(Application application) {
        if (application == null) {
            System.out.println("Error: Cannot process null application");
            return;
        }
        
        // Verify that the officer is handling this project
        if (handlingProject == null || !handlingProject.equals(application.getProject())) {
            System.out.println("Error: Officer can only process applications for projects they handle");
            return;
        }
        
        // Process based on current application status
        switch (application.getStatus()) {
            case PENDING: 
                // Officer doesn't handle pending applications directly
                System.out.println("This application is still PENDING and awaiting manager review.");
                System.out.println("No action can be taken at this time.");
                break;
            
                
            case SUCCESSFUL:
                // Process booking for approved application
                Applicant applicant = application.getApplicant();
                FlatType flatType = application.getSelectedFlatType();
                
                // Debug: Print the selected flat type before booking
                System.out.println("Debug - Processing application with flat type: " + flatType);
                
                // Use bookFlat to handle the booking process
                boolean bookingSuccess = bookFlat(applicant, flatType);
                
                if (bookingSuccess) {
                    System.out.println("Application processed: Flat successfully booked");
                } else {
                    System.out.println("Application processed: Booking failed");
                }
    
                break;
                
            case UNSUCCESSFUL:
                System.out.println("This application has been marked as UNSUCCESSFUL by the manager.");
                System.out.println("The applicant needs to submit a new application to continue.");
                break;
                
            case BOOKED:
                System.out.println("This application has already been processed and the flat is BOOKED.");
                System.out.println("No further processing is required.");
                break;
                
            default:
                System.out.println("Unknown application status: " + application.getStatus());
                System.out.println("No action can be taken.");
                break;
        }
    }
    
    /**
     * Validates an application against eligibility criteria.
     * Checks age requirements, marital status restrictions, flat type eligibility,
     * and availability of units.
     * 
     * @param application The application to validate
     */
    @Override
    public void validateApplication(Application application) {
        if (application == null) {
            System.out.println("Error: Application cannot be null");
            return;
        }
        
        Applicant applicant = application.getApplicant();
        Project project = application.getProject();
        FlatType selectedFlatType = application.getSelectedFlatType();
        
        // Check if applicant meets age requirements
        if (applicant.getMaritalStatus() == MaritalStatus.MARRIED && applicant.getAge() < 21) {
            System.out.println("Error: Married applicants must be at least 21 years old");
            return;
        }
        
        if (applicant.getMaritalStatus() == MaritalStatus.SINGLE && applicant.getAge() < 35) {
            System.out.println("Error: Single applicants must be at least 35 years old");
            return;
        }
        
        // Check flat type eligibility
        if (applicant.getMaritalStatus() == MaritalStatus.SINGLE && selectedFlatType != FlatType.TWO_ROOM) {
            System.out.println("Error: Single applicants can only apply for 2-Room flats");
            return;
        }
        
        // Check if project has available units of selected flat type
        int availableUnits = 0;
        
        if (selectedFlatType == FlatType.TWO_ROOM) {
            availableUnits = project.getTwoRoomUnitsAvailable();
        } else if (selectedFlatType == FlatType.THREE_ROOM) {
            availableUnits = project.getThreeRoomUnitsAvailable();
        }
        
        if (availableUnits <= 0) {
            System.out.println("Error: No available units of selected flat type");
            return;
        }
        
        System.out.println("Application is valid and ready for processing.");
    }
    
    /**
     * Updates the status of a batch of applications.
     * Currently just a placeholder method, actual implementation is done on a per-application basis.
     */
    @Override
    public void updateApplicationStatus() {
        // This method might be used to update application statuses for all applications
        // handled by this officer, but it's more typically used on a per-application basis
        System.out.println("Bulk application status update not implemented.");
    }
    
    /**
     * Updates the status of an application.
     * 
     * @param application The application to update.
     * @param newStatus The new status to set.
     */
    public void updateApplicationStatus(Application application, ApplicationStatus newStatus) {
        if (application == null) {
            System.out.println("Error: Cannot update status of null application");
            return;
        }
        
        ApplicationStatus oldStatus = application.getStatus();
        
        // Update the application status
        application.updateStatus(newStatus);
        
        System.out.println("Application status updated from " + oldStatus + " to " + newStatus);
        
        // Notify based on new status
        if (newStatus == ApplicationStatus.BOOKED) {
            System.out.println("Booking confirmed for applicant: " + application.getApplicant().getName());
        }
    }
    
    /**
     * Gets all enquiries for the project handled by this officer.
     * 
     * @return A list of all enquiries for the handled project
     */
    public List<Enquiry> getAllEnquiries() {
        if (handlingProject == null) {
            return new ArrayList<>();
        }
        
        return enquiryRepository.findByProject(handlingProject);
    }
    
    /**
     * Gets pending enquiries for the project handled by this officer.
     * 
     * @return A list of pending enquiries for the handled project
     */
    public List<Enquiry> getPendingEnquiries() {
        if (handlingProject == null) {
            return new ArrayList<>();
        }
        
        List<Enquiry> allEnquiries = enquiryRepository.findByProject(handlingProject);
        
        return allEnquiries.stream()
                .filter(e -> e.getStatus() == EnquiryStatus.PENDING)
                .collect(Collectors.toList());
    }
    
    /**
     * Handles an enquiry about a project.
     * Verifies that the officer is handling the project related to the enquiry.
     * 
     * @param e The enquiry to handle
     */
    @Override
    public void handleEnquiry(Enquiry e) {
        if (e == null) {
            System.out.println("Error: Cannot view null enquiry");
            return;
        }
        
        // Check if officer is handling the project related to this enquiry
        if (handlingProject == null || !handlingProject.equals(e.getProject())) {
            System.out.println("Error: Officer can only view enquiries for projects they handle");
            return;
        }
        
        e.printEnquiryDetails();
    }
    
    /**
     * Responds to an enquiry about a project.
     * Verifies that the officer is handling the project related to the enquiry before responding.
     * Updates the enquiry status to REPLIED after adding the response.
     * 
     * @param e The enquiry to respond to
     * @param response The response message to add to the enquiry
     */
    @Override
    public void respondToEnquiry(Enquiry e, String response) {
        if (e == null) {
            System.out.println("Error: Cannot respond to null enquiry");
            return;
        }
        
        // Check if officer is handling the project related to this enquiry
        if (handlingProject == null || !handlingProject.equals(e.getProject())) {
            System.out.println("Error: Officer can only respond to enquiries for projects they handle");
            return;
        }
        
        if (response == null || response.trim().isEmpty()) {
            System.out.println("Error: Response cannot be empty");
            return;
        }
        
        // Call the reply method
        e.reply(response);
        
        System.out.println("Response added successfully to enquiry ID: " + e.getEnquiryId());
        System.out.println("Enquiry status updated to: " + e.getStatus());
    }
    
    /**
     * Gets detailed information about a project.
     * 
     * @param project The project to get details for
     * @return A map containing the project details
     */
    public Map<String, Object> getProjectDetails(Project project) {
        Map<String, Object> details = new HashMap<>();
        
        if (project != null) {
            details.put("projectId", project.getProjectID());
            details.put("name", project.getProjectName());
            details.put("neighborhood", project.getNeighborhood());
            details.put("flatTypes", project.getFlatType());
            details.put("pricePerFlat", project.getPricePerFlat());
            details.put("thresholdPrice", project.getThresholdPrice());
            details.put("applicationOpenDate", project.getApplicationOpenDate());
            details.put("applicationCloseDate", project.getApplicationCloseDate());
            details.put("visible", project.isVisible());
            details.put("twoRoomUnits", project.getTwoRoomUnitsAvailable());
            details.put("threeRoomUnits", project.getThreeRoomUnitsAvailable());
            
            // Add officer-specific info
            boolean isHandlingProject = (handlingProject != null && handlingProject.equals(project));
            details.put("isHandlingProject", isHandlingProject);
            
            if (isHandlingProject) {
                details.put("availableOfficerSlots", project.getAvailableOfficerSlots());
            }
        }
        return details;
    }
    
    // Getters and setters
    /**
     * Gets the name of this officer.
     * @return The officer's name.
     */
    public String getOfficerName() {
        return officerName;
    }
    
    /**
     * Sets the name of this officer.
     * @param officerName The new name for this officer.
     */
    public void setOfficerName(String officerName) {
        this.officerName = officerName;
    }
    
    /**
     * Gets the project this officer is currently handling.
     * @return The project being handled, or null if none.
     */
    public Project getHandlingProject() {
        return handlingProject;
    }
    
    /**
     * Sets the project this officer is handling.
     * @param handlingProject The project to handle.
     */
    public void setHandlingProject(Project handlingProject) {
        this.handlingProject = handlingProject;
    }
    
    /**
     * Gets the project this officer has requested to handle.
     * @return The pending project, or null if none.
     */
    public Project getPendingProject() {
        return pendingProject;
    }
    
    /**
     * Sets the project this officer is requesting to handle.
     * @param project The project to request.
     */
    public void setPendingProject(Project project) {
        this.pendingProject = project;
    }
    
    /**
     * Gets the registration status of this officer.
     * @return The current OfficerRegistrationStatus.
     */
    public OfficerRegistrationStatus getRegistrationStatus() {
        return registrationStatus;
    }
    
    /**
     * Sets the registration status of this officer.
     * @param registrationStatus The new registration status.
     */
    public void setRegistrationStatus(OfficerRegistrationStatus registrationStatus) {
        this.registrationStatus = registrationStatus;
    }
    
    /**
     * Gets the manager who registered this officer.
     * @return The registering manager, or null if none.
     */
    public HdbManager getRegisteringManager() {
        return registeringManager;
    }
    
    /**
     * Sets the manager who registered this officer.
     * @param registeringManager The registering manager.
     */
    public void setRegisteringManager(HdbManager registeringManager) {
        this.registeringManager = registeringManager;
    }
    
    /**
     * Displays all enquiries for the project handled by this officer.
     */
    public void viewAllEnquiries() {
        List<Enquiry> enquiries = enquiryRepository.findByProject(handlingProject);
        if (enquiries.isEmpty()) {
            System.out.println("No enquiries found.");
        } else {
            for (Enquiry e : enquiries) {
                System.out.println("Enquiry ID: " + e.getEnquiryId());
                System.out.println("Applicant: " + e.getApplicant().getApplicantName());
                System.out.println("Message: " + e.getMessage());
                System.out.println("Status: " + e.getStatus());
                System.out.println("---------------------");
            }
        }
    }
    
}
