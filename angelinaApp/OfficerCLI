package sc2002.group.proj;

import java.util.List;
import java.util.Scanner;

public class OfficerCLI {
    public static void run(HdbOfficer officer, ProjectRepository projectRepo, ApplicationRepository appRepo, EnquiryRepository enquiryRepo) {
        Scanner sc = new Scanner(System.in);

        while (true) {
            System.out.println("\n--- Officer Menu ---");
            System.out.println("1. View Assigned Project Info");
            System.out.println("2. View Enquiries");
            System.out.println("3. Reply to Enquiry");
            System.out.println("4. Book Flat for Applicant");
            System.out.println("5. Generate Receipt");
            System.out.println("0. Logout");
            System.out.print("Choose an option: ");

            String choice = sc.nextLine();

            switch (choice) {
                case "1" -> {
                    Project p = officer.getHandlingProject();
                    if (p == null) {
                        System.out.println("No project assigned.");
                    } else {
                        System.out.println("Project: " + p.getProjectName());
                        System.out.println("2-room: " + p.getTwoRoomUnitsAvailable());
                        System.out.println("3-room: " + p.getThreeRoomUnitsAvailable());
                        System.out.println("Visible: " + p.isVisible());
                    }
                }
                case "2" -> {
                    Project project = officer.getHandlingProject();
                    for (Enquiry e : enquiryRepo.getAll()) {
                        if (e.getProject().equals(project)) {
                            e.displayEnquiryDetails();
                            System.out.println("Status: " + e.getStatus());
                        }
                    }
                }
                case "3" -> {
                    System.out.print("Enter Enquiry ID to reply: ");
                    String eid = sc.nextLine();
                    Enquiry e = enquiryRepo.getById(eid);
                    if (e != null && e.getProject().equals(officer.getHandlingProject()) && e.getStatus() == EnquiryStatus.PENDING) {
                        System.out.print("Enter reply message: ");
                        String reply = sc.nextLine();
                        e.reply(reply);
                        enquiryRepo.update(e);
                        System.out.println("Reply sent.");
                    } else {
                        System.out.println("Invalid or already replied enquiry.");
                    }
                }
                case "4" -> {
                    System.out.print("Enter Applicant NRIC: ");
                    String anric = sc.nextLine();
                    System.out.print("Enter Flat Type: ");
                    String ft = sc.nextLine();
                    Applicant a = UserRepository.getInstance().getApplicantByNric(anric);
                    if (a != null) {
                        officer.bookFlat(a, FlatType.valueOf(ft));
                    } else {
                        System.out.println("Applicant not found.");
                    }
                }
                case "5" -> {
                    System.out.print("Enter Applicant NRIC for receipt: ");
                    String anric = sc.nextLine();
                    Applicant a = UserRepository.getInstance().getApplicantByNric(anric);
                    if (a != null) {
                        Application app = a.getMyApplication(appRepo);
                        if (app != null && app.getStatus() == ApplicationStatus.BOOKED) {
                            Receipt r = officer.generateReceipt(app);
                            r.display();
                        } else {
                            System.out.println("No booked application found.");
                        }
                    } else {
                        System.out.println("Applicant not found.");
                    }
                }
                case "0" -> {
                    System.out.println("Logging out...");
                    return;
                }
                default -> System.out.println("Invalid option.");
            }
        }
    }
}
