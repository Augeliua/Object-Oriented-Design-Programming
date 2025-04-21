package sc2002.bto.repository;

import java.util.ArrayList;
import java.util.List;
import sc2002.bto.entity.User;
import sc2002.bto.interfaces.IRepository;
/**
 * Repository for managing User entities in the BTO system.
 * Provides methods to add, retrieve, update, and delete user records.
 * 
 */
public class UserRepository implements IRepository<User> {
    /** In-memory storage of user records */
    private List<User> users = new ArrayList<>();
    
    /**
     * Retrieves a user by their ID (NRIC).
     * 
     * @param id The ID of the user to retrieve
     * @return The user with the specified ID, or null if not found
     */
    @Override
    public User getById(String id) {
        return users.stream()
                .filter(u -> u.getId().equals(id))
                .findFirst()
                .orElse(null);
    }
    
    /**
     * Retrieves all users stored in the repository.
     * 
     * @return A list containing all users
     */
    @Override
    public List<User> getAll() {
        return new ArrayList<>(users);
    }
    
    /**
     * Adds a new user to the repository.
     * 
     * @param item The user to add
     */
    @Override
    public void add(User item) {
        users.add(item);
    }
    
    /**
     * Updates an existing user in the repository.
     * 
     * @param item The user with updated information
     */
    @Override
    public void update(User item) {
        for (int i = 0; i < users.size(); i++) {
            if (users.get(i).getId().equals(item.getId())) {
                users.set(i, item);
                return;
            }
        }
    }
    
    /**
     * Deletes a user with the specified ID.
     * 
     * @param id The ID of the user to delete
     */
    @Override
    public void delete(String id) {
        users.removeIf(u -> u.getId().equals(id));
    }
    
    /**
     * Finds a user by their username.
     * 
     * @param username The username to search for
     * @return The user with the specified username, or null if not found
     */
    public User findByUsername(String username) {
        return users.stream()
                .filter(u -> u.getName().equals(username))
                .findFirst()
                .orElse(null);
    }
}
