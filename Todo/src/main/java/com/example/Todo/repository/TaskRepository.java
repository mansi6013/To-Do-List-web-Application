package com.example.Todo.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.Todo.model.Task;
import com.example.Todo.model.User;

public interface TaskRepository extends JpaRepository<Task, Long> {
    List<Task> findByNameContainingIgnoreCase(String name);
    List<Task> findByCompleted(boolean completed);
    List<Task> findByNameContainingIgnoreCaseAndCompleted(String name, boolean completed);
    List<Task> findByUser(User user);
    List<Task> findByUserAndNameContainingIgnoreCase(User user, String keyword);
    List<Task> findByDueDate(LocalDate dueDate);


}

