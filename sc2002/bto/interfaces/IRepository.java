package sc2002.bto.interfaces;

import java.util.List;
/**
 * Generic repository interface that defines standard operations
 * for data access across different entity types.
 * 
 * @param <T> The entity type this repository manages
 */
public interface IRepository<T> {
    /**
     * Retrieves an entity by its ID.
     * 
     * @param id The ID of the entity to retrieve
     * @return The entity with the specified ID, or null if not found
     */
    T getById(String id);
     /**
     * Retrieves all entities of type T.
     * 
     * @return A list containing all entities
     */
    List<T> getAll();
    /**
     * Adds a new entity to the repository.
     * 
     * @param item The entity to add
     */
    void add(T item);
    /**
     * Updates an existing entity in the repository.
     * 
     * @param item The entity with updated information
     */
    void update(T item);
    /**
     * Deletes an entity with the specified ID.
     * 
     * @param id The ID of the entity to delete
     */
    void delete(String id);
}
