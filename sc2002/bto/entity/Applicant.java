package sc2002.bto.entity;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import sc2002.bto.enums.ApplicationStatus;
import sc2002.bto.enums.EnquiryStatus;
import sc2002.bto.enums.FlatType;
import sc2002.bto.enums.MaritalStatus;
import sc2002.bto.repository.ApplicationRepository;
import sc2002.bto.repository.EnquiryRepository;
import sc2002.bto.repository.ProjectRepository;
/**
 * Represents an applicant in the BTO Management System.
 * This class extends the User class and contains additional properties and methods
 * specific to applicants, such as income range, flat booking functionality, and enquiry management.
 * 
 */
public class Applicant extends User {
    /** The name of the applicant */
    private String applicantName;
    /** The income range of the applicant */
    private Double incomeRange;
    /** The type of flat booked by the applicant, null if not booked yet */
    private FlatType bookedFlat = null;
    /** The project for which the applicant has booked a flat, null if not booked yet */
    private Project bookedProject = null;

    /**
     * Creates a new applicant with the specified details.
     * 
     * @param id The applicant's NRIC as a unique identifier
     * @param name The applicant's name
     * @param password The applicant's password
     * @param age The applicant's age
     * @param maritalStatus The applicant's marital status
     * @param applicantName The applicant's full name
     * @param incomeRange The applicant's income range
     */
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

    /**
     * Retrieves a list of projects that the applicant is eligible to apply for.
     * Eligibility is determined by age, marital status, and available units.
     * 
     * @param projectRepo The repository containing all available projects
     * @return A list of projects that the applicant is eligible for
     */
    public List<Project> viewEligibleProjects(ProjectRepository projectRepo) {
        List<Project> allProjects = projectRepo.getAll();
        List<Project> eligibleProjects = new ArrayList<>();

        for (Project project : allProjects) {
            if (!project.isVisible()) continue;

            // check age & marital status
            MaritalStatus status = this.getMaritalStatus();
            int age = this.getAge();
            
            if (status == MaritalStatus.MARRIED && age >= 21) {
                // Married applicants can apply for 2-room or 3-room
                if (project.getUnitsAvailable(FlatType.TWO_ROOM) > 0 || 
                    project.getUnitsAvailable(FlatType.THREE_ROOM) > 0) {
                    eligibleProjects.add(project);
                    
                    // print number of avail units
                    System.out.println("Project: " + project.getProjectID());
                    System.out.println("Available Units:");
                    System.out.println("- TWO_ROOM: " + project.getUnitsAvailable(FlatType.TWO_ROOM));
                    System.out.println("- THREE_ROOM: " + project.getUnitsAvailable(FlatType.THREE_ROOM));
                    System.out.println();
                }
                
            } else if (status == MaritalStatus.SINGLE && age >= 35) {
                // Singles can only apply for 2-room
                if (project.getUnitsAvailable(FlatType.TWO_ROOM) > 0) {
                    eligibleProjects.add(project);
                    
                    // print number of avail units
                    System.out.println("Project: " + project.getProjectID());
                    System.out.println("Available Units:");
                    System.out.println("- TWO_ROOM: " + project.getUnitsAvailable(FlatType.TWO_ROOM));
                    System.out.println();
                }
            }
        
        }

        return eligibleProjects;
    }

    /**
     * Submits an enquiry about a specific project.
     * 
     * @param project The project to enquire about
     * @param message The enquiry message
     * @param enquiryRepo The repository to store the enquiry
     * @return The created enquiry object
     */
    public Enquiry submitEnquiry(Project project, String message, EnquiryRepository enquiryRepo) {
        // Create unique ID
        String enquiryId = UUID.randomUUID().toString();

        // Create new Enquiry object
        Enquiry enquiry = new Enquiry(enquiryId, project, this, message);
                                                               
        // Save it using repository
        enquiryRepo.add(enquiry);

        // Return the enquiry to the caller
        return enquiry;
    }

    /**
     * Retrieves all enquiries submitted by this applicant.
     * 
     * @param repo The enquiry repository
     * @return A list of enquiries submitted by this applicant
     */
    public List<Enquiry> viewMyEnquiries(EnquiryRepository repo) {
        List<Enquiry> all = repo.getAll();
        List<Enquiry> mine = new ArrayList<>();

        for (Enquiry e : all) {
            if (e.getApplicant().getId().equals(this.getId())) {
                mine.add(e);
            }
        }
        return mine;
    }

