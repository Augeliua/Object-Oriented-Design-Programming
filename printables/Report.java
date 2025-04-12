package sc2002.bto.entity;

import java.util.ArrayList;
import java.util.List;

import sc2002.bto.enums.ReportType;

public class Report {
    private String reportId;
    private ReportType reportType;
    private List<Object> items;
    private String generatedDate;
    
    public Report() {
        this.reportId = "REP-" + System.currentTimeMillis();
        this.items = new ArrayList<>();
        this.generatedDate = java.time.LocalDate.now().toString();
    }
    
    public String getReportId() {
        return reportId;
    }
    
    public void setReportId(String reportId) {
        this.reportId = reportId;
    }
    
    public ReportType getReportType() {
        return reportType;
    }
    
    public void setReportType(ReportType reportType) {
        this.reportType = reportType;
    }
    
    public List<Object> getItems() {
        return items;
    }
    
    public void setItems(List<Object> items) {
        this.items = items;
    }
    
    public String getGeneratedDate() {
        return generatedDate;
    }
    
    public void setGeneratedDate(String generatedDate) {
        this.generatedDate = generatedDate;
    }
    
    public void printReport() {
        System.out.println("=====================");
        System.out.println("Report ID: " + reportId);
        System.out.println("Type: " + reportType);
        System.out.println("Generated on: " + generatedDate);
        System.out.println("=====================");
        
        if (items.isEmpty()) {
            System.out.println("No items to display.");
            return;
        }
        
        switch (reportType) {
            case ALL_BOOKINGS:
                printAllBookings();
                break;
            case BY_FLAT_TYPE:
                printByFlatType();
                break;
            case BY_MARITAL_STATUS:
                printByMaritalStatus();
                break;
            default:
                System.out.println("Unknown report type");
        }
    }
    
    @SuppressWarnings("unchecked")
    private void printAllBookings() {
        System.out.println("\nAll Bookings Report");
        System.out.println("------------------");
        
        try {
            List<Application> applications = (List<Application>) items;
            for (Application app : applications) {
                if (app.getStatus() == sc2002.bto.enums.ApplicationStatus.BOOKED) {
                    Applicant applicant = app.getApplicant();
                    Project project = app.getProject();
                    
                    System.out.println("Applicant: " + applicant.getName());
                    System.out.println("NRIC: " + applicant.getId());
                    System.out.println("Age: " + applicant.getAge());
                    System.out.println("Marital Status: " + applicant.getMaritalStatus());
                    System.out.println("Project: " + project.getProjectName());
                    System.out.println("Flat Type: " + app.getSelectedFlatType());
                    System.out.println("Booking Date: " + app.getApplicationDate());
                    System.out.println("------------------");
                }
            }
        } catch (ClassCastException e) {
            System.out.println("Error: Invalid data format for this report type");
        }
    }
    
    @SuppressWarnings("unchecked")
    private void printByFlatType() {
        System.out.println("\nBookings by Flat Type Report");
        System.out.println("---------------------------");
        
        try {
            // We expect the first item to be a Map<FlatType, List<Application>>
            if (items.isEmpty()) {
                System.out.println("No data available");
                return;
            }
            
            if (!(items.get(0) instanceof java.util.Map)) {
                System.out.println("Error: Invalid data format for this report type");
                return;
            }
            
            java.util.Map<sc2002.bto.enums.FlatType, List<Application>> groupedByFlatType = 
                (java.util.Map<sc2002.bto.enums.FlatType, List<Application>>) items.get(0);
            
            for (java.util.Map.Entry<sc2002.bto.enums.FlatType, List<Application>> entry : groupedByFlatType.entrySet()) {
                sc2002.bto.enums.FlatType flatType = entry.getKey();
                List<Application> apps = entry.getValue();
                
                System.out.println("\nFlat Type: " + flatType);
                System.out.println("Total Bookings: " + apps.size());
                
                for (Application app : apps) {
                    if (app.getStatus() == sc2002.bto.enums.ApplicationStatus.BOOKED) {
                        Applicant applicant = app.getApplicant();
                        Project project = app.getProject();
                        
                        System.out.println("* Applicant: " + applicant.getName());
                        System.out.println("  Project: " + project.getProjectName());
                        System.out.println("  Age: " + applicant.getAge());
                        System.out.println("  Marital Status: " + applicant.getMaritalStatus());
                    }
                }
            }
        } catch (ClassCastException e) {
            System.out.println("Error: Invalid data format for this report type");
        }
    }
    
    @SuppressWarnings("unchecked")
    private void printByMaritalStatus() {
        System.out.println("\nBookings by Marital Status Report");
        System.out.println("-------------------------------");
        
        try {
            List<Application> applications = (List<Application>) items;
            System.out.println("Total Bookings (Married Applicants): " + applications.size());
            
            for (Application app : applications) {
                if (app.getStatus() == sc2002.bto.enums.ApplicationStatus.BOOKED) {
                    Applicant applicant = app.getApplicant();
                    Project project = app.getProject();
                    
                    System.out.println("* Applicant: " + applicant.getName());
                    System.out.println("  Age: " + applicant.getAge());
                    System.out.println("  Project: " + project.getProjectName());
                    System.out.println("  Flat Type: " + app.getSelectedFlatType());
                    System.out.println("  Booking Date: " + app.getApplicationDate());
                    System.out.println("  ------------------");
                }
            }
        } catch (ClassCastException e) {
            System.out.println("Error: Invalid data format for this report type");
        }
    }
}
