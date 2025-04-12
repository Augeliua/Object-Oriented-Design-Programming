public interface IRepository<T> {
    T getById(String id);
    List<T> getAll();
    void add(T item);
    void update(T item);
}