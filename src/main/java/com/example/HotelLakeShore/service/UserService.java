package com.example.HotelLakeShore.service;

import com.example.HotelLakeShore.model.User;
import java.util.List;

public interface UserService {
    User registerUser(User user);
    List<User> getUsers();
    void deleteUser(String email);
    User getUserByEmail(String email);


}
