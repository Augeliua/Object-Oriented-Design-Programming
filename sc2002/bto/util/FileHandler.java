package sc2002.bto.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import sc2002.bto.entity.Applicant;
import sc2002.bto.entity.Application;
import sc2002.bto.entity.Enquiry;
import sc2002.bto.entity.HdbManager;
import sc2002.bto.entity.HdbOfficer;
import sc2002.bto.entity.Project;
import sc2002.bto.entity.User;
import sc2002.bto.enums.ApplicationStatus;
import sc2002.bto.enums.FlatType;
import sc2002.bto.enums.MaritalStatus;
import sc2002.bto.enums.OfficerRegistrationStatus;
import sc2002.bto.repository.ApplicationRepository;
import sc2002.bto.repository.EnquiryRepository;
import sc2002.bto.repository.ProjectRepository;
import sc2002.bto.repository.UserRepository;

/**
 * Utility class to handle file operations for the BTO System
 * Handles both reading from and writing to CSV files for data persistence
 */
public class FileHandler {
    private static final String DATA_DIR = "data/";
    // Constants for file paths
    private static final String APPLICANT_FILE = DATA_DIR + "ApplicantList.csv";
    private static final String MANAGER_FILE = DATA_DIR + "ManagerList.csv";
    private static final String OFFICER_FILE = DATA_DIR + "OfficerList.csv";
    private static final String PROJECT_FILE = DATA_DIR + "ProjectList.csv";
    private static final String APPLICATION_FILE = DATA_DIR + "ApplicationList.csv";
    private static final String ENQUIRY_FILE = DATA_DIR + "EnquiryList.csv";
    private static final String RECEIPT_FILE = DATA_DIR + "ReceiptList.csv";

    // Date formatter for consistent date format handling
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");

    /**
     * Load all data from CSV files
     * 
     * @param userRepo        The user repository to populate
     * @param projectRepo     The project repository to populate
     * @param applicationRepo The application repository to populate
     * @param enquiryRepo     The enquiry repository to populate
     * @return True if all data loaded successfully, false otherwise
     */
    public static boolean loadAllData(UserRepository userRepo, ProjectRepository projectRepo,
            ApplicationRepository applicationRepo, EnquiryRepository enquiryRepo) {
        try {
            // First ensure data directory exists
            ensureDataDirectoryExists();

            // Then ensure all files exist, create them if they don't
            ensureAllFilesExist();

            // Load users (applicants, managers, officers)
            int userCount = loadUsers(userRepo, applicationRepo, enquiryRepo);

            // Load projects
            int projectCount = loadProjects(projectRepo, userRepo);

            // Load applications
            int applicationCount = loadApplications(applicationRepo, userRepo, projectRepo);

            // Load enquiries
            int enquiryCount = loadEnquiries(enquiryRepo, userRepo, projectRepo);

            // If no data was loaded, create default data
            if (userCount == 0 && projectCount == 0) {
                System.out.println("No data found in CSV files. Creating default data...");
                createDefaultData(userRepo, projectRepo, applicationRepo, enquiryRepo);
                // Save the default data to CSV files
                saveAllData(userRepo, projectRepo, applicationRepo, enquiryRepo);
                return false; // Return false to indicate default data was created
            }

            return true;
        } catch (Exception e) {
            System.err.println("Error loading data: " + e.getMessage());
            e.printStackTrace();

            // If loading fails, create some default data
            createDefaultData(userRepo, projectRepo, applicationRepo, enquiryRepo);
            return false;
        }
    }

    private static void ensureDataDirectoryExists() throws IOException {
        File dataDir = new File(DATA_DIR);
        if (!dataDir.exists()) {
            boolean created = dataDir.mkdirs();
            if (!created) {
                throw new IOException("Failed to create data directory: " + DATA_DIR);
            }
            System.out.println("Created data directory: " + DATA_DIR);
        }
    }

