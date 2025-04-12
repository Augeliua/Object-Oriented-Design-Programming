// ManagerCLI.java
package sc2002.group.proj;

import java.util.List;
import java.util.Scanner;

public class ManagerCLI {
    private HdbManager manager;
    private ProjectRepository projectRepo;
    private ApplicationRepository appRepo;
    private UserRepository userRepo;
    private Scanner sc;

    public ManagerCLI(HdbManager manager, ProjectRepository projectRepo, ApplicationRepository appRepo, UserRepository userRepo) {
        this.manager = manager;
        this.projectRepo = projectRepo;
        this.appRepo = appRepo;
        this.userRepo = userRepo;
        this.sc = new Scanner(System.in);
    }

    public void showMenu() {
        int choice;
        do {
            System.out.println("\n--- Manager Menu ---");
            System.out.println("1. Create Project");
            System.out.println("2. Toggle Project Visibility");
            System.out.println("3. Approve/Reject Applications");
            System.out.println("4. Approve/Reject Withdrawals");
            System.out.println("5. Generate Applicant Report");
            System.out.println("6. Change Password");
            System.out.println("0. Logout");
            System.out.print("Enter choice: ");
            choice = Integer.parseInt(sc.nextLine());

            switch (choice) {
                case 1 -> createProject();
                case 2 -> toggleVisibility();
                case 3 -> approveApplications();
                case 4 -> approveWithdrawals();
                case 5 -> generateReport();
                case 6 -> Main.changePassword(manager);
                case 0 -> System.out.println("Logging out...");
                default -> System.out.println("Invalid choice.");
            }
        } while (choice != 0);
    }

    private void createProject() {
        System.out.print("Enter project name: ");
        String name = sc.nextLine();
        System.out.print("Enter neighborhood: ");
        String area = sc.nextLine();
        System.out.print("Opening date (YYYY-MM-DD): ");
        String openDate = sc.nextLine();
        System.out.print("Closing date (YYYY-MM-DD): ");
        String closeDate = sc.nextLine();
        System.out.print("2-room units: ");
        int two = Integer.parseInt(sc.nextLine());
        System.out.print("3-room units: ");
        int three = Integer.parseInt(sc.nextLine());

        Project p = new Project(name, area, two, three, openDate, closeDate, manager);
        projectRepo.add(p);
        System.out.println("Project created!");
    }

    private void toggleVisibility() {
        List<Project> all = projectRepo.getAll();
        for (int i = 0; i < all.size(); i++) {
            Project p = all.get(i);
            if (p.getManager().equals(manager)) {
                System.out.printf("%d. %s (Visible: %b)\n", i + 1, p.getProjectName(), p.isVisible());
            }
        }
        System.out.print("Select project to toggle: ");
        int index = Integer.parseInt(sc.nextLine()) - 1;
        Project p = all.get(index);
        p.setVisible(!p.isVisible());
        System.out.println("Visibility updated: " + p.isVisible());
    }

    private void approveApplications() {
        for (Application a : appRepo.getAll()) {
            if (a.getProject().getManager().equals(manager) && a.getStatus() == ApplicationStatus.PENDING) {
                System.out.printf("Applicant: %s | Project: %s | Status: %s\n", a.getApplicant().getApplicantName(), a.getProject().getProjectName(), a.getStatus());
                System.out.print("Approve? (y/n): ");
                String input = sc.nextLine();
                if (input.equalsIgnoreCase("y")) {
                    a.setStatus(ApplicationStatus.SUCCESSFUL);
                } else {
                    a.setStatus(ApplicationStatus.UNSUCCESSFUL);
                }
                appRepo.update(a);
            }
        }
    }

    private void approveWithdrawals() {
        for (Application a : appRepo.getAll()) {
            if (a.getProject().getManager().equals(manager) && a.isWithdrawalRequested()) {
                System.out.printf("Applicant: %s requested withdrawal. Approve? (y/n): ", a.getApplicant().getApplicantName());
                String input = sc.nextLine();
                if (input.equalsIgnoreCase("y")) {
                    a.setStatus(ApplicationStatus.UNSUCCESSFUL);
                    appRepo.update(a);
                    System.out.println("Withdrawal approved.");
                }
            }
        }
    }

    private void generateReport() {
        System.out.println("\n--- Applicant Report ---");
        for (Application a : appRepo.getAll()) {
            if (a.getProject().getManager().equals(manager) && a.getStatus() == ApplicationStatus.BOOKED) {
                System.out.printf("Name: %s | NRIC: %s | Project: %s | Flat: %s | Marital Status: %s\n",
                        a.getApplicant().getApplicantName(),
                        a.getApplicant().getId(),
                        a.getProject().getProjectName(),
                        a.getApplicant().getBookedFlat(),
                        a.getApplicant().getMaritalStatus());
            }
        }
    }
}
