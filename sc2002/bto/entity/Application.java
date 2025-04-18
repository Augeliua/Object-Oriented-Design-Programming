package sc2002.bto.entity;

import sc2002.bto.enums.ApplicationStatus;
import sc2002.bto.enums.FlatType;

public class Application {
    private String applicationId;
    private Applicant applicant;
    private Project project;
    private String applicationDate; 
    private ApplicationStatus status = ApplicationStatus.PENDING;
    private FlatType selectedFlatType;
    private boolean withdrawalRequested = false;

    public Application(String applicationId, Applicant applicant, Project project, String applicationDate, FlatType selectedFlatType) {
        this.applicationId = applicationId;
        this.applicant = applicant;
        this.project = project;
        this.applicationDate = applicationDate;
        this.selectedFlatType = selectedFlatType;
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

    // Handle withdrawal
    public void requestWithdrawal() {
        this.withdrawalRequested = true;
    }
    
    public boolean isWithdrawalRequested() {
        return this.withdrawalRequested;
    }

    public void clearWithdrawalRequest() {
        this.withdrawalRequested = false;
    }    
    
    // Update status
    public void updateStatus(ApplicationStatus newStatus) {
        this.status = newStatus;
    }
}
