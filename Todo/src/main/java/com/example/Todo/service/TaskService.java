package com.example.Todo.service;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.example.Todo.model.Task;
import com.example.Todo.repository.TaskRepository;

@Service
public class TaskService {
    private final TaskRepository taskRepository;

    public TaskService(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    public List<Task> getAllTasks() {
        return taskRepository.findAll();
    }

   public Task getTaskById(Long id) {
    return taskRepository.findById(id)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Task not found with id: " + id));
}
    public Task saveTask(Task task) {
        return taskRepository.save(task);
    }

    public void deleteTask(Long id) {
        taskRepository.deleteById(id);
    }

    public void updateTaskStatus(Long id, boolean completed) {
        Task task = taskRepository.findById(id).orElse(null);
        if (task != null) {
            task.setCompleted(completed);
            taskRepository.save(task);
        }
    }
    public List<Task> getFilteredTasks(String name, Boolean completed) {
        List<Task> tasks = taskRepository.findAll();
    
        if (name != null && !name.isEmpty()) {
            tasks = tasks.stream()
                         .filter(task -> task.getName().toLowerCase().contains(name.toLowerCase()))
                         .toList();
        }
    
        if (completed != null) {
            tasks = tasks.stream()
                         .filter(task -> task.isCompleted() == completed)
                         .toList();
        }
    
        return tasks;
    }

    public List<Task> getTasksByName(String name) {
        return taskRepository.findByNameContainingIgnoreCase(name);
    }
    
    public List<Task> getTasksByStatus(boolean completed) {
        return taskRepository.findByCompleted(completed);
    }
    
    public List<Task> getTasksByNameAndStatus(String name, boolean completed) {
        return taskRepository.findByNameContainingIgnoreCaseAndCompleted(name, completed);
    }
    
    
}
