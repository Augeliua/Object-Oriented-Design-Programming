import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
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
        
        // Initialise HDB Officer slots
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
        return ProjectRepository.getAll();
    }
    
    public void approveOfficerRegistration(HdbOfficer officer) {
        // Find the project the officer is applying for
        Project targetProject = null;
        for (Project p : projectsCreated) {
            if (p.getProjectID().equals(officer.gethandlingProject().getProjectID())) {
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
            officer.setRegistrationStatus(OfficerRegistrationStatus.APPROVED);
            targetProject.setAvailableOfficerSlots(targetProject.getAvailableOfficerSlots() - 1);
            System.out.println("Officer registration approved.");
        } else {
            officer.setRegistrationStatus(OfficerRegistrationStatus.REJECTED);
            System.out.println("Officer registration rejected. No available slots.");
        }
    
    // Updated to match the approveApplication method based on feedback
    public void approveApplication(Application application) {
        Project project = application.getProject();
        FlatType type = application.getSelectedFlatType();
        
        // Check if the project is managed by this manager
        if (!projectsCreated.contains(project)) {
            System.out.println("Project not managed by this manager.");
            return;
        }
        
        // Check if there are available units for the flat type
        Map<FlatType, Integer> available = project.getAvailableUnits();
        
        if (available.getOrDefault(type, 0) > 0) {
            application.updateStatus(ApplicationStatus.SUCCESSFUL);
            // Decrease availability
            available.put(type, available.get(type) - 1);
            System.out.println("Application approved: " + application.getApplicationId());
        } else {
            application.updateStatus(ApplicationStatus.UNSUCCESSFUL);
            System.out.println("Application rejected (no units): " + application.getApplicationId());
        }
    }
    
  
    public void reviewApplications(ApplicationRepository appRepo) {
        List<Application> all = appRepo.getAll();
        
        for (Application a : all) {
            if (a.getStatus() == ApplicationStatus.PENDING) {
                // Check for withdrawal requests first
                if (a.withdrawalRequested()) {
                    a.updateStatus(ApplicationStatus.UNSUCCESSFUL);
                    System.out.println("Withdrawal processed for application: " + a.getApplicationId());
                    appRepo.update(a); // Save the new status
                    continue;
                }
                
                // Approve or reject based on flat availability
                FlatType type = a.getSelectedFlatType();
                Project project = a.getProject();
                
                Map<FlatType, Integer> available = project.getAvailableUnits();
                
                if (available.getOrDefault(type, 0) > 0) {
                    a.updateStatus(ApplicationStatus.SUCCESSFUL);
                    // Decrease availability
                    available.put(type, available.get(type) - 1);
                    System.out.println("Application approved: " + a.getApplicationId());
                } else {
                    a.updateStatus(ApplicationStatus.UNSUCCESSFUL);
                    System.out.println("Application rejected (no units): " + a.getApplicationId());
                }
                
                appRepo.update(a); // Save the new status
            }
        }
    }
    
    public Report generateReport(ReportType reportType) {
        // Create a new report
        Report report = new Report();
        report.setReportType(reportType);
        
        // Get all applications for projects managed by this manager
        List<Application> applications = ApplicationRepository.getAll()
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
        Enquiry enquiry = EnquiryRepository.getById(enquiryId);
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
    
    // Handle applicant's withdrawal request - modified based on feedback
    public void handleWithdrawalRequest(Application application, boolean approve) {
        if (approve) {
            // Release the flat unit back to available pool
            Project project = application.getProject();
            FlatType flatType = application.getSelectedFlatType();
            
            Map<FlatType, Integer> available = project.getAvailableUnits();
            available.put(flatType, available.get(flatType) + 1);
            
            application.updateStatus(ApplicationStatus.UNSUCCESSFUL);
            System.out.println("Withdrawal request approved for application: " + application.getApplicationId());
        } else {
            System.out.println("Withdrawal request rejected for application: " + application.getApplicationId());
        }
    }
    
    // View all pending HDB Officer registrations
    public List<HdbOfficer> viewPendingOfficerRegistrations() {
        return projectsCreated.stream()
                .flatMap(p -> HdbOfficer.getAllOfficers().stream()
                        .filter(o -> o.gethandlingProject() != null && 
                                 o.gethandlingProject().getProjectID().equals(p.getProjectID()) && 
                                 "PENDING".equals(o.getRegistrationStatus())))
                .collect(Collectors.toList());
    }
    
    // View all approved HDB Officer registrations
    public List<HdbOfficer> viewApprovedOfficerRegistrations() {
        return projectsCreated.stream()
                .flatMap(p -> HdbOfficer.getAllOfficers().stream()
                        .filter(o -> o.gethandlingProject() != null && 
                                 o.gethandlingProject().getProjectID().equals(p.getProjectID()) && 
                                 "APPROVED".equals(o.getRegistrationStatus())))
                .collect(Collectors.toList());
    }
    
    // View all enquiries for all projects - making these "printable" as per feedback
    public String printAllEnquiries() {
        List<Enquiry> enquiries = EnquiryRepository.getAll();
        StringBuilder sb = new StringBuilder();
        sb.append("All Enquiries:\n");
        sb.append("=============\n\n");
        
        for (Enquiry e : enquiries) {
            sb.append("ID: ").append(e.getId()).append("\n");
            sb.append("Project: ").append(e.getProject().getProjectName()).append("\n");
            sb.append("From: ").append(e.getSubmitter().getName()).append("\n");
            sb.append("Status: ").append(e.getStatus()).append("\n");
            sb.append("Question: ").append(e.getQuestion()).append("\n");
            sb.append("Response: ").append(e.getResponse() != null ? e.getResponse() : "Not answered yet").append("\n\n");
        }
        
        return sb.toString();
    }
    
    // View enquiries for projects managed by this manager - making these "printable"
    public String printMyProjectsEnquiries() {
        List<Enquiry> enquiries = EnquiryRepository.getAll().stream()
                .filter(e -> e.getProject() != null && 
                          projectsCreated.stream().anyMatch(p -> 
                              p.getProjectID().equals(e.getProject().getProjectID())))
                .collect(Collectors.toList());
        
        StringBuilder sb = new StringBuilder();
        sb.append("Enquiries for My Projects:\n");
        sb.append("=========================\n\n");
        
        for (Enquiry e : enquiries) {
            sb.append("ID: ").append(e.getId()).append("\n");
            sb.append("Project: ").append(e.getProject().getProjectName()).append("\n");
            sb.append("From: ").append(e.getSubmitter().getName()).append("\n");
            sb.append("Status: ").append(e.getStatus()).append("\n");
            sb.append("Question: ").append(e.getQuestion()).append("\n");
            sb.append("Response: ").append(e.getResponse() != null ? e.getResponse() : "Not answered yet").append("\n\n");
        }
        
        return sb.toString();
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
