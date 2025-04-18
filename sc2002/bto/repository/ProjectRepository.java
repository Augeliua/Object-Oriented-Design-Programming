package sc2002.bto.repository;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import sc2002.bto.entity.Project;
import sc2002.bto.interfaces.IRepository;

public class ProjectRepository implements IRepository<Project> {
    private List<Project> projects = new ArrayList<>();
    
    @Override
    public Project getById(String id) {
        return projects.stream()
                .filter(p -> p.getProjectID().equals(id))
                .findFirst()
                .orElse(null);
    }
    
    @Override
    public List<Project> getAll() {
        return new ArrayList<>(projects);
    }
    
    @Override
    public void add(Project project) {
        if (projects.stream().noneMatch(p -> p.getProjectID().equals(project.getProjectID()))) {
            projects.add(project);
        }
    }    
    
    @Override
    public void update(Project item) {
        for (int i = 0; i < projects.size(); i++) {
            if (projects.get(i).getProjectID().equals(item.getProjectID())) {
                projects.set(i, item);
                return;
            }
        }
    }
    
    @Override
    public void delete(String id) {
        projects.removeIf(p -> p.getProjectID().equals(id));
    }
    
    public List<Project> findByNeighborhood(String neighborhood) {
        return projects.stream()
                .filter(p -> p.getNeighborhood().equalsIgnoreCase(neighborhood))
                .collect(Collectors.toList());
    }
}
