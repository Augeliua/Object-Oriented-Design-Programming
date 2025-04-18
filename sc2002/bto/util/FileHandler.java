package sc2002.bto.util;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import sc2002.bto.entity.Applicant;
import sc2002.bto.entity.HdbManager;
import sc2002.bto.entity.HdbOfficer;
import sc2002.bto.entity.User;
import sc2002.bto.enums.MaritalStatus;
import sc2002.bto.enums.OfficerRegistrationStatus;
import sc2002.bto.repository.ApplicationRepository;
import sc2002.bto.repository.EnquiryRepository;
import sc2002.bto.repository.UserRepository;

/**
 * Utility class to handle file operations for the BTO System
 */
public class FileHandler {
    
    /**
     * Load users from files into the user repository
     * 
     * @param userRepo The user repository to populate
     * @return The number of users loaded
     * @throws IOException If an error occurs while reading the file
     */
    public static int loadUsers(UserRepository userRepo, ApplicationRepository applicationRepo, EnquiryRepository enquiryRepo) throws IOException {
        // For demonstration purposes, create some default users
        // In a real implementation, this would read from a file
        int count = createDefaultUsers(userRepo, applicationRepo, enquiryRepo);
        return count;
    }
    
    /**
     * Create some default users for demonstration
     * 
     * @param userRepo The user repository to populate
     * @return The number of users created
     */
    private static int createDefaultUsers(UserRepository userRepo,ApplicationRepository applicationRepo, EnquiryRepository enquiryRepo) {
        int count = 0;
        List<User> defaultUsers = new ArrayList<>();
        
        try {
            // Create a manager
            HdbManager manager1 = new HdbManager(
                "S1234567A", 
                "John Smith", 
                "password", 
                45, 
                MaritalStatus.MARRIED, 
                "John Smith", 
                150000.0
            );
            defaultUsers.add(manager1);
            count++;
            
            HdbManager manager2 = new HdbManager(
            		"T8765432F",
            		"Michael",
                    "password",
                    36,
                    MaritalStatus.SINGLE,
                    "Default Manager",
                    125000.0
                );
                defaultUsers.add(manager2);
                count++;
                
           HdbManager manager3 = new HdbManager(
        		   "S5678901G",
                	"Jessica",
                    "password",
                     26,
                     MaritalStatus.MARRIED,
                    "Default Manager",
                     125000.0
                    );
               defaultUsers.add(manager3);
               count++;             
                
            
            
            // Create some officers
            HdbOfficer officer1 = new HdbOfficer(
                "S2345678B", 
                "Jane Doe", 
                "password", 
                35, 
                MaritalStatus.MARRIED, 
                "Jane Doe", 
                null,
                null,// No project assigned yet
                OfficerRegistrationStatus.PENDING,
                manager3,
                applicationRepo,
                enquiryRepo   // Will be set later
            );
            defaultUsers.add(officer1);
            count++;
            
            HdbOfficer officer2 = new HdbOfficer(
                "S3456789C", 
                "Bob Johnson", 
                "password", 
                40, 
                MaritalStatus.SINGLE, 
                "Bob Johnson", 
                null,//pending project
                null,// handling project
                OfficerRegistrationStatus.PENDING,
                manager2,
                applicationRepo,  
                enquiryRepo   
            );
            defaultUsers.add(officer2);
            count++;
            
            // Create some applicants
            // Married applicants
            Applicant applicant1 = new Applicant(
                "S4567890D", 
                "Alice Wong", 
                "password", 
                30, 
                MaritalStatus.MARRIED, 
                "Alice Wong", 
                80000.0
            );
            defaultUsers.add(applicant1);
            count++;
            
            Applicant applicant2 = new Applicant(
                "S5678901E", 
                "Michael Tan", 
                "password", 
                28, 
                MaritalStatus.MARRIED, 
                "Michael Tan", 
                90000.0
            );
            defaultUsers.add(applicant2);
            count++;
            
            // Single applicants
            Applicant applicant3 = new Applicant(
                "S6789012F", 
                "Sarah Lim", 
                "password", 
                38, 
                MaritalStatus.SINGLE, 
                "Sarah Lim", 
                70000.0
            );
            defaultUsers.add(applicant3);
            count++;
            
            Applicant applicant4 = new Applicant(
                "S7890123G", 
                "David Chen", 
                "password", 
                36, 
                MaritalStatus.SINGLE, 
                "David Chen", 
                75000.0
            );
            defaultUsers.add(applicant4);
            count++;
            
            // Single applicant below 35 (ineligible for BTO)
            Applicant applicant5 = new Applicant(
                "S8901234H", 
                "Emma Lee", 
                "password", 
                32, 
                MaritalStatus.SINGLE, 
                "Emma Lee", 
                65000.0
            );
            defaultUsers.add(applicant5);
            count++;
            
            // Add all users to the repository
            for (User user : defaultUsers) {
                userRepo.add(user);
            }
        } catch (Exception e) {
            // Log the error but don't print to console
            e.printStackTrace();
        }
        
        return count;
    }
    
