package sc2002.bto.interfaces;

import sc2002.bto.entity.Enquiry;
/**
 * Defines operations for managing enquiries in the BTO system.
 * This interface is implemented by classes that need to handle and respond to enquiries.
 * 
 */
public interface IEnquiryManagement {
    /**
     * Handles an enquiry by processing its details.
     * 
     * @param e The enquiry to handle
     */
    void handleEnquiry(Enquiry e);
    /**
     * Responds to an enquiry with the specified message.
     * 
     * @param e The enquiry to respond to
     * @param r The response message
     */
    void respondToEnquiry(Enquiry e, String r);
}
