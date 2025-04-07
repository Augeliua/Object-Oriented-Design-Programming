package project;

public class HdbOfficer extends User implements IEnquiryManagement, IApplicationProcessing{
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
	
	public void processApplication(Application application)
	{
		
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
		
	}
	
	public void updateApplicationStatus(Application application)
	{
		
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
