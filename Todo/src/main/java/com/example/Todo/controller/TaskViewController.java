package com.example.Todo.controller;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.Todo.model.Task;
import com.example.Todo.model.User;
import com.example.Todo.repository.TaskRepository;
import com.example.Todo.repository.UserRepository;
import com.example.Todo.service.TaskService;

@Controller
@RequestMapping("/tasks")
public class TaskViewController {

    @Autowired
    private TaskService taskService;

    @Autowired
    private UserRepository userRepo;

    @Autowired
    private TaskRepository taskRepository;

    private User getLoggedInUser() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepo.findByUsername(username);
    }
    

    @ModelAttribute("loggedInUsername")
    public String loggedInUsername() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return auth.getName();
    }
    // 📋 Show only this user's tasks
    // @GetMapping("/tasks")
    // public String getUserTasks(Model model) {
    //     User user = getLoggedInUser();
    //     List<Task> tasks = taskRepository.findByUser(user);

    //     LocalDate today = LocalDate.now();

    //     List<Task> dueToday = tasks.stream()
    //         .filter(t -> today.equals(t.getDueDate()) && !t.isCompleted())
    //         .toList();

    //     List<Task> overdue = tasks.stream()
    //         .filter(t -> t.getDueDate() != null && t.getDueDate().isBefore(today) && !t.isCompleted())
    //         .toList();

    //     model.addAttribute("tasks", tasks);
    //     model.addAttribute("dueToday", dueToday);
    //     model.addAttribute("overdue", overdue);

    //     return "tasks";
    // }

    @GetMapping
    public String showUserTasks(@RequestParam(value = "keyword", required = false) String keyword,
                                Model model) {
        User user = getLoggedInUser();
        List<Task> tasks;

        if (keyword != null && !keyword.isEmpty()) {
            tasks = taskRepository.findByUserAndNameContainingIgnoreCase(user, keyword);
        } else {
            tasks = taskRepository.findByUser(user);
        }

        LocalDate today = LocalDate.now();

        List<Task> dueToday = tasks.stream()
            .filter(t -> today.equals(t.getDueDate()) && !t.isCompleted())
            .toList();

        List<Task> overdue = tasks.stream()
            .filter(t -> t.getDueDate() != null && t.getDueDate().isBefore(today) && !t.isCompleted())
            .toList();

        model.addAttribute("tasks", tasks);
        model.addAttribute("keyword", keyword);
        model.addAttribute("dueToday", dueToday);
        model.addAttribute("overdue", overdue);
        model.addAttribute("today", today);

        return "tasks";
    }


    @GetMapping("/overview")
    public String showOverview(Model model) {
        User user = getLoggedInUser();
        List<Task> tasks = taskRepository.findByUser(user);

        long total = tasks.size();
        long completed = tasks.stream().filter(Task::isCompleted).count();
        long incomplete = total - completed;

        Map<String, Long> categoryCounts = tasks.stream()
            .filter(t -> t.getCategory() != null)
            .collect(Collectors.groupingBy(Task::getCategory, Collectors.counting()));

        // 🔔 Reminder Logic
        LocalDate today = LocalDate.now();
        List<Task> dueToday = tasks.stream()
            .filter(t -> today.equals(t.getDueDate()) && !t.isCompleted())
            .toList();

        List<Task> overdue = tasks.stream()
            .filter(t -> t.getDueDate() != null && t.getDueDate().isBefore(today) && !t.isCompleted())
            .toList();

        model.addAttribute("total", total);
        model.addAttribute("completed", completed);
        model.addAttribute("incomplete", incomplete);
        model.addAttribute("categoryCounts", categoryCounts);
        model.addAttribute("events", tasks.stream()
            .filter(t -> t.getDueDate() != null)
            .map(t -> {
                Map<String, Object> e = new HashMap<>();
                e.put("id", t.getId());
                e.put("title", t.getName());
                e.put("start", t.getDueDate().toString());
                e.put("color", t.isCompleted() ? "#198754" : "#0d6efd");
                e.put("description", t.getDescription() != null ? t.getDescription() : "");
                e.put("category", t.getCategory() != null ? t.getCategory() : "Other");
                e.put("completed", t.isCompleted());
                return e;
            }).toList());

        // Pass due/overdue tasks for UI
        model.addAttribute("dueToday", dueToday);
        model.addAttribute("overdue", overdue);

        return "overview";
}


    @SuppressWarnings("unused")
    private String escapeJs(String input) {
        return input == null ? "" : input.replace("\"", "\\\"").replace("'", "\\'");
    }
    
        // ➕ Show form to add new task
        @GetMapping("/add-task")
        public String showAddTaskForm(Model model) {
            model.addAttribute("task", new Task());
            return "add-task";
        }

        // ✅ Handle form submission
        @PostMapping("/add")
        public String addTask(@ModelAttribute Task task) {
            task.setUser(getLoggedInUser());
            taskService.saveTask(task);
            return "redirect:/tasks";
        }


    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model) {
        Task task = taskService.getTaskById(id);
        model.addAttribute("task", task);
        return "edit"; // edit.html should exist in templates
    }

    @PostMapping("/update/{id}")
    public String updateTask(@PathVariable Long id, @ModelAttribute Task updatedTask) {
        Task task = taskService.getTaskById(id);
        task.setName(updatedTask.getName());
        task.setDescription(updatedTask.getDescription());
        task.setCompleted(updatedTask.isCompleted());
        task.setDueDate(updatedTask.getDueDate());
        task.setCategory(updatedTask.getCategory());
        taskService.saveTask(task);
        return "redirect:/tasks";
    }

    @PostMapping("/delete/{id}")
    public String deleteTask(@PathVariable Long id) {
        taskService.deleteTask(id);
        return "redirect:/tasks";
    }


}

