package sc2002.bto.ui;

import java.util.Scanner;
import sc2002.bto.entity.User;
import sc2002.bto.repository.ApplicationRepository;
import sc2002.bto.repository.EnquiryRepository;
import sc2002.bto.repository.ProjectRepository;
import sc2002.bto.repository.UserRepository;
import sc2002.bto.util.FileHandler;

/**
 * Abstract base class for all user interface classes in the BTO system.
 * Contains common functionality shared by all user types including
 * profile viewing and password changing.
 * 
 */
public abstract class BaseUserUI {
    /** Scanner for reading user input */
    protected Scanner scanner;
    /** Currently logged in user */
    protected User currentUser;
    /** Repository for user data */
    protected UserRepository userRepo;
    /** Repository for project data */
    protected ProjectRepository projectRepo;
    /** Repository for application data */
    protected ApplicationRepository applicationRepo;
    /** Repository for enquiry data */
    protected EnquiryRepository enquiryRepo;
    
    
    public BaseUserUI() {
        this.scanner = new Scanner(System.in);
    }
    
    /**
     * Initializes repositories and current user.
     * 
     * @param user Currently logged in user
     * @param userRepo Repository for user data
     * @param projectRepo Repository for project data
     * @param applicationRepo Repository for application data
     * @param enquiryRepo Repository for enquiry data
     */
    protected void initialize(User user, UserRepository userRepo, ProjectRepository projectRepo, 
                              ApplicationRepository applicationRepo, EnquiryRepository enquiryRepo) {
        this.currentUser = user;
        this.userRepo = userRepo;
        this.projectRepo = projectRepo;
        this.applicationRepo = applicationRepo;
        this.enquiryRepo = enquiryRepo;
    }
    
    /**
     * View user profile
     */
    protected void viewProfile() {
        System.out.println("\n===== User Profile =====");
        System.out.println("NRIC: " + currentUser.getId());
        System.out.println("Name: " + currentUser.getName());
        System.out.println("Age: " + currentUser.getAge());
        System.out.println("Marital Status: " + currentUser.getMaritalStatus());
        
        // Additional profile details will be implemented in subclasses
        displayAdditionalProfileInfo();
    }
    
    /**
     * Displays additional profile information specific to each user type.
     * Each subclass must implement this to show role-specific information.
     */
    protected abstract void displayAdditionalProfileInfo();
    
    /**
     * Change password functionality
     */
    protected void changePassword() {
        System.out.print("Enter current password: ");
        String currentPassword = scanner.nextLine();
        
        if (!currentUser.getPassword().equals(currentPassword)) {
            System.out.println("Current password is incorrect.");
            return;
        }
        
        System.out.print("Enter new password: ");
        String newPassword = scanner.nextLine();
        
        System.out.print("Confirm new password: ");
        String confirmPassword = scanner.nextLine();
        
        if (!newPassword.equals(confirmPassword)) {
            System.out.println("Passwords do not match.");
            return;
        }
        
        currentUser.setPassword(newPassword);
        userRepo.update(currentUser);
        
        // Save changes to ensure password change persists
        FileHandler.saveAllData(userRepo, projectRepo, applicationRepo, enquiryRepo);
        
        System.out.println("Password changed successfully.");
    }
    
    /**
     * Shows the menu options specific to each user type.
     * Each subclass must implement this to display appropriate menu items.
     */
    protected abstract void showMenu();
    
    /**
     * Handles menu choices for the specific user type.
     * 
     * @param choice The user's menu selection
     * @return true if the user wants to logout, false otherwise
     */
    protected abstract boolean handleMenuChoice(String choice);
    
    /**
     * Run the UI loop for this user type
     * @return true if the application should exit, false to return to login
     */
    public boolean run(User user, UserRepository userRepo, ProjectRepository projectRepo, 
                     ApplicationRepository applicationRepo, EnquiryRepository enquiryRepo) {
        initialize(user, userRepo, projectRepo, applicationRepo, enquiryRepo);
        
        boolean logout = false;
        while (!logout) {
            showMenu();
            String choice = scanner.nextLine();
            logout = handleMenuChoice(choice);
            
            // Save after each significant action
            if (!logout) {
                FileHandler.saveAllData(userRepo, projectRepo, applicationRepo, enquiryRepo);
            }
        }
        
        // Final save before logout
        FileHandler.saveAllData(userRepo, projectRepo, applicationRepo, enquiryRepo);
        
        return false; // Return to login screen by default
    }
}
