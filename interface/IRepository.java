package sc2002.bto.interfaces;

import java.util.List;

public interface IRepository<T> {
    T getById(String id);
    List<T> getAll();
    void add(T item);
    void update(T item);
    void delete(String id);
}
