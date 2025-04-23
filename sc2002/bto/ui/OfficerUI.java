package sc2002.bto.ui;

import java.util.List;
import java.util.stream.Collectors;
import sc2002.bto.entity.Application;
import sc2002.bto.entity.Enquiry;
import sc2002.bto.entity.HdbOfficer;
import sc2002.bto.entity.Project;
import sc2002.bto.entity.Receipt;
import sc2002.bto.entity.User;
import sc2002.bto.enums.ApplicationStatus;
import sc2002.bto.enums.EnquiryStatus;
import sc2002.bto.repository.ApplicationRepository;
import sc2002.bto.repository.EnquiryRepository;
import sc2002.bto.repository.ProjectRepository;
import sc2002.bto.repository.UserRepository;

/**
 * UI class for HDB Officer users in the BTO system.
 * Provides functionality for officers to view projects, register for projects,
 * process applications, generate receipts, and handle enquiries.
 * 
 */
public class OfficerUI extends BaseUserUI {
    /** The officer user */
    private HdbOfficer officer;

    /**
     * Constructs a new OfficerUI instance.
     * Initializes a UI controller for officer users.
     */
    public OfficerUI() {
        super();
    }

    /**
     * Initializes the OfficerUI with the current user and repositories,
     * then runs the UI loop.
     * 
     * @param user            The officer user
     * @param userRepo        The repository containing all users
     * @param projectRepo     The repository containing all projects
     * @param applicationRepo The repository containing all applications
     * @param enquiryRepo     The repository containing all enquiries
     * @return true if the application should exit, false to return to login
     */
    @Override
    public boolean run(User user, UserRepository userRepo, ProjectRepository projectRepo,
            ApplicationRepository applicationRepo, EnquiryRepository enquiryRepo) {
        super.initialize(user, userRepo, projectRepo, applicationRepo, enquiryRepo);
        this.officer = (HdbOfficer) user;
        return super.run(user, userRepo, projectRepo, applicationRepo, enquiryRepo);
    }

    /**
     * Displays additional profile information specific to officers.
     */
    @Override
    protected void displayAdditionalProfileInfo() {
        System.out.println("Officer Name: " + officer.getOfficerName());
        if (officer.getHandlingProject() != null) {
            System.out.println("Handling Project: " + officer.getHandlingProject().getProjectName());
            System.out.println("Registration Status: " + officer.getRegistrationStatus());
        }
    }

    /**
     * Shows the menu options for officer users.
     */
    @Override
    protected void showMenu() {
        System.out.println("\n===== BTO Management System: Officer Menu =====");
        System.out.println("1. View Profile");
        System.out.println("2. Change Password");
        System.out.println("3. View All Projects");
        System.out.println("4. View Project Details");
        System.out.println("5. Register for a Project");
        System.out.println("6. View My Registration Status");
        System.out.println("7. Process Application");
        System.out.println("8. Generate Receipt");
        System.out.println("9. View Enquiries");
        System.out.println("10. Respond to Enquiry");
        System.out.println("11. Apply for a Project (as Applicant)");
        System.out.println("12. Logout");
        System.out.print("Enter your choice: ");
    }

    /**
     * Handles menu choices for officer users.
     * 
     * @param choice The user's menu selection
     * @return true if the user wants to logout, false otherwise
     */
    @Override
    protected boolean handleMenuChoice(String choice) {
        switch (choice) {
            case "1":
                viewProfile();
                return false;
            case "2":
                changePassword();
                return false;
            case "3":
                displayAllProjects();
                return false;
            case "4":
                displayProjectDetails();
                return false;
            case "5":
                registerForProject();
                return false;
            case "6":
                System.out.println("Registration Status: " + officer.getRegistrationStatus());
                return false;
            case "7":
                processApplication();
                return false;
            case "8":
                generateReceipt();
                return false;
            case "9":
                officer.viewAllEnquiries();
                return false;
            case "10":
                respondToEnquiry();
                return false;
            case "11":
                System.out.println("This functionality requires additional implementation.");
                return false;
            case "12":
                System.out.println("Logging out...");
                return true;
            default:
                System.out.println("Invalid choice. Please try again.");
                return false;
        }
    }

    /**
     * Displays all projects with filtering capabilities for the HDB Officer.
     * This method:
     * 1. Retrieves all projects from the repository
     * 2. Applies the current filter settings (neighborhood, flat type, sorting)
     * 3. Displays the filtered list with options to further refine filters
     * 
     * Filter settings persist between menu navigations to maintain a consistent
     * user experience. The officer can view all projects regardless of visibility
     * settings, unlike applicants who can only see visible projects.
     */
    private void displayAllProjects() {
        List<Project> allProjects = projectRepo.getAll();

        if (allProjects.isEmpty()) {
            System.out.println("No projects available.");
            return;
        }

        // Use the base class method to display filtered projects
        displayFilteredProjects("All Projects", allProjects);
    }

