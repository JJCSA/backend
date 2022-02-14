package com.jjcsa.repository;

import com.jjcsa.dto.JjcSearchDto;
import com.jjcsa.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<User, String> {

    User findUserByEmail(String email);

    @Query(value = "SELECT CONCAT(u.first_name ,' ', u.middle_name,' ', u.last_name)as name, u.state, u.city, e.specialization, e.university_name, w.role from public.user_account u LEFT OUTER JOIN public.education e on e.user_id = u.id LEFT OUTER JOIN public.work_ex w on w.user_id = u.id",nativeQuery = true)
    List<JjcSearchDto> findAllJjcSearch();
}
