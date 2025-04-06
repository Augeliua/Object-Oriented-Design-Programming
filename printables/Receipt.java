package project;

package project;

public class Receipt {
	private String receiptID;
	private Applicant applicantDetails;
	private Project projectDetails;
	private FlatType flatType;
	private String bookingDate;
	
	public Receipt(String id, Applicant a, Project p, FlatType f, String date)
	{
		this.receiptID = id;
		this.applicantDetails = a;
		this.projectDetails = p;
		this.flatType = f;
		this.bookingDate = date;
	}
	
	public String getReceiptID()
	{
		return receiptID;
	}
	
	public void setReceiptId(String receiptId) 
	{
        this.receiptID = receiptId;
    }
	
	public Applicant getApplicantDetails()
	{
		return applicantDetails;
	}
	
	public void setApplicantDetails(Applicant applicantDetails) 
	{
        this.applicantDetails = applicantDetails;
    }
	
	public Project getProjectDetails()
	{
		return projectDetails;
	}
	
	public void setProjectDetails(Project projectDetails)
	{
        this.projectDetails = projectDetails;
    }
	
	public FlatType getFlatType()
	{
		return flatType;
	}
	
	public void setFlatType(FlatType flatType) 
	{
        this.flatType = flatType;
	}
	
	public String getBookingDate()
	{
		return bookingDate;
	}
	
	public void setBookingDate(String bookingDate) 
	{
        this.bookingDate = bookingDate;
    }
	
	public void printReceiptDetails() {
		System.out.println("---------------");
	    System.out.println("Receipt Details");
	    System.out.println("---------------");
	    System.out.println("Receipt ID: " + receiptId);
	        
	    System.out.println("\nApplicant Information:");
	    System.out.println("Name: " + applicantDetails.getName());
	    System.out.println("NRIC: " + applicantDetails.getNRIC());
	    System.out.println("Age: " + applicantDetails.getAge());
	    System.out.println("Marital Status: " + applicantDetails.getMaritalStatus());
	        
	    System.out.println("\nProject Information:");
	    System.out.println("Project Name: " + projectDetails.getProjectName());
	    System.out.println("Neighborhood: " + projectDetails.getNeighborhood());
	        
	    System.out.println("\nBooking Information:");
	    System.out.println("Flat Type: " + flatType);
	    System.out.println("Booking Date: " + bookingDate);
	}
}
