package sc2002.bto.ui;

import java.util.Scanner;

import sc2002.bto.entity.User;
import sc2002.bto.repository.ApplicationRepository;
import sc2002.bto.repository.EnquiryRepository;
import sc2002.bto.repository.ProjectRepository;
import sc2002.bto.repository.UserRepository;

/**
 * Base class for all user interface classes
 * Contains common functionality shared by all user types
 */
public abstract class BaseUserUI {
    protected Scanner scanner;
    protected User currentUser;
    protected UserRepository userRepo;
    protected ProjectRepository projectRepo;
    protected ApplicationRepository applicationRepo;
    protected EnquiryRepository enquiryRepo;
    
    public BaseUserUI() {
        this.scanner = new Scanner(System.in);
    }
    
    /**
     * Initialize repositories and current user
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
     * Display additional profile information specific to each user type
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
        
        System.out.println("Password changed successfully.");
    }
    
    /**
     * Display menu for the specific user type
     */
    protected abstract void showMenu();
    
    /**
     * Handle menu choice for the specific user type
     * @param choice The user's menu choice
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
        }
        
        return false; // Return to login screen by default
    }
}
