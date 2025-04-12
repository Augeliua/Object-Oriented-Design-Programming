package sc2002.group.proj;

public class Application {
    private String applicationId;
    private Applicant applicant;
    private Project project;
    private String applicationDate; 
    private ApplicationStatus status;
    private FlatType selectedFlatType;
    private boolean withdrawalRequested = false;

    public Application(String applicationId, Applicant applicant, Project project, String applicationDate, FlatType selectedFlatType) {
        this.applicationId = applicationId;
        this.applicant = applicant;
        this.project = project;
        this.applicationDate = applicationDate;
        this.selectedFlatType = selectedFlatType;
        this.status = ApplicationStatus.PENDING; // Set default here (entry status)
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

//    // Submit the application
//    public void submit() {
//        this.status = ApplicationStatus.PENDING;        
//    }

    // Handle withdrawal
    public void requestWithdrawal() {
        this.withdrawalRequested = true;
    }
    public boolean isWithdrawalRequested() {
        return this.withdrawalRequested;
    } // isWithdrawalRequested() communicates with HDBManager
    
    // Manually update status
    public void updateStatus(ApplicationStatus newStatus) {
    	// System.out.println("Status updated from " + this.status + " to " + newStatus);
        this.status = newStatus;
    }
}
