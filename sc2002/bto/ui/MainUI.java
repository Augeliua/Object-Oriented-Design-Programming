package sc2002.bto.ui;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Scanner;
import sc2002.bto.entity.Applicant;
import sc2002.bto.entity.HdbManager;
import sc2002.bto.entity.HdbOfficer;
import sc2002.bto.entity.User;
import sc2002.bto.enums.FlatType;
import sc2002.bto.enums.MaritalStatus;
import sc2002.bto.enums.OfficerRegistrationStatus;
import sc2002.bto.repository.ApplicationRepository;
import sc2002.bto.repository.EnquiryRepository;
import sc2002.bto.repository.ProjectRepository;
import sc2002.bto.repository.UserRepository;
import sc2002.bto.util.FileHandler;

/**
 * Main entry point for the BTO Management System.
 * Handles user login, signup, and directs users to their respective interfaces.
 * 
 */
public class MainUI {
    /** Scanner for reading user input */
    private static Scanner scanner = new Scanner(System.in);
    /** Repository for user data */
    private static UserRepository userRepo = new UserRepository();
    /** Repository for project data */
    private static ProjectRepository projectRepo = new ProjectRepository();
    /** Repository for application data */
    private static ApplicationRepository applicationRepo = new ApplicationRepository();
    /** Repository for enquiry data */
    private static EnquiryRepository enquiryRepo = new EnquiryRepository();

