package com.example.HotelLakeShore.service;


import com.example.HotelLakeShore.exception.RoleAlreadyExistsException;
import com.example.HotelLakeShore.exception.UserAlreadyExistsException;
import com.example.HotelLakeShore.model.Role;
import com.example.HotelLakeShore.model.User;
import com.example.HotelLakeShore.repository.RoleRepository;
import com.example.HotelLakeShore.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class RoleServiceImpl implements RoleService{

    @Autowired
    public RoleRepository roleRepository;

    @Autowired
    public UserRepository userRepository;

    @Override
    public List<Role> getRoles() {
          return roleRepository.findAll();
    }

    @Override
    public Role createRole(Role theRole) {
        String roleName = "ROLE_"+ theRole.getRoleName().toUpperCase();
        Role role = new Role(roleName);
        if(roleRepository.existsByRoleName(role)){
            throw new RoleAlreadyExistsException(theRole.getRoleName() + "Already Exists");
        }
        return roleRepository.save(role);
    }

    @Override
    public Role findByRoleName(String roleName) {
        return roleRepository.findByRoleName(roleName).get();
    }

    @Override
    public void deleteRole(Long roleId){
        this.removeAllUsersFromRole(roleId);
        roleRepository.deleteById(roleId);
    }

    @Override
    public User removeUserFromRole(Long userId, Long roleId) {
        Optional<User> user = userRepository.findById(userId);
        Optional<Role> role = roleRepository.findById(roleId);

        if(user.isPresent() && user.get().getRoles().contains(role.get())){
            role.get().removeRolefromUser(user.get());
            roleRepository.save(role.get());
            return user.get();
        }
        throw new UsernameNotFoundException("User not found");

    }

    @Override
    public User assignRoleToUser(Long userId, Long roleId) {
        Optional <User> user = userRepository.findById(userId);
        Optional <Role> role = roleRepository.findById(roleId);
        if(!user.isPresent() || !role.isPresent()){
            throw new UsernameNotFoundException("User or role Not Found");
        }
        if(user.isPresent() && user.get().getRoles().contains(role.get())){
            throw new UserAlreadyExistsException(user.get().getFirstName() + "is already asssigned to a role" + role.get().getRoleName());
        }
        if(role.isPresent()){
            role.get().assignRoleToUser(user.get());
            roleRepository.save(role.get());
        }
        return user.get();
    }

    @Override
    public Role removeAllUsersFromRole(Long roleId) {
        Optional <Role> role = roleRepository.findById(roleId);
        role.get().removeAllUsersFromRole();
        return roleRepository.save(role.get());
    }
}
