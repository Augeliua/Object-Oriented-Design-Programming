package sc2002.bto.entity;

import java.util.ArrayList;
import java.util.List;

import sc2002.bto.enums.ApplicationStatus;
import sc2002.bto.enums.EnquiryStatus;
import sc2002.bto.enums.FlatType;
import sc2002.bto.enums.MaritalStatus;
import sc2002.bto.enums.OfficerRegistrationStatus;
import sc2002.bto.interfaces.IApplicationProcessing;
import sc2002.bto.interfaces.IEnquiryManagement;
import sc2002.bto.repository.ApplicationRepository;
import sc2002.bto.repository.EnquiryRepository;

public class HdbOfficer extends User implements IEnquiryManagement, IApplicationProcessing {
    private String officerName;
    private Project handlingProject;
    private OfficerRegistrationStatus registrationStatus;
    private ApplicationRepository applicationRepository;
    private EnquiryRepository enquiryRepository;
    
    public HdbOfficer(String id, String name, String password, int age, MaritalStatus maritalStatus, 
                      String officerName, Project project, OfficerRegistrationStatus regStatus,
                      ApplicationRepository appRepo, EnquiryRepository enqRepo) {
        super(id, name, password, age, maritalStatus);
        this.officerName = officerName;
        this.handlingProject = project;
        this.registrationStatus = regStatus;
        this.applicationRepository = appRepo;
        this.enquiryRepository = enqRepo;
    }
    
    public boolean registerForProject(Project project) {
        if (this.handlingProject != null) {
            System.out.println("Officer is currently enrolled in this project: " + handlingProject.getProjectID());
            return false;
        }
        
        if (project.getAvailableOfficerSlots() <= 0) {
            System.out.println("No available slots for officers in this project: " + project.getProjectID());
            return false;
        }
        
        this.handlingProject = project;
        this.registrationStatus = OfficerRegistrationStatus.PENDING;
        System.out.println("Currently registered for this project, awaiting approval: " + project.getProjectID());
        return true;
    }
    
    public void viewProjectDetails(Project project) {
        boolean isHandlingProject = (handlingProject != null && handlingProject.equals(project));
        
        if (!project.isVisible() && !isHandlingProject) {
            System.out.println("Project is not visible and you are not handling this project");
            return;
        }
        
        if (project != null) {
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
        } else {
            System.out.println("Project details are not available.");
        }
    }
    
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
        
        // Verify flat type matches what was applied for
        if (!application.getSelectedFlatType().equals(flatType)) {
            System.out.println("Error: Cannot book a different flat type than what was applied for");
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
        
        // Update application status
        application.updateStatus(ApplicationStatus.BOOKED);
        
        System.out.println("Flat successfully booked for applicant: " + applicant.getApplicantName());
        System.out.println("Flat type: " + flatType);
        System.out.println("Remaining units of this type: " + (availableUnits - 1));
        
        // Generate receipt
        Receipt receipt = generateReceipt(application);
        
        return true;
    }
    
    public Receipt generateReceipt(Application application) {
        if (application == null || application.getStatus() != ApplicationStatus.SUCCESSFUL) {
            System.out.println("Cannot generate receipt: Application is not SUCCESSFUL status");
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
        
        // Update application status to BOOKED
        application.updateStatus(ApplicationStatus.BOOKED);
        
        return receipt;
    }
    
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
        }
    }
    
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
    
    @Override
    public void updateApplicationStatus() {
        // This method might be used to update application statuses for all applications
        // handled by this officer, but it's more typically used on a per-application basis
        System.out.println("Bulk application status update not implemented.");
    }
    
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
            System.out.println("Generating receipt...");
            generateReceipt(application);
        }
    }
    
    public void viewAllEnquiries() {
        if (handlingProject == null) {
            System.out.println("Error: Officer is not assigned to any project");
            return;
        }
        
        List<Enquiry> enquiries = enquiryRepository.findByProject(handlingProject);
        
        if (enquiries.isEmpty()) {
            System.out.println("No enquiries found for project: " + handlingProject.getProjectID());
            return;
        }
        
        System.out.println("==== All Enquiries for Project: " + handlingProject.getProjectID() + " ====");
        for (Enquiry enquiry : enquiries) {
            System.out.println("ID: " + enquiry.getEnquiryId() + " | From: " + enquiry.getApplicant().getName() + " | Status: " + enquiry.getStatus());
        }
    }
    
    public void viewPendingEnquiries() {
        if (handlingProject == null) {
            System.out.println("Error: Officer is not assigned to any project");
            return;
        }
        
        List<Enquiry> allEnquiries = enquiryRepository.findByProject(handlingProject);
        
        List<Enquiry> pendingEnquiries = new ArrayList<>();
        
        for (Enquiry enquiry : allEnquiries) {
            if (enquiry.getStatus() == EnquiryStatus.PENDING) { 
                pendingEnquiries.add(enquiry);
            }
        }
        
        if (pendingEnquiries.isEmpty()) {
            System.out.println("No pending enquiries found for project: " + handlingProject.getProjectID());
            return;
        }
        
        System.out.println("==== Pending Enquiries for Project: " + handlingProject.getProjectID() + " ====");
        for (Enquiry enquiry : pendingEnquiries) {
            System.out.println("Enquiry ID: " + enquiry.getEnquiryId());
        }
    }
    
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
        
        e.displayEnquiryDetails();
    }
    
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
    
    // Getters and setters
    public String getOfficerName() {
        return officerName;
    }
    
    public void setOfficerName(String officerName) {
        this.officerName = officerName;
    }
    
    public Project getHandlingProject() {
        return handlingProject;
    }
    
    public void setHandlingProject(Project handlingProject) {
        this.handlingProject = handlingProject;
    }
    
    public OfficerRegistrationStatus getRegistrationStatus() {
        return registrationStatus;
    }
    
    public void setRegistrationStatus(OfficerRegistrationStatus registrationStatus) {
        this.registrationStatus = registrationStatus;
    }
}
