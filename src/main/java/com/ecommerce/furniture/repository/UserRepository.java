package com.ecommerce.furniture.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ecommerce.furniture.models.User;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
  Optional<User> findByName(String name);

  Boolean existsByName(String name);

  Boolean existsByEmail(String email);

  Optional<User> findByEmail(String email);
}
