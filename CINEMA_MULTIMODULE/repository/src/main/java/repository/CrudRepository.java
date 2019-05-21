package repository;

import java.util.List;
import java.util.Optional;

/**
 * @author Marcin Lupa
 * interface create to work with repository classes
 * @param <T>type of class used in repository
 * @param <ID> integer id used to delate or find
 */
public interface CrudRepository<T, ID> {
    void add(T t);

    void update(T t);

    void delete(ID id);

    void deleteAll();

    Optional<T> findById(ID id);

    List<T> findAll();

}
