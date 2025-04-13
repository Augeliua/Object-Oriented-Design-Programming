package sc2002.bto.entity;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import sc2002.bto.enums.ApplicationStatus;
import sc2002.bto.enums.EnquiryStatus;
import sc2002.bto.enums.FlatType;
import sc2002.bto.enums.MaritalStatus;
import sc2002.bto.repository.ApplicationRepository;
import sc2002.bto.repository.EnquiryRepository;
import sc2002.bto.repository.ProjectRepository;

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

    //Allows applicant to edit their own enquiry message, if it's still in PENDING status
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

    //Allows applicant to delete their own enquiry, if it's still in PENDING status
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

    //Allows the applicant to view the project and application status they applied for,
    //even if the project's visibility is currently turned off
    public Application getMyApplication(ApplicationRepository appRepo) {
        for (Application a : appRepo.getAll()) {
            if (a.getApplicant().getId().equals(this.getId())) {
                return a;
            }
        }
        return null;
    }

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

        System.out.println("Withdrawal request submitted (awaiting HDB Manager's approval).");
        return true;
    }
}
