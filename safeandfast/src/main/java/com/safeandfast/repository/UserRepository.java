package com.safeandfast.repository;

import com.safeandfast.domain.User;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

     Boolean existsByEmail(String email);

      @EntityGraph(attributePaths = "roles")
     Optional<User> findByEmail(String email);


}
