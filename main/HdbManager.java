import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

public class HDBManager extends Applicant implements IProjectManagement, IEnquiryManagement {
    // Fields
    private String managerName;
    private List<Project> projectsCreated;
    
    // Constructor
    public HDBManager(String id, String name, String email, boolean isActive, int age, boolean isMarried) {
        super(id, name, email, isActive, age, isMarried);
        this.managerName = name;
        this.projectsCreated = new ArrayList<>();
    }
    
    // Methods from UML diagram
    public void createProject(Project project) {
        // Set the manager in charge
        project.setManagerInCharge(this.managerName);
        
        // Check if manager is already handling a project in the same period
        Date currentDate = new Date();
        for (Project existingProject : projectsCreated) {
            if (currentDate.after(existingProject.getApplicationOpenDate()) && 
                currentDate.before(existingProject.getApplicationCloseDate())) {
                System.out.println("Error: Manager already handling a project during this application period.");
                return;
            }
        }
        
        // Initialize HDB Officer slots
        project.setAvailableOfficerSlots(10);
        
        // Add to projects created
        projectsCreated.add(project);
        System.out.println("Project created successfully: " + project.getProjectName());
    }
    
    public void editProject(Project project) {
        for (int i = 0; i < projectsCreated.size(); i++) {
            if (projectsCreated.get(i).getProjectID().equals(project.getProjectID())) {
                projectsCreated.set(i, project);
                System.out.println("Project updated successfully: " + project.getProjectName());
                return;
            }
        }
        System.out.println("Project not found.");
    }
    
    public void deleteProject(Project project) {
        boolean removed = projectsCreated.removeIf(p -> p.getProjectID().equals(project.getProjectID()));
        if (removed) {
            System.out.println("Project deleted successfully: " + project.getProjectName());
        } else {
            System.out.println("Project not found.");
        }
    }
    
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
    
    public List<Project> viewAllProjects() {
        // Assuming there's a static list of all projects somewhere
        return ProjectRepository.findAllProjects();
    }
    
    public void approveOfficerRegistration(HdbOfficer officer) {
        // Find the project the officer is applying for
        Project targetProject = null;
        for (Project p : projectsCreated) {
            if (p.getProjectID().equals(officer.getProjectAssigned().getProjectID())) {
                targetProject = p;
                break;
            }
        }
        
        if (targetProject == null) {
            System.out.println("Project not found or not managed by this manager.");
            return;
        }
        
        // Check if there are available slots
        if (targetProject.getAvailableOfficerSlots() > 0) {
            officer.setRegistrationStatus("APPROVED");
            targetProject.setAvailableOfficerSlots(targetProject.getAvailableOfficerSlots() - 1);
            System.out.println("Officer registration approved.");
        } else {
            officer.setRegistrationStatus("REJECTED");
            System.out.println("Officer registration rejected. No available slots.");
        }
    }
    
    public void approveApplication(Application application) {
        Project project = application.getProject();
        String flatType = application.getFlatType();
        
        // Check if the project is managed by this manager
        if (!projectsCreated.contains(project)) {
            System.out.println("Project not managed by this manager.");
            return;
        }
        
        // Check if there are available units for the flat type
        int availableUnits;
        if ("2-ROOM".equals(flatType)) {
            availableUnits = project.getRemainingTwoRoomUnits();
            if (availableUnits > 0) {
                project.setRemainingTwoRoomUnits(availableUnits - 1);
                application.setApplicationStatus("APPROVED");
                System.out.println("Application approved for a 2-ROOM flat.");
            } else {
                application.setApplicationStatus("REJECTED");
                System.out.println("Application rejected. No available 2-ROOM units.");
            }
        } else if ("3-ROOM".equals(flatType)) {
            availableUnits = project.getRemainingThreeRoomUnits();
            if (availableUnits > 0) {
                project.setRemainingThreeRoomUnits(availableUnits - 1);
                application.setApplicationStatus("APPROVED");
                System.out.println("Application approved for a 3-ROOM flat.");
            } else {
                application.setApplicationStatus("REJECTED");
                System.out.println("Application rejected. No available 3-ROOM units.");
            }
        } else {
            System.out.println("Invalid flat type specified.");
        }
    }
    
