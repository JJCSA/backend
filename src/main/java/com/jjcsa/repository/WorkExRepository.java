package com.jjcsa.repository;

import com.jjcsa.model.User;
import com.jjcsa.model.WorkEx;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface WorkExRepository extends CrudRepository<WorkEx, UUID> {
    List<WorkEx> findAllByUser(User user);
}
