package sc2002.bto.entity;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.stream.Collectors;
import sc2002.bto.enums.ApplicationStatus;
import sc2002.bto.enums.FlatType;
import sc2002.bto.enums.MaritalStatus;
import sc2002.bto.enums.OfficerRegistrationStatus;
import sc2002.bto.enums.ReportType;
import sc2002.bto.interfaces.IEnquiryManagement;
import sc2002.bto.interfaces.IProjectManagement;
import sc2002.bto.repository.ApplicationRepository;
import sc2002.bto.repository.EnquiryRepository;
import sc2002.bto.repository.ProjectRepository;

/**
 * Represents an HDB Manager in the BTO Management System.
 * This class extends the Applicant class and implements IProjectManagement and IEnquiryManagement interfaces.
 * HDB Managers are responsible for creating, editing, and managing BTO projects,
 * approving applications, and handling enquiries.
 * 
 */
public class HdbManager extends Applicant implements IProjectManagement, IEnquiryManagement {
    /** The name of the manager */
    private String managerName;
    /** List of projects created by this manager */
    private List<Project> projectsCreated;
    
    /**
     * Creates a new HDB Manager with the specified details.
     * 
     * @param id The manager's NRIC as a unique identifier
     * @param name The manager's name
     * @param password The manager's password
     * @param age The manager's age
     * @param maritalStatus The manager's marital status
     * @param applicantName The manager's full name
     * @param incomeRange The manager's income range
     */
    public HdbManager(String id, String name, String password, int age, MaritalStatus maritalStatus, 
                      String applicantName, Double incomeRange) {
        super(id, name, password, age, maritalStatus, applicantName, incomeRange);
        this.managerName = name;
        this.projectsCreated = new ArrayList<>();
    }
    
    /**
     * Creates a new BTO project.
     * 
     * @param project The project to create
     * @param projectRepo The project repository
     */
    public void createProject(Project project, ProjectRepository projectRepo) {
        // Set the manager in charge
        project.setManagerInCharge(this.managerName);
        
        // Check if manager is already handling a project in the same period
        Date currentDate = new Date();
        for (Project existingProject : projectsCreated) {
            if (currentDate.after(parseDate(existingProject.getApplicationOpenDate())) && 
                currentDate.before(parseDate(existingProject.getApplicationCloseDate()))) {
                System.out.println("Error: Manager already handling a project during this application period.");
                return;
            }
        }
        
        // Initialize HDB Officer slots
        project.setAvailableOfficerSlots(10);
        
        // Add to projects created
        projectsCreated.add(project);
        projectRepo.add(project);
        System.out.println("Project created successfully: " + project.getProjectID());
    }
    
    /**
     * Parses a date string into a Date object.
     * 
     * @param dateStr Date string in format yyyy-MM-dd
     * @return A Date object representing the parsed date
     */
    private Date parseDate(String dateStr) {
        try {
            return new java.text.SimpleDateFormat("yyyy-MM-dd").parse(dateStr);
        } catch (Exception e) {
            return new Date(); // Default to current date if parsing fails
        }
    }
    
    /**
     * Edits an existing project's details.
     * 
     * @param project The project with updated information
     */
    public void editProject(Project project) {
        for (int i = 0; i < projectsCreated.size(); i++) {
            if (projectsCreated.get(i).getProjectID().equals(project.getProjectID())) {
                projectsCreated.set(i, project);
                System.out.println("Project updated successfully: " + project.getProjectID());
                return;
            }
        }
        System.out.println("Project not found.");
    }

    /**
     * Deletes an existing project.
     * 
     * @param project The project to delete
     */
    public void deleteProject(Project project) {
        boolean removed = projectsCreated.removeIf(p -> p.getProjectID().equals(project.getProjectID()));
        if (removed) {
            System.out.println("Project deleted successfully: " + project.getProjectID());
        } else {
            System.out.println("Project not found.");
        }
    }
    
