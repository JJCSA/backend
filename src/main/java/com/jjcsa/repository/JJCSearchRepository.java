package com.jjcsa.repository;

import com.jjcsa.model.JJCSearch;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface JJCSearchRepository extends PagingAndSortingRepository<JJCSearch, String> {
}