    /**
     * Edits an existing enquiry message if it's still in PENDING status.
     * 
     * @param enquiryId The ID of the enquiry to edit
     * @param newMessage The new message content
     * @param enquiryRepo The enquiry repository
     */
    public void editEnquiry(String enquiryId, String newMessage, EnquiryRepository enquiryRepo) {
        List<Enquiry> all = enquiryRepo.getAll();

        for (Enquiry e : all) {
            if (e.getEnquiryId().equals(enquiryId) && e.getApplicant().getId().equals(this.getId())) {
                if (e.getStatus() == EnquiryStatus.PENDING) {
                    e.setMessage(newMessage);
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

    /**
     * Deletes an enquiry if it's still in PENDING status.
     * 
     * @param enquiryId The ID of the enquiry to delete
     * @param enquiryRepo The enquiry repository
     */
    public void deleteEnquiry(String enquiryId, EnquiryRepository enquiryRepo) {
        List<Enquiry> all = enquiryRepo.getAll();

        for (Enquiry e : all) {
            if (e.getEnquiryId().equals(enquiryId) && e.getApplicant().getId().equals(this.getId())) {
                if (e.getStatus() == EnquiryStatus.PENDING) {
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

    /**
     * Submits an application for a project and flat type.
     * 
     * @param project The project to apply for
     * @param flatType The type of flat to apply for
     * @param appRepo The application repository
     * @param projectRepo The project repository
     */
    public void submitApplication(Project project, FlatType flatType, ApplicationRepository appRepo, ProjectRepository projectRepo) {
        // Check if applicant is eligible to apply for the project (i.e., has viewing rights)
        List<Project> eligibleProjects = viewEligibleProjects(projectRepo);
        if (!eligibleProjects.contains(project)) {
            System.out.println("You are not eligible to apply for this project (no viewing rights).");
            return;
        }
        
        String applicationId = UUID.randomUUID().toString();
        String applicationDate = java.time.LocalDate.now().toString();
        
        Application application = new Application(applicationId, this, project, applicationDate, flatType);
        
        appRepo.add(application);
        
        this.bookedProject = project;
    }

    /**
     * Retrieves the applicant's current application.
     * 
     * @param appRepo The application repository
     * @return The applicant's current application, or null if none exists
     */
    public Application getMyApplication(ApplicationRepository appRepo) {
        for (Application a : appRepo.getAll()) {
            if (a.getApplicant().getId().equals(this.getId())) {
                return a;
            }
        }
        return null;
    }

    /**
     * Requests to book a flat through an HDB Officer.
     * 
     * @param officer The HDB Officer handling the booking
     * @param application The application for which to book a flat
     * @param flatType The type of flat to book
     * @return true if the booking request was successful, false otherwise
     */
    public boolean requestFlatBooking(HdbOfficer officer, Application application, FlatType flatType) {
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

    /**
     * Requests withdrawal of the applicant's current application.
     * 
     * @param appRepo The application repository
     * @return true if the withdrawal request was successful, false otherwise
     */

    public boolean requestWithdrawal(ApplicationRepository appRepo) {
        List<Application> allApplications = appRepo.getAll();
        Application myApplication = null;

        for (Application a : allApplications) {
            if (a.getApplicant().getId().equals(this.getId())) {
                myApplication = a;
                break;
            }
        }

        if (myApplication == null) {
            System.out.println("No active application found.");
            return false;
        }

        myApplication.requestWithdrawal();
        appRepo.update(myApplication);
        return true;
    }

    /**
     * Displays the status of the applicant's current application.
     * 
     * @param appRepo The application repository
     */
    public void viewMyApplicationStatus(ApplicationRepository appRepo) {
        for (Application a : appRepo.getAll()) {
            if (a.getApplicant().getId().equals(this.getId())) {
                System.out.println("Project: " + a.getProject().getProjectName());
                System.out.println("Status: " + a.getStatus());
                return;
            }
        }
        System.out.println("No application found.");
    }
    
}
