package com.example.Todo.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.Todo.model.User;

public interface UserRepository extends JpaRepository<User, Long> {
    User findByUsername(String username);
}