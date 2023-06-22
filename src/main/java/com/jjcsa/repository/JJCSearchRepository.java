package com.jjcsa.repository;

import com.jjcsa.model.JJCSearch;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface JJCSearchRepository extends JpaRepository<JJCSearch, String> {
}
