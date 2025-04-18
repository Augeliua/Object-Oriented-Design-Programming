package sc2002.bto.entity;

import sc2002.bto.enums.EnquiryStatus;

public class Enquiry {
    private String enquiryId;
    private Project project;
    private Applicant applicant;
    private String message;
    private String response;
    private EnquiryStatus status;
    
    public Enquiry(String enquiryId, Project project, Applicant applicant, String message) {
        this.enquiryId = enquiryId;
        this.project = project;
        this.applicant = applicant;
        this.message = message;
        this.status = EnquiryStatus.PENDING; // Default status
        this.response = null; // No reply yet
    }

    // Getters
    public String getEnquiryId() { 
        return enquiryId; 
    }
    
    public Project getProject() { 
        return project; 
    }
    
    public Applicant getApplicant() { 
        return applicant; 
    }
    
    public String getMessage() { 
        return message; 
    }
    
    public String getResponse() { 
        return response; 
    }
    
    public EnquiryStatus getStatus() { 
        return status; 
    }

    // For applicant use
    public void setMessage(String message) {
        if (status == EnquiryStatus.PENDING) 
            this.message = message;
    }

    public boolean isEditableOrDeletable() {
        return status == EnquiryStatus.PENDING;
    }

    // For officer/manager use
    public void reply(String responseMessage) {
        this.response = responseMessage;
        this.status = EnquiryStatus.REPLIED;
    }

    // /**
    //  * Gets the enquiry details as a formatted string
    //  * @return A string containing all the enquiry details
    //  */
    // public String getEnquiryDetailsAsString() {
    //     StringBuilder sb = new StringBuilder();
    //     sb.append("Enquiry ID: ").append(enquiryId).append("\n");
    //     sb.append("From: ").append(applicant.getName()).append("\n");
        
    //     if (project != null) {
    //         sb.append("Project: ").append(project.getProjectID()).append("\n");
    //     } else {
    //         sb.append("Project: Not specified\n");
    //     }
        
    //     sb.append("Message: ").append(message).append("\n");
    //     sb.append("Status: ").append(status).append("\n");
        
    //     if (response != null && !response.trim().isEmpty()) {
    //         sb.append("Response: ").append(response);
    //     } else {
    //         sb.append("Response: Not yet answered");
    //     }
        
    //     return sb.toString();
    // }
    
    // /**
    //  * @deprecated Use getEnquiryDetailsAsString() instead
    //  * Displays the enquiry details to the console
    //  */
    // @Deprecated
    // public void displayEnquiryDetails() {
    //     System.out.println("Enquiry ID: " + enquiryId);
    //     System.out.println("From: " + applicant.getName());
        
    //     if (project != null) {
    //         System.out.println("Project: " + project.getProjectID());
    //     } else {
    //         System.out.println("Project: Not specified");
    //     }
        
    //     System.out.println("Message: " + message);
    //     System.out.println("Status: " + status);
        
    //     if (response != null && !response.trim().isEmpty()) {
    //         System.out.println("Response: " + response);
    //     } else {
    //         System.out.println("Response: Not yet answered");
    //     }
    // }

    // changed to solve error in HdbOfficer: The method displayEnquiryDetails() from the type Enquiry is deprecated
    /**
     * Gets the enquiry details as a formatted string
     * @return A string containing all the enquiry details
     */
    public String getEnquiryDetailsAsString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Enquiry ID: ").append(enquiryId).append("\n");
        sb.append("From: ").append(applicant.getName()).append("\n");
        
        if (project != null) {
            sb.append("Project: ").append(project.getProjectID()).append("\n");
        } else {
            sb.append("Project: Not specified\n");
        }
        
        sb.append("Message: ").append(message).append("\n");
        sb.append("Status: ").append(status).append("\n");
        
        if (response != null && !response.trim().isEmpty()) {
            sb.append("Response: ").append(response);
        } else {
            sb.append("Response: Not yet answered");
        }
        
        return sb.toString();
    }

    /**
     * Prints the enquiry details to the console using the formatted string.
     */
    public void printEnquiryDetails() {
        System.out.println(getEnquiryDetailsAsString());
    }

}