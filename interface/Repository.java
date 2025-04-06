public interface Repository<T> {
    T getById(String id);
    void add(T item);
    void update(T item);
    void delete(String id);
}