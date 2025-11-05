package com.rsandoval.todo_api;

import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tasks") // Sets a "base" URL for all methods in this class
public class TaskController {

    private final TaskRepository taskRepository;

    // Constructor Injection: Inject the TaskRepository you're managing
    // "Inversion of Control"
    public TaskController(TaskRepository taskRepository){
        this.taskRepository = taskRepository;
    }

    // Handle GET requests to "/api/tasks"
    @GetMapping
    public List<Task> getAllTasks(){
        return taskRepository.findAll();
    }

    // Handle POST requests to "/api/tasks"
    @PostMapping
    public Task createTask(@RequestBody Task task){
        // Spring Boot takes the JSON from the request
        // and converts it into a Task object (@RequestBody).
        // We then save it to the database.
        return taskRepository.save(task);
    }
}
