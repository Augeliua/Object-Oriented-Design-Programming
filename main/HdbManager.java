package sc2002.bto.entity;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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

public class HdbManager extends Applicant implements IProjectManagement, IEnquiryManagement {
    // Fields
    private String managerName;
    private List<Project> projectsCreated;
    
    // Constructor
    public HdbManager(String id, String name, String password, int age, MaritalStatus maritalStatus, 
                      String applicantName, Double incomeRange) {
        super(id, name, password, age, maritalStatus, applicantName, incomeRange);
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
        System.out.println("Project created successfully: " + project.getProjectID());
    }
    
    private Date parseDate(String dateStr) {
        try {
            return new java.text.SimpleDateFormat("yyyy-MM-dd").parse(dateStr);
        } catch (Exception e) {
            return new Date(); // Default to current date if parsing fails
        }
    }
    
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
    
    public void deleteProject(Project project) {
        boolean removed = projectsCreated.removeIf(p -> p.getProjectID().equals(project.getProjectID()));
        if (removed) {
            System.out.println("Project deleted successfully: " + project.getProjectID());
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
    
    public List<Project> viewAllProjects(ProjectRepository projectRepo) {
        return projectRepo.getAll();
    }
    
    public void approveOfficerRegistration(HdbOfficer officer) {
        // Find the project the officer is applying for
        Project targetProject = null;
        if (officer.getHandlingProject() != null) {
            for (Project p : projectsCreated) {
                if (p.getProjectID().equals(officer.getHandlingProject().getProjectID())) {
                    targetProject = p;
                    break;
                }
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
    }
    
    public void approveApplication(Application application) {
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
            // Decrease availability
            if (type == FlatType.TWO_ROOM) {
                project.setTwoRoomUnitsAvailable(project.getTwoRoomUnitsAvailable() - 1);
            } else if (type == FlatType.THREE_ROOM) {
                project.setThreeRoomUnitsAvailable(project.getThreeRoomUnitsAvailable() - 1);
            }
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
                if (a.isWithdrawalRequested()) {
                    a.updateStatus(ApplicationStatus.UNSUCCESSFUL);
                    System.out.println("Withdrawal processed for application: " + a.getApplicationId());
                    appRepo.update(a); // Save the new status
                    continue;
                }
                
                // Approve or reject based on flat availability
                FlatType type = a.getSelectedFlatType();
                Project project = a.getProject();
                
                int availableUnits = project.getUnitsAvailable(type);
                
                if (availableUnits > 0) {
                    a.updateStatus(ApplicationStatus.SUCCESSFUL);
                    // Decrease availability
                    if (type == FlatType.TWO_ROOM) {
                        project.setTwoRoomUnitsAvailable(project.getTwoRoomUnitsAvailable() - 1);
                    } else if (type == FlatType.THREE_ROOM) {
                        project.setThreeRoomUnitsAvailable(project.getThreeRoomUnitsAvailable() - 1);
                    }
                    System.out.println("Application approved: " + a.getApplicationId());
                } else {
                    a.updateStatus(ApplicationStatus.UNSUCCESSFUL);
                    System.out.println("Application rejected (no units): " + a.getApplicationId());
                }
                
                appRepo.update(a); // Save the new status
            }
        }
    }
    
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
    
    // Create a new project with details
    public void createProject(String projectName, String neighborhood, FlatType[] flatTypes, 
                             int twoRoomUnits, int threeRoomUnits, String openDate, String closeDate) {
        Project project = new Project(
            "PROJ-" + System.currentTimeMillis(), 
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
        
        createProject(project);
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
    
    // Handle applicant's withdrawal request
    public void handleWithdrawalRequest(Application application, boolean approve) {
        if (approve) {
            // Release the flat unit back to available pool
            Project project = application.getProject();
            FlatType flatType = application.getSelectedFlatType();
            
            if (flatType == FlatType.TWO_ROOM) {
                project.setTwoRoomUnitsAvailable(project.getTwoRoomUnitsAvailable() + 1);
            } else if (flatType == FlatType.THREE_ROOM) {
                project.setThreeRoomUnitsAvailable(project.getThreeRoomUnitsAvailable() + 1);
            }
            
            application.updateStatus(ApplicationStatus.UNSUCCESSFUL);
            System.out.println("Withdrawal request approved for application: " + application.getApplicationId());
        } else {
            System.out.println("Withdrawal request rejected for application: " + application.getApplicationId());
        }
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
    
    // View enquiries for projects managed by this manager
    public List<Enquiry> getMyProjectsEnquiries(EnquiryRepository enquiryRepo) {
        return enquiryRepo.getAll().stream()
                .filter(e -> e.getProject() != null && 
                          projectsCreated.stream().anyMatch(p -> 
                              p.getProjectID().equals(e.getProject().getProjectID())))
                .collect(Collectors.toList());
    }
    
    // Check if manager is handling any active projects
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
    
    // Override application method from Applicant to prevent HDB Managers from applying
    @Override
    public void submitApplication(Project project, FlatType flatType, ApplicationRepository appRepo, ProjectRepository projectRepo) {
        System.out.println("HDB Managers cannot apply for BTO projects.");
    }
