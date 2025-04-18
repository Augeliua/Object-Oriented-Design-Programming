package sc2002.bto.repository;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import sc2002.bto.entity.Enquiry;
import sc2002.bto.entity.Project;
import sc2002.bto.interfaces.IRepository;

public class EnquiryRepository implements IRepository<Enquiry> {
    private List<Enquiry> enquiries = new ArrayList<>();
    
    @Override
    public Enquiry getById(String id) {
        return enquiries.stream()
                .filter(e -> e.getEnquiryId().equals(id))
                .findFirst()
                .orElse(null);
    }
    
    @Override
    public List<Enquiry> getAll() {
        return new ArrayList<>(enquiries);
    }
    
    @Override
    public void add(Enquiry item) {
        enquiries.add(item);
    }
    
    @Override
    public void update(Enquiry item) {
        for (int i = 0; i < enquiries.size(); i++) {
            if (enquiries.get(i).getEnquiryId().equals(item.getEnquiryId())) {
                enquiries.set(i, item);
                return;
            }
        }
    }
    
    @Override
    public void delete(String id) {
        enquiries.removeIf(e -> e.getEnquiryId().equals(id));
    }
    
    public List<Enquiry> findByProject(Project project) {
        return enquiries.stream()
                .filter(e -> e.getProject() != null && 
                         e.getProject().getProjectID().equals(project.getProjectID()))
                .collect(Collectors.toList());
    }
}