    /**
     * Toggles the visibility of a project.
     * 
     * @param project The project to toggle visibility for
     */
    public void toggleProjectVisibility(Project project) {
        for (Project p : projectsCreated) {
            if (p.getProjectID().equals(project.getProjectID())) {
                p.setVisible(!p.isVisible());
                System.out.println("Project visibility toggled to: " + (p.isVisible() ? "Visible" : "Hidden"));
                return;
            }
        }
        System.out.println("Project not found.");
    }
    
    /**
     * Views all projects in the system.
     * 
     * @param projectRepo The project repository
     * @return A list of all projects
     */
    public List<Project> viewAllProjects(ProjectRepository projectRepo) {
        return projectRepo.getAll();
    }
    
    /**
     * Approves an HDB Officer's registration to handle a project.
     * 
     * @param officer The HDB Officer to approve
     */
    public void approveOfficerRegistration(HdbOfficer officer) {
        Project targetProject = officer.getPendingProject(); // ✅ changed from getHandlingProject()

        if (targetProject == null) {
            System.out.println("No pending registration found for this officer.");
            return;
        }

        // Check if this manager created the project
        boolean isManagedByThisManager = false;
        for (Project p : projectsCreated) {
            if (p.getProjectID().equals(targetProject.getProjectID())) {
                isManagedByThisManager = true;
                break;
            }
        }

        if (!isManagedByThisManager) {
            System.out.println("Project is not managed by this manager.");
            return;
        }

        // Check if there are available slots
        if (targetProject.getAvailableOfficerSlots() > 0) {
            officer.setRegistrationStatus(OfficerRegistrationStatus.APPROVED);
            officer.setHandlingProject(targetProject); // ✅ Now assigned officially
            officer.setPendingProject(null);           // ✅ Clear pending
            targetProject.setAvailableOfficerSlots(targetProject.getAvailableOfficerSlots() - 1);
            System.out.println("Officer registration approved and assigned to project: " + targetProject.getProjectName());
        } else {
            officer.setRegistrationStatus(OfficerRegistrationStatus.REJECTED);
            officer.setPendingProject(null); // ✅ Clear pending even if rejected
            System.out.println("Officer registration rejected. No available slots.");
        }
    }

    /**
     * Approves or rejects an application for a BTO project.
     * 
     * @param application The application to approve or reject
     * @param projectRepo The project repository
     */
    public void approveApplication(Application application, ProjectRepository projectRepo) {
        Project project = application.getProject();
        FlatType type = application.getSelectedFlatType();
        
        // Check if the project is managed by this manager
        if (!projectsCreated.contains(project)) {
            System.out.println("Project not managed by this manager.");
            return;
        }
        
        // Check if there are available units for the flat type
        int availableUnits = project.getUnitsAvailable(type);
        
        if (availableUnits > 0) {
            application.updateStatus(ApplicationStatus.SUCCESSFUL);
            // Do not decrease availability here — only on booking!
            projectRepo.update(project); // Optional: just to sync
        }
        else {
            application.updateStatus(ApplicationStatus.UNSUCCESSFUL);
        }
    }
    
