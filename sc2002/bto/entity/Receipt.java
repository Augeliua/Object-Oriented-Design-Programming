package sc2002.bto.entity;

import sc2002.bto.enums.FlatType;
/**
 * Represents a receipt for a successful flat booking in the BTO system.
 * Contains applicant details, project information, and booking information.
 * 
 */
public class Receipt {
     /** Counter for generating unique receipt IDs */
     private static int receiptCounter = 0;
    
     /** Unique identifier for this receipt */
     private String receiptID;
     /** Name of the applicant */
     private String name;
     /** NRIC of the applicant */
     private String nric;
     /** Age of the applicant */
     private int age;
     /** Marital status of the applicant */
     private String maritalStatus;
     /** ID of the project */
     private String projectID;
     /** Neighborhood of the project */
     private String neighborhood;
     /** Price per flat unit */
     private double pricePerFlat;
     /** Type of flat booked */
     private FlatType flatType;
     /** Date of booking */
     private String bookingDate;
     
     /**
      * Creates a new receipt with a generated ID.
      */
    public Receipt() {
        this.receiptID = generateNextReceiptID();
    }
    
    /**
     * Creates a new receipt with the specified details.
     * 
     * @param id Receipt ID
     * @param name Applicant name
     * @param nric Applicant NRIC
     * @param age Applicant age
     * @param maritalStatus Applicant marital status
     * @param projID Project ID
     * @param neighborhood Project neighborhood
     * @param price Price per flat
     * @param f Flat type
     * @param date Booking date
     */
    public Receipt(String id, String name, String nric, int age, String maritalStatus, 
                  String projID, String neighborhood, double price, FlatType f, String date) {
        this.receiptID = id;
        this.name = name;
        this.nric = nric;
        this.age = age;
        this.maritalStatus = maritalStatus;
        this.projectID = projID;
        this.neighborhood = neighborhood;
        this.pricePerFlat = price;
        this.flatType = f;
        this.bookingDate = date;
    }
    
    /**
     * Generates a unique receipt ID.
     * 
     * @return A new unique receipt ID
     */
    private synchronized String generateNextReceiptID() {
        receiptCounter++;
        return String.format("RCPT-%05d", receiptCounter);
    }
    
    public String getReceiptID() {
        return receiptID;
    }
    
    public void setReceiptId(String receiptId) {
        this.receiptID = receiptId;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public String getNRIC() {
        return nric;
    }
    
    public void setNRIC(String nric) {
        this.nric = nric;
    }
    
    public int getAge() {
        return age;
    }
    
    public void setAge(int age) {
        this.age = age;
    }
    
    public String getMaritalStatus() {
        return maritalStatus;
    }
    
    public void setMaritalStatus(String maritalStatus) {
        this.maritalStatus = maritalStatus;
    }
    
    public String getProjectID() {
        return projectID;
    }
    
    public void setProjectID(String projID) {
        this.projectID = projID;
    }
    
    public String getNeighborhood() {
        return neighborhood;
    }
    
    public void setNeighborhood(String neighborhood) {
        this.neighborhood = neighborhood;
    }
    
    public double getPricePerFlat() {
        return pricePerFlat;
    }
    
    public void setPricePerFlat(double price) {
        this.pricePerFlat = price;
    }
    
    public FlatType getFlatType() {
        return flatType;
    }
    
    public void setFlatType(FlatType flatType) {
        this.flatType = flatType;
    }
    
    public String getBookingDate() {
        return bookingDate;
    }
    
    public void setBookingDate(String bookingDate) {
        this.bookingDate = bookingDate;
    }
    
    /**
     * Gets the receipt details as a formatted string.
     * 
     * @return A string containing all the receipt details
     */
    public String getReceiptDetailsAsString() {
        StringBuilder sb = new StringBuilder();
        sb.append("---------------\n");
        sb.append("Receipt Details\n");
        sb.append("---------------\n");
        sb.append("Receipt ID: ").append(receiptID).append("\n\n");
            
        sb.append("Applicant Information:\n");
        sb.append("Name: ").append(name).append("\n");
        sb.append("NRIC: ").append(nric).append("\n");
        sb.append("Age: ").append(age).append("\n");
        sb.append("Marital Status: ").append(maritalStatus).append("\n\n");
            
        sb.append("Project Information:\n");
        sb.append("Project ID: ").append(projectID).append("\n");
        sb.append("Neighborhood: ").append(neighborhood).append("\n");
        sb.append("Price per Flat: $").append(pricePerFlat).append("\n\n");
            
        sb.append("Booking Information:\n");
        sb.append("Flat Type: ").append(flatType).append("\n");
        sb.append("Booking Date: ").append(bookingDate);
        
        return sb.toString();
    }

    /**
     * Prints the receipt details to the console.
     */
    public void printReceiptDetails() {
        System.out.println(getReceiptDetailsAsString());
    }
    
}
