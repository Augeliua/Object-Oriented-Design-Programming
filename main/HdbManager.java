import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

public class HdbManager extends Applicant implements IProjectManagement, IEnquiryManagement {
    private String managerName;
    private List<Project> projectsCreated;

    // Constructor
    public HdbManager(String id, String name, String password, int age, boolean status, String managerName) {
        super(id, name, password, age, status);
        this.managerName = managerName;
        this.projectsCreated = new ArrayList<>();
    }

    // IProjectManagement implementation
    @Override
    public void createProject(Project project) {
        // Check if manager is already handling a project in an active application period
        if (isHandlingActiveProject() && isWithinApplicationPeriod(project)) {
            System.out.println("Cannot create new project. You are already handling a project within an application period.");
            return;
        }
        
        // Set the manager in charge
        project.setManagerInCharge(this.getName());
        projectsCreated.add(project);
        System.out.println("Project " + project.getProjectID() + " created successfully.");
    }

    @Override
    public void editProject(Project project) {
        int index = findProjectIndex(project);
        if (index != -1) {
            projectsCreated.set(index, project);
            System.out.println("Project " + project.getProjectID() + " updated successfully.");
        } else {
            System.out.println("Project not found or you don't have permission to edit it.");
        }
    }

    @Override
    public void deleteProject(Project project) {
        int index = findProjectIndex(project);
        if (index != -1) {
            projectsCreated.remove(index);
            System.out.println("Project " + project.getProjectID() + " deleted successfully.");
        } else {
            System.out.println("Project not found or you don't have permission to delete it.");
        }
    }

    @Override
    public void toggleProjectVisibility(Project project) {
        int index = findProjectIndex(project);
        if (index != -1) {
            project.setVisible(!project.isVisible());
            projectsCreated.set(index, project);
            System.out.println("Project " + project.getProjectID() + " visibility toggled to: " + (project.isVisible() ? "Visible" : "Hidden"));
        } else {
            System.out.println("Project not found or you don't have permission to toggle its visibility.");
        }
    }

    @Override
    public List<Project> viewAllProjects(Project project) {
        // This method should return all projects regardless of who created them
        // Implementation would depend on how projects are stored in the system
        // For now, returning the projects created by this manager
        return projectsCreated;
    }

    // IEnquiryManagement implementation
    @Override
    public void respondToEnquiry(Enquiry enquiry, String response) {
        if (enquiry != null) {
            enquiry.setResponse(response);
            enquiry.setStatus("RESPONDED");
            System.out.println("Response to enquiry " + enquiry.getEnquiryID() + " submitted successfully.");
        } else {
            System.out.println("Enquiry not found.");
        }
    }

    // HdbManager specific methods
    public void approveOfficerRegistration(HdbOfficer officer) {
        // Find the project the officer is applying for
        Project project = findProjectById(officer.getProjectId());
        
        if (project == null) {
            System.out.println("Project not found.");
            return;
        }
        
        // Check if this manager is in charge of the project
        if (!project.getManagerInCharge().equals(this.getName())) {
            System.out.println("You are not authorized to approve officers for this project.");
            return;
        }
        
        // Check if there are available slots for officers
        if (project.getAvailableOfficerSlots() > 0) {
            officer.setRegistrationStatus("APPROVED");
            project.setAvailableOfficerSlots(project.getAvailableOfficerSlots() - 1);
            System.out.println("Officer " + officer.getName() + " approved for project " + project.getProjectID());
        } else {
            System.out.println("No available officer slots for this project.");
        }
    }

    public void approveApplication(Application application) {
        Project project = application.getProject();
        
        // Check if this manager is in charge of the project
        if (!project.getManagerInCharge().equals(this.getName())) {
            System.out.println("You are not authorized to approve applications for this project.");
            return;
        }
        
        // Check if there are available units for the selected flat type
        String flatType = application.getSelectedFlatType();
        if (flatType.equals("2-ROOM") && project.getRemainingTwoRoom() > 0) {
            application.setApplicationStatus("APPROVED");
            project.setRemainingTwoRoom(project.getRemainingTwoRoom() - 1);
            System.out.println("Application for 2-ROOM approved.");
        } else if (flatType.equals("3-ROOM") && project.getRemainingThreeRoom() > 0) {
            application.setApplicationStatus("APPROVED");
            project.setRemainingThreeRoom(project.getRemainingThreeRoom() - 1);
            System.out.println("Application for 3-ROOM approved.");
        } else {
            application.setApplicationStatus("REJECTED");
            System.out.println("Application rejected due to no available units for the selected flat type.");
        }
    }

