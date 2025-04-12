public class Project {
    private String projectID;
    private String neighborhood;
    private FlatType[] flatType;
    private double floorCount;
    private double pricePerFlat;
    private double thresholdPrice;
    private String applicationOpenDate;
    private String applicationCloseDate;
    private boolean isVisible;
    private int availableOfficerSlots;
    private int twoRoomUnitsAvailable;
    private int threeRoomUnitsAvailable;
    
    // Constructor
    public Project(String projectID, String neighborhood, FlatType[] flatType, 
                   double floorCount, double pricePerFlat, double thresholdPrice,
                   String applicationOpenDate, String applicationCloseDate,
                   boolean isVisible, int availableOfficerSlots,
                   int twoRoomUnitsAvailable, int threeRoomUnitsAvailable) {
        this.projectID = projectID;
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
    
    // Helper method to get units available for a specific flat type
    public int getUnitsAvailable(FlatType type) {
        if (type == FlatType.TWO_ROOM) {
            return twoRoomUnitsAvailable;
        } else if (type == FlatType.THREE_ROOM) {
            return threeRoomUnitsAvailable;
        }
        return 0;
    }
}