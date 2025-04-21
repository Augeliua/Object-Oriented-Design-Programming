package sc2002.bto.enums;
/**
 * Represents the marital status of a user in the BTO system.
 * Marital status affects the types of flats an applicant is eligible for.
 * 
 */
public enum MaritalStatus {
    /**
     * Single status - applicants must be at least 35 years old and can only apply for 2-Room flats
     */
    SINGLE,
    /**
     * Married status - applicants must be at least 21 years old and can apply for any flat type
     */
    MARRIED
}
