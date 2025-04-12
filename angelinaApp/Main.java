package sc2002.group.proj;

import java.util.*;

public class Main {
    private static final Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        System.out.println("Welcome to the BTO Application System!");

        while (true) {
            System.out.print("Enter User ID (or type 'exit'): ");
            String id = scanner.nextLine();
            if (id.equalsIgnoreCase("exit")) break;

            if (!isValidNRIC(id)) {
                System.out.println("Invalid NRIC format. It must start with S/T, followed by 7 digits, and end with a letter.");
                continue;
            }

            System.out.print("Enter Password: ");
            String password = scanner.nextLine();

            User user = UserRepository.getInstance().getUserById(id);
            if (user == null || !user.getPassword().equals(password)) {
                System.out.println("Invalid login. Please try again.\n");
                continue;
            }

            System.out.println("Login successful as: " + user.getClass().getSimpleName());

            if (user instanceof Applicant) {
                ApplicantCLI.launch((Applicant) user);
            } else if (user instanceof HdbOfficer) {
                OfficerCLI.launch((HdbOfficer) user);
            } else if (user instanceof HdbManager) {
                ManagerCLI.launch((HdbManager) user);
            } else {
                System.out.println("Unknown user role.");
            }
        }

        System.out.println("Exiting system. Goodbye!");
    }

    private static boolean isValidNRIC(String nric) {
        return nric.matches("[ST]\d{7}[A-Z]");
    }

    public static void changePassword(User user) {
        Scanner sc = new Scanner(System.in);
    
        System.out.print("Enter current password: ");
        String oldPass = sc.nextLine();
    
        if (!user.getPassword().equals(oldPass)) {
            System.out.println("Incorrect current password.");
            return;
        }
    
        System.out.print("Enter new password: ");
        String newPass = sc.nextLine();
        System.out.print("Confirm new password: ");
        String confirmPass = sc.nextLine();
    
        if (!newPass.equals(confirmPass)) {
            System.out.println("Passwords do not match.");
            return;
        }
    
        user.setPassword(newPass); // assumes you already have setPassword in User class
        System.out.println("Password changed successfully.");
    }

}
