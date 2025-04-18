package com.example.Todo.service;

import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.example.Todo.model.Task;
import com.example.Todo.repository.TaskRepository;

import jakarta.transaction.Transactional;

@Service
public class ReminderService {

    @Autowired
    private TaskRepository taskRepository;

    // 🔁 Runs every day at 8AM
    @Scheduled(cron = "0 0 8 * * *") // You can test with shorter interval too
    @Transactional
    public void checkDueTasks() {
        LocalDate today = LocalDate.now();
        List<Task> dueTasks = taskRepository.findByDueDate(today);

        if (!dueTasks.isEmpty()) {
            System.out.println("📢 Reminder: You have tasks due today!");
            for (Task task : dueTasks) {
                System.out.println(" - " + task.getName());
            }
        }
    }
}
