package com.jjcsa.repository;

import com.jjcsa.dto.IUserStatusCountDTO;
import com.jjcsa.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public interface UserRepository extends JpaRepository<User, String> {
    User findUserByEmail(String email);

    Boolean existsByEmail(String email);

    @Query(value = "SELECT user_status as userStatus, COUNT(id) as userStatusCount FROM user_account GROUP BY user_status", nativeQuery = true)
    List<IUserStatusCountDTO> getUserStatusCount();

    @Query(value = "SELECT first_name || ' ' || last_name AS full_name FROM user_account WHERE user_status='NewUser'", nativeQuery = true)
    List<String> getListOfNewUsers();
}
