package sc2002.group.proj;

public class Enquiry {
    private String enquiryId;
    private Project project;
    private Applicant applicant;
    private String message;
    private String response;
    private EnquiryStatus status;
}

public Enquiry(String enquiryId, Project project, Applicant applicant, String message) {
    this.enquiryId = enquiryId;
    this.project = project;
    this.applicant = applicant;
    this.message = message;
    this.status = EnquiryStatus.PENDING; // Default status
    this.response = null; // No reply yet
}

//Getters
public String getEnquiryId() { return enquiryId; } // Needed for looking up/editing/deleting
public Project getProject() { return project; } // Used to filter enquiries by project
public Applicant getApplicant() { return applicant; } // Used to check if the applicant owns this enquir
public String getMessage() { return message; } // To display the enquiry content
public String getResponse() { return response; } // To show the officer/manager's reply
public EnquiryStatus getStatus() { return status; } // To control edit/delete permissions


//For applicant use
public void setMessage(String message) {
	if (status == EnquiryStatus.PENDING) 
		this.message = message;
} // Allows the applicant to change their message — but only if the enquiry hasn’t been replied to. 

public boolean isEditableOrDeletable() {
 return status == EnquiryStatus.PENDING;
}


//For officer/manager use
public void reply(String responseMessage) {
 this.response = responseMessage;
 this.status = EnquiryStatus.REPLIED;
} // It sets the response message and updates the status to REPLIED — which locks it from further edits/deletions by the applicant.


