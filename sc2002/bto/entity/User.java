package sc2002.bto.entity;

import sc2002.bto.enums.MaritalStatus;

/**
 * Represents a user in the BTO Management System.
 * This is the base class for all types of users including Applicants, HDB
 * Officers, and HDB Managers.
 * It contains common attributes and functionalities shared by all user types.
 * 
 */
public class User {
    /** Unique identifier for the user (NRIC) */
    private String id;
    /** The user's name */
    private String name;
    /** The user's password for authentication */
    private String password;
    /** The user's age */
    private int age;
    /** The user's marital status */
    private MaritalStatus maritalStatus;

    /**
     * Creates a new user with the specified details.
     * 
     * @param id            The user's NRIC as a unique identifier
     * @param name          The user's full name
     * @param password      The user's password
     * @param age           The user's age
     * @param maritalStatus The user's marital status (SINGLE or MARRIED)
     */
    public User(String id, String name, String password, int age, MaritalStatus maritalStatus) {
        this.id = id;
        this.name = name;
        this.password = password;
        this.age = age;
        this.maritalStatus = maritalStatus;
    }

    /**
     * Gets the user's unique identifier (NRIC).
     * 
     * @return The user's ID
     */
    public String getId() {
        return id;
    }

    /**
     * Sets the user's unique identifier.
     * 
     * @param id The new ID for the user
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * Gets the user's name.
     * 
     * @return The user's name
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the user's name.
     * 
     * @param name The new name for the user
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Gets the user's password.
     * 
     * @return The user's password
     */
    public String getPassword() {
        return password;
    }

    /**
     * Sets the user's password.
     * 
     * @param password The new password for the user
     */
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * Gets the user's age.
     * 
     * @return The user's age in years
     */
    public int getAge() {
        return age;
    }

    /**
     * Sets the user's age.
     * 
     * @param age The new age for the user
     */
    public void setAge(int age) {
        this.age = age;
    }

    /**
     * Gets the user's marital status.
     * 
     * @return The marital status (SINGLE or MARRIED)
     */
    public MaritalStatus getMaritalStatus() {
        return maritalStatus;
    }

    /**
     * Sets the user's marital status.
     * 
     * @param maritalStatus The new marital status for the user
     */
    public void setMaritalStatus(MaritalStatus maritalStatus) {
        this.maritalStatus = maritalStatus;
    }

    /**
     * Authenticates a user with the provided credentials.
     * 
     * @param username The username (typically same as name)
     * @param password The password to verify
     * @return true if authentication is successful, false otherwise
     */
    public boolean login(String username, String password) {
        // Compare the provided username and password with the stored values
        return this.name.equals(username) && this.password.equals(password);
    }

    /**
     * Changes the user's password to a new value.
     * 
     * @param newPassword The new password to set
     */
    public void changePassword(String newPassword) {
        this.password = newPassword;
    }

    /**
     * Creates a new user account with the specified information.
     * This method performs validation on the input parameters before creating the
     * user.
     * 
     * @param id            The user's NRIC as a unique identifier
     * @param name          The user's full name
     * @param password      The user's password (must be at least 6 characters)
     * @param age           The user's age (must be at least 18)
     * @param maritalStatus The user's marital status (SINGLE or MARRIED)
     * @return A new User object if signup is successful
     * @throws IllegalArgumentException If any validation fails
     */
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