    /**
     * Ensure all required CSV files exist, create them if they don't
     */
    private static void ensureAllFilesExist() throws IOException {
        String[] files = {
                APPLICANT_FILE, MANAGER_FILE, OFFICER_FILE, PROJECT_FILE,
                APPLICATION_FILE, ENQUIRY_FILE, RECEIPT_FILE
        };

        for (String file : files) {
            File f = new File(file);
            if (!f.exists()) {
                f.createNewFile();

                // Write headers to the new file
                try (BufferedWriter writer = new BufferedWriter(new FileWriter(f))) {
                    switch (file) {
                        case APPLICANT_FILE:
                            writer.write("ID,Name,Password,Age,MaritalStatus,Income\n");
                            break;
                        case MANAGER_FILE:
                            writer.write("ID,Name,Password,Age,MaritalStatus,Income\n");
                            break;
                        case OFFICER_FILE:
                            writer.write(
                                    "ID,Name,Password,Age,MaritalStatus,HandlingProjectID,RegistrationStatus,RegisteringManager\n");
                            break;
                        case PROJECT_FILE:
                            writer.write(
                                    "ProjectID,ProjectName,Neighborhood,FlatTypes,FloorCount,PricePerFlat,ThresholdPrice,"
                                            +
                                            "OpenDate,CloseDate,Visible,OfficerSlots,TwoRoomUnits,ThreeRoomUnits,ManagerInCharge\n");
                            break;
                        case APPLICATION_FILE:
                            writer.write(
                                    "ApplicationID,ApplicantID,ProjectID,ApplicationDate,Status,FlatType,WithdrawalRequested\n");
                            break;
                        case ENQUIRY_FILE:
                            writer.write("EnquiryID,ProjectID,ApplicantID,Message,Response,Status\n");
                            break;
                        case RECEIPT_FILE:
                            writer.write(
                                    "ReceiptID,Name,NRIC,Age,MaritalStatus,ProjectID,Neighborhood,Price,FlatType,BookingDate\n");
                            break;
                    }
                }
            }
        }
    }

    /**
     * Load users (applicants, managers, officers) from CSV files
     */
    public static int loadUsers(UserRepository userRepo, ApplicationRepository applicationRepo,
            EnquiryRepository enquiryRepo) {
        int count = 0;

        try {
            // Load Applicants
            count += loadApplicants(userRepo);

            // Load Managers
            count += loadManagers(userRepo);

            // Load Officers
            count += loadOfficers(userRepo, applicationRepo, enquiryRepo);

            return count;
        } catch (Exception e) {
            System.err.println("Error loading users: " + e.getMessage());
            e.printStackTrace();

            // If loading fails, create default users
            count = createDefaultUsers(userRepo, applicationRepo, enquiryRepo);
            return count;
        }
    }

    /**
     * Load applicants from CSV file
     */
    private static int loadApplicants(UserRepository userRepo) throws IOException {
        int count = 0;
        File file = new File(APPLICANT_FILE);

        if (!file.exists() || file.length() == 0) {
            return 0;
        }

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            // Skip header
            String header = br.readLine();
            if (header == null)
                return 0;

            String line;
            while ((line = br.readLine()) != null) {
                String[] data = line.split(",");
                if (data.length < 6)
                    continue;

                String id = data[0].trim();
                String name = data[1].trim();
                String password = data[2].trim();
                int age = Integer.parseInt(data[3].trim());
                MaritalStatus status = data[4].trim().equalsIgnoreCase("MARRIED") ? MaritalStatus.MARRIED
                        : MaritalStatus.SINGLE;
                double income = Double.parseDouble(data[5].trim());

                Applicant applicant = new Applicant(id, name, password, age, status, name, income);
                userRepo.add(applicant);
                count++;
            }
        }

