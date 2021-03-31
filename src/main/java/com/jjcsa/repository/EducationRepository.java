package com.jjcsa.repository;

import com.jjcsa.model.Education;
import com.jjcsa.model.User;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface EducationRepository extends CrudRepository<Education, UUID> {
    List<Education> findAllByUser(User user);
}
