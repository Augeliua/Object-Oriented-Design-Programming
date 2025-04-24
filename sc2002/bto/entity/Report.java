package sc2002.bto.entity;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import sc2002.bto.enums.ApplicationStatus;
import sc2002.bto.enums.MaritalStatus;
import sc2002.bto.enums.ReportType;

/**
 * Represents a report generated in the BTO system.
 * Reports can contain various types of data depending on the report type.
 * 
 */
public class Report {
    /** Unique identifier for this report */
    private String reportId;
    /** The type of this report */
    private ReportType reportType;
    /** List of items included in this report */
    private List<Object> items;
    /** Date when this report was generated */
    private String generatedDate;

    /**
     * Creates a new report with a generated ID and current date.
     */
    public Report() {
        this.reportId = "REP-" + System.currentTimeMillis();
        this.items = new ArrayList<>();
        this.generatedDate = java.time.LocalDate.now().toString();
    }

    /**
     * Gets the report's unique identifier.
     * 
     * @return The report ID
     */
    public String getReportId() {
        return reportId;
    }

    /**
     * Sets the report's unique identifier.
     * 
     * @param reportId The new report ID
     */
    public void setReportId(String reportId) {
        this.reportId = reportId;
    }

    /**
     * Gets the type of this report.
     * 
     * @return The report type (ALL_BOOKINGS, BY_FLAT_TYPE, or BY_MARITAL_STATUS)
     */
    public ReportType getReportType() {
        return reportType;
    }

    /**
     * Sets the type of this report.
     * 
     * @param reportType The report type to set
     */
    public void setReportType(ReportType reportType) {
        this.reportType = reportType;
    }

    /**
     * Gets the list of items included in this report.
     * The actual type of items varies based on the report type.
     * 
     * @return A list of objects representing the report data
     */
    public List<Object> getItems() {
        return items;
    }

    /**
     * Sets the list of items for this report.
     * 
     * @param items The list of objects to include in the report
     */
    public void setItems(List<Object> items) {
        this.items = items;
    }

    /**
     * Gets the date when this report was generated.
     * 
     * @return The generation date as a string
     */
    public String getGeneratedDate() {
        return generatedDate;
    }

    /**
     * Sets the date when this report was generated.
     * 
     * @param generatedDate The generation date as a string
     */
    public void setGeneratedDate(String generatedDate) {
        this.generatedDate = generatedDate;
    }

    /**
     * Gets the report content as a formatted string.
     * 
     * @return A string containing the report content
     */
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

    /**
     * Prints the report to the console.
     */
    public void printReport() {
        System.out.println(this.getReportAsString());
    }

    /**
     * Formats an all bookings report.
     * 
     * @param sb The StringBuilder to append the report content to
     */
    private void formatAllBookingsReport(StringBuilder sb) {
        sb.append("\nAll Bookings Report\n");
        sb.append("------------------\n");

        try {
            List<Application> applications = items.stream()
                    .filter(Application.class::isInstance)
                    .map(Application.class::cast)
                    .collect(Collectors.toList());
            // List<Application> applications = (List<Application>) items;
            // // Cannot cast from List<Object> to List<Application>
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

    /**
     * Formats a report of bookings by flat type.
     * 
     * @param sb The StringBuilder to append the report content to
     */
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

            java.util.Map<sc2002.bto.enums.FlatType, List<Application>> groupedByFlatType = (java.util.Map<sc2002.bto.enums.FlatType, List<Application>>) items
                    .get(0);

            for (java.util.Map.Entry<sc2002.bto.enums.FlatType, List<Application>> entry : groupedByFlatType
                    .entrySet()) {
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

    /**
     * Formats a report of bookings by marital status.
     * 
     * @param sb The StringBuilder to append the report content to
     */
    private void formatByMaritalStatusReport(StringBuilder sb) {
        sb.append("Bookings by Marital Status Report (Only Booked Applicants)\n");
        sb.append("-------------------------------\n");

        List<Application> marriedBookings = items.stream()
            .filter(i -> i instanceof Application)
            .map(i -> (Application) i)
            .filter(app -> app.getStatus() == ApplicationStatus.BOOKED)
            .filter(app -> app.getApplicant().getMaritalStatus() == MaritalStatus.MARRIED)
            .collect(Collectors.toList());

        sb.append("Total Bookings (Married Applicants): ").append(marriedBookings.size()).append("\n");

        for (Application app : marriedBookings) {
            sb.append("* Applicant: ").append(app.getApplicant().getName()).append("\n");
            sb.append("  Age: ").append(app.getApplicant().getAge()).append("\n");
            sb.append("  Project: Project ").append(app.getProject().getProjectID()).append("\n");
            sb.append("  Flat Type: ").append(app.getSelectedFlatType()).append("\n");
            sb.append("  ------------------\n");
        }
    }


    /**
     * Prints all booking information to the console.
     */
    public void printAllBookings() {
        System.out.println("\nAll Bookings Report");
        System.out.println("------------------");

        try {
            // List<Application> applications = (List<Application>) items;
            // Cannot cast from List<Object> to List<Application>
            List<Application> applications = new ArrayList<>();
            for (Object obj : items) {
                if (obj instanceof Application) {
                    applications.add((Application) obj);
                }
            }
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
