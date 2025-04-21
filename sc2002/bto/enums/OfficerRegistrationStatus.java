package sc2002.bto.enums;
/**
 * Represents the status of an HDB Officer's registration to handle a project.
 * 
 */
public enum OfficerRegistrationStatus {
    /**
     * Registration request has been submitted but not yet approved or rejected
     */
    PENDING,
    /**
     * Registration request has been approved by the HDB Manager
     */
    APPROVED,
    /**
     * Registration request has been rejected by the HDB Manager
     */
    REJECTED
}
