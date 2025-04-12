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
    
    public String getReportAsString() {
        StringBuilder sb = new StringBuilder();
        sb.append("=====================\n");
        sb.append("Report ID: ").append(reportId).append("\n");
        sb.append("Type: ").append(reportType).append("\n");
        sb.append("Generated on: ").append(generatedDate).append("\n");
        sb.append("=====================\n");
        
        if (items.isEmpty()) {
            sb.append("No items to display.");
            return sb.toString();
        }
        
        switch (reportType) {
            case ALL_BOOKINGS:
                formatAllBookingsReport(sb);
                break;
            case BY_FLAT_TYPE:
                formatByFlatTypeReport(sb);
                break;
            case BY_MARITAL_STATUS:
                formatByMaritalStatusReport(sb);
                break;
            default:
                sb.append("Unknown report type");
        }
        
        return sb.toString();
    }
    
    @SuppressWarnings("unchecked")
   private void formatAllBookingsReport(StringBuilder sb) {
        sb.append("\nAll Bookings Report\n");
        sb.append("------------------\n");
        
        try {
            List<Application> applications = (List<Application>) items;
            for (Application app : applications) {
                if (app.getStatus() == sc2002.bto.enums.ApplicationStatus.BOOKED) {
                    Applicant applicant = app.getApplicant();
                    Project project = app.getProject();
                    
                    sb.append("Applicant: ").append(applicant.getName()).append("\n");
                    sb.append("NRIC: ").append(applicant.getId()).append("\n");
                    sb.append("Age: ").append(applicant.getAge()).append("\n");
                    sb.append("Marital Status: ").append(applicant.getMaritalStatus()).append("\n");
                    sb.append("Project: ").append(project.getProjectName()).append("\n");
                    sb.append("Flat Type: ").append(app.getSelectedFlatType()).append("\n");
                    sb.append("Booking Date: ").append(app.getApplicationDate()).append("\n");
                    sb.append("------------------\n");
                }
            }
        } catch (ClassCastException e) {
            sb.append("Error: Invalid data format for this report type");
        }
    }
    
    @SuppressWarnings("unchecked")
    private void formatByFlatTypeReport(StringBuilder sb) {
        sb.append("\nBookings by Flat Type Report\n");
        sb.append("---------------------------\n");
        
        try {
            // We expect the first item to be a Map<FlatType, List<Application>>
            if (items.isEmpty()) {
                sb.append("No data available");
                return;
            }
            
            if (!(items.get(0) instanceof java.util.Map)) {
                sb.append("Error: Invalid data format for this report type");
                return;
            }
            
            java.util.Map<sc2002.bto.enums.FlatType, List<Application>> groupedByFlatType = 
                (java.util.Map<sc2002.bto.enums.FlatType, List<Application>>) items.get(0);
            
            for (java.util.Map.Entry<sc2002.bto.enums.FlatType, List<Application>> entry : groupedByFlatType.entrySet()) {
                sc2002.bto.enums.FlatType flatType = entry.getKey();
                List<Application> apps = entry.getValue();
                
                sb.append("\nFlat Type: ").append(flatType).append("\n");
                sb.append("Total Bookings: ").append(apps.size()).append("\n");
                
                for (Application app : apps) {
                    if (app.getStatus() == sc2002.bto.enums.ApplicationStatus.BOOKED) {
                        Applicant applicant = app.getApplicant();
                        Project project = app.getProject();
                        
                        sb.append("* Applicant: ").append(applicant.getName()).append("\n");
                        sb.append("  Project: ").append(project.getProjectName()).append("\n");
                        sb.append("  Age: ").append(applicant.getAge()).append("\n");
                        sb.append("  Marital Status: ").append(applicant.getMaritalStatus()).append("\n");
                    }
                }
            }
        } catch (ClassCastException e) {
            sb.append("Error: Invalid data format for this report type");
        }
    }
    
    @SuppressWarnings("unchecked")
    private void formatByMaritalStatusReport(StringBuilder sb) {
        sb.append("\nBookings by Marital Status Report\n");
        sb.append("-------------------------------\n");
        
        try {
            List<Application> applications = (List<Application>) items;
            sb.append("Total Bookings (Married Applicants): ").append(applications.size()).append("\n");
            
            for (Application app : applications) {
                if (app.getStatus() == sc2002.bto.enums.ApplicationStatus.BOOKED) {
                    Applicant applicant = app.getApplicant();
                    Project project = app.getProject();
                    
                    sb.append("* Applicant: ").append(applicant.getName()).append("\n");
                    sb.append("  Age: ").append(applicant.getAge()).append("\n");
                    sb.append("  Project: ").append(project.getProjectName()).append("\n");
                    sb.append("  Flat Type: ").append(app.getSelectedFlatType()).append("\n");
                    sb.append("  Booking Date: ").append(app.getApplicationDate()).append("\n");
                    sb.append("  ------------------\n");
                }
            }
        } catch (ClassCastException e) {
            sb.append("Error: Invalid data format for this report type");
        }
    }

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
}
