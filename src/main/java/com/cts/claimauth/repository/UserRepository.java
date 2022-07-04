package com.cts.claimauth.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.cts.claimauth.models.User;

@Repository
public interface UserRepository extends JpaRepository<User,Long> {
  Optional<User> findByUserId(Integer userId);
  
  Boolean existsByUserId(Integer userId);
  
  Boolean existsByEmail(String email);
}
