package sc2002.bto.repository;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import sc2002.bto.entity.User;
import sc2002.bto.interfaces.IRepository;

public class UserRepository implements IRepository<User> {
    private List<User> users = new ArrayList<>();
    
    @Override
    public User getById(String id) {
        return users.stream()
                .filter(u -> u.getId().equals(id))
                .findFirst()
                .orElse(null);
    }
    
    @Override
    public List<User> getAll() {
        return new ArrayList<>(users);
    }
    
    @Override
    public void add(User item) {
        users.add(item);
    }
    
    @Override
    public void update(User item) {
        for (int i = 0; i < users.size(); i++) {
            if (users.get(i).getId().equals(item.getId())) {
                users.set(i, item);
                return;
            }
        }
    }
    
    @Override
    public void delete(String id) {
        users.removeIf(u -> u.getId().equals(id));
    }
    
    public User findByUsername(String username) {
        return users.stream()
                .filter(u -> u.getName().equals(username))
                .findFirst()
                .orElse(null);
    }
}
