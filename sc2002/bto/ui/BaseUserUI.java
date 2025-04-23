package sc2002.bto.ui;

import java.util.List;
import java.util.Scanner;
import sc2002.bto.entity.Project;
import sc2002.bto.entity.User;
import sc2002.bto.enums.FlatType;
import sc2002.bto.repository.ApplicationRepository;
import sc2002.bto.repository.EnquiryRepository;
import sc2002.bto.repository.ProjectRepository;
import sc2002.bto.repository.UserRepository;
import sc2002.bto.util.ProjectFilter;

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
    protected ProjectFilter projectFilter; // Filter object to store user preferences
    
    public BaseUserUI() {
        this.scanner = new Scanner(System.in);
        this.projectFilter = new ProjectFilter(); // Initialize with default settings
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
    
    /**
     * Common method to filter and display projects based on user preferences.
     * This method applies the current filter settings from projectFilter to the provided list
     * of projects, then displays the filtered results. It also offers the user options to
     * further refine the filter settings.
     * 
     * The filter settings are persisted in the projectFilter object, which means they will
     * be maintained when the user navigates between different UI screens.
     * 
     * @param title The title to display for the projects list
     * @param projects The list of projects to filter and display
     */
    protected void displayFilteredProjects(String title, List<Project> projects) {
        if (projects.isEmpty()) {
            System.out.println("No projects available.");
            return;
        }
        
        System.out.println("\n===== " + title + " =====");
        System.out.println("Current " + projectFilter);
        
        // Apply filters
        List<Project> filteredProjects = projectFilter.apply(projects);
        
        if (filteredProjects.isEmpty()) {
            System.out.println("No projects match your filter criteria.");
            return;
        }
        
        for (int i = 0; i < filteredProjects.size(); i++) {
            Project p = filteredProjects.get(i);
            System.out.println((i + 1) + ". " + p.getProjectName() + " (" + p.getNeighborhood() + ")");
            System.out.println("   Visibility: " + (p.isVisible() ? "Visible" : "Hidden"));
            System.out.println("   Application Period: " + p.getApplicationOpenDate() + " to " + p.getApplicationCloseDate());
            System.out.println("   Available Units:");
            System.out.println("   - 2-Room: " + p.getTwoRoomUnitsAvailable());
            System.out.println("   - 3-Room: " + p.getThreeRoomUnitsAvailable());
            System.out.println();
        }
        
        // Show filter options
        showFilterOptions(projects);
    }
    
    /**
     * Displays filter options to the user and processes their selection.
     * This method presents a menu of filtering and sorting options for projects:
     * - Filter by neighborhood
     * - Filter by flat type
     * - Sort by a selected field
     * - Toggle sort order between ascending and descending
     * - Reset all filters to default values
     * 
     * The user's selection is processed immediately and the filter settings are updated
     * accordingly in the projectFilter object, which persists between menu navigations.
     * 
     * @param projects The list of projects to extract filter options from (e.g., available neighborhoods)
     */
    protected void showFilterOptions(List<Project> projects) {
        System.out.println("\n===== Filter Options =====");
        System.out.println("1. Filter by Neighborhood");
        System.out.println("2. Filter by Flat Type");
        System.out.println("3. Sort by Field");
        System.out.println("4. Toggle Sort Order (Current: " + (projectFilter.isAscending() ? "Ascending" : "Descending") + ")");
        System.out.println("5. Reset Filters");
        System.out.println("6. Return to Menu");
        System.out.print("Enter your choice: ");
        
        String choice = scanner.nextLine();
        
        switch (choice) {
            case "1":
                filterByNeighborhood(projects);
                break;
            case "2":
                filterByFlatType();
                break;
            case "3":
                sortByField();
                break;
            case "4":
                projectFilter.setAscending(!projectFilter.isAscending());
                System.out.println("Sort order set to " + (projectFilter.isAscending() ? "Ascending" : "Descending"));
                break;
            case "5":
                projectFilter.reset();
                System.out.println("Filters reset to default values.");
                break;
            case "6":
                return;
            default:
                System.out.println("Invalid choice.");
                break;
        }
    }
    
    /**
     * Allows the user to filter projects by selecting a specific neighborhood.
     * This method:
     * 1. Extracts all unique neighborhoods from the provided list of projects
     * 2. Displays them as a numbered list to the user
     * 3. Prompts the user to select a neighborhood or clear the filter
     * 4. Updates the projectFilter object based on the user's selection
     * 
     * The neighborhood filter will persist between menu navigations as it's stored
     * in the projectFilter object.
     * 
     * @param projects The list of projects to extract available neighborhoods from
     */
    private void filterByNeighborhood(List<Project> projects) {
        // Collect all unique neighborhoods
        System.out.println("\n===== Neighborhoods =====");
        java.util.Set<String> neighborhoods = new java.util.HashSet<>();
        
        for (Project p : projects) {
            neighborhoods.add(p.getNeighborhood());
        }
        
        int i = 1;
        for (String n : neighborhoods) {
            System.out.println(i + ". " + n);
            i++;
        }
        
        System.out.println(i + ". Clear neighborhood filter");
        System.out.print("Select a neighborhood: ");
        
        try {
            int choice = Integer.parseInt(scanner.nextLine());
            
            if (choice == i) {
                // Clear filter
                projectFilter.setNeighborhood(null);
                System.out.println("Neighborhood filter cleared.");
            } else if (choice > 0 && choice < i) {
                // Set filter
                String selectedNeighborhood = (String) neighborhoods.toArray()[choice - 1];
                projectFilter.setNeighborhood(selectedNeighborhood);
                System.out.println("Neighborhood filter set to: " + selectedNeighborhood);
            } else {
                System.out.println("Invalid choice.");
            }
        } catch (NumberFormatException e) {
            System.out.println("Invalid input. Please enter a number.");
        }
    }
    
    /**
     * Allows the user to filter projects by selecting a specific flat type.
     * This method:
     * 1. Displays a list of available flat types (2-Room and 3-Room)
     * 2. Prompts the user to select a flat type or clear the filter
     * 3. Updates the projectFilter object based on the user's selection
     * 
     * The flat type filter will persist between menu navigations as it's stored
     * in the projectFilter object.
     */
    private void filterByFlatType() {
        System.out.println("\n===== Flat Types =====");
        System.out.println("1. 2-Room");
        System.out.println("2. 3-Room");
        System.out.println("3. Clear flat type filter");
        System.out.print("Select a flat type: ");
        
        String choice = scanner.nextLine();
        
        switch (choice) {
            case "1":
                projectFilter.setFlatType(FlatType.TWO_ROOM);
                System.out.println("Flat type filter set to: 2-Room");
                break;
            case "2":
                projectFilter.setFlatType(FlatType.THREE_ROOM);
                System.out.println("Flat type filter set to: 3-Room");
                break;
            case "3":
                projectFilter.setFlatType(null);
                System.out.println("Flat type filter cleared.");
                break;
            default:
                System.out.println("Invalid choice.");
                break;
        }
    }
    
    /**
     * Allows the user to select a field for sorting projects.
     * This method:
     * 1. Displays a list of available sort fields:
     *    - Project Name
     *    - Neighborhood
     *    - Application Opening Date
     *    - Application Closing Date
     *    - 2-Room Units Available
     *    - 3-Room Units Available
     * 2. Prompts the user to select a field
     * 3. Updates the projectFilter object with the selected sort field
     * 
     * The sort field selection will persist between menu navigations as it's stored
     * in the projectFilter object. The sort order (ascending/descending) can be
     * toggled separately.
     */
    private void sortByField() {
        System.out.println("\n===== Sort By =====");
        System.out.println("1. Project Name");
        System.out.println("2. Neighborhood");
        System.out.println("3. Application Opening Date");
        System.out.println("4. Application Closing Date");
        System.out.println("5. 2-Room Units Available");
        System.out.println("6. 3-Room Units Available");
        System.out.print("Select a field to sort by: ");
        
        String choice = scanner.nextLine();
        
        switch (choice) {
            case "1":
                projectFilter.setSortBy("name");
                System.out.println("Sorting by: Project Name");
                break;
            case "2":
                projectFilter.setSortBy("neighborhood");
                System.out.println("Sorting by: Neighborhood");
                break;
            case "3":
                projectFilter.setSortBy("opendate");
                System.out.println("Sorting by: Application Opening Date");
                break;
            case "4":
                projectFilter.setSortBy("closedate");
                System.out.println("Sorting by: Application Closing Date");
                break;
            case "5":
                projectFilter.setSortBy("tworoom");
                System.out.println("Sorting by: 2-Room Units Available");
                break;
            case "6":
                projectFilter.setSortBy("threeroom");
                System.out.println("Sorting by: 3-Room Units Available");
                break;
            default:
                System.out.println("Invalid choice. Sorting by Project Name.");
                projectFilter.setSortBy("name");
                break;
        }
    }
}
