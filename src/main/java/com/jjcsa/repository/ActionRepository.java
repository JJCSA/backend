package com.jjcsa.repository;

import com.jjcsa.model.Action;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface ActionRepository extends CrudRepository<Action, UUID> {

}
