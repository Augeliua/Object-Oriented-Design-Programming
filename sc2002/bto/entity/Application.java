package sc2002.bto.entity;

import sc2002.bto.enums.ApplicationStatus;
import sc2002.bto.enums.FlatType;
/**
 * Represents a BTO housing application submitted by an applicant.
 * Tracks the application status, selected flat type, and withdrawal requests.
 * 
 */
public class Application {
    private String applicationId;
    private Applicant applicant;
    private Project project;
    private String applicationDate; 
    private ApplicationStatus status;
    private FlatType selectedFlatType;
    private boolean withdrawalRequested = false;

    /**
     * Creates a new application with the specified details.
     * The default status is set to PENDING.
     * 
     * @param applicationId Unique identifier for this application
     * @param applicant The applicant submitting the application
     * @param project The project being applied for
     * @param applicationDate The date of application submission
     * @param selectedFlatType The type of flat selected in this application
     */
    public Application(String applicationId, Applicant applicant, Project project, String applicationDate, FlatType selectedFlatType) {
        this.applicationId = applicationId;
        this.applicant = applicant;
        this.project = project;
        this.applicationDate = applicationDate;
        this.selectedFlatType = selectedFlatType;
        this.status = ApplicationStatus.PENDING; // Set default status
    }

    // Getters
    public String getApplicationId() {
        return applicationId;
    }

    public Applicant getApplicant() {
        return applicant;
    }

    public Project getProject() {
        return project;
    }

    public String getApplicationDate() {
        return applicationDate;
    }

    public ApplicationStatus getStatus() {
        return status;
    }

    public FlatType getSelectedFlatType() {
        return selectedFlatType;
    }

    // Setters
    public void setSelectedFlatType(FlatType flatType) {
        this.selectedFlatType = flatType;
    }

    /**
     * Requests a withdrawal of this application.
     * The withdrawalRequested flag is set to true, but actual withdrawal
     * requires approval from an HDB Manager.
     */
    public void requestWithdrawal() {
        this.withdrawalRequested = true;
    }
    /**
     * Checks if a withdrawal has been requested for this application.
     * 
     * @return true if withdrawal has been requested, false otherwise
     */
    public boolean isWithdrawalRequested() {
        return this.withdrawalRequested;
    }

    public void clearWithdrawalRequest() {
        this.withdrawalRequested = false;
    }    
    
    /**
     * Updates the status of this application.
     * 
     * @param newStatus The new status to set for this application
     */
    public void updateStatus(ApplicationStatus newStatus) {
        this.status = newStatus;
    }
}
