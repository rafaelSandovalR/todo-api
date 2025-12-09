package com.rsandoval.todo_api;

import com.rsandoval.todo_api.model.Task;
import com.rsandoval.todo_api.model.User;
import com.rsandoval.todo_api.repository.TaskRepository;
import com.rsandoval.todo_api.repository.UserRepository;
import com.rsandoval.todo_api.service.JwtService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

// 1. We use @SpringBootTest to start the entire application, and tell it to run a random, unused port to avoid conflicts.
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class TaskApiIntegrationTests {

    // 2. Spring will automatically inject a real HTTP client that is configured to talk to our running test server
    @Autowired
    private TestRestTemplate restTemplate;

    // 3. We also inject the real repository so we can clean up the database after each test.
    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private User testUser;

    private HttpHeaders getAuthHeaders() {

        if (testUser == null) {
            testUser = new User();
            testUser.setUsername("testuser");
            testUser.setPassword(passwordEncoder.encode("password"));
            testUser.setRole("USER");
            testUser = userRepository.save(testUser);
        }

        UserDetails userDetails = org.springframework.security.core.userdetails.User.builder()
                .username(testUser.getUsername())
                .password(testUser.getPassword())
                .roles(testUser.getRole())
                .build();

        String token = jwtService.generateToken(userDetails);

        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + token);
        headers.add("Content-Type", "application/json");
        return headers;
    }

    // 4. This helper method cleans the database after every test, so our tests don't interfere with each other.
    @AfterEach
    void tearDown() {
        taskRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    void testCreateTask_ShouldSaveToDatabase() {
        // -- ARRANGE -- Create the task object we want to send
        Task task = new Task();
        task.setDescription("Watch new PTA movie");

        // Wrap the task AND the headers in an HttpEntity
        HttpEntity<Task> request = new HttpEntity<>(task, getAuthHeaders());

        // -- ACT -- Use the real HTTP client to send a real POST request
        ResponseEntity<Task> response = restTemplate.exchange(
                "/api/tasks",
                HttpMethod.POST,
                request,
                Task.class
        );

        // -- ASSERT --
        // A. Check the HTTP response
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody()).isNotNull(); // Check that the body is not null before using it
        assertThat(response.getBody().getId()).isNotNull();
        assertThat(response.getBody().getDescription()).isEqualTo("Watch new PTA movie");

        // B. Check the database directly
        Long newId = response.getBody().getId();
        Optional<Task> optionalTask = taskRepository.findById(newId); // Check for presence before calling .get()
        assertThat(optionalTask).isPresent();
        Task savedTask = optionalTask.get(); // Now that we've confirmed it's present, we can safely .get() it
        assertThat(savedTask.getDescription()).isEqualTo("Watch new PTA movie");
    }

    @Test
    void testGetAllTasks_ShouldReturnAllTasks() {
        // Initialize 'testuser'
        HttpHeaders headers = getAuthHeaders();

        Task task1 = new Task();
        task1.setDescription("Test task 1");
        task1.setUser(testUser);
        taskRepository.save(task1);

        Task task2 = new Task();
        task2.setDescription("Test task 2");
        task2.setUser(testUser);
        taskRepository.save(task2);

        HttpEntity<String> request = new HttpEntity<>(null, getAuthHeaders());

        ResponseEntity<Task[]> response = restTemplate.exchange(
                "/api/tasks",
                HttpMethod.GET,
                request,
                Task[].class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().length).isEqualTo(2);
        assertThat(response.getBody()[0].getDescription()).isEqualTo("Test task 1");
        assertThat(response.getBody()[1].getDescription()).isEqualTo("Test task 2");
    }

    @Test
    void testGetTasksByStatus_ShouldReturnCompletedTasks() {
        HttpHeaders headers = getAuthHeaders();

        Task incompleteTask = new Task();
        incompleteTask.setDescription("Fix car");
        incompleteTask.setCompleted(false);
        incompleteTask.setUser(testUser);
        taskRepository.save(incompleteTask);

        Task completedTask1 = new Task();
        completedTask1.setDescription("Buy milk");
        completedTask1.setCompleted(true);
        completedTask1.setUser(testUser);
        taskRepository.save(completedTask1);

        Task completedTask2 = new Task();
        completedTask2.setDescription("Walk the dog");
        completedTask2.setCompleted(true);
        completedTask2.setUser(testUser);
        taskRepository.save(completedTask2);

        HttpEntity<String> request = new HttpEntity<>(null, getAuthHeaders());

        ResponseEntity<Task[]> response = restTemplate.exchange(
                "/api/tasks/search?completed=true",
                HttpMethod.GET,
                request,
                Task[].class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().length).isEqualTo(2);
        assertThat(response.getBody()[0].getDescription()).isEqualTo("Buy milk");
        assertThat(response.getBody()[1].getDescription()).isEqualTo("Walk the dog");
        assertThat(response.getBody())
                .extracting(Task::isCompleted)
                .containsOnly(true);
    }
}