    /**
     * Allows the officer to view detailed information about a selected project with
     * filtering support.
     * This method:
     * 1. Retrieves all projects from the repository
     * 2. Applies the current filter settings (neighborhood, flat type, sorting)
     * 3. Displays the filtered list with options to further refine filters
     * 4. Prompts the officer to select a project from the filtered list
     * 5. Displays detailed information about the selected project
     * 
     * Filter settings persist between menu navigations to maintain a consistent
     * user experience. Officers can see additional project details that are not
     * visible to regular applicants, especially for projects they are handling.
     */
    private void displayProjectDetails() {
        // First show all projects
        List<Project> allProjects = projectRepo.getAll();

        if (allProjects.isEmpty()) {
            System.out.println("No projects available.");
            return;
        }

        // Display projects with filters
        displayFilteredProjects("Projects", allProjects);

        // Apply filters and get the filtered list
        List<Project> filteredProjects = projectFilter.apply(allProjects);

        if (filteredProjects.isEmpty()) {
            System.out.println("No projects match the current filters.");
            return;
        }

        System.out.print("\nSelect a project to view details: ");
        int projectChoice;
        try {
            projectChoice = Integer.parseInt(scanner.nextLine()) - 1;
        } catch (NumberFormatException e) {
            System.out.println("Invalid input. Please enter a number.");
            return;
        }

        if (projectChoice < 0 || projectChoice >= filteredProjects.size()) {
            System.out.println("Invalid project selection.");
            return;
        }

        Project selectedProject = filteredProjects.get(projectChoice);

        // Use officer's viewProjectDetails method
        officer.viewProjectDetails(selectedProject);
    }

    /**
     * Allows the officer to register to handle a project with filtering support.
     * This method:
     * 1. Retrieves all projects with available officer slots
     * 2. Applies the current filter settings (neighborhood, flat type, sorting)
     * 3. Displays the filtered list with options to further refine filters
     * 4. Prompts the officer to select a project from the filtered list
     * 5. Submits a registration request for the selected project
     * 
     * Filter settings persist between menu navigations to maintain a consistent
     * user experience. The registration request will require approval from the
     * HDB Manager in charge of the selected project.
     */
    private void registerForProject() {
        // First show all projects with vacancies for officers
        List<Project> availableProjects = projectRepo.getAll().stream()
                .filter(p -> p.getAvailableOfficerSlots() > 0)
                .collect(Collectors.toList());

        if (availableProjects.isEmpty()) {
            System.out.println("No projects available for registration.");
            return;
        }

        // Display available projects with filters
        displayFilteredProjects("Available Projects for Registration", availableProjects);

        // Apply filters and get the filtered list
        List<Project> filteredProjects = projectFilter.apply(availableProjects);

        if (filteredProjects.isEmpty()) {
            System.out.println("No projects match the current filters.");
            return;
        }

        System.out.print("\nSelect a project to register for: ");
        int projectChoice;
        try {
            projectChoice = Integer.parseInt(scanner.nextLine()) - 1;
        } catch (NumberFormatException e) {
            System.out.println("Invalid input. Please enter a number.");
            return;
        }

        if (projectChoice < 0 || projectChoice >= filteredProjects.size()) {
            System.out.println("Invalid project selection.");
            return;
        }

        Project selectedProject = filteredProjects.get(projectChoice);

        // Call officer's method
        boolean registrationResult = officer.registerForProject(selectedProject);

        if (registrationResult) {
            System.out.println("Registration request submitted successfully for " + selectedProject.getProjectName());
            System.out.println("Your registration status is pending approval from the manager.");
        }
    }