        return count;
    }

    /**
     * Load managers from CSV file
     */
    private static int loadManagers(UserRepository userRepo) throws IOException {
        int count = 0;
        File file = new File(MANAGER_FILE);

        if (!file.exists() || file.length() == 0) {
            return 0;
        }

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            // Skip header
            String header = br.readLine();
            if (header == null)
                return 0;

            String line;
            while ((line = br.readLine()) != null) {
                String[] data = line.split(",");
                if (data.length < 6)
                    continue;

                String id = data[0].trim();
                String name = data[1].trim();
                String password = data[2].trim();
                int age = Integer.parseInt(data[3].trim());
                MaritalStatus status = data[4].trim().equalsIgnoreCase("MARRIED") ? MaritalStatus.MARRIED
                        : MaritalStatus.SINGLE;
                double income = Double.parseDouble(data[5].trim());

                HdbManager manager = new HdbManager(id, name, password, age, status, name, income);
                userRepo.add(manager);
                count++;
            }
        }

        return count;
    }

    /**
     * Load officers from CSV file
     */
    private static int loadOfficers(UserRepository userRepo,
            ApplicationRepository appRepo,
            EnquiryRepository enqRepo) throws IOException {
        int count = 0;
        File file = new File(OFFICER_FILE);

        if (!file.exists() || file.length() == 0) {
            return 0;
        }

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            // Skip header
            String header = br.readLine();
            if (header == null)
                return 0;

            String line;
            while ((line = br.readLine()) != null) {
                String[] data = line.split(",");
                if (data.length < 7)
                    continue;

                String id = data[0].trim();
                String name = data[1].trim();
                String password = data[2].trim();
                int age = Integer.parseInt(data[3].trim());
                MaritalStatus status = data[4].trim().equalsIgnoreCase("MARRIED") ? MaritalStatus.MARRIED
                        : MaritalStatus.SINGLE;

                // Project and status will be set after loading projects
                OfficerRegistrationStatus regStatus = OfficerRegistrationStatus.PENDING;
                if (data.length > 6) {
                    String statusStr = data[6].trim();
                    if (statusStr.equalsIgnoreCase("APPROVED")) {
                        regStatus = OfficerRegistrationStatus.APPROVED;
                    } else if (statusStr.equalsIgnoreCase("REJECTED")) {
                        regStatus = OfficerRegistrationStatus.REJECTED;
                    }
                }

                // Create officer without project reference (will be set later)
                HdbOfficer officer = new HdbOfficer(id, name, password, age, status, name,
                        null, null, regStatus, null, appRepo, enqRepo);
                userRepo.add(officer);
                count++;
            }
        }

        return count;
    }

    /**
     * Load projects from CSV file
     */
    private static int loadProjects(ProjectRepository projectRepo, UserRepository userRepo) throws IOException {
        int count = 0;
        File file = new File(PROJECT_FILE);

        if (!file.exists() || file.length() == 0) {
            return 0;
        }

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            // Skip header
            String header = br.readLine();
            if (header == null)
                return 0;

            String line;
            while ((line = br.readLine()) != null) {
                String[] data = line.split(",");
                if (data.length < 14)
                    continue; // Must have all required fields

                String projectID = data[0].trim();
                String projectName = data[1].trim();
                String neighborhood = data[2].trim();

                // Parse flat types
                String[] flatTypeStrs = data[3].trim().split("\\|");
                List<FlatType> flatTypeList = new ArrayList<>();
                for (String type : flatTypeStrs) {
                    if (type.equalsIgnoreCase("TWO_ROOM")) {
                        flatTypeList.add(FlatType.TWO_ROOM);
                    } else if (type.equalsIgnoreCase("THREE_ROOM")) {
                        flatTypeList.add(FlatType.THREE_ROOM);
                    }
                }
                FlatType[] flatTypes = flatTypeList.toArray(new FlatType[0]);

                double floorCount = Double.parseDouble(data[4].trim());
                double pricePerFlat = Double.parseDouble(data[5].trim());
                double thresholdPrice = Double.parseDouble(data[6].trim());
                String openDate = data[7].trim();
                String closeDate = data[8].trim();
                boolean visible = Boolean.parseBoolean(data[9].trim());
                int officerSlots = Integer.parseInt(data[10].trim());
                int twoRoomUnits = Integer.parseInt(data[11].trim());
                int threeRoomUnits = Integer.parseInt(data[12].trim());
                String managerInCharge = data[13].trim();

                // Create the project
                Project project = new Project(projectID, neighborhood, flatTypes, floorCount,
                        pricePerFlat, thresholdPrice, openDate, closeDate,
                        visible, officerSlots, twoRoomUnits, threeRoomUnits);
                project.setProjectName(projectName);
                project.setManagerInCharge(managerInCharge);

                projectRepo.add(project);
                count++;

                // Find the manager and add this project to their list
                for (User user : userRepo.getAll()) {
                    if (user instanceof HdbManager &&
                            user.getName().equals(managerInCharge)) {
                        HdbManager manager = (HdbManager) user;
                        manager.getProjectsCreated().add(project);
                    }
                }
            }
        }

        return count;
    }

    /**
     * Load applications from CSV file
     */
    private static int loadApplications(ApplicationRepository appRepo,
            UserRepository userRepo,
            ProjectRepository projectRepo) throws IOException {
        int count = 0;
        File file = new File(APPLICATION_FILE);

        if (!file.exists() || file.length() == 0) {
            return 0;
        }

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            // Skip header
            String header = br.readLine();
            if (header == null)
                return 0;

            String line;
            while ((line = br.readLine()) != null) {
                String[] data = line.split(",");
                if (data.length < 7)
                    continue;

                String applicationID = data[0].trim();
                String applicantID = data[1].trim();
                String projectID = data[2].trim();
                String applicationDate = data[3].trim();
                String statusStr = data[4].trim();
                String flatTypeStr = data[5].trim();
                boolean withdrawalRequested = Boolean.parseBoolean(data[6].trim());

                // Find the applicant
                Applicant applicant = null;
                for (User user : userRepo.getAll()) {
                    if (user instanceof Applicant && user.getId().equals(applicantID)) {
                        applicant = (Applicant) user;
                        break;
                    }
                }

                // Find the project
                Project project = projectRepo.getById(projectID);

                if (applicant == null || project == null) {
                    System.out.println("Skipping application due to missing applicant or project");
                    continue;
                }

                // Parse flat type
                FlatType flatType = flatTypeStr.equalsIgnoreCase("THREE_ROOM") ? FlatType.THREE_ROOM
                        : FlatType.TWO_ROOM;

                // Create the application
                Application application = new Application(
                        applicationID, applicant, project, applicationDate, flatType);

                // Set status
                ApplicationStatus status = ApplicationStatus.PENDING;
                if (statusStr.equalsIgnoreCase("SUCCESSFUL")) {
                    status = ApplicationStatus.SUCCESSFUL;
                } else if (statusStr.equalsIgnoreCase("UNSUCCESSFUL")) {
                    status = ApplicationStatus.UNSUCCESSFUL;
                } else if (statusStr.equalsIgnoreCase("BOOKED")) {
                    status = ApplicationStatus.BOOKED;
                    // If booked, update applicant's profile
                    applicant.setBookedFlat(flatType);
                    applicant.setBookedProject(project);
                }
                application.updateStatus(status);

                // Set withdrawal request if applicable
                if (withdrawalRequested) {
                    application.requestWithdrawal();
                }

                appRepo.add(application);
                count++;
            }
        }

        return count;
    }

    /**
     * Load enquiries from CSV file
     */
    private static int loadEnquiries(EnquiryRepository enquiryRepo,
            UserRepository userRepo,
            ProjectRepository projectRepo) throws IOException {
        int count = 0;
        File file = new File(ENQUIRY_FILE);

        if (!file.exists() || file.length() == 0) {
            return 0;
        }

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            // Skip header
            String header = br.readLine();
            if (header == null)
                return 0;

            String line;
            while ((line = br.readLine()) != null) {
                // Handle commas within quoted message content
                List<String> data = parseCSVLine(line);
                if (data.size() < 6)
                    continue;

                String enquiryID = data.get(0).trim();
                String projectID = data.get(1).trim();
                String applicantID = data.get(2).trim();
                String message = data.get(3).trim();
                String response = data.get(4).trim();
                String statusStr = data.get(5).trim();

                // Find the applicant
                Applicant applicant = null;
                for (User user : userRepo.getAll()) {
                    if (user instanceof Applicant && user.getId().equals(applicantID)) {
                        applicant = (Applicant) user;
                        break;
                    }
                }

                // Find the project
                Project project = projectRepo.getById(projectID);

                if (applicant == null || project == null) {
                    System.out.println("Skipping enquiry due to missing applicant or project");
                    continue;
                }

                // Create the enquiry
                Enquiry enquiry = new Enquiry(enquiryID, project, applicant, message);

                // Set status and response if applicable
                if (statusStr.equalsIgnoreCase("REPLIED") && !response.isEmpty() && !response.equals("null")) {
                    enquiry.reply(response);
                }

                enquiryRepo.add(enquiry);
                count++;
            }
        }

        return count;
    }

    /**
     * Save all data to CSV files
     */
    public static boolean saveAllData(UserRepository userRepo, ProjectRepository projectRepo,
            ApplicationRepository appRepo, EnquiryRepository enquiryRepo) {
        try {
            // Save users (applicants, managers, officers)
            saveApplicants(userRepo);
            saveManagers(userRepo);
            saveOfficers(userRepo);

            // Save projects
            saveProjects(projectRepo);

            // Save applications
            saveApplications(appRepo);

            // Save enquiries
            saveEnquiries(enquiryRepo);

            return true;
        } catch (IOException e) {
            System.err.println("Error saving data: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Save applicants to CSV file
     */
    private static void saveApplicants(UserRepository userRepo) throws IOException {
        List<User> users = userRepo.getAll();
        List<Applicant> applicants = new ArrayList<>();

        // Filter to get only applicants
        for (User user : users) {
            if (user instanceof Applicant && !(user instanceof HdbManager) && !(user instanceof HdbOfficer)) {
                applicants.add((Applicant) user);
            }
        }

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(APPLICANT_FILE))) {
            // Write header
            writer.write("ID,Name,Password,Age,MaritalStatus,Income\n");

            // Write data
            for (Applicant applicant : applicants) {
                writer.write(
                        applicant.getId() + "," +
                                applicant.getName() + "," +
                                applicant.getPassword() + "," +
                                applicant.getAge() + "," +
                                applicant.getMaritalStatus() + "," +
                                applicant.getIncomeRange() + "\n");
            }
        }
    }

    /**
     * Save managers to CSV file
     */
    private static void saveManagers(UserRepository userRepo) throws IOException {
        List<User> users = userRepo.getAll();
        List<HdbManager> managers = new ArrayList<>();

        // Filter to get only managers
        for (User user : users) {
            if (user instanceof HdbManager) {
                managers.add((HdbManager) user);
            }
        }

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(MANAGER_FILE))) {
            // Write header
            writer.write("ID,Name,Password,Age,MaritalStatus,Income\n");

            // Write data
            for (HdbManager manager : managers) {
                writer.write(
                        manager.getId() + "," +
                                manager.getName() + "," +
                                manager.getPassword() + "," +
                                manager.getAge() + "," +
                                manager.getMaritalStatus() + "," +
                                manager.getIncomeRange() + "\n");
            }
        }
    }

    /**
     * Save officers to CSV file
     */
    private static void saveOfficers(UserRepository userRepo) throws IOException {
        List<User> users = userRepo.getAll();
        List<HdbOfficer> officers = new ArrayList<>();

        // Filter to get only officers
        for (User user : users) {
            if (user instanceof HdbOfficer) {
                officers.add((HdbOfficer) user);
            }
        }

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(OFFICER_FILE))) {
            // Write header
            writer.write(
                    "ID,Name,Password,Age,MaritalStatus,HandlingProjectID,RegistrationStatus,RegisteringManager\n");

            // Write data
            for (HdbOfficer officer : officers) {
                String handlingProjectID = (officer.getHandlingProject() != null)
                        ? officer.getHandlingProject().getProjectID()
                        : "";
                String pendingProjectID = (officer.getPendingProject() != null)
                        ? officer.getPendingProject().getProjectID()
                        : "";
                String registeringManagerID = (officer.getRegisteringManager() != null)
                        ? officer.getRegisteringManager().getId()
                        : "";

                writer.write(
                        officer.getId() + "," +
                                officer.getName() + "," +
                                officer.getPassword() + "," +
                                officer.getAge() + "," +
                                officer.getMaritalStatus() + "," +
                                handlingProjectID + "," +
                                officer.getRegistrationStatus() + "," +
                                registeringManagerID + "\n");
            }
        }
    }

    /**
     * Save projects to CSV file
     */
    private static void saveProjects(ProjectRepository projectRepo) throws IOException {
        List<Project> projects = projectRepo.getAll();

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(PROJECT_FILE))) {
            // Write header
            writer.write("ProjectID,ProjectName,Neighborhood,FlatTypes,FloorCount,PricePerFlat,ThresholdPrice," +
                    "OpenDate,CloseDate,Visible,OfficerSlots,TwoRoomUnits,ThreeRoomUnits,ManagerInCharge\n");

            // Write data
            for (Project project : projects) {
                // Convert flat types to a pipe-separated string
                StringBuilder flatTypesStr = new StringBuilder();
                for (int i = 0; i < project.getFlatType().length; i++) {
                    if (i > 0)
                        flatTypesStr.append("|");
                    flatTypesStr.append(project.getFlatType()[i].toString());
                }

                writer.write(
                        project.getProjectID() + "," +
                                project.getProjectName() + "," +
                                project.getNeighborhood() + "," +
                                flatTypesStr.toString() + "," +
                                project.getFloorCount() + "," +
                                project.getPricePerFlat() + "," +
                                project.getThresholdPrice() + "," +
                                project.getApplicationOpenDate() + "," +
                                project.getApplicationCloseDate() + "," +
                                project.isVisible() + "," +
                                project.getAvailableOfficerSlots() + "," +
                                project.getTwoRoomUnitsAvailable() + "," +
                                project.getThreeRoomUnitsAvailable() + "," +
                                project.getManagerInCharge() + "\n");
            }
        }
    }

    /**
     * Save applications to CSV file
     */
    private static void saveApplications(ApplicationRepository appRepo) throws IOException {
        List<Application> applications = appRepo.getAll();

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(APPLICATION_FILE))) {
            // Write header
            writer.write("ApplicationID,ApplicantID,ProjectID,ApplicationDate,Status,FlatType,WithdrawalRequested\n");

            // Write data
            for (Application app : applications) {
                writer.write(
                        app.getApplicationId() + "," +
                                app.getApplicant().getId() + "," +
                                app.getProject().getProjectID() + "," +
                                app.getApplicationDate() + "," +
                                app.getStatus() + "," +
                                app.getSelectedFlatType() + "," +
                                app.isWithdrawalRequested() + "\n");
            }
        }
    }

    /**
     * Save enquiries to CSV file
     */
    private static void saveEnquiries(EnquiryRepository enquiryRepo) throws IOException {
        List<Enquiry> enquiries = enquiryRepo.getAll();

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(ENQUIRY_FILE))) {
            // Write header
            writer.write("EnquiryID,ProjectID,ApplicantID,Message,Response,Status\n");

            // Write data
            for (Enquiry enquiry : enquiries) {
                // Escape commas in message and response
                String message = escapeCSV(enquiry.getMessage());
                String response = enquiry.getResponse() != null ? escapeCSV(enquiry.getResponse()) : "";

                writer.write(
                        enquiry.getEnquiryId() + "," +
                                enquiry.getProject().getProjectID() + "," +
                                enquiry.getApplicant().getId() + "," +
                                message + "," +
                                response + "," +
                                enquiry.getStatus() + "\n");
            }
        }
    }

    /**
     * Create default data if loading fails
     */
    private static void createDefaultData(UserRepository userRepo, ProjectRepository projectRepo,
            ApplicationRepository appRepo, EnquiryRepository enquiryRepo) {
        createDefaultUsers(userRepo, appRepo, enquiryRepo);
        createSampleProjects(userRepo, projectRepo);
    }

    /**
     * Creates some default users for demonstration
     * 
     * @param userRepo The user repository to populate
     * @return The number of users created
     */
    private static int createDefaultUsers(UserRepository userRepo, ApplicationRepository applicationRepo,
            EnquiryRepository enquiryRepo) {
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
                    150000.0);
            defaultUsers.add(manager1);
            count++;

            HdbManager manager2 = new HdbManager(
                    "T8765432F",
                    "Michael",
                    "password",
                    36,
                    MaritalStatus.SINGLE,
                    "Default Manager",
                    125000.0);
            defaultUsers.add(manager2);
            count++;

            HdbManager manager3 = new HdbManager(
                    "S5678901G",
                    "Jessica",
                    "password",
                    26,
                    MaritalStatus.MARRIED,
                    "Default Manager",
                    125000.0);
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
                    null, // No project assigned yet
                    OfficerRegistrationStatus.PENDING,
                    manager3,
                    applicationRepo,
                    enquiryRepo // Will be set later
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
                    null, // pending project
                    null, // handling project
                    OfficerRegistrationStatus.PENDING,
                    manager2,
                    applicationRepo,
                    enquiryRepo);
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
                    80000.0);
            defaultUsers.add(applicant1);
            count++;

            Applicant applicant2 = new Applicant(
                    "S5678901E",
                    "Michael Tan",
                    "password",
                    28,
                    MaritalStatus.MARRIED,
                    "Michael Tan",
                    90000.0);
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
                    70000.0);
            defaultUsers.add(applicant3);
            count++;

            Applicant applicant4 = new Applicant(
                    "S7890123G",
                    "David Chen",
                    "password",
                    36,
                    MaritalStatus.SINGLE,
                    "David Chen",
                    75000.0);
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
                    65000.0);
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
     * Create sample projects for testing purposes
     */
    private static void createSampleProjects(UserRepository userRepo, ProjectRepository projectRepo) {
        try {
            // Find existing managers
            HdbManager manager1 = null;
            HdbManager manager2 = null;
            HdbManager manager3 = null;

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
                        120000.0);
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
                        125000.0);
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
                        130000.0);
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
                    projectRepo);

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
                    projectRepo);

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
                    projectRepo);
        } catch (Exception e) {
            System.err.println("Error creating sample projects: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Parse a CSV line, handling quoted content with commas
     */
    private static List<String> parseCSVLine(String line) {
        List<String> result = new ArrayList<>();
        boolean inQuotes = false;
        StringBuilder field = new StringBuilder();

        for (int i = 0; i < line.length(); i++) {
            char c = line.charAt(i);

            if (c == '"') {
                inQuotes = !inQuotes;
            } else if (c == ',' && !inQuotes) {
                result.add(field.toString());
                field = new StringBuilder();
            } else {
                field.append(c);
            }
        }

        // Add the last field
        result.add(field.toString());

        return result;
    }

    /**
     * Escape CSV content (wrap in quotes if it contains commas)
     */
    private static String escapeCSV(String input) {
        if (input == null)
            return "";

        if (input.contains(",") || input.contains("\"") || input.contains("\n")) {
            // Replace all quotes with double quotes
            String escaped = input.replace("\"", "\"\"");
            // Wrap in quotes
            return "\"" + escaped + "\"";
        }

        return input;
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
