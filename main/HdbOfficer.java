package project;

public class HdbOfficer extends User implements EnquiryManagement, ApplicationProcessing{
	private String officerName;
	private Project handlingProject;
	private String registrationStatus;
	
	public HdbOfficer(String id, String name, String password, int age, boolean maritalStatus, String officerName, Project project, String regiStatus) 
	{
		super(id, name, password, age, maritalStatus);

		this.officerName = officerName;
		this.handlingProject = project;
		this.registrationStatus = regiStatus;
	}
	
	public boolean registerForProject(Project project)
	{
		if (this.handlingProject != null)
		{
			System.out.println("Officer is currently enrolled in this project: " + handlingProject.getProjectID());
			return false;
		}
		
		if (project.getAvailableOfficerSlots <= 0)
		{
			System.out.println("No available slots for officers in this project: " + handlingProject.getProjectID());
			return false;
		}
		
		this.handlingProject = project;
		this.registrationStatus = "PENDING";
		System.out.println("Currently registered for this project, awaiting approval: " + handlingProject.getProjectID());
		return true;
	}
	
	public void viewProjectDetails(Project project)
	{
		boolean isHandlingProject = (handlingProject != null && handlingProject.equals(project));
		
		if (project.isVisible == false && isHandlingProject == false)
		{
			System.out.println("Project is not visible and you are not handling this project");
			return;
		}
		
		if (project != null) 
		{
	        System.out.println("Project ID: " + project.getProjectID());
	        System.out.println("Neighborhood: " + project.getNeighborhood());
	        System.out.println("Available flat types:");
	        for (FlatType flatType : project.getFlatTypes()) 
	        {
	            System.out.println(" - " + flatType.getDescription());
	        }
	        System.out.println("Price per flat: $" + project.getPricePerFlat());
	        System.out.println("Threshold price: $" + project.getThresholdPrice());
	        System.out.println("Application period: " + project.getApplicationOpenDate() + " to " + project.getApplicationCloseDate());
	        System.out.println("Visibility: " + (project.isVisible() ? "Visible" : "Not visible"));
	        
	        System.out.println("Units available:");
            for (FlatType flatType : project.getFlatTypes()) 
            {
                Integer units = project.getUnitsAvailable().get(flatType);
                System.out.println(" - " + flatType.getDescription() + ": " + (units != null ? units : 0));
            }
            if (isHandlingProject) 
            {
                System.out.println("\nYou are currently handling this project.");
                System.out.println("Available officer slots: " + project.getAvailableOfficerSlots());
            }
        } 
		else 
		{
            System.out.println("Project details are not available.");
        }
	}
	
	
	public boolean bookFlat(Applicant applicant, FlatType flatType)
	{
		
	}
	
	public Receipt generateReceipt(Application application)
	{
		if (application == null || application.getStatus() != ApplicationStatus.SUCCESSFUL) 
		{
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
        receipt.setName(applicant.getApplicantName());
        receipt.setNRIC(applicant.getNRIC());
        receipt.setAge(applicant.getAge());
        receipt.setMaritalStatus(applicant.getMaritalStatus());
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
	
	public void processApplication(Application application)
	{
		
	}
	
	public boolean validateApplication(Application application)
	{
		if (application == null) 
		{
	        System.out.println("Error: Application cannot be null");
	        return false;
	    }
	    
	    Applicant applicant = application.getApplicant();
	    Project project = application.getProject();
	    FlatType selectedFlatType = application.getSelectedFlatType();
	   
	    if (applicant.isWithPartner() && applicant.getAge() < 21) 
	    {
	        System.out.println("Error: Married applicants must be at least 21 years old");
	        return false;
	    }
	    
	    if (!applicant.isWithPartner() && applicant.getAge() < 35) 
	    {
	        System.out.println("Error: Single applicants must be at least 35 years old");
	        return false;
	    }
	    
	    // Check flat type eligibility
	    if (!applicant.isWithPartner() && selectedFlatType != FlatType.TWO_ROOM) 
	    {
	        System.out.println("Error: Single applicants can only apply for 2-Room flats");
	        return false;
	    }
	    
	    // Check if project has available units of selected flat type
	    if (project.getUnitsAvailable().get(selectedFlatType) <= 0) 
	    {
	        System.out.println("Error: No available units of selected flat type");
	        return false;
	    }
	    
	    // Check if applicant has already applied for other projects
	    // This would require access to a repository or data store
	    // For now, assume we're checking application status
	    if (application.getStatus() != ApplicationStatus.PENDING) 
	    {
	        System.out.println("Error: Application status must be PENDING for validation");
	        return false;
	    }
	    
	    return true;
	}
	
	public void updateApplicationStatus(Application application, ApplicationStatus newStatus) 
	{
	    if (application == null) 
	    {
	        System.out.println("Error: Cannot update status of null application");
	        return;
	    }
	    
	    ApplicationStatus oldStatus = application.getStatus();
	    
	    // Update the application status
	    application.updateStatus(newStatus);
	    
	    System.out.println("Application status updated from " + oldStatus + " to " + newStatus);
	    
	    // Handle side effects based on new status
	    if (newStatus == ApplicationStatus.SUCCESSFUL) 
	    {
	        System.out.println("Applicant " + application.getApplicant().getApplicantName() + " is invited to book a flat.");
	    } 
	    else if (newStatus == ApplicationStatus.UNSUCCESSFUL) 
	    {
	        System.out.println("Applicant " + application.getApplicant().getApplicantName() + " may apply for another project.");
	    } 
	    else if (newStatus == ApplicationStatus.BOOKED) 
	    {
	        // Decrease available units for the flat type
	        Project project = application.getProject();
	        FlatType flatType = application.getSelectedFlatType();
	        
	        int availableUnits = project.getUnitsAvailable().get(flatType);
	        project.getUnitsAvailable().put(flatType, availableUnits - 1);
	        
	        System.out.println("Flat booked successfully. Remaining " + flatType + " units: " + (availableUnits - 1));
	    }
	}
	
	public void viewAllEnquiries()
	{
		
	}
	
	public void viewPendingEnquiries()
	{
		
	}
	
	public void respondToEnquiry(Enquiry e, String response)
	{
		
	}
	
	
}
