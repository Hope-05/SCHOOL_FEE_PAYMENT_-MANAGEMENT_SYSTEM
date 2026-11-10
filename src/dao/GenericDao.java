/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dao;

import java.util.List;

/**
 *
 * @author shema
 */
public interface GenericDao<T, ID> {
    
    void save(T entity);
    
    T findById(ID id);
    
    List<T> findAll();
    
    void update(T entity);
    
    void delete(T entity);
    
}

