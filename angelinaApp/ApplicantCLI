package sc2002.group.proj;

import java.util.List;
import java.util.Scanner;

public class ApplicantCLI {
    public static void launch(Applicant applicant) {
        Scanner sc = new Scanner(System.in);
        ProjectRepository projectRepo = ProjectRepository.getInstance();
        ApplicationRepository appRepo = ApplicationRepository.getInstance();
        EnquiryRepository enquiryRepo = EnquiryRepository.getInstance();

        while (true) {
            System.out.println("\n--- Applicant Menu ---");
            System.out.println("1. View Eligible Projects");
            System.out.println("2. Apply for a Project");
            System.out.println("3. View My Application Status");
            System.out.println("4. Submit Enquiry");
            System.out.println("5. View My Enquiries");
            System.out.println("6. Edit Enquiry");
            System.out.println("7. Delete Enquiry");
            System.out.println("8. Request Flat Booking");
            System.out.println("9. Request Withdrawal");
            System.out.println("0. Logout");
            System.out.print("Enter your choice: ");

            String choice = sc.nextLine();
            switch (choice) {
                case "1" -> applicant.viewEligibleProjects(projectRepo);
                case "2" -> {
                    System.out.print("Enter Project ID: ");
                    String pid = sc.nextLine();
                    Project project = projectRepo.getById(pid);
                    if (project == null) {
                        System.out.println("Project not found.");
                        continue;
                    }
                    System.out.print("Enter Flat Type (TWO_ROOM or THREE_ROOM): ");
                    String type = sc.nextLine();
                    applicant.submitApplication(project, FlatType.valueOf(type), appRepo, projectRepo);
                }
                case "3" -> applicant.viewMyApplicationStatus(appRepo);
                case "4" -> {
                    System.out.print("Enter Project ID: ");
                    String pid = sc.nextLine();
                    Project p = projectRepo.getById(pid);
                    if (p != null) {
                        System.out.print("Enter message: ");
                        String msg = sc.nextLine();
                        applicant.submitEnquiry(p, msg, enquiryRepo);
                    } else {
                        System.out.println("Invalid project.");
                    }
                }
                case "5" -> {
                    List<Enquiry> myEnquiries = applicant.viewMyEnquiries(enquiryRepo);
                    for (Enquiry e : myEnquiries) {
                        e.displayEnquiryDetails();
                    }
                }
                case "6" -> {
                    System.out.print("Enter Enquiry ID to edit: ");
                    String eid = sc.nextLine();
                    System.out.print("Enter new message: ");
                    String newMsg = sc.nextLine();
                    applicant.editEnquiry(eid, newMsg, enquiryRepo);
                }
                case "7" -> {
                    System.out.print("Enter Enquiry ID to delete: ");
                    String eid = sc.nextLine();
                    applicant.deleteEnquiry(eid, enquiryRepo);
                }
                case "8" -> {
                    System.out.print("Enter Flat Type to book: ");
                    String type = sc.nextLine();
                    System.out.print("Enter Officer NRIC: ");
                    String onric = sc.nextLine();
                    HdbOfficer officer = UserRepository.getInstance().getOfficerByNric(onric);
                    if (officer != null) {
                        applicant.requestFlatBooking(officer, FlatType.valueOf(type));
                    } else {
                        System.out.println("Invalid officer.");
                    }
                }
                case "9" -> applicant.requestWithdrawal(appRepo);
                case "0" -> {
                    System.out.println("Logging out...");
                    return;
                }
                default -> System.out.println("Invalid option.");
            }
        }
    }
}
