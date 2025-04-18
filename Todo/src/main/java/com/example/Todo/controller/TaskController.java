package com.example.Todo.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.Todo.model.Task;
import com.example.Todo.service.TaskService;

@RestController
@RequestMapping("/api/tasks")
public class TaskController {

    private final TaskService taskService;

    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }   

    @GetMapping
    public List<Task> getTasks(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) Boolean completed) {

        if (name != null && completed != null) {
            return taskService.getTasksByNameAndStatus(name, completed);
        } else if (name != null) {
            return taskService.getTasksByName(name);
        } else if (completed != null) {
            return taskService.getTasksByStatus(completed);
        } else {
            return taskService.getAllTasks();
        }
    }

    // Get task by ID
    @GetMapping("/{id}")
    public Task getTaskById(@PathVariable Long id) {
        return taskService.getTaskById(id);
    }
    // Create new task
    @PostMapping
    public Task createTask(@RequestBody Task task) {
        return taskService.saveTask(task);
    }
    @GetMapping("/add-task")
    public String showAddTaskForm() {
        return "add-task";
    }

    // Update task
    @PutMapping("/{id}")
    public Task updateTask(@PathVariable Long id, @RequestBody Task updatedTask) {
        Task task = taskService.getTaskById(id); // will throw 404 if not found
        task.setDescription(updatedTask.getDescription());
        task.setCompleted(updatedTask.isCompleted());
        return taskService.saveTask(task);
    }
    

    // Delete task
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTask(@PathVariable Long id) {
        taskService.getTaskById(id); // check if it exists, or 404
        taskService.deleteTask(id);
        return ResponseEntity.noContent().build();
    }

    // Update task status using query param (e.g. ?completed=true)
    @PatchMapping("/{id}/status")
    public Task updateTaskStatus(@PathVariable Long id, @RequestParam boolean completed) {
        taskService.updateTaskStatus(id, completed);
        return taskService.getTaskById(id);
    }
    
}
