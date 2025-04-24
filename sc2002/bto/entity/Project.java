package sc2002.bto.entity;

import java.util.HashMap;
import java.util.Map;
import sc2002.bto.enums.FlatType;

/**
 * Represents a Build-To-Order (BTO) housing project.
 * This class contains all information about a BTO project including its location,
 * available units, application dates, and visibility status.
 * 
 */
public class Project {
        /** Unique identifier for the project */
        private String projectID;
        /** Name of the project */
        private String projectName;
        /** Neighborhood where the project is located */
        private String neighborhood;
        /** Types of flats available in this project */
        private FlatType[] flatType;
        /** Number of floors in the project buildings */
        private double floorCount;
        /** Base price per flat unit */
        private double pricePerFlat;
        /** Minimum price threshold for the project */
        private double thresholdPrice;
        /** Date when applications for this project open */
        private String applicationOpenDate;
        /** Date when applications for this project close */
        private String applicationCloseDate;
        /** Visibility status of the project to applicants */
        private boolean isVisible;
        /** Number of HDB officer slots available for this project */
        private int availableOfficerSlots;
        /** Number of 2-room units currently available */
        private int twoRoomUnitsAvailable;
        /** Number of 3-room units currently available */
        private int threeRoomUnitsAvailable;
        /** NRIC of the manager in charge of this project */
        private String managerInCharge;
    
    
    // Constructor
    /**
     * Creates a new BTO project with the specified details.
     * 
     * @param projectID Unique identifier for the project
     * @param neighborhood Area where the project is located
     * @param flatType Array of flat types available in this project
     * @param floorCount Number of floors in the buildings
     * @param pricePerFlat Base price per flat unit
     * @param thresholdPrice Minimum price threshold
     * @param applicationOpenDate Date when applications open (format: YYYY-MM-DD)
     * @param applicationCloseDate Date when applications close (format: YYYY-MM-DD)
     * @param isVisible Whether the project is visible to applicants
     * @param availableOfficerSlots Number of HDB officer slots available
     * @param twoRoomUnitsAvailable Number of 2-room units available
     * @param threeRoomUnitsAvailable Number of 3-room units available
     */
    public Project(String projectID, String neighborhood, FlatType[] flatType, 
                   double floorCount, double pricePerFlat, double thresholdPrice,
                   String applicationOpenDate, String applicationCloseDate,
                   boolean isVisible, int availableOfficerSlots,
                   int twoRoomUnitsAvailable, int threeRoomUnitsAvailable) {
        this.projectID = projectID;
        this.projectName = "Project " + projectID; // Default name if not set
        this.neighborhood = neighborhood;
        this.flatType = flatType;
        this.floorCount = floorCount;
        this.pricePerFlat = pricePerFlat;
        this.thresholdPrice = thresholdPrice;
        this.applicationOpenDate = applicationOpenDate;
        this.applicationCloseDate = applicationCloseDate;
        this.isVisible = isVisible;
        this.availableOfficerSlots = availableOfficerSlots;
        this.twoRoomUnitsAvailable = twoRoomUnitsAvailable;
        this.threeRoomUnitsAvailable = threeRoomUnitsAvailable;
    }
    
    /**
     * Gets the unique ID of this project.
     * @return The project's unique identifier.
     */
    public String getProjectID() {
        return projectID;
    }
    
    /**
     * Sets the project ID.
     * @param projectID The new ID for this project.
     */
    public void setProjectID(String projectID) {
        this.projectID = projectID;
    }
    
    /**
     * Gets the name of this project.
     * @return The project name.
     */
    public String getProjectName() {
        return projectName;
    }
    
