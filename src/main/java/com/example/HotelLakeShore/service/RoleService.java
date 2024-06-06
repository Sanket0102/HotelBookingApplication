package com.example.HotelLakeShore.service;

import com.example.HotelLakeShore.model.Role;
import com.example.HotelLakeShore.model.User;

import java.util.List;

public interface RoleService {
    public List<Role> getRoles();
    public Role createRole(Role theRole);
    public Role findByRoleName(String roleName);
    public User removeUserFromRole(Long userId, Long roleId);
    public User assignRoleToUser(Long userId, Long roleId);
    public Role removeAllUsersFromRole(Long roleId);
    public void deleteRole(Long roleId);


}
