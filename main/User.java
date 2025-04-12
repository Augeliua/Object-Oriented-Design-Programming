package sc2002.bto.entity;

import sc2002.bto.enums.MaritalStatus;

public class User {
    private String id;
    private String name;
    private String password;
    private int age;
    private MaritalStatus maritalStatus;
    
    public User(String id, String name, String password, int age, MaritalStatus maritalStatus) {
        this.id = id;
        this.name = name;
        this.password = password;
        this.age = age;
        this.maritalStatus = maritalStatus;
    }
    
    // Getters and setters
    public String getId() {
        return id;
    }
    
    public void setId(String id) {
        this.id = id;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public String getPassword() {
        return password;
    }
    
    public void setPassword(String password) {
        this.password = password;
    }
    
    public int getAge() {
        return age;
    }
    
    public void setAge(int age) {
        this.age = age;
    }
    
    public MaritalStatus getMaritalStatus() {
        return maritalStatus;
    }
    
    public void setMaritalStatus(MaritalStatus maritalStatus) {
        this.maritalStatus = maritalStatus;
    }
    
    public boolean login(String username, String password) {
        // Compare the provided username and password with the stored values
        return this.name.equals(username) && this.password.equals(password);
    }
    
    public void changePassword(String newPassword) {
        this.password = newPassword;
    }
    
    public static User signup(String id, String name, String password, int age, MaritalStatus maritalStatus) {
        // Validate input parameters
        if (id == null || id.isEmpty()) {
            throw new IllegalArgumentException("ID cannot be empty");
        }
        
        if (name == null || name.isEmpty()) {
            throw new IllegalArgumentException("Username cannot be empty");
        }
        
        if (password == null || password.length() < 6) {
            throw new IllegalArgumentException("Password must be at least 6 characters long");
        }
        
        if (age < 18) {
            throw new IllegalArgumentException("User must be at least 18 years old");
        }
        
        // Create and return a new User object
        return new User(id, name, password, age, maritalStatus);
    }
}
