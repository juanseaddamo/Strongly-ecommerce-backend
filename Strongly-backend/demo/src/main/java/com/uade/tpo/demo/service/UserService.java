package com.uade.tpo.demo.service;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import com.uade.tpo.demo.entity.User;

public interface UserService {

    public User createUser(String fullName, String email, String password);

    public Optional<User> getUserById(Long id);

    public Page<User> getUsers(PageRequest pageRequest);

    public void deleteUser(Long id);
}
