package com.users.management.demo.repositories.relational;

import com.users.management.demo.repositories.relational.model.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, Long> {}

