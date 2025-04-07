package sc2002.group.proj;

public class Applicant extends User{
	private String applicantName;
	private Boolean withPartner;
	private Double incomeRange;
	private FlatType bookedFlat;
	private Project bookedProject;
}

public Applicant(String id, String name, String password, int age, MaritalStatus /*enum name*/ maritalStatus, String applicantName, Boolean withPartner, Double incomeRange, FlatType bookedFlat, Project bookedProject) 
{
	super(id, name, password, age, maritalStatus);
	this.applicantName = applicantName;
	this.withPartner = withPartner;
	this.incomeRange = incomeRange;
	this.bookedFlat = bookedFlat;
	this.bookedProject = bookedProject;
}

public List<Project> viewEligibleProjects(ProjectRepository projectRepo) {
    List<Project> allProjects = projectRepo.getAll();  // all projects in the system (even if not visible or not eligible).
    // #######################Shyam needs to implement getAll in IRepository interface 
    List<Project> eligibleProjects = new ArrayList<>(); // empty list to store projects that applicant can view

    for (Project project : allProjects) {
        if (!project.isVisible()) continue; // only visible projects are considered for eligibility

        // check age & marital status
        MaritalStatus status = this.getMaritalStatus();
        int age = this.getAge(); // ############### currently not in user class
        
        if (status == MaritalStatus.MARRIED && age >= 21) {
        	// Married applicants can apply for 2-room or 3-room
            if (project.getAvailableUnits().containsKey(FlatType.TWO_ROOM) || // ####### unitsAvailable Map<FlatType,int> currently not in project class
            														      	  // containsKey() is a method for Map in Java -> returns true or false
                project.getAvailableUnits().containsKey(FlatType.THREE_ROOM)) { // check if the project has either 2-room or 3-room flats available
                eligibleProjects.add(project);
                
                // print number of avail units
                System.out.println("Project: " + project.getDescription()); // alr in project class
                System.out.println("Available Units:");
                for (Map.Entry<FlatType, Integer> entry : project.getAvailableUnits().entrySet()) { // not yet in project class
                    System.out.println("- " + entry.getKey() + ": " + entry.getValue());
                }
                System.out.println();
            }
            
        } else if (status == MaritalStatus.SINGLE && age >= 35) {
        	// Singles can only apply for 2-room
            if (project.getAvailableUnits().containsKey(FlatType.TWO_ROOM)) { // check if the project has 2-room flats available
                eligibleProjects.add(project);
                
                // print number of avail units
                System.out.println("Project: " + project.getDescription()); // alr in project class
                System.out.println("Available Units:");
                for (Map.Entry<FlatType, Integer> entry : project.getAvailableUnits().entrySet()) { // not yet in project class
                    System.out.println("- " + entry.getKey() + ": " + entry.getValue());
                }
                System.out.println();
            }
        }
    
    }

    return eligibleProjects;
}

public Enquiry submitEnquiry(String message, EnquiryRepository /*currently missing*/ enquiryRepo) {
    // Create unique ID (or use UUID)
    String enquiryId = UUID.randomUUID().toString(); // UUID is an object so need to convert to string

    // Get applicant ID (from User parent class)
    String applicantId = this.getId(); 

    // Create new Enquiry object
    Enquiry enquiry = new Enquiry(enquiryId, applicantId, message, /*LocalDateTime.now(),*/ EnquiryStatus.PENDING);
    														       // for our design, do we choose to display timestamp?

    // Save it using repository
    enquiryRepo.add(enquiry);

    // Return the enquiry to the caller
    return enquiry;
}

public List<Enquiry> viewMyEnquiries(EnquiryRepository repo) {
    List<Enquiry> all = repo.getAll(); // fetch all enquiries from repository (to be implemented in IRepository interface)
    List<Enquiry> mine = new ArrayList<>();

    for (Enquiry e : all) {
        if (e.getApplicantId().equals(this.getId())) { // applicantID stored in enquiry obj = current applicant running the method?
            mine.add(e);
        }
    }
    return mine;
}

//Allows applicant to edit their own enquiry message, if it's still in PENDING status
public void editEnquiry(String enquiryId, String newMessage, EnquiryRepository enquiryRepo) {
 List<Enquiry> all = enquiryRepo.getAll();

 for (Enquiry e : all) {
     if (e.getEnquiryId().equals(enquiryId) && e.getApplicantId().equals(this.getId())) { // e.getEnquiryId().equals(enquiryId) → Is this the enquiry the user is trying to edit?
    	 																				  // e.getApplicantId().equals(this.getId()) → Does this enquiry belong to the current applicant?    					 
         if (e.getStatus() == EnquiryStatus.PENDING) {
             e.setMessage(newMessage); // to add in Enquiry class
             enquiryRepo.update(e); // Save the updated enquiry
             System.out.println("Enquiry updated successfully.");
         } else {
             System.out.println("Cannot edit enquiry. It has already been replied to.");
         }
         return;
     }
 }
 System.out.println("Enquiry not found or not owned by applicant.");
}

