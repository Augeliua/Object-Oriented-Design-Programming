package sc2002.bto.entity;

import sc2002.bto.enums.ApplicationStatus;
import sc2002.bto.enums.FlatType;

/**
 * Represents a BTO housing application submitted by an applicant.
 * Tracks the application status, selected flat type, and withdrawal requests.
 * 
 */
public class Application {
    /** Unique identifier for this application */
    private String applicationId;
    /** The applicant who submitted this application */
    private Applicant applicant;
    /** The project being applied for */
    private Project project;
    /** Date when the application was submitted */
    private String applicationDate;
    /** Current status of the application */
    private ApplicationStatus status;
    /** Type of flat selected in this application */
    private FlatType selectedFlatType;
    /** Whether a withdrawal has been requested for this application */
    private boolean withdrawalRequested = false;

    /**
     * Creates a new application with the specified details.
     * The default status is set to PENDING.
     * 
     * @param applicationId    Unique identifier for this application
     * @param applicant        The applicant submitting the application
     * @param project          The project being applied for
     * @param applicationDate  The date of application submission
     * @param selectedFlatType The type of flat selected in this application
     */
    public Application(String applicationId, Applicant applicant, Project project, String applicationDate,
            FlatType selectedFlatType) {
        this.applicationId = applicationId;
        this.applicant = applicant;
        this.project = project;
        this.applicationDate = applicationDate;
        this.selectedFlatType = selectedFlatType;
        this.status = ApplicationStatus.PENDING; // Set default status
    }

    /**
     * Gets the unique ID of this application.
     * 
     * @return The application's unique identifier.
     */
    public String getApplicationId() {
        return applicationId;
    }

    /**
     * Gets the unique ID of this application.
     * 
     * @return The application's unique identifier.
     */
    public Applicant getApplicant() {
        return applicant;
    }

    /**
     * Gets the project associated with this application.
     * 
     * @return The Project object for this application.
     */
    public Project getProject() {
        return project;
    }

    /**
     * Gets the date when this application was submitted.
     * 
     * @return The application date as a string.
     */
    public String getApplicationDate() {
        return applicationDate;
    }

    /**
     * Gets the current status of this application.
     * 
     * @return The current ApplicationStatus (PENDING, SUCCESSFUL, UNSUCCESSFUL, or
     *         BOOKED).
     */
    public ApplicationStatus getStatus() {
        return status;
    }

    /**
     * Gets the flat type selected in this application.
     * 
     * @return The FlatType selected by the applicant.
     */
    public FlatType getSelectedFlatType() {
        return selectedFlatType;
    }

    /**
     * Updates the flat type for this application.
     * 
     * @param flatType The new flat type to select.
     */
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

    /**
     * Clears the withdrawal request flag for this application.
     * Called after a withdrawal request has been processed.
     */
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
