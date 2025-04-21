package sc2002.bto.ui;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import sc2002.bto.entity.Applicant;
import sc2002.bto.entity.Enquiry;
import sc2002.bto.entity.Project;
import sc2002.bto.entity.User;
import sc2002.bto.enums.ApplicationStatus;
import sc2002.bto.enums.FlatType;
import sc2002.bto.enums.MaritalStatus;
import sc2002.bto.repository.ApplicationRepository;
import sc2002.bto.repository.EnquiryRepository;
import sc2002.bto.repository.ProjectRepository;
import sc2002.bto.repository.UserRepository;

/**
 * UI class for Applicant users in the BTO system.
 * Provides functionality for applicants to view projects, submit applications,
 * manage enquiries, and request withdrawals.
 * 
 */
public class ApplicantUI extends BaseUserUI {
    /** The applicant user */
    private Applicant applicant;
    
    @Override
    public boolean run(User user, UserRepository userRepo, ProjectRepository projectRepo, 
                      ApplicationRepository applicationRepo, EnquiryRepository enquiryRepo) {
        super.initialize(user, userRepo, projectRepo, applicationRepo, enquiryRepo);
        this.applicant = (Applicant) user;
        return super.run(user, userRepo, projectRepo, applicationRepo, enquiryRepo);
    }
    
    /**
     * Displays additional profile information specific to applicants.
     */
    @Override
    protected void displayAdditionalProfileInfo() {
        System.out.println("Income Range: $" + applicant.getIncomeRange());
        if (applicant.getBookedFlat() != null) {
            System.out.println("Booked Flat Type: " + applicant.getBookedFlat());
            System.out.println("Booked Project: " + applicant.getBookedProject().getProjectName());
        }
    }
    
    /**
     * Shows the menu options for applicant users.
     */
    @Override
    protected void showMenu() {
        System.out.println("\n===== BTO Management System: Applicant Menu =====");
        System.out.println("1. View Profile");
        System.out.println("2. Change Password");
        System.out.println("3. View Eligible Projects");
        System.out.println("4. Apply for a Project");
        System.out.println("5. View My Application Status");
        System.out.println("6. Request Withdrawal");
        System.out.println("7. Submit Enquiry");
        System.out.println("8. View My Enquiries");
        System.out.println("9. Logout");
        System.out.print("Enter your choice: ");
    }
    
    /**
     * Handles menu choices for applicant users.
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
                displayEligibleProjects();
                return false;
            case "4":
                boolean hasBooked = applicationRepo.getAll().stream()
                    .anyMatch(a -> a.getApplicant().equals(applicant) && a.getStatus() == ApplicationStatus.BOOKED);

                if (hasBooked) {
                    System.out.println("You have already booked a flat. Cannot apply for another.");
                    return false;
                }

                applyForProject();
                return false;
            case "5":
                applicant.viewMyApplicationStatus(applicationRepo);
                return false;
            case "6":
                requestWithdrawal();
                return false;
            case "7":
                submitEnquiry();
                return false;
            case "8":
                viewMyEnquiries();
                return false;
            case "9":
                System.out.println("Logging out...");
                return true;
            default:
                System.out.println("Invalid choice. Please try again.");
                return false;
        }
    }
    
    /**
     * Displays projects that the applicant is eligible to apply for.
     */
    private void displayEligibleProjects() {
        List<Project> eligibleProjects = applicant.viewEligibleProjects(projectRepo);
        
        if (eligibleProjects.isEmpty()) {
            System.out.println("No eligible projects found for your profile.");
            return;
        }
        
        System.out.println("\n===== Eligible Projects =====");
        for (int i = 0; i < eligibleProjects.size(); i++) {
            Project p = eligibleProjects.get(i);
            System.out.println((i + 1) + ". " + p.getProjectName() + " (" + p.getNeighborhood() + ")");
        }
    }
    
