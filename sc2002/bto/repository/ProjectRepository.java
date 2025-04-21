package sc2002.bto.repository;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import sc2002.bto.entity.Project;
import sc2002.bto.interfaces.IRepository;

/**
 * Repository for managing Project entities in the BTO system.
 * Provides methods to add, retrieve, update, and delete project records.
 * 
 */
public class ProjectRepository implements IRepository<Project> {
    /** In-memory storage of project records */
    private List<Project> projects = new ArrayList<>();
    
    /**
     * Retrieves a project by its ID.
     * 
     * @param id The ID of the project to retrieve
     * @return The project with the specified ID, or null if not found
     */
    public Project getById(String id) {
        return projects.stream()
                .filter(p -> p.getProjectID().equals(id))
                .findFirst()
                .orElse(null);
    }
    
    /**
     * Retrieves all projects stored in the repository.
     * 
     * @return A list containing all projects
     */
    public List<Project> getAll() {
        return new ArrayList<>(projects);
    }
    
    /**
     * Adds a new project to the repository if a project with the same ID doesn't already exist.
     * 
     * @param project The project to add
     */
    @Override
    public void add(Project project) {
        if (projects.stream().noneMatch(p -> p.getProjectID().equals(project.getProjectID()))) {
            projects.add(project);
        }
    }    
    
    /**
     * Updates an existing project in the repository.
     * 
     * @param item The project with updated information
     */
    @Override
    public void update(Project item) {
        for (int i = 0; i < projects.size(); i++) {
            if (projects.get(i).getProjectID().equals(item.getProjectID())) {
                projects.set(i, item);
                return;
            }
        }
    }
    
    /**
     * Deletes a project with the specified ID.
     * 
     * @param id The ID of the project to delete
     */
    @Override
    public void delete(String id) {
        projects.removeIf(p -> p.getProjectID().equals(id));
    }
    
    /**
     * Finds projects by neighborhood location.
     * 
     * @param neighborhood The neighborhood to search for
     * @return A list of projects in the specified neighborhood
     */
    public List<Project> findByNeighborhood(String neighborhood) {
        return projects.stream()
                .filter(p -> p.getNeighborhood().equalsIgnoreCase(neighborhood))
                .collect(Collectors.toList());
    }
}