    /**
     * Main method that starts the application.
     * 
     * @param args Command line arguments (not used)
     */
    public static void main(String[] args) {
        System.out.println("Welcome to BTO Management System");

        // Load initial data from CSV files
        loadInitialData();

        // Main application loop
        boolean exit = false;
        while (!exit) {
            showLoginMenu();
            String choice = scanner.nextLine();

            switch (choice) {
                case "1": // Login
                    User currentUser = login();
                    if (currentUser != null) {
                        if (currentUser instanceof HdbManager) {
                            ManagerUI managerUI = new ManagerUI();
                            managerUI.run((HdbManager) currentUser, userRepo, projectRepo, applicationRepo,
                                    enquiryRepo);
                        } else if (currentUser instanceof HdbOfficer) {
                            OfficerUI officerUI = new OfficerUI();
                            officerUI.run((HdbOfficer) currentUser, userRepo, projectRepo, applicationRepo,
                                    enquiryRepo);
                        } else if (currentUser instanceof Applicant) {
                            ApplicantUI applicantUI = new ApplicantUI();
                            applicantUI.run((Applicant) currentUser, userRepo, projectRepo, applicationRepo,
                                    enquiryRepo);
                        }

                        // Save data after user interaction
                        saveData();
                    }
                    break;
                case "2": // Signup
                    signup();
                    // Save data after signup
                    saveData();
                    break;
                case "3": // Exit
                    exit = true;
                    break;
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        }

        // Final save before exit
        saveData();

        System.out.println("Thank you for using BTO Management System. Goodbye!");
        scanner.close();
    }

    /**
     * Loads initial data for the system.
     */
    private static void loadInitialData() {
        try {
            System.out.println("Loading data from CSV files...");
            boolean success = FileHandler.loadAllData(userRepo, projectRepo, applicationRepo, enquiryRepo);

            if (success) {
                System.out.println("Data loaded successfully from CSV files.");
            } else {
                // Check if repositories have data (default data was created)
                if (!userRepo.getAll().isEmpty() || !projectRepo.getAll().isEmpty()) {
                    System.out.println("Default data has been created and saved to CSV files.");
                } else {
                    System.out.println("Some errors occurred while loading data.");
                }
            }

            // Print user summary
            System.out.println(FileHandler.getUserSummary(userRepo));
        } catch (Exception e) {
            System.out.println("Error loading initial data: " + e.getMessage());
            e.printStackTrace();

            // Create default data as fallback
            System.out.println("Creating emergency default data...");
            FileHandler.loadUsers(userRepo, applicationRepo, enquiryRepo);
            createSampleProjects();
        }
    }

    /**
     * Save all data to CSV files
     */
    private static void saveData() {
        try {
            System.out.println("Saving data to CSV files...");
            boolean success = FileHandler.saveAllData(userRepo, projectRepo, applicationRepo, enquiryRepo);

            if (success) {
                System.out.println("Data saved successfully.");
            } else {
                System.out.println("Some errors occurred while saving data.");
            }
        } catch (Exception e) {
            System.out.println("Error saving data: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Displays the login menu.
     */
    private static void showLoginMenu() {
        System.out.println("\n===== BTO Management System =====");
        System.out.println("1. Login");
        System.out.println("2. Sign up");
        System.out.println("3. Exit");
        System.out.print("Enter your choice: ");
    }

    /**
     * Handles the user login process.
     * 
     * @return The authenticated user, or null if authentication fails
     */
    private static User login() {
        System.out.print("Enter NRIC: ");
        String nric = scanner.nextLine();

        System.out.print("Enter password: ");
        String password = scanner.nextLine();

        User user = userRepo.getById(nric);
        if (user != null && user.getPassword().equals(password)) {
            System.out.println("Login successful. Welcome, " + user.getName() + "!");
            return user;
        } else {
            System.out.println("Invalid credentials. Please try again.");
            return null;
        }
    }

    /**
     * Handles the user signup process.
     */
    private static void signup() {
        System.out.println("\n===== Sign Up =====");

        // Prompt for role
        System.out.println("Select user type:");
        System.out.println("1. Applicant");
        System.out.println("2. HDB Officer");
        System.out.println("3. HDB Manager");
        System.out.print("Enter choice: ");
        String roleChoice = scanner.nextLine();

        // Get common details
        System.out.print("Enter NRIC: ");
        String nric = scanner.nextLine();

        // Check if NRIC already exists
        if (userRepo.getById(nric) != null) {
            System.out.println("A user with this NRIC already exists. Please use a different NRIC.");
            return;
        }

        System.out.print("Enter Name: ");
        String name = scanner.nextLine();

        System.out.print("Enter Password: ");
        String password = scanner.nextLine();

        int age = -1;
        while (true) {
            System.out.print("Enter Age: ");
            try {
                age = Integer.parseInt(scanner.nextLine());
                if (age <= 0) {
                    System.out.println("Please enter a valid positive number.");
                } else {
                    break; // valid input
                }
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a number.");
            }
        }

        System.out.print("Enter Marital Status (SINGLE/MARRIED): ");
        String status = scanner.nextLine().toUpperCase();
        MaritalStatus maritalStatus = status.equals("MARRIED") ? MaritalStatus.MARRIED : MaritalStatus.SINGLE;

        // Optional income input
        double income = 0;
        while (true) {
            System.out.print("Enter Income Range: ");
            try {
                income = Double.parseDouble(scanner.nextLine());
                if (income < 0) {
                    System.out.println("Please enter a valid non-negative number.");
                } else {
                    break; // valid input
                }
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a number.");
            }
        }

        User newUser = null;

        switch (roleChoice) {
            case "1": // Applicant
                newUser = new Applicant(nric, name, password, age, maritalStatus, name, income);
                break;

            case "2": // Officer
                newUser = new HdbOfficer(nric, name, password, age, maritalStatus, name, null, null,
                        OfficerRegistrationStatus.PENDING, null, applicationRepo, enquiryRepo);
                break;

            case "3": // Manager
                newUser = new HdbManager(nric, name, password, age, maritalStatus, name, income);
                break;

            default:
                System.out.println("Invalid user type selected. Sign up failed.");
                return;
        }

        userRepo.add(newUser);
        System.out.println("Sign up successful! You may now log in as a " + newUser.getClass().getSimpleName());
    }

    /**
     * Creates sample projects for demonstration purposes.
     */
    private static void createSampleProjects() {
        try {
            // Find existing managers or create one if none exists
            HdbManager manager = null;
            for (User user : userRepo.getAll()) {
                if (user instanceof HdbManager) {
                    manager = (HdbManager) user;
                    break;
                }
            }

            if (manager == null) {
                // Create a manager if none exists
                manager = new HdbManager(
                        "S1234567A",
                        "John Smith",
                        "password",
                        45,
                        MaritalStatus.MARRIED,
                        "John Smith",
                        150000.0);
                userRepo.add(manager);
            }

            // Create sample project
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            String currentDate = sdf.format(new Date());

            // Calculate application closing date (30 days from now)
            Date futureDate = new Date(System.currentTimeMillis() + 30L * 24 * 60 * 60 * 1000);
            String closingDate = sdf.format(futureDate);

            // Project: Yishun Meadows
            FlatType[] flatTypes = { FlatType.TWO_ROOM, FlatType.THREE_ROOM };
            manager.createProject(
                    "Yishun Meadows",
                    "Yishun",
                    flatTypes,
                    50, // 2-room units
                    30, // 3-room units
                    currentDate,
                    closingDate,
                    projectRepo);

            System.out.println("Sample project created successfully.");
        } catch (Exception e) {
            System.out.println("Error creating sample project: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