    /**
     * Handles the process of applying for a project.
     */
    private void applyForProject() {
        // Get eligible projects
        List<Project> eligibleProjects = applicant.viewEligibleProjects(projectRepo);
        
        if (eligibleProjects.isEmpty()) {
            System.out.println("No eligible projects found for your profile.");
            return;
        }
        
        // Display eligible projects
        System.out.println("\n===== Available Projects =====");
        for (int i = 0; i < eligibleProjects.size(); i++) {
            Project p = eligibleProjects.get(i);
            System.out.println((i + 1) + ". " + p.getProjectName() + " (" + p.getNeighborhood() + ")");
        }
        
        // Get user selection
        System.out.print("Select a project number: ");
        int projectChoice;
        try {
            projectChoice = Integer.parseInt(scanner.nextLine()) - 1;
        } catch (NumberFormatException e) {
            System.out.println("Invalid input. Please enter a number.");
            return;
        }
        
        if (projectChoice < 0 || projectChoice >= eligibleProjects.size()) {
            System.out.println("Invalid project selection.");
            return;
        }
        
        Project selectedProject = eligibleProjects.get(projectChoice);
        
        // Get available flat types based on marital status
        List<FlatType> availableFlatTypes = new ArrayList<>();
        
        if (applicant.getMaritalStatus() == MaritalStatus.MARRIED) {
            // Married applicants can apply for any flat types
            if (selectedProject.getUnitsAvailable(FlatType.TWO_ROOM) > 0) {
                availableFlatTypes.add(FlatType.TWO_ROOM);
            }
            if (selectedProject.getUnitsAvailable(FlatType.THREE_ROOM) > 0) {
                availableFlatTypes.add(FlatType.THREE_ROOM);
            }
        } else {
            // Single applicants can only apply for 2-room flats
            if (selectedProject.getUnitsAvailable(FlatType.TWO_ROOM) > 0) {
                availableFlatTypes.add(FlatType.TWO_ROOM);
            }
        }
        
        if (availableFlatTypes.isEmpty()) {
            System.out.println("No available flat types for this project.");
            return;
        }
        
        // Display available flat types
        System.out.println("\n===== Available Flat Types =====");
        for (int i = 0; i < availableFlatTypes.size(); i++) {
            FlatType type = availableFlatTypes.get(i);
            System.out.println((i + 1) + ". " + type + " (" + selectedProject.getUnitsAvailable(type) + " units available)");
        }
        
        // Get user selection
        System.out.print("Select a flat type: ");
        int flatTypeChoice;
        try {
            flatTypeChoice = Integer.parseInt(scanner.nextLine()) - 1;
        } catch (NumberFormatException e) {
            System.out.println("Invalid input. Please enter a number.");
            return;
        }
        
        if (flatTypeChoice < 0 || flatTypeChoice >= availableFlatTypes.size()) {
            System.out.println("Invalid flat type selection.");
            return;
        }
        
        FlatType selectedFlatType = availableFlatTypes.get(flatTypeChoice);
        
        // Submit the application
        applicant.submitApplication(selectedProject, selectedFlatType, applicationRepo, projectRepo);
        
        System.out.println("Application submitted successfully for " + selectedProject.getProjectName() + 
                          " with flat type " + selectedFlatType);
    }
    
    /**
     * Handles the process of requesting withdrawal of an application.
     */
    private void requestWithdrawal() {
        if (applicant.requestWithdrawal(applicationRepo)) {
            System.out.println("Withdrawal request submitted successfully. (awaiting HDB Manager's approval)");
        }
    }
    
    /**
     * Handles the process of submitting an enquiry.
     */
    private void submitEnquiry() {
        // Show all visible projects
        List<Project> visibleProjects = projectRepo.getAll().stream()
                                        .filter(Project::isVisible)
                                        .collect(Collectors.toList());
        
        if (visibleProjects.isEmpty()) {
            System.out.println("No visible projects available for enquiries.");
            return;
        }
        
        // Display projects
        System.out.println("\n===== Projects =====");
        for (int i = 0; i < visibleProjects.size(); i++) {
            Project p = visibleProjects.get(i);
            System.out.println((i + 1) + ". " + p.getProjectName() + " (" + p.getNeighborhood() + ")");
        }
        
        // Get project selection
        System.out.print("Select a project for your enquiry: ");
        int projectChoice;
        try {
            projectChoice = Integer.parseInt(scanner.nextLine()) - 1;
        } catch (NumberFormatException e) {
            System.out.println("Invalid input. Please enter a number.");
            return;
        }
        
        if (projectChoice < 0 || projectChoice >= visibleProjects.size()) {
            System.out.println("Invalid project selection.");
            return;
        }
        
        Project selectedProject = visibleProjects.get(projectChoice);
        
        // Get enquiry message
        System.out.println("Enter your enquiry:");
        String message = scanner.nextLine();
        
        if (message.trim().isEmpty()) {
            System.out.println("Enquiry message cannot be empty.");
            return;
        }
        
        // Submit enquiry
        Enquiry enquiry = applicant.submitEnquiry(selectedProject, message, enquiryRepo);
        
        System.out.println("Enquiry submitted successfully with ID: " + enquiry.getEnquiryId());
    }
    