//Allows applicant to delete their own enquiry, if it's still in PENDING status
public void deleteEnquiry(String enquiryId, EnquiryRepository enquiryRepo) {
	 List<Enquiry> all = enquiryRepo.getAll();
	
	 for (Enquiry e : all) {
	     if (e.getEnquiryId().equals(enquiryId) && e.getApplicantId().equals(this.getId())) {
	         if (e.getStatus() == EnquiryStatus.PENDING) {
	             enquiryRepo.delete(enquiryId); // Delete from repository 
	             System.out.println("Enquiry deleted successfully.");
	         } else {
	             System.out.println("Cannot delete enquiry. It has already been replied to.");
	         }
	         return;
	     }
	 }
	 System.out.println("Enquiry not found or not owned by applicant.");
}

public void submitApplication(Project project, FlatType flatType, ApplicationRepository /*currently missing*/ appRepo, ProjectRepository projectRepo) {

	// Check if applicant is eligible to apply for the project (i.e., has viewing rights)
	List<Project> eligibleProjects = viewEligibleProjects(projectRepo); // Reuses eligibility logic based on age, marital status, and visibility
	if (!eligibleProjects.contains(project)) {
	System.out.println("You are not eligible to apply for this project (no viewing rights).");
	return; // Prevents submission if project is not viewable (SOLID - enforces single responsibility cleanly)
	}
	
	String applicationId = UUID.randomUUID().toString(); // create unique ID
	// LocalDate applicationDate = LocalDate.now();
	
	Application application = new Application(applicationId, this, project, applicationDate.toString(), ApplicationStatus.PENDING, flatType);
	
	application.submit(); // Delegate logic to Application
	    // SRP: Applicant only initiates submission; Application manages its own logic.
	    // OCP: Application.submit() can be extended without changing Applicant.
	appRepo.add(application);
	
	this.bookedProject = project; // So applicant can view it later (Updates the Applicant object to remember which project they applied for)
}

//Allows the applicant to view the project and application status they applied for,
//even if the project's visibility is currently turned off
public void viewMyApplicationStatus(ApplicationRepository appRepo) {
 for (Application a : appRepo.getAll()) {
     if (a.getApplicant().getId().equals(this.getId())) { // check if this application belongs to the current applicant.
         System.out.println("Project: " + a.getProject().getDescription());
         System.out.println("Status: " + a.getStatus());
         return;
     }
 }
 System.out.println("No application found.");
}

//Lets the applicant book a flat if their application is successful,
//and prevents booking multiple flats across different projects.
public void bookFlat(ApplicationRepository appRepo, FlatType flatType) {
 for (Application a : appRepo.getAll()) {
     if (a.getApplicant().getId().equals(this.getId())) {
         if (a.getStatus() == ApplicationStatus.SUCCESSFUL && this.bookedFlat == null) {
             a.updateStatus(ApplicationStatus.BOOKED); // delegate update to Application class
             appRepo.update(a); // IRepository needs to add this in 
             this.bookedFlat = flatType;
             System.out.println("Flat booked successfully: " + flatType);
         } else if (this.bookedFlat != null) {
             System.out.println("You have already booked a flat.");
         } else {
             System.out.println("Booking not allowed. Application not successful.");
         }
         return;
     }
 }
 System.out.println("No application found.");
}

public boolean requestWithdrawal(ApplicationRepository appRepo) { // ApplicationRepository needs to be implemented
    List<Application> allApplications = appRepo.getAll();
    Application myApplication = null; // to store the application that belongs to the applicant

    for (Application a : allApplications) {
        if (a.getApplicantId().equals(this.getId())) { // if application belongs to applicant
            myApplication = a;
            break;
        }
    }

    if (myApplication == null) {
        System.out.println("No active application found.");
        return false;
    }

    // Update the status to "UNSUCCESSFUL" to indicate withdrawal (based on FAQ)
    myApplication.setStatus(ApplicationStatus.UNSUCCESSFUL);
    appRepo.update(myApplication);

    // Clear booking info, since the application was withdrawn
    this.bookedFlat = null;
    this.bookedProject = null;

    System.out.println("Application withdrawn. Status set to UNSUCCESSFUL.");
    return true;
}

// if withdrawal always assumed to be approved (return type change to void, no need for error checking)
//public void requestWithdrawal(ApplicationRepository appRepo) {
//    List<Application> allApplications = appRepo.getAll();
//    Application myApplication = null;
//
//    for (Application a : allApplications) {
//        if (a.getApplicantId().equals(this.getId())) {
//            myApplication = a;
//            break;
//        }
//    }
//
//    // Since withdrawal is always assumed to succeed, we skip error checks
//
//    // Mark status as UNSUCCESSFUL to represent withdrawal
//    myApplication.setStatus(ApplicationStatus.UNSUCCESSFUL);
//    appRepo.update(myApplication);
//
//    // Clear booking info (if any)
//    this.bookedFlat = null;
//    this.bookedProject = null;
//
//    System.out.println("Withdrawal processed. Status set to UNSUCCESSFUL.");
//}

