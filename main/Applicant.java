package sc2002.group.proj;

public class Applicant extends User {
	private String applicantName;
	private Double incomeRange;
	private FlatType bookedFlat = null;    // Default: not booked
	private Project bookedProject = null;  // Default: not applied/booked

	public Applicant(String id, String name, String password, int age, MaritalStatus maritalStatus, 
	                 String applicantName, Double incomeRange) {
		super(id, name, password, age, maritalStatus);
		this.applicantName = applicantName;
		this.incomeRange = incomeRange;
	}
	
	//Getters
	public String getApplicantName() {
		return applicantName;
	}

	public Double getIncomeRange() {
		return incomeRange;
	}

	public FlatType getBookedFlat() {
		return bookedFlat;
	}

	public Project getBookedProject() {
		return bookedProject;
	}

	//Setters
	public void setApplicantName(String applicantName) {
		this.applicantName = applicantName;
	}

	public void setIncomeRange(Double incomeRange) {
		this.incomeRange = incomeRange;
	}

	public void setBookedFlat(FlatType bookedFlat) {
		this.bookedFlat = bookedFlat;
	}

	public void setBookedProject(Project bookedProject) {
		this.bookedProject = bookedProject;
	}

	public List<Project> viewEligibleProjects(ProjectRepository projectRepo) {
	    List<Project> allProjects = projectRepo.getAll();  // all projects in the system (even if not visible or not eligible) // ###### getAll in IRepository interface? 
	    List<Project> eligibleProjects = new ArrayList<>(); // empty list to store projects that applicant can view

	    for (Project project : allProjects) {
	        if (!project.isVisible()) continue; // only visible projects are considered for eligibility #########isVisible() in Project class?

	        // check age & marital status
	        MaritalStatus status = this.getMaritalStatus();
	        int age = this.getAge(); // ####### getAge() in User class?
	        
	        if (status == MaritalStatus.MARRIED && age >= 21) {
	        	// Married applicants can apply for 2-room or 3-room
	            if (project.getAvailableUnits().containsKey(FlatType.TWO_ROOM) || // ####### unitsAvailable Map<FlatType,int> in project class?
	            														      	  // containsKey() is a method for Map in Java -> returns true or false
	                project.getAvailableUnits().containsKey(FlatType.THREE_ROOM)) { // check if the project has either 2-room or 3-room flats available
	                eligibleProjects.add(project); // add() comes from the List interface, and is implemented by ArrayList
	                
	                // print number of avail units
	                System.out.println("Project: " + project.getDescription()); // getDescription() in project class?
	                System.out.println("Available Units:");
	                for (Map.Entry<FlatType, Integer> entry : project.getAvailableUnits().entrySet()) { 
	                    System.out.println("- " + entry.getKey() + ": " + entry.getValue());
	                }
	                System.out.println();
	            }
	            
	        } else if (status == MaritalStatus.SINGLE && age >= 35) {
	        	// Singles can only apply for 2-room
	            if (project.getAvailableUnits().containsKey(FlatType.TWO_ROOM)) { // check if the project has 2-room flats available
	                eligibleProjects.add(project);
	                
	                // print number of avail units
	                System.out.println("Project: " + project.getDescription()); 
	                System.out.println("Available Units:");
	                for (Map.Entry<FlatType, Integer> entry : project.getAvailableUnits().entrySet()) { 
	                    System.out.println("- " + entry.getKey() + ": " + entry.getValue());
	                }
	                System.out.println();
	            }
	        }
	    
	    }

	    return eligibleProjects;
	}

	public Enquiry submitEnquiry(Project project, String message, EnquiryRepository enquiryRepo) { // ####### EnquiryRepository exists?
	    // Create unique ID (or use UUID)
	    String enquiryId = UUID.randomUUID().toString(); // UUID is an object so need to convert to string

	    // Get applicant ID (from User parent class)
	    String applicantId = this.getId(); // getId() in user class?

	    // Create new Enquiry object
	    Enquiry enquiry = new Enquiry(enquiryId, project, this, message);
	    														       
	    // Save it using repository
	    enquiryRepo.add(enquiry);

	    // Return the enquiry to the caller
	    return enquiry;
	}

