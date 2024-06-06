package com.example.HotelLakeShore.repository;

import com.example.HotelLakeShore.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User,Long> {
    public boolean existsByEmail(String email);

    public void deleteByEmail(String email);

    public Optional<User> findByEmail(String email);
}
