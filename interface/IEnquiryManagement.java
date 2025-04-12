package sc2002.bto.interfaces;

import sc2002.bto.entity.Enquiry;

public interface IEnquiryManagement {
    void handleEnquiry(Enquiry e);
    void respondToEnquiry(Enquiry e, String r);
}