    /**
     * Reviews pending applications for projects created by this manager.
     * 
     * @param appRepo The application repository
     * @param projectRepo The project repository
     */
    public void reviewApplications(ApplicationRepository appRepo, ProjectRepository projectRepo) {
        List<Application> all = appRepo.getAll();
        
        // Filter applications for projects created by this manager
        List<Application> managerApplications = all.stream()
                .filter(a -> projectsCreated.stream()
                        .anyMatch(p -> p.getProjectID().equals(a.getProject().getProjectID())))
                .filter(a -> a.getStatus() == ApplicationStatus.PENDING)
                .collect(Collectors.toList());
        
        // If no applications found, print "No applications"
        if (managerApplications.isEmpty()) {
            System.out.println("No applications.");
            return;
        }
        
        // Scanner for user input
        @SuppressWarnings("resource")
        Scanner scanner = new Scanner(System.in);
        
        for (Application a : managerApplications) {
            // Skip already processed applications
            if (a.getStatus() != ApplicationStatus.PENDING) continue;
            
            // Check for withdrawal requests first
            if (a.isWithdrawalRequested()) {
                System.out.println("\nWithdrawal Request Received:");
                System.out.println("Applicant: " + a.getApplicant().getName());
                System.out.println("Project: " + a.getProject().getProjectName());
                System.out.println("Flat Type: " + a.getSelectedFlatType());
                
                System.out.print("Approve withdrawal? (Y/N): ");
                String withdrawalApproval = scanner.nextLine();
                
                if (withdrawalApproval.equalsIgnoreCase("Y")) {
                    a.updateStatus(ApplicationStatus.UNSUCCESSFUL);
                    System.out.println("Withdrawal processed and approved.");
                } else {
                    System.out.println("Withdrawal request rejected.");
                    // Keep the application in PENDING status
                    continue;
                }
                
                appRepo.update(a);
                continue;
            }
            
            // Display application details
            System.out.println("\nApplication Details:");
            System.out.println("Applicant: " + a.getApplicant().getName());
            System.out.println("Project: " + a.getProject().getProjectName());
            System.out.println("Flat Type: " + a.getSelectedFlatType());
            
            // Check flat availability
            Project project = a.getProject();
            FlatType type = a.getSelectedFlatType();
            int availableUnits = project.getUnitsAvailable(type);
            
            // Explicitly show unit availability
            System.out.println("Available " + type + " Units: " + availableUnits);
            
            // Check for insufficient rooms
            if (availableUnits <= 0) {
                System.out.println("INSUFFICIENT ROOMS: There are no available units for " + type + ".");
                System.out.println("You will need to manually reject this application.");
            }
            
            // Prompt for approval
            System.out.print("Approve this application? (Y/N): ");
            String approval = scanner.nextLine();
            
            if (approval.equalsIgnoreCase("Y")) {
                // Check units availability again before approving
                if (availableUnits > 0) {
                    approveApplication(a, projectRepo); // update status to SUCCESSFUL and reduce num of avail units by one
                    System.out.println("Application approved. Applicant is invited to book a flat.");
                } else {
                    a.updateStatus(ApplicationStatus.UNSUCCESSFUL);
                    System.out.println("Application rejected due to no available units.");
                }
            } else {
                a.updateStatus(ApplicationStatus.UNSUCCESSFUL);
                System.out.println("Application rejected.");
            }
            
            // Update application in repository
            appRepo.update(a);
        }
        
        System.out.println("\nApplication review completed.");
    } 
    
    /**
     * Generates a report based on the specified report type.
     * 
     * @param reportType The type of report to generate
     * @param appRepo The application repository
     * @return The generated report
     */
    public Report generateReport(ReportType reportType, ApplicationRepository appRepo) {
        // Create a new report
        Report report = new Report();
        report.setReportType(reportType);
        
        // Get all applications for projects managed by this manager
        List<Application> applications = appRepo.getAll()
                .stream()
                .filter(app -> projectsCreated.contains(app.getProject()))
                .collect(Collectors.toList());
        
        // Apply filters based on report type
        List<Object> filteredData = new ArrayList<>();
        if (reportType == ReportType.BY_FLAT_TYPE) {
            // Group by flat type
            Map<FlatType, List<Application>> groupedByFlatType = new HashMap<>();
            for (Application app : applications) {
                FlatType flatType = app.getSelectedFlatType();
                if (!groupedByFlatType.containsKey(flatType)) {
                    groupedByFlatType.put(flatType, new ArrayList<>());
                }
                groupedByFlatType.get(flatType).add(app);
            }
            filteredData.add(groupedByFlatType);
        } else if (reportType == ReportType.BY_MARITAL_STATUS) {
            // Filter by marital status
            filteredData.addAll(applications.stream()
                    .filter(app -> app.getApplicant().getMaritalStatus() == MaritalStatus.MARRIED)
                    .collect(Collectors.toList()));
        } else {
            // Default - all bookings
            filteredData.addAll(applications);
        }
        
        report.setItems(filteredData);
        return report;
    }
    
    // Methods from IProjectManagement interface
    @Override
    public void reviewProject(Project p) {
        if (projectsCreated.contains(p)) {
            System.out.println("Reviewing project: " + p.getProjectID());
            // Implementation details
        } else {
            System.out.println("Cannot review project not created by this manager.");
        }
    }
    