	public List<Enquiry> viewMyEnquiries(EnquiryRepository repo) {
	    List<Enquiry> all = repo.getAll(); // fetch all enquiries from repository 
	    List<Enquiry> mine = new ArrayList<>();

	    for (Enquiry e : all) {
	    	if (e.getApplicant().getId().equals(this.getId())) { // applicantID stored in enquiry obj = current applicant running the method?
	            mine.add(e);
	        }
	    }
	    return mine;
	}

	//Allows applicant to edit their own enquiry message, if it's still in PENDING status
	public void editEnquiry(String enquiryId, String newMessage, EnquiryRepository enquiryRepo) {
	    List<Enquiry> all = enquiryRepo.getAll();

	    for (Enquiry e : all) {
	        if (e.getEnquiryId().equals(enquiryId) && e.getApplicant().getId().equals(this.getId())) {
	            if (e.isEditableOrDeletable()) {
	                e.setMessage(newMessage);  // Only works if status is PENDING
	                enquiryRepo.update(e);
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
	        if (e.getEnquiryId().equals(enquiryId) && e.getApplicant().getId().equals(this.getId())) {
	            if (e.isEditableOrDeletable()) {
	                enquiryRepo.delete(enquiryId);
	                System.out.println("Enquiry deleted successfully.");
	            } else {
	                System.out.println("Cannot delete enquiry. It has already been replied to.");
	            }
	            return;
	        }
	    }
	    System.out.println("Enquiry not found or not owned by applicant.");
	}


	public void submitApplication(Project project, FlatType flatType, ApplicationRepository appRepo, ProjectRepository projectRepo) {

		// Check if applicant is eligible to apply for the project (i.e., has viewing rights)
		List<Project> eligibleProjects = viewEligibleProjects(projectRepo); // Reuses eligibility logic based on age, marital status, and visibility
		if (!eligibleProjects.contains(project)) {
			System.out.println("You are not eligible to apply for this project (no viewing rights).");
			return; // Prevents submission if project is not viewable (SOLID - enforces single responsibility cleanly)
		}
		
		String applicationId = UUID.randomUUID().toString(); // create unique ID
		// LocalDate applicationDate = LocalDate.now();
		
		Application application = new Application(applicationId, this, project, applicationDate.toString(), flatType); // status is automatically set to PENDING in constructor
		
		appRepo.add(application);
		
		this.bookedProject = project; // So applicant can view it later (Updates the Applicant object to remember which project they applied for)
	}

	public Application getMyApplication(ApplicationRepository appRepo) {
        	for (Application a : appRepo.getAll()) {
            		if (a.getApplicant().getId().equals(this.getId())) {
                		return a;
            		}
        	}
        	return null;
    	}


	public boolean requestFlatBooking(HDBOfficer officer, FlatType flatType) {
		// only can request to book through officer IF status is SUCCESSFUL
		if (application.getStatus() != ApplicationStatus.SUCCESSFUL) {
		    System.out.println("You cannot book a flat unless your application is successful.");
		    return false;
		}
		// applicant can only book ONE flat through officer
	    if (this.bookedFlat != null) {
	        return false; // Already booked, officer will handle messaging if needed
	    }
	    // request flat booking to officer. officer handles actual booking
	    return officer.bookFlat(this, flatType);
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

	    myApplication.requestWithdrawal(); // delegate logic to Application class (SRP & OCP) -> application class will update status to "UNSUCCESSFUL" based on FAQ
	    appRepo.update(myApplication); // in IRepository

	    System.out.println("Withdrawal request submitted (awaiting HDB Manager's approval).");
	    return true;
	}

}
