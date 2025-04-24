package sc2002.bto.entity;

import sc2002.bto.enums.EnquiryStatus;
/**
 * Represents an enquiry submitted by an applicant regarding a BTO project.
 * Tracks the enquiry status, messages, and responses.
 * 
 */
public class Enquiry {
    /** Unique identifier for this enquiry */
    private String enquiryId;
    /** The project this enquiry is about */
    private Project project;
    /** The applicant who submitted this enquiry */
    private Applicant applicant;
    /** The enquiry message content */
    private String message;
    /** The response to this enquiry (if any) */
    private String response;
    /** Current status of the enquiry (PENDING or REPLIED) */
    private EnquiryStatus status;
    
     /**
     * Creates a new enquiry with the specified details.
     * The default status is set to PENDING.
     * 
     * @param enquiryId Unique identifier for this enquiry
     * @param project The project this enquiry is about
     * @param applicant The applicant who submitted this enquiry
     * @param message The enquiry message content
     */
    public Enquiry(String enquiryId, Project project, Applicant applicant, String message) {
        this.enquiryId = enquiryId;
        this.project = project;
        this.applicant = applicant;
        this.message = message;
        this.status = EnquiryStatus.PENDING; // Default status
        this.response = null; // No reply yet
    }

    /**
     * Gets the unique ID of this enquiry.
     * @return The enquiry's unique identifier.
     */
    public String getEnquiryId() { 
        return enquiryId; 
    }
    
    /**
     * Gets the project associated with this enquiry.
     * @return The Project object.
     */
    public Project getProject() { 
        return project; 
    }
    
    /**
     * Gets the applicant who submitted this enquiry.
     * @return The Applicant object.
     */
    public Applicant getApplicant() { 
        return applicant; 
    }
    
    /**
     * Gets the message content of this enquiry.
     * @return The enquiry message.
     */
    public String getMessage() { 
        return message; 
    }
    
    /**
     * Gets the response to this enquiry, if any.
     * @return The response message, or null if no response yet.
     */
    public String getResponse() { 
        return response; 
    }
    
    /**
     * Gets the current status of this enquiry.
     * @return The current EnquiryStatus (PENDING or REPLIED).
     */
    public EnquiryStatus getStatus() { 
        return status; 
    }

    /**
     * Updates the enquiry message.
     * Only allowed if the enquiry status is still PENDING.
     * 
     * @param message The new message content
     */
    public void setMessage(String message) {
        if (status == EnquiryStatus.PENDING) 
            this.message = message;
    }

    /**
     * Checks if the enquiry can be edited or deleted.
     * An enquiry can only be edited or deleted if it has not been replied to yet.
     * 
     * @return true if the enquiry can be edited or deleted, false otherwise
     */
    public boolean isEditableOrDeletable() {
        return status == EnquiryStatus.PENDING;
    }

    /**
     * Adds a response to this enquiry and updates its status to REPLIED.
     * 
     * @param responseMessage The response message to add
     */
    public void reply(String responseMessage) {
        this.response = responseMessage;
        this.status = EnquiryStatus.REPLIED;
    }

    /**
     * Gets the enquiry details as a formatted string.
     * 
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
