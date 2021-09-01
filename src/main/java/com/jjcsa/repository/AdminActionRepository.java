package com.jjcsa.repository;

import com.jjcsa.model.AdminAction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface AdminActionRepository extends JpaRepository<AdminAction, UUID> {
}
