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
    
    // Getters and setters
    public String getProjectID() {
        return projectID;
    }
    
    public void setProjectID(String projectID) {
        this.projectID = projectID;
    }
    
    public String getProjectName() {
        return projectName;
    }
    
    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }
    
    public String getNeighborhood() {
        return neighborhood;
    }
    
    public void setNeighborhood(String neighborhood) {
        this.neighborhood = neighborhood;
    }
    
    public FlatType[] getFlatType() {
        return flatType;
    }
    
    public void setFlatType(FlatType[] flatType) {
        this.flatType = flatType;
    }
    
    public double getFloorCount() {
        return floorCount;
    }
    
    public void setFloorCount(double floorCount) {
        this.floorCount = floorCount;
    }
    
    public double getPricePerFlat() {
        return pricePerFlat;
    }
    
    public void setPricePerFlat(double pricePerFlat) {
        this.pricePerFlat = pricePerFlat;
    }
    
    public double getThresholdPrice() {
        return thresholdPrice;
    }
    
    public void setThresholdPrice(double thresholdPrice) {
        this.thresholdPrice = thresholdPrice;
    }
    
    public String getApplicationOpenDate() {
        return applicationOpenDate;
    }
    
    public void setApplicationOpenDate(String applicationOpenDate) {
        this.applicationOpenDate = applicationOpenDate;
    }
    
    public String getApplicationCloseDate() {
        return applicationCloseDate;
    }
    
    public void setApplicationCloseDate(String applicationCloseDate) {
        this.applicationCloseDate = applicationCloseDate;
    }
    
    public boolean isVisible() {
        return isVisible;
    }
    
    public void setVisible(boolean visible) {
        isVisible = visible;
    }
    
    public int getAvailableOfficerSlots() {
        return availableOfficerSlots;
    }
    
    public void setAvailableOfficerSlots(int availableOfficerSlots) {
        this.availableOfficerSlots = availableOfficerSlots;
    }
    
    public int getTwoRoomUnitsAvailable() {
        return twoRoomUnitsAvailable;
    }
    
    public void setTwoRoomUnitsAvailable(int twoRoomUnitsAvailable) {
        this.twoRoomUnitsAvailable = twoRoomUnitsAvailable;
    }
    
    public int getThreeRoomUnitsAvailable() {
        return threeRoomUnitsAvailable;
    }
    
    public void setThreeRoomUnitsAvailable(int threeRoomUnitsAvailable) {
        this.threeRoomUnitsAvailable = threeRoomUnitsAvailable;
    }
    
    public String getManagerInCharge() {
        return managerInCharge;
    }
    
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
