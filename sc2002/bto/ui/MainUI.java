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
 * Main entry point for the BTO Management System
 * Handles login, signup, and directing users to their respective interfaces
 */
public class MainUI {
    private static Scanner scanner = new Scanner(System.in);
    private static UserRepository userRepo = new UserRepository();
    private static ProjectRepository projectRepo = new ProjectRepository();
    private static ApplicationRepository applicationRepo = new ApplicationRepository();
    private static EnquiryRepository enquiryRepo = new EnquiryRepository();
    
    public static void main(String[] args) {
        System.out.println("Welcome to BTO Management System");
        
        // Load initial data
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
                            managerUI.run((HdbManager)currentUser, userRepo, projectRepo, applicationRepo, enquiryRepo);
                        } else if (currentUser instanceof HdbOfficer) {
                            OfficerUI officerUI = new OfficerUI();
                            officerUI.run((HdbOfficer)currentUser, userRepo, projectRepo, applicationRepo, enquiryRepo);
                        } else if (currentUser instanceof Applicant) {
                            ApplicantUI applicantUI = new ApplicantUI();
                            applicantUI.run((Applicant)currentUser, userRepo, projectRepo, applicationRepo, enquiryRepo);
                        }
                    }
                    break;
                case "2": // Signup
                    signup();
                    break;
                case "3": // Exit
                    exit = true;
                    break;
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        }
        
        System.out.println("Thank you for using BTO Management System. Goodbye!");
        scanner.close();
    }
    
    private static void loadInitialData() {
        try {
            // Load users from file
            System.out.println("Loading user data...");
            FileHandler.loadUsers(userRepo, applicationRepo, enquiryRepo);
            System.out.println("User data loaded successfully.");
            
            // Create some sample projects for demonstration
            createSampleProjects();
            
            System.out.println("System initialized successfully.");
        } catch (Exception e) {
            System.out.println("Error loading initial data: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private static void createSampleProjects() {
        try {
        	// Create three managers instead of one
            HdbManager manager1 = null;
            HdbManager manager2 = null;
            HdbManager manager3 = null;
            
            // Find existing managers
            for (User user : userRepo.getAll()) {
                if (user instanceof HdbManager) {
                	if (manager1 == null) {
                        manager1 = (HdbManager) user;
                    } else if (manager2 == null) {
                        manager2 = (HdbManager) user;
                    } else if (manager3 == null) {
                        manager3 = (HdbManager) user;
                    }
                }
            }
            
            if (manager1 == null) {
                // Create a manager if none exists
                manager1 = new HdbManager(
                    "S1234567A", 
                    "John Smith", 
                    "password", 
                    35, 
                    MaritalStatus.MARRIED, 
                    "Default Manager", 
                    120000.0
                );
                userRepo.add(manager1);
            }
            
            if (manager2 == null) {
                manager2 = new HdbManager(
                    "T8765432F",
                    "Manager Two",
                    "password",
                    36,
                    MaritalStatus.SINGLE,
                    "Default Manager",
                    125000.0
                );
                userRepo.add(manager2);
            }
            
            if (manager3 == null) {
                manager3 = new HdbManager(
                    "S5678901G",
                    "Manager Three",
                    "password",
                    26,
                    MaritalStatus.MARRIED,
                    "Default Manager",
                    130000.0
                );
                userRepo.add(manager3);
            }

            
            
            
            // Create sample projects
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            String currentDate = sdf.format(new Date());
            
            // Calculate application closing date (30 days from now)
            Date futureDate = new Date(System.currentTimeMillis() + 30L * 24 * 60 * 60 * 1000);
            String closingDate = sdf.format(futureDate);
            
            // Project 1: Yishun Meadows
            FlatType[] flatTypes1 = { FlatType.TWO_ROOM, FlatType.THREE_ROOM };
            manager1.createProject(
                "Yishun Meadows", 
                "Yishun", 
                flatTypes1, 
                50, // 2-room units
                30, // 3-room units
                currentDate, 
                closingDate,
                projectRepo
            );
            
            // Project 2: Tampines Greenview
            FlatType[] flatTypes2 = { FlatType.TWO_ROOM, FlatType.THREE_ROOM };
            manager2.createProject(
                "Tampines Greenview", 
                "Tampines", 
                flatTypes2, 
                40, // 2-room units
                60, // 3-room units
                currentDate, 
                closingDate,
                projectRepo
            );
            
            // Project 3: Woodlands Horizon
            FlatType[] flatTypes3 = { FlatType.TWO_ROOM };
            manager3.createProject(
                "Woodlands Horizon", 
                "Woodlands", 
                flatTypes3, 
                70, // 2-room units
                0, // No 3-room units
                currentDate, 
                closingDate,
                projectRepo
            );
            

            System.out.println("Sample projects created successfully.");
        } catch (Exception e) {
            System.out.println("Error creating sample projects: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private static void showLoginMenu() {
        System.out.println("\n===== BTO Management System =====");
        System.out.println("1. Login");
        System.out.println("2. Sign up");
        System.out.println("3. Exit");
        System.out.print("Enter your choice: ");
    }
    
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
    
    // private static void signup() {
    //     System.out.println("Sign up as a new user");
        
    //     System.out.print("Enter NRIC (e.g., S1234567A): ");
    //     String nric = scanner.nextLine();
        
    //     // Check if user already exists
    //     if (userRepo.getById(nric) != null) {
    //         System.out.println("A user with this NRIC already exists.");
    //         return;
    //     }
        
    //     System.out.print("Enter name: ");
    //     String name = scanner.nextLine();
        
    //     System.out.print("Enter password: ");
    //     String password = scanner.nextLine();
        
    //     System.out.print("Enter age: ");
    //     int age = Integer.parseInt(scanner.nextLine());
        
    //     System.out.print("Marital status (1: Single, 2: Married): ");
    //     int maritalChoice = Integer.parseInt(scanner.nextLine());
    //     MaritalStatus maritalStatus = (maritalChoice == 2) ? MaritalStatus.MARRIED : MaritalStatus.SINGLE;
        
    //     System.out.print("Monthly income: ");
    //     double income = Double.parseDouble(scanner.nextLine());
        
    //     // Create applicant by default (role assignment would be more complex in a real system)
    //     Applicant newUser = new Applicant(nric, name, password, age, maritalStatus, name, income);
    //     userRepo.add(newUser);
        
    //     System.out.println("Registration successful. Please login.");
    // }

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
        System.out.print("Enter Income Range: ");
        double income = Double.parseDouble(scanner.nextLine());

        User newUser = null;

        switch (roleChoice) {
            case "1":  // Applicant
                newUser = new Applicant(nric, name, password, age, maritalStatus, name, income);
                break;

            case "2":  // Officer
                newUser = new HdbOfficer(nric, name, password, age, maritalStatus, name, null, null,
                        OfficerRegistrationStatus.PENDING, null, applicationRepo, enquiryRepo);
                break;

            case "3":  // Manager
                newUser = new HdbManager(nric, name, password, age, maritalStatus, name, income);
                break;

            default:
                System.out.println("Invalid user type selected. Sign up failed.");
                return;
        }

        userRepo.add(newUser);
        System.out.println("Sign up successful! You may now log in as a " + newUser.getClass().getSimpleName());
    }

}
