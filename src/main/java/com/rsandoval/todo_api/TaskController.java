package com.rsandoval.todo_api;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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

    @GetMapping("/{id}")
    public Task getTask(@PathVariable Long id){
        return taskRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Task not found with ID: " + id));
    }

    // Handle POST requests to "/api/tasks"
    @PostMapping
    public ResponseEntity<Task> createTask(@RequestBody Task task){
        // Spring Boot takes the JSON from the request
        // and converts it into a Task object (@RequestBody).
        // We then save it to the database.
        Task savedTask = taskRepository.save(task);

        // Return a ResponseEntity that wraps the saved task AND the "201 Created" status
        return ResponseEntity.status(HttpStatus.CREATED).body(savedTask);
    }

    // Handle DELETE requests to "/api/tasks/{id}"
    @DeleteMapping("/{id}")
    public void deleteTask(@PathVariable Long id){
        taskRepository.deleteById(id);
    }

    // Handle PUT requests to "/api/tasks/{id}"
    @PutMapping("/{id}")
    public Task updateTask(@PathVariable Long id, @RequestBody Task updatedTaskData){
        // 1. Find the existing task in the database, or throw an error if it's not found.
        Task existingTask = taskRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Task not found with ID: " + id));
        // 2. Update the fields of the *existing* task with the *new* data.
        existingTask.setDescription(updatedTaskData.getDescription());
        existingTask.setCompleted(updatedTaskData.isCompleted());
        // 3. Save the updated task; save() will perform an UPDATE, not a new INSERT, due to existing ID
        return taskRepository.save(existingTask);
    }
}
