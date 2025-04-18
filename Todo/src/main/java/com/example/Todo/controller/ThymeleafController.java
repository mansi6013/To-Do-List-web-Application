package com.example.Todo.controller;

import java.util.Arrays;
import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.client.RestTemplate;

import com.example.Todo.model.Task;

@Controller
public class ThymeleafController {

    private final RestTemplate restTemplate;

    public ThymeleafController() {
        this.restTemplate = new RestTemplate();
    }

    @GetMapping("/view-tasks")
    public String showTasks(Model model) {
        String apiUrl = "http://localhost:8080/api/tasks";
        Task[] tasks = restTemplate.getForObject(apiUrl, Task[].class);
        List<Task> taskList = Arrays.asList(tasks);
        model.addAttribute("tasks", taskList);
        return "tasks";
    }
}