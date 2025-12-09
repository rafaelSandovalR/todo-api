package com.rsandoval.todo_api.controller;

import com.rsandoval.todo_api.model.Task;
import com.rsandoval.todo_api.exception.TaskNotFoundException;
import com.rsandoval.todo_api.model.User;
import com.rsandoval.todo_api.repository.TaskRepository;
import com.rsandoval.todo_api.repository.UserRepository;
import org.apache.coyote.Response;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.context.SecurityContextHolderThreadLocalAccessor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/api/tasks") // Sets a "base" URL for all methods in this class
public class TaskController {

    private final TaskRepository taskRepository;
    private final UserRepository userRepository;

    // Constructor Injection: Inject the TaskRepository you're managing
    // "Inversion of Control"
    public TaskController(TaskRepository taskRepository, UserRepository userRepository){
        this.taskRepository = taskRepository;
        this.userRepository = userRepository;
    }

    private User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User not found"));
    }

    // Handle GET requests to "/api/tasks"
    @GetMapping
    public List<Task> getAllTasks(){
        User currentUser = getCurrentUser();
        return taskRepository.findByUserId(
                currentUser.getId(),
                Sort.by(Sort.Order.asc("completed"), Sort.Order.asc("id"))
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<Task> getTask(@PathVariable Long id){
        User currentUser = getCurrentUser();
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new TaskNotFoundException("Task not found with ID: " + id));

        if (!task.getUser().getId().equals(currentUser.getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You do not have access to this task");
        }
        return ResponseEntity.ok(task);
    }

    // Handle GET requests to "/api/tasks/search?completed=..."
    @GetMapping("/search")
    public List<Task> getTasksByStatus(@RequestParam boolean completed){
        User currentUser = getCurrentUser();
        return taskRepository.findByUserIdAndCompleted(currentUser.getId(), completed);
    }

    // Handle POST requests to "/api/tasks"
    @PostMapping
    public ResponseEntity<Task> createTask(@RequestBody Task task){
        User currentUser = getCurrentUser();
        task.setUser(currentUser);
        // Spring Boot takes the JSON from the request
        // and converts it into a Task object (@RequestBody).
        // We then save it to the database.
        Task newTask = taskRepository.save(task);

        // Return a ResponseEntity that wraps the saved task AND the "201 Created" status
        return ResponseEntity.status(HttpStatus.CREATED).body(newTask);
    }

    // Handle DELETE requests to "/api/tasks/{id}"
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTask(@PathVariable Long id){
        User currentUser = getCurrentUser();
        Task task = taskRepository.findById(id)
                        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Task not found"));

        if (!task.getUser().getId().equals(currentUser.getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You do not have permission to delete this task");
        }
        taskRepository.deleteById(id);
        return ResponseEntity.ok().build();
    }

    // Handle PUT requests to "/api/tasks/{id}"
    @PutMapping("/{id}")
    public ResponseEntity<Task> updateTask(@PathVariable Long id, @RequestBody Task updatedTaskData){
        User currentUser = getCurrentUser();
        // 1. Find the existing task in the database, or throw an error if it's not found.
        Task existingTask = taskRepository.findById(id)
                .orElseThrow(() -> new TaskNotFoundException("Task not found with ID: " + id));
        // Security Check: Does this task belong to the current user?
        if (!existingTask.getUser().getId().equals(currentUser.getId())){
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You do not have permission to update this task");
        }

        // 2. Update the fields of the *existing* task with the *new* data.
        existingTask.setDescription(updatedTaskData.getDescription());
        existingTask.setCompleted(updatedTaskData.isCompleted());
        // 3. Save the updated task; save() will perform an UPDATE, not a new INSERT, due to existing ID
        Task updatedTask = taskRepository.save(existingTask);
        return ResponseEntity.ok(updatedTask);
    }
}