    public Report generateReport(ReportType reportType) {
        Report report = new Report();
        report.setReportID(generateReportId());
        report.setReportType(reportType);
        
        // Generate report data based on the report type
        List<Object> reportData = new ArrayList<>();
        
        switch (reportType.toString()) {
            case "ALL_BOOKINGS":
                // Get all applications
                // Implementation depends on how applications are stored
                break;
            case "BY_FLAT_TYPE":
                // Get applications filtered by flat type
                break;
            case "BY_MARITAL_STATUS":
                // Get applications filtered by marital status
                break;
            default:
                System.out.println("Invalid report type.");
        }
        
        report.setItems(reportData);
        return report;
    }
    
    // Helper methods
    private int findProjectIndex(Project project) {
        for (int i = 0; i < projectsCreated.size(); i++) {
            if (projectsCreated.get(i).getProjectID().equals(project.getProjectID())) {
                return i;
            }
        }
        return -1;
    }
    
    private Project findProjectById(String projectId) {
        for (Project project : projectsCreated) {
            if (project.getProjectID().equals(projectId)) {
                return project;
            }
        }
        return null;
    }
    
    private boolean isHandlingActiveProject() {
        Date currentDate = new Date();
        for (Project project : projectsCreated) {
            if (this.getName().equals(project.getManagerInCharge()) &&
                !currentDate.before(project.getApplicationOpenDate()) &&
                !currentDate.after(project.getApplicationCloseDate())) {
                return true;
            }
        }
        return false;
    }
    
    private boolean isWithinApplicationPeriod(Project newProject) {
        Date currentDate = new Date();
        return !currentDate.before(newProject.getApplicationOpenDate()) &&
               !currentDate.after(newProject.getApplicationCloseDate());
    }
    
    private String generateReportId() {
        // Simple implementation to generate a report ID
        return "RPT-" + System.currentTimeMillis();
    }
    
    // Additional methods based on the requirements
    public List<Project> viewAllProjects() {
        // This would typically fetch all projects from a repository or database
        // For now, returning the projects created by this manager
        return projectsCreated;
    }
    
    public List<Project> viewMyProjects() {
        return projectsCreated;
    }
    
    public void approveWithdrawalRequest(Application application) {
        Project project = application.getProject();
        
        // Check if this manager is in charge of the project
        if (!project.getManagerInCharge().equals(this.getName())) {
            System.out.println("You are not authorized to approve withdrawal requests for this project.");
            return;
        }
        
        // Process withdrawal request
        String flatType = application.getSelectedFlatType();
        application.setApplicationStatus("WITHDRAWN");
        
        // Return the unit back to available units
        if (flatType.equals("2-ROOM")) {
            project.setRemainingTwoRoom(project.getRemainingTwoRoom() + 1);
        } else if (flatType.equals("3-ROOM")) {
            project.setRemainingThreeRoom(project.getRemainingThreeRoom() + 1);
        }
        
        System.out.println("Withdrawal request approved for application: " + application.getApplicationId());
    }
    
    public List<Enquiry> viewAllEnquiries() {
        // This would typically fetch all enquiries from a repository or database
        // Implementation depends on how enquiries are stored
        return new ArrayList<>(); // Placeholder
    }
    
    public List<Enquiry> viewMyProjectEnquiries() {
        // Get enquiries for projects managed by this manager
        List<Enquiry> allEnquiries = viewAllEnquiries();
        return allEnquiries.stream()
            .filter(enquiry -> {
                Project project = enquiry.getProject();
                return project != null && project.getManagerInCharge().equals(this.getName());
            })
            .collect(Collectors.toList());
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
}
