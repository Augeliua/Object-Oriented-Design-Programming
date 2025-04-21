package sc2002.bto.interfaces;

import sc2002.bto.entity.Application;
/**
 * Defines operations for processing BTO applications.
 * This interface is implemented by classes responsible for validating and updating applications.
 * 
 */
public interface IApplicationProcessing {
    /**
     * Processes an application by handling the appropriate actions based on its status.
     * 
     * @param a The application to process
     */
    void processApplication(Application a);
    /**
     * Validates an application to ensure all criteria are met.
     * 
     * @param a The application to validate
     */
    void validateApplication(Application a);
    /**
     * Updates the status of application(s) based on current conditions.
     */
    void updateApplicationStatus();
}
