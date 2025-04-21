package sc2002.bto.repository;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import sc2002.bto.entity.Enquiry;
import sc2002.bto.entity.Project;
import sc2002.bto.interfaces.IRepository;
/**
 * Repository for managing Enquiry entities in the BTO system.
 * Provides methods to add, retrieve, update, and delete enquiry records.
 * 
 */
public class EnquiryRepository implements IRepository<Enquiry> {
    /** In-memory storage of enquiry records */
    private List<Enquiry> enquiries = new ArrayList<>();
    
    /**
     * Retrieves an enquiry by its ID.
     * 
     * @param id The ID of the enquiry to retrieve
     * @return The enquiry with the specified ID, or null if not found
     */
    public Enquiry getById(String id) {
        return enquiries.stream()
                .filter(e -> e.getEnquiryId().equals(id))
                .findFirst()
                .orElse(null);
    }
    
    /**
     * Retrieves all enquiries stored in the repository.
     * 
     * @return A list containing all enquiries
     */
    @Override
    public List<Enquiry> getAll() {
        return new ArrayList<>(enquiries);
    }
    
    /**
     * Adds a new enquiry to the repository.
     * 
     * @param item The enquiry to add
     */
    @Override
    public void add(Enquiry item) {
        enquiries.add(item);
    }
    
     /**
     * Updates an existing enquiry in the repository.
     * 
     * @param item The enquiry with updated information
     */
    @Override
    public void update(Enquiry item) {
        for (int i = 0; i < enquiries.size(); i++) {
            if (enquiries.get(i).getEnquiryId().equals(item.getEnquiryId())) {
                enquiries.set(i, item);
                return;
            }
        }
    }
    
    /**
     * Deletes an enquiry with the specified ID.
     * 
     * @param id The ID of the enquiry to delete
     */
    @Override
    public void delete(String id) {
        enquiries.removeIf(e -> e.getEnquiryId().equals(id));
    }
    
    /**
     * Finds all enquiries related to a specific project.
     * 
     * @param project The project to find enquiries for
     * @return A list of enquiries for the specified project
     */
    public List<Enquiry> findByProject(Project project) {
        return enquiries.stream()
                .filter(e -> e.getProject() != null && 
                         e.getProject().getProjectID().equals(project.getProjectID()))
                .collect(Collectors.toList());
    }
}

