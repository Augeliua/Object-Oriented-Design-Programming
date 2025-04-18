package sc2002.bto.repository;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import sc2002.bto.entity.Applicant;
import sc2002.bto.entity.Application;
import sc2002.bto.interfaces.IRepository;

public class ApplicationRepository implements IRepository<Application> {
    private List<Application> applications = new ArrayList<>();
    
    @Override
    public Application getById(String id) {
        return applications.stream()
                .filter(app -> app.getApplicationId().equals(id))
                .findFirst()
                .orElse(null);
    }
    
    @Override
    public List<Application> getAll() {
        return new ArrayList<>(applications);
    }
    
    @Override
    public void add(Application item) {
        applications.add(item);
    }
    
    @Override
    public void update(Application item) {
        for (int i = 0; i < applications.size(); i++) {
            if (applications.get(i).getApplicationId().equals(item.getApplicationId())) {
                applications.set(i, item);
                return;
            }
        }
    }
    
    @Override
    public void delete(String id) {
        applications.removeIf(app -> app.getApplicationId().equals(id));
    }
    
    public List<Application> findByApplicant(Applicant applicant) {
        return applications.stream()
                .filter(app -> app.getApplicant() != null && 
                         app.getApplicant().getId().equals(applicant.getId()))
                .collect(Collectors.toList());
    }
}

