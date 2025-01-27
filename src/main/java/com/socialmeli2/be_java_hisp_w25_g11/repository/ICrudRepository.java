package com.socialmeli2.be_java_hisp_w25_g11.repository;

import java.util.List;
import java.util.Optional;

public interface ICrudRepository <T, ID> {
    List<T> getAll();
    List<T> createAll(List<T> entities);
    T create(T user);
    Optional<T> findById(ID id);
    boolean update(ID id, T user);
    boolean delete(ID id);
    void clearData();
}
