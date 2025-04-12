package sc2002.bto.entity;

import sc2002.bto.enums.FlatType;

public class Receipt {
    private static int receiptCounter = 0;
    
    private String receiptID;
    private String name;
    private String nric;
    private int age;
    private String maritalStatus;
    private String projectID;
    private String neighborhood;
    private double pricePerFlat;
    private FlatType flatType;
    private String bookingDate;
    
    public Receipt() {
        this.receiptID = generateNextReceiptID();
    }
    
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
    
    public void printReceiptDetails() {
        System.out.println("---------------");
        System.out.println("Receipt Details");
        System.out.println("---------------");
        System.out.println("Receipt ID: " + receiptID);
            
        System.out.println("\nApplicant Information:");
        System.out.println("Name: " + name);
        System.out.println("NRIC: " + nric);
        System.out.println("Age: " + age);
        System.out.println("Marital Status: " + maritalStatus);
            
        System.out.println("\nProject Information:");
        System.out.println("Project ID: " + projectID);
        System.out.println("Neighborhood: " + neighborhood);
        System.out.println("Price per Flat: $" + pricePerFlat);
            
        System.out.println("\nBooking Information:");
        System.out.println("Flat Type: " + flatType);
        System.out.println("Booking Date: " + bookingDate);
    }
}
