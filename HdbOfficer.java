package project;

public class HdbOfficer extends User{
	private String officerName;
	private Project handlingProject;
	private String registrationStatus;
	
	public HdbOfficer(String name, Project project, String regiStatus)
	{
		this.officerName = name;
		this.handlingProject = project;
		this.registrationStatus = regiStatus;
	}
	
	public boolean registerForProject(Project project)
	{
		
	}
	
	public void viewProjectDetails(Project project)
	{
		System.out.println("=== PROJECT DETAILS ===");
	    System.out.println("Project ID: " + project.getProjectID());
	    System.out.println("Neighborhood: " + project.getNeighborhood());
	    System.out.println("Application Period: " + project.getApplicationOpenDate() + " to " + project.getApplicationCloseDate());
	    System.out.println("Available Flat Types:");
	    for (FlatType type : project.getFlatType()) 
	    {
	        int available = project.getUnitsAvailable().get(type);
	        System.out.println("- " + type + ": " + available + " units");
	    }
	    
	    System.out.println("Price per flat: $" + project.getPricePerFlat());
	    System.out.println("Threshold Price: $" + project.getThresholdPrice());
	    System.out.println("Available Officer Slots: " + project.getAvailableOfficerSlots());
	    System.out.println("Project Visibility: " + (project.isVisible() ? "Visible" : "Hidden"));
	    System.out.println("========================");
	}
	
	
	public enquiry viewEnquiry(Enquiry enquiry)
	{
		
	}
	
	public void processApplication(Application application)
	{
		
	}
	
	public boolean bookFlat(Applicant applicant, FlatType flatType)
	{
		
	}
	
	public receipt generateReceipt(Application application)
	{
		
	}
}
