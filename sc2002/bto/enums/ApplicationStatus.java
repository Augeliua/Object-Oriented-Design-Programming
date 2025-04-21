package sc2002.bto.enums;
/**
 * Represents the possible statuses of a BTO application.
 * These statuses track the application through its lifecycle from
 * submission to completion.
 * 
 */
public enum ApplicationStatus {
    /**
     * Initial status upon application submission - no decision made yet
     */
    PENDING,
    /**
     * Application has been approved - applicant is invited to book a flat
     */
    SUCCESSFUL,
    /**
     * Application has been rejected - applicant cannot proceed to booking
     */
    UNSUCCESSFUL,
     /**
     * Applicant has successfully booked a flat
     */
    BOOKED
}