    /**
     * Handles the process of processing an application.
     */
    private void processApplication() {
        if (officer.getHandlingProject() == null) {
            System.out.println("You are not handling any project yet.");
            return;
        }

        // Get applications for the officer's handling project
        List<Application> projectApplications = applicationRepo.getAll().stream()
                .filter(a -> a.getProject() != null &&
                        a.getProject().equals(officer.getHandlingProject()))
                .collect(Collectors.toList());

        if (projectApplications.isEmpty()) {
            System.out.println("No applications found for your handling project.");
            return;
        }

        System.out.println("\n===== Applications for " + officer.getHandlingProject().getProjectName() + " =====");
        for (int i = 0; i < projectApplications.size(); i++) {
            Application a = projectApplications.get(i);
            System.out.println((i + 1) + ". Application ID: " + a.getApplicationId());
            System.out.println("   Applicant: " + a.getApplicant().getName());
            System.out.println("   Flat Type: " + a.getSelectedFlatType());
            System.out.println("   Status: " + a.getStatus());
        }

        System.out.print("Select an application to process: ");
        int appChoice;
        try {
            appChoice = Integer.parseInt(scanner.nextLine()) - 1;
        } catch (NumberFormatException e) {
            System.out.println("Invalid input. Please enter a number.");
            return;
        }

        if (appChoice < 0 || appChoice >= projectApplications.size()) {
            System.out.println("Invalid application selection.");
            return;
        }

        Application selectedApplication = projectApplications.get(appChoice);

        // Call officer's method
        officer.processApplication(selectedApplication);
    }

    /**
     * Handles the process of generating a receipt.
     */
    private void generateReceipt() {
        if (officer.getHandlingProject() == null) {
            System.out.println("You are not handling any project yet.");
            return;
        }

        // Get applications with status SUCCESSFUL for the officer's handling project
        List<Application> successfulApplications = applicationRepo.getAll().stream()
                .filter(a -> a.getProject() != null &&
                        a.getProject().equals(officer.getHandlingProject()) &&
                        (a.getStatus() == ApplicationStatus.SUCCESSFUL || a.getStatus() == ApplicationStatus.BOOKED))
                .collect(Collectors.toList());

        if (successfulApplications.isEmpty()) {
            System.out.println("No successful applications found for receipt generation.");
            return;
        }

        System.out.println("\n===== Successful Applications =====");
        for (int i = 0; i < successfulApplications.size(); i++) {
            Application a = successfulApplications.get(i);
            System.out.println((i + 1) + ". Application ID: " + a.getApplicationId());
            System.out.println("   Applicant: " + a.getApplicant().getName());
            System.out.println("   Flat Type: " + a.getSelectedFlatType());
        }

        System.out.print("Select an application to generate receipt: ");
        int appChoice;
        try {
            appChoice = Integer.parseInt(scanner.nextLine()) - 1;
        } catch (NumberFormatException e) {
            System.out.println("Invalid input. Please enter a number.");
            return;
        }

        if (appChoice < 0 || appChoice >= successfulApplications.size()) {
            System.out.println("Invalid application selection.");
            return;
        }

        Application selectedApplication = successfulApplications.get(appChoice);

        // Call officer's method
        Receipt receipt = officer.generateReceipt(selectedApplication);

        if (receipt != null) {
            System.out.println("Receipt generated successfully with ID: " + receipt.getReceiptID());
        }
    }

    /**
     * Handles the process of responding to an enquiry.
     */
    private void respondToEnquiry() {
        if (officer.getHandlingProject() == null) {
            System.out.println("You are not handling any project yet.");
            return;
        }

        // Get pending enquiries for the officer's handling project
        List<Enquiry> pendingEnquiries = enquiryRepo.findByProject(officer.getHandlingProject()).stream()
                .filter(e -> e.getStatus() == EnquiryStatus.PENDING)
                .collect(Collectors.toList());

        if (pendingEnquiries.isEmpty()) {
            System.out.println("No pending enquiries found for your handling project.");
            return;
        }

        System.out.println("\n===== Pending Enquiries =====");
        for (int i = 0; i < pendingEnquiries.size(); i++) {
            Enquiry e = pendingEnquiries.get(i);
            System.out.println((i + 1) + ". Enquiry ID: " + e.getEnquiryId());
            System.out.println("   From: " + e.getApplicant().getName());
            System.out.println("   Message: " + e.getMessage());
        }

        System.out.print("Select an enquiry to respond to: ");
        int enquiryChoice;
        try {
            enquiryChoice = Integer.parseInt(scanner.nextLine()) - 1;
        } catch (NumberFormatException e) {
            System.out.println("Invalid input. Please enter a number.");
            return;
        }

        if (enquiryChoice < 0 || enquiryChoice >= pendingEnquiries.size()) {
            System.out.println("Invalid enquiry selection.");
            return;
        }

        Enquiry selectedEnquiry = pendingEnquiries.get(enquiryChoice);

        System.out.println("Enter your response:");
        String response = scanner.nextLine();

        if (response.trim().isEmpty()) {
            System.out.println("Response cannot be empty.");
            return;
        }

        // Call officer's method
        officer.respondToEnquiry(selectedEnquiry, response);

        System.out.println("Response submitted successfully.");
    }
}