    /**
     * Displays the applicant's enquiries and provides options to manage them.
     */
    private void viewMyEnquiries() {
        List<Enquiry> myEnquiries = applicant.viewMyEnquiries(enquiryRepo);
        
        if (myEnquiries.isEmpty()) {
            System.out.println("You haven't submitted any enquiries yet.");
            return;
        }
        
        // Display enquiries
        System.out.println("\n===== My Enquiries =====");
        for (int i = 0; i < myEnquiries.size(); i++) {
            Enquiry e = myEnquiries.get(i);
            System.out.println((i + 1) + ". Enquiry ID: " + e.getEnquiryId());
            System.out.println("   Project: " + e.getProject().getProjectID());
            System.out.println("   Status: " + e.getStatus());
            System.out.println("   Message: " + e.getMessage());
            
            if (e.getResponse() != null && !e.getResponse().isEmpty()) {
                System.out.println("   Response: " + e.getResponse());
            } else {
                System.out.println("   Response: Pending");
            }
            System.out.println();
        }
        
        // Get user action
        System.out.println("Do you want to edit or delete an enquiry? (1: Edit, 2: Delete, 3: Back)");
        String choice = scanner.nextLine();
        
        if (choice.equals("1")) {
            editEnquiry(myEnquiries);
        } else if (choice.equals("2")) {
            deleteEnquiry(myEnquiries);
        }
    }
    
    /**
     * Handles the process of editing an enquiry.
     * 
     * @param myEnquiries List of the applicant's enquiries
     */
    private void editEnquiry(List<Enquiry> myEnquiries) {
        System.out.print("Enter enquiry number to edit: ");
        int enquiryNum;
        try {
            enquiryNum = Integer.parseInt(scanner.nextLine()) - 1;
        } catch (NumberFormatException e) {
            System.out.println("Invalid input. Please enter a number.");
            return;
        }
        
        if (enquiryNum < 0 || enquiryNum >= myEnquiries.size()) {
            System.out.println("Invalid enquiry selection.");
            return;
        }
        
        Enquiry selectedEnquiry = myEnquiries.get(enquiryNum);
        
        if (!selectedEnquiry.isEditableOrDeletable()) {
            System.out.println("This enquiry cannot be edited because it has already been replied to.");
            return;
        }
        
        System.out.println("Current message: " + selectedEnquiry.getMessage());
        System.out.println("Enter new message:");
        String newMessage = scanner.nextLine();
        
        if (newMessage.trim().isEmpty()) {
            System.out.println("New message cannot be empty.");
            return;
        }
        
        applicant.editEnquiry(selectedEnquiry.getEnquiryId(), newMessage, enquiryRepo);
    }
    
    /**
     * Handles the process of deleting an enquiry.
     * 
     * @param myEnquiries List of the applicant's enquiries
     */
    private void deleteEnquiry(List<Enquiry> myEnquiries) {
        System.out.print("Enter enquiry number to delete: ");
        int enquiryNum;
        try {
            enquiryNum = Integer.parseInt(scanner.nextLine()) - 1;
        } catch (NumberFormatException e) {
            System.out.println("Invalid input. Please enter a number.");
            return;
        }
        
        if (enquiryNum < 0 || enquiryNum >= myEnquiries.size()) {
            System.out.println("Invalid enquiry selection.");
            return;
        }
        
        Enquiry selectedEnquiry = myEnquiries.get(enquiryNum);
        
        if (!selectedEnquiry.isEditableOrDeletable()) {
            System.out.println("This enquiry cannot be deleted because it has already been replied to.");
            return;
        }
        
        System.out.print("Are you sure you want to delete this enquiry? (Y/N): ");
        String confirm = scanner.nextLine();
        
        if (confirm.equalsIgnoreCase("Y")) {
            applicant.deleteEnquiry(selectedEnquiry.getEnquiryId(), enquiryRepo);
        }
    }
}