    /**
     * Read users from a CSV file
     * 
     * @param filePath The path to the CSV file
     * @param userRepo The user repository to populate
     * @param appRepo The application repository (for officers)
     * @param enqRepo The enquiry repository (for officers)
     * @return The number of users loaded
     * @throws IOException If an error occurs while reading the file
     */
    public static int readUserCsvFile(String filePath, UserRepository userRepo, 
                                      ApplicationRepository appRepo, EnquiryRepository enqRepo) throws IOException {
        int count = 0;
        
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            // Skip header line
            br.readLine();
            
            while ((line = br.readLine()) != null) {
                String[] data = line.split(",");
                if (data.length < 6) {
                    continue; // Skip invalid lines
                }
                
                String id = data[0].trim();
                String name = data[1].trim();
                String password = data[2].trim();
                int age = Integer.parseInt(data[3].trim());
                MaritalStatus maritalStatus = data[4].trim().equalsIgnoreCase("MARRIED") ? 
                                             MaritalStatus.MARRIED : MaritalStatus.SINGLE;
                String role = data[5].trim();
                
                User user = null;
                
                switch (role.toUpperCase()) {
                    case "MANAGER":
                        user = new HdbManager(
                            id, name, password, age, maritalStatus, name, 
                            Double.parseDouble(data[6].trim())
                        );
                        break;
                    case "OFFICER":
                        user = new HdbOfficer(
                            id, name, password, age, maritalStatus, name, 
                            null, //No pending project on file
                            null,// No project assigned yet
                            OfficerRegistrationStatus.PENDING,
                            null,
                            appRepo,
                            enqRepo
                        );
                        break;
                    case "APPLICANT":
                        user = new Applicant(
                            id, name, password, age, maritalStatus, name, 
                            Double.parseDouble(data[6].trim())
                        );
                        break;
                    default:
                        // Skip unknown roles
                        continue;
                }
                
                userRepo.add(user);
                count++;
            }
        }
        
        return count;
    }
    
    /**
     * Gets a summary of loaded users
     * 
     * @param userRepo The user repository
     * @return A summary string with counts of different user types
     */
    public static String getUserSummary(UserRepository userRepo) {
        List<User> users = userRepo.getAll();
        int managerCount = 0;
        int officerCount = 0;
        int applicantCount = 0;
        
        for (User user : users) {
            if (user instanceof HdbManager) {
                managerCount++;
            } else if (user instanceof HdbOfficer) {
                officerCount++;
            } else if (user instanceof Applicant) {
                applicantCount++;
            }
        }
        
        StringBuilder sb = new StringBuilder();
        sb.append("User Summary:\n");
        sb.append("Total Users: ").append(users.size()).append("\n");
        sb.append("Managers: ").append(managerCount).append("\n");
        sb.append("Officers: ").append(officerCount).append("\n");
        sb.append("Applicants: ").append(applicantCount);
        
        return sb.toString();
    }
}
