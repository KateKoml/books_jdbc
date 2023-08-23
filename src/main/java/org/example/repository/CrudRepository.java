package org.example.repository;

import java.util.List;
import java.util.Optional;

public interface CrudRepository<K, T> {
    T findById(K id);

    Optional<T> findOptionalById(K id);

    List<T> findAll();

    T create(T object);

    T update(T object);

    boolean delete(K id);
}
