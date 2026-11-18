package com.pickit.backend.auth.repository;

import com.pickit.backend.auth.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
}
