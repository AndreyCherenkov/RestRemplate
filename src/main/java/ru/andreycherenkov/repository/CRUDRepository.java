package ru.andreycherenkov.repository;

import java.util.List;

public interface CRUDRepository<T, K>  {

    T findById(K id);

    boolean deleteById(K id);

    List<T> findAll();

    T save(T t);

}