    public Report generateReport(ReportType reportType) {
        // Create a new report
        Report report = new Report();
        report.setReportType(reportType);
        
        // Get all applications for projects managed by this manager
        List<Application> applications = ApplicationRepository.findAllApplications()
                .stream()
                .filter(app -> projectsCreated.contains(app.getProject()))
                .collect(Collectors.toList());
        
        // Apply filters based on report type
        List<Object> filteredData = new ArrayList<>();
        if (reportType == ReportType.BY_FLAT_TYPE) {
            // Group by flat type
            // Implementation would depend on how you want to structure the report
            filteredData.addAll(applications);
        } else if (reportType == ReportType.BY_MARITAL_STATUS) {
            // Filter by marital status
            filteredData.addAll(applications.stream()
                    .filter(app -> app.getApplicant().isMarried())
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
    public void createProject(String projectName, String neighborhood, List<String> flatTypes, 
                             int twoRoomUnits, int threeRoomUnits, Date openDate, Date closeDate) {
        Project project = new Project();
        project.setProjectID("PROJ-" + System.currentTimeMillis());
        project.setProjectName(projectName);
        project.setNeighborhood(neighborhood);
        project.setTwoRoomUnits(twoRoomUnits);
        project.setThreeRoomUnits(threeRoomUnits);
        project.setRemainingTwoRoomUnits(twoRoomUnits);
        project.setRemainingThreeRoomUnits(threeRoomUnits);
        project.setApplicationOpenDate(openDate);
        project.setApplicationCloseDate(closeDate);
        project.setManagerInCharge(this.managerName);
        project.setAvailableOfficerSlots(10);
        project.setVisible(true);
        
        createProject(project);
    }

    @Override
    public void respondToEnquiry(String enquiryId, String response) {
        // Find the enquiry and update its response
        Enquiry enquiry = EnquiryRepository.findById(enquiryId);
        if (enquiry != null) {
            // Check if the enquiry is related to a project managed by this manager
            Project projectInEnquiry = enquiry.getProject();
            if (projectsCreated.stream().anyMatch(p -> p.getProjectID().equals(projectInEnquiry.getProjectID()))) {
                enquiry.setResponse(response);
                enquiry.setStatus("ANSWERED");
                System.out.println("Enquiry responded successfully.");
            } else {
                System.out.println("Cannot respond to enquiry for a project not managed by this manager.");
            }
        } else {
            System.out.println("Enquiry not found.");
        }
    }
    
    // Methods to handle additional requirements
    
    // View projects created by this manager only
    public List<Project> viewMyProjects() {
        return new ArrayList<>(projectsCreated);
    }
    
    // Handle applicant's withdrawal request
    public void handleWithdrawalRequest(Application application, boolean approve) {
        if (approve) {
            // Release the flat unit back to available pool
            Project project = application.getProject();
            String flatType = application.getFlatType();
            
            if ("2-ROOM".equals(flatType)) {
                project.setRemainingTwoRoomUnits(project.getRemainingTwoRoomUnits() + 1);
            } else if ("3-ROOM".equals(flatType)) {
                project.setRemainingThreeRoomUnits(project.getRemainingThreeRoomUnits() + 1);
            }
            
            application.setApplicationStatus("WITHDRAWN");
            System.out.println("Withdrawal request approved.");
        } else {
            System.out.println("Withdrawal request rejected.");
        }
    }
    
    // View all pending HDB Officer registrations
    public List<HdbOfficer> viewPendingOfficerRegistrations() {
        return projectsCreated.stream()
                .flatMap(p -> HdbOfficer.getAllOfficers().stream()
                        .filter(o -> o.getProjectAssigned() != null && 
                                 o.getProjectAssigned().getProjectID().equals(p.getProjectID()) && 
                                 "PENDING".equals(o.getRegistrationStatus())))
                .collect(Collectors.toList());
    }
    
    // View all approved HDB Officer registrations
    public List<HdbOfficer> viewApprovedOfficerRegistrations() {
        return projectsCreated.stream()
                .flatMap(p -> HdbOfficer.getAllOfficers().stream()
                        .filter(o -> o.getProjectAssigned() != null && 
                                 o.getProjectAssigned().getProjectID().equals(p.getProjectID()) && 
                                 "APPROVED".equals(o.getRegistrationStatus())))
                .collect(Collectors.toList());
    }
    
    // View all enquiries for all projects
    public List<Enquiry> viewAllEnquiries() {
        return EnquiryRepository.findAllEnquiries();
    }
    
    // View enquiries for projects managed by this manager
    public List<Enquiry> viewMyProjectsEnquiries() {
        return EnquiryRepository.findAllEnquiries().stream()
                .filter(e -> e.getProject() != null && 
                          projectsCreated.stream().anyMatch(p -> 
                              p.getProjectID().equals(e.getProject().getProjectID())))
                .collect(Collectors.toList());
    }
    
    // Check if manager is handling any active projects
    public boolean isHandlingActiveProject() {
        Date currentDate = new Date();
        return projectsCreated.stream().anyMatch(p -> 
            currentDate.after(p.getApplicationOpenDate()) && 
            currentDate.before(p.getApplicationCloseDate()));
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
    
    // Override application method from Applicant to prevent HDB Managers from applying
    @Override
    public void createApplication(Project project, String flatType) {
        System.out.println("HDB Managers cannot apply for BTO projects.");
    }
}
