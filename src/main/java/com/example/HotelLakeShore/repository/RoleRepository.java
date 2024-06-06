package com.example.HotelLakeShore.repository;

import com.example.HotelLakeShore.model.Role;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RoleRepository extends JpaRepository<Role,Long> {
    public Optional<Role> findByRoleName(String role);
    public boolean existsByRoleName(Role role);
}
