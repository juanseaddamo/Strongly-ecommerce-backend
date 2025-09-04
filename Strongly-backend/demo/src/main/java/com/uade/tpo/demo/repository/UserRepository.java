package com.uade.tpo.demo.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.uade.tpo.demo.entity.User;

public interface UserRepository extends JpaRepository<User, Long>{
    @Query("select u from User u where u.id = ?1 and u.isActive = true")
    Optional<User> findByIdAndIsActiveTrue(Long id);

    @Query("select u from User u where u.email = ?1 and u.isActive = true")
    Optional<User> findByEmailAndIsActiveTrue(String email);

    @Query("select u from User u where u.fullName = ?1 and u.isActive = true")
    Optional<User> findByFullNameAndIsActiveTrue(String fullName);

    @Query("select u from User u where u.isActive = true")
    List<User> findAllActiveUsers();

}