    @Override
    public void approveProject(Project p) {
        if (projectsCreated.contains(p)) {
            System.out.println("Approving project: " + p.getProjectID());
            // Implementation details
        } else {
            System.out.println("Cannot approve project not created by this manager.");
        }
    }
    
    /**
     * Creates a new project with the specified details.
     * 
     * @param projectName The name of the project
     * @param neighborhood The neighborhood where the project is located
     * @param flatTypes Array of flat types available in this project
     * @param twoRoomUnits Number of 2-room units
     * @param threeRoomUnits Number of 3-room units
     * @param openDate Application opening date
     * @param closeDate Application closing date
     * @param projectRepo The project repository
     */
    public void createProject(String projectName, String neighborhood, FlatType[] flatTypes, 
                             int twoRoomUnits, int threeRoomUnits, String openDate, String closeDate, ProjectRepository projectRepo) {
        Project project = new Project(
            "PROJ-" + java.util.UUID.randomUUID().toString().substring(0, 8).toUpperCase(), 
            neighborhood, 
            flatTypes, 
            1, // Default floor count 
            500000, // Default price per flat
            450000, // Default threshold price
            openDate, 
            closeDate, 
            true, // Visible by default
            10, // Available officer slots
            twoRoomUnits, 
            threeRoomUnits
        );
        project.setManagerInCharge(this.managerName);
        
        createProject(project, projectRepo);
    }

    @Override
    public void handleEnquiry(Enquiry e) {
        if (e == null) {
            System.out.println("Cannot handle null enquiry");
            return;
        }
        
        Project project = e.getProject();
        
        // Check if this manager handles the project
        if (projectsCreated.stream().noneMatch(p -> p.getProjectID().equals(project.getProjectID()))) {
            System.out.println("Cannot handle enquiry for project not managed by this manager");
            return;
        }
        
        System.out.println("Handling enquiry: " + e.getEnquiryId());
        // Additional handling logic
    }
    
    @Override
    public void respondToEnquiry(Enquiry e, String response) {
        if (e == null) {
            System.out.println("Cannot respond to null enquiry");
            return;
        }
        
        Project project = e.getProject();
        
        // Check if this manager handles the project
        if (projectsCreated.stream().noneMatch(p -> p.getProjectID().equals(project.getProjectID()))) {
            System.out.println("Cannot respond to enquiry for project not managed by this manager");
            return;
        }
        
        e.reply(response);
        System.out.println("Response added to enquiry: " + e.getEnquiryId());
    }
    
    // Methods to handle additional requirements
    
    // View projects created by this manager only
    public List<Project> viewMyProjects() {
        return new ArrayList<>(projectsCreated);
    }
    
    /**
     * Handles an applicant's withdrawal request.
     * 
     * @param application The application to withdraw
     * @param approve Whether to approve the withdrawal request
     */
    public void handleWithdrawalRequest(Application application, boolean approve) {
        if (approve) {
            Project project = application.getProject();
            FlatType flatType = application.getSelectedFlatType();
        
            // Only add back a flat if it was actually booked
            if (application.getStatus() == ApplicationStatus.BOOKED) {
                if (flatType == FlatType.TWO_ROOM) {
                    project.setTwoRoomUnitsAvailable(project.getTwoRoomUnitsAvailable() + 1);
                } else if (flatType == FlatType.THREE_ROOM) {
                    project.setThreeRoomUnitsAvailable(project.getThreeRoomUnitsAvailable() + 1);
                }
            }
        
            application.updateStatus(ApplicationStatus.UNSUCCESSFUL);
        } else {
            System.out.println("Withdrawal request rejected for application: " + application.getApplicationId());
        }

        application.clearWithdrawalRequest(); // clears the flag
    }
    
    // View all pending HDB Officer registrations
    public List<HdbOfficer> viewPendingOfficerRegistrations(List<HdbOfficer> allOfficers) {
        return allOfficers.stream()
                .filter(o -> o.getHandlingProject() != null && 
                         projectsCreated.stream().anyMatch(p -> 
                             p.getProjectID().equals(o.getHandlingProject().getProjectID())) &&
                         o.getRegistrationStatus() == OfficerRegistrationStatus.PENDING)
                .collect(Collectors.toList());
    }
    