    /**
     * Sets the name of this project.
     * @param projectName The new name for this project.
     */
    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }
    
    /**
     * Gets the neighborhood where this project is located.
     * @return The neighborhood name.
     */
    public String getNeighborhood() {
        return neighborhood;
    }
    
    /**
     * Sets the neighborhood for this project.
     * @param neighborhood The new neighborhood location.
     */
    public void setNeighborhood(String neighborhood) {
        this.neighborhood = neighborhood;
    }
    
    /**
     * Gets the flat types available in this project.
     * @return An array of FlatType values.
     */
    public FlatType[] getFlatType() {
        return flatType;
    }
    
    /**
     * Sets the flat types available in this project.
     * @param flatType An array of FlatType values.
     */
    public void setFlatType(FlatType[] flatType) {
        this.flatType = flatType;
    }
    
    /**
     * Gets the number of floors in the project buildings.
     * @return The floor count.
     */
    public double getFloorCount() {
        return floorCount;
    }
    
    /**
     * Sets the number of floors in the project buildings.
     * @param floorCount The new floor count.
     */
    public void setFloorCount(double floorCount) {
        this.floorCount = floorCount;
    }
    
    /**
     * Gets the price per flat in this project.
     * @return The price per flat.
     */
    public double getPricePerFlat() {
        return pricePerFlat;
    }
    
    /**
     * Sets the price per flat in this project.
     * @param pricePerFlat The new price per flat.
     */
    public void setPricePerFlat(double pricePerFlat) {
        this.pricePerFlat = pricePerFlat;
    }
    
    /**
     * Gets the threshold price for this project.
     * @return The threshold price.
     */
    public double getThresholdPrice() {
        return thresholdPrice;
    }
    
    /**
     * Sets the threshold price for this project.
     * @param thresholdPrice The new threshold price.
     */
    public void setThresholdPrice(double thresholdPrice) {
        this.thresholdPrice = thresholdPrice;
    }
    
    /**
     * Gets the application opening date for this project.
     * @return The opening date as a string.
     */
    public String getApplicationOpenDate() {
        return applicationOpenDate;
    }
    
    /**
     * Sets the application opening date for this project.
     * @param applicationOpenDate The new opening date.
     */
    public void setApplicationOpenDate(String applicationOpenDate) {
        this.applicationOpenDate = applicationOpenDate;
    }
    
    /**
     * Gets the application closing date for this project.
     * @return The closing date as a string.
     */
    public String getApplicationCloseDate() {
        return applicationCloseDate;
    }
    
    /**
     * Sets the application closing date for this project.
     * @param applicationCloseDate The new closing date.
     */
    public void setApplicationCloseDate(String applicationCloseDate) {
        this.applicationCloseDate = applicationCloseDate;
    }
    
    /**
     * Checks if this project is visible to applicants.
     * @return true if the project is visible, false otherwise.
     */
    public boolean isVisible() {
        return isVisible;
    }

    /**
     * Sets the visibility status of this project.
     * @param visible The new visibility status.
     */
    public void setVisible(boolean visible) {
        isVisible = visible;
    }

    /**
     * Gets the number of available slots for HDB officers.
     * @return The number of available officer slots.
     */
    public int getAvailableOfficerSlots() {
        return availableOfficerSlots;
    }

    /**
     * Sets the number of available slots for HDB officers.
     * @param availableOfficerSlots The new number of available officer slots.
     */
    public void setAvailableOfficerSlots(int availableOfficerSlots) {
        this.availableOfficerSlots = availableOfficerSlots;
    }

    /**
     * Gets the number of available 2-room units.
     * @return The number of available 2-room units.
     */
    public int getTwoRoomUnitsAvailable() {
        return twoRoomUnitsAvailable;
    }

    /**
     * Sets the number of available 2-room units.
     * @param twoRoomUnitsAvailable The new number of available 2-room units.
     */
    public void setTwoRoomUnitsAvailable(int twoRoomUnitsAvailable) {
        this.twoRoomUnitsAvailable = twoRoomUnitsAvailable;
    }

    /**
     * Gets the number of available 3-room units.
     * @return The number of available 3-room units.
     */
    public int getThreeRoomUnitsAvailable() {
        return threeRoomUnitsAvailable;
    }

    /**
     * Sets the number of available 3-room units.
     * @param threeRoomUnitsAvailable The new number of available 3-room units.
     */
    public void setThreeRoomUnitsAvailable(int threeRoomUnitsAvailable) {
        this.threeRoomUnitsAvailable = threeRoomUnitsAvailable;
    }

    /**
     * Gets the manager in charge of this project.
     * @return The name of the manager in charge.
     */
    public String getManagerInCharge() {
        return managerInCharge;
    }

    /**
     * Sets the manager in charge of this project.
     * @param managerInCharge The name of the new manager in charge.
     */
    public void setManagerInCharge(String managerInCharge) {
        this.managerInCharge = managerInCharge;
    }
    
    /**
     * Gets the number of available units for a specific flat type.
     * 
     * @param type The flat type to check
     * @return The number of available units for the specified flat type
     */
    public int getUnitsAvailable(FlatType type) {
        if (type == FlatType.TWO_ROOM) {
            return twoRoomUnitsAvailable;
        } else if (type == FlatType.THREE_ROOM) {
            return threeRoomUnitsAvailable;
        }
        return 0;
    }
    
    /**
     * Gets a map of all available units by flat type.
     * 
     * @return A map with flat types as keys and the number of available units as values
     */
    public Map<FlatType, Integer> getAvailableUnits() {
        Map<FlatType, Integer> availableUnits = new HashMap<>();
        availableUnits.put(FlatType.TWO_ROOM, twoRoomUnitsAvailable);
        availableUnits.put(FlatType.THREE_ROOM, threeRoomUnitsAvailable);
        return availableUnits;
    }
}
