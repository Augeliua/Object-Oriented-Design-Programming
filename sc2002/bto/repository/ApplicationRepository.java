package sc2002.bto.repository;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import sc2002.bto.entity.Applicant;
import sc2002.bto.entity.Application;
import sc2002.bto.interfaces.IRepository;

/**
 * Repository for managing Application entities in the BTO system.
 * Provides methods to add, retrieve, update, and delete application records.
 * 
 */
public class ApplicationRepository implements IRepository<Application> {
    /** In-memory storage of application records */
    private List<Application> applications = new ArrayList<>();
    
    /**
     * Retrieves an application by its ID.
     * 
     * @param id The ID of the application to retrieve
     * @return The application with the specified ID, or null if not found
     */
    public Application getById(String id) {
        return applications.stream()
                .filter(app -> app.getApplicationId().equals(id))
                .findFirst()
                .orElse(null);
    }
    
    /**
     * Retrieves all applications stored in the repository.
     * 
     * @return A list containing all applications
     */
    @Override
    public List<Application> getAll() {
        return new ArrayList<>(applications);
    }
    
    /**
     * Adds a new application to the repository.
     * 
     * @param item The application to add
     */
    @Override
    public void add(Application item) {
        applications.add(item);
    }
    
    /**
     * Updates an existing application in the repository.
     * 
     * @param item The application with updated information
     */
    @Override
    public void update(Application item) {
        for (int i = 0; i < applications.size(); i++) {
            if (applications.get(i).getApplicationId().equals(item.getApplicationId())) {
                applications.set(i, item);
                return;
            }
        }
    }
    
    /**
     * Deletes an application with the specified ID.
     * 
     * @param id The ID of the application to delete
     */
    @Override
    public void delete(String id) {
        applications.removeIf(app -> app.getApplicationId().equals(id));
    }
    
    /**
     * Finds all applications submitted by a specific applicant.
     * 
     * @param applicant The applicant to find applications for
     * @return A list of applications from the specified applicant
     */
    public List<Application> findByApplicant(Applicant applicant) {
        return applications.stream()
                .filter(app -> app.getApplicant() != null && 
                         app.getApplicant().getId().equals(applicant.getId()))
                .collect(Collectors.toList());
    }
}

