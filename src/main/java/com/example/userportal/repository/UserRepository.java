package com.example.userportal.repository;

import com.example.userportal.entity.User;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface UserRepository extends JpaRepository<User, Long> 
{

@Query("SELECT u FROM User u WHERE (u.email = :username OR u.mobile = :username) AND u.password = :password")
Optional<User> findByEmailOrMobileAndPassword(String username, String password);

@Query("SELECT u FROM User u WHERE u.email = :identifier OR u.mobile = :identifier")
Optional<User> findByEmailOrMobile(String identifier);
}