    // View all approved HDB Officer registrations
    public List<HdbOfficer> viewApprovedOfficerRegistrations(List<HdbOfficer> allOfficers) {
        return allOfficers.stream()
                .filter(o -> o.getHandlingProject() != null && 
                         projectsCreated.stream().anyMatch(p -> 
                             p.getProjectID().equals(o.getHandlingProject().getProjectID())) &&
                         o.getRegistrationStatus() == OfficerRegistrationStatus.APPROVED)
                .collect(Collectors.toList());
    }
    
    // View all enquiries for all projects
    public List<Enquiry> getAllEnquiries(EnquiryRepository enquiryRepo) {
        return enquiryRepo.getAll();
    }
    
    // Print all enquiries for all projects
    // added to solve error in ManagerUI: The method printAllEnquiries(EnquiryRepository) is undefined for the type HdbManager
    public void printAllEnquiries(EnquiryRepository enquiryRepo) {
        List<Enquiry> enquiries = getAllEnquiries(enquiryRepo);
    
        if (enquiries.isEmpty()) {
            System.out.println("There are no enquiries in the system.");
            return;
        }
    
        System.out.println("=== All Enquiries in System ===");
        for (Enquiry e : enquiries) {
            System.out.println("ID: " + e.getEnquiryId() +
                               ", Applicant: " + e.getApplicant().getApplicantName() +
                               ", Project: " + e.getProject().getProjectID() +
                               ", Status: " + e.getStatus());
        }
    }

    // View enquiries for projects managed by this manager
    public List<Enquiry> getMyProjectsEnquiries(EnquiryRepository enquiryRepo) {
        return enquiryRepo.getAll().stream()
                .filter(e -> e.getProject() != null && 
                          projectsCreated.stream().anyMatch(p -> 
                              p.getProjectID().equals(e.getProject().getProjectID())))
                .collect(Collectors.toList());
    }

    // print enquiries for projects managed by this manager
    // added to solve this error in ManagerUI: The method printMyProjectsEnquiries(EnquiryRepository) is undefined for the type HdbManager
    public void printMyProjectsEnquiries(EnquiryRepository enquiryRepo) {
        List<Enquiry> enquiries = getMyProjectsEnquiries(enquiryRepo);
    
        if (enquiries.isEmpty()) {
            System.out.println("No enquiries for your projects.");
            return;
        }
    
        System.out.println("=== Enquiries for Projects Managed by " + this.managerName + " ===");
        for (Enquiry e : enquiries) {
            System.out.println("Enquiry ID: " + e.getEnquiryId());
            System.out.println("Project: " + e.getProject().getProjectID());
            System.out.println("Applicant: " + e.getApplicant().getApplicantName());
            System.out.println("Message: " + e.getMessage());
            System.out.println("Status: " + e.getStatus());
            System.out.println("-----------------------------");
        }
    }
    
    /**
     * Checks if the manager is handling any active projects.
     * 
     * @return true if the manager is handling an active project, false otherwise
     */
    public boolean isHandlingActiveProject() {
        Date currentDate = new Date();
        return projectsCreated.stream().anyMatch(p -> 
            currentDate.after(parseDate(p.getApplicationOpenDate())) && 
            currentDate.before(parseDate(p.getApplicationCloseDate())));
    }
    
    // Getters and setters
    public String getManagerName() {
        return managerName;
    }

    public void setManagerName(String managerName) {
        this.managerName = managerName;
    }

    public List<Project> getProjectsCreated() {
        return projectsCreated;
    }

    public void setProjectsCreated(List<Project> projectsCreated) {
        this.projectsCreated = projectsCreated;
    }
    
    /**
     * Overrides the submitApplication method from Applicant to prevent managers from applying for BTO projects.
     */
    @Override
    public void submitApplication(Project project, FlatType flatType, ApplicationRepository appRepo, ProjectRepository projectRepo) {
        System.out.println("HDB Managers cannot apply for BTO projects.");
    }
}
