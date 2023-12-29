package com.jjcsa.repository;

import com.jjcsa.dto.IUserStatusCountDTO;
import com.jjcsa.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserRepository extends JpaRepository<User, String> {
    User findUserByEmail(String email);

    Boolean existsByEmail(String email);

    @Query(value = "SELECT user_status as userStatus, COUNT(id) as userStatusCount FROM user_account GROUP BY user_status", nativeQuery = true)
    List<IUserStatusCountDTO> getUserStatusCount();
}
