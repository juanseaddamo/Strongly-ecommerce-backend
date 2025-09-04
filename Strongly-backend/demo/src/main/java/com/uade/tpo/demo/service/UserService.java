package com.uade.tpo.demo.service;

import java.util.List;

import com.uade.tpo.demo.entity.User;

public interface UserService {

    public User createUser(String fullName, String email, String password);

    public User getUserById(Long id);

    public List<User> getAllUsers();

    public void deleteUser(Long id);
}
